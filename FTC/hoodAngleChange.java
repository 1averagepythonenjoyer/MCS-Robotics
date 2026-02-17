package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;

@TeleOp(name = "Hood Angle Change (While Loop Version)")
public class hoodAngleChange extends LinearOpMode {

    private CRServo s1;
    private DigitalChannel hoodSwitch;
    // private DcMotor flywheel;

    // function to check switch to return bool
    public boolean switchStatus() {
        return !hoodSwitch.getState();
    }

    @Override
    public void runOpMode() {

        s1 = hardwareMap.get(CRServo.class, "s1AsServo");
        // flywheel = hardwareMap.get(DcMotor.class, "flywheel");

        hoodSwitch = hardwareMap.get(DigitalChannel.class, "hoodSwitch");
        hoodSwitch.setMode(DigitalChannel.Mode.INPUT);

        final double TIME_FACTOR = 0.002;
        double ANGLE_TO_ROTATE = 10;
        final double TIME_TAKEN_TO_SHOOT = 5.0;

        s1.setDirection(CRServo.Direction.REVERSE);

        telemetry.addLine("Initialized. Waiting for start...");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // If X is pressed move the servo
            if (gamepad2.x) {

                // move hood
                double beginTime = getRuntime(); //record time when X is pressed

                // Loop until the calculated time is reached
                while (opModeIsActive() && getRuntime() < beginTime + (TIME_FACTOR * ANGLE_TO_ROTATE)) {
                    s1.setPower(-0.8);

                    telemetry.addData("Status", "Moving Hood");
                    telemetry.addData("Target Time", TIME_FACTOR * ANGLE_TO_ROTATE);
                    telemetry.update();
                }

//                s1.setPower(0); // Stop servo
//
//                // --- 2. SPIN THE FLYWHEEL ---
//                double powerStartTime = getRuntime(); // Reset the timer for the flywheel
//
//                while (opModeIsActive() && getRuntime() < powerStartTime + TIME_TAKEN_TO_SHOOT) {
//                    flywheel.setPower(0.8);
//
//                    telemetry.addData("Status", "Shooting!");
//                    telemetry.update();
//                }
//
//                flywheel.setPower(0); // Stop flywheel
            }
            // If X is NOT being pressed, recalibrate
            else {
                // If the switch is not pressed, move backwards to find home
                if (!switchStatus()) {
                    s1.setPower(0.5);
                    telemetry.addData("Status", "Homing (Finding Zero)");
                }
                // If the switch is pressed, stop
                else {
                    s1.setPower(0);
                    telemetry.addData("Status", "Ready to Shoot");
                }
            }

            telemetry.addData("Switch Pressed", switchStatus());
            telemetry.update();
        }
    }
}
