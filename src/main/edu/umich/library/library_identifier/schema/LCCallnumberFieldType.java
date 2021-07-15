package edu.umich.library.library_identifier.schema;

import edu.umich.library.library_identifier.normalizers.LCCallNumberSimple;
import edu.umich.library.library_identifier.normalizers.CallnumberInterface;

import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.StrField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Map;

public class LCCallnumberFieldType extends StrField {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public String accept = "valid";

  public CallnumberInterface callnumber(String str) {
    return new LCCallNumberSimple(str);
  }

  protected void init(IndexSchema schema, Map<String,String> args) {
    String accept_arg = args.remove("accept");
    if (accept_arg != null)
      accept = accept_arg;
    super.init(schema, args);
  }


  @Override
  public String toInternal(String val) {
    CallnumberInterface lccns = callnumber(val);
    if (lccns.isValid) {
      return lccns.collation_key();
    }
    if (accept.equals("all")) {
      return lccns.invalid_collation_key();
    } else {
      return null;
    }
  }
}
