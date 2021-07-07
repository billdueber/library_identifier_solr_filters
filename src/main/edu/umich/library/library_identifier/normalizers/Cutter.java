package edu.umich.library.library_identifier.normalizers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cutter {

  private static final Logger LOGGER = LoggerFactory.getLogger(Cutter.class);

  public static String pattern_str = "(?<cutter>\\p{L}\\d+)";
  public static Pattern pattern = Pattern.compile(pattern_str);
  public static Pattern start_pattern = Pattern.compile("^" + pattern_str);

  public String letter = "";
  public String digits = "";

  private Pattern cutterInternal = Pattern.compile("^(?<letter>\\p{L})(?<digits>\\d+)");

  public Cutter(String str) {
    Matcher m = cutterInternal.matcher(str.toUpperCase(Locale.ROOT));
    if (m.matches()) {
      letter = m.group("letter");
      digits = m.group("digits");
    } else {
      LOGGER.debug("String '" + str + "' is not a cutter");
    }
  }

  public String collation_key() {
    return (this.letter + this.digits).toUpperCase();
  }

  public String toString() {
    return letter.toUpperCase() + digits;
  }

}
