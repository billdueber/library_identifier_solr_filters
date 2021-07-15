package edu.umich.library.library_identifier.normalizers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

class DeweyCallNumberSimpleTest {

  @ParameterizedTest
  @CsvFileSource(files = "src/test/java/edu/umich/library/library_identifier/normalizers/dewey_collation_pairs.csv", delimiterString = "->")
  void collation_key(String original, String collation) {
    DeweyCallNumberSimple d = new DeweyCallNumberSimple(original);
    String key = d.collation_key();
    assert(key).equals(collation);
  }

  @Test
  void invalid_collation_key() {
  }

  @Test
  void any_collation_key() {
  }
}