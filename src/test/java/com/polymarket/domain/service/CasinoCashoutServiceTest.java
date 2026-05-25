package com.polymarket.domain.service;

import com.polymarket.dao.WalletRepository;
import com.polymarket.dao.WalletSnapshot;
import com.polymarket.domain.exception.CasinoCashoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CasinoCashoutServiceTest {

    private static final long USER_ID = 7L;

    @Mock private Connection connection;
    @Mock private WalletRepository walletRepository;
    @Mock private WageringService wageringService;

    private CasinoCashoutService service;

    @BeforeEach
    void setUp() {
        service = new CasinoCashoutService(connection, walletRepository, wageringService);
    }

    @Test
    void cashoutAll_throws_whenWageringNotMet() throws SQLException {
        when(wageringService.canCashout(USER_ID)).thenReturn(false);

        assertThrows(CasinoCashoutException.class, () -> service.cashoutAll(USER_ID));

        verify(walletRepository, never()).debitVirtual(anyLong(), anyDouble());
    }

    @Test
    void cashoutAll_throws_whenWalletNotFound() {
        when(wageringService.canCashout(USER_ID)).thenReturn(true);
        when(walletRepository.findByUserId(USER_ID)).thenReturn(null);

        assertThrows(CasinoCashoutException.class, () -> service.cashoutAll(USER_ID));
    }

    @Test
    void cashoutAll_commitsTransaction_onSuccess() throws SQLException {
        when(wageringService.canCashout(USER_ID)).thenReturn(true);
        when(walletRepository.findByUserId(USER_ID))
                .thenReturn(new WalletSnapshot(USER_ID, 120.0, 200.0));
        when(walletRepository.debitVirtual(USER_ID, 120.0)).thenReturn(1);
        when(walletRepository.creditReal(USER_ID, 120.0)).thenReturn(1);
        when(walletRepository.resetWagered(USER_ID)).thenReturn(1);

        double transferred = service.cashoutAll(USER_ID);

        assertEquals(120.0, transferred);
        verify(connection).setAutoCommit(false);
        verify(walletRepository).debitVirtual(USER_ID, 120.0);
        verify(walletRepository).creditReal(USER_ID, 120.0);
        verify(walletRepository).resetWagered(USER_ID);
        verify(connection).commit();
        verify(connection, never()).rollback();
        verify(connection).setAutoCommit(true);
    }

    @Test
    void cashoutAll_rollbacks_whenDebitFails() throws SQLException {
        when(wageringService.canCashout(USER_ID)).thenReturn(true);
        when(walletRepository.findByUserId(USER_ID))
                .thenReturn(new WalletSnapshot(USER_ID, 50.0, 80.0));
        when(walletRepository.debitVirtual(USER_ID, 50.0)).thenReturn(0);

        assertThrows(CasinoCashoutException.class, () -> service.cashoutAll(USER_ID));

        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(walletRepository, never()).creditReal(anyLong(), anyDouble());
        verify(walletRepository, never()).resetWagered(anyLong());
    }

    @Test
    void cashoutAll_rollbacks_whenCreditFails() throws SQLException {
        when(wageringService.canCashout(USER_ID)).thenReturn(true);
        when(walletRepository.findByUserId(USER_ID))
                .thenReturn(new WalletSnapshot(USER_ID, 50.0, 80.0));
        when(walletRepository.debitVirtual(USER_ID, 50.0)).thenReturn(1);
        when(walletRepository.creditReal(USER_ID, 50.0)).thenReturn(0);

        assertThrows(CasinoCashoutException.class, () -> service.cashoutAll(USER_ID));

        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(walletRepository, never()).resetWagered(anyLong());
    }

    @Test
    void cashoutAll_rollbacks_whenResetFails() throws SQLException {
        when(wageringService.canCashout(USER_ID)).thenReturn(true);
        when(walletRepository.findByUserId(USER_ID))
                .thenReturn(new WalletSnapshot(USER_ID, 50.0, 80.0));
        when(walletRepository.debitVirtual(USER_ID, 50.0)).thenReturn(1);
        when(walletRepository.creditReal(USER_ID, 50.0)).thenReturn(1);
        when(walletRepository.resetWagered(USER_ID)).thenReturn(0);

        assertThrows(CasinoCashoutException.class, () -> service.cashoutAll(USER_ID));

        verify(connection).rollback();
        verify(connection, never()).commit();
    }

    @Test
    void cashoutAll_rollbacks_onSQLException() throws SQLException {
        when(wageringService.canCashout(USER_ID)).thenReturn(true);
        when(walletRepository.findByUserId(USER_ID))
                .thenReturn(new WalletSnapshot(USER_ID, 50.0, 80.0));
        when(walletRepository.debitVirtual(USER_ID, 50.0))
                .thenThrow(new SQLException("DB down"));

        assertThrows(CasinoCashoutException.class, () -> service.cashoutAll(USER_ID));

        verify(connection).rollback();
        verify(connection, never()).commit();
    }
}
