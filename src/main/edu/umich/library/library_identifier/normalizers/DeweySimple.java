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
public class DeweySimple {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  public Logger logger() {
    return LOGGER;
  }

  public String original;
  public String digits = "";
  public String decimals = "";
  public String rest = "";
  public Boolean isValid;

  public static Pattern dewey = Pattern.compile(
      "^\\s*(?<digits>\\d{3})" +
      "(?:\\.?(?<decimals>[\\d/']+))?" +
      "\\s*(?<rest>.*)$");


  public DeweySimple(String str) {
    String original = trim_punctuation(str.trim().toLowerCase());
    Matcher m = dewey.matcher(original);
    if (m.matches()) {
      isValid  = true;
      digits   = m.group("digits");
      decimals = fixed_decimals(m.group("decimals"));
      rest     = fixed_rest(m.group("rest"));
    } else {
      LOGGER.debug("'" + original + "' is not valid Dewey");
      isValid = false;
    }
  }

  public String collation_key() {
    if (isValid) {
      return trim_punctuation(digits + decimals + rest);
    } else {
      return null;
    }
  }

  public String fixed_decimals(String str) {
    if (str == null) return "";
    if (str.trim() == "") return "";
    return "." + str.trim().replaceAll("[/']+", "");
  }

  public String fixed_rest(String str) {
    if (str == null)  return "";
    String s = str.trim();
    if (s.equals("")) {
      return s;
    }
    s = ditch_dots_after_letters(s);
    s = trim_punctuation(s);
    s = collapse_spaces(s);
    s = " " + s;
    return s;
  }

  public String ditch_dots_after_letters(String str) {
    return str.replaceAll("(\\p{L})\\.", "$1 ");
  }

  // For trimming punctuation
  public static Pattern trim_punct = Pattern.compile(
      "^\\p{Punct}*(.*?)\\p{Punct}*$"
  );

  public String trim_punctuation(String str) {
    Matcher m = trim_punct.matcher(str);
    if (m.matches()) {
      return m.group(1);
    } else {
      return str;
    }
  }

  public String collapse_spaces(String str) {
    return str.trim().replaceAll("\\s+", " ");
  }

}
