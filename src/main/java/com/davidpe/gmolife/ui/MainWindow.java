package com.davidpe.gmolife.ui;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public final class MainWindow {

  public void show(Stage stage) {
    BorderPane root = new BorderPane();
    root.setCenter(new Label("Game of Life - Bootstrap"));

    Scene scene = new Scene(root, 960, 640);
    stage.setTitle("Game of Life");
    stage.setScene(scene);
    stage.show();
  }
}
