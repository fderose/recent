    URL=http://localhost:8983/solr/update/json
curl $URL -H 'Content-type:application/json' -d '
[
  {"id":"1","title":"The British Bond Market","author":"George Banks","category":"financial","date_published":"2013-01-01T00:00:00Z"},
  {"id":"2","title":"The Name is Bond","author":"Ian Fleming","category":"espionage","date_published":"2014-02-01T00:00:00Z"},
  {"id":"3","title":"Java 8 In Action","author":"Mario Fusco","category":"computer","date_published":"2014-08-28T00:00:00Z"},
  {"id":"4","title":"ElasticSearch Server","author":"Rafal Kuc","category":"computer","date_published":"2014-04-04T00:00:00Z"}
]'
curl $URL?commit=true

