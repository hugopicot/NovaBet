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
import com.polymarket.model.*;
import com.polymarket.ui.HistoryView;
import com.polymarket.ui.MarketDetailView;
import com.polymarket.ui.MarketsListView;
import com.polymarket.ui.PortfolioView;
import com.polymarket.ui.WalletView;
import com.polymarket.ui.auth.AuthModule;
import com.polymarket.domain.service.WalletService;
import com.polymarket.domain.service.WalletServiceImpl;
import com.polymarket.dao.PriceHistoryDao;
import com.polymarket.domain.service.PolymarketSyncService;
import com.polymarket.domain.service.PolymarketResolutionService;
import com.polymarket.infrastructure.polymarket.PolymarketHttpClient;
import com.polymarket.infrastructure.polymarket.PolymarketGammaClient;
import com.polymarket.infrastructure.polymarket.PolymarketClobClient;
import com.polymarket.app.services.StripePaymentService;
import com.polymarket.app.services.TransactionService;
import com.polymarket.oto.OneTimeOfferController;
import com.stripe.exception.StripeException;

import java.awt.Desktop;
import java.net.URI;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application {

    private MarketService marketService;
    private MarketsListView marketsView;
    private MarketDetailView detailView;
    private PortfolioView portfolioView;
    private HistoryView historyView;

    private Scene marketsScene;
    private Scene detailScene;
    private Scene portfolioScene;
    private Scene historyScene;
    private Scene walletScene;

    private Stage primaryStage;
    private String css;
    private Long selectedMarketId;
    private Long currentUserId;
    private BettingService bettingService;
    private WalletService walletService;
    private walletsDao walletDao;
    private WalletView walletView;
    private PolymarketSyncService polymarketSyncService;
    private PolymarketClobClient clobClient;
    private PolymarketResolutionService resolutionService;
    private Timeline uiPollingTimeline;
    private AuthModule authModule;

    private final StripePaymentService paymentService = new StripePaymentService();
    private final TransactionService transactionService = new TransactionService();
    private final ScheduledExecutorService depositScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "stripe-deposit-poller");
        t.setDaemon(true);
        return t;
    });
    private ScheduledFuture<?> currentDepositPoll;

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
            historyView = new HistoryView();
