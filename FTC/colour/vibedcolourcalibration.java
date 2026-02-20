package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

/*
 * CALIBRATION INSTRUCTIONS:
 * 1. CHECK "EMPTY" FLOOR: With no balls, note the 'Val' (Value) on telemetry. 
 * Set MIN_VALUE to be slightly higher than this (e.g., if empty is 0.02, set to 0.05).
 * * 2. CHECK SATURATION: Place a ball in. 'Sat' should jump (usually > 0.5).
 * Set MIN_SATURATION to a safe middle ground (e.g., 0.35) to ignore gray/white dust.
 * * 3. DEFINE HUE BUCKETS: 
 * - Place GREEN ball: Note 'Hue'. Set GREEN_HUE_MIN/MAX around that value.
 * - Place PURPLE ball: Note 'Hue'. Set PURPLE_HUE_MIN/MAX around that value.
 * - Note: Purple often sits near 300°. If it flickers near 0°, the "wrap-around" logic handles it.
 */

@TeleOp(name = "Ball Color Detector: Stable", group = "Sensor")
public class BallColorDetector extends OpMode {
    
    private NormalizedColorSensor sensor1, sensor2, sensor3;
    
    // --- TUNE THESE VALUES USING THE INSTRUCTIONS ABOVE ---
    private static final float PURPLE_HUE_MIN = 270.0f;
    private static final float PURPLE_HUE_MAX = 330.0f;
    private static final float GREEN_HUE_MIN = 90.0f;
    private static final float GREEN_HUE_MAX = 150.0f;
    
    private static final float MIN_SATURATION = 0.35f; 
    private static final float MIN_VALUE = 0.05f;      
    // -------------------------------------------------------

    private SensorState state1, state2, state3;
    private final float[] hsvValues = new float[3];

    @Override
    public void init() {
        sensor1 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor1");
        sensor2 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor2");
        sensor3 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor3");

        state1 = new SensorState();
        state2 = new SensorState();
        state3 = new SensorState();

        telemetry.addData("Status", "Initialized on 3 Buses");
        telemetry.update();
    }

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

    @Override
    public void loop() {
        state1.addReading(detectBallColor(sensor1));
        state2.addReading(detectBallColor(sensor2));
        state3.addReading(detectBallColor(sensor3));

        telemetry.addData("Slot 1", "%s %s", state1.getStableReading(), state1.isStable() ? "✓" : "...");
        telemetry.addData("Slot 2", "%s %s", state2.getStableReading(), state2.isStable() ? "✓" : "...");
        telemetry.addData("Slot 3", "%s %s", state3.getStableReading(), state3.isStable() ? "✓" : "...");
        
        // Tuning Telemetry for Sensor 1
        NormalizedRGBA c1 = sensor1.getNormalizedColors();
        Color.colorToHSV(c1.toColor(), hsvValues);
        telemetry.addLine("\n--- Sensor 1 Tuning ---");
        telemetry.addData("Hue", "%.1f", hsvValues[0]);
        telemetry.addData("Sat", "%.2f", hsvValues[1]);
        telemetry.addData("Val", "%.2f", hsvValues[2]);
        telemetry.update();
    }

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

        String getStableReading() { return stableColor; }
        boolean isStable() { return !stableColor.equals("UNKNOWN"); }
    }
}

