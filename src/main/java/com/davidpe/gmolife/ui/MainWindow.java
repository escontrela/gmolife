package com.davidpe.gmolife.ui;

import com.davidpe.gmolife.engine.SimulationEngine;
import com.davidpe.gmolife.pattern.PatternData;
import com.davidpe.gmolife.pattern.PatternIO;
import com.davidpe.gmolife.ui.grid.EditableGridView;
import com.davidpe.gmolife.ui.grid.GridState;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;

public final class MainWindow {

  private static final int DEFAULT_GRID_ROWS = 25;
  private static final int DEFAULT_GRID_COLUMNS = 25;
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
  private static final int AI_POPULATION_SIZE = 40;
  private static final int AI_GENERATIONS = 24;
  private static final int AI_EVALUATION_STEPS = 12;
  private static final double AI_MUTATION_RATE = 0.05;
  private static final double AI_CROSSOVER_RATE = 0.6;
  private static final double AI_PREVIEW_CELL_SIZE = 6;
  private static final double AI_AUTO_APPLY_THRESHOLD = 25.0;
  private static final SimulationEngine.GeneticObjective AI_DEFAULT_OBJECTIVE =
      SimulationEngine.GeneticObjective.HIGH_POPULATION;
  private static final List<GridSize> GRID_SIZES =
      List.of(
          new GridSize("25x25", 25, 25),
          new GridSize("50x30", 30, 50),
          new GridSize("100x60", 60, 100),
          new GridSize("150x90", 90, 150));

  private GridState gridState = new GridState(DEFAULT_GRID_ROWS, DEFAULT_GRID_COLUMNS);
  private GridState aiPreviewState = new GridState(DEFAULT_GRID_ROWS, DEFAULT_GRID_COLUMNS);
  private final SimulationEngine simulationEngine = new SimulationEngine();
  private final Random random = new Random();
  private EditableGridView gridView;
  private Timeline timeline;
  private int generation;
  private Label generationValue;
  private Label populationValue;
  private Label speedValue;
  private Label tpsValue;
  private long tpsWindowStartNanos;
  private int tpsTickCount;
  private Label minPopulationValue;
  private Label maxPopulationValue;
  private Label avgPopulationValue;
  private int minPopulation;
  private int maxPopulation;
  private long totalPopulation;
  private int populationSamples;
  private XYChart.Series<Number, Number> populationSeries;
  private XYChart.Series<Number, Number> birthsSeries;
  private XYChart.Series<Number, Number> deathsSeries;
  private Stage stage;
  private Button stepButton;
  private Button playButton;
  private Button pauseButton;
  private Button resetButton;
  private StackPane gridContainer;
  private double desiredCellSize = DEFAULT_CELL_SIZE;
  private Label zoomValue;
  private Label playStateValue;
  private CheckBox toroidalToggle;
  private boolean toroidalEnabled;
  private CheckBox gridLinesToggle;
  private boolean gridLinesVisible = true;
  private Button aiRunButton;
  private Button aiCancelButton;
  private Button aiApplyButton;
  private Label aiRunningIndicator;
  private Label aiFitnessValue;
  private Label aiIterationValue;
  private Label aiStatusValue;
  private Label aiObjectiveValue;
  private Label aiSeedValue;
  private ListView<String> aiHistoryList;
  private Label statusMessage;
  private Label cursorPositionValue;
  private EditableGridView aiPreviewView;
  private StackPane aiPreviewContainer;
  private TextField aiPopulationField;
  private TextField aiGenerationsField;
  private TextField aiMutationField;
  private TextField aiSeedField;
  private TextField aiAutoApplyThresholdField;
  private ComboBox<SimulationEngine.GeneticObjective> aiObjectivePicker;
  private final List<String> aiHistoryEntries = new ArrayList<>(5);
  private SimulationEngine.GeneticObjective lastAiObjectiveUsed = AI_DEFAULT_OBJECTIVE;
  private boolean lastAiAutoApplyEnabled;
  private double lastAiAutoApplyThreshold = AI_AUTO_APPLY_THRESHOLD;
  private CheckBox aiAutoApplyToggle;
  private CompletableFuture<SimulationEngine.GeneticSearchResult> aiSearch;
  private boolean[][] aiResultPattern;
  private SimulationEngine.CancellationToken aiCancellationToken;

  public void show(Stage stage) {
    this.stage = stage;
    BorderPane root = new BorderPane();
    SplitPane content = new SplitPane();
    content.getItems().addAll(buildGrid(), buildPopulationPanel());
    content.setDividerPositions(0.68);
    root.setCenter(content);
    root.setTop(buildControls());
    root.setBottom(buildStatusBar());

    Scene scene = new Scene(root, 960, 640);
    setupKeyboardShortcuts(scene);
    stage.setTitle("Game of Life");
    stage.setScene(scene);
    stage.setMinWidth(MIN_WINDOW_WIDTH);
    stage.setMinHeight(MIN_WINDOW_HEIGHT);
    stage.setOnCloseRequest(
        event -> {
          if (!confirmExitIfNeeded()) {
            event.consume();
            return;
          }
          simulationEngine.shutdown();
        });
    stage.show();
  }

  private StackPane buildGrid() {
    gridView = new EditableGridView(gridState, DEFAULT_CELL_SIZE);
    configureGridView(gridView);

    gridContainer = new StackPane(gridView);
    gridContainer.setPadding(new Insets(16));
    gridContainer
        .widthProperty()
        .addListener(
            (obs, oldValue, newValue) -> {
              updateGridSizing();
            });
    gridContainer
        .heightProperty()
        .addListener(
            (obs, oldValue, newValue) -> {
              updateGridSizing();
            });
    updateGridSizing();
    return gridContainer;
  }

  private void configureGridView(EditableGridView view) {
    view.setOnEdit(this::pauseIfEditingDuringPlay);
    view.setPadding(new Insets(24));
    view.setCellGridLinesVisible(gridLinesVisible);
    view.setOnHover(this::updateCursorPosition);
    view.setOnHoverExit(this::clearCursorPosition);
  }

