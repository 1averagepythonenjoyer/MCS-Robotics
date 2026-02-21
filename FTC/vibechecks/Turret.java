package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class Turret {

    private DcMotorEx turretMotor;

    // Encoder constants (from TurretManual)
    private static final double COUNTS_PER_MOTOR_REV  = 288.0;
    private static final double WHEEL_CIRCUMFERENCE   = 54.0;
    private static final double COUNTS_PER_WHEEL_REV  = COUNTS_PER_MOTOR_REV;
    private static final double MAX_RPM               = 125.0;
    private static final double MAX_TICKS_PER_SECOND  = MAX_RPM / 60.0 * COUNTS_PER_WHEEL_REV;

    // PD constants for auto tracking (from TurretAutomatic)
    private double kP             = 0.0001;
    private double kD             = 0.0000;
    private double angleTolerance = 0.2;
    private double lastError      = 0;
    private static final double MAX_POWER = 0.6;

    private final ElapsedTime timer = new ElapsedTime();

    public Turret(HardwareMap hwMap) {
        turretMotor = hwMap.get(DcMotorEx.class, "turretMotor");
        turretMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        turretMotor.setDirection(DcMotorEx.Direction.FORWARD);
    }

    /**
     * MANUAL MODE — call every loop in TeleOp.
     * Pass gamepad2.right_stick_x directly.
     */
    public void updateManual(double gamepadInput) {
        final double DEADZONE            = 0.1;
        final double VELOCITY_COEFFICIENT = 0.2;

        if (Math.abs(gamepadInput) < DEADZONE) gamepadInput = 0;
        gamepadInput = Range.clip(gamepadInput, -1.0, 1.0);

        double velocity = gamepadInput * VELOCITY_COEFFICIENT * MAX_TICKS_PER_SECOND;
        turretMotor.setVelocity(velocity);
    }

    /**
     * AUTO TRACKING MODE — call every loop when using HuskyLens bearing.
     * Pass tag.bearingDeg from HuskyLensReader — turret will PD-track to zero bearing.
     * Returns current motor power for telemetry if needed.
     */
    public double updateAuto(double bearingDeg) {
        double deltaTime = timer.seconds();
        timer.reset();

        double error = 0 - bearingDeg; // goal is to center the tag (bearing = 0)
        double pTerm = error * kP;

        double dTerm = 0;
        if (deltaTime > 0) {
            dTerm = ((error - lastError) / deltaTime) * kD;
        }

        double power = (Math.abs(error) < angleTolerance)
                ? 0
                : Range.clip(pTerm + dTerm, -MAX_POWER, MAX_POWER);

        turretMotor.setPower(power);
        lastError = error;
        return power;
    }

    /** Full stop. */
    public void stop() {
        turretMotor.setPower(0);
        turretMotor.setVelocity(0);
    }
}
