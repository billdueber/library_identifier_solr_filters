package edu.umich.lib.library_identifier.callnumber;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

class AnyCallNumberSimpleTest {

    @ParameterizedTest
    @CsvFileSource(files = "src/test/java/edu/umich/lib/library_identifier/callnumber/any_valid_key_pairs.tsv", delimiterString = "->")
    void any_valid_key(String original, String collated)  {
        AnyCallNumberSimple acn = new AnyCallNumberSimple(original);
        String computed = acn.any_valid_key();
        if (computed == null) computed = "null";
        assertEquals(collated.toString(), computed);
    }

    @Test
    void valid_truncated_key() {
    }

    @Test
    void invalid_key() {
    }
}