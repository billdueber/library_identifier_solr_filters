# library_identifer_solr_filters

## Overview

This is a series of simple solr analysis-chain filters useful to those
dealing with library identifiers (currently only LC Callnumbers, but
more to come).

## Getting/generating the .jar file

You can just nab a .jar file from the [github releases page](https://github.com/billdueber/library_identifier_solr_filters/releases). They're labeled
with the version of the library and the version of solr they're created
against. 

You can also use maven. You should be able to build with just

```shell
mvn package # .jar file will appear in `target/`

```

To use different versions of solr and/or the icu4j library, you can
define them on the command line (defaults are in the pom.xml file)

```shell
mvn package -Dsolr.version=8.6.1 -Dicu.version=66.1

```

## Placing the .jar file

The jar file needs to put somewhere solr's going to pick it up, which
is defined in the `solrconfig.xml` file. 

I like to have a `lib` directory
"next to" my `conf` directory with the solr configuration

```
mycore
  |- conf
      |- schema.xml
      |- solrconfig.xml
      |- ...
  |- lib
      |- library_identifier_solr_filters-0.1-solr8.8.2.jar

```

... and then have `conf/solrconfig.xml` include the line:

```xml
<lib dir="${solr.core.config}/lib" regex=".*\.jar"/>
```

## LC Callnumbers

This is a simple/simplistic attempt to take LC callnumbers and turn them
into something sortable/searchable. It does the bare minimum to massage the 
callnumbers before indexing.

In addition to the underlying code to do the conversion, there are two ways
to use it in fieldTypes.

### LCCallNumberSimpleFilterFactory for prefix queries

An analysis filter that will take a token and perform the callnumber
normalization (or at least do its best), suitable for use with
the edge n-gram filter for providing It requires 
that callnumbers be treated as a single token, so should only be used 
with a keyword tokenizer.

Note that this filter will never be called for range searches; if you 
want to use ranges see the `CallnumberSortableFieldType`.

A good fieldType definition for prefix searches is as follows:

```xml
<fieldType name="callnumber_prefix_search"  class="solr.TextField">
  <analyzer type="index">
    <tokenizer class="solr.KeywordTokenizerFactory"/>
    <filter class="edu.umich.library.lucene.analysis.LCCallNumberSimpleFilterFactory" passThroughOnError="true"/>
    <filter class="solr.EdgeNGramFilterFactory" maxGramSize="40" minGramSize="2"/>
  </analyzer>
  <analyzer type="query">
    <tokenizer class="solr.KeywordTokenizerFactory"/>
    <filter class="edu.umich.library.lucene.analysis.LCCallNumberSimpleFilterFactory" passThroughOnError="true"/>
  </analyzer>
</fieldType>
```


### The CallnumberSortableFieldType

`CallnumberSortableFieldType` is a derivative of `solr.String` which does
the callnumber conversion on the way in (for both stored and indexed values). 
This not only gives you a sortable value (which the filter does as well),
but allows the type to be used correctly with ranges (since Solr doesn't
run the analysis chain for range queries).

Because it's implemented as a FieldType, all the normalization works
as you'd expect (e.g., `callnumber_search:[qa20 to *]` will pick up
the callnumber "QA 20.2" and not "QA 3.11 .D4"). 

The FieldType can/should be used for both exact queries and range queries,
as well as (pretty) accurate sorting,
but can't be used for prefix search since it's not a part of the analysis 
chain (being based on String and not TextField), hence the filter, above.

```xml

<fieldType name="callnumber_sortable" class="edu.umich.library.
library_identifier.schema.CallnumberSortableFieldType" />


<field name="callnumber_search" type="callnumber_sortable"
       multiValued="true"/>

<field name="callnumber_sort" type="callnumber_sortable"
       multiValued="false"/>


```


### The normalization algorithm

Given a callnumber:
```
QA 123.456 .C5 D6 1990 v.3
 1  2   3  <--    4    -->
```

We label it as follows: 
 1. The _initial letters_
 2. The _digits_
 3. An (optional) _decimal_
 4. Everything else

In particular, there's no attempt to separate out the cutters, enumchron,
year, etc. since at my institution there just wasn't any appetite for what
little functionality it added compared to the ambiguities/bugs it 
produced.

The transformation process is, essentially:

  * Lowercase everything, trim/collapse whitespace
  * Remove any space between the initial letters and digits
  * Prepend the digits with its string length (e.g., 44 -> 244, 1234 -> 
    41234).
    This makes the number correctly sort "alphabetically" and we don't have
    to mess around with zero-padding or anything
  * Remove punctuation other than dots that create a decimal (e.g., "v.1" will
    become "v1", but "no. 123.45" will become "no 123.45").
    
## Invalid callnumbers

Anything that doesn't start with some letters followed by some digits is
declared _invalid_. These values can be either kept or ignored depending on
the argument `allowInvalid` in the solr fieldType (see below).

The invalid callnumber passed through isn't exactly the same as what
was passed in -- we still do lowercasing, space collapse/trim, and remove
non-decimal-place-looking punctuation.


