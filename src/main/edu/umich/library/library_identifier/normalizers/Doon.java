package edu.umich.library.library_identifier.normalizers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Doon {

  private static final Logger LOGGER = LoggerFactory.getLogger(Doon.class);

  public static String pattern_str = "(?<doon>\\d+\\p{L}{0,2})";
  public static Pattern pattern = Pattern.compile(pattern_str);
  public static Pattern start_pattern = Pattern.compile("^" + pattern_str);

  private Pattern doonInternal = Pattern.compile("^(?<digits>\\d+)(?<letters>\\p{L}*)")

  public String digits = "";
  public String letters = "";

  public Doon(String str) {
    Matcher m = doonInternal.matcher(str.toUpperCase());
    if (m.matches()) {
      digits  = m.group("digits");
      letters = m.group("letters");
    } else {
      LOGGER.debug("Sent string '" + str + "' to doon; it's not a doon");
    }
  }

  public String toString() {
    return digits + letters;
  }

  public String collation_key() {
    Integer dlen = digits.length();
    return dlen.toString() + digits + letters;
  }


}
