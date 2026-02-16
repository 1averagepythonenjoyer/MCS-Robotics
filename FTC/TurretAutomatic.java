package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;


@TeleOp(name = "PidTurret (Blocks to Java)")
public class TurretAutomatic extends LinearOpMode {
    private DcMotor turretMotor;

    private double kP = 0.0001;
    private double kD = 0.0000;
    private double goalX = 0;
    private double lastError = 0;
    private double angleTolerance = 0.2;
    private final double MAX_POWER = 0.6;
    private double power = 0;

    private final ElapsedTime timer = new ElapsedTime();

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
//            AprilTagDetection curID = new AprilTagDetection();
            double deltaTime = timer.seconds();

//            double error = goalX - curID.ftcPose.bearing;
            double error = goalX - 0.1; // Placeholder
            double pTerm = error * kP;

            double dTerm = 0;
            if (deltaTime > 0) {
                dTerm = ((error - lastError) / deltaTime) * kD;
            }
            if (Math.abs(error) < angleTolerance) {
                power = 0;
            } else {
                power = Range.clip(pTerm + dTerm, -MAX_POWER, MAX_POWER);
            }

            // Safety goes here.

            turretMotor.setPower(power);
            lastError = error;

            telemetry.addData("Motor Power", turretMotor.getPower());
            telemetry.addLine("Main Turret Loop");
            telemetry.update();
        }
    }
}
