package edu.umich.library.library_identifier.solrFilter;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umich.library.library_identifier.normalizers.LCCallNumberSimple;

import java.io.IOException;


/**
 * A Solr filter that take an LC Call Number (/ shelf key) and
 * turns it into something that can be sorted correctly _and_
 * can be used for left-anchored search if turned into edge-ngrams.
 * <p>
 *
 * <fieldType name="lc_callnumber_sortable" keepInvalid="true">
 *
 * </fieldType>
 */
public final class LCCallNumberSimpleFilter extends TokenFilter {
  /**
   * Logger used to log info/warnings.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(LCCallNumberSimpleFilter.class);

  /**
   * The filter term that is a result of the conversion.
   */
  private final CharTermAttribute myTermAttribute =
      addAttribute(CharTermAttribute.class);

  /**
   * Should we pass through an invalid (doens't look like) callnumber,
   * or return nothing (default)
   */

  private Boolean allowInvalid = false;

  /**
   * @param aStream A {@link TokenStream} that parses streams with
   *                ISO-639-1 and ISO-639-2 codes
   */
  public LCCallNumberSimpleFilter(TokenStream aStream, Boolean passThrough) {
    super(aStream);
    allowInvalid = passThrough;
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
    LOGGER.warn("Trying on " + t);
    LOGGER.warn("allowInvalid is " + allowInvalid.toString());
    if (t != null && t.length() != 0) {
      try {
        myTermAttribute.setEmpty();
        LCCallNumberSimple lc = new LCCallNumberSimple(t);
        if (lc.isValid) {
          myTermAttribute.append(lc.collation_key());
        } else if (allowInvalid) {
          LOGGER.warn("GOT HERE");
          myTermAttribute.append(lc.invalid_collation_key());
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
