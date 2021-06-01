package edu.umich.library.library_identifier.normalizers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

class LCCallNumberSimpleTest {

    @ParameterizedTest
    @CsvFileSource(files = "src/test/java/edu/umich/library/library_identifier/normalizers/lc_collation_pairs.tsv", delimiterString = "->")
    void collation_key(String original, String collation) {
        String key = new LCCallNumberSimple(original).collation_key();
        assert(key).equals(collation);
    }
}