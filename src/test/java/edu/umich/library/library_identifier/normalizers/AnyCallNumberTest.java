package edu.umich.library.library_identifier.normalizers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

class AnyCallNumberTest {

    @ParameterizedTest
    @CsvFileSource(files = "src/test/java/edu/umich/library/library_identifier/normalizers/any_pairs.tsv", delimiterString = "->")
    void collation_key(String original, String collation) {
        AnyCallNumberSimple cn = new AnyCallNumberSimple(original);
        String key = cn.collation_key();
        if (key == null) key = "null";
        assert(key).equals(collation.toString());
    }
}