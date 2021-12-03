package edu.umich.library.library_identifier.schema;

import edu.umich.library.library_identifier.normalizers.AnyCallNumberSimple;
import edu.umich.library.library_identifier.normalizers.DeweySimple;
import edu.umich.library.library_identifier.normalizers.LCCallNumberSimple;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.StrField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Map;

public class CallNumberSortKeyFieldType extends StrField {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private Boolean passThroughInvalid = false;

  // Field delimiter sorts last
  private final String FIELD_DELIMITER = "\\|\\|";

  // End of callnumber sorts first (so A1<field delim> sorts before A1 1<field delim>
  private final String END_OF_CALLNUMBER = "  ";

  protected void init(IndexSchema schema, Map<String, String> args) {
    super.init(schema, args);
    String p = args.remove("passThroughInvalid");
    if (p != null) {
      passThroughInvalid = Boolean.parseBoolean(p);
    }
  }

  public String validCallnumber(String str) {
    LCCallNumberSimple lc = new LCCallNumberSimple(str);
    if (lc.isValid) return lc.collation_key();

    DeweySimple d = new DeweySimple(str);
    if (d.isValid) return d.collation_key();

    return null;
  }

  public String bundled_fields(String normalized_cn, String appended_field) {
    return normalized_cn + END_OF_CALLNUMBER + FIELD_DELIMITER + appended_field;
  }

  @Override
  public String toInternal(String val) {
    String[] fields = val.split(FIELD_DELIMITER, 2);
    String appended_fields = "";
    if (fields.length > 1) {
      appended_fields = fields[1];
    }

    AnyCallNumberSimple cn = new AnyCallNumberSimple(fields[0]);

    // Valid? Return it
    if (cn.isValid) return bundled_fields(cn.collation_key(), appended_fields);

    // Not valid, so if we're not passing through, return null.
    if (passThroughInvalid) {
      return  bundled_fields(cn.any_collation_key(), appended_fields);
    } else {
      return null;
    }
  }

}
