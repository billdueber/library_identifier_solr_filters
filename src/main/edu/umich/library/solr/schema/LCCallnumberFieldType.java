package edu.umich.library.solr.schema;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import edu.umich.library.library_identifier.normalizers.LCCallNumberSimple;
import edu.umich.library.library_identifier.normalizers.CallnumberInterface;

import org.apache.solr.response.TextResponseWriter;
import org.apache.solr.schema.*;
import org.apache.solr.uninverting.UninvertingReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Map;

public class LCCallnumberFieldType extends TextField {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public String accept = "valid";

  public CallnumberInterface callnumber(String str) {
    return new LCCallNumberSimple(str);
  }

  protected void init(IndexSchema schema, Map<String, String> args) {
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
    if (accept.equals("all") || accept.equals("any")) {
      return lccns.invalid_collation_key();
    } else {
      return null;
    }
  }
}
