package edu.umich.library.library_identifier.schema;

import edu.umich.library.library_identifier.normalizers.AnyCallNumberSimple;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.StrField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Map;

public class CallNumberSortKeyFieldType extends StrField {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  protected Boolean passThroughOnError = false;
  protected Boolean allowTruncated = true;

  // Field delimiter sorts last
  private final String FIELD_DELIMITER = "\u001F";

  // End of callnumber sorts first (so A1<field delim> sorts before A1 1<field delim>
  // Spaces sort nice and early.
  private final String END_OF_CALLNUMBER = "  ";

  protected void init(IndexSchema schema, Map<String, String> args) {
    super.init(schema, args);
    String p = args.remove("passThroughOnError");
    if (p != null) {
      passThroughOnError = Boolean.parseBoolean(p);
    }

    String trunc = args.remove("allowTruncated");
    if (trunc != null) {
      allowTruncated = true;
    }
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
    if (cn.has_valid_key()) return bundled_fields(cn.valid_key(), appended_fields);
    if (allowTruncated && cn.has_valid_truncated_key()) return bundled_fields(cn.valid_truncated_key(), appended_fields);

    // Not valid at all, so if we're not passing through, return null.
    if (passThroughOnError) {
      return  bundled_fields(cn.invalid_key(), appended_fields);
    } else {
      return null;
    }
  }


  public String bundled_fields(String normalized_cn, String appended_field) {
    return normalized_cn + END_OF_CALLNUMBER + FIELD_DELIMITER + appended_field;
  }


}
