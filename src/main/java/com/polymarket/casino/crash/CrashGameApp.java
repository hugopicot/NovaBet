package com.polymarket.casino.crash;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CrashGameApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        CrashGameView view = new CrashGameView(1L, () -> System.out.println("back"));
        Scene scene = new Scene(view.getView(), 900, 650);
        stage.setTitle("NovaBet · 🚀 Crash Roquette");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
