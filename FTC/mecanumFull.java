//OTHER MECANUM FILE ORIGINALLY ON GITHUB DOUBLE CHECK IF THIS WILL WORK



//teleop vs autoop for manual vs auto (selects itself)
package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.robotcore.external.JavaUtil;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


@TeleOp(name = "mecanumFull (Blocks to Java)")
public class mecanumFull extends LinearOpMode {


    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;


    double ROBOT_WHEEL_TO_WHEEL_WIDTH_MM;
    double max_velocity_tps;
    double self_x;
    float forwardBack;
    double change_x;
    double self_y;
    float strafe;
    double change_y;
    float turn;
    double move_mm;
    double self_heading_deg;
    int move_ticks;
    double COUNTS_PER_MM;
    int slow_mode_multi;


    /*set and calculate motor values */
    private void INIT_ROBOT_SPECS() {
        double wheel_max_rpm;
        double COUNTS_PER_MOTOR_REV;
        double DRIVE_GEAR_REDUCTION;
        double WHEEL_DIAMETER_MM;
        double WHEEL_CIRCUMFERENCE_MM;
        double COUNTS_PER_WHEEL_REV;

        ROBOT_WHEEL_TO_WHEEL_WIDTH_MM = 405.85;
        //gobilda specs
        wheel_max_rpm = 312.0;
        COUNTS_PER_MOTOR_REV = 28.0;

        DRIVE_GEAR_REDUCTION = (1.0 + (46.0 / 17.0)) * (1.0 + (46.0 / 11.0)); //needs .0 otherwise java automatically rounds

        WHEEL_DIAMETER_MM = 104.0;
        WHEEL_CIRCUMFERENCE_MM = WHEEL_DIAMETER_MM * Math.PI;

        COUNTS_PER_WHEEL_REV = COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION; // ~537.689
        COUNTS_PER_MM = COUNTS_PER_WHEEL_REV / WHEEL_CIRCUMFERENCE_MM;
        max_velocity_tps = (wheel_max_rpm / 60.0) * COUNTS_PER_WHEEL_REV;
    }


    /*set starting coordinates */
    private void INIT_START_COORDS() {
        int self_heading_rad;
        // assume start at origin, pointing north for testing ourposes
        // Assume origin for testing
        self_x = 0;
        // Assume origin for testing
        self_y = 0;
        self_heading_rad = 90;
        self_heading_deg = AngleUnit.DEGREES.fromUnit(AngleUnit.RADIANS, self_heading_rad);
    }


//main loop to be run
    @Override
    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        INIT_START_COORDS();
        INIT_ROBOT_SPECS();
        MOTOR_SETTINGS_ENCODER();

        waitForStart();

