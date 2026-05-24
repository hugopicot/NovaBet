package com.polymarket.domain.service;

import com.polymarket.dao.CasinoSessionDAO;
import com.polymarket.dao.OtoEventDAO;
import com.polymarket.domain.exception.OtoServiceException;
import com.polymarket.model.CasinoSession;
import com.polymarket.model.OtoEvent;
import com.polymarket.model.OtoEventType;
import com.polymarket.model.OtoVariant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OtoServiceTest {

    private Connection connection;
    private OtoEventDAO eventDAO;
    private CasinoSessionDAO sessionDAO;
    private OtoService service;

    @BeforeEach
    void setUp() {
        connection = mock(Connection.class);
        eventDAO = mock(OtoEventDAO.class);
        sessionDAO = mock(CasinoSessionDAO.class);
        service = new OtoService(connection, eventDAO, sessionDAO);
    }

    // ─── logDisplayed ────────────────────────────────────────────────

    @Test
    void logDisplayed_insereEventAvecTypeDISPLAYED() {
        when(eventDAO.save(any(OtoEvent.class))).thenReturn(42);

        OtoEvent event = service.logDisplayed(7, OtoVariant.HARD, 100.0);

        assertEquals(OtoEventType.DISPLAYED, event.getEventType());
        assertEquals(7, event.getUserId());
        assertEquals(100.0, event.getBaseAmount());
        assertNull(event.getMultiplier());
        assertNull(event.getPayoutAmount());

        ArgumentCaptor<OtoEvent> captor = ArgumentCaptor.forClass(OtoEvent.class);
        verify(eventDAO).save(captor.capture());
        assertEquals(OtoEventType.DISPLAYED, captor.getValue().getEventType());
    }

    @Test
    void logDisplayed_montantInvalide_leveException() {
        OtoServiceException ex = assertThrows(OtoServiceException.class,
                () -> service.logDisplayed(1, OtoVariant.HARD, 0));
        assertTrue(ex.getMessage().contains("strictement positif"));
        verify(eventDAO, never()).save(any());
    }

    @Test
    void logDisplayed_echecDAO_leveException() {
        when(eventDAO.save(any(OtoEvent.class))).thenReturn(-1);
        assertThrows(OtoServiceException.class,
                () -> service.logDisplayed(1, OtoVariant.HARD, 50));
    }

    // ─── logAccepted (transaction atomique) ──────────────────────────

    @Test
    void logAccepted_insereEventEtSession_etCommit() throws SQLException {
        when(eventDAO.save(any(OtoEvent.class))).thenReturn(10);
        when(sessionDAO.save(any(CasinoSession.class))).thenReturn(20);

        CasinoSession session = service.logAccepted(7, OtoVariant.HARD, 100.0, 5);

        // Verifications fonctionnelles
        assertEquals(500.0, session.getCreditsIn(), "creditsIn = baseAmount * multiplier");
        assertEquals(5, session.getOtoMultiplier());
        assertEquals(7, session.getUserId());

        // Verification de la sequence transactionnelle
        verify(connection).setAutoCommit(false);
        verify(eventDAO).save(any(OtoEvent.class));
        verify(sessionDAO).save(any(CasinoSession.class));
        verify(connection).commit();
        verify(connection, never()).rollback();
        verify(connection).setAutoCommit(true);
    }

    @Test
    void logAccepted_echecInsertEvent_rollbackEtException() throws SQLException {
        when(eventDAO.save(any(OtoEvent.class))).thenReturn(-1); // echec
        when(sessionDAO.save(any(CasinoSession.class))).thenReturn(20);

        assertThrows(OtoServiceException.class,
                () -> service.logAccepted(7, OtoVariant.HARD, 100.0, 5));

        verify(connection).setAutoCommit(false);
        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(connection).setAutoCommit(true);
    }

    @Test
    void logAccepted_echecInsertSession_rollbackEtException() throws SQLException {
        when(eventDAO.save(any(OtoEvent.class))).thenReturn(10);
        when(sessionDAO.save(any(CasinoSession.class))).thenReturn(-1); // echec session

        assertThrows(OtoServiceException.class,
                () -> service.logAccepted(7, OtoVariant.HARD, 100.0, 5));

        verify(connection).rollback();
        verify(connection, never()).commit();
    }

    @Test
    void logAccepted_multiplicateurInvalide_leveException() {
        assertThrows(OtoServiceException.class,
                () -> service.logAccepted(7, OtoVariant.HARD, 100.0, 3));
        verifyNoInteractions(eventDAO);
        verifyNoInteractions(sessionDAO);
    }

    @Test
    void logAccepted_calculePayoutCorrectement() throws SQLException {
        when(eventDAO.save(any(OtoEvent.class))).thenReturn(1);
        when(sessionDAO.save(any(CasinoSession.class))).thenReturn(1);

        ArgumentCaptor<OtoEvent> eventCaptor = ArgumentCaptor.forClass(OtoEvent.class);
        service.logAccepted(7, OtoVariant.HARD, 250.0, 10);

        verify(eventDAO).save(eventCaptor.capture());
        assertEquals(2500.0, eventCaptor.getValue().getPayoutAmount());
        assertEquals(10, eventCaptor.getValue().getMultiplier());
        assertEquals(OtoEventType.ACCEPTED, eventCaptor.getValue().getEventType());
    }

    // ─── logRefused / logExpired ─────────────────────────────────────

    @Test
    void logRefused_insereEventAvecTypeREFUSED() {
        when(eventDAO.save(any(OtoEvent.class))).thenReturn(33);

        OtoEvent event = service.logRefused(7, OtoVariant.MEDIUM, 80.0);

        assertEquals(OtoEventType.REFUSED, event.getEventType());
        assertNull(event.getMultiplier());
        verify(sessionDAO, never()).save(any());
    }

    @Test
    void logExpired_insereEventAvecTypeEXPIRED() {
        when(eventDAO.save(any(OtoEvent.class))).thenReturn(34);

        OtoEvent event = service.logExpired(7, OtoVariant.SOFT, 80.0);

        assertEquals(OtoEventType.EXPIRED, event.getEventType());
        assertNull(event.getMultiplier());
        verify(sessionDAO, never()).save(any());
    }
}
