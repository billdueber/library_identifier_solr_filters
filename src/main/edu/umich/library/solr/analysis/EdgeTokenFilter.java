package edu.umich.library.solr.analysis;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umich.library.library_identifier.normalizers.LCCallNumberSimple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class EdgeTokenFilter  extends TokenFilter {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(EdgeTokenFilter.class);
  private final CharTermAttribute charTermAtt = addAttribute(CharTermAttribute.class);
  private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
  private final OffsetAttribute offsetAttr = addAttribute(OffsetAttribute.class);
  private final PositionLengthAttribute posLengthAttr = addAttribute(PositionLengthAttribute.class);
  private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class);

  private List<String> tokens = new ArrayList<String>();

  /**
   * Clear out the list of tokens and reset.
   */
  @Override
  public void reset() throws IOException {
    super.reset();
    tokens.clear();
  }




}
