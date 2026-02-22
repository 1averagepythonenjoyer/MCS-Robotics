package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class Turret {

    private DcMotorEx turretMotor;

    // ── Encoder constants ─────────────────────────────────────────────────────
    private static final double COUNTS_PER_MOTOR_REV = 288.0;
    private static final double MAX_RPM              = 125.0;
    private static final double MAX_TICKS_PER_SECOND = MAX_RPM / 60.0 * COUNTS_PER_MOTOR_REV;

    // ── Gear ratio ────────────────────────────────────────────────────────────
    // Core Hex drives a 72T gear which meshes with 240T gear on the turret.
    // Turret revolutions per motor revolution = 72 / 240 = 0.3
    // To rotate turret 90°: 0.25 turret revs / 0.3 = 0.833 motor revs = 240 counts
    private static final double GEAR_RATIO        = 72.0 / 240.0;
    private static final int    COUNTS_PER_90_DEG = 240; // ±240 counts = ±90° turret rotation
    private static final int    SOFT_LIMIT_COUNTS = COUNTS_PER_90_DEG; // 240

    // ── PD constants for auto tracking ───────────────────────────────────────
    private double kP             = 0.0001;
    private double kD             = 0.0000;
    private double angleTolerance = 0.2; // degrees — within this, treat as aligned
    private double lastError      = 0;
    private static final double MAX_POWER = 0.6;

    private final ElapsedTime timer = new ElapsedTime();

    // ── Manual drive constants ────────────────────────────────────────────────
    private static final double DEADZONE             = 0.1;
    private static final double VELOCITY_COEFFICIENT = 0.2;

    // ─────────────────────────────────────────────────────────────────────────

    public Turret(HardwareMap hwMap) {
        turretMotor = hwMap.get(DcMotorEx.class, "turretMotor");

        // Zero the encoder at whatever position the turret is physically at.
        // Manually align the turret to face forward before each match.
        turretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turretMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        turretMotor.setDirection(DcMotorEx.Direction.FORWARD);
        turretMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**
     * MANUAL MODE — call every loop in TeleOp.
     * Pass gamepad2.right_stick_x directly.
     * Enforces ±90° software limits — turret stops and holds at boundary.
     */
    public void updateManual(double gamepadInput) {
        if (Math.abs(gamepadInput) < DEADZONE) gamepadInput = 0;
        gamepadInput = Range.clip(gamepadInput, -1.0, 1.0);

        // Block movement at software limits
        int currentPos = turretMotor.getCurrentPosition();
        if (currentPos >= SOFT_LIMIT_COUNTS  && gamepadInput > 0) gamepadInput = 0;
        if (currentPos <= -SOFT_LIMIT_COUNTS && gamepadInput < 0) gamepadInput = 0;

        if (gamepadInput == 0) {
            holdPosition();
            return;
        }

        if (turretMotor.getMode() == DcMotor.RunMode.RUN_TO_POSITION) turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        double velocity = gamepadInput * VELOCITY_COEFFICIENT * MAX_TICKS_PER_SECOND;
        turretMotor.setVelocity(velocity);
    }

    /**
     * AUTO TRACKING MODE — call every loop when using HuskyLens bearing.
     * Pass tag.bearingDeg from HuskyLensReader — turret will PD-track to zero bearing.
     * Enforces ±90° software limits — auto-align stops and holds at boundary.
     * Returns current motor power for telemetry if needed.
     */
    public double updateAuto(double bearingDeg) {
        double deltaTime = timer.seconds();
        timer.reset();

        double error  = 0 - bearingDeg; // goal is to center the tag (bearing = 0)
        double pTerm  = error * kP;

        double dTerm = 0;
        if (deltaTime > 0) {
            dTerm = ((error - lastError) / deltaTime) * kD;
        }

        double power = (Math.abs(error) < angleTolerance)
                ? 0
                : Range.clip(pTerm + dTerm, -MAX_POWER, MAX_POWER);

        // Enforce software limits in auto mode too
        int currentPos = turretMotor.getCurrentPosition();
        if (currentPos >= SOFT_LIMIT_COUNTS  && power > 0) power = 0;
        if (currentPos <= -SOFT_LIMIT_COUNTS && power < 0) power = 0;

        if (power == 0) {
            holdPosition();
        } else {
            turretMotor.setPower(power);
        }

        lastError = error;
        return power;
    }

    /**
     * Holds the turret at its current position using RUN_TO_POSITION.
     * Called automatically when at a software limit or when input is zero.
     */
    private void holdPosition() {
        turretMotor.setTargetPosition(turretMotor.getCurrentPosition());
        turretMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turretMotor.setPower(MAX_POWER);
    }

    /**
     * Returns the current turret angle in degrees.
     * Positive = right, negative = left, 0 = forward.
     */
    public double getAngleDeg() {
        return turretMotor.getCurrentPosition() / (double) COUNTS_PER_90_DEG * 90.0;
    }

    /**
     * Returns true if the turret is at or beyond either software limit.
     */
    public boolean isAtLimit() {
        int pos = turretMotor.getCurrentPosition();
        return pos >= SOFT_LIMIT_COUNTS || pos <= -SOFT_LIMIT_COUNTS;
    }

    /** Full stop — releases hold. */
    public void stop() {
        turretMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        turretMotor.setPower(0);
        turretMotor.setVelocity(0);
    }
}
