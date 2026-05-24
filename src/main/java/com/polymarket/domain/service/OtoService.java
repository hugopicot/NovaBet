package com.polymarket.domain.service;

import com.polymarket.dao.CasinoSessionDAO;
import com.polymarket.dao.OtoEventDAO;
import com.polymarket.domain.exception.OtoServiceException;
import com.polymarket.model.CasinoSession;
import com.polymarket.model.OtoEvent;
import com.polymarket.model.OtoEventType;
import com.polymarket.model.OtoVariant;
import com.polymarket.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Service métier OTO. Gère les événements de l'offre (DISPLAYED / ACCEPTED / REFUSED / EXPIRED)
 * et l'ouverture d'une session casino quand l'offre est acceptée.
 *
 * Les écritures multi-tables (event + session) sont encapsulées dans une transaction SQL
 * atomique : tout ou rien.
 */
public class OtoService {

    private final Connection connection;
    private final OtoEventDAO otoEventDAO;
    private final CasinoSessionDAO casinoSessionDAO;

    /**
     * Constructeur de production : crée sa propre connexion via {@code DatabaseConnection}.
     */
    public OtoService() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new OtoServiceException("Impossible d'ouvrir la connexion DB", e);
        }
        this.otoEventDAO = new OtoEventDAO(this.connection);
        this.casinoSessionDAO = new CasinoSessionDAO(this.connection);
    }

    /**
     * Constructeur d'injection (tests) : reçoit une connexion + des DAOs déjà configurés
     * sur cette même connexion.
     */
    public OtoService(Connection connection, OtoEventDAO otoEventDAO, CasinoSessionDAO casinoSessionDAO) {
        this.connection = connection;
        this.otoEventDAO = otoEventDAO;
        this.casinoSessionDAO = casinoSessionDAO;
    }

    /**
     * Log l'affichage de l'offre OTO à l'utilisateur. Pas de modification de wallet.
     * Pas besoin de transaction : une seule écriture.
     */
    public OtoEvent logDisplayed(int userId, OtoVariant variant, double baseAmount) {
        validateAmount(baseAmount);
        OtoEvent event = new OtoEvent(userId, variant, OtoEventType.DISPLAYED, baseAmount, null, null);
        int generatedId = otoEventDAO.save(event);
        if (generatedId == -1) {
            throw new OtoServiceException("Echec d'enregistrement de l'evenement OTO DISPLAYED");
        }
        return event;
    }

    /**
     * Log l'acceptation de l'offre + ouvre une session casino en parallèle.
     *
     * Transaction atomique : l'event ACCEPTED et la session CasinoSession sont créés
     * dans la même transaction SQL — soit les deux, soit rien.
     */
    public CasinoSession logAccepted(int userId, OtoVariant variant, double baseAmount, int multiplier) {
        validateAmount(baseAmount);
        validateMultiplier(multiplier);

        double payout = baseAmount * multiplier;
        OtoEvent event = new OtoEvent(userId, variant, OtoEventType.ACCEPTED, baseAmount, multiplier, payout);
        CasinoSession session = new CasinoSession(userId, payout, multiplier);

        try {
            connection.setAutoCommit(false);

            int eventId = otoEventDAO.save(event);
            if (eventId == -1) {
                throw new SQLException("Echec INSERT oto_events");
            }

            int sessionId = casinoSessionDAO.save(session);
            if (sessionId == -1) {
                throw new SQLException("Echec INSERT casino_sessions");
            }

            connection.commit();
            return session;
        } catch (SQLException e) {
            rollbackQuietly();
            throw new OtoServiceException("Transaction logAccepted echouee — rollback", e);
        } finally {
            restoreAutoCommit();
        }
    }

    /**
     * Log le refus explicite de l'offre par l'utilisateur (clic sur "Non merci").
     */
    public OtoEvent logRefused(int userId, OtoVariant variant, double baseAmount) {
        return logTerminal(userId, variant, baseAmount, OtoEventType.REFUSED);
    }

    /**
     * Log l'expiration du timer sans action de l'utilisateur (fallback retrait simple).
     */
    public OtoEvent logExpired(int userId, OtoVariant variant, double baseAmount) {
        return logTerminal(userId, variant, baseAmount, OtoEventType.EXPIRED);
    }

    private OtoEvent logTerminal(int userId, OtoVariant variant, double baseAmount, OtoEventType type) {
        validateAmount(baseAmount);
        OtoEvent event = new OtoEvent(userId, variant, type, baseAmount, null, null);
        int generatedId = otoEventDAO.save(event);
        if (generatedId == -1) {
            throw new OtoServiceException("Echec d'enregistrement de l'evenement OTO " + type);
        }
        return event;
    }

    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new OtoServiceException("Le montant de base doit etre strictement positif : " + amount);
        }
    }

    private void validateMultiplier(int multiplier) {
        if (multiplier != 2 && multiplier != 4 && multiplier != 5 && multiplier != 10) {
            throw new OtoServiceException("Multiplicateur invalide : " + multiplier
                    + " (attendu 2, 4, 5 ou 10)");
        }
    }

    private void rollbackQuietly() {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }

    private void restoreAutoCommit() {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException ignored) {
        }
    }
}
