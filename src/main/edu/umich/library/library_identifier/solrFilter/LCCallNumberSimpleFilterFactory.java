package edu.umich.library.library_identifier.solrFilter;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import java.util.Map;

/**
 * @author dueberb
 */
public class LCCallNumberSimpleFilterFactory extends TokenFilterFactory {
  private Boolean allowInvalid = false;

  public LCCallNumberSimpleFilterFactory(Map<String, String> args) {
    super(args);
    allowInvalid = getBoolean(args, "allowInvalid", false);
  }

  @Override
  public LCCallNumberSimpleFilter create(TokenStream input) {
    return new LCCallNumberSimpleFilter(input, allowInvalid);
  }
}
