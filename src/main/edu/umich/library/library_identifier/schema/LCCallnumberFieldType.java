package edu.umich.library.library_identifier.schema;

import org.apache.jute.Index;
import org.apache.lucene.document.StoredField;
import org.apache.solr.response.TextResponseWriter;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.schema.StrField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.index.IndexableField;

import edu.umich.library.library_identifier.normalizers.LCCallNumberSimple;

public class LCCallnumberFieldType extends StrField {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  public String toInternal(String val) {
    LCCallNumberSimple lccns = new LCCallNumberSimple(val);
    return lccns.any_collation_key();
  }
}
