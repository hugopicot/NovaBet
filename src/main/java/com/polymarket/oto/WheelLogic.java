package com.polymarket.oto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Logique pure de la roue OTO, sans dépendance JavaFX.
 * Extrait de WheelController pour permettre les tests unitaires.
 *
 * Pondération réelle du tirage : 60% pour 2x, 30% pour 5x, 10% pour 10x.
 * La distribution visuelle des 8 segments est différente (4x 2×, 3x 5×, 1x 10×)
 * et indépendante des probabilités réelles : on tire d'abord le multiplicateur,
 * puis on choisit un segment compatible pour l'animation.
 */
public class WheelLogic {

    public static final int[] SLICE_MULTIPLIERS = { 2, 10, 2, 5, 2, 5, 2, 5 };
    public static final double SEGMENT_DEG = 360.0 / SLICE_MULTIPLIERS.length;
    public static final int N_TURNS = 6;

    public static final double P_2X = 0.60;
    public static final double P_5X = 0.30;
    // P_10X = 0.10 (déduit)

    private final Random random;

    public WheelLogic() {
        this(new Random());
    }

    public WheelLogic(Random random) {
        this.random = random;
    }

    /** Tirage pondéré 60/30/10 → renvoie 2, 5 ou 10. */
    public int drawMultiplier() {
        double r = random.nextDouble();
        if (r < P_2X) return 2;
        if (r < P_2X + P_5X) return 5;
        return 10;
    }

    /**
     * Sélectionne aléatoirement un index de segment qui correspond au multiplicateur donné.
     * @throws IllegalArgumentException si aucun segment ne matche
     */
    public int pickSegmentIndex(int multiplier) {
        List<Integer> candidates = new ArrayList<>();
        for (int i = 0; i < SLICE_MULTIPLIERS.length; i++) {
            if (SLICE_MULTIPLIERS[i] == multiplier) candidates.add(i);
        }
        if (candidates.isEmpty()) {
            throw new IllegalArgumentException("Aucun segment pour le multiplicateur " + multiplier);
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    /**
     * Calcule l'angle de rotation (en degrés horaires) pour que le segment d'indice {@code i}
     * arrive sous le pointeur (haut de la roue).
     * <p>
     * Convention JavaFX : Arc mesure en sens trigo (positif = antihoraire depuis 3h),
     * Rotate est horaire. Le centre du segment {@code i} en repère trigo =
     * {@code i * SEGMENT_DEG + SEGMENT_DEG / 2}. Cible visuelle = 90° (sommet).
     * Rotation horaire à appliquer = {@code (i * SEGMENT_DEG + SEGMENT_DEG / 2) - 90},
     * plus N tours pour le suspense.
     */
    public double computeTargetRotation(int segmentIndex) {
        double targetAngle = segmentIndex * SEGMENT_DEG + SEGMENT_DEG / 2 - 90;
        return N_TURNS * 360 + targetAngle;
    }
}
