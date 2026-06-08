package com.polymarket.domain.service;

import com.polymarket.dao.betsDao;
import com.polymarket.dao.eventsDao;
import com.polymarket.dao.outcomesDao;
import com.polymarket.dao.transactionsDao;
import com.polymarket.dao.walletsDao;
import com.polymarket.model.bets;
import com.polymarket.model.events;
import com.polymarket.model.outcomes;
import com.polymarket.model.transactions;
import com.polymarket.model.wallets;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

public class PolymarketResolutionService {

    private static final Logger LOGGER = Logger.getLogger(PolymarketResolutionService.class.getName());

    private final eventsDao eventDao;
    private final outcomesDao outcomeDao;
    private final betsDao betDao;
    private final walletsDao walletDao;
    private final transactionsDao transactionDao;

    public PolymarketResolutionService(
            eventsDao eventDao,
            outcomesDao outcomeDao,
            betsDao betDao,
            walletsDao walletDao,
            transactionsDao transactionDao
    ) {
        this.eventDao = eventDao;
        this.outcomeDao = outcomeDao;
        this.betDao = betDao;
        this.walletDao = walletDao;
        this.transactionDao = transactionDao;
    }

    public void settleMarket(Long eventId, String winningLabel) {
        events event = eventDao.findById(eventId);
        if (event == null) {
            LOGGER.warning("Event not found for settlement: " + eventId);
            return;
        }

        if ("CLOSED".equals(event.getStatus()) && event.getResolution() != null) {
            return;
        }

        event.setStatus("CLOSED");
        event.setResolution(winningLabel);
        eventDao.update(event);

        List<outcomes> eventOutcomes = outcomeDao.findByEventId(eventId);
        List<bets> unsettledBets = betDao.findUnsettledByEventId(eventId);

        for (bets bet : unsettledBets) {
            outcomes betOutcome = findOutcomeById(eventOutcomes, bet.getOutcome_id());
            if (betOutcome == null) {
                continue;
            }

            double payout;
            if (betOutcome.getLabel().equalsIgnoreCase(winningLabel)) {
                double sharePrice = betOutcome.getOdds();
                if (sharePrice <= 0) {
                    sharePrice = 0.01;
                }
                payout = bet.getAmount() / sharePrice;
            } else {
                payout = 0.0;
            }

            if (payout > 0) {
                wallets wallet = walletDao.findByUserId((long) bet.getUser_id());
                if (wallet != null) {
                    wallet.setVirtualBalance(wallet.getVirtualBalance() + payout);
                    walletDao.update(wallet);
                }
                recordTransaction(bet.getUser_id(), "BET_WON", payout);
            } else {
                recordTransaction(bet.getUser_id(), "BET_LOST", 0.0);
            }

            betDao.settleBet(bet.getId(), payout);
        }

        LOGGER.info("Settled market " + event.getTitle() + " -> " + winningLabel + " (" + unsettledBets.size() + " bets)");
    }

    private outcomes findOutcomeById(List<outcomes> outcomes, int outcomeId) {
        for (outcomes o : outcomes) {
            if (o.getId() != null && o.getId().intValue() == outcomeId) {
                return o;
            }
        }
        return null;
    }

    private void recordTransaction(long userId, String type, double amount) {
        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try {
            transactionDao.add(new transactions(userId, type, amount, createdAt));
        } catch (Exception e) {
            LOGGER.warning("Failed to record transaction: " + e.getMessage());
        }
    }
}
