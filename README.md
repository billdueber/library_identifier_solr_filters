# library_identifer_solr_filters

## Overview

This is a series of simple solr analysis-chain filters useful to those
dealing with library identifiers such as ISBN/ISSN, LCCN, LC Callnumber, etc.

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

## LC Callnumber Simple

This is a simple/simplistic attempt to take LC callnumbers and turn them
into something sortable/searchable. It does very little to massage the 
callnumbers before indexing. 


### Explanation

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

## Left-anchored ("starts with") searching
Solr's wildcard search (.e.g, 'QA1*`) doesn't work for "start-with" searching
because it ignores the whole analysis pipeline. To get around this we can 
use edge n-grams such that:

  * "QA 1" will match `QA1` and `QA1.4` but not `QA11`
  * Spaces and punctuation will be ignored

### Usage

The resulting indexed value is suitable for both sorting and left-anchored
searching.

```xml
    <!-- Turn LC callnumbers into something sortable. Ignores anything 
         that doesn't look like a callnumber, and must be single valued
         if it's going to be used for sorting. -->
    <fieldType name="lc_callnumber_sortable" class="solr.TextField" 
               multiValued="false">
      <analyzer>
          <tokenizer class="solr.KeywordTokenizerFactory"/>
          <filter class="edu.umich.library.library_identifier.solrFilter.LCCallNumberSimpleFilterFactory"
                  allowInvalid="false"/>
        </analyzer>
    </fieldType>

    <!-- Make LC Callnumbers left-anchored ("starts with") searchable. 
         Do _not_ add a trailing '*' to your searches -- this is not a 
         wildcard search. Allow invalid callnumbers to just be 
         passed through, so folks can search on those, too, albeit
         not as well.-->

  <fieldType name="lc_callnumber_starts_with" class="solr.TextField">
    <analyzer type="index">
      <tokenizer class="solr.KeywordTokenizerFactory"/>
      <filter class="edu.umich.library.library_identifier.solrFilter.LCCallNumberSimpleFilterFactory" allowInvalid="true"/>
      <filter class="solr.EdgeNGramFilterFactory" maxGramSize="40" minGramSize="2"/>
    </analyzer>
    <analyzer type="query">
      <tokenizer class="solr.KeywordTokenizerFactory"/>
      <filter class="edu.umich.library.library_identifier.solrFilter.LCCallNumberSimpleFilterFactory" allowInvalid="true"/>
    </analyzer>
  </fieldType>

  <!-- A couple example fields. The sortable one must be single valued, so 
    you need to pick a canonical lc callnumber for each record.
    For searching, there's no need to restrict ourselves to one value. -->
  <field name="lc_sortable" type="lc_callnumber_sortable" indexed="true" 
         stored="true" multiValued="false"/>
  <field name="lc_starts_with" type="lc_callnumber_starts_with" indexed="true"
         stored="false" multiValued="true"/>

```

