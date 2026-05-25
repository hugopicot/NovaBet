package com.polymarket.oto;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WheelLogicTest {

    private static final double TOLERANCE = 0.02; // ±2 points de pourcentage

    @Test
    void drawMultiplier_respecteLaDistribution60_30_10() {
        WheelLogic logic = new WheelLogic(new Random(42L));
        int trials = 100_000;
        Map<Integer, Integer> counts = new HashMap<>();
        counts.put(2, 0);
        counts.put(5, 0);
        counts.put(10, 0);

        for (int i = 0; i < trials; i++) {
            int m = logic.drawMultiplier();
            counts.merge(m, 1, Integer::sum);
        }

        double p2 = counts.get(2) / (double) trials;
        double p5 = counts.get(5) / (double) trials;
        double p10 = counts.get(10) / (double) trials;

        assertEquals(0.60, p2, TOLERANCE, "P(2x) doit etre proche de 0.60");
        assertEquals(0.30, p5, TOLERANCE, "P(5x) doit etre proche de 0.30");
        assertEquals(0.10, p10, TOLERANCE, "P(10x) doit etre proche de 0.10");
        assertEquals(trials, counts.get(2) + counts.get(5) + counts.get(10),
                "La somme des tirages doit egaler le nombre d'essais");
    }

    @Test
    void drawMultiplier_neRetourneQueDesValeursValides() {
        WheelLogic logic = new WheelLogic(new Random(7L));
        Set<Integer> seen = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            int m = logic.drawMultiplier();
            assertTrue(m == 2 || m == 5 || m == 10,
                    "Multiplicateur invalide : " + m);
            seen.add(m);
        }
        assertEquals(3, seen.size(), "Les 3 multiplicateurs doivent etre tires au moins une fois");
    }

    @Test
    void pickSegmentIndex_retourneUnIndexCompatible() {
        WheelLogic logic = new WheelLogic(new Random(1L));

        for (int trial = 0; trial < 100; trial++) {
            int idx2 = logic.pickSegmentIndex(2);
            int idx5 = logic.pickSegmentIndex(5);
            int idx10 = logic.pickSegmentIndex(10);

            assertEquals(2, WheelLogic.SLICE_MULTIPLIERS[idx2]);
            assertEquals(5, WheelLogic.SLICE_MULTIPLIERS[idx5]);
            assertEquals(10, WheelLogic.SLICE_MULTIPLIERS[idx10]);
        }
    }

    @Test
    void pickSegmentIndex_levantExceptionPourMultiplicateurInconnu() {
        WheelLogic logic = new WheelLogic(new Random(1L));
        assertThrows(IllegalArgumentException.class, () -> logic.pickSegmentIndex(3));
        assertThrows(IllegalArgumentException.class, () -> logic.pickSegmentIndex(0));
        assertThrows(IllegalArgumentException.class, () -> logic.pickSegmentIndex(100));
    }

    @Test
    void computeTargetRotation_aligneLeCentreDuSegmentSousLePointeur() {
        WheelLogic logic = new WheelLogic(new Random(1L));

        // Pour tout segment i, l'angle final modulo 360 doit ramener
        // le centre du segment a la position 90 deg (haut de la roue).
        for (int i = 0; i < WheelLogic.SLICE_MULTIPLIERS.length; i++) {
            double rotation = logic.computeTargetRotation(i);
            // Apres rotation horaire, le centre du segment (en trigo) devient :
            //   centre_visuel = centre_trigo - rotation  (mod 360)
            double centerTrig = i * WheelLogic.SEGMENT_DEG + WheelLogic.SEGMENT_DEG / 2;
            double centerAfter = ((centerTrig - rotation) % 360 + 360) % 360;
            assertEquals(90.0, centerAfter, 1e-6,
                    "Le centre du segment " + i + " doit etre a 90 deg apres rotation");
        }
    }

    @Test
    void computeTargetRotation_inclueAuMoinsN_TURNS_complets() {
        WheelLogic logic = new WheelLogic(new Random(1L));
        for (int i = 0; i < WheelLogic.SLICE_MULTIPLIERS.length; i++) {
            double rotation = logic.computeTargetRotation(i);
            assertTrue(rotation >= WheelLogic.N_TURNS * 360 - 180,
                    "Doit inclure au moins ~N_TURNS tours pour le suspense");
        }
    }

    @Test
    void constructeurParDefaut_creeUnRandomFonctionnel() {
        WheelLogic logic = new WheelLogic();
        int m = logic.drawMultiplier();
        assertTrue(m == 2 || m == 5 || m == 10);
    }
}
