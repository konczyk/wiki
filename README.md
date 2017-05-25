# Wiki API

A simple REST API to query Wikipedia pages.

# Requirements

The service requires Elasticsearch to be running.

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
and then runf the indexing service (it make take a few minutes to finish).

```text
$ ./sbt "runMain wiki.Indexer downloaded_file.xml"
```

# Querying

To run queries, you need to start the API service with the following command
```text
$ ./sbt "runMain wiki.WikiAPI"
```

## Get page

Getting page by its id number return a JSON string with title and text fields

```text
curl -XGET 'localhost:9000/get/4'

{
  "title":"Alergologia",
  "text":"'''Alergologia''' \u2013 dziedzina [[medycyna|medycyny]] zajmująca się ..."
}
```

## Search pages

Searching pages for a given word(s). Current strategy searches for document that
contain all the words. Without additional params, first 10 hits are returned.

```text
curl -XGET 'localhost:9000/search?q=awk%20sed'

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
of pages is controlled by the maxHits setting in application.conf

```text
curl -XGET 'localhost:9000/search?q=awk%20sed&from=2&size=3'
```
