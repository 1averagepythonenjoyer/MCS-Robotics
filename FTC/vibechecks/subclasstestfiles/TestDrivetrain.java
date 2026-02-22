package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * TEST: Drivetrain
 *
 * Gamepad1 controls:
 *   Left stick Y   — forward / back
 *   Right stick X  — strafe left / right
 *   Left stick X   — turn left / right
 *   Left bumper    — hold for slow mode (50% power)
 *   A              — full stop
 *
 * What to verify:
 *   1. Forward pushes robot forward (not backward)
 *   2. Strafe right moves robot right (not left)
 *   3. Turn right rotates robot clockwise
 *   4. Slow mode noticeably halves speed
 *   5. Deadzone: tiny stick drift doesn't move robot
 *   6. Diagonal movement (forward + strafe) is smooth, no wheel fighting
 *   7. Full stop button kills all motors instantly
 */
@TeleOp(name = "TEST Drivetrain", group = "MCS Test")
public class TestDrivetrain extends LinearOpMode {

    @Override
    public void runOpMode() {
        Drivetrain drivetrain = new Drivetrain(hardwareMap);

        telemetry.addData("Status", "Drivetrain initialized. Press START.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.a) {
                drivetrain.stop();
            } else {
                drivetrain.drive(
                        -gamepad1.left_stick_y,
                        gamepad1.right_stick_x,
                        gamepad1.left_stick_x,
                        gamepad1.left_bumper
                );
            }

            telemetry.addData("Forward/Back", "%.2f", -gamepad1.left_stick_y);
            telemetry.addData("Strafe",       "%.2f", gamepad1.right_stick_x);
            telemetry.addData("Turn",         "%.2f", gamepad1.left_stick_x);
            telemetry.addData("Slow mode",    gamepad1.left_bumper);
            telemetry.update();
        }

        drivetrain.stop();
    }
}
