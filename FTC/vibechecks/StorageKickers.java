package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class StorageKickers {

    private Servo storageServo1, storageServo2, storageServo3;
    private ElapsedTime stopwatch = new ElapsedTime();

    // Tunable constants
    private static final double STARTING_POSITION          = 0.0;
    private static final double KICK_AMOUNT                = 0.3;
    private static final double DELAY_KICKER_STAYING_UP    = 0.3; // seconds
    private static final double DELAY_BETWEEN_KICKS        = 1.0; // seconds

    private enum SequenceState {
        IDLE,
        SERVO_WAIT_UP,
        SERVO_WAIT_DOWN
    }

    private SequenceState state = SequenceState.IDLE;

    // Which servo is currently active (1, 2, or 3). 0 = none.
    private int activeServo = 0;

    // Queue of servos to kick in order. Filled by kickOne() or kickAll().
    private int[] queue      = new int[0];
    private int   queueIndex = 0;

    public StorageKickers(HardwareMap hwMap) {
        storageServo1 = hwMap.get(Servo.class, "storageServo1");
        storageServo2 = hwMap.get(Servo.class, "storageServo2");
        storageServo3 = hwMap.get(Servo.class, "storageServo3");

        storageServo1.setPosition(STARTING_POSITION);
        storageServo2.setPosition(STARTING_POSITION);
        storageServo3.setPosition(STARTING_POSITION);
    }

    /**
     * Call once per loop in TeleOp — drives the non-blocking state machine.
     */
    public void update() {
        switch (state) {

            case IDLE:
                // Start next queued kick if one exists
                if (queueIndex < queue.length) {
                    activeServo = queue[queueIndex];
                    getServo(activeServo).setPosition(STARTING_POSITION + KICK_AMOUNT);
                    stopwatch.reset();
                    state = SequenceState.SERVO_WAIT_UP;
                }
                break;

            case SERVO_WAIT_UP:
                if (stopwatch.seconds() >= DELAY_KICKER_STAYING_UP) {
                    getServo(activeServo).setPosition(STARTING_POSITION);
                    stopwatch.reset();
                    state = SequenceState.SERVO_WAIT_DOWN;
                }
                break;

            case SERVO_WAIT_DOWN:
                if (stopwatch.seconds() >= DELAY_BETWEEN_KICKS) {
                    queueIndex++;
                    state = SequenceState.IDLE;
                }
                break;
        }
    }

    /**
     * Queue a single servo kick. servoNumber = 1, 2, or 3.
     * Safe to call from TeleOp button press — ignored if already busy.
     */
    public void kickOne(int servoNumber) {
        if (state == SequenceState.IDLE) {
            queue      = new int[]{ servoNumber };
            queueIndex = 0;
        }
    }

    /**
     * Queue all three servos in sequence (1 → 2 → 3).
     * Safe to call from TeleOp button press — ignored if already busy.
     */
    public void kickAll() {
        if (state == SequenceState.IDLE) {
            queue      = new int[]{ 1, 2, 3 };
            queueIndex = 0;
        }
    }

    /**
     * Blocking version for use in Autonomous.
     * Kicks all three servos in sequence and waits for completion.
     * Call from Auto OpMode — do NOT call from TeleOp loop.
     */
    public void kickAllBlocking() {
        kickServoBlocking(storageServo1);
        kickServoBlocking(storageServo2);
        kickServoBlocking(storageServo3);
    }

    /** True if a kick sequence is currently in progress. */
    public boolean isBusy() {
        return state != SequenceState.IDLE || queueIndex < queue.length;
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private Servo getServo(int number) {
        switch (number) {
            case 1:  return storageServo1;
            case 2:  return storageServo2;
            case 3:  return storageServo3;
        }
    }

    private void kickServoBlocking(Servo servo) {
        ElapsedTime t = new ElapsedTime();

        servo.setPosition(STARTING_POSITION + KICK_AMOUNT);
        t.reset();
        while (t.seconds() < DELAY_KICKER_STAYING_UP) { /* wait */ }

        servo.setPosition(STARTING_POSITION);
        t.reset();
        while (t.seconds() < DELAY_BETWEEN_KICKS) { /* wait */ }
    }
}
