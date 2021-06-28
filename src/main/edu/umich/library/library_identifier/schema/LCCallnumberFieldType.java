package edu.umich.library.library_identifier.schema;

import org.apache.solr.schema.StrField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import edu.umich.library.library_identifier.normalizers.LCCallNumberSimple;

public class LCCallnumberFieldType extends StrField {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  public String toInternal(String val) {
    this.log.warn("In here");
    LCCallNumberSimple lccns = new LCCallNumberSimple(val);
    return lccns.any_collation_key();

  }

}
