package edu.umich.library.library_identifier.normalizers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LCCallNumberSimple implements CallnumberInterface {

    public  String original;
    public  String letters = "";
    public  String digits = "";
    public  String decimals = "";
    public  String rest = "";
    public  Boolean isValid = false;


    private static final Logger LOGGER = LoggerFactory.getLogger(LCCallNumberSimple.class);

    public static Pattern lc_start = Pattern.compile(
            "^\\s*(?<letters>\\p{L}{1,4})\\s*" + // 1-4 initial letters, plus optional whitespace
                    "(?<digits>\\d+)" +                  // any number of digits
                    "(?:\\.(?<decimals>\\d+))?(?<rest>.*)$");    // an optional decimal ('.' plus digits)


    public LCCallNumberSimple(String str) {
        original = str.trim();
        Matcher m = lc_start.matcher(original.toLowerCase());
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

    }

    public String collation_key() {
        String key =  collation_letters() + collation_digits() + collation_decimals() + collation_rest();
        return key.trim();
    }

    public String invalid_collation_key() {
        return cleanup_freetext(original).trim();
    }

    public String any_collation_key() {
        if (isValid) {
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
        return digit_length.toString() + digits;
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
        if (str.length() == 0) {
            return "";
        }
        return str.toLowerCase(Locale.ROOT).replaceAll("\\s+\\.(\\p{L})", " $1")
            .replaceAll("(\\d)\\.(\\d)", "$1AAAAA$2")
            .replaceAll("\\p{P}", "")
            .replaceAll("(\\d)AAAAA(\\d)", "$1.$2");

    }
}


