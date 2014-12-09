1.) Build the source code into a jar.

2.) Add the lines in the solrconfig.xml file in this directory to the
solrconfig.xml file in your Solr instance.

3.) Add the lines in the schema.xml file in this directory to the
schema.xml file in your Solr instance.

4.) Start the Solr instance.

5.) Go to the Solr admin console, select the relevant document collection,
and the Query page.

6.) On the Query page:

  -- add the following string in the "q" field:

    +title:Bond +date_published:[2013-01-01T00:00:00Z TO 2014-12-08T23:59:59Z] _val_:"recent($targetDate,date_published)"

  -- add the following string in the "Raw Query Parameters" field:

    targetDate=2014-12-08T23:59:59Z

Note: This query supposes that today's date is 2014-12-08T23:59:59Z. You will need to change as suitable.

7.) Execute the query. You should be able to set a break point inside of
the RecentFunction.getValues method and step through the code.
