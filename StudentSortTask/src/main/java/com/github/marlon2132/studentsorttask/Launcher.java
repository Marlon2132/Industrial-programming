package com.github.marlon2132.studentsorttask;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("student-view.fxml"));
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.setTitle("Student Sort Task");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
