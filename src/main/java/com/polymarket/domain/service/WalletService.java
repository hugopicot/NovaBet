package com.polymarket.domain.service;

import com.polymarket.model.transactions;
import com.polymarket.model.wallets;

import java.util.List;

public interface WalletService {
    void deposit(Long userId, double amount);
    void withdraw(Long userId, double amount);
    wallets getWallet(Long userId);
    List<transactions> getTransactionHistory(Long userId);
}
