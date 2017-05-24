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

```text
curl -XGET 'localhost:9000/get/4'

{
  "id":"4",
  "title":"Alergologia",
  "text":"'''Alergologia''' \u2013 dziedzina [[medycyna|medycyny]] zajmująca się rozpoznawaniem i [[leczenie|leczeniem]] [[alergia|schorzeń alergicznych]].\n\n== Zobacz też ==\n* [[alergen]]\n\n== Linki zewnętrzne ==\n{{wikisłownik|alergologia}}\n{{commonscat|Allergology}}\n* [http://www.pta.med.pl/ Polskie Towarzystwo Alergologiczne]\n* [http://www.alergologia.org/ Portal Lekarzy Alergologów 'alergologia.org']\n* [http://alergie.mp.pl/ Alergie.mp.pl, serwis wydawnictwa Medycyna Praktyczna]\n\n\n{{Zastrzeżenia|Medycyna}}\n\n{{Specjalności medyczne}}\n\n[[Kategoria:Alergologia| ]]\n[[Kategoria:Specjalności lekarskie]]"
}
```
