package com.github.marlon2132.cw_2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CW_Application extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/github/marlon2132/cw_2/window-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Награждение победителей — расчёт затрат");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
