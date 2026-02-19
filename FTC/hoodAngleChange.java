// VERSION WITH NO INTERRUPTING WHILE LOOPS !!
// IMPORTANT NOTE FOR CALIBRATING THIS PROGRAM:
// tune these factors: DEFAULT_HOOD_SERVO_POWER and TIME_FACTOR, until the hood actually turns by the correct ANGLE_TO_ROTATE (btn. b should turn exactly 15 degrees - which is the top limit angle turn)
// tune this factor: DEFAULT_FLYWHEEL_POWER, until the ball lands exactly in the goal at 4.8m when the ANGLE_TO_ROTATE is 15 degrees


package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Hood Angle Change (Stopwatch Version)")
public class hoodAngleChange extends LinearOpMode {

    private CRServo hoodServo;
    //private DigitalChannel hoodSwitch;
    private DcMotor flywheel;                                                  // Comment this to test without flywheel

    enum launchState {
        Idle,
        Power_Servo_And_Flywheel,
        Stop_Servo,
        Stop_Flywheel,
        Retract_Servo

    }
    launchState currentState = launchState.Idle;

    // function to check switch to return bool
    //public boolean switchStatus() {
        //return !hoodSwitch.getState();
    //}

    @Override
    public void runOpMode() {

        hoodServo = hardwareMap.get(CRServo.class, "s1AsServo");
        flywheel = hardwareMap.get(DcMotor.class, "flywheel");                   // Comment this to test without flywheel

        //hoodSwitch = hardwareMap.get(DigitalChannel.class, "hoodSwitch");
        //hoodSwitch.setMode(DigitalChannel.Mode.INPUT);

        final double TIME_FACTOR = 0.2;                                       // Tune this factor!!    - factor to calculate how far to move the hood Servo
        final double FLYWHEEL_SPINNING_PERIOD = 5.0;                            // Tune this factor!!    - Period of time in seconds during which the ball will be pushed to the flywheel and launched
        final double DEFAULT_HOOD_SERVO_POWER =   1.0;                         // Tune this factor!!
        final double DEFAULT_FLYWHEEL_POWER =   0.7;                           // Tune this factor!!
        double ANGLE_TO_ROTATE = 10;                    //in degrees            // This value needs to be adapted to be calculated using input from the camera's april tag info



        ElapsedTime stopwatch = new ElapsedTime();

        hoodServo.setDirection(CRServo.Direction.FORWARD);

        telemetry.addLine("Initialized. Waiting for start...");

        waitForStart();



        if (opModeIsActive()){

        while (opModeIsActive()) {

            switch (currentState) {

                case Idle:
                    if (gamepad1.x) {                                             //only for testing - IDEALLY replaced by a function that calculates angle automatically
                        currentState = launchState.Power_Servo_And_Flywheel;
                        ANGLE_TO_ROTATE = 3;
                    } else if (gamepad1.y) {
                        currentState = launchState.Power_Servo_And_Flywheel;
                        ANGLE_TO_ROTATE = 7;
                    } else if (gamepad1.a) {
                        currentState = launchState.Power_Servo_And_Flywheel;
                        ANGLE_TO_ROTATE = 11;
                    } else if (gamepad1.b) {
                        currentState = launchState.Power_Servo_And_Flywheel;
                        ANGLE_TO_ROTATE = 15;                                                //MAXIMUM ANGLE
                    }

                    // IN FUTURE: ANGLE_TO_ROTATE must be calculated using the distance from the goal using a function like below
                    // ANGLE_TO_ROTATE = (distance_from_goal / max_distance_from_goal) * 15 * TUNABLE_FACTOR
                    break;

                case Power_Servo_And_Flywheel:                                      // To save time I started the flywheel and servo at the same time
                    stopwatch.reset();

                    hoodServo.setPower(DEFAULT_HOOD_SERVO_POWER);
                    flywheel.setPower(DEFAULT_FLYWHEEL_POWER);                          // Comment this to test without flywheel


                    telemetry.addData("Status", "Moving Hood");
                    telemetry.addData("Target Time", TIME_FACTOR * ANGLE_TO_ROTATE);
                    telemetry.addData("Status", "Shooting!");

                    currentState = launchState.Stop_Servo;
                    break;

                case Stop_Servo:

                    if (stopwatch.seconds() >=  TIME_FACTOR * ANGLE_TO_ROTATE){
                        hoodServo.setPower(0);
                        currentState = launchState.Stop_Flywheel;

                    }


                    break;

                case Stop_Flywheel:

                    if (stopwatch.seconds() >= FLYWHEEL_SPINNING_PERIOD) {                          //NOTE: (FLYWHEEL_SPINNING_PERIOD) must be much greater than (TIME_FACTOR * ANGLE_TO_ROTATE)
                        flywheel.setPower(0);                                                           // Comment this to test without flywheel
                        currentState = launchState.Idle;
                    }
                    break;

                // case Retract_Servo:
                //     // If the switch is not pressed, move backwards to find home
                //     if (!switchStatus()) {
                //         hoodServo.setPower(-0.5);

                //         telemetry.addData("Status", "Retracting (Finding Zero)");
                //     }
                //     // If the switch is pressed, stop
                //     else {
                //         hoodServo.setPower(0);

                //         telemetry.addData("Status", "Ready to Aim the Hood");
                //         currentState = launchState.Idle;
                //     }
                //     break;
            }
            telemetry.update();
        }
        
    }
    
    }
}
