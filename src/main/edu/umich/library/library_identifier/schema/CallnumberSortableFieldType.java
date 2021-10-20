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
import java.util.Map;
import java.util.Objects;

public class CallnumberSortableFieldType extends StrField {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private Boolean passThroughInvalid = false;

  protected void init(IndexSchema schema, Map<String, String> args) {
    super.init(schema, args);
    String p = args.remove("passThroughInvalid");
    if (p != null) {
      passThroughInvalid = Boolean.parseBoolean(p);
    }
  }

  @Override
  public String toInternal(String val) {
    LCCallNumberSimple lccns = new LCCallNumberSimple(val);
    if (passThroughInvalid) {
      return lccns.any_collation_key();
    } else {
      return lccns.collation_key();
    }
  }

  @Override
  public IndexableField createField(SchemaField field, Object value) {
    if (!field.indexed() && !field.stored()) {
      if (log.isTraceEnabled())
        log.trace("Ignoring unindexed/unstored field: {}", field);
      return null;
    }


    String val = value.toString();
    if (val == null) return null;

    if (!passThroughInvalid) {
      if (!(new LCCallNumberSimple(val).isValid)) return null;
    }

    org.apache.lucene.document.FieldType newType = new org.apache.lucene.document.FieldType();
    newType.setTokenized(true);
    newType.setStored(field.stored());
    newType.setIndexOptions(IndexOptions.DOCS);

    return createField(field.getName(), val, newType);
  }
}
