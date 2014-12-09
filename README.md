# recent: A Solr ValueSource plugin function to boost search results based on how recent (current) the document is

The task is to build a Solr function that boosts a document based on how recent (current) the document is. For example, you might be interested only in the most recently released videos from Netflix that match a set of search terms or the most recently published books from Amazon that match those search terms.

This search routine takes as input:

  * a set of search terms
  * a range of dates: This range will serve to limit the number of documents processed.
  * a target date: Documents whose date is closest to this target date will be given the largest boost; when searching for the newest books, the target date is the current date.

The routine outputs a list of books (videos) ordered by a score computed based on two criteria:

  * C1: how well the book’s (video's) title matches the search terms
  * C2: how new or recent the book is (as determined from some date field in the document

Book documents are stored in a Solr index and have the following four fields:

  * title
  * author
  * category (for example, financial, computer, espionage, gardening)
  * date_published

Note: In the default example in the Solr 4.10.2 release, the documents represented by the *schema.xml* file are books and have all the fields listed above except __``date_published``__, so this field must be added to the schema.xml file.

Now, suppose that Frank issues a search for books by the supplying the search term “bond.” Consideration of criterion C1 (how well the book title matches the search terms) suggests that two books should be returned by this search:

    
        title: The British Bond Market
        author: George Banks
        category: Financial
        date_published:2013-01-01T00:00:00Z
    
        title: The Name is Bond
        author: Ian Fleming
        category: espionage
        date_published:2014-02-01T00:00:00Z
    
But, it is unclear based just on criterion C1 how these results should be ordered when they are returned to Frank since both books contain a single term in the title field that matches the search term. On the other hand, consideration of criterion C2 (how recently the book was published) suggests that the first book is should be given a boost since it is newer and returned at the top of the list.

Suppose that today's date is 2014-12-08T00:00:00Z (in Solr's Zulu format). We can boost search results by newness by issuing the following query to Solr:

    +title:Bond +date_published:[2014-01-01T00:00:00Z TO 2014-12-08:12:59:59Z] _val_:"recent($targetDate,date_published)"&targetDate=2014-12-08T23:59:59Z

where

  * __``+title:Bond``__ means "find all documents where the title field contains the search term "Bond." The __``+``__ sign indicates that the search term must be present in order for the document to be returned.
  *__``+date_published:[2013-01-01T00:00:00Z TO 2013-12-08T23:59:59Z]``__ means find all documents where the __``date_published``__ field falls in the specified range. The __``+``__ sign indicates that documents must be in this date range in order for the document to be returned. (This arbitrary range is included in the search to limit the number of documents that must be processed by the __``recent``__ function.)
  * __``targetDate=2014-12-08T23:59:59Z``__ is an assignment of today's date to the local variable __``targetDate``__
  * __``$targetDate``__ is a dereferencing of that local variable __``targetDate``__, which evaluates to the current date
  * __``date_published``__ is the date_published field from the book document
  * __``recent``__ is a function that we have written and plugged in to Solr by extending the Solr/Lucene [ValueSource](http://lucene.apache.org/core/4_10_2/queries/org/apache/lucene/queries/function/ValueSource.html?is-external=true) and [ValueSourceParser](http://lucene.apache.org/solr/4_10_2/solr-core/org/apache/solr/search/ValueSourceParser.html) classes.

The __``recent``__ function compares all the values in the __``date_published``__ field of all documents with the targetDate. The closer the __``published_date``__ is to the __``targetDate``__, the higher the boost that will be applied to the matching book.

The Solr query infrastructure then calculates a final score by combining the boost returned by the __``recent``__ function with the relevancy score based on how well the book title matched the search term “bond” (this relevancy score having been calculated by Lucene's [Similarity](http://lucene.apache.org/core/4_10_2/core/org/apache/lucene/search/similarities/Similarity.html) class).

The end result is that the scores of books matching the search terms are boosted by how recently they were published.

### Specific results

In Solr Admin, place the following string in the "q" field:
 
    +title:Bond +date_published:[2014-01-01T00:00:00Z TO 2014-12-08T23:59:59Z] _val_:"recent($targetDate,date_published)"

  -- add the following string in the "Raw Query Parameters" field:

    targetDate=2014-12-08T23:59:59Z

Then, if you execute the query, the more recently published book with the search term "Bond" in the title will be returned first, namely the book with the title "The Name is Bond."
