package com.davidpe.gmolife.ui;

import com.davidpe.gmolife.pattern.PatternData;
import com.davidpe.gmolife.pattern.PatternIO;
import com.davidpe.gmolife.ui.grid.EditableGridView;
import com.davidpe.gmolife.ui.grid.GridState;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Pos;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

public final class MainWindow {

  private static final int GRID_ROWS = 25;
  private static final int GRID_COLUMNS = 25;
  private static final double DEFAULT_CELL_SIZE = 24;
  private static final double MIN_CELL_SIZE = 12;
  private static final double MAX_CELL_SIZE = 40;
  private static final double MIN_WINDOW_WIDTH = 900;
  private static final double MIN_WINDOW_HEIGHT = 640;
  private static final int TICK_MILLIS = 300;
  private static final int MIN_TICK_MILLIS = 50;
  private static final int MAX_TICK_MILLIS = 1000;
  private static final int RANDOM_ATTEMPTS = 200;
  private static final int RANDOM_VALIDATE_STEPS = 5;
  private static final double RANDOM_MIN_PROBABILITY = 0.20;
  private static final double RANDOM_MAX_PROBABILITY = 0.45;
  private static final int POPULATION_HISTORY = 100;
  private static final List<String> BASIC_PATTERNS = List.of("Glider", "Blinker", "Toad", "Beacon");

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
  private Button stepButton;
  private Button playButton;
  private Button pauseButton;
  private Button resetButton;
  private StackPane gridContainer;
  private double desiredCellSize = DEFAULT_CELL_SIZE;
  private Label zoomValue;

  public void show(Stage stage) {
    this.stage = stage;
    BorderPane root = new BorderPane();
    SplitPane content = new SplitPane();
    content.getItems().addAll(buildGrid(), buildPopulationPanel());
    content.setDividerPositions(0.68);
    root.setCenter(content);
    root.setTop(buildControls());

    Scene scene = new Scene(root, 960, 640);
    setupKeyboardShortcuts(scene);
    stage.setTitle("Game of Life");
    stage.setScene(scene);
    stage.setMinWidth(MIN_WINDOW_WIDTH);
    stage.setMinHeight(MIN_WINDOW_HEIGHT);
    stage.show();
  }

  private StackPane buildGrid() {
    gridView = new EditableGridView(gridState, DEFAULT_CELL_SIZE);
    gridView.setPadding(new Insets(24));

    gridContainer = new StackPane(gridView);
    gridContainer.setPadding(new Insets(16));
    gridContainer.widthProperty().addListener((obs, oldValue, newValue) -> {
      updateGridSizing();
    });
    gridContainer.heightProperty().addListener((obs, oldValue, newValue) -> {
      updateGridSizing();
    });
    updateGridSizing();
    return gridContainer;
  }

