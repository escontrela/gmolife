package com.davidpe.gmolife.ui.grid;

import java.util.Objects;

public final class GridState {

  private final int rows;
  private final int columns;
  private final boolean[][] cells;

  public GridState(int rows, int columns) {
    if (rows < 1 || columns < 1) {
      throw new IllegalArgumentException("Grid dimensions must be positive.");
    }
    this.rows = rows;
    this.columns = columns;
    this.cells = new boolean[rows][columns];
  }

  public int getRows() {
    return rows;
  }

  public int getColumns() {
    return columns;
  }

  public boolean isAlive(int row, int column) {
    validateCoordinates(row, column);
    return cells[row][column];
  }

  public void toggle(int row, int column) {
    validateCoordinates(row, column);
    cells[row][column] = !cells[row][column];
  }

  public void advance() {
    boolean[][] next = new boolean[rows][columns];
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        int neighbors = countAliveNeighbors(row, column);
        boolean alive = cells[row][column];
        if (alive) {
          next[row][column] = neighbors == 2 || neighbors == 3;
        } else {
          next[row][column] = neighbors == 3;
        }
      }
    }
    for (int row = 0; row < rows; row++) {
      System.arraycopy(next[row], 0, cells[row], 0, columns);
    }
  }

  public int countAliveCells() {
    int count = 0;
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        if (cells[row][column]) {
          count++;
        }
      }
    }
    return count;
  }

  public void clear() {
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        cells[row][column] = false;
      }
    }
  }

  private int countAliveNeighbors(int row, int column) {
    int count = 0;
    for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
      for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
        if (rowOffset == 0 && columnOffset == 0) {
          continue;
        }
        int neighborRow = row + rowOffset;
        int neighborColumn = column + columnOffset;
        if (neighborRow < 0 || neighborRow >= rows) {
          continue;
        }
        if (neighborColumn < 0 || neighborColumn >= columns) {
          continue;
        }
        if (cells[neighborRow][neighborColumn]) {
          count++;
        }
      }
    }
    return count;
  }

  private void validateCoordinates(int row, int column) {
    Objects.checkIndex(row, rows);
    Objects.checkIndex(column, columns);
  }
}
