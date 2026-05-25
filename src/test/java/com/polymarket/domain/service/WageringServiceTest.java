package com.polymarket.domain.service;

import com.polymarket.dao.WalletRepository;
import com.polymarket.dao.WalletSnapshot;
import com.polymarket.domain.exception.WageringServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WageringServiceTest {

    private static final long USER_ID = 42L;

    @Mock private Connection connection;
    @Mock private WalletRepository walletRepository;

    private WageringService service;

    @BeforeEach
    void setUp() {
        service = new WageringService(connection, walletRepository);
    }

    // ── canCashout ───────────────────────────────────────────

    @Test
    void canCashout_returnsFalse_whenWalletNotFound() {
        when(walletRepository.findByUserId(USER_ID)).thenReturn(null);
        assertFalse(service.canCashout(USER_ID));
    }

    @Test
    void canCashout_returnsFalse_whenVirtualBalanceIsZero() {
        when(walletRepository.findByUserId(USER_ID))
                .thenReturn(new WalletSnapshot(USER_ID, 0.0, 100.0));
        assertFalse(service.canCashout(USER_ID));
    }

    @Test
    void canCashout_returnsFalse_whenWagerInsufficient() {
        when(walletRepository.findByUserId(USER_ID))
                .thenReturn(new WalletSnapshot(USER_ID, 50.0, 30.0));
        assertFalse(service.canCashout(USER_ID));
    }

    @Test
    void canCashout_returnsTrue_whenWagerEqualsBalance() {
        when(walletRepository.findByUserId(USER_ID))
                .thenReturn(new WalletSnapshot(USER_ID, 50.0, 50.0));
        assertTrue(service.canCashout(USER_ID));
    }

    @Test
    void canCashout_returnsTrue_whenWagerExceedsBalance() {
        when(walletRepository.findByUserId(USER_ID))
                .thenReturn(new WalletSnapshot(USER_ID, 50.0, 75.0));
        assertTrue(service.canCashout(USER_ID));
    }

    // ── recordBet (validation) ───────────────────────────────

    @Test
    void recordBet_throws_whenAmountIsZero() throws SQLException {
        assertThrows(WageringServiceException.class,
                () -> service.recordBet(USER_ID, 0.0));
        verify(walletRepository, never()).debitVirtual(anyLong(), anyDouble());
    }

    @Test
    void recordBet_throws_whenAmountIsNegative() throws SQLException {
        assertThrows(WageringServiceException.class,
                () -> service.recordBet(USER_ID, -10.0));
        verify(walletRepository, never()).debitVirtual(anyLong(), anyDouble());
    }

    @Test
    void recordBet_throws_whenWalletNotFound() {
        when(walletRepository.findByUserId(USER_ID)).thenReturn(null);
        assertThrows(WageringServiceException.class,
                () -> service.recordBet(USER_ID, 10.0));
    }

    @Test
    void recordBet_throws_whenInsufficientCredits() {
        when(walletRepository.findByUserId(USER_ID))
                .thenReturn(new WalletSnapshot(USER_ID, 5.0, 0.0));
        assertThrows(WageringServiceException.class,
                () -> service.recordBet(USER_ID, 10.0));
    }

    // ── recordBet (transaction) ──────────────────────────────

    @Test
    void recordBet_commitsTransaction_onSuccess() throws SQLException {
        when(walletRepository.findByUserId(USER_ID))
                .thenReturn(new WalletSnapshot(USER_ID, 100.0, 0.0));
        when(walletRepository.debitVirtual(USER_ID, 25.0)).thenReturn(1);
        when(walletRepository.incrementWagered(USER_ID, 25.0)).thenReturn(1);

        service.recordBet(USER_ID, 25.0);

        verify(connection).setAutoCommit(false);
        verify(walletRepository).debitVirtual(USER_ID, 25.0);
        verify(walletRepository).incrementWagered(USER_ID, 25.0);
        verify(connection).commit();
        verify(connection, never()).rollback();
        verify(connection).setAutoCommit(true);
    }

    @Test
    void recordBet_rollbacks_whenDebitFails() throws SQLException {
        when(walletRepository.findByUserId(USER_ID))
                .thenReturn(new WalletSnapshot(USER_ID, 100.0, 0.0));
        when(walletRepository.debitVirtual(USER_ID, 25.0)).thenReturn(0);

        assertThrows(WageringServiceException.class,
                () -> service.recordBet(USER_ID, 25.0));

        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(walletRepository, never()).incrementWagered(anyLong(), anyDouble());
    }

    @Test
    void recordBet_rollbacks_whenIncrementFails() throws SQLException {
        when(walletRepository.findByUserId(USER_ID))
                .thenReturn(new WalletSnapshot(USER_ID, 100.0, 0.0));
        when(walletRepository.debitVirtual(USER_ID, 25.0)).thenReturn(1);
        when(walletRepository.incrementWagered(USER_ID, 25.0)).thenReturn(0);

        assertThrows(WageringServiceException.class,
                () -> service.recordBet(USER_ID, 25.0));

        verify(connection).rollback();
        verify(connection, never()).commit();
    }

    @Test
    void recordBet_rollbacks_whenSQLExceptionThrown() throws SQLException {
        when(walletRepository.findByUserId(USER_ID))
                .thenReturn(new WalletSnapshot(USER_ID, 100.0, 0.0));
        when(walletRepository.debitVirtual(USER_ID, 25.0))
                .thenThrow(new SQLException("DB down"));

        assertThrows(WageringServiceException.class,
                () -> service.recordBet(USER_ID, 25.0));

        verify(connection).rollback();
        verify(connection, never()).commit();
    }

    // ── recordWin ────────────────────────────────────────────

    @Test
    void recordWin_throws_whenAmountIsZero() {
        assertThrows(WageringServiceException.class,
                () -> service.recordWin(USER_ID, 0.0));
    }

    @Test
    void recordWin_throws_whenAmountIsNegative() {
        assertThrows(WageringServiceException.class,
                () -> service.recordWin(USER_ID, -5.0));
    }

    @Test
    void recordWin_throws_whenWalletNotFound() throws SQLException {
        when(walletRepository.creditVirtual(USER_ID, 10.0)).thenReturn(0);
        assertThrows(WageringServiceException.class,
                () -> service.recordWin(USER_ID, 10.0));
    }

    @Test
    void recordWin_creditsSuccessfully() throws SQLException {
        when(walletRepository.creditVirtual(USER_ID, 10.0)).thenReturn(1);
        service.recordWin(USER_ID, 10.0);
        verify(walletRepository, times(1)).creditVirtual(USER_ID, 10.0);
    }

    @Test
    void recordWin_throws_onSQLException() throws SQLException {
        when(walletRepository.creditVirtual(USER_ID, 10.0))
                .thenThrow(new SQLException("DB down"));
        WageringServiceException ex = assertThrows(WageringServiceException.class,
                () -> service.recordWin(USER_ID, 10.0));
        assertEquals("Echec credit virtual_balance", ex.getMessage());
    }
}