  private FlowPane buildControls() {
    stepButton = new Button("Step");
    stepButton.setOnAction(event -> {
      handleStep();
    });
    playButton = new Button("Play");
    pauseButton = new Button("Pause");
    resetButton = new Button("Reset");
    Button randomizeButton = new Button("Randomize");
    Button saveButton = new Button("Save");
    Button loadButton = new Button("Load");
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
    speedSlider.setPrefWidth(160);
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
    zoomSlider.setPrefWidth(160);
    zoomValue = new Label();
    applyZoom(zoomSlider.getValue());

    zoomSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
      applyZoom(newValue.doubleValue());
    });

    playButton.setOnAction(event -> {
      startPlay();
    });

    pauseButton.setOnAction(event -> {
      pausePlay();
    });

    resetButton.setOnAction(event -> {
      resetSimulation();
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

    loadButton.setOnAction(event -> {
      loadPattern();
    });

    Label patternLabel = new Label("Patrones:");
    ComboBox<String> patternPicker = new ComboBox<>();
    patternPicker.getItems().addAll(BASIC_PATTERNS);
    patternPicker.setPromptText("Selecciona");
    patternPicker.setOnAction(event -> {
      String selection = patternPicker.getValue();
      if (selection != null && !selection.isBlank()) {
        loadPatternCentered(patternFor(selection));
      }
    });

    Label generationLabel = new Label("Generacion:");
    generationValue = new Label("0");
    Label populationLabel = new Label("Poblacion:");
    populationValue = new Label("0");
    updateCounters();

    FlowPane controls = new FlowPane(stepButton, playButton, pauseButton, resetButton, randomizeButton, saveButton, loadButton, patternLabel, patternPicker, speedLabel, speedSlider, speedValue, zoomLabel, zoomSlider, zoomValue, generationLabel, generationValue, populationLabel, populationValue);
    controls.setPadding(new Insets(16));
    controls.setHgap(12);
    controls.setVgap(8);
    controls.setAlignment(Pos.CENTER_LEFT);
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
    chart.setMinWidth(280);
    chart.setPrefWidth(320);

    populationSeries = new XYChart.Series<>();
    chart.getData().add(populationSeries);

    VBox panel = new VBox(chart);
    panel.setPadding(new Insets(16));
    panel.setMinWidth(280);
    VBox.setVgrow(chart, Priority.ALWAYS);
    return panel;
  }

  private void advanceAndRefresh() {
    gridState.advance();
    generation++;
    gridView.refresh();
    updateCounters();
  }

  private void handleStep() {
    if (stepButton != null && !stepButton.isDisabled()) {
      advanceAndRefresh();
    }
  }

  private void startPlay() {
    if (timeline != null) {
      timeline.play();
    }
    if (playButton != null) {
      playButton.setDisable(true);
    }
    if (pauseButton != null) {
      pauseButton.setDisable(false);
    }
    if (stepButton != null) {
      stepButton.setDisable(true);
    }
  }

  private void pausePlay() {
    if (timeline != null) {
      timeline.pause();
    }
    if (playButton != null) {
      playButton.setDisable(false);
    }
    if (pauseButton != null) {
      pauseButton.setDisable(true);
    }
    if (stepButton != null) {
      stepButton.setDisable(false);
    }
  }

  private void resetSimulation() {
    if (timeline != null) {
      timeline.stop();
    }
    generation = 0;
    gridState.clear();
    gridView.refresh();
    resetPopulationSeries();
    updateCounters();
    if (playButton != null) {
      playButton.setDisable(false);
    }
    if (pauseButton != null) {
      pauseButton.setDisable(true);
    }
    if (stepButton != null) {
      stepButton.setDisable(false);
    }
  }

  private void setupKeyboardShortcuts(Scene scene) {
    scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
      if (event.getCode() == KeyCode.SPACE) {
        togglePlayPause();
        event.consume();
      } else if (event.getCode() == KeyCode.N) {
        handleStep();
        event.consume();
      } else if (event.getCode() == KeyCode.R) {
        resetSimulation();
        event.consume();
      }
    });
  }

  private void togglePlayPause() {
    if (timeline == null) {
      return;
    }
    if (timeline.getStatus() == Animation.Status.RUNNING) {
      pausePlay();
    } else {
      startPlay();
    }
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


  private void applyZoom(double cellSize) {
    desiredCellSize = cellSize;
    updateGridSizing();
  }

  private void updateGridSizing() {
    if (gridView == null || gridContainer == null) {
      return;
    }
    double availableWidth = gridContainer.getWidth();
    double availableHeight = gridContainer.getHeight();
    if (availableWidth <= 0 || availableHeight <= 0) {
      return;
    }
    Insets containerPadding = gridContainer.getPadding();
    if (containerPadding != null) {
      availableWidth -= containerPadding.getLeft() + containerPadding.getRight();
      availableHeight -= containerPadding.getTop() + containerPadding.getBottom();
    }
    Insets gridPadding = gridView.getPadding();
    if (gridPadding != null) {
      availableWidth -= gridPadding.getLeft() + gridPadding.getRight();
      availableHeight -= gridPadding.getTop() + gridPadding.getBottom();
    }
    if (availableWidth <= 0 || availableHeight <= 0) {
      return;
    }
    double fitByWidth = availableWidth / GRID_COLUMNS;
    double fitByHeight = availableHeight / GRID_ROWS;
    double fitCellSize = Math.min(fitByWidth, fitByHeight);
    double clamped = Math.max(MIN_CELL_SIZE, Math.min(desiredCellSize, fitCellSize));
    gridView.setCellSize(clamped);
    if (zoomValue != null) {
      zoomValue.setText(String.format("%.0f px", clamped));
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

  private void loadPattern() {
    if (stage == null) {
      return;
    }
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Cargar patron");
    chooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Game of Life Pattern (*.gol)", "*.gol"),
        new FileChooser.ExtensionFilter("Text file (*.txt)", "*.txt"),
        new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
    var file = chooser.showOpenDialog(stage);
    if (file == null) {
      return;
    }
    Path filePath = file.toPath();
    try {
      PatternData data = PatternIO.read(filePath);
      if (data.rows() != GRID_ROWS || data.columns() != GRID_COLUMNS) {
        showError("El patron no coincide con el tamano de la cuadricula.");
        return;
      }
      gridState.load(data.cells());
      generation = 0;
      gridView.refresh();
      resetPopulationSeries();
      updateCounters();
      showInfo("Patron cargado desde " + filePath.getFileName() + ".");
    } catch (IOException | IllegalArgumentException ex) {
      showError("No se pudo cargar el patron: " + ex.getMessage());
    }
  }

  private boolean[][] patternFor(String name) {
    return switch (name) {
      case "Glider" -> new boolean[][]{
          {false, true, false},
          {false, false, true},
          {true, true, true}
      };
      case "Blinker" -> new boolean[][]{
          {true},
          {true},
          {true}
      };
      case "Toad" -> new boolean[][]{
          {false, true, true, true},
          {true, true, true, false}
      };
      case "Beacon" -> new boolean[][]{
          {true, true, false, false},
          {true, true, false, false},
          {false, false, true, true},
          {false, false, true, true}
      };
      default -> new boolean[][]{{true}};
    };
  }

  private void loadPatternCentered(boolean[][] pattern) {
    if (pattern == null || pattern.length == 0 || pattern[0].length == 0) {
      showError("El patron seleccionado es invalido.");
      return;
    }
    if (pattern.length > GRID_ROWS || pattern[0].length > GRID_COLUMNS) {
      showError("El patron excede el tamano de la cuadricula.");
      return;
    }
    gridState.clear();
    int rowOffset = (GRID_ROWS - pattern.length) / 2;
    int columnOffset = (GRID_COLUMNS - pattern[0].length) / 2;
    for (int row = 0; row < pattern.length; row++) {
      for (int column = 0; column < pattern[row].length; column++) {
        if (pattern[row][column]) {
          gridState.setCell(row + rowOffset, column + columnOffset, true);
        }
      }
    }
    generation = 0;
    gridView.refresh();
    resetPopulationSeries();
    updateCounters();
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
