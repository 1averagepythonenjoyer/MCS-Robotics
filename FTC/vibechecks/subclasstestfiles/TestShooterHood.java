package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * TEST: ShooterHood (CRServo hood + flywheel motor)
 *
 * Gamepad2 controls:
 *   D-pad up       — nudge hood up (hold)
 *   D-pad down     — nudge hood down (hold)
 *   Right trigger  — start flywheel manually
 *   Left trigger   — stop flywheel manually
 *   Y              — trigger full launch sequence at 5° hood angle
 *   X              — trigger full launch sequence at 11° (max angle)
 *   A              — trigger full launch sequence at 0° (flat — **BUG CHECK**)
 *
 * What to verify:
 *   1. Nudge up/down moves hood in correct direction; stops on release
 *   2. Nudge is blocked during an active launch sequence (isBusy == true)
 *   3. Flywheel starts/stops cleanly with trigger controls
 *   4. Full launch sequence plays out: servo moves → holds → flywheel spins → retracts
 *   5. Retract returns servoPosition to 0.0
 *   6. State machine transitions: IDLE → POWER → STOP_SERVO → STOP_FLYWHEEL → RETRACT → IDLE
 *   7. **BUG CHECK**: Launch at 0° — HOOD_ANGLE_SHOOT_POSE=0.0 means
 *      timeToRotate=0, servo won't move. Confirm this is intended or needs calibration.
 *   8. MAX_SERVO_TIME clamp works (11° × 0.2 = 2.2s cap)
 */
@TeleOp(name = "TEST ShooterHood", group = "MCS Test")
public class TestShooterHood extends LinearOpMode {

    @Override
    public void runOpMode() {
        ShooterHood hood = new ShooterHood(hardwareMap);

        telemetry.addData("Status", "ShooterHood initialized. Press START.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            // Manual nudge
            if (gamepad2.dpad_up) {
                hood.nudge(0.3);
            } else if (gamepad2.dpad_down) {
                hood.nudge(-0.3);
            } else {
                hood.nudge(0);
            }

            // Manual flywheel
            if (gamepad2.right_trigger > 0.1) {
                hood.startFlywheel();
            } else if (gamepad2.left_trigger > 0.1) {
                hood.stopFlywheel();
            }

            // Launch sequences at different angles
            if (gamepad2.y) hood.triggerLaunch(5.0);
            if (gamepad2.x) hood.triggerLaunch(11.0);
            if (gamepad2.a) hood.triggerLaunch(0.0);  // BUG CHECK: zero angle

            hood.update();

            telemetry.addData("State",         hood.getState());
            telemetry.addData("Busy?",         hood.isBusy());
            telemetry.addData("Servo pos (s)", "%.3f", hood.getServoPosition());
            telemetry.update();
        }

        hood.stopFlywheel();
    }
}
