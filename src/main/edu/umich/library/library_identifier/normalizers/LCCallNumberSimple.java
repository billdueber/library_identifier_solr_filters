package edu.umich.library.library_identifier.normalizers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LCCallNumberSimple extends AbstractCallNumber {

  public String letters = "";
  public String digits = "";
  public String decimals = "";
  public String rest = "";


  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  // THe letters for a callnumber are either one letter, two letters, a K followed by 0-2 letters, or
  // the three letter sequence LAW

  public static String letter_pat = "(?<letters>(?:LAW|law|Law|[Kk]\\p{L}{0,2}|\\p{L}{1,2}))";

  public static Pattern lc_start = Pattern.compile(
//      "^\\s*(?<letters>[KkLl]?\\p{L}{1,2})[-\\s]*" + // 1-2 (3 in the Ks) initial letters, plus optional whitespace
        "^\\s*" + letter_pat + "\\s*" +
          "(?<digits>\\d+)" +                  // any number of digits
          "(?:\\.(?<decimals>\\d+))?" +   // an optional decimal ('.' plus digits)
          "(?<rest>.*)$"        // Whatever's left
  );


  // When searching, we'll often want a range query that starts with only letters
  // That can be a single letter, any two-letter combination, or a set of three
  // letters starting with "K" (books about legal issues) or "L" (more of the same).
  // @TODO Put a guard around letter-only queries so we only accept them when an argument to the constructor says to.
  public static Pattern acceptable_only_letters = Pattern.compile("^\\p{L}{1,3}$");


  public LCCallNumberSimple(String str) {
    trimmed_original = trim_punctuation(str.trim()).trim().toLowerCase();
    Matcher m = lc_start.matcher(trimmed_original);
    if (m.matches()) {
      isValid  = true;
      letters  = m.group("letters");
      digits   = m.group("digits");
      decimals = m.group("decimals");
      rest     = m.group("rest");
    } else {
      LOGGER.debug("LC Callnumber '" + trimmed_original + "' is invalid.");
      isValid = false;
    }
    if (has_valid_truncated_key()) {
      LOGGER.debug("Original '" + trimmed_original + "'matches one-acceptable_only pattern");
    }
  }

  public Logger logger() {
    return LOGGER;
  }

  public Boolean has_valid_key() {
    return isValid;
  }
  public String valid_key() {
    return collation_key();
  }

  public String collation_key() {
    if (isValid) {
      String key = collation_letters() + collation_digits() + collation_decimals() +  collation_rest();
      return collapse_spaces(key);
    } else {
      return null;
    }
  }

  public Boolean has_valid_truncated_key() {
    return is_acceptable_truncated_query(trimmed_original);
  }
  public String valid_truncated_key() {
    if (has_valid_truncated_key()) {
      return trimmed_original;
    } else {
      return null;
    }
  }

  public String invalid_key() {
    return cleanup_freetext(trimmed_original);
  }



  private String collation_letters() {
    return letters;
  }

  private String collation_digits() {
    Integer digit_length = digits.length();
    return digit_length + digits;
  }

  private String collation_decimals() {
    if (decimals == null) {
      return "";
    } else {
      return "." + decimals;
    }
  }

  private String collation_rest() {
    if ((rest == null) || (rest.equals(""))) {
      return "";
    } else {
      return " " + cleanup_freetext(rest);
    }
  }

  private String cleanup_freetext(String str) {
    if (str == null)  return "";
    String s = str.trim();
    if (s.equals("")) {
      return s;
    }
    String rv = replace_dot_before_letter_with_space(s);
    rv = remove_dots_between_letters(rv);
    rv = remove_non_decimal_point_punctuation(rv);
    rv = force_space_between_digit_and_letter(rv);
    return collapse_spaces(rv);

  }


  private Boolean is_acceptable_truncated_query(String str) {
    return acceptable_only_letters.matcher(str).matches();
  }



  private String remove_dots_between_letters(String str) {
    return str.replaceAll("(\\p{L})\\.(\\p{L})", "$1$2");
  }

  private String replace_dot_before_letter_with_space(String str) {
    return str.replaceAll("\\s+\\.(\\p{L})", " $1");
  }

  private String remove_non_decimal_point_punctuation(String str) {
    return str.replaceAll("(\\d)\\.(\\d)", "$1AAAAA$2")
        .replaceAll("\\p{P}", "")
        .replaceAll("(\\d)AAAAA(\\d)", "$1.$2");
  }

  private String force_space_between_digit_and_letter(String str) {
    return str.replaceAll("(\\d)(\\p{L})", "$1 $2");
  }

}


