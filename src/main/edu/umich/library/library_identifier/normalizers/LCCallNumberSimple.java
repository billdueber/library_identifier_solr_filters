package edu.umich.library.library_identifier.normalizers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LCCallNumberSimple {

    public  String original;
    public  String letters = "";
    public  String digits = "";
    public  String decimals = "";
    public  String rest = "";
    public  Boolean isValid;


    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static Pattern lc_start = Pattern.compile(
            "^\\s*(?<letters>\\p{L}{1,3})\\s*" + // 1-3 initial letters, plus optional whitespace
                    "(?<digits>\\d+)" +                  // any number of digits followed by whitespace
                    "(?:\\.(?<decimals>\\d+))?" +   // an optional decimal ('.' plus digits)
                    "(?:\\s+(?<rest>.*))?$" // either end of string, or whitespace plus other stuff
    );
    public static Pattern one_or_two_letters = Pattern.compile("^\\p{L}{1,2}$");



    public LCCallNumberSimple(String str) {
        original = str.trim().toLowerCase();
        LOGGER.warn("got " + original);
        Matcher m = lc_start.matcher(original);
        if (m.matches()) {
            isValid  = true;
            letters  = m.group("letters");
            digits   = m.group("digits");
            decimals = m.group("decimals");
            rest     = m.group("rest");
        } else {
            LOGGER.debug("LC Callnumber '" + original + "' is invalid.");
            isValid = false;
        }
        if (is_one_or_two_letters(original)) {
            LOGGER.warn("Original matches one-or-two-letters");
        }

    }

    public Boolean is_one_or_two_letters(String str) {
        return one_or_two_letters.matcher(original).matches();
    }

    public String collation_key() {
        if (isValid) {
            String key = collation_letters() + collation_digits() + collation_decimals() + " " + collation_rest();
            return key.trim();
        }
        if (is_one_or_two_letters(original)) {
            LOGGER.warn("In here");
           return original;
        } else {
            return null;
        }
    }

    public String invalid_collation_key() {
        return cleanup_freetext(original);
    }

    public String any_collation_key() {
        String c = collation_key();
        if (c != null) {
            return collation_key();
        } else {
            return invalid_collation_key();
        }
    }

    public String collation_letters() {
        return letters;
    }

    public String collation_digits() {
        Integer digit_length = digits.length();
        return digit_length + digits;
    }

    public String collation_decimals() {
        if (decimals == null) {
            return "";
        } else {
            return "." + decimals;
        }
    }

    public String collation_rest() {
        if ((rest == null) || (rest.equals(""))) {
            return "";
        } else {
            return cleanup_freetext(rest);
        }
    }

    private String cleanup_freetext(String str) {
        return str.replaceAll("\\s+\\.(\\p{L})", " $1")
            .replaceAll("(\\d)\\.(\\d)", "$1AAAAA$2")
            .replaceAll("\\p{P}", "")
            .replaceAll("(\\d)AAAAA(\\d)", "$1.$2");

    }
}


