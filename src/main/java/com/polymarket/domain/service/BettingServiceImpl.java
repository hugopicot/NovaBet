package com.polymarket.domain.service;

import com.polymarket.dao.betsDao;
import com.polymarket.dao.eventsDao;
import com.polymarket.dao.outcomesDao;
import com.polymarket.dao.transactionsDao;
import com.polymarket.dao.walletsDao;
import com.polymarket.domain.dto.BetRequest;
import com.polymarket.domain.dto.BetResult;
import com.polymarket.domain.dto.OutcomeLabel;
import com.polymarket.domain.exception.InsufficientFundsException;
import com.polymarket.domain.exception.InvalidBetException;
import com.polymarket.domain.exception.MarketNotOpenException;
import com.polymarket.domain.exception.OutcomeNotFoundException;
import com.polymarket.model.bets;
import com.polymarket.model.events;
import com.polymarket.model.outcomes;
import com.polymarket.model.transactions;
import com.polymarket.model.wallets;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.Instant;

public class BettingServiceImpl implements BettingService {

    private static final BigDecimal SHARE_PAYOUT = BigDecimal.ONE;
    private static final BigDecimal MIN_BET_AMOUNT = new BigDecimal("0.01");
    private static final int SHARE_PRICE_SCALE = 4;

    private final outcomesDao outcomeDao;
    private final eventsDao eventDao;
    private final walletsDao walletDao;
    private final betsDao betDao;
    private final transactionsDao transactionDao;

    public BettingServiceImpl(
        outcomesDao outcomeDao,
        eventsDao eventDao,
        walletsDao walletDao,
        betsDao betDao,
        transactionsDao transactionDao
    ) throws SQLException {
        this.outcomeDao = outcomeDao;
        this.eventDao = eventDao;
        this.walletDao = walletDao;
        this.betDao = betDao;
        this.transactionDao = transactionDao;
    }

    @Override
    public BetResult buyShares(BetRequest request) {
        validateBetRequest(request);

        events event = getAndValidateEvent(request.eventId());
        outcomes outcome = getAndValidateOutcome(request.eventId(), request.outcome());

        BigDecimal sharePrice = calculateSharePriceFromOdds(BigDecimal.valueOf(outcome.getOdds()), request.outcome());
        int shareCount = calculateShareCount(request.amount(), sharePrice);
        BigDecimal totalCost = sharePrice.multiply(BigDecimal.valueOf(shareCount))
            .setScale(2, RoundingMode.HALF_UP);

        wallets wallet = getAndValidateWallet(request.userId(), totalCost);

        deductFromWallet(wallet, totalCost, request.useVirtualBalance());

        BigDecimal potentialWin = calculatePotentialWin(shareCount);

        bets bet = new bets(
            (int) request.userId(),
            outcome.getId().intValue(),
            totalCost.intValue(),
            potentialWin.intValue()
        );
        betDao.add(bet);

        transactionDao.add(new transactions(
            request.userId(),
            "BET_PLACED",
            totalCost.negate().doubleValue(),
            Instant.now().toString()
        ));

        wallets updatedWallet = walletDao.findById(wallet.getId());

        return new BetResult(
            bet,
            sharePrice,
            shareCount,
            totalCost,
            BigDecimal.valueOf(updatedWallet.getRealBalance() + updatedWallet.getVirtualBalance())
        );
    }

    @Override
    public BigDecimal calculateSharePrice(double yesProbability, OutcomeLabel outcome) {
        if (yesProbability < 0.0 || yesProbability > 1.0) {
            throw new InvalidBetException("Probability must be between 0 and 1");
        }

        BigDecimal price;
        if (outcome == OutcomeLabel.YES) {
            price = BigDecimal.valueOf(yesProbability);
        } else {
            price = BigDecimal.valueOf(1.0 - yesProbability);
        }

        return price.setScale(SHARE_PRICE_SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public int calculateShareCount(BigDecimal amount, BigDecimal sharePrice) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidBetException("Amount must be greater than zero");
        }
        if (sharePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidBetException("Share price must be greater than zero");
        }

        return amount.divide(sharePrice, SHARE_PRICE_SCALE, RoundingMode.DOWN).intValue();
    }

    @Override
    public BigDecimal calculatePotentialWin(int shareCount) {
        if (shareCount <= 0) {
            throw new InvalidBetException("Share count must be greater than zero");
        }
        return SHARE_PAYOUT.multiply(BigDecimal.valueOf(shareCount))
            .setScale(2, RoundingMode.HALF_UP);
    }

    private void validateBetRequest(BetRequest request) {
        if (request.userId() <= 0) {
            throw new InvalidBetException("Invalid user ID");
        }
        if (request.eventId() <= 0) {
            throw new InvalidBetException("Invalid event ID");
        }
        if (request.amount().compareTo(MIN_BET_AMOUNT) < 0) {
            throw new InvalidBetException(
                "Bet amount must be at least " + MIN_BET_AMOUNT
            );
        }
    }

    private events getAndValidateEvent(long eventId) {
        events event = eventDao.findById(eventId);
        if (event == null) {
            throw new InvalidBetException("Event not found: " + eventId);
        }

        if (!"OPEN".equals(event.getStatus())) {
            throw new MarketNotOpenException(
                "Market is not open: " + event.getTitle() + " (status: " + event.getStatus() + ")"
            );
        }

        return event;
    }

    private outcomes getAndValidateOutcome(long eventId, OutcomeLabel label) {
        outcomes outcome = outcomeDao.findById(eventId);
        if (outcome == null) {
            throw new OutcomeNotFoundException(
                "Outcome " + label + " not found for event: " + eventId
            );
        }

        return outcome;
    }

    private wallets getAndValidateWallet(long userId, BigDecimal requiredAmount) {
        wallets wallet = walletDao.findById(userId);
        if (wallet == null) {
            throw new InsufficientFundsException(
                "Wallet not found for user: " + userId
            );
        }

        BigDecimal availableBalance = BigDecimal.valueOf(wallet.getRealBalance() + wallet.getVirtualBalance());
        if (availableBalance.compareTo(requiredAmount) < 0) {
            throw new InsufficientFundsException(
                "Insufficient funds. Required: " + requiredAmount +
                ", Available: " + availableBalance
            );
        }

        return wallet;
    }

    private void deductFromWallet(wallets wallet, BigDecimal amount, boolean useVirtualFirst) {
        double amountDouble = amount.doubleValue();
        if (useVirtualFirst) {
            double virtual = wallet.getVirtualBalance();
            if (virtual >= amountDouble) {
                wallet.setVirtualBalance(virtual - amountDouble);
            } else {
                wallet.setVirtualBalance(0);
                wallet.setRealBalance(wallet.getRealBalance() - (amountDouble - virtual));
            }
        } else {
            wallet.setRealBalance(wallet.getRealBalance() - amountDouble);
        }
        walletDao.update(wallet);
    }

    private BigDecimal calculateSharePriceFromOdds(BigDecimal odds, OutcomeLabel outcome) {
        BigDecimal probability = BigDecimal.ONE.divide(odds, 10, RoundingMode.HALF_UP);

        if (probability.compareTo(BigDecimal.ZERO) < 0 || probability.compareTo(BigDecimal.ONE) > 0) {
            throw new InvalidBetException("Invalid odds resulting in probability outside [0,1]: " + odds);
        }

        return probability.setScale(SHARE_PRICE_SCALE, RoundingMode.HALF_UP);
    }
}
