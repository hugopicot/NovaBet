package com.polymarket.domain.service;

import com.polymarket.dao.transactionsDao;
import com.polymarket.dao.walletsDao;
import com.polymarket.domain.exception.InsufficientFundsException;
import com.polymarket.domain.exception.InvalidAmountException;
import com.polymarket.model.wallets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WalletServiceImplTest {

    private walletsDao walletDao;
    private transactionsDao transactionDao;
    private WalletServiceImpl walletService;

    @BeforeEach
    void setUp() throws SQLException {

        walletDao = mock(walletsDao.class);
        transactionDao = mock(transactionsDao.class);

        walletService = new WalletServiceImpl(walletDao, transactionDao);
    }

    @Test
    void testDepositSuccess() {

        wallets wallet = new wallets();
        wallet.setVirtualBalance(100);

        when(walletDao.findByUserId(1L)).thenReturn(wallet);

        walletService.deposit(1L, 50);

        assertEquals(150, wallet.getVirtualBalance());
    }

    @Test
    void testWithdrawInsufficientFunds() {

        wallets wallet = new wallets();
        wallet.setVirtualBalance(20);

        when(walletDao.findByUserId(1L)).thenReturn(wallet);

        assertThrows(
                InsufficientFundsException.class,
                () -> walletService.withdraw(1L, 100)
        );
    }

    @Test
    void testInvalidDepositAmount() {

        assertThrows(
                InvalidAmountException.class,
                () -> walletService.deposit(1L, -10)
        );
    }
}