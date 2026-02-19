package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Autonomous(name = "Pedro Test: 500mm Straight")
public class mecanumAutoOp extends LinearOpMode {

    // --- YOUR CONSTANTS (Keeping these exactly as you had them) ---
    public static FollowerConstants followerConstants = new FollowerConstants().mass(5);
    public static MecanumConstants driveConstants = new MecanumConstants()
        .maxPower(1)
        .rightFrontMotorName("frontRight")
        .rightRearMotorName("backRight")
        .leftRearMotorName("backLeft")
        .leftFrontMotorName("frontLeft")
        .leftFrontMotorDirection(DcMotorEx.Direction.FORWARD)
        .leftRearMotorDirection(DcMotorEx.Direction.REVERSE)
        .rightFrontMotorDirection(DcMotorEx.Direction.FORWARD)
        .rightRearMotorDirection(DcMotorEx.Direction.FORWARD);

    public static PinpointConstants localiserConstants = new PinpointConstants()
            .forwardPodY(-168)
            .strafePodX(-84)
            .distanceUnit(DistanceUnit.MM)
            .hardwareMapName("pinpoint")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .yawScalar(-1);

    private Follower follower;
    private PathChain testPath;

    @Override
    public void runOpMode() {
        // 1. Initialize Follower
        follower = new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(localiserConstants)
                .build();

        // 2. Build the path BEFORE the match starts
        // If Pinpoint is in MM, Pose(500, 0, 0) moves 50cm forward.
        testPath = follower.pathBuilder()
                .addPath(new BezierLine(new Pose(0, 0, 0), new Pose(500, 0, 0)))
                .build();

        follower.setStartingPose(new Pose(0, 0, 0));

        // 3. Setup Motors (Pedro handles the rest)
        setupDriveMotors();

        telemetry.addLine("Ready. Robot will move 500mm forward.");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            // Start following
            follower.followPath(testPath);

            // 4. THE LOOP: This must run for the robot to move
            while (opModeIsActive() && follower.isBusy()) {
                follower.update(); // Keep the PID loop running

                // Real-time Telemetry
                Pose currentPose = follower.getPose();
                telemetry.addData("X Position", currentPose.getX());
                telemetry.addData("Y Position", currentPose.getY());
                telemetry.addData("Heading (Deg)", Math.toDegrees(currentPose.getHeading()));
                telemetry.update();
            }

            // Path finished
            follower.breakFollowing();
        }
    }

    private void setupDriveMotors() {
        // We get them from hardwareMap just to set the zero power behavior and run mode
        String[] names = {"frontLeft", "frontRight", "backLeft", "backRight"};
        for (String name : names) {
            DcMotorEx motor = hardwareMap.get(DcMotorEx.class, name);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
    }
}
