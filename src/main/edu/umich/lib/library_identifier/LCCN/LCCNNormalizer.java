package edu.umich.lib.library_identifier.LCCN;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created with IntelliJ IDEA.
 * User: dueberb
 * Date: 1/30/15
 * Time: 10:14 AM
 * To change this template use File | Settings | File Templates.
 */

public class LCCNNormalizer {

    // Normalization patterns from http://www.loc.gov/marc/lccn-namespace.html#syntax
    public static final Pattern trailingSlashPattern = Pattern.compile("^(.*?)/.*$");
    public static final Pattern lccnDashPattern = Pattern.compile("^(\\w+)(?:-(\\d+))?.*$");

    public static String normalize(String raw) {

        // First, ditch all the spaces and lowercase
        raw = raw.replaceAll("\\s+", "").toLowerCase();

        // Lose everything after a slash
        Matcher m = trailingSlashPattern.matcher(raw);
        if (m.matches()) {
            raw = m.group(1);
        }

        // See if it's even viable. If not, return as-is
        m = lccnDashPattern.matcher(raw);
        if (!(m.matches())) {
            return raw;
        }

        // Otherwise, build it up.
        String prefix = m.group(1);
        String postHyphenDigits = m.group(2);

        // If there wasn't a hyphen, just return the prefix
        if (postHyphenDigits == null || postHyphenDigits.length() == 0) {
            return prefix;
        }

        // If we get here, there *were* digits after the hyphen; we may need to build
        // them out to six digits (zero-padded on the left)

        if (postHyphenDigits.length() < 6) {
            postHyphenDigits = String.format("%06d", Integer.parseInt(postHyphenDigits));
        }
        return prefix + postHyphenDigits;

    }


}
