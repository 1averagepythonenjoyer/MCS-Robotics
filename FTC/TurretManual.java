package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "PidTurret (Blocks to Java)")
public class TurretManual extends LinearOpMode {
    private DcMotorEx turretMotor;

    static final double COUNTS_PER_MOTOR_REV = 288.0;
    static final double WHEEL_CIRCUMFERENCE = 54.0;
    static final double COUNTS_PER_WHEEL_REV = COUNTS_PER_MOTOR_REV;
    static final double COUNTS_PER_MM = COUNTS_PER_WHEEL_REV / WHEEL_CIRCUMFERENCE;
    static final double MAX_RPM = 125.0;
    static final double MAX_TICKS_PER_SECOND = MAX_RPM / 60.0 * COUNTS_PER_WHEEL_REV;

    private void TURRET_MOTOR_HEX_SETTINGS_ENCODER() {
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turretMotor.setDirection(DcMotor.Direction.FORWARD);
    }

    @Override
    public void runOpMode() {
        turretMotor = hardwareMap.get(DcMotorEx.class, "turretMotor");
        TURRET_MOTOR_HEX_SETTINGS_ENCODER();

        waitForStart();
        if (opModeIsActive()) {
            return;
        }
        while (opModeIsActive()) {
            float ticksPerSecond = 125.0f;
            float deadzone = 0.1f;
            double gamepadInput = gamepad2.right_stick_x;
            if (Math.abs(gamepadInput) < deadzone) gamepadInput = 0;
            gamepadInput = (float) Math.max(-1.0, Math.min(gamepadInput, 1.0));
            double velocityCoefficient = 0.2f;
            double velocity = gamepadInput * velocityCoefficient * MAX_TICKS_PER_SECOND;
            turretMotor.setVelocity(velocity);

            telemetry.addData("Motor Velocity", velocity);
            telemetry.addLine("Main Turret Loop");
            telemetry.update();
        }
    }
}
