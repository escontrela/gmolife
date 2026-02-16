package com.davidpe.gmolife.ui;

import com.davidpe.gmolife.ui.grid.EditableGridView;
import com.davidpe.gmolife.ui.grid.GridState;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public final class MainWindow {

  private static final int GRID_ROWS = 25;
  private static final int GRID_COLUMNS = 25;
  private static final double CELL_SIZE = 24;
  private static final int TICK_MILLIS = 300;

  private final GridState gridState = new GridState(GRID_ROWS, GRID_COLUMNS);
  private EditableGridView gridView;
  private Timeline timeline;
  private int generation;
  private Label generationValue;
  private Label populationValue;

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
      advanceAndRefresh();
    });
    Button playButton = new Button("Play");
    Button pauseButton = new Button("Pause");
    Button resetButton = new Button("Reset");
    pauseButton.setDisable(true);

    timeline = new Timeline(new KeyFrame(Duration.millis(TICK_MILLIS), event -> {
      advanceAndRefresh();
    }));
    timeline.setCycleCount(Timeline.INDEFINITE);

    playButton.setOnAction(event -> {
      timeline.play();
      playButton.setDisable(true);
      pauseButton.setDisable(false);
      stepButton.setDisable(true);
    });

    pauseButton.setOnAction(event -> {
      timeline.pause();
      playButton.setDisable(false);
      pauseButton.setDisable(true);
      stepButton.setDisable(false);
    });

    resetButton.setOnAction(event -> {
      timeline.stop();
      generation = 0;
      gridState.clear();
      gridView.refresh();
      updateCounters();
      playButton.setDisable(false);
      pauseButton.setDisable(true);
      stepButton.setDisable(false);
    });

    Label generationLabel = new Label("Generacion:");
    generationValue = new Label("0");
    Label populationLabel = new Label("Poblacion:");
    populationValue = new Label("0");
    updateCounters();

    HBox controls = new HBox(stepButton, playButton, pauseButton, resetButton, generationLabel, generationValue, populationLabel, populationValue);
    controls.setPadding(new Insets(16));
    controls.setSpacing(12);
    return controls;
  }

  private void advanceAndRefresh() {
    gridState.advance();
    generation++;
    gridView.refresh();
    updateCounters();
  }

  private void updateCounters() {
    if (generationValue != null) {
      generationValue.setText(Integer.toString(generation));
    }
    if (populationValue != null) {
      populationValue.setText(Integer.toString(gridState.countAliveCells()));
    }
  }
}
