package com.davidpe.gmolife.ui.grid;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import java.util.function.BiConsumer;

public final class EditableGridView extends GridPane {

  private static final String DEAD_FILL = "#f5f6f7";
  private static final String ALIVE_FILL = "#2ecc71";
  private static final String DEAD_BORDER = "#c7cdd1";
  private static final String ALIVE_BORDER = "#1f2d3a";

  private final GridState gridState;
  private double cellSize;
  private boolean gridLinesVisible = true;
  private Region[][] cellViews;
  private Runnable onEdit;
  private BiConsumer<Integer, Integer> onHover;
  private Runnable onHoverExit;

  public EditableGridView(GridState gridState, double cellSize) {
    this.gridState = gridState;
    this.cellSize = cellSize;
    buildCells();
    setOnMouseExited(event -> notifyHoverExit());
  }

  private void buildCells() {
    cellViews = new Region[gridState.getRows()][gridState.getColumns()];
    for (int row = 0; row < gridState.getRows(); row++) {
      for (int column = 0; column < gridState.getColumns(); column++) {
        Region cell = createCell(row, column);
        cellViews[row][column] = cell;
        add(cell, column, row);
      }
    }
  }

  private Region createCell(int row, int column) {
    Region cell = new Region();
    applyCellSize(cell);
    updateCellStyle(cell, gridState.isAlive(row, column));
    cell.setOnMouseEntered(event -> notifyHover(row, column));
    cell.setOnMousePressed(
        event -> {
          gridState.toggle(row, column);
          updateCellStyle(cell, gridState.isAlive(row, column));
          notifyEdit();
        });
    cell.setOnDragDetected(event -> cell.startFullDrag());
    cell.setOnMouseDragEntered(
        event -> {
          gridState.toggle(row, column);
          updateCellStyle(cell, gridState.isAlive(row, column));
          notifyEdit();
          notifyHover(row, column);
        });
    return cell;
  }

  private void applyCellSize(Region cell) {
    cell.setMinSize(cellSize, cellSize);
    cell.setPrefSize(cellSize, cellSize);
  }

  public void setCellSize(double cellSize) {
    this.cellSize = cellSize;
    if (cellViews == null) {
      return;
    }
    for (int row = 0; row < gridState.getRows(); row++) {
      for (int column = 0; column < gridState.getColumns(); column++) {
        applyCellSize(cellViews[row][column]);
      }
    }
  }

  public void setOnEdit(Runnable onEdit) {
    this.onEdit = onEdit;
  }

  public void setOnHover(BiConsumer<Integer, Integer> onHover) {
    this.onHover = onHover;
  }

  public void setOnHoverExit(Runnable onHoverExit) {
    this.onHoverExit = onHoverExit;
  }

  private void notifyEdit() {
    if (onEdit != null) {
      onEdit.run();
    }
  }

  private void notifyHover(int row, int column) {
    if (onHover != null) {
      onHover.accept(row, column);
    }
  }

  private void notifyHoverExit() {
    if (onHoverExit != null) {
      onHoverExit.run();
    }
  }

  private void updateCellStyle(Region cell, boolean alive) {
    cell.setStyle(styleFor(alive));
  }

  public void setGridLinesVisible(boolean visible) {
    gridLinesVisible = visible;
    refresh();
  }

  private String styleFor(boolean alive) {
    String fill = alive ? ALIVE_FILL : DEAD_FILL;
    if (!gridLinesVisible) {
      return "-fx-background-color: " + fill + "; -fx-border-color: transparent; -fx-border-width: 0;";
    }
    String border = alive ? ALIVE_BORDER : DEAD_BORDER;
    return "-fx-background-color: "
        + fill
        + "; -fx-border-color: "
        + border
        + "; -fx-border-width: 0.5;";
  }

  public void refresh() {
    for (int row = 0; row < gridState.getRows(); row++) {
      for (int column = 0; column < gridState.getColumns(); column++) {
        updateCellStyle(cellViews[row][column], gridState.isAlive(row, column));
      }
    }
  }
}
