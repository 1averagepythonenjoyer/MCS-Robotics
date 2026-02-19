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
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Autonomous(name = "AutoOp: Base (Pedro Prepped)")
public class mecanumAutoOp extends LinearOpMode {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(5);
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

    private DcMotorEx frontLeft;
    private DcMotorEx frontRight;
    private DcMotorEx backLeft;
    private DcMotorEx backRight;

     private Follower follower;

    double max_velocity_tps;
    int slow_mode_multi;

    final double kP_translation = 0.05;
    final double kP_heading = 0.01;

    public void createFollower(HardwareMap hardwareMap) {
        follower = new FollowerBuilder(followerConstants, hardwareMap)
//                .pathConstraints(
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(localiserConstants)
                .build();
    }

    private void INIT_HARDWARE() {
        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");

        // Pedropathing handles localization, so drive motors must be set to RUN_WITHOUT_ENCODER
        // This prevents the internal motor PID from fighting Pedro's PID
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        createFollower(hardwareMap);
    }

    @Override
    public void runOpMode() {
        INIT_HARDWARE();

        follower.setStartingPose(new Pose(0, 0, 0));

        telemetry.addData("Status", "Initialized. Waiting for Start...");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            PathChain path = follower.pathBuilder()
                    .addPath(new BezierLine(new Pose(0, 0, 0), new Pose(100, 0, 0))).build();
            follower.followPath(path);
            while (opModeIsActive() && follower.isBusy()) {
                
                 follower.update();

                 telemetry.addData("X", follower.getPose().getX());
                 telemetry.addData("Y", follower.getPose().getY());
                 telemetry.addData("Heading", follower.getPose().getHeading());
                
                telemetry.addData("Slow Mode Active", gamepad1.left_bumper);
                telemetry.update();
            }
            follower.breakFollowing();
        }
    }
}
