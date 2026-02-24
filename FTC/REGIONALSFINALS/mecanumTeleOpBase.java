////modified for gobilda motors
//
//package org.firstinspires.ftc.teamcode;
//
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//
//@TeleOp(name = "TeleOp: Base (Pedro Prepped)")
//public class mecanumTeleOpBase extends LinearOpMode {
//
//    private DcMotorEx frontLeft;
//    private DcMotorEx frontRight;
//    private DcMotorEx backLeft;
//    private DcMotorEx backRight;
//
//    // // TODO: declare pedro follower
//    // private Follower follower;
//
//    double max_velocity_tps;
//    int slow_mode_multi;
//
//    private void INIT_ROBOT_SPECS() {
//        // goBilda specs
//        double wheel_max_rpm = 312.0;
//        double COUNTS_PER_MOTOR_REV = 28.0;
//        double DRIVE_GEAR_REDUCTION = (1.0 + (46.0 / 17.0)) * (1.0 + (46.0 / 11.0)); // 19.2032...
//
//        double COUNTS_PER_WHEEL_REV = COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION; // ~537.689
//        max_velocity_tps = (wheel_max_rpm / 60.0) * COUNTS_PER_WHEEL_REV;
//    }
//
//    private void INIT_HARDWARE() {
//        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
//        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
//        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
//        backRight = hardwareMap.get(DcMotorEx.class, "backRight");
//
//        // Pedropathing handles localization, so drive motors must be set to RUN_WITHOUT_ENCODER
//        // This prevents the internal motor PID from fighting Pedro's PID
//        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//
//        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//
//        frontLeft.setDirection(DcMotor.Direction.REVERSE);  //need to change these
//        backLeft.setDirection(DcMotor.Direction.REVERSE);
//        frontRight.setDirection(DcMotor.Direction.FORWARD);
//        backRight.setDirection(DcMotor.Direction.FORWARD);
//    }
//
//    @Override
//    public void runOpMode() {
//        INIT_ROBOT_SPECS();
//        INIT_HARDWARE();
//
//        // // TODO(pedro): Initialize the Follower and set starting pose
//        // follower = new Follower(hardwareMap);
//        // follower.setStartingPose(new Pose(0, 0, 0));
//
//        telemetry.addData("Status", "Initialized. Waiting for Start...");
//        telemetry.update();
//
//        waitForStart();
//
//        if (opModeIsActive()) {
//            while (opModeIsActive()) {
//
//                // // TODO (Pedro): Call follower.update every loop to keep Pinpoint tracking alive
//                // follower.update();
//
//                MECANUM_DRIVE();
//
//                // // TODO (Pedro): Print actual Pinpoint coordinates to telemetry
//                // telemetry.addData("X", follower.getPose().getX());
//                // telemetry.addData("Y", follower.getPose().getY());
//                // telemetry.addData("Heading", follower.getPose().getHeading());
//
//                telemetry.addData("Slow Mode Active", gamepad1.left_bumper);
//                telemetry.update();
//            }
//        }
//    }
//
//    private void MECANUM_DRIVE() {
//        float deadzone = 0.1f;
//
//
//        float forwardBack = -gamepad1.left_stick_y;
//        float strafe = gamepad1.right_stick_x;
//        float turn = gamepad1.left_stick_x;
//
//        if (Math.abs(forwardBack) < deadzone) forwardBack = 0;
//        if (Math.abs(strafe) < deadzone) strafe = 0;
//        if (Math.abs(turn) < deadzone) turn = 0;
//
//
//        float leftFrontPower = (forwardBack + strafe) + turn;
//        float leftBackPower = (forwardBack - strafe) + turn;
//        float rightFrontPower = forwardBack - strafe - turn;
//        float rightBackPower = (forwardBack + strafe) - turn;
//
//        slow_mode_multi = gamepad1.left_bumper ? 2 : 1;
//
//        // Normalization (prevennts sending a power value > 1.0)
//        float max_motor_val = Math.max(Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower)),
//                Math.max(Math.abs(leftBackPower), Math.abs(rightBackPower)));
//        if (max_motor_val < 1f) {
//            max_motor_val = 1f;
//        }
//
//        float scale = 1f / (max_motor_val * slow_mode_multi);
//
//        frontLeft.setPower(leftFrontPower * scale);
//        frontRight.setPower(rightFrontPower * scale);
//        backLeft.setPower(leftBackPower * scale);
//        backRight.setPower(rightBackPower * scale);
//    }
//}
