package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * TEST: Turret
 *
 * Gamepad2 controls:
 *   Right stick X  — manual turret rotation
 *   D-pad right    — slow nudge right (0.4)
 *   D-pad left     — slow nudge left (-0.4)
 *   A              — return to center (0°)
 *   B              — full stop (release hold)
 *
 * What to verify:
 *   1. Right stick right rotates turret right (positive angle)
 *   2. ±90° software limits engage — turret holds and doesn't overrun
 *   3. holdPosition() locks turret when stick is released (no drift)
 *   4. D-pad nudge moves slowly and stops when released
 *   5. Return to center brings turret back to 0° reliably
 *   6. Encoder reads 0 at startup (pre-match alignment matters)
 *   7. **BUG CHECK**: holdPosition() → updateManual() transition
 *      (should switch from RUN_TO_POSITION to RUN_USING_ENCODER before setVelocity)
 */
@TeleOp(name = "TEST Turret", group = "MCS Test")
public class TestTurret extends LinearOpMode {

    @Override
    public void runOpMode() {
        Turret turret = new Turret(hardwareMap);

        telemetry.addData("Status", "Turret initialized. Press START.");
        telemetry.addData("NOTE", "Align turret forward before starting!");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            if (gamepad2.a) {
                turret.returnToCenter();
            } else if (gamepad2.b) {
                turret.stop();
            } else if (gamepad2.dpad_right) {
                turret.updateManual(0.4);
            } else if (gamepad2.dpad_left) {
                turret.updateManual(-0.4);
            } else {
                turret.updateManual(gamepad2.right_stick_x);
            }

            telemetry.addData("Angle (deg)",    "%.2f", turret.getAngleDeg());
            telemetry.addData("At limit?",      turret.isAtLimit());
            telemetry.addData("Aligned?",       turret.isAligned());
            telemetry.addData("Stick X",        "%.2f", gamepad2.right_stick_x);
            telemetry.update();
        }

        turret.stop();
    }
}