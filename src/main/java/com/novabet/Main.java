package com.novabet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Font.loadFont(getClass().getResourceAsStream("/com/novabet/fonts/InstrumentSerif-Regular.ttf"), 14);
        URL fxmlLocation = getClass().getResource("/com/novabet/views/OnboardingView.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();
        Scene scene = new Scene(root, 1920, 1080);
        primaryStage.setTitle("NovaBet");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
