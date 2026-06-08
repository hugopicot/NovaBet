package com.polymarket.service;

import com.polymarket.dao.game_sessionsDao;

import com.polymarket.model.game_sessions;

import java.sql.SQLException;
import java.util.Random;

public class SlotMachineService {

    public static final String[] SYMBOLS = {

            "💎", "7️⃣", "🍒", "🍋", "🔔", "⭐", "🍀"

    };

    private final Random random;

    private final WalletService walletService;

    private final game_sessionsDao gameSessionDAO;

    public SlotMachineService() {

        this.random = new Random();

        this.walletService = new WalletService();

        try {
            this.gameSessionDAO = new game_sessionsDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public SpinResult spin(int userId, double betAmount) {

        if (!walletService.placeBet(userId, betAmount)) {

            return null;

        }

        // 🎲 génération des reels

        int[] results = new int[3];

        for (int i = 0; i < 3; i++) {

            results[i] = random.nextInt(SYMBOLS.length);

        }

        double winAmount = calculateWinnings(results, betAmount);

        String status = winAmount > 0 ? "WIN" : "LOSS";

        if (winAmount > 0) {

            walletService.addWinnings(userId, winAmount);

        }

        try {

            // ⚠️ ON UTILISE TON CONSTRUCTEUR COMPLET

            game_sessions session = new game_sessions(

                    null,          // id auto (DB)

                    (long) userId,

                    1L,            // gameId slot machine

                    betAmount,

                    status,

                    winAmount,

                    null           // created_at DB

            );

            gameSessionDAO.add(session);

        } catch (Exception e) {

            e.printStackTrace();

        }

        return new SpinResult(results, winAmount, getSymbols(results));

    }

    private double calculateWinnings(int[] results, double bet) {

        if (results[0] == results[1] && results[1] == results[2]) {

            return bet * 50;

        }

        if (results[0] == results[1] ||

                results[1] == results[2] ||

                results[0] == results[2]) {

            return bet * 2;

        }

        return 0;

    }

    private String[] getSymbols(int[] indices) {

        String[] symbols = new String[3];

        for (int i = 0; i < 3; i++) {

            symbols[i] = SYMBOLS[indices[i]];

        }

        return symbols;

    }

    public static class SpinResult {

        public final int[] indices;

        public final double winAmount;

        public final String[] symbols;

        public SpinResult(int[] indices, double winAmount, String[] symbols) {

            this.indices = indices;

            this.winAmount = winAmount;

            this.symbols = symbols;

        }

        public boolean isWin() {

            return winAmount > 0;

        }

    }

}