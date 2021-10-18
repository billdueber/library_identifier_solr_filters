package edu.umich.library.lucene.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import java.util.Map;

/**
 * @author dueberb
 * A Solr filter that take an LC Call Number (/ shelf key) and
 * turns it into something that can be sorted correctly. While the
 * fieldType () is better for general use, if you want to do prefix
 * queries, you need to have an analysis chain so you can add the
 * edge ngram filter, so we've got this.
 * <p>
 *
 * <fieldType name="callnumber_prefix_search"  class="solr.TextField">
 *   <analyzer type="index">
 *     <tokenizer class="solr.KeywordTokenizerFactory"/>
 *     <filter class="edu.umich.library.lucene.analysis.LCCallNumberSimpleFilterFactory" passThroughInvalid="true"/>
 *     <filter class="solr.EdgeNGramFilterFactory" maxGramSize="40" minGramSize="2"/>
 *   </analyzer>
 *   <analyzer type="query">
 *     <tokenizer class="solr.KeywordTokenizerFactory"/>
 *     <filter class="edu.umich.library.lucene.analysis.LCCallNumberSimpleFilterFactory" passThroughInvalid="true"/>
 *   </analyzer>
 * </fieldType>
 */
public class LCCallNumberSimpleFilterFactory extends TokenFilterFactory {
  private Boolean passThroughInvalid;

  public LCCallNumberSimpleFilterFactory(Map<String, String> args) {
    super(args);
    passThroughInvalid = getBoolean(args, "passThroughInvalid", false);
  }

  @Override
  public LCCallNumberSimpleFilter create(TokenStream input) {
    return new LCCallNumberSimpleFilter(input, passThroughInvalid);
  }
}
