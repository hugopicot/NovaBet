package com.polymarket.oto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class OtoApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                getClass().getResource("/com/polymarket/oto/OneTimeOfferView.fxml")
        );
        Scene scene = new Scene(root, 1200, 800);
        stage.setTitle("NovaBet . OTO");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
