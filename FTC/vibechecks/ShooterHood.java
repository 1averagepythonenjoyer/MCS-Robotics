package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class ShooterHood {

    private CRServo        hoodServo;
    private DcMotor        flywheel;
    private DigitalChannel hoodSwitch;

    // ── Physical constants — fill in after calibration ───────────────────────
    private static final double GOAL_HEIGHT_MM    = 0.0;  // TODO: measure
    private static final double SHOOTER_HEIGHT_MM = 0.0;  // TODO: measure
    private static final double VERTICAL_RISE     = GOAL_HEIGHT_MM - SHOOTER_HEIGHT_MM;

    // ── Tunable factors ───────────────────────────────────────────────────────
    // TIME_FACTOR: seconds per degree of hood rotation. Tune until correct.
    private static final double TIME_FACTOR              = 0.2;
    private static final double FLYWHEEL_SPINNING_PERIOD = 5.0;
    private static final double DEFAULT_HOOD_SERVO_POWER = 1.0;
    private static final double HOMING_SERVO_POWER       = 0.5; // slower for homing
    private static final double DEFAULT_FLYWHEEL_POWER   = 0.7;
    private static final double HOMING_TIMEOUT_SECONDS   = 5.0; // safety cutoff

    // ── Angle safety cap ──────────────────────────────────────────────────────
    // 11 degrees is the physical maximum the hood can rotate to.
    // Any angle above this is silently clamped down to MAX_HOOD_ANGLE.
    private static final double MAX_HOOD_ANGLE = 11.0;

    // ── Calibration table — fill in from your calibration sheet ──────────────
    // Distances in mm, corrections in degrees. Must be in ascending distance order.
    private static final double[] CAL_DISTANCES   = { 500, 750, 1000, 1250, 1500, 1750, 2000 };
    private static final double[] CAL_CORRECTIONS = { 0.0, 0.0,  0.0,  0.0,  0.0,  0.0,  0.0 };
    // ↑ Replace 0.0 values with your measured corrections after calibration

    // ── State machine ─────────────────────────────────────────────────────────
    public enum LaunchState {
        IDLE,
        POWER_SERVO_AND_FLYWHEEL,
        STOP_SERVO,
        STOP_FLYWHEEL,
        RETRACT_SERVO          // homing back to zero after every shot
    }

    private LaunchState       currentState  = LaunchState.IDLE;
    private final ElapsedTime stopwatch     = new ElapsedTime();
    private double            angleToRotate = 0;

    // ── Constructor ───────────────────────────────────────────────────────────

    public ShooterHood(HardwareMap hwMap) {
        hoodServo  = hwMap.get(CRServo.class,        "s1AsServo");
        flywheel   = hwMap.get(DcMotor.class,        "flywheel");
        hoodSwitch = hwMap.get(DigitalChannel.class, "hoodSwitch");

        hoodServo.setDirection(CRServo.Direction.FORWARD);
        hoodServo.setPower(0);

        hoodSwitch.setMode(DigitalChannel.Mode.INPUT);

        // Flywheel brakes immediately when power is set to zero
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        flywheel.setPower(0);
    }

    // ── Homing ────────────────────────────────────────────────────────────────

    /**
     * Blocking home — call ONCE before waitForStart() in your OpMode init.
     * Runs the hood in reverse until the limit switch is pressed (or timeout).
     * After this returns, the hood is at zero and ready to fire.
     */
    public void homeBlocking() {
        ElapsedTime timeout = new ElapsedTime();
        hoodServo.setPower(-HOMING_SERVO_POWER);

        while (!switchPressed() && timeout.seconds() < HOMING_TIMEOUT_SECONDS) {
            // spin until switch closes or we hit the timeout safety cutoff
        }

        hoodServo.setPower(0);
        currentState = LaunchState.IDLE;
    }

    // ── Main update loop ──────────────────────────────────────────────────────

    /**
     * Call every loop in TeleOp / Auto to drive the state machine.
     */
    public void update() {
        switch (currentState) {

            case IDLE:
                // waiting for a triggerLaunch call
                break;

            case POWER_SERVO_AND_FLYWHEEL:
                // Start servo and flywheel simultaneously, then move to STOP_SERVO
                // so the stopwatch is checked on the next loop iteration
                stopwatch.reset();
                hoodServo.setPower(DEFAULT_HOOD_SERVO_POWER);
                flywheel.setPower(DEFAULT_FLYWHEEL_POWER);
                currentState = LaunchState.STOP_SERVO;
                break;

            case STOP_SERVO:
                // Wait until the hood has rotated the required angle
                if (stopwatch.seconds() >= TIME_FACTOR * angleToRotate) {
                    hoodServo.setPower(0);
                    currentState = LaunchState.STOP_FLYWHEEL;
                }
                break;

            case STOP_FLYWHEEL:
                // Wait until ball has been launched, then brake the flywheel
                // NOTE: FLYWHEEL_SPINNING_PERIOD must be >> TIME_FACTOR * angleToRotate
                if (stopwatch.seconds() >= FLYWHEEL_SPINNING_PERIOD) {
                    flywheel.setPower(0); // ZeroPowerBehavior.BRAKE kicks in immediately
                    currentState = LaunchState.RETRACT_SERVO;
                    stopwatch.reset();
                }
                break;

            case RETRACT_SERVO:
                // Drive hood back down in reverse until limit switch is pressed
                hoodServo.setPower(-HOMING_SERVO_POWER);

                if (switchPressed()) {
                    // Switch hit — we're home
                    hoodServo.setPower(0);
                    currentState = LaunchState.IDLE;
                } else if (stopwatch.seconds() > HOMING_TIMEOUT_SECONDS) {
                    // Safety: switch never triggered — stop anyway to avoid damage
                    hoodServo.setPower(0);
                    currentState = LaunchState.IDLE;
                }
                break;
        }
    }

    // ── Trigger methods ───────────────────────────────────────────────────────

    /**
     * Trigger a launch at a manually specified hood angle (degrees).
     * Angle is clamped to MAX_HOOD_ANGLE (11°) automatically.
     * Ignored if a launch is already in progress.
     */
    public void triggerLaunch(double angleDeg) {
        if (currentState == LaunchState.IDLE) {
            angleToRotate = clampAngle(angleDeg);
            currentState  = LaunchState.POWER_SERVO_AND_FLYWHEEL;
        }
    }

    /**
     * Trigger a launch calculated from HuskyLens distance.
     * Computes geometric angle + interpolated correction, clamps, then fires.
     * Ignored if a launch is already in progress.
     */
    public void triggerLaunchForDistance(double distanceMm) {
        if (currentState == LaunchState.IDLE) {
            double geoAngle   = Math.toDegrees(Math.atan2(VERTICAL_RISE, distanceMm));
            double correction = interpolateCorrection(distanceMm);
            angleToRotate     = clampAngle(geoAngle + correction);
            currentState      = LaunchState.POWER_SERVO_AND_FLYWHEEL;
        }
    }

    // ── Status ────────────────────────────────────────────────────────────────

    /** True if any launch or retract sequence is currently running. */
    public boolean isBusy() {
        return currentState != LaunchState.IDLE;
    }

    public LaunchState getState() {
        return currentState;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Reads the limit switch.
     * Rev digital channels return FALSE when the circuit is closed (switch pressed).
     */
    private boolean switchPressed() {
        return !hoodSwitch.getState();
    }

    /**
     * Clamps hood angle to [0, MAX_HOOD_ANGLE].
     * Any value above 11 degrees is silently reduced to 11.
     */
    private double clampAngle(double angleDeg) {
        return Math.max(0, Math.min(angleDeg, MAX_HOOD_ANGLE));
    }

    private double interpolateCorrection(double distanceMm) {
        if (distanceMm <= CAL_DISTANCES[0])
            return CAL_CORRECTIONS[0];

        int last = CAL_DISTANCES.length - 1;
        if (distanceMm >= CAL_DISTANCES[last])
            return CAL_CORRECTIONS[last];

        for (int i = 0; i < last; i++) {
            if (distanceMm >= CAL_DISTANCES[i] && distanceMm < CAL_DISTANCES[i + 1]) {
                double t = (distanceMm - CAL_DISTANCES[i])
                         / (CAL_DISTANCES[i + 1] - CAL_DISTANCES[i]);
                return CAL_CORRECTIONS[i] + t * (CAL_CORRECTIONS[i + 1] - CAL_CORRECTIONS[i]);
            }
        }
        return 0.0;
    }
}
