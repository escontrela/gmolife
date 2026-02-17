package com.davidpe.gmolife.ui.grid;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import java.util.function.BiConsumer;

public final class EditableGridView extends GridPane {

  private static final String DEAD_FILL = "#f5f6f7";
  private static final String ALIVE_FILL = "#2ecc71";
  private static final String DEAD_BORDER = "#c7cdd1";
  private static final String ALIVE_BORDER = "#1f2d3a";
  private static final String HOVER_BORDER = "#f2b01e";

  private final GridState gridState;
  private double cellSize;
  private boolean gridLinesVisible = true;
  private Region[][] cellViews;
  private Runnable onEdit;
  private BiConsumer<Integer, Integer> onHover;
  private Runnable onHoverExit;
  private int hoveredRow = -1;
  private int hoveredColumn = -1;

  public EditableGridView(GridState gridState, double cellSize) {
    this.gridState = gridState;
    this.cellSize = cellSize;
    buildCells();
    setOnMouseExited(
        event -> {
          clearHover();
          notifyHoverExit();
        });
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
    updateCellStyle(cell, gridState.isAlive(row, column), isHovered(row, column));
    cell.setOnMouseEntered(event -> setHoverCell(row, column));
    cell.setOnMousePressed(
        event -> {
          gridState.toggle(row, column);
          updateCellStyle(cell, gridState.isAlive(row, column), isHovered(row, column));
          notifyEdit();
        });
    cell.setOnDragDetected(event -> cell.startFullDrag());
    cell.setOnMouseDragEntered(
        event -> {
          setHoverCell(row, column);
          gridState.toggle(row, column);
          updateCellStyle(cell, gridState.isAlive(row, column), isHovered(row, column));
          notifyEdit();
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
    cell.setStyle(styleFor(alive, false));
  }

  private void updateCellStyle(Region cell, boolean alive, boolean hovered) {
    cell.setStyle(styleFor(alive, hovered));
  }

  public void setCellGridLinesVisible(boolean visible) {
    gridLinesVisible = visible;
    refresh();
  }

  private String styleFor(boolean alive, boolean hovered) {
    String fill = alive ? ALIVE_FILL : DEAD_FILL;
    if (hovered) {
      return "-fx-background-color: "
          + fill
          + "; -fx-border-color: "
          + HOVER_BORDER
          + "; -fx-border-width: 1.5;";
    }
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
        updateCellStyle(
            cellViews[row][column], gridState.isAlive(row, column), isHovered(row, column));
      }
    }
  }

  private void setHoverCell(int row, int column) {
    if (row == hoveredRow && column == hoveredColumn) {
      notifyHover(row, column);
      return;
    }
    if (hoveredRow >= 0 && hoveredColumn >= 0) {
      updateCellStyle(
          cellViews[hoveredRow][hoveredColumn],
          gridState.isAlive(hoveredRow, hoveredColumn),
          false);
    }
    hoveredRow = row;
    hoveredColumn = column;
    updateCellStyle(cellViews[row][column], gridState.isAlive(row, column), true);
    notifyHover(row, column);
  }

  private void clearHover() {
    if (hoveredRow < 0 || hoveredColumn < 0) {
      return;
    }
    updateCellStyle(
        cellViews[hoveredRow][hoveredColumn],
        gridState.isAlive(hoveredRow, hoveredColumn),
        false);
    hoveredRow = -1;
    hoveredColumn = -1;
  }

  private boolean isHovered(int row, int column) {
    return row == hoveredRow && column == hoveredColumn;
  }
}
