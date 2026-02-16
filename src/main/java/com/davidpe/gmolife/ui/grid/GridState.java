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

  private void validateCoordinates(int row, int column) {
    Objects.checkIndex(row, rows);
    Objects.checkIndex(column, columns);
  }
}