  private FlowPane buildControls() {
    stepButton = new Button("Step");
    stepButton.setOnAction(
        event -> {
          handleStep();
        });
    playButton = new Button("Play");
    pauseButton = new Button("Pause");
    resetButton = new Button("Reset");
    Button randomizeButton = new Button("Randomize");
    Button centerButton = new Button("Centrar");
    Button saveButton = new Button("Save");
    Button loadButton = new Button("Load");
    Button helpButton = new Button("Ayuda");
    Button exportPngButton = new Button("Exportar PNG");
    Button copyPatternButton = new Button("Copiar patron");
    Button pastePatternButton = new Button("Pegar patron");
    pauseButton.setDisable(true);
    applyPlayPauseStyles(playButton, "#2e7d32", "#388e3c", "#1b5e20");
    applyPlayPauseStyles(pauseButton, "#c62828", "#d32f2f", "#b71c1c");
    Label playStateLabel = new Label("Simulacion:");
    playStateValue = new Label("Pause");
    HBox playStateRow = new HBox(playStateLabel, playStateValue);
    playStateRow.setSpacing(6);

    timeline =
        new Timeline(
            new KeyFrame(
                Duration.millis(TICK_MILLIS),
                event -> {
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

    speedSlider
        .valueProperty()
        .addListener(
            (obs, oldValue, newValue) -> {
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

    zoomSlider
        .valueProperty()
        .addListener(
            (obs, oldValue, newValue) -> {
              applyZoom(newValue.doubleValue());
            });

    Label gridSizeLabel = new Label("Tamano:");
    ComboBox<GridSize> gridSizePicker = new ComboBox<>();
    gridSizePicker.getItems().addAll(GRID_SIZES);
    gridSizePicker.setValue(GRID_SIZES.get(0));
    gridSizePicker.setOnAction(
        event -> {
          GridSize size = gridSizePicker.getValue();
          if (size != null) {
            applyGridSize(size);
          }
        });

    toroidalToggle = new CheckBox("Toroidal");
    toroidalToggle.setSelected(false);
    toroidalToggle.setOnAction(
        event -> {
          toroidalEnabled = toroidalToggle.isSelected();
          gridState.setToroidal(toroidalEnabled);
        });

    gridLinesToggle = new CheckBox("Lineas de cuadricula");
    gridLinesToggle.setSelected(true);
    gridLinesToggle.setOnAction(
        event -> {
          gridLinesVisible = gridLinesToggle.isSelected();
          if (gridView != null) {
            gridView.setCellGridLinesVisible(gridLinesVisible);
          }
        });

    playButton.setOnAction(
        event -> {
          startPlay();
        });

    pauseButton.setOnAction(
        event -> {
          pausePlay();
        });

    resetButton.setOnAction(
        event -> {
          resetSimulation();
        });

    randomizeButton.setOnAction(
        event -> {
          timeline.stop();
          randomizeGrid();
          playButton.setDisable(false);
          pauseButton.setDisable(true);
          stepButton.setDisable(false);
        });

    centerButton.setOnAction(
        event -> {
          centerPattern();
        });

    saveButton.setOnAction(
        event -> {
          savePattern();
        });

    loadButton.setOnAction(
        event -> {
          loadPattern();
        });

    helpButton.setOnAction(
        event -> {
          showShortcutsHelp();
        });

    exportPngButton.setOnAction(
        event -> {
          exportGridPng();
        });

    copyPatternButton.setOnAction(
        event -> {
          copyPatternToClipboard();
        });

    pastePatternButton.setOnAction(
        event -> {
          pastePatternFromClipboard();
        });

    Label patternLabel = new Label("Patrones:");
    ComboBox<String> patternPicker = new ComboBox<>();
    patternPicker.getItems().addAll(BASIC_PATTERNS);
    patternPicker.setPromptText("Selecciona");
    patternPicker.setOnAction(
        event -> {
          String selection = patternPicker.getValue();
          if (selection != null && !selection.isBlank()) {
            loadPatternCentered(patternFor(selection));
          }
        });

    Label generationLabel = new Label("Generacion:");
    generationValue = new Label("0");
    Label populationLabel = new Label("Poblacion:");
    populationValue = new Label("0");
    Label minPopulationLabel = new Label("Min:");
    minPopulationValue = new Label("0");
    Label maxPopulationLabel = new Label("Max:");
    maxPopulationValue = new Label("0");
    Label avgPopulationLabel = new Label("Promedio:");
    avgPopulationValue = new Label("0");
    Label tpsLabel = new Label("TPS:");
    tpsValue = new Label("0");
    updateCounters();

    FlowPane controls =
        new FlowPane(
            stepButton,
            playButton,
            pauseButton,
            resetButton,
            randomizeButton,
            centerButton,
            playStateRow,
            saveButton,
            loadButton,
            helpButton,
            exportPngButton,
            copyPatternButton,
            pastePatternButton,
            patternLabel,
            patternPicker,
            speedLabel,
            speedSlider,
            speedValue,
            zoomLabel,
            zoomSlider,
            zoomValue,
            gridSizeLabel,
            gridSizePicker,
            toroidalToggle,
            gridLinesToggle,
            generationLabel,
            generationValue,
            populationLabel,
            populationValue,
            minPopulationLabel,
            minPopulationValue,
            maxPopulationLabel,
            maxPopulationValue,
            avgPopulationLabel,
            avgPopulationValue,
            tpsLabel,
            tpsValue);
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
    chart.setLegendVisible(true);
    chart.setCreateSymbols(false);
    chart.setAnimated(false);
    chart.setMinWidth(280);
    chart.setPrefWidth(320);

    populationSeries = new XYChart.Series<>();
    populationSeries.setName("Poblacion");
    birthsSeries = new XYChart.Series<>();
    birthsSeries.setName("Nacimientos");
    deathsSeries = new XYChart.Series<>();
    deathsSeries.setName("Muertes");
    chart.getData().add(populationSeries);
    chart.getData().addAll(birthsSeries, deathsSeries);

    Button exportCsvButton = new Button("Exportar CSV");
    exportCsvButton.setOnAction(event -> exportPopulationCsv());

    VBox panel = new VBox(chart, exportCsvButton, buildAiPanel());
    panel.setPadding(new Insets(16));
    panel.setMinWidth(280);
    VBox.setVgrow(chart, Priority.ALWAYS);
    return panel;
  }

  private HBox buildStatusBar() {
    Label statusLabel = new Label("Estado:");
    statusMessage = new Label("Listo");
    Label cursorLabel = new Label("Cursor:");
    cursorPositionValue = new Label("N/A");
    HBox statusBar = new HBox(statusLabel, statusMessage, cursorLabel, cursorPositionValue);
    statusBar.setSpacing(8);
    statusBar.setPadding(new Insets(8, 16, 8, 16));
    statusBar.setAlignment(Pos.CENTER_LEFT);
    HBox.setHgrow(statusMessage, Priority.ALWAYS);
    return statusBar;
  }

  private VBox buildAiPanel() {
    Label title = new Label("IA");
    title.setStyle("-fx-font-weight: bold;");

    aiRunButton = new Button("Buscar");
    aiRunButton.setOnAction(event -> runAiSearch());

    aiCancelButton = new Button("Cancelar IA");
    aiCancelButton.setDisable(true);
    aiCancelButton.setOnAction(event -> cancelAiSearch());

    aiApplyButton = new Button("Aplicar IA");
    aiApplyButton.setDisable(true);
    aiApplyButton.setOnAction(event -> applyAiPattern());

    aiRunningIndicator = new Label("IA en ejecucion");
    aiRunningIndicator.setStyle(
        "-fx-background-color: #f7d07a; "
            + "-fx-text-fill: #3b2f00; "
            + "-fx-padding: 4 8; "
            + "-fx-background-radius: 6; "
            + "-fx-font-size: 11;");
    aiRunningIndicator.setVisible(false);
    aiRunningIndicator.setManaged(false);

    Label populationLabel = new Label("Poblacion:");
    aiPopulationField = new TextField(Integer.toString(AI_POPULATION_SIZE));
    aiPopulationField.setPrefColumnCount(6);
    HBox populationRow = new HBox(populationLabel, aiPopulationField);
    populationRow.setSpacing(6);

    Label generationsLabel = new Label("Generaciones:");
    aiGenerationsField = new TextField(Integer.toString(AI_GENERATIONS));
    aiGenerationsField.setPrefColumnCount(6);
    HBox generationsRow = new HBox(generationsLabel, aiGenerationsField);
    generationsRow.setSpacing(6);

    Label mutationLabel = new Label("Mutacion (0-1):");
    aiMutationField = new TextField(String.format("%.2f", AI_MUTATION_RATE));
    aiMutationField.setPrefColumnCount(6);
    HBox mutationRow = new HBox(mutationLabel, aiMutationField);
    mutationRow.setSpacing(6);

    Label seedLabel = new Label("Semilla (opcional):");
    aiSeedField = new TextField();
    aiSeedField.setPrefColumnCount(10);
    HBox seedRow = new HBox(seedLabel, aiSeedField);
    seedRow.setSpacing(6);

    Label objectiveLabel = new Label("Objetivo:");
    aiObjectivePicker = new ComboBox<>();
    aiObjectivePicker.getItems().addAll(SimulationEngine.GeneticObjective.values());
    aiObjectivePicker.setValue(AI_DEFAULT_OBJECTIVE);
    aiObjectivePicker.setConverter(new AiObjectiveConverter());
    HBox objectiveRow = new HBox(objectiveLabel, aiObjectivePicker);
    objectiveRow.setSpacing(6);

    aiAutoApplyToggle = new CheckBox("Auto-aplicar");
    aiAutoApplyToggle.setSelected(false);
    Label thresholdLabel = new Label("Umbral fitness:");
    aiAutoApplyThresholdField = new TextField(String.format("%.2f", AI_AUTO_APPLY_THRESHOLD));
    aiAutoApplyThresholdField.setPrefColumnCount(6);
    HBox autoApplyRow = new HBox(aiAutoApplyToggle, thresholdLabel, aiAutoApplyThresholdField);
    autoApplyRow.setSpacing(6);

    Label iterationLabel = new Label("Iteracion:");
    aiIterationValue = new Label("0");
    HBox iterationRow = new HBox(iterationLabel, aiIterationValue);
    iterationRow.setSpacing(6);

    Label statusLabel = new Label("Estado:");
    aiStatusValue = new Label("Listo");
    HBox statusRow = new HBox(statusLabel, aiStatusValue);
    statusRow.setSpacing(6);

    Label fitnessLabel = new Label("Fitness:");
    aiFitnessValue = new Label("-");
    HBox fitnessRow = new HBox(fitnessLabel, aiFitnessValue);
    fitnessRow.setSpacing(6);

    Label objectiveValueLabel = new Label("Objetivo activo:");
    aiObjectiveValue = new Label(formatObjective(AI_DEFAULT_OBJECTIVE));
    HBox objectiveValueRow = new HBox(objectiveValueLabel, aiObjectiveValue);
    objectiveValueRow.setSpacing(6);

    Label seedValueLabel = new Label("Semilla usada:");
    aiSeedValue = new Label("-");
    HBox seedValueRow = new HBox(seedValueLabel, aiSeedValue);
    seedValueRow.setSpacing(6);

    Label historyLabel = new Label("Historial IA:");
    aiHistoryList = new ListView<>();
    aiHistoryList.setPrefHeight(140);

    Label previewLabel = new Label("Vista previa:");
    aiPreviewView = new EditableGridView(aiPreviewState, AI_PREVIEW_CELL_SIZE);
    aiPreviewView.setMouseTransparent(true);
    aiPreviewContainer = new StackPane(aiPreviewView);
    aiPreviewContainer.setPadding(new Insets(8));
    aiPreviewContainer.setPrefSize(220, 220);

    VBox panel =
        new VBox(
            title,
            aiRunningIndicator,
            aiRunButton,
            aiCancelButton,
            aiApplyButton,
            populationRow,
            generationsRow,
            mutationRow,
            seedRow,
            objectiveRow,
            autoApplyRow,
            iterationRow,
            statusRow,
            fitnessRow,
            objectiveValueRow,
            seedValueRow,
            previewLabel,
            aiPreviewContainer,
            historyLabel,
            aiHistoryList);
    panel.setSpacing(8);
    panel.setPadding(new Insets(12, 0, 0, 0));
    return panel;
  }

  private void advanceAndRefresh() {
    boolean[][] before = snapshotGrid();
    gridState.advance();
    generation++;
    gridView.refresh();
    updateCounters();
    updateBirthDeathSeries(before);
    updatePopulationStats(gridState.countAliveCells());
    updateTps(timeline != null && timeline.getStatus() == Animation.Status.RUNNING);
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
    resetTps();
    updatePlayState(true);
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
    resetTps();
    updatePlayState(false);
  }

  private void pauseIfEditingDuringPlay() {
    if (timeline == null) {
      return;
    }
    if (timeline.getStatus() == Animation.Status.RUNNING) {
      pausePlay();
    }
  }

  private void resetSimulation() {
    if (!confirmResetIfNeeded()) {
      return;
    }
    if (timeline != null) {
      timeline.stop();
    }
    generation = 0;
    gridState.clear();
    gridView.refresh();
    resetPopulationSeries();
    updateCounters();
    clearAiPreview();
    resetPopulationStats();
    resetTps();
    if (playButton != null) {
      playButton.setDisable(false);
    }
    if (pauseButton != null) {
      pauseButton.setDisable(true);
    }
    if (stepButton != null) {
      stepButton.setDisable(false);
    }
    updatePlayState(false);
  }

  private void updatePlayState(boolean playing) {
    if (playStateValue != null) {
      playStateValue.setText(playing ? "Play" : "Pause");
    }
  }

  private void applyPlayPauseStyles(
      Button button, String normalColor, String hoverColor, String focusBorderColor) {
    Runnable updater =
        () -> {
          boolean hovered = button.isHover();
          boolean focused = button.isFocused();
          String background = hovered ? hoverColor : normalColor;
          StringBuilder style = new StringBuilder();
          style.append("-fx-background-color: ").append(background).append("; ");
          style.append("-fx-text-fill: white; ");
          style.append("-fx-background-radius: 6; ");
          style.append("-fx-border-radius: 6; ");
          style.append("-fx-border-width: 2; ");
          if (focused) {
            style.append("-fx-border-color: ").append(focusBorderColor).append("; ");
          } else {
            style.append("-fx-border-color: transparent; ");
          }
          button.setStyle(style.toString());
        };
    button.hoverProperty().addListener((obs, oldValue, newValue) -> updater.run());
    button.focusedProperty().addListener((obs, oldValue, newValue) -> updater.run());
    updater.run();
  }

  private boolean confirmResetIfNeeded() {
    if (gridState.countAliveCells() == 0) {
      return true;
    }
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Game of Life");
    alert.setHeaderText("Confirmar reset");
    alert.setContentText("Hay celdas vivas en el tablero. ¿Deseas reiniciar?");
    ButtonType confirm = new ButtonType("Reset");
    ButtonType cancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
    alert.getButtonTypes().setAll(confirm, cancel);
    Optional<ButtonType> result = alert.showAndWait();
    return result.isPresent() && result.get() == confirm;
  }

  private void setupKeyboardShortcuts(Scene scene) {
    scene.addEventHandler(
        KeyEvent.KEY_PRESSED,
        event -> {
          if (event.getCode() == KeyCode.SPACE) {
            togglePlayPause();
            event.consume();
          } else if (event.getCode() == KeyCode.N) {
            handleStep();
            event.consume();
          } else if (event.getCode() == KeyCode.R) {
            resetSimulation();
            event.consume();
          } else if (event.getCode() == KeyCode.S && event.isShortcutDown()) {
            savePattern();
            event.consume();
          } else if (event.getCode() == KeyCode.O && event.isShortcutDown()) {
            loadPattern();
            event.consume();
          }
        });
  }

  private void showShortcutsHelp() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Game of Life");
    alert.setHeaderText("Atajos de teclado");
    alert.setContentText(
        "Play/Pause: Barra espaciadora\n"
            + "Step: N\n"
            + "Reset: R\n"
            + "Save: Ctrl/Cmd+S\n"
            + "Load: Ctrl/Cmd+O");
    alert.showAndWait();
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

  private void updatePopulationStats(int alive) {
    if (minPopulationValue == null || maxPopulationValue == null || avgPopulationValue == null) {
      return;
    }
    if (populationSamples == 0) {
      minPopulation = alive;
      maxPopulation = alive;
    } else {
      minPopulation = Math.min(minPopulation, alive);
      maxPopulation = Math.max(maxPopulation, alive);
    }
    totalPopulation += alive;
    populationSamples++;
    double average = totalPopulation / (double) populationSamples;
    minPopulationValue.setText(Integer.toString(minPopulation));
    maxPopulationValue.setText(Integer.toString(maxPopulation));
    avgPopulationValue.setText(String.format("%.1f", average));
  }

  private void resetPopulationStats() {
    minPopulation = 0;
    maxPopulation = 0;
    totalPopulation = 0L;
    populationSamples = 0;
    if (minPopulationValue != null) {
      minPopulationValue.setText("0");
    }
    if (maxPopulationValue != null) {
      maxPopulationValue.setText("0");
    }
    if (avgPopulationValue != null) {
      avgPopulationValue.setText("0");
    }
  }

  private void updateTps(boolean running) {
    if (tpsValue == null) {
      return;
    }
    if (!running) {
      tpsValue.setText("0");
      return;
    }
    long now = System.nanoTime();
    if (tpsWindowStartNanos == 0L) {
      tpsWindowStartNanos = now;
    }
    tpsTickCount++;
    long elapsedNanos = now - tpsWindowStartNanos;
    if (elapsedNanos >= 1_000_000_000L) {
      double elapsedSeconds = elapsedNanos / 1_000_000_000.0;
      int tps = (int) Math.round(tpsTickCount / elapsedSeconds);
      tpsValue.setText(Integer.toString(tps));
      tpsWindowStartNanos = now;
      tpsTickCount = 0;
    }
  }

  private void resetTps() {
    tpsWindowStartNanos = 0L;
    tpsTickCount = 0;
    if (tpsValue != null) {
      tpsValue.setText("0");
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
    if (birthsSeries != null) {
      birthsSeries.getData().clear();
    }
    if (deathsSeries != null) {
      deathsSeries.getData().clear();
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

  private int resolveAiPopulationSize() {
    return parsePositiveIntField(aiPopulationField, "poblacion", AI_POPULATION_SIZE);
  }

  private int resolveAiGenerations() {
    return parsePositiveIntField(aiGenerationsField, "generaciones", AI_GENERATIONS);
  }

  private double resolveAiMutationRate() {
    return parseRateField(aiMutationField, "tasa de mutacion", AI_MUTATION_RATE, 0.0, 1.0);
  }

  private int parsePositiveIntField(TextField field, String label, int fallback) {
    if (field == null) {
      return fallback;
    }
    String raw = field.getText();
    if (raw == null || raw.isBlank()) {
      field.setText(Integer.toString(fallback));
      return fallback;
    }
    try {
      int value = Integer.parseInt(raw.trim());
      if (value < 1) {
        showError("El valor de " + label + " debe ser al menos 1. Se usara " + fallback + ".");
        field.setText(Integer.toString(fallback));
        return fallback;
      }
      return value;
    } catch (NumberFormatException ex) {
      showError("El valor de " + label + " no es valido. Se usara " + fallback + ".");
      field.setText(Integer.toString(fallback));
      return fallback;
    }
  }

  private double parseRateField(
      TextField field, String label, double fallback, double min, double max) {
    if (field == null) {
      return fallback;
    }
    String raw = field.getText();
    if (raw == null || raw.isBlank()) {
      field.setText(String.format("%.2f", fallback));
      return fallback;
    }
    try {
      double value = Double.parseDouble(raw.trim());
      if (value < min || value > max) {
        double clamped = Math.max(min, Math.min(max, value));
        showError(
            "El valor de "
                + label
                + " debe estar entre "
                + min
                + " y "
                + max
                + ". Se ajusto a "
                + String.format("%.2f", clamped)
                + ".");
        field.setText(String.format("%.2f", clamped));
        return clamped;
      }
      return value;
    } catch (NumberFormatException ex) {
      showError("El valor de " + label + " no es valido. Se usara " + fallback + ".");
      field.setText(String.format("%.2f", fallback));
      return fallback;
    }
  }

  private void runAiSearch() {
    if (aiSearch != null && !aiSearch.isDone()) {
      return;
    }
    if (aiRunButton != null) {
      aiRunButton.setDisable(true);
    }
    setAiRunningIndicator(true);
    if (aiCancelButton != null) {
      aiCancelButton.setDisable(false);
    }
    if (aiApplyButton != null) {
      aiApplyButton.setDisable(true);
    }
    if (aiStatusValue != null) {
      aiStatusValue.setText("Buscando...");
    }
    if (aiIterationValue != null) {
      aiIterationValue.setText("0");
    }
    if (aiFitnessValue != null) {
      aiFitnessValue.setText("-");
    }
    SimulationEngine.GeneticObjective objective = resolveAiObjective();
    if (aiObjectiveValue != null) {
      aiObjectiveValue.setText(formatObjective(objective));
    }
    lastAiObjectiveUsed = objective;
    lastAiAutoApplyEnabled = aiAutoApplyToggle != null && aiAutoApplyToggle.isSelected();
    lastAiAutoApplyThreshold = resolveAiAutoApplyThreshold();
    Long seed = resolveAiSeed();
    if (aiSeedValue != null) {
      aiSeedValue.setText(Long.toString(seed));
    }
    aiCancellationToken = new SimulationEngine.CancellationToken();
    int populationSize = resolveAiPopulationSize();
    int generations = resolveAiGenerations();
    double mutationRate = resolveAiMutationRate();
    SimulationEngine.GeneticSearchConfig config =
        new SimulationEngine.GeneticSearchConfig(
            gridState.getRows(),
            gridState.getColumns(),
            populationSize,
            generations,
            AI_EVALUATION_STEPS,
            mutationRate,
            AI_CROSSOVER_RATE,
            objective,
            seed);
    SimulationEngine.GeneticSearchProgressListener progressListener =
        (iteration, bestFitness) -> {
          Platform.runLater(() -> updateAiProgress(iteration, bestFitness));
        };
    aiSearch = simulationEngine.findPromisingPattern(config, aiCancellationToken, progressListener);
    aiSearch.whenComplete(
        (result, error) -> {
          Platform.runLater(() -> handleAiSearchCompleted(result, error));
        });
  }

  private void cancelAiSearch() {
    if (aiCancellationToken != null) {
      aiCancellationToken.cancel();
    }
    if (aiSearch != null) {
      aiSearch.cancel(true);
    }
    setAiRunningIndicator(false);
    if (aiCancelButton != null) {
      aiCancelButton.setDisable(true);
    }
    if (aiStatusValue != null) {
      aiStatusValue.setText("Cancelando...");
    }
  }

  private void handleAiSearchCompleted(
      SimulationEngine.GeneticSearchResult result, Throwable error) {
    if (aiRunButton != null) {
      aiRunButton.setDisable(false);
    }
    if (aiCancelButton != null) {
      aiCancelButton.setDisable(true);
    }
    setAiRunningIndicator(false);
    if (error != null) {
      Throwable cause = error instanceof CompletionException ? error.getCause() : error;
      if (cause instanceof CancellationException) {
        if (aiStatusValue != null) {
          aiStatusValue.setText("Cancelado");
        }
        if (aiApplyButton != null) {
          aiApplyButton.setDisable(aiResultPattern == null);
        }
        return;
      }
      if (aiStatusValue != null) {
        aiStatusValue.setText("Error");
      }
      if (aiApplyButton != null) {
        aiApplyButton.setDisable(aiResultPattern == null);
      }
      showError("No se pudo completar la busqueda IA: " + cause.getMessage());
      return;
    }
    aiResultPattern = result.pattern();
    if (aiFitnessValue != null) {
      aiFitnessValue.setText(String.format("%.2f", result.fitness()));
    }
    if (aiApplyButton != null) {
      aiApplyButton.setDisable(aiResultPattern == null);
    }
    updateAiPreview(aiResultPattern);
    addAiHistoryEntry(result.fitness(), lastAiObjectiveUsed);
    if (lastAiAutoApplyEnabled) {
      if (result.fitness() >= lastAiAutoApplyThreshold) {
        if (aiStatusValue != null) {
          aiStatusValue.setText("Auto-aplicado");
        }
        applyAiPattern();
      } else {
        if (aiStatusValue != null) {
          aiStatusValue.setText(
              String.format(
                  "No aplicado: fitness %.2f < umbral %.2f",
                  result.fitness(), lastAiAutoApplyThreshold));
        }
      }
    } else if (aiStatusValue != null) {
      aiStatusValue.setText("Listo");
    }
  }

  private void applyAiPattern() {
    if (aiResultPattern == null) {
      showError("No hay un resultado de IA disponible para aplicar.");
      return;
    }
    loadPatternCentered(aiResultPattern);
  }

  private void updateAiProgress(int iteration, double bestFitness) {
    if (aiIterationValue != null) {
      aiIterationValue.setText(Integer.toString(iteration));
    }
    if (aiFitnessValue != null) {
      aiFitnessValue.setText(String.format("%.2f", bestFitness));
    }
  }

  private void addAiHistoryEntry(double fitness, SimulationEngine.GeneticObjective objective) {
    if (aiHistoryList == null) {
      return;
    }
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    String entry =
        String.format(
            "%s | Objetivo: %s | Fitness: %.2f", timestamp, formatObjective(objective), fitness);
    aiHistoryEntries.add(0, entry);
    if (aiHistoryEntries.size() > 5) {
      aiHistoryEntries.remove(aiHistoryEntries.size() - 1);
    }
    aiHistoryList.getItems().setAll(aiHistoryEntries);
  }

  private SimulationEngine.GeneticObjective resolveAiObjective() {
    if (aiObjectivePicker == null) {
      return AI_DEFAULT_OBJECTIVE;
    }
    SimulationEngine.GeneticObjective selected = aiObjectivePicker.getValue();
    return selected != null ? selected : AI_DEFAULT_OBJECTIVE;
  }

  private Long resolveAiSeed() {
    if (aiSeedField == null) {
      return random.nextLong();
    }
    String raw = aiSeedField.getText();
    if (raw == null || raw.isBlank()) {
      return random.nextLong();
    }
    try {
      return Long.parseLong(raw.trim());
    } catch (NumberFormatException ex) {
      showError("La semilla debe ser un numero entero. Se usara una semilla aleatoria.");
      return random.nextLong();
    }
  }

  private double resolveAiAutoApplyThreshold() {
    if (aiAutoApplyThresholdField == null) {
      return AI_AUTO_APPLY_THRESHOLD;
    }
    String raw = aiAutoApplyThresholdField.getText();
    if (raw == null || raw.isBlank()) {
      aiAutoApplyThresholdField.setText(String.format("%.2f", AI_AUTO_APPLY_THRESHOLD));
      return AI_AUTO_APPLY_THRESHOLD;
    }
    try {
      double value = Double.parseDouble(raw.trim());
      if (value < 0) {
        showError("El umbral de fitness no puede ser negativo. Se usara el valor por defecto.");
        aiAutoApplyThresholdField.setText(String.format("%.2f", AI_AUTO_APPLY_THRESHOLD));
        return AI_AUTO_APPLY_THRESHOLD;
      }
      return value;
    } catch (NumberFormatException ex) {
      showError("El umbral de fitness no es valido. Se usara el valor por defecto.");
      aiAutoApplyThresholdField.setText(String.format("%.2f", AI_AUTO_APPLY_THRESHOLD));
      return AI_AUTO_APPLY_THRESHOLD;
    }
  }

  private String formatObjective(SimulationEngine.GeneticObjective objective) {
    return switch (objective) {
      case HIGH_POPULATION -> "Poblacion alta";
      case OSCILLATOR -> "Oscilador";
      case GLIDER -> "Glider";
    };
  }

  private static final class AiObjectiveConverter
      extends javafx.util.StringConverter<SimulationEngine.GeneticObjective> {
    @Override
    public String toString(SimulationEngine.GeneticObjective objective) {
      if (objective == null) {
        return "";
      }
      return switch (objective) {
        case HIGH_POPULATION -> "Poblacion alta";
        case OSCILLATOR -> "Oscilador";
        case GLIDER -> "Glider";
      };
    }

    @Override
    public SimulationEngine.GeneticObjective fromString(String value) {
      if (value == null) {
        return AI_DEFAULT_OBJECTIVE;
      }
      String trimmed = value.trim();
      if (trimmed.equalsIgnoreCase("Oscilador")) {
        return SimulationEngine.GeneticObjective.OSCILLATOR;
      }
      if (trimmed.equalsIgnoreCase("Glider")) {
        return SimulationEngine.GeneticObjective.GLIDER;
      }
      return SimulationEngine.GeneticObjective.HIGH_POPULATION;
    }
  }

  private void setAiRunningIndicator(boolean running) {
    if (aiRunningIndicator == null) {
      return;
    }
    aiRunningIndicator.setVisible(running);
    aiRunningIndicator.setManaged(running);
  }

  private void updateAiPreview(boolean[][] pattern) {
    if (aiPreviewView == null || pattern == null) {
      return;
    }
    aiPreviewState.load(pattern);
    aiPreviewView.refresh();
  }

  private void clearAiPreview() {
    if (aiPreviewView == null) {
      return;
    }
    aiPreviewState.clear();
    aiPreviewView.refresh();
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
    double fitByWidth = availableWidth / gridState.getColumns();
    double fitByHeight = availableHeight / gridState.getRows();
    double fitCellSize = Math.min(fitByWidth, fitByHeight);
    double clamped = Math.max(MIN_CELL_SIZE, Math.min(desiredCellSize, fitCellSize));
    gridView.setCellSize(clamped);
    if (zoomValue != null) {
      zoomValue.setText(String.format("%.0f px", clamped));
    }
  }

  private void applyGridSize(GridSize size) {
    if (size == null) {
      return;
    }
    if (gridState.getRows() == size.rows && gridState.getColumns() == size.columns) {
      return;
    }
    if (aiSearch != null && !aiSearch.isDone()) {
      cancelAiSearch();
    }
    if (timeline != null) {
      timeline.stop();
    }
    generation = 0;
    gridState = new GridState(size.rows, size.columns);
    gridState.setToroidal(toroidalEnabled);
    gridView = new EditableGridView(gridState, desiredCellSize);
    configureGridView(gridView);
    if (gridContainer != null) {
      gridContainer.getChildren().setAll(gridView);
    }
    updateGridSizing();
    resetPopulationSeries();
    updateCounters();
    resetPopulationStats();
    resetAiStateForGrid();
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

  private void resetAiStateForGrid() {
    aiResultPattern = null;
    if (aiApplyButton != null) {
      aiApplyButton.setDisable(true);
    }
    if (aiStatusValue != null) {
      aiStatusValue.setText("Listo");
    }
    if (aiFitnessValue != null) {
      aiFitnessValue.setText("-");
    }
    if (aiIterationValue != null) {
      aiIterationValue.setText("0");
    }
    aiPreviewState = new GridState(gridState.getRows(), gridState.getColumns());
    aiPreviewView = new EditableGridView(aiPreviewState, AI_PREVIEW_CELL_SIZE);
    aiPreviewView.setMouseTransparent(true);
    if (aiPreviewContainer != null) {
      aiPreviewContainer.getChildren().setAll(aiPreviewView);
    }
  }

  private void randomizeGrid() {
    boolean[][] pattern = null;
    for (int attempt = 0; attempt < RANDOM_ATTEMPTS; attempt++) {
      double probability =
          RANDOM_MIN_PROBABILITY
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
    resetPopulationStats();
  }

  private void centerPattern() {
    int rows = gridState.getRows();
    int columns = gridState.getColumns();
    int minRow = rows;
    int maxRow = -1;
    int minColumn = columns;
    int maxColumn = -1;
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        if (gridState.isAlive(row, column)) {
          minRow = Math.min(minRow, row);
          maxRow = Math.max(maxRow, row);
          minColumn = Math.min(minColumn, column);
          maxColumn = Math.max(maxColumn, column);
        }
      }
    }
    if (maxRow < minRow || maxColumn < minColumn) {
      showInfo("No hay celdas vivas para centrar.");
      return;
    }
    int patternRows = maxRow - minRow + 1;
    int patternColumns = maxColumn - minColumn + 1;
    int targetRow = (rows - patternRows) / 2;
    int targetColumn = (columns - patternColumns) / 2;
    boolean[][] centered = new boolean[rows][columns];
    for (int row = minRow; row <= maxRow; row++) {
      for (int column = minColumn; column <= maxColumn; column++) {
        if (gridState.isAlive(row, column)) {
          centered[targetRow + (row - minRow)][targetColumn + (column - minColumn)] = true;
        }
      }
    }
    gridState.load(centered);
    gridView.refresh();
  }

  private void savePattern() {
    if (stage == null) {
      return;
    }
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Guardar patron");
    chooser
        .getExtensionFilters()
        .addAll(
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
    if (!confirmLoadIfNeeded()) {
      return;
    }
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Cargar patron");
    chooser
        .getExtensionFilters()
        .addAll(
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
      if (data.rows() != gridState.getRows() || data.columns() != gridState.getColumns()) {
        showError("El patron no coincide con el tamano de la cuadricula.");
        return;
      }
      gridState.load(data.cells());
      generation = 0;
      gridView.refresh();
      resetPopulationSeries();
      updateCounters();
      resetPopulationStats();
      showInfo("Patron cargado desde " + filePath.getFileName() + ".");
    } catch (IOException | IllegalArgumentException ex) {
      showError("No se pudo cargar el patron: " + ex.getMessage());
    }
  }

  private boolean confirmLoadIfNeeded() {
    if (gridState.countAliveCells() == 0) {
      return true;
    }
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Game of Life");
    alert.setHeaderText("Confirmar carga");
    alert.setContentText("Hay celdas vivas en el tablero. ¿Deseas cargar otro patron?");
    ButtonType confirm = new ButtonType("Cargar");
    ButtonType cancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
    alert.getButtonTypes().setAll(confirm, cancel);
    Optional<ButtonType> result = alert.showAndWait();
    return result.isPresent() && result.get() == confirm;
  }

  private boolean confirmExitIfNeeded() {
    if (gridState.countAliveCells() == 0) {
      return true;
    }
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Game of Life");
    alert.setHeaderText("Confirmar salida");
    alert.setContentText("Hay celdas vivas en el tablero. ¿Deseas salir?");
    ButtonType confirm = new ButtonType("Salir");
    ButtonType cancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
    alert.getButtonTypes().setAll(confirm, cancel);
    Optional<ButtonType> result = alert.showAndWait();
    return result.isPresent() && result.get() == confirm;
  }

  private void exportPopulationCsv() {
    if (stage == null) {
      return;
    }
    if (populationSeries == null) {
      showError("No hay datos de poblacion para exportar.");
      return;
    }
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Exportar poblacion a CSV");
    chooser
        .getExtensionFilters()
        .addAll(
            new FileChooser.ExtensionFilter("CSV file (*.csv)", "*.csv"),
            new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
    chooser.setInitialFileName("poblacion.csv");
    var file = chooser.showSaveDialog(stage);
    if (file == null) {
      return;
    }
    Path filePath = file.toPath();
    StringBuilder builder = new StringBuilder("generacion,poblacion\n");
    for (XYChart.Data<Number, Number> point : populationSeries.getData()) {
      builder
          .append(point.getXValue().intValue())
          .append(',')
          .append(point.getYValue().intValue())
          .append('\n');
    }
    try {
      Files.writeString(filePath, builder.toString(), StandardCharsets.UTF_8);
      showInfo("CSV guardado en " + filePath.getFileName() + ".");
    } catch (IOException ex) {
      showError("No se pudo exportar el CSV: " + ex.getMessage());
    }
  }

  private boolean[][] patternFor(String name) {
    return switch (name) {
      case "Glider" ->
          new boolean[][] {
            {false, true, false},
            {false, false, true},
            {true, true, true}
          };
      case "Blinker" -> new boolean[][] {{true}, {true}, {true}};
      case "Toad" ->
          new boolean[][] {
            {false, true, true, true},
            {true, true, true, false}
          };
      case "Beacon" ->
          new boolean[][] {
            {true, true, false, false},
            {true, true, false, false},
            {false, false, true, true},
            {false, false, true, true}
          };
      default -> new boolean[][] {{true}};
    };
  }

  private void loadPatternCentered(boolean[][] pattern) {
    if (pattern == null || pattern.length == 0 || pattern[0].length == 0) {
      showError("El patron seleccionado es invalido.");
      return;
    }
    int rows = gridState.getRows();
    int columns = gridState.getColumns();
    if (pattern.length > rows || pattern[0].length > columns) {
      showError("El patron excede el tamano de la cuadricula.");
      return;
    }
    gridState.clear();
    int rowOffset = (rows - pattern.length) / 2;
    int columnOffset = (columns - pattern[0].length) / 2;
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
    int rows = gridState.getRows();
    int columns = gridState.getColumns();
    boolean[][] snapshot = new boolean[rows][columns];
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        snapshot[row][column] = gridState.isAlive(row, column);
      }
    }
    return snapshot;
  }

  private void updateBirthDeathSeries(boolean[][] before) {
    if (birthsSeries == null || deathsSeries == null || before == null) {
      return;
    }
    int rows = before.length;
    if (rows == 0) {
      return;
    }
    int columns = before[0].length;
    int births = 0;
    int deaths = 0;
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        boolean wasAlive = before[row][column];
        boolean isAlive = gridState.isAlive(row, column);
        if (!wasAlive && isAlive) {
          births++;
        } else if (wasAlive && !isAlive) {
          deaths++;
        }
      }
    }
    birthsSeries.getData().add(new XYChart.Data<>(generation, births));
    deathsSeries.getData().add(new XYChart.Data<>(generation, deaths));
    trimSeries(birthsSeries);
    trimSeries(deathsSeries);
  }

  private void trimSeries(XYChart.Series<Number, Number> series) {
    if (series == null) {
      return;
    }
    int size = series.getData().size();
    if (size > POPULATION_HISTORY) {
      series.getData().remove(0, size - POPULATION_HISTORY);
    }
  }

  private void showInfo(String message) {
    setStatusMessage(message, false);
  }

  private void showError(String message) {
    setStatusMessage(message, true);
  }

  private void setStatusMessage(String message, boolean error) {
    if (statusMessage == null) {
      return;
    }
    statusMessage.setText(message);
    statusMessage.setStyle(error ? "-fx-text-fill: #b00020;" : "-fx-text-fill: #1b5e20;");
  }

  private void updateCursorPosition(int row, int column) {
    if (cursorPositionValue == null) {
      return;
    }
    cursorPositionValue.setText("Fila " + (row + 1) + ", Col " + (column + 1));
  }

  private void clearCursorPosition() {
    if (cursorPositionValue == null) {
      return;
    }
    cursorPositionValue.setText("N/A");
  }

  private void exportGridPng() {
    if (gridView == null) {
      showError("No hay una cuadricula disponible para exportar.");
      return;
    }
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Exportar PNG");
    chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
    chooser.setInitialFileName("tablero.png");
    File file = chooser.showSaveDialog(stage);
    if (file == null) {
      return;
    }
    SnapshotParameters parameters = new SnapshotParameters();
    WritableImage image = gridView.snapshot(parameters, null);
    BufferedImage buffered = SwingFXUtils.fromFXImage(image, null);
    try {
      ImageIO.write(buffered, "png", file);
      showInfo("PNG guardado en " + file.getName() + ".");
    } catch (IOException ex) {
      showError("No se pudo exportar el PNG: " + ex.getMessage());
    }
  }

  private void copyPatternToClipboard() {
    try {
      String serialized = PatternIO.serialize(snapshotGrid());
      ClipboardContent content = new ClipboardContent();
      content.putString(serialized);
      Clipboard.getSystemClipboard().setContent(content);
      showInfo("Patron copiado al portapapeles.");
    } catch (IllegalArgumentException ex) {
      showError("No se pudo copiar el patron: " + ex.getMessage());
    }
  }

  private void pastePatternFromClipboard() {
    Clipboard clipboard = Clipboard.getSystemClipboard();
    if (clipboard == null || !clipboard.hasString()) {
      showError("El portapapeles no contiene un patron valido.");
      return;
    }
    String content = clipboard.getString();
    try {
      PatternData data = PatternIO.parse(content);
      boolean[][] pattern = data.cells();
      if (pattern.length > gridState.getRows() || pattern[0].length > gridState.getColumns()) {
        showError("El patron excede el tamano de la cuadricula.");
        return;
      }
      gridState.clear();
      int rowOffset = (gridState.getRows() - pattern.length) / 2;
      int columnOffset = (gridState.getColumns() - pattern[0].length) / 2;
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
      resetPopulationStats();
      showInfo("Patron pegado desde el portapapeles.");
    } catch (IllegalArgumentException ex) {
      showError("No se pudo pegar el patron: " + ex.getMessage());
    }
  }

  private boolean[][] buildRandomPattern(double probability) {
    int rows = gridState.getRows();
    int columns = gridState.getColumns();
    boolean[][] pattern = new boolean[rows][columns];
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        pattern[row][column] = random.nextDouble() < probability;
      }
    }
    return pattern;
  }

  private boolean isTrivial(boolean[][] pattern) {
    int alive = 0;
    int total = gridState.getRows() * gridState.getColumns();
    for (int row = 0; row < gridState.getRows(); row++) {
      for (int column = 0; column < gridState.getColumns(); column++) {
        if (pattern[row][column]) {
          alive++;
        }
      }
    }
    return alive == 0 || alive == total;
  }

  private boolean evolvesWithinSteps(boolean[][] pattern, int steps) {
    GridState simulation = new GridState(gridState.getRows(), gridState.getColumns());
    simulation.load(pattern);
    for (int step = 0; step < steps; step++) {
      simulation.advance();
    }
    for (int row = 0; row < gridState.getRows(); row++) {
      for (int column = 0; column < gridState.getColumns(); column++) {
        if (simulation.isAlive(row, column) != pattern[row][column]) {
          return true;
        }
      }
    }
    return false;
  }

  private static final class GridSize {
    private final String label;
    private final int rows;
    private final int columns;

    private GridSize(String label, int rows, int columns) {
      this.label = label;
      this.rows = rows;
      this.columns = columns;
    }

    @Override
    public String toString() {
      return label;
    }
  }
}
