package com.davidpe.gmolife.ui;

import com.davidpe.gmolife.ui.grid.EditableGridView;
import com.davidpe.gmolife.ui.grid.GridState;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Random;

public final class MainWindow {

  private static final int GRID_ROWS = 25;
  private static final int GRID_COLUMNS = 25;
  private static final double CELL_SIZE = 24;
  private static final int TICK_MILLIS = 300;
  private static final int MIN_TICK_MILLIS = 50;
  private static final int MAX_TICK_MILLIS = 1000;
  private static final int RANDOM_ATTEMPTS = 200;
  private static final int RANDOM_VALIDATE_STEPS = 5;
  private static final double RANDOM_MIN_PROBABILITY = 0.20;
  private static final double RANDOM_MAX_PROBABILITY = 0.45;

  private final GridState gridState = new GridState(GRID_ROWS, GRID_COLUMNS);
  private final Random random = new Random();
  private EditableGridView gridView;
  private Timeline timeline;
  private int generation;
  private Label generationValue;
  private Label populationValue;
  private Label speedValue;

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
    Button randomizeButton = new Button("Randomize");
    pauseButton.setDisable(true);

    timeline = new Timeline(new KeyFrame(Duration.millis(TICK_MILLIS), event -> {
      advanceAndRefresh();
    }));
    timeline.setCycleCount(Timeline.INDEFINITE);

    Label speedLabel = new Label("Velocidad:");
    Slider speedSlider = new Slider(MIN_TICK_MILLIS, MAX_TICK_MILLIS, TICK_MILLIS);
    speedSlider.setShowTickLabels(true);
    speedSlider.setShowTickMarks(true);
    speedSlider.setMajorTickUnit(250);
    speedSlider.setMinorTickCount(4);
    speedSlider.setBlockIncrement(50);
    speedValue = new Label();
    applySpeed(speedSlider.getValue());

    speedSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
      applySpeed(newValue.doubleValue());
    });

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

    randomizeButton.setOnAction(event -> {
      timeline.stop();
      randomizeGrid();
      playButton.setDisable(false);
      pauseButton.setDisable(true);
      stepButton.setDisable(false);
    });

    Label generationLabel = new Label("Generacion:");
    generationValue = new Label("0");
    Label populationLabel = new Label("Poblacion:");
    populationValue = new Label("0");
    updateCounters();

    HBox controls = new HBox(stepButton, playButton, pauseButton, resetButton, randomizeButton, speedLabel, speedSlider, speedValue, generationLabel, generationValue, populationLabel, populationValue);
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

  private void applySpeed(double tickMillis) {
    if (speedValue != null) {
      speedValue.setText(String.format("%.0f ms", tickMillis));
    }
    if (timeline != null && tickMillis > 0) {
      double rate = TICK_MILLIS / tickMillis;
      timeline.setRate(rate);
    }
  }

  private void randomizeGrid() {
    boolean[][] pattern = null;
    for (int attempt = 0; attempt < RANDOM_ATTEMPTS; attempt++) {
      double probability = RANDOM_MIN_PROBABILITY
          + random.nextDouble() * (RANDOM_MAX_PROBABILITY - RANDOM_MIN_PROBABILITY);
      boolean[][] candidate = buildRandomPattern(probability);
      if (isTrivial(candidate)) {
        continue;
      }
      if (!evolvesWithinSteps(candidate, RANDOM_VALIDATE_STEPS)) {
        continue;
      }
      pattern = candidate;
      break;
    }

    if (pattern == null) {
      pattern = buildRandomPattern((RANDOM_MIN_PROBABILITY + RANDOM_MAX_PROBABILITY) / 2.0);
    }

    gridState.load(pattern);
    generation = 0;
    gridView.refresh();
    updateCounters();
  }

  private boolean[][] buildRandomPattern(double probability) {
    boolean[][] pattern = new boolean[GRID_ROWS][GRID_COLUMNS];
    for (int row = 0; row < GRID_ROWS; row++) {
      for (int column = 0; column < GRID_COLUMNS; column++) {
        pattern[row][column] = random.nextDouble() < probability;
      }
    }
    return pattern;
  }

  private boolean isTrivial(boolean[][] pattern) {
    int alive = 0;
    int total = GRID_ROWS * GRID_COLUMNS;
    for (int row = 0; row < GRID_ROWS; row++) {
      for (int column = 0; column < GRID_COLUMNS; column++) {
        if (pattern[row][column]) {
          alive++;
        }
      }
    }
    return alive == 0 || alive == total;
  }

  private boolean evolvesWithinSteps(boolean[][] pattern, int steps) {
    GridState simulation = new GridState(GRID_ROWS, GRID_COLUMNS);
    simulation.load(pattern);
    for (int step = 0; step < steps; step++) {
      simulation.advance();
    }
    for (int row = 0; row < GRID_ROWS; row++) {
      for (int column = 0; column < GRID_COLUMNS; column++) {
        if (simulation.isAlive(row, column) != pattern[row][column]) {
          return true;
        }
      }
    }
    return false;
  }
}
