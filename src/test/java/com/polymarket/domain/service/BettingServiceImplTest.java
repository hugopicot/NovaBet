package com.polymarket.domain.service;

import com.polymarket.dao.*;
import com.polymarket.domain.dto.BetRequest;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BettingServiceImplTest {

    private TestBetsDao betDao;
    private TestEventsDao eventDao;
    private TestOutcomesDao outcomeDao;
    private TestWalletsDao walletDao;
    private TestTransactionsDao transactionDao;
    private BettingServiceImpl bettingService;

    @BeforeEach
    void setUp() throws SQLException {
        betDao = new TestBetsDao();
        eventDao = new TestEventsDao();
        outcomeDao = new TestOutcomesDao();
        walletDao = new TestWalletsDao();
        transactionDao = new TestTransactionsDao();

        bettingService = new BettingServiceImpl(
            outcomeDao,
            eventDao,
            walletDao,
            betDao,
            transactionDao
        );

        seedData();
    }

    private void seedData() {
        events openEvent = new events(1L, "Will it rain tomorrow?", "Weather prediction", "OPEN", null, "2026-05-10T00:00:00Z");
        eventDao.events.add(openEvent);

        events resolvedEvent = new events(2L, "Past election", "Already resolved", "RESOLVED", "YES", "2026-05-10T00:00:00Z");
        eventDao.events.add(resolvedEvent);

        outcomes yesOutcome = new outcomes(1L, 1L, "YES", 0.60);
        outcomes noOutcome = new outcomes(2L, 1L, "NO", 0.40);
        outcomeDao.outcomes.add(yesOutcome);
        outcomeDao.outcomes.add(noOutcome);

        outcomes yesOutcomeResolved = new outcomes(3L, 2L, "YES", 0.80);
        outcomeDao.outcomes.add(yesOutcomeResolved);

        wallets wallet = new wallets(1L, 1L, 100.00, 50.00);
        walletDao.wallets.add(wallet);
    }

    @Test
    void buyShares_yesOutcome_success() {
        BetRequest request = new BetRequest(1L, 1L, OutcomeLabel.YES, new BigDecimal("10.00"), false);

        var result = bettingService.buyShares(request);

        assertNotNull(result);
        assertNotNull(result.bet());
        assertEquals(1L, result.bet().getUser_id());
        assertTrue(result.shareCount() > 0);
        assertEquals(new BigDecimal("9.60").setScale(2, java.math.RoundingMode.HALF_UP), result.totalCost());
        assertEquals(140.40, result.remainingBalance().doubleValue(), 0.01);
        assertEquals(1, betDao.bets.size());
        assertEquals(1, transactionDao.transactions.size());
    }

    @Test
    void buyShares_noOutcome_success() {
        BetRequest request = new BetRequest(1L, 1L, OutcomeLabel.NO, new BigDecimal("10.00"), false);

        var result = bettingService.buyShares(request);

        assertNotNull(result);
        assertNotNull(result.bet());
        assertTrue(result.shareCount() > 0);
        assertEquals(1, betDao.bets.size());
    }

    @Test
    void buyShares_usesVirtualBalance_whenRequested() {
        BetRequest request = new BetRequest(1L, 1L, OutcomeLabel.YES, new BigDecimal("10.00"), true);

        var result = bettingService.buyShares(request);

        assertNotNull(result);
        assertEquals(140.40, result.remainingBalance().doubleValue(), 0.01);
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

    static class TestBetsDao extends betsDao {
        List<bets> bets = new ArrayList<>();

        public TestBetsDao() throws SQLException { super(); }

        @Override
        public void add(bets bet) { bets.add(bet); }
    }

    static class TestEventsDao extends eventsDao {
        List<events> events = new ArrayList<>();

        public TestEventsDao() throws SQLException { super(); }

        @Override
        public events findById(Long id) {
            return events.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
        }
    }

    static class TestOutcomesDao extends outcomesDao {
        List<outcomes> outcomes = new ArrayList<>();

        public TestOutcomesDao() throws SQLException { super(); }

        @Override
        public outcomes findById(Long id) {
            return outcomes.stream().filter(o -> o.getId().equals(id)).findFirst().orElse(null);
        }

        @Override
        public List<outcomes> findByEventId(Long eventId) {
            List<outcomes> result = new ArrayList<>();
            for (outcomes o : outcomes) {
                if (o.getEventId().equals(eventId)) {
                    result.add(o);
                }
            }
            return result;
        }
    }

    static class TestWalletsDao extends walletsDao {
        List<wallets> wallets = new ArrayList<>();

        public TestWalletsDao() throws SQLException { super(); }

        @Override
        public wallets findById(Long id) {
            return wallets.stream().filter(w -> w.getId().equals(id)).findFirst().orElse(null);
        }

        @Override
        public wallets findByUserId(Long userId) {
            return wallets.stream().filter(w -> w.getUserId().equals(userId)).findFirst().orElse(null);
        }

        @Override
        public void update(wallets wallet) {
            wallets.removeIf(w -> w.getId().equals(wallet.getId()));
            wallets.add(wallet);
        }
    }

    static class TestTransactionsDao extends transactionsDao {
        List<transactions> transactions = new ArrayList<>();

        public TestTransactionsDao() throws SQLException { super(); }

        @Override
        public void add(transactions transaction) { transactions.add(transaction); }
    }
}
