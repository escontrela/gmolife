package com.davidpe.gmolife.ui.grid;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GridStateTest {

  @Test
  void birthsWhenDeadCellHasThreeNeighbors() {
    GridState grid = new GridState(3, 3);
    grid.setCell(0, 1, true);
    grid.setCell(1, 0, true);
    grid.setCell(1, 1, true);

    grid.advance();

    assertTrue(grid.isAlive(0, 0));
  }

  @Test
  void survivesWithTwoNeighbors() {
    GridState grid = new GridState(3, 3);
    grid.setCell(1, 1, true);
    grid.setCell(1, 0, true);
    grid.setCell(0, 1, true);

    grid.advance();

    assertTrue(grid.isAlive(1, 1));
  }

  @Test
  void diesWithUnderPopulation() {
    GridState grid = new GridState(3, 3);
    grid.setCell(1, 1, true);
    grid.setCell(1, 0, true);

    grid.advance();

    assertFalse(grid.isAlive(1, 1));
  }

  @Test
  void toroidalEdgesWrapNeighbors() {
    GridState toroidal = new GridState(3, 3);
    toroidal.setToroidal(true);
    toroidal.setCell(0, 2, true);
    toroidal.setCell(2, 0, true);
    toroidal.setCell(2, 2, true);

    toroidal.advance();

    assertTrue(toroidal.isAlive(0, 0));

    GridState bounded = new GridState(3, 3);
    bounded.setCell(0, 2, true);
    bounded.setCell(2, 0, true);
    bounded.setCell(2, 2, true);

    bounded.advance();

    assertFalse(bounded.isAlive(0, 0));
  }
}
