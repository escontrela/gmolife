package com.davidpe.gmolife.pattern;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PatternIOTest {

  @Test
  void serializeAndParseRoundTrip() {
    boolean[][] pattern =
        new boolean[][] {
          {true, false, true},
          {false, true, false}
        };

    String serialized = PatternIO.serialize(pattern);
    PatternData parsed = PatternIO.parse(serialized);

    assertEquals(2, parsed.rows());
    assertEquals(3, parsed.columns());
    assertArrayEquals(pattern, parsed.cells());
  }

  @Test
  void parseRejectsInvalidHeader() {
    assertThrows(IllegalArgumentException.class, () -> PatternIO.parse("2\n10\n01\n"));
  }

  @Test
  void parseRejectsInvalidCellValues() {
    assertThrows(
        IllegalArgumentException.class,
        () -> PatternIO.parse("2 2\n10\n0x\n"));
  }
}
