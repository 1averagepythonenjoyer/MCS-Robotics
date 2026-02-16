
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


@TeleOp(name = "storageKickers")
public class storageKickers extends LinearOpMode {

    private Servo storageServo1;
    private Servo storageServo2;
    private Servo storageServo3;




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

        waitForStart();

        while (opModeIsActive()) {


            double startingServoPosition = 0;
            double DELAY_KICKER_STAYING_UP_TIME = 0.3;
            double DELAY_BETWEEN_KICKS = 1;

            boolean sequenceActive = false;


            //    set servos to start positions
            storageServo1.setPosition(startingServoPosition);
            storageServo2.setPosition(startingServoPosition);
            storageServo3.setPosition(startingServoPosition);


            waitForStart();


            while (opModeIsActive()) {


                ElapsedTime timer = new ElapsedTime();


                switch (currentState) {
                    case IDLE:           // Check for Controller Inputs
                        if (gamepad1.a) {
                            currentState = SequenceState.SERVO1_UP;
                        } else if (gamepad1.b) {
                            currentState = SequenceState.SERVO2_UP;
                        } else if (gamepad1.x) {
                            currentState = SequenceState.SERVO3_UP;
                        } else if (gamepad1.y) {
                            currentState = SequenceState.SERVO1_UP;
                            sequenceActive = true;
                        }
                        break;

                    case SERVO1_UP:
                        storageServo1.setPosition(startingServoPosition + 0.3);  // extend kicker - to push ball up
                        timer.reset();
                        currentState = SequenceState.SERVO1_WAIT_UP;
                        break;

                    case SERVO1_WAIT_UP:
                        if (timer.seconds() >= DELAY_KICKER_STAYING_UP_TIME) {  // Stay up for some period of time
                            storageServo1.setPosition(startingServoPosition);   //retract kicker
                            currentState = SequenceState.SERVO1_WAIT_DOWN;
                        }
                        timer.reset();
                        break;

                    case SERVO1_WAIT_DOWN:
                        if (timer.seconds() >= DELAY_BETWEEN_KICKS) {       // going down for some period of time before next servo is launched
                            if (!sequenceActive) {
                                currentState = SequenceState.IDLE;
                            } else {
                                currentState = SequenceState.SERVO2_UP;
                            }
                        }
                        break;


                    case SERVO2_UP:
                        storageServo2.setPosition(startingServoPosition + 0.3);
                        timer.reset();
                        currentState = SequenceState.SERVO2_WAIT_UP;
                        break;

                    case SERVO2_WAIT_UP:
                        if (timer.seconds() >= DELAY_KICKER_STAYING_UP_TIME) {
                            storageServo2.setPosition(startingServoPosition);
                            currentState = SequenceState.SERVO2_WAIT_DOWN;
                        }
                        timer.reset();
                        break;

                    case SERVO2_WAIT_DOWN:
                        if (timer.seconds() >= DELAY_BETWEEN_KICKS) { // going down for some period of time before next servo is launched
                            if (!sequenceActive) {
                                currentState = SequenceState.IDLE;
                            } else {
                                currentState = SequenceState.SERVO3_UP;
                            }
                        }
                        break;


                    case SERVO3_UP:
                        storageServo3.setPosition(startingServoPosition + 0.3);

                        timer.reset();
                        currentState = SequenceState.SERVO3_WAIT_UP;
                        break;

                    case SERVO3_WAIT_UP:
                        if (timer.seconds() >= DELAY_KICKER_STAYING_UP_TIME) {
                            storageServo3.setPosition(startingServoPosition);
                            currentState = SequenceState.SERVO3_WAIT_DOWN;
                        }
                        timer.reset();
                        break;

                    case SERVO3_WAIT_DOWN:
                        if (timer.seconds() >= DELAY_BETWEEN_KICKS) { // going down for some period of time before next servo is launched

                            currentState = SequenceState.IDLE;   // either way this is the end of the sequence so must be idle
                            sequenceActive = false;
                        }
                        break;


                }


            }
        }
    }
}
