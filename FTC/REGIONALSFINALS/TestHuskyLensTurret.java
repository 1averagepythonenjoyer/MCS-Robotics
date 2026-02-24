package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.List;

@TeleOp(name = "TEST HuskyLens + Turret + Power", group = "MCS Test")
public class TestHuskyLensTurret extends LinearOpMode {

    private boolean autoTracking = false;
    private boolean lastAPress   = false;

    @Override
    public void runOpMode() {
        HuskyLensReader camera = new HuskyLensReader(hardwareMap, "huskydyppy");
        Turret turret = new Turret(hardwareMap);

        telemetry.addData("Status", "HuskyLens + Turret initialized.");
        telemetry.addData("HuskyLens connected?", camera.isConnected());
        telemetry.addData("NOTE", "Align turret forward before starting!");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            // ── Toggle auto-tracking with A (rising edge debounced) ──────
            if (gamepad2.a && !lastAPress) {
                autoTracking = !autoTracking;
                if (autoTracking) {
                    turret.resetAutoState();
                }
            }
            lastAPress = gamepad2.a;

            // ── Read HuskyLens ───────────────────────────────────────────
            List<HuskyLensReader.TagData> tags = camera.read();

            // ── Track variables for telemetry ────────────────────────────
            double cmdPower = 0;
            boolean navFound = false;

            if (autoTracking) {
                for (HuskyLensReader.TagData tag : tags) {
                    if (tag.type == HuskyLensReader.TagData.TagType.NAVIGATION) {
                        cmdPower = turret.updateAuto(tag.bearingDeg);
                        navFound = true;
                        break;
                    }
                }
                if (!navFound) {
                    // Tag lost — return to centre
                    turret.returnToCenter();
                }
            } else if (gamepad2.y) {
                turret.stop();
            } else if (gamepad2.b) {
                turret.returnToCenter();
            } else if (gamepad2.dpad_right) {
                turret.updateManual(-0.4);
            } else if (gamepad2.dpad_left) {
                turret.updateManual(0.4);
            } else {
                turret.updateManual(-gamepad2.right_stick_x);
            }

            // ── Telemetry ────────────────────────────────────────────────
            telemetry.addLine("── Mode ──────────────────────");
            telemetry.addData("Auto tracking",  autoTracking);
            telemetry.addData("HuskyLens OK?",  camera.isConnected());

            telemetry.addLine("── Turret ────────────────────");
            telemetry.addData("Angle (deg)",    "%.2f", turret.getAngleDeg());
            telemetry.addData("At limit?",      turret.isAtLimit());
            telemetry.addData("Aligned?",       turret.isAligned());
            if (autoTracking) {
                telemetry.addData("Cmd power", "%.3f", cmdPower);
                telemetry.addData("Nav tag found?", navFound);
            }

            telemetry.addLine("── Tags ──────────────────────");
            telemetry.addData("Tags seen", tags.size());
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

            telemetry.update();
        }

        turret.stop();
    }
}
