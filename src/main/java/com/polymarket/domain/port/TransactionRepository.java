package com.polymarket.domain.port;

import com.polymarket.domain.model.Transaction;
import com.polymarket.domain.model.TransactionType;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionRepository {
    Transaction save(long userId, TransactionType type, BigDecimal amount);

    List<Transaction> findByUserId(long userId);
}
