package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

/**
 * FORMATTING NOTE: ALL REFERENCES TO "COLOR" MUST BE WRITTEN WITH US SPELLING "COLOR", AS THAT IS HOW THE SDK DEFINES IT, AND MISTAKES WILL OTHERWISE BE MADE
 * Modular Ball Color Detector - can be easily integrated into any OpMode
 * Detects purple/green balls in 3 storage slots with debouncing
 * Only function that needs to be run in the main loop is update()
 * Unless more specific logic is required.
 * getSlot[i]Color(): returns stable color string for that slot
 */
public class BallColorDetector {

    // Internal type-safe color representation — returned as a string for external telemetry and communication
    private enum BallColor { PURPLE, GREEN, EMPTY, UNKNOWN }

    // Sensors
    private NormalizedColorSensor sensor1, sensor2, sensor3;

    // HSV thresholds
    private static final float PURPLE_HUE_MIN = 270.0f;
    private static final float PURPLE_HUE_MAX = 330.0f;
    private static final float GREEN_HUE_MIN  =  90.0f;
    private static final float GREEN_HUE_MAX  = 150.0f;
    private static final float MIN_SATURATION =  0.35f;
    private static final float MIN_VALUE      =  0.05f;

    // State tracking
    private SensorState state1, state2, state3;  // one SensorState per slot — see inner class at bottom
    private final float[] hsvValues = new float[3]; // reused every loop: [hue, saturation, value]

    /**
     * Constructor — initializes sensors from hardware map. Will be called in main files.
     */
    public BallColorDetector(HardwareMap hardwareMap) {
        sensor1 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor1");
        sensor2 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor2");
        sensor3 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor3");

        state1 = new SensorState();
        state2 = new SensorState();
        state3 = new SensorState();
    }

    /**
     * Call this every loop to update sensor readings.
     */
    public void update() {
        state1.addReading(detectBallColor(sensor1));
        state2.addReading(detectBallColor(sensor2));
        state3.addReading(detectBallColor(sensor3));
    }

    public String getSlot1Color() { // returns stable color for slot 1: "PURPLE", "GREEN", "EMPTY", or "UNKNOWN"
        return state1.getStableReading();
    }

    public String getSlot2Color() { // returns stable color for slot 2
        return state2.getStableReading();
    }

    public String getSlot3Color() { // returns stable color for slot 3
        return state3.getStableReading();
    }

    // ── INDIVIDUAL COLOR AND STABILITY CHECKS FOR EACH SLOT ──────────────────
    // "If stable" methods — return null if the reading isn't ready yet, or the color string if it is.
    // Use these instead of getSlot1Color() etc. when you want to be sure the reading is trustworthy.

    public String getSlot1IfStable() {
        return state1.isStable() ? state1.getStableReading() : null;
    }

    public String getSlot2IfStable() {
        return state2.isStable() ? state2.getStableReading() : null;
    }

    public String getSlot3IfStable() {
        return state3.isStable() ? state3.getStableReading() : null;
    }

    // ── SIMULTANEOUS COLOR AND STABILITY CHECK FOR ALL THREE SLOTS ────────────
    /**
     * "All stable" method — returns null if ANY slot isn't ready yet, or all 3 colours as a String array.
     * Use this when you need all 3 slots to be trustworthy before acting — safest option if all slots are working.
     * Usage:
     *   String[] colors = getAllColorsIfStable();
     *   if (colors != null) {
     *       String slot1 = colors[0];
     *       String slot2 = colors[1];
     *       String slot3 = colors[2];
     *   }
     */
    public String[] getAllColorsIfStable() {
        if (allSlotsStable()) {
            return getAllColors();
        }
        return null;
    }

    public String[] getAllColors() {
        return new String[]{
                state1.getStableReading(),
                state2.getStableReading(),
                state3.getStableReading()
        };
    }

    public boolean isSlot1Stable() { return state1.isStable(); }
    public boolean isSlot2Stable() { return state2.isStable(); }
    public boolean isSlot3Stable() { return state3.isStable(); }

    public boolean allSlotsStable() {
        return state1.isStable() && state2.isStable() && state3.isStable();
    }

    // ── UTILITY METHODS ───────────────────────────────────────────────────────

    /**
     * Count how many slots contain a specific color.
     * @param color "PURPLE", "GREEN", "EMPTY", or "UNKNOWN"
     * @return count (0-3)
     */
    public int countColor(String color) {
        int count = 0;
        if (state1.getStableReading().equals(color)) count++;
        if (state2.getStableReading().equals(color)) count++;
        if (state3.getStableReading().equals(color)) count++;
        return count;
    }

    public boolean hasColor(String color) {
        return countColor(color) > 0;
    }

    /**
     * Get the first slot index (1-3) that contains the specified color.
     * @return slot number (1, 2, or 3), or -1 if not found
     */
    public int findFirstSlotWithColor(String color) {
        if (state1.getStableReading().equals(color)) return 1;
        if (state2.getStableReading().equals(color)) return 2;
        if (state3.getStableReading().equals(color)) return 3;
        return -1;
    }

