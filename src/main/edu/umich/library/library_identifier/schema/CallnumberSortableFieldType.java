package edu.umich.library.library_identifier.schema;

import edu.umich.library.library_identifier.normalizers.LCCallNumberSimple;
import org.apache.solr.schema.StrField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class CallnumberSortableFieldType extends StrField {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  public String toInternal(String val) {
    LCCallNumberSimple lccns = new LCCallNumberSimple(val);
    return lccns.any_collation_key();
  }
}
