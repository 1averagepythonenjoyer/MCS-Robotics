package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

/**
 * Modular Ball Color Detector - can be easily integrated into any OpMode
 * Detects purple/green balls in 3 storage slots with debouncing
 * ACTUALLY USEFUL FUNCTION IS IN LINE 110
 */
public class BallColorDetectorModule {
    
    // Sensors
    private NormalizedColorSensor sensor1, sensor2, sensor3;
    
    // HSV thresholds
    private static final float PURPLE_HUE_MIN = 270.0f;
    private static final float PURPLE_HUE_MAX = 330.0f;
    private static final float GREEN_HUE_MIN = 90.0f;
    private static final float GREEN_HUE_MAX = 150.0f;
    private static final float MIN_SATURATION = 0.35f;
    private static final float MIN_VALUE = 0.05f;
    
    // State tracking
    private SensorState state1, state2, state3;
    private final float[] hsvValues = new float[3];
    
    /**
     * Constructor - initializes sensors from hardware map
     */
    public BallColorDetectorModule(HardwareMap hardwareMap) {
        sensor1 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor1");
        sensor2 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor2");
        sensor3 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor3");
        
        state1 = new SensorState();
        state2 = new SensorState();
        state3 = new SensorState();
    }
    
    /**
     * Call this every loop to update sensor readings
     */
    public void update() {
        state1.addReading(detectBallColor(sensor1));
        state2.addReading(detectBallColor(sensor2));
        state3.addReading(detectBallColor(sensor3));
    }
    
    /**
     * Get the stable color for slot 1
     * @return "PURPLE", "GREEN", "EMPTY", or "UNKNOWN"
     */
    public String getSlot1Color() {
        return state1.getStableReading();
    }
    
    /**
     * Get the stable color for slot 2
     */
    public String getSlot2Color() {
        return state2.getStableReading();
    }
    
    /**
     * Get the stable color for slot 3
     */
    public String getSlot3Color() {
        return state3.getStableReading();
    }
    
    /**
     * EASY MODE: Get slot 1 color only if stable, otherwise returns null
     * Usage: String color = getSlot1IfStable();
     *        if (color != null) { /* safe to use */ }
     */
    public String getSlot1IfStable() {
        return state1.isStable() ? state1.getStableReading() : null;
    }
    
    /**
     * EASY MODE: Get slot 2 color only if stable, otherwise returns null
     */
    public String getSlot2IfStable() {
        return state2.isStable() ? state2.getStableReading() : null;
    }
    
    /**
     * EASY MODE: Get slot 3 color only if stable, otherwise returns null
     */
    public String getSlot3IfStable() {
        return state3.isStable() ? state3.getStableReading() : null;
    }
    
    /**
     * SUPER EASY MODE: Get all colors only if ALL are stable, otherwise returns null
     * This is the safest way to read - you know all readings are reliable
     * 
     * Usage: 
     *   String[] colors = getAllColorsIfStable();
     *   if (colors != null) {
     *       // All readings are stable and safe to use!
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
    
    /**
     * Get all slot colors as an array
     * @return String array [slot1, slot2, slot3]
     */
    public String[] getAllColors() {
        return new String[]{
            state1.getStableReading(),
            state2.getStableReading(),
            state3.getStableReading()
        };
    }
    
    /**
     * Check if slot 1 has a stable reading
     */
    public boolean isSlot1Stable() {
        return state1.isStable();
    }
    
    /**
     * Check if slot 2 has a stable reading
     */
    public boolean isSlot2Stable() {
        return state2.isStable();
    }
    
    /**
     * Check if slot 3 has a stable reading
     */
    public boolean isSlot3Stable() {
        return state3.isStable();
    }
    
    /**
     * Check if all slots have stable readings
     */
    public boolean allSlotsStable() {
        return state1.isStable() && state2.isStable() && state3.isStable();
    }
    
    /**
     * Count how many slots contain a specific color
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
    
    /**
     * Check if any slot contains a specific color
     */
    public boolean hasColor(String color) {
        return countColor(color) > 0;
    }
    
    /**
     * Get the first slot index (1-3) that contains the specified color
     * @return slot number (1, 2, or 3), or -1 if not found
     */
    public int findFirstSlotWithColor(String color) {
        if (state1.getStableReading().equals(color)) return 1;
        if (state2.getStableReading().equals(color)) return 2;
        if (state3.getStableReading().equals(color)) return 3;
        return -1;
    }
    
    /**
     * Check if storage is full (no empty slots)
     */
    public boolean isStorageFull() {
        return countColor("EMPTY") == 0;
    }
    
    /**
     * Check if storage is empty (all slots empty)
     */
    public boolean isStorageEmpty() {
        return countColor("EMPTY") == 3;
    }
    
    /**
     * Get raw HSV values for a specific sensor (for tuning)
     * @param slotNumber 1, 2, or 3
     * @return float array [hue, saturation, value]
     */
    public float[] getRawHSV(int slotNumber) {
        NormalizedColorSensor sensor;
        switch (slotNumber) {
            case 1: sensor = sensor1; break;
            case 2: sensor = sensor2; break;
            case 3: sensor = sensor3; break;
            default: return new float[]{0, 0, 0};
        }
        
        NormalizedRGBA colors = sensor.getNormalizedColors();
        float[] hsv = new float[3];
        Color.colorToHSV(colors.toColor(), hsv);
        return hsv;
    }
    
    // ========== PRIVATE METHODS ==========
    
    private String detectBallColor(NormalizedColorSensor sensor) {
        NormalizedRGBA colors = sensor.getNormalizedColors();
        Color.colorToHSV(colors.toColor(), hsvValues);
        
        float hue = hsvValues[0];
        float saturation = hsvValues[1];
        float value = hsvValues[2];

        if (value < MIN_VALUE) return "EMPTY";
        
        if (hue >= GREEN_HUE_MIN && hue <= GREEN_HUE_MAX && saturation >= MIN_SATURATION) {
            return "GREEN";
        }
        
        if ((hue >= PURPLE_HUE_MIN || hue <= (PURPLE_HUE_MAX - 360)) && saturation >= MIN_SATURATION) {
            return "PURPLE";
        }

        return "UNKNOWN";
    }
    
    // ========== INNER CLASS ==========
    
    private class SensorState {
        private static final int STABLE_READINGS_REQUIRED = 3;
        private final String[] history = new String[5];
        private int index = 0;
        private String stableColor = "UNKNOWN";

        void addReading(String reading) {
            history[index] = reading;
            index = (index + 1) % 5;

            int count = 0;
            for (String s : history) {
                if (reading.equals(s)) count++;
            }
            if (count >= STABLE_READINGS_REQUIRED) stableColor = reading;
        }

        String getStableReading() { 
            return stableColor; 
        }
        
        boolean isStable() { 
            return !stableColor.equals("UNKNOWN"); 
        }
    }
}
