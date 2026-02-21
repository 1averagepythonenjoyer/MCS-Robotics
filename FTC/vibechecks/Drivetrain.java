package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Drivetrain {

    private DcMotorEx frontLeft, frontRight, backLeft, backRight;

    // goBilda specs
    private static final double WHEEL_MAX_RPM        = 312.0;
    private static final double COUNTS_PER_MOTOR_REV = 28.0;
    private static final double DRIVE_GEAR_REDUCTION = (1.0 + (46.0 / 17.0)) * (1.0 + (46.0 / 11.0)); // ~19.20
    private static final double COUNTS_PER_WHEEL_REV = COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION;    // ~537.69
    public  final double MAX_VELOCITY_TPS            = (WHEEL_MAX_RPM / 60.0) * COUNTS_PER_WHEEL_REV;

    public Drivetrain(HardwareMap hwMap) {
        frontLeft  = hwMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hwMap.get(DcMotorEx.class, "frontRight");
        backLeft   = hwMap.get(DcMotorEx.class, "backLeft");
        backRight  = hwMap.get(DcMotorEx.class, "backRight");

        // Pedro pathing handles localization — motors must NOT fight its PID
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);
    }

    /**
     * Call every loop in TeleOp.
     * forwardBack / strafe / turn are raw gamepad axis values (-1.0 to 1.0).
     * slowMode halves speed when true (left bumper).
     */
    public void drive(float forwardBack, float strafe, float turn, boolean slowMode) {
        final float DEADZONE = 0.1f;

        if (Math.abs(forwardBack) < DEADZONE) forwardBack = 0;
        if (Math.abs(strafe)      < DEADZONE) strafe      = 0;
        if (Math.abs(turn)        < DEADZONE) turn        = 0;

        float lf = (forwardBack + strafe) + turn;
        float lb = (forwardBack - strafe) + turn;
        float rf =  forwardBack - strafe  - turn;
        float rb = (forwardBack + strafe) - turn;

        // Normalize so no value exceeds 1.0
        float max = Math.max(Math.max(Math.abs(lf), Math.abs(rf)),
                             Math.max(Math.abs(lb), Math.abs(rb)));
        if (max < 1f) max = 1f;

        float scale = 1f / (max * (slowMode ? 2 : 1));

        frontLeft.setPower(lf * scale);
        frontRight.setPower(rf * scale);
        backLeft.setPower(lb * scale);
        backRight.setPower(rb * scale);
    }

    /** Full stop. */
    public void stop() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
}
