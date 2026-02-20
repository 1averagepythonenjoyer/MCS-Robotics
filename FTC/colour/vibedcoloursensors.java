package org.firstinspires.ftc.teamcode;

import android.graphics.Color; // The "secret sauce" for reliable HSV
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

@TeleOp(name = "Ball Color Detector: Stable", group = "Sensor")
public class BallColorDetector extends OpMode {
    
    // Using NormalizedColorSensor is better for the APDS9960
    private NormalizedColorSensor sensor1, sensor2, sensor3;
    
    // HSV thresholds
    private static final float PURPLE_HUE_MIN = 270.0f;
    private static final float PURPLE_HUE_MAX = 330.0f;
    private static final float GREEN_HUE_MIN = 90.0f;
    private static final float GREEN_HUE_MAX = 150.0f;
    
    private static final float MIN_SATURATION = 0.35f; // Must be "colorful"
    private static final float MIN_VALUE = 0.05f;      // Minimum brightness to be "not empty"
    
    private SensorState state1, state2, state3;

    // We reuse this array to save memory/CPU
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
        
        // This utility handles all the complex math you had in rgbToHsv
        Color.colorToHSV(colors.toColor(), hsvValues);
        
        float hue = hsvValues[0];
        float saturation = hsvValues[1];
        float value = hsvValues[2];

        // 1. Check if empty
        if (value < MIN_VALUE) return "EMPTY";
        
        // 2. Check for Green
        if (hue >= GREEN_HUE_MIN && hue <= GREEN_HUE_MAX && saturation >= MIN_SATURATION) {
            return "GREEN";
        }
        
        // 3. Check for Purple (Handling the 360-degree wrap)
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
        
        // Debug info for sensor 1 (use this to tune your thresholds)
        NormalizedRGBA c1 = sensor1.getNormalizedColors();
        Color.colorToHSV(c1.toColor(), hsvValues);
        telemetry.addLine("\n--- Sensor 1 Tuning ---");
        telemetry.addData("Hue", "%.1f", hsvValues[0]);
        telemetry.addData("Sat", "%.2f", hsvValues[1]);
        telemetry.addData("Val", "%.2f", hsvValues[2]);
        telemetry.update();
    }

    // Your inner class for debouncing remains great logic!
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
