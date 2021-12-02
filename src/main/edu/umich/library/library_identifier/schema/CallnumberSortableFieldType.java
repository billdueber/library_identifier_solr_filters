package edu.umich.library.library_identifier.schema;

import com.google.protobuf.Any;
import edu.umich.library.library_identifier.normalizers.AnyCallNumber;
import edu.umich.library.library_identifier.normalizers.LCCallNumberSimple;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.schema.StrField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.AncestorEvent;
import java.lang.invoke.MethodHandles;
import java.util.Map;


public class CallnumberSortableFieldType extends CallNumberSortKeyFieldType {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private Boolean passThroughInvalid = false;

  @Override
  public IndexableField createField(SchemaField field, Object value) {
    if (!field.indexed() && !field.stored()) {
      if (log.isTraceEnabled())
        log.trace("Ignoring unindexed/unstored field: {}", field);
      return null;
    }

    String val = value.toString();
    if (val == null) return null;

    AnyCallNumber cn = new AnyCallNumber(val);

    if (!passThroughInvalid && !cn.isValid) return null;

    org.apache.lucene.document.FieldType newType = new org.apache.lucene.document.FieldType();
    newType.setTokenized(true);
    newType.setStored(field.stored());
    newType.setIndexOptions(IndexOptions.DOCS);

    return createField(field.getName(), val, newType);
  }
}
