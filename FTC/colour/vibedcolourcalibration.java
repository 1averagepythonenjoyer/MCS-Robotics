package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

/*
 * COLOUR SENSOR CALIBRATION — run this before your first match to tune thresholds.
 *
 * STEP 1 — Empty floor baseline:
 *   With no balls in any slot, check the "Val" reading on telemetry for each sensor.
 *   Set MIN_VALUE slightly above the highest empty reading you see.
 *   e.g. if empty reads 0.02, set MIN_VALUE = 0.05.
 *
 * STEP 2 — Saturation check:
 *   Place a ball in slot 1. "Sat" should jump noticeably above the empty reading.
 *   Set MIN_SATURATION to a safe middle ground between empty and ball readings.
 *   e.g. if empty reads 0.1 and ball reads 0.6, set MIN_SATURATION = 0.35.
 *
 * STEP 3 — Green hue range:
 *   Place a GREEN ball in slot 1. Note the "Hue" value.
 *   Set GREEN_HUE_MIN and GREEN_HUE_MAX to bracket that value with some margin.
 *   e.g. if green reads 115°, set GREEN_HUE_MIN = 90, GREEN_HUE_MAX = 150.
 *
 * STEP 4 — Purple hue range:
 *   Place a PURPLE ball in slot 1. Note the "Hue" value.
 *   Set PURPLE_HUE_MIN and PURPLE_HUE_MAX similarly.
 *   Note: purple often sits near 300°. The wrap-around logic in detectBallColor()
 *   handles cases where it flickers near 0°/360°.
 *
 * STEP 5 — Repeat for slots 2 and 3.
 *   If sensors read differently, you may need per-sensor thresholds.
 *   For now all three share the same thresholds — adjust if needed.
 */

@TeleOp(name = "Colour Sensor Calibration", group = "Tests")
public class ColourSensorCalibration extends OpMode {

    private NormalizedColorSensor sensor1, sensor2, sensor3;

    // ── TUNE THESE VALUES ─────────────────────────────────────────────────────
    private static final float PURPLE_HUE_MIN = 270.0f;
    private static final float PURPLE_HUE_MAX = 330.0f;
    private static final float GREEN_HUE_MIN  =  90.0f;
    private static final float GREEN_HUE_MAX  = 150.0f;
    private static final float MIN_SATURATION =  0.35f;
    private static final float MIN_VALUE      =  0.05f;
    // ─────────────────────────────────────────────────────────────────────────

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

        telemetry.addData("Status", "Initialized — place balls in slots and press Start");
        telemetry.update();
    }

    @Override
    public void loop() {
        state1.addReading(detectBallColor(sensor1));
        state2.addReading(detectBallColor(sensor2));
        state3.addReading(detectBallColor(sensor3));

        // Stable colour readings
        telemetry.addLine("── Stable Readings ───────────────");
        telemetry.addData("Slot 1", "%s %s", state1.getStableReading(), state1.isStable() ? "✓" : "...");
        telemetry.addData("Slot 2", "%s %s", state2.getStableReading(), state2.isStable() ? "✓" : "...");
        telemetry.addData("Slot 3", "%s %s", state3.getStableReading(), state3.isStable() ? "✓" : "...");

        // Raw HSV for sensor 1 — use this to tune thresholds
        NormalizedRGBA c1 = sensor1.getNormalizedColors();
        Color.colorToHSV(c1.toColor(), hsvValues);
        telemetry.addLine("── Sensor 1 Raw HSV (use to tune) ");
        telemetry.addData("  Hue", "%.1f", hsvValues[0]);
        telemetry.addData("  Sat", "%.2f", hsvValues[1]);
        telemetry.addData("  Val", "%.2f", hsvValues[2]);

        // Raw HSV for sensor 2
        NormalizedRGBA c2 = sensor2.getNormalizedColors();
        Color.colorToHSV(c2.toColor(), hsvValues);
        telemetry.addLine("── Sensor 2 Raw HSV ───────────────");
        telemetry.addData("  Hue", "%.1f", hsvValues[0]);
        telemetry.addData("  Sat", "%.2f", hsvValues[1]);
        telemetry.addData("  Val", "%.2f", hsvValues[2]);

        // Raw HSV for sensor 3
        NormalizedRGBA c3 = sensor3.getNormalizedColors();
        Color.colorToHSV(c3.toColor(), hsvValues);
        telemetry.addLine("── Sensor 3 Raw HSV ───────────────");
        telemetry.addData("  Hue", "%.1f", hsvValues[0]);
        telemetry.addData("  Sat", "%.2f", hsvValues[1]);
        telemetry.addData("  Val", "%.2f", hsvValues[2]);

        telemetry.update();
    }

    private String detectBallColor(NormalizedColorSensor sensor) {
        NormalizedRGBA colors = sensor.getNormalizedColors();
        Color.colorToHSV(colors.toColor(), hsvValues);

        float hue        = hsvValues[0];
        float saturation = hsvValues[1];
        float value      = hsvValues[2];

        if (value < MIN_VALUE) return "EMPTY";

        if (hue >= GREEN_HUE_MIN && hue <= GREEN_HUE_MAX && saturation >= MIN_SATURATION) {
            return "GREEN";
        }

        // Purple wraps around 360° so check both sides
        if ((hue >= PURPLE_HUE_MIN || hue <= (PURPLE_HUE_MAX - 360)) && saturation >= MIN_SATURATION) {
            return "PURPLE";
        }

        return "UNKNOWN";
    }

    // SensorState is static so it can have the static constant at Java level 8
    private static class SensorState {
        private static final int STABLE_READINGS_REQUIRED = 3;
        private final String[] history = new String[5];
        private int    index       = 0;
        private String stableColor = "UNKNOWN";

        void addReading(String reading) {
            history[index] = reading;
            index = (index + 1) % 5;

            int count = 0;
            for (String s : history) {
                if (s != null && s.equals(reading)) count++;
            }
            if (count >= STABLE_READINGS_REQUIRED) stableColor = reading;
        }

        String getStableReading() { return stableColor; }
        boolean isStable()        { return !stableColor.equals("UNKNOWN"); }
    }
}
