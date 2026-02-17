package com.davidpe.gmolife.pattern;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class PatternIO {

  private PatternIO() {
  }

  public static void write(Path path, boolean[][] pattern) throws IOException {
    Files.writeString(path, serialize(pattern));
  }

  public static PatternData read(Path path) throws IOException {
    String content = Files.readString(path);
    return parse(content);
  }

  public static String serialize(boolean[][] pattern) {
    ensureRectangular(pattern);
    int rows = pattern.length;
    int columns = pattern[0].length;
    StringBuilder builder = new StringBuilder();
    builder.append(rows).append(' ').append(columns).append('\n');
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        builder.append(pattern[row][column] ? '1' : '0');
      }
      builder.append('\n');
    }
    return builder.toString();
  }

  public static PatternData parse(String content) {
    if (content == null || content.isBlank()) {
      throw new IllegalArgumentException("Pattern file is empty.");
    }
    String[] rawLines = content.split("\\R");
    List<String> lines = new ArrayList<>();
    for (String line : rawLines) {
      if (line != null && !line.isBlank()) {
        lines.add(line.trim());
      }
    }
    if (lines.isEmpty()) {
      throw new IllegalArgumentException("Pattern file is empty.");
    }
    String[] headerParts = lines.get(0).trim().split("\\s+");
    if (headerParts.length != 2) {
      throw new IllegalArgumentException("Header must contain rows and columns.");
    }
    int rows;
    int columns;
    try {
      rows = Integer.parseInt(headerParts[0]);
      columns = Integer.parseInt(headerParts[1]);
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("Header rows/columns must be integers.");
    }
    if (rows < 1 || columns < 1) {
      throw new IllegalArgumentException("Header rows/columns must be positive.");
    }
    if (lines.size() - 1 < rows) {
      throw new IllegalArgumentException("Pattern file does not contain enough rows.");
    }
    boolean[][] cells = new boolean[rows][columns];
    for (int row = 0; row < rows; row++) {
      String rowLine = lines.get(row + 1).replaceAll("\\s+", "");
      if (rowLine.length() != columns) {
        throw new IllegalArgumentException("Row " + (row + 1) + " length mismatch.");
      }
      for (int column = 0; column < columns; column++) {
        char value = rowLine.charAt(column);
        if (value == '1') {
          cells[row][column] = true;
        } else if (value == '0') {
          cells[row][column] = false;
        } else {
          throw new IllegalArgumentException("Invalid cell value at row " + (row + 1) + ".");
        }
      }
    }
    return new PatternData(rows, columns, cells);
  }

  private static void ensureRectangular(boolean[][] pattern) {
    if (pattern == null || pattern.length == 0) {
      throw new IllegalArgumentException("Pattern must not be empty.");
    }
    int columns = pattern[0].length;
    if (columns == 0) {
      throw new IllegalArgumentException("Pattern columns must be positive.");
    }
    for (int row = 0; row < pattern.length; row++) {
      if (pattern[row] == null || pattern[row].length != columns) {
        throw new IllegalArgumentException("Pattern must be rectangular.");
      }
    }
  }
}
