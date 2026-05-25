package com.polymarket.casino.lobby;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Lanceur standalone du Casino Lobby — utile pour developper et demontrer
 * le lobby seul, sans le reste de l'app.
 */
public class CasinoLobbyApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                getClass().getResource("/com/polymarket/casino/lobby/CasinoLobbyView.fxml")
        );
        Scene scene = new Scene(root, 1200, 800);
        stage.setTitle("NovaBet · Casino Lobby");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
