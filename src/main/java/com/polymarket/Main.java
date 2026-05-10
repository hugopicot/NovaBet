package com.polymarket;

import com.polymarket.ui.CreateMarketView;
import com.polymarket.ui.DeleteMarketView;
import com.polymarket.ui.MarketDetailView;
import com.polymarket.ui.MarketsListView;
import com.polymarket.ui.UpdateMarketView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        loadFonts();

        MarketsListView marketsView = new MarketsListView();
        MarketDetailView detailView = new MarketDetailView();
        CreateMarketView createView = new CreateMarketView();
        UpdateMarketView updateView = new UpdateMarketView();
        DeleteMarketView deleteView = new DeleteMarketView();

        Scene marketsScene = new Scene(marketsView.getView(), 1280, 800);
        Scene detailScene = new Scene(detailView.getView(), 1280, 800);
        Scene createScene = new Scene(createView.getView(), 1280, 800);
        Scene updateScene = new Scene(updateView.getView(), 1280, 800);
        Scene deleteScene = new Scene(deleteView.getView(), 1280, 800);

        String css = getClass().getResource("/styles.css").toExternalForm();
        marketsScene.getStylesheets().add(css);
        detailScene.getStylesheets().add(css);
        createScene.getStylesheets().add(css);
        updateScene.getStylesheets().add(css);
        deleteScene.getStylesheets().add(css);

        marketsView.setOnMarketClick(() -> primaryStage.setScene(detailScene));
        marketsView.setOnCreateMarket(() -> primaryStage.setScene(createScene));

        detailView.setOnMarketsClick(() -> primaryStage.setScene(marketsScene));
        detailView.setOnEditMarket(() -> primaryStage.setScene(updateScene));
        detailView.setOnDeleteMarket(() -> primaryStage.setScene(deleteScene));

        createView.setOnBack(() -> primaryStage.setScene(marketsScene));
        updateView.setOnBack(() -> primaryStage.setScene(detailScene));
        deleteView.setOnBack(() -> primaryStage.setScene(detailScene));
        deleteView.setOnConfirmDelete(() -> primaryStage.setScene(marketsScene));

        primaryStage.setTitle("NovaBet");
        primaryStage.setScene(marketsScene);
        primaryStage.show();
    }

    private void loadFonts() {
        try {
            Font.loadFont(
                "https://cdn.jsdelivr.net/gh/google/fonts@main/ofl/instrumentserif/InstrumentSerif-Regular.ttf",
                12
            );
            Font.loadFont(
                "https://cdn.jsdelivr.net/gh/google/fonts@main/ofl/inter/Inter-VariableFont_opsz,wght.ttf",
                12
            );
        } catch (Exception e) {
            System.err.println("Failed to load fonts: " + e.getMessage());
        }
    }

//    REvert this file in Git to restore auth flow
//    1. Create DB
//    2. Connect to DB
//    3. Restore permanently auth flow

    public static void main(String[] args) {
        launch(args);
    }
}
