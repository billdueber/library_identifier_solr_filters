package edu.umich.library.library_identifier.normalizers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class DeweySimpleTest {

    @ParameterizedTest
    @CsvFileSource(files = "src/test/java/edu/umich/library/library_identifier/normalizers/dewey_pairs.tsv", delimiterString = "->")
    void collation_key(String original, String collation) {
        DeweySimple dewey = new DeweySimple(original);
        String key = dewey.collation_key();
        dewey.logger().debug("Checking given " + collation + " against " + key);
        assert(key).equals(collation.toString());
    }
}