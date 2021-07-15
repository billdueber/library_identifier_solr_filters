package edu.umich.library.library_identifier.normalizers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeweyCallNumberSimple implements CallnumberInterface {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeweyCallNumberSimple.class);

  public  String original;
  public  String digits = "";
  public  String decimals = "";
  public  String rest = "";
  public  Boolean isValid = false;

  public static Pattern dewey_pattern = Pattern.compile(
      "^\\s*(?<digits>\\d{3})\\s*(?<decimals>\\.\\d+)?\\s*\\.?(?<rest>.*)$",
      Pattern.COMMENTS);

  public DeweyCallNumberSimple(String str) {
    this.original = str.trim().toLowerCase(Locale.ROOT);
    Matcher m = dewey_pattern.matcher(this.original);
    if (m.matches()) {
      isValid  = true;
      digits  =  m.group("digits");
      decimals = m.group("decimals");
      rest     = m.group("rest");

      if (decimals == null) {
        decimals = "";
      }

      if (rest == null) {
        rest = "";
      } else {
        rest = trim_punctuation(rest).replaceAll("\\p{Punct}", "/");
      }

    } else {
      LOGGER.debug("Dewey Callnumber '" + original + "' is invalid.");
      isValid = false;
    }
  }

  /**
   * Dewey normalization is really easy -- just fix spacing and throw it all together
   */

  public String collation_key() {
    String c = digits + decimals + " " + basic_text_normalization(rest);
    return c.trim();
  }

  public String invalid_collation_key() {
    return basic_text_normalization(original);
  }

  public String any_collation_key() {
    if (isValid) {
      return collation_key();
    } else {
      return invalid_collation_key();
    }
  }

  private String trim_punctuation(String str) {
    if (str.isEmpty()) return "";
    Pattern p = Pattern.compile("^[\\s\\p{Punct}]*(?<meat>.*?)[\\s\\p{Punct}]*$");
    Matcher m = p.matcher(str);
    if (m.matches()) {
      return m.group("meat");
    } else {
      return str;
    }
  }

  private String basic_text_normalization(String str) {
    String s =  str.replaceAll("\\s+", " ")
        .toLowerCase(Locale.ROOT);
    return trim_punctuation(s);
  }




}
