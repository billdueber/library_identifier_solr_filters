package edu.umich.library.solr.schema;

import edu.umich.library.library_identifier.normalizers.CallnumberInterface;
import edu.umich.library.library_identifier.normalizers.DeweyCallNumberSimple;

public class DeweyCallNumberFieldType extends LCCallnumberFieldType {

  @Override
  public CallnumberInterface callnumber(String str) {
    return new DeweyCallNumberSimple(str);
  }

}
