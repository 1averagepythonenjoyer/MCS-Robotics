package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "PidTurret (Blocks to Java)")
public class TurretManual extends LinearOpMode {
    private DcMotor turretMotor;
    private float turningValue;

    private void MOTOR_SETTINGS_ENCODER() {
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turretMotor.setDirection(DcMotor.Direction.FORWARD);
    }

    @Override
    public void runOpMode() {
        turretMotor = hardwareMap.get(DcMotor.class, "turretMotor");

        waitForStart();
        if (opModeIsActive()) {
            return;
        }
        while (opModeIsActive()) {
//            float turretVelo;
//            float maxMotorVal;
            float deadzone = 0.1f;
            turningValue = gamepad1.right_stick_x;
            if (Math.abs(turningValue) < deadzone) turningValue = 0;
            turningValue = (float) Math.max(turningValue, 1.0);
            turretMotor.setPower(turningValue);

            telemetry.addData("Motor Power", turretMotor.getPower());
            telemetry.addLine("Main Turret Loop");
            telemetry.update();
        }
    }
}
