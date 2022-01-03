package edu.umich.library.library_identifier.normalizers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class AbstractCallNumber {

  public String trimmed_original;
  public Boolean isValid;

  abstract Boolean has_valid_key();
  abstract String valid_key();

  abstract Boolean has_valid_truncated_key();
  abstract String valid_truncated_key();

  abstract String invalid_key();

  public String any_valid_key() {
    if (has_valid_key()) return valid_key();
    if (has_valid_truncated_key()) return valid_truncated_key();
    return null;
  }

  public String best_key(Boolean passThroughOnError, Boolean allowTruncated) {
    if (has_valid_truncated_key()) return valid_key();
    if (allowTruncated && has_valid_truncated_key()) return valid_truncated_key();
    if (passThroughOnError) return invalid_key();
    return null;
  }

  public String any_key() {
    String k = any_valid_key();
    if (k == null ) {
      return invalid_key();
    } else {
      return k;
    }
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
