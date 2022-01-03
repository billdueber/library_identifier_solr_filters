package edu.umich.library.library_identifier.normalizers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * For purposes of this simple normalizer, define a valid dewey number as follows:
 *   * Starts with three digits
 *   * Optional decimal places which can include IGNORED slashes or apostrophes
 *   * Whatever's left:
 *    * Lowercase
 *    * remove dots after a letter
 *    * Trim spaces
 *    * remove leading/trailing punctuation (which will take care of a dot before a cutter)
 *    * trim spaces again
 *    * compact spaces
 *    * add back in with a preceding space
 *
 */
public class DeweySimple extends AbstractCallNumber {

  protected static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  public static Logger logger() {
    return LOGGER;
  }

  public String digits = "";
  public String decimals = "";
  public String rest = "";

  public static Pattern dewey = Pattern.compile(
      "^\\s*(?<digits>\\d{3})" +
      "(?:\\.(?<decimals>[\\d/']+))" +
      "\\s*(?<rest>.*)$");

  public static Pattern acceptable_three_digits = Pattern.compile("^\\s*\\d{1,3}\\s*$");


  public DeweySimple(String str) {
    trimmed_original = trim_punctuation(str.trim().toLowerCase());
    Matcher m = dewey.matcher(trimmed_original);
    if (m.matches()) {
      isValid  = true;
      digits   = m.group("digits");
      decimals = fixed_decimals(m.group("decimals"));
      rest     = cleanup_freetext(m.group("rest"));
    } else {
      logger().debug(trimmed_original + " is invalid");
      isValid = false;
    }

  }

  public Boolean has_valid_key() {
    return isValid;
  }
  public String valid_key() {
    return collation_key();
  }

  public String collation_key() {
    if (isValid) {
      return trim_punctuation(digits + decimals + rest);
    } else {
      return null;
    }
  }

  public Boolean has_valid_truncated_key() {
    return is_valid_truncated_query(trimmed_original);
  }

  public String valid_truncated_key() {
    if (is_valid_truncated_query(trimmed_original)) {
      return trimmed_original;
    } else {
      return null;
    }
  }

  public String invalid_key() {
    return cleanup_freetext(trimmed_original);
  }


  private String fixed_decimals(String str) {
    if (str == null) return "";
    if (str.trim().equals("")) return "";
    return "." + str.trim().replaceAll("[/']+", "");
  }

  private String cleanup_freetext(String str) {
    if (str == null)  return "";
    String s = str.trim();
    if (s.equals("")) {
      return s;
    }
    s = remove_dots_between_letters(s);
    s = ditch_dots_after_letters(s);
    s = trim_punctuation(s);
    s = collapse_spaces(s);
    s = " " + s;
    return s;
  }

  private String remove_dots_between_letters(String str) {
    return str.replaceAll("(\\p{L})\\.(\\p{L})", "$1$2");
  }

  public String ditch_dots_after_letters(String str) {
    return str.replaceAll("(\\p{L})\\.", "$1 ");
  }

  private Boolean is_valid_truncated_query(String str) {
    return acceptable_three_digits.matcher(str).matches();
  }

}