        if (opModeIsActive()) {
            slow_mode_multi = 1;

            while (opModeIsActive()) {
                MECANUM_DRIVE_VELOCITY();
                // This will now show correctly because we fixed the logic below
                telemetry.addData("Slow Mode Active", gamepad1.left_bumper);
                telemetry.update();
            }
        }
    }


    /**
     * Motor presets for teleop
     */
    private void MOTOR_SETTINGS_ENCODER() {
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setDirection(DcMotor.Direction.FORWARD);


        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setDirection(DcMotor.Direction.FORWARD);
    }




    /**
     * Motor presets for autoop
     */
    private void AUTO_MOTOR_SETTINGS() {
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setDirection(DcMotor.Direction.REVERSE);


        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setDirection(DcMotor.Direction.FORWARD);
    }


    /*TeleOp Manual Control Function */
    private void MECANUM_DRIVE_VELOCITY() {
        float leftFrontVelo, rightFrontVelo, leftBackVelo, rightBackVelo;
        float max_motor_val;
        float deadzone = 0.1f;

        // Determining movement based on gamepad inputs
        forwardBack = gamepad1.left_stick_y;
        telemetry.addData("forwardBack", JavaUtil.formatNumber(forwardBack, 5));
        strafe = gamepad1.right_stick_x;
        turn = gamepad1.left_stick_x;

        if (Math.abs(forwardBack) < deadzone) forwardBack = 0;
        if (Math.abs(strafe) < deadzone) strafe = 0;
        if (Math.abs(turn) < deadzone) turn = 0;

        leftFrontVelo = (forwardBack + strafe) + turn;
        leftBackVelo = (forwardBack - strafe) + turn;
        rightFrontVelo = forwardBack - strafe - turn;
        rightBackVelo = (forwardBack + strafe) - turn;



        if (gamepad1.left_bumper) {
            slow_mode_multi = 2; // Divides speed by 2
        } else {
            slow_mode_multi = 1; // Normal speed
        }


        // Normalisation to prevent input velocity being > max_velocity_tps
        max_motor_val = Math.max(Math.max(Math.abs(leftFrontVelo), Math.abs(rightFrontVelo)), Math.max(Math.abs(leftBackVelo), Math.abs(rightBackVelo)));
        if (max_motor_val < 1f) {
            max_motor_val = 1f;
        }


        float scale = (float) (max_velocity_tps) / (max_motor_val * slow_mode_multi);
        leftFrontVelo  = leftFrontVelo  * scale;
        rightFrontVelo = rightFrontVelo * scale;
        leftBackVelo   = leftBackVelo   * scale;
        rightBackVelo  = rightBackVelo  * scale;


        // Setting Motor Velocity
        ((DcMotorEx) frontLeft).setVelocity(leftFrontVelo);
        ((DcMotorEx) frontRight).setVelocity(rightFrontVelo);
        ((DcMotorEx) backLeft).setVelocity(leftBackVelo);
        ((DcMotorEx) backRight).setVelocity(rightBackVelo);


        telemetry.addData("frontRight", JavaUtil.formatNumber(rightFrontVelo, 5));
        telemetry.addData("left motor velocity", JavaUtil.formatNumber(leftBackVelo, 5));
        telemetry.addData("gamepad_leftstick_y", JavaUtil.formatNumber(gamepad1.left_stick_y, 5));
        telemetry.addData("gamepad_leftstick_x", JavaUtil.formatNumber(gamepad1.left_stick_x, 5));
    }


    /**
     * takes (x,y) input as a coordinate on the arena, calculates distance and turning angle to new position. sends turning signal to motors, then move distance
     */
    private void MOVE_TARGET(double target_x, double target_y) {


        int anti_spin_multiplier;
        double target_angle_rad;
        double target_angle_deg;
        double turn_angle_deg;
        double turn_mm;
        double turn_ticks;


        // may want to reduce speed later
        anti_spin_multiplier = 1;
        // calculate changes required
        change_x = target_x - self_x;
        change_y = target_y - self_y;

        //%%%%%%%%%%   TURNING    %%%%%%%%%%%%%%%
        target_angle_rad = Math.atan2(change_y, change_x);
        target_angle_deg = AngleUnit.DEGREES.fromUnit(AngleUnit.RADIANS, target_angle_rad);
        turn_angle_deg = target_angle_deg - self_heading_deg;

        while (turn_angle_deg > 180) turn_angle_deg -=360;  //normalising values to find shortest turn angle.
        while (turn_angle_deg <=-180) turn_angle_deg +=360;

        turn_mm = Math.PI * ROBOT_WHEEL_TO_WHEEL_WIDTH_MM * (turn_angle_deg / 360.0);  //calculate ticks sent to motors.
        turn_ticks = turn_mm * COUNTS_PER_MM;




        // send values to encoders: values are cumulative in RUN_TO_POSITION, so we must add/subtract move-ticks to the current position of each motor
        frontLeft.setTargetPosition(frontLeft.getCurrentPosition() + (int)turn_ticks);
        backLeft.setTargetPosition(backLeft.getCurrentPosition() + (int)turn_ticks);
        frontRight.setTargetPosition(frontRight.getCurrentPosition() - (int)turn_ticks);
        backRight.setTargetPosition(backRight.getCurrentPosition() - (int)turn_ticks);


        ((DcMotorEx) frontLeft).setVelocity(anti_spin_multiplier * max_velocity_tps);
        ((DcMotorEx) frontRight).setVelocity(anti_spin_multiplier * max_velocity_tps);
        ((DcMotorEx) backLeft).setVelocity(anti_spin_multiplier * max_velocity_tps);
        ((DcMotorEx) backRight).setVelocity(anti_spin_multiplier * max_velocity_tps);
        while (opModeIsActive() && (frontLeft.isBusy() || frontRight.isBusy() || backLeft.isBusy() || backRight.isBusy())) {
            telemetry.addData("Status", "Turning to target...");
            telemetry.update();
            idle();
        }
        self_heading_deg = self_heading_deg + turn_angle_deg;




        move_mm = Math.sqrt(Math.pow(change_x, 2) + Math.pow(change_y, 2));
        move_ticks = (int) (move_mm * COUNTS_PER_MM);


        telemetry.addData("change_x", change_x);
        telemetry.addData("change_y", change_y);
        telemetry.addData("move_mm", move_mm);
        telemetry.addData("move_ticks", move_ticks);


        frontLeft.setTargetPosition(frontLeft.getCurrentPosition() + move_ticks);
        frontRight.setTargetPosition(frontRight.getCurrentPosition() + move_ticks);
        backLeft.setTargetPosition(backLeft.getCurrentPosition() + move_ticks);
        backRight.setTargetPosition(backRight.getCurrentPosition() + move_ticks);


        ((DcMotorEx) frontLeft).setVelocity(max_velocity_tps);
        ((DcMotorEx) frontRight).setVelocity(max_velocity_tps);
        ((DcMotorEx) backLeft).setVelocity(max_velocity_tps);
        ((DcMotorEx) backRight).setVelocity(max_velocity_tps);


        while (opModeIsActive() && (frontLeft.isBusy() || frontRight.isBusy() || backLeft.isBusy() || backRight.isBusy())) {
            telemetry.addData("Status", "Moving to target...");
            telemetry.update();
            idle();
        }
        self_x = target_x;
        self_y = target_y;
        self_heading_deg = self_heading_deg % 360;
        if (self_heading_deg < 0) self_heading_deg += 360; //normalises bearing value between 0-360
    }
}
