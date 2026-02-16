package com.davidpe.gmolife.ui.grid;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public final class EditableGridView extends GridPane {

  private static final String DEAD_STYLE = "-fx-background-color: #f5f6f7; -fx-border-color: #c7cdd1; -fx-border-width: 0.5;";
  private static final String ALIVE_STYLE = "-fx-background-color: #2ecc71; -fx-border-color: #1f2d3a; -fx-border-width: 0.5;";

  private final GridState gridState;
  private final double cellSize;

  public EditableGridView(GridState gridState, double cellSize) {
    this.gridState = gridState;
    this.cellSize = cellSize;
    buildCells();
  }

  private void buildCells() {
    for (int row = 0; row < gridState.getRows(); row++) {
      for (int column = 0; column < gridState.getColumns(); column++) {
        Region cell = createCell(row, column);
        add(cell, column, row);
      }
    }
  }

  private Region createCell(int row, int column) {
    Region cell = new Region();
    cell.setPrefSize(cellSize, cellSize);
    updateCellStyle(cell, gridState.isAlive(row, column));
    cell.setOnMouseClicked(event -> {
      gridState.toggle(row, column);
      updateCellStyle(cell, gridState.isAlive(row, column));
    });
    return cell;
  }

  private void updateCellStyle(Region cell, boolean alive) {
    cell.setStyle(alive ? ALIVE_STYLE : DEAD_STYLE);
  }
}
