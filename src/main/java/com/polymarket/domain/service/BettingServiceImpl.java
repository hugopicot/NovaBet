package com.polymarket.domain.service;

import com.polymarket.domain.exception.InsufficientFundsException;
import com.polymarket.domain.exception.InvalidBetException;
import com.polymarket.domain.exception.MarketNotOpenException;
import com.polymarket.domain.exception.OutcomeNotFoundException;
import com.polymarket.domain.model.*;
import com.polymarket.domain.port.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Optional;

public class BettingServiceImpl implements BettingService {

    private static final BigDecimal SHARE_PAYOUT = BigDecimal.ONE;
    private static final BigDecimal MIN_BET_AMOUNT = new BigDecimal("0.01");
    private static final int SHARE_PRICE_SCALE = 4;

    private final OutcomeRepository outcomeRepository;
    private final EventRepository eventRepository;
    private final WalletRepository walletRepository;
    private final BetRepository betRepository;
    private final TransactionRepository transactionRepository;

    public BettingServiceImpl(
        OutcomeRepository outcomeRepository,
        EventRepository eventRepository,
        WalletRepository walletRepository,
        BetRepository betRepository,
        TransactionRepository transactionRepository
    ) {
        this.outcomeRepository = outcomeRepository;
        this.eventRepository = eventRepository;
        this.walletRepository = walletRepository;
        this.betRepository = betRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public BetResult buyShares(BetRequest request) {
        validateBetRequest(request);

        Event event = getAndValidateEvent(request.eventId());
        Outcome outcome = getAndValidateOutcome(request.eventId(), request.outcome());

        BigDecimal sharePrice = calculateSharePriceFromOdds(outcome.odds(), request.outcome());
        int shareCount = calculateShareCount(request.amount(), sharePrice);
        BigDecimal totalCost = sharePrice.multiply(BigDecimal.valueOf(shareCount))
            .setScale(2, RoundingMode.HALF_UP);

        Wallet wallet = getAndValidateWallet(request.userId(), totalCost);

        Wallet updatedWallet = walletRepository.deductFromBalance(
            wallet.id(),
            totalCost,
            request.useVirtualBalance()
        );

        BigDecimal potentialWin = calculatePotentialWin(shareCount);

        Bet bet = new Bet(
            0,
            request.userId(),
            outcome.id(),
            totalCost,
            potentialWin,
            sharePrice,
            shareCount,
            BetStatus.PENDING,
            Instant.now()
        );

        Bet savedBet = betRepository.save(bet);

        transactionRepository.save(
            request.userId(),
            TransactionType.BET_PLACED,
            totalCost.negate()
        );

        return new BetResult(
            savedBet,
            sharePrice,
            shareCount,
            totalCost,
            updatedWallet.totalBalance()
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

    private Event getAndValidateEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new InvalidBetException("Event not found: " + eventId));

        if (!event.isOpen()) {
            throw new MarketNotOpenException(
                "Market is not open: " + event.title() + " (status: " + event.status() + ")"
            );
        }

        return event;
    }

    private Outcome getAndValidateOutcome(long eventId, OutcomeLabel label) {
        Outcome outcome = outcomeRepository.findByEventIdAndLabel(eventId, label)
            .orElseThrow(() -> new OutcomeNotFoundException(
                "Outcome " + label + " not found for event: " + eventId
            ));

        return outcome;
    }

    private Wallet getAndValidateWallet(long userId, BigDecimal requiredAmount) {
        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new InsufficientFundsException(
                "Wallet not found for user: " + userId
            ));

        BigDecimal availableBalance = wallet.totalBalance();
        if (availableBalance.compareTo(requiredAmount) < 0) {
            throw new InsufficientFundsException(
                "Insufficient funds. Required: " + requiredAmount +
                ", Available: " + availableBalance
            );
        }

        return wallet;
    }

    private BigDecimal calculateSharePriceFromOdds(BigDecimal odds, OutcomeLabel outcome) {
        BigDecimal probability = BigDecimal.ONE.divide(odds, 10, RoundingMode.HALF_UP);

        if (probability.compareTo(BigDecimal.ZERO) < 0 || probability.compareTo(BigDecimal.ONE) > 0) {
            throw new InvalidBetException("Invalid odds resulting in probability outside [0,1]: " + odds);
        }

        return probability.setScale(SHARE_PRICE_SCALE, RoundingMode.HALF_UP);
    }
}
