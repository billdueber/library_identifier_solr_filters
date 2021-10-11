package edu.umich.library.solr.analysis;

import edu.umich.library.library_identifier.normalizers.CallnumberInterface;
import edu.umich.library.library_identifier.normalizers.DeweyCallNumberSimple;
import org.apache.lucene.analysis.TokenStream;

public class DeweyCallNumberSimpleFilter extends LCCallNumberSimpleFilter {

  public DeweyCallNumberSimpleFilter(TokenStream aStream, String accept) {
    super(aStream, accept);
  }

  @Override
  public CallnumberInterface callnumber(String str) {
    return new DeweyCallNumberSimple(str);
  }
}
