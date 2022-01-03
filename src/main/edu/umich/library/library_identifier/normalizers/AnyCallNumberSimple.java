package edu.umich.library.library_identifier.normalizers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class AnyCallNumberSimple extends AbstractCallNumber {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public String trimmed_original;
  public String either_valid_key;
  public Boolean isValid;
  public LCCallNumberSimple lc;
  public DeweySimple dewey;


  public AnyCallNumberSimple(String str) {
    trimmed_original = str;
    lc               = new LCCallNumberSimple(str);
    dewey            = new DeweySimple(str);
    either_valid_key = getValidKey(trimmed_original);
    isValid          = !(either_valid_key == null);
  }


  public Boolean has_valid_key() {
    return isValid;
  }

  public String valid_key() {
    return either_valid_key;
  }


  public Boolean has_valid_truncated_key() {
    return (lc.has_valid_truncated_key() || dewey.has_valid_truncated_key());
  }

  public String valid_truncated_key() {
    if (lc.has_valid_truncated_key()) return lc.valid_truncated_key();
    if (dewey.has_valid_truncated_key()) return dewey.valid_truncated_key();
    return null;
  }


  public String invalid_key() {
    return lc.invalid_key();
  }


  private String getValidKey(String str) {
    if (lc.isValid) return lc.collation_key();
    if (dewey.isValid) return dewey.collation_key();
    return null;
  }


}
