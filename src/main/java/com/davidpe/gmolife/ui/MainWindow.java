package com.davidpe.gmolife.ui;

import com.davidpe.gmolife.pattern.PatternIO;
import com.davidpe.gmolife.ui.grid.EditableGridView;
import com.davidpe.gmolife.ui.grid.GridState;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

public final class MainWindow {

  private static final int GRID_ROWS = 25;
  private static final int GRID_COLUMNS = 25;
  private static final double DEFAULT_CELL_SIZE = 24;
  private static final double MIN_CELL_SIZE = 12;
  private static final double MAX_CELL_SIZE = 40;
  private static final int TICK_MILLIS = 300;
  private static final int MIN_TICK_MILLIS = 50;
  private static final int MAX_TICK_MILLIS = 1000;
  private static final int RANDOM_ATTEMPTS = 200;
  private static final int RANDOM_VALIDATE_STEPS = 5;
  private static final double RANDOM_MIN_PROBABILITY = 0.20;
  private static final double RANDOM_MAX_PROBABILITY = 0.45;
  private static final int POPULATION_HISTORY = 100;

  private final GridState gridState = new GridState(GRID_ROWS, GRID_COLUMNS);
  private final Random random = new Random();
  private EditableGridView gridView;
  private Timeline timeline;
  private int generation;
  private Label generationValue;
  private Label populationValue;
  private Label speedValue;
  private XYChart.Series<Number, Number> populationSeries;
  private Stage stage;

  public void show(Stage stage) {
    this.stage = stage;
    BorderPane root = new BorderPane();
    root.setRight(buildPopulationPanel());
    root.setCenter(buildGrid());
    root.setTop(buildControls());

    Scene scene = new Scene(root, 960, 640);
    stage.setTitle("Game of Life");
    stage.setScene(scene);
    stage.show();
  }

  private StackPane buildGrid() {
    gridView = new EditableGridView(gridState, DEFAULT_CELL_SIZE);
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
    Button saveButton = new Button("Save");
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


    Label zoomLabel = new Label("Zoom:");
    Slider zoomSlider = new Slider(MIN_CELL_SIZE, MAX_CELL_SIZE, DEFAULT_CELL_SIZE);
    zoomSlider.setShowTickLabels(true);
    zoomSlider.setShowTickMarks(true);
    zoomSlider.setMajorTickUnit(4);
    zoomSlider.setMinorTickCount(3);
    zoomSlider.setBlockIncrement(1);
    Label zoomValue = new Label();
    applyZoom(zoomSlider.getValue(), zoomValue);

    zoomSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
      applyZoom(newValue.doubleValue(), zoomValue);
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
      resetPopulationSeries();
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

    saveButton.setOnAction(event -> {
      savePattern();
    });

    Label generationLabel = new Label("Generacion:");
    generationValue = new Label("0");
    Label populationLabel = new Label("Poblacion:");
    populationValue = new Label("0");
    updateCounters();

    HBox controls = new HBox(stepButton, playButton, pauseButton, resetButton, randomizeButton, saveButton, speedLabel, speedSlider, speedValue, zoomLabel, zoomSlider, zoomValue, generationLabel, generationValue, populationLabel, populationValue);
    controls.setPadding(new Insets(16));
    controls.setSpacing(12);
    return controls;
  }

  private VBox buildPopulationPanel() {
    NumberAxis xAxis = new NumberAxis();
    xAxis.setLabel("Generacion");
    xAxis.setForceZeroInRange(false);
    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Poblacion");

    LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
    chart.setLegendVisible(false);
    chart.setCreateSymbols(false);
    chart.setAnimated(false);
    chart.setMinWidth(320);

    populationSeries = new XYChart.Series<>();
    chart.getData().add(populationSeries);

    VBox panel = new VBox(chart);
    panel.setPadding(new Insets(16));
    return panel;
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
      int alive = gridState.countAliveCells();
      populationValue.setText(Integer.toString(alive));
      updatePopulationSeries(generation, alive);
    }
  }

  private void updatePopulationSeries(int currentGeneration, int alive) {
    if (populationSeries == null) {
      return;
    }
    populationSeries.getData().add(new XYChart.Data<>(currentGeneration, alive));
    int size = populationSeries.getData().size();
    if (size > POPULATION_HISTORY) {
      populationSeries.getData().remove(0, size - POPULATION_HISTORY);
    }
  }

  private void resetPopulationSeries() {
    if (populationSeries != null) {
      populationSeries.getData().clear();
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


  private void applyZoom(double cellSize, Label zoomValue) {
    if (zoomValue != null) {
      zoomValue.setText(String.format("%.0f px", cellSize));
    }
    if (gridView != null && cellSize > 0) {
      gridView.setCellSize(cellSize);
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

  private void savePattern() {
    if (stage == null) {
      return;
    }
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Guardar patron");
    chooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Game of Life Pattern (*.gol)", "*.gol"),
        new FileChooser.ExtensionFilter("Text file (*.txt)", "*.txt"),
        new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
    var file = chooser.showSaveDialog(stage);
    if (file == null) {
      return;
    }
    Path filePath = file.toPath();
    try {
      PatternIO.write(filePath, snapshotGrid());
      showInfo("Patron guardado en " + filePath.getFileName() + ".");
    } catch (IOException | IllegalArgumentException ex) {
      showError("No se pudo guardar el patron: " + ex.getMessage());
    }
  }

  private boolean[][] snapshotGrid() {
    boolean[][] snapshot = new boolean[GRID_ROWS][GRID_COLUMNS];
    for (int row = 0; row < GRID_ROWS; row++) {
      for (int column = 0; column < GRID_COLUMNS; column++) {
        snapshot[row][column] = gridState.isAlive(row, column);
      }
    }
    return snapshot;
  }

  private void showInfo(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Game of Life");
    alert.setHeaderText("Operacion completada");
    alert.setContentText(message);
    alert.showAndWait();
  }

  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Game of Life");
    alert.setHeaderText("Operacion fallida");
    alert.setContentText(message);
    alert.showAndWait();
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
