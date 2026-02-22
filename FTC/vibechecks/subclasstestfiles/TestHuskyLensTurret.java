package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.List;

/**
 * TEST: HuskyLensReader + Turret Auto-Tracking
 *
 * Gamepad2 controls:
 *   A              — toggle auto-tracking on/off
 *   Right stick X  — manual turret control (when auto is OFF)
 *   B              — return turret to center
 *
 * What to verify:
 *   1. HuskyLens connects (isConnected() = true) — check I2C wiring if false
 *   2. Pattern tags (IDs 1-3) report correct pattern strings: gpp, pgp, ppg
 *   3. Navigation tags (IDs 4-5) report plausible distance and bearing:
 *      - Hold tag at ~50cm → distanceMm should read ~500
 *      - Center tag in frame → bearingDeg should be near 0
 *      - Move tag left → bearing goes negative; right → positive
 *   4. Auto-tracking: turret follows nav tag bearing smoothly
 *   5. Turret stops tracking at ±90° software limits
 *   6. turret.isAligned() becomes true when tag is centered
 *   7. returnToCenter() works when no tag is visible
 *   8. Empty tag list when nothing is in view (no phantom detections)
 */
@TeleOp(name = "TEST HuskyLens + Turret", group = "MCS Test")
public class TestHuskyLensTurret extends LinearOpMode {

    private boolean autoTracking = false;
    private boolean lastAPress   = false;

    @Override
    public void runOpMode() {
        HuskyLensReader camera = new HuskyLensReader(hardwareMap, "huskydyppy");
        Turret turret = new Turret(hardwareMap);

        telemetry.addData("Status", "HuskyLens + Turret initialized.");
        telemetry.addData("HuskyLens connected?", camera.isConnected());
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            // Toggle auto-tracking with A (debounced)
            if (gamepad2.a && !lastAPress) {
                autoTracking = !autoTracking;
            }
            lastAPress = gamepad2.a;

            List<HuskyLensReader.TagData> tags = camera.read();

            if (autoTracking) {
                boolean navFound = false;
                for (HuskyLensReader.TagData tag : tags) {
                    if (tag.type == HuskyLensReader.TagData.TagType.NAVIGATION) {
                        turret.updateAuto(tag.bearingDeg);
                        navFound = true;
                        break;
                    }
                }
                if (!navFound) {
                    turret.returnToCenter();
                }
            } else if (gamepad2.b) {
                turret.returnToCenter();
            } else {
                turret.updateManual(gamepad2.right_stick_x);
            }

            // Telemetry
            telemetry.addData("Auto tracking", autoTracking);
            telemetry.addData("HuskyLens OK?", camera.isConnected());
            telemetry.addData("Tags seen",     tags.size());
            for (HuskyLensReader.TagData tag : tags) {
                telemetry.addData("  Tag", tag.toString());
                if (tag.type == HuskyLensReader.TagData.TagType.NAVIGATION) {
                    telemetry.addData("    Distance mm", "%.1f", tag.distanceMm);
                    telemetry.addData("    Bearing deg", "%.2f", tag.bearingDeg);
                    if (tag.vector != null) {
                        telemetry.addData("    Vector X mm", "%.1f", tag.vector[0]);
                        telemetry.addData("    Vector Y mm", "%.1f", tag.vector[1]);
                    }
                }
            }
            telemetry.addData("Turret angle",  "%.2f°", turret.getAngleDeg());
            telemetry.addData("Turret aligned", turret.isAligned());
            telemetry.addData("Turret at limit", turret.isAtLimit());
            telemetry.update();
        }

        turret.stop();
    }
}