PolymarketHttpClient polymarketHttp = new PolymarketHttpClient();
            PolymarketGammaClient gammaClient = new PolymarketGammaClient(polymarketHttp);
            clobClient = new PolymarketClobClient(polymarketHttp);
            resolutionService = new PolymarketResolutionService(
                eventDao, outcomeDao, new betsDao(), walletDao, new transactionsDao()
            );
            PriceHistoryDao priceHistoryDao = new PriceHistoryDao();
            polymarketSyncService = new PolymarketSyncService(
                gammaClient, clobClient, eventDao, outcomeDao, resolutionService, priceHistoryDao
            );
        } catch (Exception e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        }

        css = getClass().getResource("/styles.css").toExternalForm();

        marketsView = new MarketsListView();
        detailView = new MarketDetailView();
        portfolioView = new PortfolioView();

        marketsScene = createScene(marketsView.getView());
        detailScene = createScene(detailView.getView());
        portfolioScene = createScene(portfolioView.getView());
        historyScene = createScene(historyView.getView());
        walletScene = createScene(walletView.getView());

        wireNavigation();

        authModule = new AuthModule(primaryStage, css);
        authModule.setOnLoginSuccess(() -> {
            users user = authModule.getCurrentUser();
            boolean isAdmin = user != null && "antonio@gmail.com".equalsIgnoreCase(user.getEmail());
            if (user != null) {
                currentUserId = user.getId();
                marketsView.setCurrentUserId(currentUserId);
                detailView.setCurrentUserId(currentUserId);
                detailView.setAdmin(isAdmin);
                portfolioView.setCurrentUserId(currentUserId);
                historyView.setCurrentUserId(currentUserId);
                refreshBalance();
            }
            detailView.setOnAdminSettle(winnerLabel -> {
                if (selectedMarketId != null && resolutionService != null) {
                    resolutionService.settleMarket(selectedMarketId, winnerLabel);
                    refreshBalance();
                }
            });
            loadMarkets();
            if (polymarketSyncService != null) {
                polymarketSyncService.start();
            }
            uiPollingTimeline = new Timeline(new KeyFrame(Duration.seconds(30), e -> {
                refreshBalance();
                if (primaryStage.getScene() == marketsScene) {
                    loadMarkets();
                } else if (primaryStage.getScene() == detailScene && selectedMarketId != null) {
                    loadMarketDetail(selectedMarketId);
                } else if (primaryStage.getScene() == walletScene) {
                    loadWallet();
                } else if (primaryStage.getScene() == portfolioScene) {
                    loadPortfolio();
                } else if (primaryStage.getScene() == historyScene) {
                    loadHistory();
                }
            }));
            uiPollingTimeline.setCycleCount(Timeline.INDEFINITE);
            uiPollingTimeline.play();
            switchToScene(marketsScene);
        });
        authModule.start();

        primaryStage.setTitle("NovaBet");
        primaryStage.show();
    }

    private void wireNavigation() {
        marketsView.setOnMarketClick(eventId -> {
            selectedMarketId = eventId;
            loadMarketDetail(eventId);
            refreshBalance();
            switchToScene(detailScene);
        });
        marketsView.setOnPortfolioClick(() -> {
            loadPortfolio();
            refreshBalance();
            switchToScene(portfolioScene);
        });
        marketsView.setOnCasino(this::openCasinoLobby);
        detailView.setOnCasinoClick(this::openCasinoLobby);
        portfolioView.setOnCasinoClick(this::openCasinoLobby);
        historyView.setOnCasinoClick(this::openCasinoLobby);
        walletView.setOnCasinoClick(this::openCasinoLobby);

        detailView.setOnMarketsClick(() -> {
            loadMarkets();
            refreshBalance();
            switchToScene(marketsScene);
        });
        detailView.setOnPortfolioClick(() -> {
            loadPortfolio();
            refreshBalance();
            switchToScene(portfolioScene);
        });

        portfolioView.setOnMarketsClick(() -> {
            loadMarkets();
            refreshBalance();
            switchToScene(marketsScene);
        });
        portfolioView.setOnMarketClick(eventId -> {
            selectedMarketId = eventId;
            loadMarketDetail(eventId);
            refreshBalance();
            switchToScene(detailScene);
        });
        portfolioView.setOnHistoryClick(() -> {
            loadHistory();
            refreshBalance();
            switchToScene(historyScene);
        });
        portfolioView.setOnWalletClick(() -> {
            loadWallet();
            refreshBalance();
            switchToScene(walletScene);
        });

        historyView.setOnMarketsClick(() -> {
            loadMarkets();
            refreshBalance();
            switchToScene(marketsScene);
        });
        historyView.setOnPortfolioClick(() -> {
            loadPortfolio();
            refreshBalance();
            switchToScene(portfolioScene);
        });
        historyView.setOnWalletClick(() -> {
            loadWallet();
            refreshBalance();
            switchToScene(walletScene);
        });

        marketsView.setOnPlaceBet(this::handlePlaceBet);
        detailView.setOnPlaceBet(this::handlePlaceBet);

        marketsView.setOnWalletClick(() -> {
            loadWallet();
            refreshBalance();
            switchToScene(walletScene);
        });
        marketsView.setOnHistoryClick(() -> {
            loadHistory();
            refreshBalance();
            switchToScene(historyScene);
        });
        detailView.setOnWalletClick(() -> {
            loadWallet();
            refreshBalance();
            switchToScene(walletScene);
        });
        detailView.setOnHistoryClick(() -> {
            loadHistory();
            refreshBalance();
            switchToScene(historyScene);
        });

        walletView.setOnBack(() -> {
            loadMarkets();
            refreshBalance();
            switchToScene(marketsScene);
        });
        walletView.setOnPortfolioClick(() -> {
            loadPortfolio();
            refreshBalance();
            switchToScene(portfolioScene);
        });
        walletView.setOnHistoryClick(() -> {
            loadHistory();
            refreshBalance();
            switchToScene(historyScene);
        });
        walletView.setOnDeposit(this::handleDeposit);
        walletView.setOnWithdraw(this::handleWithdraw);

        Runnable logoutHandler = this::doLogout;
        marketsView.setOnLogout(logoutHandler);
        portfolioView.setOnLogout(logoutHandler);
        historyView.setOnLogout(logoutHandler);
        walletView.setOnLogout(logoutHandler);
        detailView.setOnLogout(logoutHandler);
    }

    private void doLogout() {
        if (uiPollingTimeline != null) {
            uiPollingTimeline.stop();
        }
        if (polymarketSyncService != null) {
            polymarketSyncService.stop();
        }
        currentUserId = null;
        marketsView.setCurrentUserId(null);
        detailView.setCurrentUserId(null);
        portfolioView.setCurrentUserId(null);
        historyView.setCurrentUserId(null);
        if (authModule != null) {
            authModule.showLogin();
        }
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
            portfolioView.setBalance(total);
            historyView.setBalance(total);
            walletView.setBalance(total);
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
            List<events> markets = marketService.getOpenMarkets();
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

            if (event != null && "POLYMARKET".equals(event.getSource()) && clobClient != null) {
                outcomes yesOutcome = null;
                outcomes noOutcome = null;
                for (outcomes o : outcomes) {
                    if ("YES".equalsIgnoreCase(o.getLabel())) yesOutcome = o;
                    if ("NO".equalsIgnoreCase(o.getLabel())) noOutcome = o;
                }
                final outcomes finalYes = yesOutcome;
                final outcomes finalNo = noOutcome;

                if ((finalYes != null && finalYes.getPolymarketTokenId() != null && !finalYes.getPolymarketTokenId().isBlank()) ||
                    (finalNo != null && finalNo.getPolymarketTokenId() != null && !finalNo.getPolymarketTokenId().isBlank())) {
                    javafx.concurrent.Task<Void> historyTask = new javafx.concurrent.Task<>() {
                        @Override
                        protected Void call() {
                            try {
                                java.util.List<Double> yesPrices = new java.util.ArrayList<>();
                                java.util.List<Double> noPrices = new java.util.ArrayList<>();

                                long nowSec = System.currentTimeMillis() / 1000;
                                long sevenDaysAgoSec = nowSec - (7L * 24 * 60 * 60);
                                final int MAX_POINTS = 200;

                                // Fetch YES price history
                                if (finalYes != null && finalYes.getPolymarketTokenId() != null && !finalYes.getPolymarketTokenId().isBlank()) {
                                    String yesTokenId = finalYes.getPolymarketTokenId();
                                    System.out.println("[PriceHistory] Fetching YES history for tokenId=" + yesTokenId + " last-7d");
                                    var yesResponse = clobClient.getPriceHistory(yesTokenId, "max", sevenDaysAgoSec, nowSec);
                                    if (yesResponse != null && yesResponse.getHistory() != null) {
                                        for (var point : yesResponse.getHistory()) {
                                            String pVal = point.getP();
                                            if (pVal == null || pVal.isBlank()) continue;
                                            try {
                                                double yesPrice = Double.parseDouble(pVal);
                                                yesPrices.add(yesPrice);
                                                noPrices.add(Math.max(0.0, Math.min(1.0, 1.0 - yesPrice)));
                                            } catch (NumberFormatException ignored) {}
                                        }
                                        System.out.println("[PriceHistory] YES history fetched raw: " + yesPrices.size() + " points for token " + yesTokenId);
                                    }
                                }

                                // If no YES data, try fetching NO price history and invert
                                if (yesPrices.isEmpty() && finalNo != null && finalNo.getPolymarketTokenId() != null && !finalNo.getPolymarketTokenId().isBlank()) {
                                    String noTokenId = finalNo.getPolymarketTokenId();
                                    System.out.println("[PriceHistory] Fetching NO history for tokenId=" + noTokenId + " last-7d");
                                    var noResponse = clobClient.getPriceHistory(noTokenId, "max", sevenDaysAgoSec, nowSec);
                                    if (noResponse != null && noResponse.getHistory() != null) {
                                        for (var point : noResponse.getHistory()) {
                                            String pVal = point.getP();
                                            if (pVal == null || pVal.isBlank()) continue;
                                            try {
                                                double noPrice = Double.parseDouble(pVal);
                                                noPrices.add(noPrice);
                                                yesPrices.add(Math.max(0.0, Math.min(1.0, 1.0 - noPrice)));
                                            } catch (NumberFormatException ignored) {}
                                        }
                                        System.out.println("[PriceHistory] NO history fetched raw: " + noPrices.size() + " points for token " + noTokenId);
                                    }
                                }

                                // Downsample to MAX_POINTS if too many for chart clarity
                                if (yesPrices.size() > MAX_POINTS) {
                                    java.util.List<Double> sampledYes = new java.util.ArrayList<>(MAX_POINTS);
                                    java.util.List<Double> sampledNo = new java.util.ArrayList<>(MAX_POINTS);
                                    double step = (double) (yesPrices.size() - 1) / (MAX_POINTS - 1);
                                    for (int i = 0; i < MAX_POINTS; i++) {
                                        int idx = (int) Math.round(i * step);
                                        sampledYes.add(yesPrices.get(idx));
                                        sampledNo.add(noPrices.get(idx));
                                    }
                                    yesPrices = sampledYes;
                                    noPrices = sampledNo;
                                    System.out.println("[PriceHistory] Downsampled to " + yesPrices.size() + " points");
                                }

                                if (!yesPrices.isEmpty()) {
                                    detailView.updatePriceHistoryFromApi(yesPrices, noPrices);
                                } else {
                                    System.out.println("[PriceHistory] No valid price history data for market conditionId=" + event.getPolymarketConditionId());
                                }
                            } catch (Exception e) {
                                System.err.println("Price history fetch failed: " + e.getMessage());
                            }
                            return null;
                        }
                    };
                    new Thread(historyTask).start();
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading market detail: " + ex.getMessage());
        }
    }

    private void loadPortfolio() {
        if (currentUserId == null) return;
        try {
            betsDao betsDao = new betsDao();
            List<bets> userBets = betsDao.findByUserId(currentUserId.intValue());

            outcomesDao outcomeDao = new outcomesDao();
            eventsDao eventsDao = new eventsDao();

            Map<Long, events> eventsMap = new HashMap<>();
            Map<Long, outcomes> outcomesMap = new HashMap<>();

            for (bets bet : userBets) {
                outcomes outcome = outcomeDao.findById((long) bet.getOutcome_id());
                if (outcome != null) {
                    outcomesMap.put((long) bet.getOutcome_id(), outcome);
                    events event = eventsDao.findById(outcome.getEventId());
                    if (event != null) {
                        eventsMap.put((long) bet.getOutcome_id(), event);
                    }
                }
            }

            portfolioView.setBets(userBets, eventsMap, outcomesMap);
        } catch (Exception ex) {
            System.err.println("Error loading portfolio: " + ex.getMessage());
        }
    }

    private void loadHistory() {
        if (currentUserId == null) return;
        try {
            betsDao betsDao = new betsDao();
            List<bets> userBets = betsDao.findByUserId(currentUserId.intValue());
            historyView.setBetStats(userBets.size());

            var txs = walletService.getTransactionHistory(currentUserId);
            historyView.setTransactions(txs);
        } catch (Exception ex) {
            System.err.println("Error loading history: " + ex.getMessage());
        }
    }

    private void handleDeposit(double amount) {
        if (currentUserId == null) return;
        if (amount <= 0) {
            showAlert(Alert.AlertType.ERROR, "Deposit failed", "Invalid amount.");
            return;
        }

        long userId = currentUserId;
        new Thread(() -> {
            try {
                StripePaymentService.CheckoutHandle h = paymentService.createCheckout(userId, amount);
                long txId = transactionService.createPendingDeposit(userId, amount, h.sessionId());
                openInBrowser(h.hostedUrl());
                javafx.application.Platform.runLater(() ->
                    showAlert(Alert.AlertType.INFORMATION, "Deposit started",
                        "Complete the payment in your browser. We'll confirm it automatically."));
                pollPaymentStatus(h.sessionId(), txId, userId, amount);
            } catch (StripeException | SQLException ex) {
                javafx.application.Platform.runLater(() ->
                    showAlert(Alert.AlertType.ERROR, "Deposit failed", ex.getMessage()));
            }
        }, "wallet-deposit-start").start();
    }

    private void pollPaymentStatus(String sessionId, long txId, long userId, double amount) {
        cancelCurrentDepositPoll();
        currentDepositPoll = depositScheduler.scheduleAtFixedRate(() -> {
            try {
                StripePaymentService.CheckoutResult res = paymentService.fetchStatus(sessionId);
                if ("paid".equals(res.status())) {
                    transactionService.confirmDeposit(txId, userId, amount, res.paymentMethodType());
                    cancelCurrentDepositPoll();
                    javafx.application.Platform.runLater(() -> {
                        refreshBalance();
                        loadWallet();
                        showAlert(Alert.AlertType.INFORMATION, "Deposit confirmed",
                            "Your deposit of " + amount + " $NVB has been credited.");
                    });
                }
            } catch (StripeException | SQLException ex) {
                javafx.application.Platform.runLater(() ->
                    showAlert(Alert.AlertType.ERROR, "Deposit polling error", ex.getMessage()));
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    private void openInBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
            }
        } catch (Exception e) {
            System.err.println("Cannot open browser: " + e.getMessage());
        }
    }

    private void cancelCurrentDepositPoll() {
        if (currentDepositPoll != null && !currentDepositPoll.isDone()) {
            currentDepositPoll.cancel(false);
        }
    }

    private void handleWithdraw(double amount) {
        if (walletService == null || currentUserId == null) return;
openOneTimeOffer(amount);
    }

    private void openOneTimeOffer(double amount) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/polymarket/oto/OneTimeOfferView.fxml")
            );
            Parent oto = loader.load();
            OneTimeOfferController controller = loader.getController();
            controller.setAmount(amount);
            controller.setUserId(currentUserId != null ? currentUserId : 1L);
            controller.setOnClose(() -> {
                try {
                    walletService.withdraw(currentUserId, amount);
                } catch (Exception ex) {
                    System.err.println("Withdraw error: " + ex.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Withdraw failed", ex.getMessage());
                }
                refreshBalance();
                loadWallet();
                switchToScene(walletScene);
            });
            controller.setOnPlayInCasino(() -> {
                refreshBalance();
                loadWallet();
                openCasinoLobby();
            });
            switchToScene(createScene(oto));
        } catch (Exception e) {
            System.err.println("Cannot open OTO: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open offer: " + e.getMessage());
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

    private void switchToScene(Scene scene) {
        primaryStage.setScene(scene);
        javafx.application.Platform.runLater(() -> {
            if (scene != null && scene.getRoot() != null) {
                scene.getRoot().applyCss();
                scene.getRoot().requestLayout();
            }
        });
    }

    private void loadFonts() {
        try {
            Font.loadFont(
                getClass().getResourceAsStream("/com/polymarket/fonts/InstrumentSerif-Regular.ttf"),
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

    private void openCasinoLobby() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/polymarket/casino/lobby/CasinoLobbyView.fxml")
            );
            Parent lobby = loader.load();
            com.polymarket.casino.lobby.CasinoLobbyController controller = loader.getController();
            if (currentUserId != null) controller.setCurrentUserId(currentUserId);
            controller.setOnBack(() -> {
                loadMarkets();
                refreshBalance();
                switchToScene(marketsScene);
            });
            controller.setOnLaunchCrashGame(() -> openCrashGame());
            switchToScene(createScene(lobby));
        } catch (Exception e) {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setHeaderText("Cannot open casino");
            err.setContentText(e.getMessage());
            err.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        if (uiPollingTimeline != null) {
            uiPollingTimeline.stop();
        }
        if (polymarketSyncService != null) {
            polymarketSyncService.stop();
        }
        if (depositScheduler != null) {
            depositScheduler.shutdownNow();
        }
    }

    private void openCrashGame() {
        try {
            com.polymarket.casino.crash.CrashGameView crashView =
                    new com.polymarket.casino.crash.CrashGameView(
                            currentUserId != null ? currentUserId : 1L,
                            this::openCasinoLobby
                    );
            switchToScene(createScene(crashView.getView()));
        } catch (Exception e) {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setHeaderText("Cannot open Crash Rocket");
            err.setContentText(e.getMessage());
            err.showAndWait();
        }
    }
}