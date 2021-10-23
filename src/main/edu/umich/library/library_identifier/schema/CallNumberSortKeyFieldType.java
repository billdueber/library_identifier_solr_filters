package edu.umich.library.library_identifier.schema;

import edu.umich.library.library_identifier.normalizers.LCCallNumberSimple;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.schema.StrField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Map;

public class CallNumberSortKeyFieldType extends StrField  {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private Boolean passThroughInvalid = false;

  private final String FIELD_DELIMITER = "\\|\\|";

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
    String rest_of_fields = "";
    if (fields.length > 1) {
      rest_of_fields = fields[1];
    }

    LCCallNumberSimple lccns = new LCCallNumberSimple(fields[0]);
    if (passThroughInvalid) {
      return lccns.any_collation_key() + "!!" + rest_of_fields;
    } else {
      return lccns.collation_key() + "!!" + rest_of_fields;
    }
  }

}
