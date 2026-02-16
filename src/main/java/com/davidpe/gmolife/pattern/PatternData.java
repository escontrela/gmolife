package com.davidpe.gmolife.pattern;

public record PatternData(int rows, int columns, boolean[][] cells) {

  public PatternData {
    if (rows < 1 || columns < 1) {
      throw new IllegalArgumentException("Pattern dimensions must be positive.");
    }
    if (cells == null || cells.length != rows) {
      throw new IllegalArgumentException("Pattern rows must match declared size.");
    }
    for (int row = 0; row < rows; row++) {
      if (cells[row] == null || cells[row].length != columns) {
        throw new IllegalArgumentException("Pattern columns must match declared size.");
      }
    }
  }
}
