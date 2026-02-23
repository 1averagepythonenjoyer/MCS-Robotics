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
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Autonomous(name = "MCS Auto", group = "MCS")
public class MCS_AutoOp extends OpMode {

    // ── Pedro constants — must match MCS_TeleOp exactly ──────────────────────
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

    // ── Poses ─────────────────────────────────────────────────────────────────
    private final Pose startPose = new Pose(21.46682188591386, 119.64610011641443, Math.toRadians(135));
    private final Pose shootPose = new Pose(68.57275902211875,  74.71944121071013, Math.toRadians(135));

    // ── Hood angle — fill in after calibration ────────────────────────────────
    private static final double HOOD_ANGLE_SHOOT_POSE = 0.0; // TODO: calibrate

    // ── Subsystems ────────────────────────────────────────────────────────────
    private ShooterHood    hood;
    private StorageKickers kickers;
    private Turret         turret;

    // ── Pedro ─────────────────────────────────────────────────────────────────
    private Follower  follower;
    private PathChain driveToShootPos;

    // ── Timers ────────────────────────────────────────────────────────────────
    private Timer pathTimer;
    private Timer opModeTimer;

    // ── Path state machine ────────────────────────────────────────────────────
    private enum PathState {
        DRIVE_TO_SHOOT_POS, // drive from start to shoot pose
        SHOOT,              // wait for hood to finish firing
        KICK_STORAGE,       // kick all 3 storage servos
        DONE
    }
    private PathState pathState;

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void init() {
        pathTimer   = new Timer();
        opModeTimer = new Timer();

        // Init subsystems
        hood    = new ShooterHood(hardwareMap);
        kickers = new StorageKickers(hardwareMap);
        turret  = new Turret(hardwareMap);

        // Pedro follower
        follower = new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(localiserConstants)
                .build();
        follower.setPose(startPose);

        // Build path
        driveToShootPos = follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
                .build();

        pathState = PathState.DRIVE_TO_SHOOT_POS;

        telemetry.addData("Status", "Initialized — waiting for start");
        telemetry.update();
    }

    @Override
    public void start() {
        opModeTimer.resetTimer();
        setPathState(PathState.DRIVE_TO_SHOOT_POS);
    }

    @Override
    public void loop() {
        follower.update();
        hood.update();
        kickers.update();

        pathingLogic();

        telemetry.addData("Path state",   pathState);
        telemetry.addData("Hood state",   hood.getState());
        telemetry.addData("Kicker busy",  kickers.isBusy());
        telemetry.addData("X",            "%.1f", follower.getPose().getX());
        telemetry.addData("Y",            "%.1f", follower.getPose().getY());
        telemetry.addData("Heading°",     "%.1f", Math.toDegrees(follower.getPose().getHeading()));
        telemetry.addData("Path time",    "%.2f", pathTimer.getElapsedTimeSeconds());
        telemetry.update();
    }

    // ── Path state machine ────────────────────────────────────────────────────

    private void pathingLogic() {
        switch (pathState) {

            case DRIVE_TO_SHOOT_POS:
                // Start driving and arm the hood at the same time
                follower.followPath(driveToShootPos, true);
                hood.triggerLaunch(HOOD_ANGLE_SHOOT_POSE);
                setPathState(PathState.SHOOT);
                break;

            case SHOOT:
                // Wait for Pedro to finish AND for the hood to finish firing + retracting
                if (!follower.isBusy() && !hood.isBusy()) {
                    setPathState(PathState.KICK_STORAGE);
                }
                break;

            case KICK_STORAGE:
                // Non-blocking — kickAll() queues all 3, kickers.update() in loop() drains it
                if (!kickers.isBusy()) {
                    kickers.kickAll();
                    setPathState(PathState.DONE);
                }
                break;

            case DONE:
                telemetry.addLine("Auto complete.");
                break;
        }
    }

    private void setPathState(PathState newState) {
        pathState = newState;
        pathTimer.resetTimer();
    }
}
