package com.polymarket.service;

import com.polymarket.dao.game_sessionsDao;
import com.polymarket.dao.walletsDao;
import com.polymarket.model.game_sessions;
import com.polymarket.model.wallets;

import java.sql.SQLException;
import java.util.Random;

public class SlotMachineService {

    public static final String[] SYMBOLS = {
            "💎", "7️⃣", "🍒", "🍋", "🔔", "⭐", "🍀"
    };

    private final Random random;
    private final walletsDao walletDAO;
    private final game_sessionsDao gameSessionDAO;

    public SlotMachineService() {
        this.random = new Random();
        try {
            this.walletDAO = new walletsDao();
            this.gameSessionDAO = new game_sessionsDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public SpinResult spin(int userId, double betAmount) {
        wallets wallet = walletDAO.findByUserId((long) userId);
        if (wallet == null) return null;

        double virtualBalance = wallet.getVirtualBalance();
        double casinoBalance = wallet.getCasinoBalance();
        double realBalance = wallet.getRealBalance();

        if (virtualBalance + casinoBalance + realBalance < betAmount) {
            return null;
        }

        double remaining = betAmount;
        // Debit casino_balance first (non-withdrawable credits)
        if (casinoBalance > 0) {
            double fromCasino = Math.min(casinoBalance, remaining);
            wallet.setCasinoBalance(casinoBalance - fromCasino);
            remaining -= fromCasino;
        }
        // Then virtual_balance
        if (remaining > 0 && virtualBalance > 0) {
            double fromVirtual = Math.min(virtualBalance, remaining);
            wallet.setVirtualBalance(virtualBalance - fromVirtual);
            remaining -= fromVirtual;
        }
        // Finally real_balance
        if (remaining > 0) {
            wallet.setRealBalance(realBalance - remaining);
        }
        walletDAO.update(wallet);

        int[] results = new int[3];
        for (int i = 0; i < 3; i++) {
            results[i] = random.nextInt(SYMBOLS.length);
        }

        double winAmount = calculateWinnings(results, betAmount);

        if (winAmount > 0) {
            wallet.setVirtualBalance(wallet.getVirtualBalance() + winAmount);
            walletDAO.update(wallet);
        }

        String status = winAmount > 0 ? "WIN" : "LOSS";

        try {
            game_sessions session = new game_sessions(
                    null,
                    (long) userId,
                    1L,
                    betAmount,
                    status,
                    winAmount,
                    null
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
        if (results[0] == results[1] || results[1] == results[2] || results[0] == results[2]) {
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

    public wallets getWallet(long userId) {
        return walletDAO.findByUserId(userId);
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