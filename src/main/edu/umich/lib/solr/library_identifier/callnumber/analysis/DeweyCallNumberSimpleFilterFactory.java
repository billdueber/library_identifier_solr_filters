package edu.umich.lib.solr.library_identifier.callnumber.analysis;

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
 */
public class DeweyCallNumberSimpleFilterFactory extends TokenFilterFactory {
    private Boolean allowTruncated;
    private Boolean passThroughOnError;

    public DeweyCallNumberSimpleFilterFactory(Map<String, String> args) {
        super(args);
        allowTruncated = getBoolean(args, "allowTruncated", true);
        passThroughOnError = getBoolean(args, "passThroughOnError", false);

    }

    @Override
    public DeweyCallNumberSimpleFilter create(TokenStream input) {
        return new DeweyCallNumberSimpleFilter(input, allowTruncated, passThroughOnError);
    }
}
