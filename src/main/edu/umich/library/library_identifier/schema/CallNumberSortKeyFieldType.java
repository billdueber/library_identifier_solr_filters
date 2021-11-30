package edu.umich.library.library_identifier.schema;

import edu.umich.library.library_identifier.normalizers.LCCallNumberSimple;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.StrField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Map;

public class CallNumberSortKeyFieldType extends StrField  {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private Boolean passThroughInvalid = false;

  private final String FIELD_DELIMITER = "\\|\\|";
  private final String END_OF_CALLNUMBER = "!!";

  protected void init(IndexSchema schema, Map<String, String> args) {
    super.init(schema, args);
    String p = args.remove("passThroughInvalid");
    if (p != null) {
      passThroughInvalid = Boolean.parseBoolean(p);
    }
  }

  @Override
  public String toInternal(String val) {
    String[] fields = val.split(FIELD_DELIMITER, 2);
    String appended_fields = "";
    if (fields.length > 1) {
      appended_fields = fields[1];
    }

    LCCallNumberSimple lccns = new LCCallNumberSimple(fields[0]);
    if (passThroughInvalid) {
      return lccns.any_collation_key() + END_OF_CALLNUMBER + FIELD_DELIMITER +  appended_fields;
    } else {
      return lccns.collation_key() + END_OF_CALLNUMBER  + FIELD_DELIMITER +  appended_fields;
    }
  }

}
