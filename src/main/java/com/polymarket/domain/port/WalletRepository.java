package com.polymarket.domain.port;

import com.polymarket.domain.model.Wallet;

import java.math.BigDecimal;
import java.util.Optional;

public interface WalletRepository {
    Optional<Wallet> findByUserId(long userId);

    Wallet updateBalances(long walletId, BigDecimal realBalance, BigDecimal virtualBalance);

    Wallet deductFromBalance(long walletId, BigDecimal amount, boolean useVirtualFirst);

    Wallet addToBalance(long walletId, BigDecimal amount, boolean isVirtual);
}
