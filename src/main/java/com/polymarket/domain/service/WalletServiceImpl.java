package com.polymarket.domain.service;

import com.polymarket.dao.transactionsDao;
import com.polymarket.dao.walletsDao;
import com.polymarket.domain.exception.InsufficientFundsException;
import com.polymarket.domain.exception.InvalidAmountException;
import com.polymarket.model.transactions;
import com.polymarket.model.wallets;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WalletServiceImpl implements WalletService {

    private final walletsDao walletDao;
    private final transactionsDao transactionDao;

    public WalletServiceImpl(walletsDao walletDao, transactionsDao transactionDao) throws SQLException {
        this.walletDao = walletDao;
        this.transactionDao = transactionDao;
    }

    @Override
    public void deposit(Long userId, double amount) {
        validateAmount(amount);

        wallets wallet = walletDao.findByUserId(userId);
        if (wallet == null) {
            throw new IllegalStateException("Wallet not found for user: " + userId);
        }

        wallet.setVirtualBalance(wallet.getVirtualBalance() + amount);
        walletDao.update(wallet);

        recordTransaction(userId, "DEPOSIT", amount);
    }

    @Override
    public void withdraw(Long userId, double amount) {
        validateAmount(amount);

        wallets wallet = walletDao.findByUserId(userId);
        if (wallet == null) {
            throw new IllegalStateException("Wallet not found for user: " + userId);
        }

        if (wallet.getVirtualBalance() < amount) {
            throw new InsufficientFundsException(
                "Insufficient virtual funds. Available: " + wallet.getVirtualBalance() +
                ", Requested: " + amount
            );
        }

        wallet.setVirtualBalance(wallet.getVirtualBalance() - amount);
        walletDao.update(wallet);

        recordTransaction(userId, "WITHDRAW", -amount);
    }

    @Override
    public wallets getWallet(Long userId) {
        return walletDao.findByUserId(userId);
    }

    @Override
    public List<transactions> getTransactionHistory(Long userId) {
        return transactionDao.findByUserId(userId);
    }

    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }
    }

    private void recordTransaction(Long userId, String type, double amount) {
        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        transactions tx = new transactions(userId, type, amount, createdAt);
        transactionDao.add(tx);
    }
}
