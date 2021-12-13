package edu.umich.library.library_identifier.normalizers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.invoke.MethodHandles;

public class AnyCallNumberSimple {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public String original;
  public String valid_key;
  public Boolean isValid;
  public LCCallNumberSimple lc;

  public AnyCallNumberSimple(String str) {
    original = str;
    lc = new LCCallNumberSimple(str);
    valid_key = getValidKey(original);
    isValid = !(valid_key == null);
  }

  public String getValidKey(String str) {
    if (lc.isValid) return lc.collation_key();

    DeweySimple d = new DeweySimple(str);
    if (d.isValid) return d.collation_key();

    return null;
  }

  public String collation_key() {
    return valid_key;
  }

  /**
   * The LC code has pretty good passthrough normalization, so just use it if need be
   */
  public String any_collation_key() {
    if (isValid) {
      return valid_key;
    } else {
      return lc.invalid_collation_key();
    }
  }



}
