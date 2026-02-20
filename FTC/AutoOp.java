package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.LinkedList;
import java.util.Queue;

@Autonomous(name = "AutoOp")
public class AutoOp extends OpMode {
    private final FollowerConstants followerConstants = new FollowerConstants()
            .mass(5);
    private final MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName("frontRight")
            .rightRearMotorName("backRight")
            .leftRearMotorName("backLeft")
            .leftFrontMotorName("frontLeft")
            .leftFrontMotorDirection(DcMotorEx.Direction.FORWARD)
            .leftRearMotorDirection(DcMotorEx.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorEx.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorEx.Direction.FORWARD);

    private final PinpointConstants localiserConstants = new PinpointConstants()
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
    private Timer pathTimer, opModeTimer, launcherTimer;
    public enum PathState {
        DRIVE_START_POS_TO_SHOOT_POS,
        SHOOT_PRELOAD,
    }
    PathState pathState;
    private final Pose startPose = new Pose(21.46682188591386, 119.64610011641443, Math.toRadians((135)));
    private final Pose shootPose = new Pose(68.57275902211875, 74.71944121071013, Math.toRadians((135)));
    private final Pose aPose = new Pose(21.46682188591386, 119.64610011641443, Math.toRadians((135)));
    private final Pose bPose = new Pose(68.57275902211875, 74.71944121071013, Math.toRadians((135)));
    private PathChain driveStartPosShootPos;

    public enum LauncherState {
        IDLE,
        SPINNING_UP,
        LAUNCHING,
        RESET,
//
//        private final ServoId servoId;
//
//        LauncherState(ServoId servoId) {
//            this.servoId = servoId;
//        }
    }

    private ServoId servoId;

    public enum ServoId {
        s1,
        s2,
        s3,
    }
    LauncherState launcherState;

    private Servo storageServo1;
    private Servo storageServo2;
    private Servo storageServo3;

    Queue<ServoId> servosToKick = new LinkedList<>();

    private static final double servoStartingPosition = 0;
    private static final double servoKickedPosition = 0.3;
    private static final double DELAY_KICKER_STAYING_UP_TIME = 0.3;
    private static final double DELAY_BETWEEN_KICKS = 1;

    private DcMotor turretMotor;

    private double kP = 0.0001;
    private double kD = 0.0000;
    private double goalX = 0;
    private double lastError = 0;
    private double angleTolerance = 0.2;
    private final double MAX_POWER = 0.6;
    private double power = 0;

    private final Timer turretTimer = new Timer();

    public void buildPaths() {
        driveStartPosShootPos = follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
                .build();
    }

    public void pathingLogic() {
        switch(pathState) {
            case DRIVE_START_POS_TO_SHOOT_POS:
                follower.followPath(driveStartPosShootPos, true);
                setPathState(PathState.SHOOT_PRELOAD);
                break;
            case SHOOT_PRELOAD:
                if (!follower.isBusy()) {
                    telemetry.addLine("Done Path 1");
                    setLauncherState(LauncherState.SPINNING_UP);
                }
                break;
            default:
                telemetry.addLine("No State Commanded");
                break;
        }
    }
    public void launcherLogic() {
        switch(launcherState) {
            case IDLE:
                telemetry.addLine("Launcher is idle.");
                if (!servosToKick.isEmpty()) {
                    servoId = servosToKick.poll();
                    Servo current = getCurrentServo();
                    if (current != null) {
                        current.setPosition(servoKickedPosition);
                    }
                    launcherTimer.resetTimer();
                    launcherState = LauncherState.SPINNING_UP;
                }
                break;
            case SPINNING_UP:
                telemetry.addLine("Spinning up flywheel.");
                launcherState = LauncherState.LAUNCHING;
                // Todo
                break;
            case LAUNCHING:
                telemetry.addData("Launching from servo ID", servoId);
                if (launcherTimer.getElapsedTimeSeconds() >= DELAY_KICKER_STAYING_UP_TIME) {
                    Servo current = getCurrentServo();
                    if (current != null) {
                        current.setPosition(servoStartingPosition);
                    }
                    launcherState = LauncherState.RESET;
                    launcherTimer.resetTimer();
                }
                break;
            case RESET:
                telemetry.addLine("Launcher is resetting.");
                if (launcherTimer.getElapsedTimeSeconds() >= DELAY_BETWEEN_KICKS) {
                    launcherState = LauncherState.IDLE;
                }
                break;
            default:
                telemetry.addLine("No launcher state.");
                break;
        }
    }

    public Servo getCurrentServo() {
        if (servoId == null) return null;
        switch (servoId) {
            case s1:
                return storageServo1;
            case s2:
                return storageServo2;
            default:
                return storageServo3;
        }
    }

    public void createFollower(HardwareMap hardwareMap) {
        follower = new FollowerBuilder(followerConstants, hardwareMap)
//                .pathConstraints(
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(localiserConstants)
                .build();
    }

    private void initHardware() {
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

        storageServo1 = hardwareMap.get(Servo.class, "storageServo1");
        storageServo2 = hardwareMap.get(Servo.class, "storageServo2");
        storageServo3 = hardwareMap.get(Servo.class, "storageServo3");

        storageServo1.setPosition(servoStartingPosition);
        storageServo2.setPosition(servoStartingPosition);
        storageServo3.setPosition(servoStartingPosition);

        turretMotor = hardwareMap.get(DcMotor.class, "turretMotor");
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turretMotor.setDirection(DcMotor.Direction.FORWARD);
    }


    public void setPathState(PathState newState) {
        pathState = newState;
        pathTimer.resetTimer();
    }

    public void setLauncherState(LauncherState newState) {
        launcherState = newState;
        launcherTimer.resetTimer();
    }

    public void setPlaceholderServosToKick() {
//        servosToKick = new Queue(ServoId.s1, ServoId.s2, ServoId.s3);
        servosToKick.add(ServoId.s1);
        servosToKick.add(ServoId.s2);
        servosToKick.add(ServoId.s3);
    }

    @Override
    public void init() {
        pathState = PathState.DRIVE_START_POS_TO_SHOOT_POS;
        pathTimer = new Timer();
        opModeTimer = new Timer();
        launcherState = LauncherState.IDLE;
        launcherTimer = new Timer();
        setPlaceholderServosToKick();

        createFollower(hardwareMap);
        initHardware();

        buildPaths();
        follower.setPose(startPose);
    }

    @Override
    public void start() {
        opModeTimer.resetTimer();
        setPathState(pathState);
    }

    @Override
    public void loop() {
        follower.update();
        pathingLogic();
        launcherLogic();
        turretLogic();

        telemetry.addData("Path State", pathState.toString());
        telemetry.addData("X", follower.getPose().getX());
        telemetry.addData("Y", follower.getPose().getY());
        telemetry.addData("Heading", follower.getPose().getHeading());
        telemetry.addData("Path Time", pathTimer.getElapsedTimeSeconds());
        telemetry.update();
    }

    public void turretLogic() {
//            AprilTagDetection curID = new AprilTagDetection();
        double deltaTime = turretTimer.getElapsedTime();

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

        telemetry.addData("Turret Motor Power", turretMotor.getPower());

        // Safety goes here.

        turretMotor.setPower(power);
        lastError = error;

        turretTimer.resetTimer();
    }
}
