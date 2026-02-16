package com.davidpe.gmolife.ui;

import com.davidpe.gmolife.ui.grid.EditableGridView;
import com.davidpe.gmolife.ui.grid.GridState;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public final class MainWindow {

  private static final int GRID_ROWS = 25;
  private static final int GRID_COLUMNS = 25;
  private static final double CELL_SIZE = 24;

  private final GridState gridState = new GridState(GRID_ROWS, GRID_COLUMNS);
  private EditableGridView gridView;

  public void show(Stage stage) {
    BorderPane root = new BorderPane();
    root.setTop(buildControls());
    root.setCenter(buildGrid());

    Scene scene = new Scene(root, 960, 640);
    stage.setTitle("Game of Life");
    stage.setScene(scene);
    stage.show();
  }

  private StackPane buildGrid() {
    gridView = new EditableGridView(gridState, CELL_SIZE);
    gridView.setPadding(new Insets(24));

    StackPane container = new StackPane(gridView);
    container.setPadding(new Insets(16));
    return container;
  }

  private HBox buildControls() {
    Button stepButton = new Button("Step");
    stepButton.setOnAction(event -> {
      gridState.advance();
      gridView.refresh();
    });
    HBox controls = new HBox(stepButton);
    controls.setPadding(new Insets(16));
    controls.setSpacing(12);
    return controls;
  }
}
