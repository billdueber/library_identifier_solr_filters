package edu.umich.library.library_identifier.schema;

import edu.umich.library.library_identifier.normalizers.LCCallNumberSimple;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.schema.SchemaField;
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

  @Override
  public IndexableField createField(SchemaField field, Object value) {
    if (!field.indexed() && !field.stored()) {
      if (log.isTraceEnabled())
        log.trace("Ignoring unindexed/unstored field: {}", field);
      return null;
    }

    String val = value.toString();
    if (val==null) return null;
    org.apache.lucene.document.FieldType newType = new org.apache.lucene.document.FieldType();
    newType.setTokenized(true);
    newType.setStored(field.stored());
    newType.setIndexOptions(IndexOptions.DOCS);

    return createField(field.getName(), val, newType);
  }
}
