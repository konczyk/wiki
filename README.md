# Wiki API

A simple REST API to query Wikipedia pages. Uses Elasticsearch and Akka HTTP.
Connections to Elasticsearch are done with the native Java client with additional
wrapper to integrate it with Scala futures, however indexing is done synchronously,
since it's run only once. XML parsing is done with XMLEventReader.

# Requirements

The service requires Elasticsearch to be running. It can be configured in
application.conf or simply run locally with the default settings

```text
$ ./bin/elasticsearch
```

# Testing

Run tests with:
```text
$ ./sbt test
```

# Indexing

The data is not included. Please download and unpack xml files
(plwiki-latest-pages-meta-current) from https://dumps.wikimedia.org/plwiki/
and then run the indexing service (it make take a few minutes to finish).

```text
$ ./sbt "runMain wiki.Indexer downloaded_file.xml"
```

# Querying

To run queries, you need to start the API service with the following command
```text
$ ./sbt "runMain wiki.WikiAPI"
```

## Get page

Getting a page by its id number returns a JSON string with title and text fields

```text
curl -XGET 'localhost:9000/page/4'

{
  "title":"Alergologia",
  "text":"'''Alergologia''' \u2013 dziedzina [[medycyna|medycyny]] zajmująca się ..."
}
```

## Get pages matching criteria

Searching pages for a given word(s) matches the ones that contain all the
query words. Without additional params, Elasticsearch defaults
are used and up to 10 hits are returned.

```text
curl -XGET 'localhost:9000/pages?q=awk%20sed'

[
  {
    "title":"Korn shell",
    "text":"'''Korn shell''' ('''ksh''') jest jedną z [[Powłoka systemowa|powłok]]..."
  },
  {
    "title":"AWK",
    "text":"{{Język programowania infobox\n |logo =\n |nazwa = AWK\n |paradygmat = ..."
  }
  ...
]
```

It is also possible to pass additional params to control pagination. Maximum number
of pages is controlled by the maxHits setting in application.conf, which is
set to 20.

```text
curl -XGET 'localhost:9000/pages?q=awk%20sed&from=2&size=3'
```

## TODO

* more structured API responses to encapsulate both success and failure states and additional logging
* more full text search options - compact Lucene query string syntax could be a good candidate
* exposing asynchronous indexing API with authorization to simplify/automate index updates
