package com.polymarket;

import com.polymarket.dao.betsDao;
import com.polymarket.dao.eventsDao;
import com.polymarket.dao.outcomesDao;
import com.polymarket.dao.transactionsDao;
import com.polymarket.dao.walletsDao;
import com.polymarket.domain.dto.BetRequest;
import com.polymarket.domain.dto.BetResult;
import com.polymarket.domain.service.BettingService;
import com.polymarket.domain.service.BettingServiceImpl;
import com.polymarket.domain.service.MarketService;
import com.polymarket.domain.service.MarketServiceImpl;
import com.polymarket.model.events;
import com.polymarket.model.outcomes;
import com.polymarket.model.users;
import com.polymarket.model.wallets;
import com.polymarket.ui.CreateMarketView;
import com.polymarket.ui.DeleteMarketView;
import com.polymarket.ui.MarketDetailView;
import com.polymarket.ui.MarketsListView;
import com.polymarket.ui.UpdateMarketView;
import com.polymarket.ui.WalletView;
import com.polymarket.ui.auth.AuthModule;
import com.polymarket.domain.service.WalletService;
import com.polymarket.domain.service.WalletServiceImpl;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application {

    private MarketService marketService;
    private MarketsListView marketsView;
    private MarketDetailView detailView;
    private CreateMarketView createView;
    private UpdateMarketView updateView;
    private DeleteMarketView deleteView;

    private Scene marketsScene;
    private Scene detailScene;
    private Scene createMarketScene;
    private Scene updateScene;
    private Scene deleteScene;
    private Scene walletScene;

    private Stage primaryStage;
    private String css;
    private Long selectedMarketId;
    private Long currentUserId;
    private BettingService bettingService;
    private WalletService walletService;
    private walletsDao walletDao;
    private WalletView walletView;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadFonts();

        try {
            eventsDao eventDao = new eventsDao();
            outcomesDao outcomeDao = new outcomesDao();
            marketService = new MarketServiceImpl(eventDao, outcomeDao);
            walletDao = new walletsDao();
            bettingService = new BettingServiceImpl(
                outcomeDao, eventDao, walletDao, new betsDao(), new transactionsDao()
            );
            walletService = new WalletServiceImpl(walletDao, new transactionsDao());
            walletView = new WalletView();
        } catch (Exception e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        }

        css = getClass().getResource("/styles.css").toExternalForm();

        marketsView = new MarketsListView();
        detailView = new MarketDetailView();
        createView = new CreateMarketView();
        updateView = new UpdateMarketView();
        deleteView = new DeleteMarketView();

        marketsScene = createScene(marketsView.getView());
        detailScene = createScene(detailView.getView());
        createMarketScene = createScene(createView.getView());
        updateScene = createScene(updateView.getView());
        deleteScene = createScene(deleteView.getView());
        walletScene = createScene(walletView.getView());

        wireNavigation();

        AuthModule authModule = new AuthModule(primaryStage, css);
        authModule.setOnLoginSuccess(() -> {
            users user = authModule.getCurrentUser();
            if (user != null) {
                currentUserId = user.getId();
                marketsView.setCurrentUserId(currentUserId);
                detailView.setCurrentUserId(currentUserId);
                refreshBalance();
            }
            loadMarkets();
            primaryStage.setScene(marketsScene);
        });
        authModule.start();

        primaryStage.setTitle("NovaBet");
        primaryStage.show();
    }

    private void wireNavigation() {
        marketsView.setOnMarketClick(eventId -> {
            selectedMarketId = eventId;
            loadMarketDetail(eventId);
            primaryStage.setScene(detailScene);
        });
        marketsView.setOnCreateMarket(() -> {
            createView.clearForm();
            primaryStage.setScene(createMarketScene);
        });

        detailView.setOnMarketsClick(() -> {
            loadMarkets();
            primaryStage.setScene(marketsScene);
        });
        detailView.setOnEditMarket(() -> {
            if (selectedMarketId != null) {
                events event = marketService.getMarketById(selectedMarketId);
                updateView.setEventData(event);
            }
            primaryStage.setScene(updateScene);
        });
        detailView.setOnDeleteMarket(() -> {
            if (selectedMarketId != null) {
                events event = marketService.getMarketById(selectedMarketId);
                deleteView.setEventData(event);
            }
            primaryStage.setScene(deleteScene);
        });

        createView.setOnBack(() -> {
            loadMarkets();
            primaryStage.setScene(marketsScene);
        });
        createView.setOnMarketsClick(() -> {
            loadMarkets();
            primaryStage.setScene(marketsScene);
        });
        createView.setOnMarketCreated((event, yesProbability) -> {
            try {
                marketService.createMarket(event);
                outcomes yesOutcome = new outcomes(event.getId(), "YES", yesProbability);
                outcomes noOutcome = new outcomes(event.getId(), "NO", 1.0 - yesProbability);
                new outcomesDao().add(yesOutcome);
                new outcomesDao().add(noOutcome);
                loadMarkets();
                primaryStage.setScene(marketsScene);
            } catch (Exception ex) {
                System.err.println("Error creating market: " + ex.getMessage());
            }
        });

        updateView.setOnBack(() -> {
            if (selectedMarketId != null) {
                loadMarketDetail(selectedMarketId);
            }
            primaryStage.setScene(detailScene);
        });
        updateView.setOnMarketsClick(() -> {
            loadMarkets();
            primaryStage.setScene(marketsScene);
        });
        updateView.setOnMarketUpdated((event, yesProbability) -> {
            try {
                marketService.updateMarket(event);
                outcomesDao outcomeDao = new outcomesDao();
                List<outcomes> existingOutcomes = outcomeDao.findByEventId(event.getId());
                for (outcomes o : existingOutcomes) {
                    if ("YES".equalsIgnoreCase(o.getLabel())) {
                        o.setOdds(yesProbability);
                    } else if ("NO".equalsIgnoreCase(o.getLabel())) {
                        o.setOdds(1.0 - yesProbability);
                    }
                    outcomeDao.update(o);
                }
                if (selectedMarketId != null) {
                    loadMarketDetail(selectedMarketId);
                    primaryStage.setScene(detailScene);
                }
            } catch (Exception ex) {
                System.err.println("Error updating market: " + ex.getMessage());
            }
        });

        deleteView.setOnBack(() -> {
            if (selectedMarketId != null) {
                loadMarketDetail(selectedMarketId);
            }
            primaryStage.setScene(detailScene);
        });
        deleteView.setOnMarketsClick(() -> {
            loadMarkets();
            primaryStage.setScene(marketsScene);
        });
        deleteView.setOnConfirmDelete(() -> {
            if (selectedMarketId != null) {
                marketService.deleteMarket(selectedMarketId);
                selectedMarketId = null;
            }
            loadMarkets();
            primaryStage.setScene(marketsScene);
        });

        marketsView.setOnPlaceBet(this::handlePlaceBet);
        detailView.setOnPlaceBet(this::handlePlaceBet);

        marketsView.setOnWalletClick(() -> {
            loadWallet();
            primaryStage.setScene(walletScene);
        });
        detailView.setOnWalletClick(() -> {
            loadWallet();
            primaryStage.setScene(walletScene);
        });
        createView.setOnWalletClick(() -> {
            loadWallet();
            primaryStage.setScene(walletScene);
        });
        updateView.setOnWalletClick(() -> {
            loadWallet();
            primaryStage.setScene(walletScene);
        });
        deleteView.setOnWalletClick(() -> {
            loadWallet();
            primaryStage.setScene(walletScene);
        });

        walletView.setOnBack(() -> {
            loadMarkets();
            primaryStage.setScene(marketsScene);
        });
        walletView.setOnDeposit(this::handleDeposit);
        walletView.setOnWithdraw(this::handleWithdraw);
    }

    private void handlePlaceBet(BetRequest request) {
        if (bettingService == null) return;
        try {
            BetResult result = bettingService.buyShares(request);
            refreshBalance();
            showAlert(Alert.AlertType.INFORMATION, "Bet placed",
                String.format("You bought %d shares for %s $NVB. Remaining balance: %s $NVB",
                    result.shareCount(), result.totalCost(), result.remainingBalance()));
        } catch (Exception ex) {
            System.err.println("Betting error: " + ex.getMessage());
            showAlert(Alert.AlertType.ERROR, "Bet failed", ex.getMessage());
        }
    }

    private void refreshBalance() {
        if (walletDao == null || currentUserId == null) return;
        wallets wallet = walletDao.findByUserId(currentUserId);
        if (wallet != null) {
            double total = wallet.getRealBalance() + wallet.getVirtualBalance();
            marketsView.setBalance(total);
            detailView.setBalance(total);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadMarkets() {
        if (marketService == null) return;
        try {
            List<events> markets = marketService.getAllMarkets();
            outcomesDao outcomeDao = new outcomesDao();
            Map<Long, List<outcomes>> outcomesMap = new HashMap<>();
            for (events e : markets) {
                outcomesMap.put(e.getId(), outcomeDao.findByEventId(e.getId()));
            }
            marketsView.setMarkets(markets, outcomesMap);
        } catch (Exception ex) {
            System.err.println("Error loading markets: " + ex.getMessage());
        }
    }

    private void loadMarketDetail(Long eventId) {
        if (marketService == null) return;
        try {
            events event = marketService.getMarketById(eventId);
            outcomesDao outcomeDao = new outcomesDao();
            List<outcomes> outcomes = outcomeDao.findByEventId(eventId);
            detailView.setEventData(event, outcomes);
        } catch (Exception ex) {
            System.err.println("Error loading market detail: " + ex.getMessage());
        }
    }

    private void handleDeposit(double amount) {
        if (walletService == null || currentUserId == null) return;
        try {
            walletService.deposit(currentUserId, amount);
            refreshBalance();
            loadWallet();
        } catch (Exception ex) {
            System.err.println("Deposit error: " + ex.getMessage());
            showAlert(Alert.AlertType.ERROR, "Deposit failed", ex.getMessage());
        }
    }

    private void handleWithdraw(double amount) {
        if (walletService == null || currentUserId == null) return;
        try {
            walletService.withdraw(currentUserId, amount);
            refreshBalance();
            loadWallet();
        } catch (Exception ex) {
            System.err.println("Withdraw error: " + ex.getMessage());
            showAlert(Alert.AlertType.ERROR, "Withdraw failed", ex.getMessage());
        }
    }

    private void loadWallet() {
        if (walletService == null || currentUserId == null) return;
        try {
            wallets wallet = walletService.getWallet(currentUserId);
            if (wallet != null) {
                double total = wallet.getRealBalance() + wallet.getVirtualBalance();
                walletView.setBalances(total, wallet.getRealBalance(), wallet.getVirtualBalance());
            }
            var txs = walletService.getTransactionHistory(currentUserId);
            walletView.setTransactions(txs);
        } catch (Exception ex) {
            System.err.println("Error loading wallet: " + ex.getMessage());
        }
    }

    private Scene createScene(Parent root) {
        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(css);
        return scene;
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

    public static void main(String[] args) {
        launch(args);
    }
}