    /**
     * Returns true only when all three slots have a confirmed stable ball (not EMPTY, not UNKNOWN).
     * UNKNOWN slots are not treated as full or empty — returns false until all slots settle.
     */
    public boolean isStorageFull() {
        return state1.getStableColor() != BallColor.EMPTY
                && state1.getStableColor() != BallColor.UNKNOWN
                && state2.getStableColor() != BallColor.EMPTY
                && state2.getStableColor() != BallColor.UNKNOWN
                && state3.getStableColor() != BallColor.EMPTY
                && state3.getStableColor() != BallColor.UNKNOWN;
    }

    /**
     * Returns true only when all three slots are stably confirmed EMPTY.
     * UNKNOWN slots are not treated as empty — returns false until all slots settle.
     */
    public boolean isStorageEmpty() {
        return state1.getStableColor() == BallColor.EMPTY
                && state2.getStableColor() == BallColor.EMPTY
                && state3.getStableColor() == BallColor.EMPTY;
    }

    /**
     * Get raw HSV values for a specific sensor (for calibration tuning).
     * @param slotNumber 1, 2, or 3
     * @return float array [hue, saturation, value]
     */
    public float[] getRawHSV(int slotNumber) {
        NormalizedColorSensor sensor;
        switch (slotNumber) {
            case 1:  sensor = sensor1; break;
            case 2:  sensor = sensor2; break;
            case 3:  sensor = sensor3; break;
            default: return new float[]{0, 0, 0};
        }
        NormalizedRGBA colors = sensor.getNormalizedColors();
        float[] hsv = new float[3];
        Color.colorToHSV(colors.toColor(), hsv);
        return hsv;
    }

    // ── PRIVATE METHODS ───────────────────────────────────────────────────────

    /**
     * Checks whether a hue value falls within [min, max], correctly handling
     * wrap-around ranges that cross the 0°/360° boundary.
     * For a normal range (min < max): standard containment check.
     * For a wrap-around range (min > max): hue is valid if above min OR below max.
     */
    private boolean inHueRange(float hue, float min, float max) {
        if (min <= max) {
            return hue >= min && hue <= max;
        } else {
            return hue >= min || hue <= max;
        }
    }

    /**
     * Reads one sensor and classifies the ball color.
     * No dynamic allocations — reuses the hsvValues field.
     * Returns a BallColor enum value, never a String.
     */
    private BallColor detectBallColor(NormalizedColorSensor sensor) {
        NormalizedRGBA colors = sensor.getNormalizedColors();
        Color.colorToHSV(colors.toColor(), hsvValues);

        float hue        = hsvValues[0];
        float saturation = hsvValues[1];
        float value      = hsvValues[2];

        // EMPTY requires BOTH low brightness AND low saturation.
        // A colored object close to the sensor may have low value but high saturation
        // and must not be classified as EMPTY.
        if (value < MIN_VALUE && saturation < MIN_SATURATION) return BallColor.EMPTY;

        if (inHueRange(hue, GREEN_HUE_MIN, GREEN_HUE_MAX) && saturation >= MIN_SATURATION) {
            return BallColor.GREEN;
        }

        if (inHueRange(hue, PURPLE_HUE_MIN, PURPLE_HUE_MAX) && saturation >= MIN_SATURATION) {
            return BallColor.PURPLE;
        }

        return BallColor.UNKNOWN;
    }

    // ── INNER CLASS ───────────────────────────────────────────────────────────

    /**
     * Tracks stability for one sensor using consecutive identical readings.
     * Stability resets immediately to UNKNOWN when the reading changes — stale
     * readings are never held after the physical object is removed or swapped.
     * Stores BallColor internally — only converts to String at the public boundary.
     * Static so it can hold a static constant at Java language level 8.
     */
    private static class SensorState {
        // Number of consecutive identical readings required before a color is considered stable
        private static final int STABLE_READINGS_REQUIRED = 5;

        private BallColor lastReading      = null;
        private int       consecutiveCount = 0;
        private BallColor stableColor      = BallColor.UNKNOWN;

        void addReading(BallColor reading) {
            // Fix 2: ignore null readings from hardware hiccups — do not update any state
            if (reading == null) return;

            if (reading == lastReading) {
                // Enum comparison with == is correct and preferred — no .equals() needed
                consecutiveCount++;
            } else {
                // Fix 1: reading changed — reset streak AND immediately unlatch stableColor
                // so a removed ball is never reported as still present
                lastReading      = reading;
                consecutiveCount = 1;
                stableColor      = BallColor.UNKNOWN;
            }

            if (consecutiveCount >= STABLE_READINGS_REQUIRED) {
                stableColor = reading;
            }
        }

        // String conversion happens only here at the public boundary
        String getStableReading() { return stableColor.name(); }

        // Used internally by isStorageFull() and isStorageEmpty() to avoid String conversion
        BallColor getStableColor() { return stableColor; }

        boolean isStable() { return stableColor != BallColor.UNKNOWN; }
    }
}
