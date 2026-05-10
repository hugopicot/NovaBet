package com.polymarket;

import com.polymarket.ui.auth.AuthModule;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        AuthModule authModule = new AuthModule(
            primaryStage,
            getClass().getResource("/styles.css").toExternalForm()
        );

        primaryStage.setTitle("NovaBet");
        authModule.start();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
