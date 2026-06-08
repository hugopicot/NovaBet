package com.polymarket.service;

import com.polymarket.dao.walletsDao;

import com.polymarket.dao.transactionsDao;

import com.polymarket.model.wallets;

import java.sql.SQLException;

public class WalletService {

    private walletsDao walletDAO;

    private transactionsDao transactionDAO;

    public WalletService() {

        this.walletDAO = new walletsDao();

        this.transactionDAO = new transactionsDao();

    }

    // 🔹 Récupérer wallet utilisateur

    public wallets getWallet(int userId) {

        return walletDAO.findByUserId((long) userId);

    }

    // 🔹 Placer un pari

    public boolean placeBet(int userId, double amount) {

        wallets wallet = walletDAO.findByUserId((long) userId);

        if (wallet == null) return false;

        double virtualBalance = wallet.getVirtualBalance();

        double realBalance = wallet.getRealBalance();

        // ✔️ 1. utiliser solde virtuel d'abord

        if (virtualBalance >= amount) {

            wallet.setVirtualBalance(virtualBalance - amount);

            // ✔️ 2. sinon mix virtuel + réel

        } else if (virtualBalance + realBalance >= amount) {

            double remaining = amount - virtualBalance;

            wallet.setVirtualBalance(0);

            wallet.setRealBalance(realBalance - remaining);

        } else {

            return false; // ❌ solde insuffisant

        }

        // ✔️ update wallet

        walletDAO.update(wallet);

        // ✔️ transaction (si ta méthode existe)

        // transactionDAO.create(new transactions(userId, "BET", -amount));

        return true;

    }

    // 🔹 Ajouter gains

    public void addWinnings(int userId, double amount) {

        wallets wallet = walletDAO.findByUserId((long) userId);

        if (wallet == null) return;

        wallet.setVirtualBalance(wallet.getVirtualBalance() + amount);

        walletDAO.update(wallet);

        // transactionDAO.create(new transactions(userId, "WIN", amount));

    }

}