
// For my darling dearest precious cherished favourite Calvin -
// This very simple program uses buttons a, b and x to control 'kick' each servo 90 degrees, and uses button y to trigger all 3 'kicks' in sequence
// There are three (double type) tunable variables:
// startingServoPosition = 0;             number from 0 to 1, which is the resting position of the servo
// DELAY_KICKER_STAYING_UP_TIME = 0.3;    number of seconds that the kicker stays up until it is instructed to retract
// DELAY_BETWEEN_KICKS = 1;               number of seconds allotted for the servo that is up, to move down to resting position before the next servo can 'kick' (to prevent collisions between 'kicks')
// P.S. Also this is kinda random, but could you send me the photos you took of the bitcoin thing with the gyro     (^ω^)


package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@TeleOp(name = "storageKicker")
public class storageKickerAsFunction extends LinearOpMode {

    private Servo storageServo1;
    private Servo storageServo2;
    private Servo storageServo3;

    List<Integer> servosToKick;

    double startingServoPosition = 0;
    double DELAY_KICKER_STAYING_UP_TIME = 0.3;
    double DELAY_BETWEEN_KICKS = 1;
    ElapsedTime stopwatch = new ElapsedTime();       //Important set stopwatch

    int servosListIndex = 0;

    enum SequenceState {
        IDLE,

        SERVO1_WAIT_UP,

        SERVO2_WAIT_UP,

        SERVO3_WAIT_UP,
        SERVO_WAIT_DOWN,

        DEFINE_VARIABLES

    }

    SequenceState currentState = SequenceState.IDLE;


    @Override
    public void runOpMode() {


        storageServo1 = hardwareMap.get(Servo.class, "storageServo1");
        storageServo2 = hardwareMap.get(Servo.class, "storageServo2");
        storageServo3 = hardwareMap.get(Servo.class, "storageServo3");


        //    set servos to start positions
        storageServo1.setPosition(startingServoPosition);
        storageServo2.setPosition(startingServoPosition);
        storageServo3.setPosition(startingServoPosition);


        waitForStart();

        Integer[] arr = {};
        servosToKick = new ArrayList<>(Arrays.asList(arr));

        while (opModeIsActive()) {

            if (gamepad1.a) {
                currentState = SequenceState.DEFINE_VARIABLES;   // HAPPENS ONCE
                servosToKick.add(1);
            } else if (gamepad1.b) {
                currentState = SequenceState.DEFINE_VARIABLES;   // HAPPENS ONCE
                servosToKick.add(2);
            } else if (gamepad1.x) {
                currentState = SequenceState.DEFINE_VARIABLES;   // HAPPENS ONCE
                servosToKick.add(3);
            }


            KICK_SERVO(servosToKick);
        }
    }

    public void KICK_SERVO(List<Integer> servosToKick) {  // BEFORE THE FUNCTION IS CALLED, CURRENT STATE MUST BE DEFINED AS DEFINE_VARIABLES



            switch (currentState) {


                case IDLE:           // Check for Controller Inputs
                    if (servosListIndex < servosToKick.size()) {
                        if (servosToKick.get(servosListIndex) == 1) {
                            storageServo1.setPosition(startingServoPosition + 0.3);  // extend kicker - to push ball up
                            stopwatch.reset();
                            currentState = SequenceState.SERVO1_WAIT_UP;

                        } else if (servosToKick.get(servosListIndex) == 2) {
                            storageServo2.setPosition(startingServoPosition + 0.3);
                            stopwatch.reset();
                            currentState = SequenceState.SERVO2_WAIT_UP;

                        } else if (servosToKick.get(servosListIndex) == 3) {
                            storageServo3.setPosition(startingServoPosition + 0.3);
                            stopwatch.reset();
                            currentState = SequenceState.SERVO3_WAIT_UP;
                        }
                    }


                    break;


                case SERVO1_WAIT_UP:
                    if (stopwatch.seconds() >= DELAY_KICKER_STAYING_UP_TIME) {  // Stay up for some period of time
                        storageServo1.setPosition(startingServoPosition);   //retract kicker
                        currentState = SequenceState.SERVO_WAIT_DOWN;
                        stopwatch.reset();
                    }

                    break;


                case SERVO2_WAIT_UP:
                    if (stopwatch.seconds() >= DELAY_KICKER_STAYING_UP_TIME) {
                        storageServo2.setPosition(startingServoPosition);
                        currentState = SequenceState.SERVO_WAIT_DOWN;
                        stopwatch.reset();
                    }

                    break;


                case SERVO3_WAIT_UP:
                    if (stopwatch.seconds() >= DELAY_KICKER_STAYING_UP_TIME) {
                        storageServo3.setPosition(startingServoPosition);
                        currentState = SequenceState.SERVO_WAIT_DOWN;
                        stopwatch.reset();
                    }

                    break;

                case SERVO_WAIT_DOWN:
                    if (stopwatch.seconds() >= DELAY_BETWEEN_KICKS) {       // going down for some period of time before next servo is launched
                        currentState = SequenceState.IDLE;
                        servosListIndex += 1;
                    }
                    break;


            }

        }
    }



