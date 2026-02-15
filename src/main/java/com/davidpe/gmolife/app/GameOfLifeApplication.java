package com.davidpe.gmolife.app;

import com.davidpe.gmolife.ui.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public final class GameOfLifeApplication extends Application {

  private final MainWindow mainWindow = new MainWindow();

  @Override
  public void start(Stage primaryStage) {
    mainWindow.show(primaryStage);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
