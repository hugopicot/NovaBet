package com.polymarket.domain.service;

import com.polymarket.domain.exception.InsufficientFundsException;
import com.polymarket.domain.exception.InvalidBetException;
import com.polymarket.domain.exception.MarketNotOpenException;
import com.polymarket.domain.exception.OutcomeNotFoundException;
import com.polymarket.domain.model.*;
import com.polymarket.domain.port.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BettingServiceImplTest {

    private TestOutcomeRepository outcomeRepository;
    private TestEventRepository eventRepository;
    private TestWalletRepository walletRepository;
    private TestBetRepository betRepository;
    private TestTransactionRepository transactionRepository;
    private BettingServiceImpl bettingService;

    @BeforeEach
    void setUp() {
        outcomeRepository = new TestOutcomeRepository();
        eventRepository = new TestEventRepository();
        walletRepository = new TestWalletRepository();
        betRepository = new TestBetRepository();
        transactionRepository = new TestTransactionRepository();

        bettingService = new BettingServiceImpl(
            outcomeRepository,
            eventRepository,
            walletRepository,
            betRepository,
            transactionRepository
        );

        seedData();
    }

    private void seedData() {
        Event openEvent = new Event(1L, "Will it rain tomorrow?", "Weather prediction", EventStatus.OPEN, null, Instant.now());
        eventRepository.events.add(openEvent);

        Event resolvedEvent = new Event(2L, "Past election", "Already resolved", EventStatus.RESOLVED, "YES", Instant.now());
        eventRepository.events.add(resolvedEvent);

        Outcome yesOutcome = new Outcome(1L, 1L, OutcomeLabel.YES, new BigDecimal("1.50"));
        Outcome noOutcome = new Outcome(2L, 1L, OutcomeLabel.NO, new BigDecimal("2.50"));
        outcomeRepository.outcomes.add(yesOutcome);
        outcomeRepository.outcomes.add(noOutcome);

        Outcome yesOutcomeResolved = new Outcome(3L, 2L, OutcomeLabel.YES, new BigDecimal("1.20"));
        outcomeRepository.outcomes.add(yesOutcomeResolved);

        Wallet wallet = new Wallet(1L, 1L, new BigDecimal("100.00"), new BigDecimal("50.00"));
        walletRepository.wallets.add(wallet);
    }

    @Test
    void buyShares_yesOutcome_success() {
        BetRequest request = new BetRequest(1L, 1L, OutcomeLabel.YES, new BigDecimal("10.00"), false);

        BetResult result = bettingService.buyShares(request);

        assertNotNull(result);
        assertNotNull(result.bet());
        assertEquals(BetStatus.PENDING, result.bet().status());
        assertEquals(1L, result.bet().userId());
        assertTrue(result.shareCount() > 0);
        assertEquals(new BigDecimal("10.00").setScale(2, java.math.RoundingMode.HALF_UP), result.totalCost());
        assertEquals(135.00, result.remainingBalance().doubleValue(), 0.01);
        assertEquals(1, betRepository.bets.size());
        assertEquals(1, transactionRepository.transactions.size());
    }

    @Test
    void buyShares_noOutcome_success() {
        BetRequest request = new BetRequest(1L, 1L, OutcomeLabel.NO, new BigDecimal("10.00"), false);

        BetResult result = bettingService.buyShares(request);

        assertNotNull(result);
        assertNotNull(result.bet());
        assertTrue(result.shareCount() > 0);
        assertEquals(1, betRepository.bets.size());
    }

    @Test
    void buyShares_usesVirtualBalance_whenRequested() {
        BetRequest request = new BetRequest(1L, 1L, OutcomeLabel.YES, new BigDecimal("10.00"), true);

        BetResult result = bettingService.buyShares(request);

        assertNotNull(result);
        assertEquals(140.00, result.remainingBalance().doubleValue(), 0.01);
    }

    @Test
    void buyShares_insufficientFunds_throwsException() {
        BetRequest request = new BetRequest(1L, 1L, OutcomeLabel.YES, new BigDecimal("200.00"), false);

        assertThrows(InsufficientFundsException.class, () -> bettingService.buyShares(request));
    }

    @Test
    void buyShares_walletNotFound_throwsException() {
        BetRequest request = new BetRequest(999L, 1L, OutcomeLabel.YES, new BigDecimal("10.00"), false);

        assertThrows(InsufficientFundsException.class, () -> bettingService.buyShares(request));
    }

    @Test
    void buyShares_marketNotOpen_throwsException() {
        BetRequest request = new BetRequest(1L, 2L, OutcomeLabel.YES, new BigDecimal("10.00"), false);

        assertThrows(MarketNotOpenException.class, () -> bettingService.buyShares(request));
    }

    @Test
    void buyShares_eventNotFound_throwsException() {
        BetRequest request = new BetRequest(1L, 999L, OutcomeLabel.YES, new BigDecimal("10.00"), false);

        assertThrows(InvalidBetException.class, () -> bettingService.buyShares(request));
    }

    @Test
    void buyShares_outcomeNotFound_throwsException() {
        outcomeRepository.outcomes.clear();

        BetRequest request = new BetRequest(1L, 1L, OutcomeLabel.YES, new BigDecimal("10.00"), false);

        assertThrows(OutcomeNotFoundException.class, () -> bettingService.buyShares(request));
    }

    @Test
    void buyShares_amountTooLow_throwsException() {
        BetRequest request = new BetRequest(1L, 1L, OutcomeLabel.YES, new BigDecimal("0.005"), false);

        assertThrows(InvalidBetException.class, () -> bettingService.buyShares(request));
    }

    @Test
    void buyShares_invalidUserId_throwsException() {
        BetRequest request = new BetRequest(0L, 1L, OutcomeLabel.YES, new BigDecimal("10.00"), false);

        assertThrows(InvalidBetException.class, () -> bettingService.buyShares(request));
    }

    @Test
    void buyShares_invalidEventId_throwsException() {
        BetRequest request = new BetRequest(1L, 0L, OutcomeLabel.YES, new BigDecimal("10.00"), false);

        assertThrows(InvalidBetException.class, () -> bettingService.buyShares(request));
    }

    @Test
    void calculateSharePrice_yesOutcome_correctPrice() {
        BigDecimal price = bettingService.calculateSharePrice(0.65, OutcomeLabel.YES);

        assertEquals(new BigDecimal("0.6500"), price);
    }

    @Test
    void calculateSharePrice_noOutcome_correctPrice() {
        BigDecimal price = bettingService.calculateSharePrice(0.65, OutcomeLabel.NO);

        assertEquals(new BigDecimal("0.3500"), price);
    }

    @Test
    void calculateSharePrice_probabilityZero_yesIsFree() {
        BigDecimal price = bettingService.calculateSharePrice(0.0, OutcomeLabel.YES);

        assertEquals(new BigDecimal("0.0000"), price);
    }

    @Test
    void calculateSharePrice_probabilityOne_yesIsMax() {
        BigDecimal price = bettingService.calculateSharePrice(1.0, OutcomeLabel.YES);

        assertEquals(new BigDecimal("1.0000"), price);
    }

    @Test
    void calculateSharePrice_invalidProbabilityNegative_throwsException() {
        assertThrows(InvalidBetException.class, () -> bettingService.calculateSharePrice(-0.1, OutcomeLabel.YES));
    }

    @Test
    void calculateSharePrice_invalidProbabilityOverOne_throwsException() {
        assertThrows(InvalidBetException.class, () -> bettingService.calculateSharePrice(1.1, OutcomeLabel.YES));
    }

    @Test
    void calculateShareCount_validInputs_correctCount() {
        BigDecimal amount = new BigDecimal("10.00");
        BigDecimal price = new BigDecimal("0.25");

        int count = bettingService.calculateShareCount(amount, price);

        assertEquals(40, count);
    }

    @Test
    void calculateShareCount_fractionalShares_roundsDown() {
        BigDecimal amount = new BigDecimal("10.00");
        BigDecimal price = new BigDecimal("0.33");

        int count = bettingService.calculateShareCount(amount, price);

        assertEquals(30, count);
    }

    @Test
    void calculateShareCount_zeroAmount_throwsException() {
        assertThrows(InvalidBetException.class, () -> bettingService.calculateShareCount(BigDecimal.ZERO, new BigDecimal("0.50")));
    }

    @Test
    void calculateShareCount_zeroPrice_throwsException() {
        assertThrows(InvalidBetException.class, () -> bettingService.calculateShareCount(new BigDecimal("10.00"), BigDecimal.ZERO));
    }

    @Test
    void calculatePotentialWin_validShareCount_correctWin() {
        BigDecimal win = bettingService.calculatePotentialWin(10);

        assertEquals(new BigDecimal("10.00"), win);
    }

    @Test
    void calculatePotentialWin_singleShare_correctWin() {
        BigDecimal win = bettingService.calculatePotentialWin(1);

        assertEquals(new BigDecimal("1.00"), win);
    }

    @Test
    void calculatePotentialWin_zeroShares_throwsException() {
        assertThrows(InvalidBetException.class, () -> bettingService.calculatePotentialWin(0));
    }

    @Test
    void calculatePotentialWin_negativeShares_throwsException() {
        assertThrows(InvalidBetException.class, () -> bettingService.calculatePotentialWin(-5));
    }

    static class TestOutcomeRepository implements OutcomeRepository {
        List<Outcome> outcomes = new ArrayList<>();

        @Override
        public Optional<Outcome> findById(long id) {
            return outcomes.stream().filter(o -> o.id() == id).findFirst();
        }

        @Override
        public Optional<Outcome> findByEventIdAndLabel(long eventId, OutcomeLabel label) {
            return outcomes.stream()
                .filter(o -> o.eventId() == eventId && o.label() == label)
                .findFirst();
        }
    }

    static class TestEventRepository implements EventRepository {
        List<Event> events = new ArrayList<>();

        @Override
        public Optional<Event> findById(long id) {
            return events.stream().filter(e -> e.id() == id).findFirst();
        }

        @Override
        public List<Event> findByStatus(EventStatus status) {
            return events.stream().filter(e -> e.status() == status).toList();
        }
    }

    static class TestWalletRepository implements WalletRepository {
        List<Wallet> wallets = new ArrayList<>();

        @Override
        public Optional<Wallet> findByUserId(long userId) {
            return wallets.stream().filter(w -> w.userId() == userId).findFirst();
        }

        @Override
        public Wallet updateBalances(long walletId, BigDecimal realBalance, BigDecimal virtualBalance) {
            return wallets.stream()
                .filter(w -> w.id() == walletId)
                .findFirst()
                .map(w -> {
                    Wallet updated = new Wallet(w.id(), w.userId(), realBalance, virtualBalance);
                    wallets.remove(w);
                    wallets.add(updated);
                    return updated;
                })
                .orElseThrow();
        }

        @Override
        public Wallet deductFromBalance(long walletId, BigDecimal amount, boolean useVirtualFirst) {
            return wallets.stream()
                .filter(w -> w.id() == walletId)
                .findFirst()
                .map(w -> {
                    BigDecimal remaining = amount;
                    BigDecimal newReal = w.realBalance();
                    BigDecimal newVirtual = w.virtualBalance();

                    if (useVirtualFirst) {
                        if (newVirtual.compareTo(remaining) >= 0) {
                            newVirtual = newVirtual.subtract(remaining);
                            remaining = BigDecimal.ZERO;
                        } else {
                            remaining = remaining.subtract(newVirtual);
                            newVirtual = BigDecimal.ZERO;
                            newReal = newReal.subtract(remaining);
                        }
                    } else {
                        if (newReal.compareTo(remaining) >= 0) {
                            newReal = newReal.subtract(remaining);
                        } else {
                            remaining = remaining.subtract(newReal);
                            newReal = BigDecimal.ZERO;
                            newVirtual = newVirtual.subtract(remaining);
                        }
                    }

                    Wallet updated = new Wallet(w.id(), w.userId(), newReal, newVirtual);
                    wallets.remove(w);
                    wallets.add(updated);
                    return updated;
                })
                .orElseThrow();
        }

        @Override
        public Wallet addToBalance(long walletId, BigDecimal amount, boolean isVirtual) {
            return wallets.stream()
                .filter(w -> w.id() == walletId)
                .findFirst()
                .map(w -> {
                    BigDecimal newReal = isVirtual ? w.realBalance() : w.realBalance().add(amount);
                    BigDecimal newVirtual = isVirtual ? w.virtualBalance().add(amount) : w.virtualBalance();
                    Wallet updated = new Wallet(w.id(), w.userId(), newReal, newVirtual);
                    wallets.remove(w);
                    wallets.add(updated);
                    return updated;
                })
                .orElseThrow();
        }
    }

    static class TestBetRepository implements BetRepository {
        List<Bet> bets = new ArrayList<>();
        private long nextId = 1;

        @Override
        public Bet save(Bet bet) {
            Bet saved = new Bet(
                nextId++,
                bet.userId(),
                bet.outcomeId(),
                bet.amount(),
                bet.potentialWin(),
                bet.sharePrice(),
                bet.shareCount(),
                bet.status(),
                bet.createdAt()
            );
            bets.add(saved);
            return saved;
        }

        @Override
        public List<Bet> findByUserId(long userId) {
            return bets.stream().filter(b -> b.userId() == userId).toList();
        }
    }

    static class TestTransactionRepository implements TransactionRepository {
        List<Transaction> transactions = new ArrayList<>();
        private long nextId = 1;

        @Override
        public Transaction save(long userId, TransactionType type, BigDecimal amount) {
            Transaction tx = new Transaction(nextId++, userId, type, amount, Instant.now());
            transactions.add(tx);
            return tx;
        }

        @Override
        public List<Transaction> findByUserId(long userId) {
            return transactions.stream().filter(t -> t.userId() == userId).toList();
        }
    }
}
