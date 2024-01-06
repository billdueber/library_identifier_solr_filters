package edu.umich.lib.solr.library_identifier.callnumber.analysis;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umich.lib.library_identifier.callnumber.LCCallNumberSimple;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * A Solr filter that take an LC Call Number (/ shelf key) and
 * turns it into something that can be sorted correctly _and_
 * can be used for left-anchored search if turned into edge-ngrams.
 * <p>
 *
 * <fieldType name="callnumber_sortable" passThroughOnError="true">
 *
 * </fieldType>
 */

public final class LCCallNumberSimpleFilter extends TokenFilter {
  /**
   * Logger used to log info/warnings.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(MethodHandles.lookup().lookupClass());

  /**
   * The filter term that is a result of the conversion.
   */
  private final CharTermAttribute myTermAttribute =
      addAttribute(CharTermAttribute.class);

  /**
   * Should we pass through an invalid (doesn't look like) callnumber,
   * or return nothing (default)
   */

  private Boolean passThroughOnError;
  private Boolean allowTruncated;

  /**
   * @param aStream A {@link TokenStream} that parses streams with
   *                ISO-639-1 and ISO-639-2 codes
   */
  public LCCallNumberSimpleFilter(TokenStream aStream, Boolean passThroughOnError, Boolean allowTruncated) {
    super(aStream);
    this.passThroughOnError = passThroughOnError;
    this.allowTruncated     = allowTruncated;
  }

  public LCCallNumberSimpleFilter(TokenStream aStream) {
    this(aStream, false, true);
  }


  /**
   * Increments and processes tokens in the ISO-639 code stream.
   *
   * @return True if a value is still available for processing in the token
   * stream; otherwise, false
   */
  @Override
  public boolean incrementToken() throws IOException {
    if (!input.incrementToken()) {
      return false;
    }

    String t = myTermAttribute.toString();
    if (t != null && t.length() != 0) {
      try {
        myTermAttribute.setEmpty();
        LCCallNumberSimple lc = new LCCallNumberSimple(t);
        String key = lc.bestKey(passThroughOnError, allowTruncated);
        if (key == null) {
          return false;
        } else {
          myTermAttribute.append(key);
        }
      } catch (IllegalArgumentException details) {
        if (LOGGER.isInfoEnabled()) {
          LOGGER.info(details.getMessage(), details);
        }
      }
    }

    return true;
  }
}
