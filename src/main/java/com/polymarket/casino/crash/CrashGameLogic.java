package com.polymarket.casino.crash;

public class CrashGameLogic {

    private final double crashPoint;
    private final long startTimeMs;

    // Multiplier increases ~0.07× per 100ms (doubles roughly every 2 seconds)
    private static final double GROWTH_RATE = 0.00065;

    public CrashGameLogic() {
        this.crashPoint = generateCrashPoint();
        this.startTimeMs = System.currentTimeMillis();
    }

    // Exposed for tests
    CrashGameLogic(double crashPoint) {
        this.crashPoint = crashPoint;
        this.startTimeMs = System.currentTimeMillis();
    }

    // Distribution: 50% crash before 2×, 25% before 4×, etc. Same as real crash games.
    private double generateCrashPoint() {
        double r = Math.random();
        if (r < 0.01) return 1.00; // 1% instant crash
        return Math.max(1.01, 0.99 / r);
    }

    public double currentMultiplier() {
        long elapsed = System.currentTimeMillis() - startTimeMs;
        return Math.pow(Math.E, GROWTH_RATE * elapsed);
    }

    public boolean hasCrashed() {
        return currentMultiplier() >= crashPoint;
    }

    public double getCrashPoint() {
        return crashPoint;
    }

    public double cashout() {
        if (hasCrashed()) return 0;
        return currentMultiplier();
    }
}
