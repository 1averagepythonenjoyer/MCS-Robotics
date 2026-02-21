
// This very simple program uses buttons a, b and x to control 'kick' each servo 90 degrees, and uses button y to trigger all 3 'kicks' in sequence
// There are three (double type) tunable variables:
// startingServoPosition = 0;             number from 0 to 1, which is the resting position of the servo
// DELAY_KICKER_STAYING_UP_TIME = 0.3;    number of seconds that the kicker stays up until it is instructed to retract
// DELAY_BETWEEN_KICKS = 1;               number of seconds allotted for the servo that is up, to move down to resting position before the next servo can 'kick' (to prevent collisions between 'kicks')


package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name = "storageKicker")
public class kickersWithFlywheelTest extends LinearOpMode {

    private Servo storageServo1;

    private Servo storageServo2;
    private Servo storageServo3;

    private DcMotor flywheel;                                                  // Comment this to test without flywheel



    enum SequenceState {
        IDLE,
        SERVO1_UP,
        SERVO1_WAIT_UP,
        SERVO2_UP,
        SERVO2_WAIT_UP,
        SERVO3_UP,
        SERVO3_WAIT_UP,
        SERVO1_WAIT_DOWN,
        SERVO2_WAIT_DOWN,
        SERVO3_WAIT_DOWN


    }

    SequenceState currentState = SequenceState.IDLE;



    @Override
    public void runOpMode() {


        storageServo1 = hardwareMap.get(Servo.class, "storageServo1");
        storageServo2 = hardwareMap.get(Servo.class, "storageServo2");
        storageServo3 = hardwareMap.get(Servo.class, "storageServo3");

        storageServo3.setDirection(Servo.Direction.REVERSE);
      
        flywheel = hardwareMap.get(DcMotor.class, "flywheel");                   // Comment this to test without flywheel


        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
      
        final double DEFAULT_FLYWHEEL_POWER = 0.4;                           // Tune this factor!!



        double startingServoPosition = 0;
        double DELAY_KICKER_STAYING_UP_TIME = 2;
        double DELAY_BETWEEN_KICKS = 2;

        double startingServo1Pos = 0.21;
        double startingServo2Pos = 0.08;
        double startingServo3Pos = 0.3;

        boolean sequenceActive = false;
        double rotateFactor = 0.38;


        //    set servos to start positions
        storageServo1.setPosition(startingServo1Pos);
        storageServo2.setPosition(startingServo2Pos);
        storageServo3.setPosition(startingServo3Pos);

        ElapsedTime stopwatch = new ElapsedTime();       //Important set stopwatch


        waitForStart();


        while (opModeIsActive()) {




            switch (currentState) {
                case IDLE:           // Check for Controller Inputs

                    if (gamepad1.dpad_left) {                           //cross
                        currentState = SequenceState.SERVO1_UP;
                    } else if (gamepad1.dpad_up) {                    //circle
                        currentState = SequenceState.SERVO2_UP;
                    } else if (gamepad1.dpad_right) {                    //square
                        currentState = SequenceState.SERVO3_UP;
                    } else if (gamepad1.dpad_down) {                    //triangle
                        currentState = SequenceState.SERVO1_UP;
                        sequenceActive = true;
                    }  else if (gamepad1.right_bumper) {
                        if (flywheel.getPower() > 0) {
                                flywheel.setPower(0);
                        }
                        else {
                                flywheel.setPower(DEFAULT_FLYWHEEL_POWER);
                        }
                    }
                    break;

                case SERVO1_UP:
                    storageServo1.setPosition(startingServo1Pos + rotateFactor);  // extend kicker - to push ball up
                    stopwatch.reset();
                    currentState = SequenceState.SERVO1_WAIT_UP;
                    telemetry.addLine("Servo 1 kicking up");

                    break;

                case SERVO1_WAIT_UP:
                    if (stopwatch.seconds() >= DELAY_KICKER_STAYING_UP_TIME) {  // Stay up for some period of time
                        storageServo1.setPosition(startingServo1Pos);   //retract kicker
                        currentState = SequenceState.SERVO1_WAIT_DOWN;
                        telemetry.addLine("Servo 1 going down");

                        stopwatch.reset();
                    }

                    break;

                case SERVO1_WAIT_DOWN:
                    if (stopwatch.seconds() >= DELAY_BETWEEN_KICKS) {       // going down for some period of time before next servo is launched
                        if (!sequenceActive) {
                            currentState = SequenceState.IDLE;
                        } else {
                            currentState = SequenceState.SERVO2_UP;
                        }
                    }
                    break;


                case SERVO2_UP:
                    storageServo2.setPosition(startingServo2Pos + rotateFactor);
                    stopwatch.reset();
                    currentState = SequenceState.SERVO2_WAIT_UP;
                    telemetry.addLine("Servo 2 kicking up");

                    break;

                case SERVO2_WAIT_UP:
                    if (stopwatch.seconds() >= DELAY_KICKER_STAYING_UP_TIME) {
                        storageServo2.setPosition(startingServo2Pos);
                        currentState = SequenceState.SERVO2_WAIT_DOWN;
                        telemetry.addLine("Servo 2 going down");

                        stopwatch.reset();
                    }

                    break;

                case SERVO2_WAIT_DOWN:
                    if (stopwatch.seconds() >= DELAY_BETWEEN_KICKS) { // going down for some period of time before next servo is launched
                        if (!sequenceActive) {
                            currentState = SequenceState.IDLE;
                        } else {
                            currentState = SequenceState.SERVO3_UP;
                        }
                    }
                    break;


                case SERVO3_UP:
                    storageServo3.setPosition(startingServo3Pos + rotateFactor);
                    telemetry.addLine("Servo 3 kicking up");

                    stopwatch.reset();
                    currentState = SequenceState.SERVO3_WAIT_UP;
                    break;

                case SERVO3_WAIT_UP:
                    if (stopwatch.seconds() >= DELAY_KICKER_STAYING_UP_TIME) {
                        storageServo3.setPosition(startingServo3Pos);
                        currentState = SequenceState.SERVO3_WAIT_DOWN;
                        stopwatch.reset();
                        telemetry.addLine("Servo 3 going down");

                    }

                    break;

                case SERVO3_WAIT_DOWN:
                    if (stopwatch.seconds() >= DELAY_BETWEEN_KICKS) { // going down for some period of time before next servo is launched

                        currentState = SequenceState.IDLE;   // either way this is the end of the sequence so must be idle
                        sequenceActive = false;
                    }
                    break;


            }

            telemetry.update();


        }
    }

}
