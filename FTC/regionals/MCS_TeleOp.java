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
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.List;

@TeleOp(name = "MCS TeleOp", group = "MCS")
public class MCS_TeleOp extends LinearOpMode {

    // ── Pedro constants — must match mecanumAutoOp exactly ───────────────────
    private final FollowerConstants followerConstants = new FollowerConstants()
            .mass(5);

    private final MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName("frontRight")
            .rightRearMotorName("backRight")
            .leftRearMotorName("backLeft")
            .leftFrontMotorName("frontLeft")
            .leftFrontMotorDirection(DcMotorEx.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorEx.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorEx.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorEx.Direction.FORWARD);

    private final PinpointConstants localiserConstants = new PinpointConstants()
            .forwardPodY(-6.614)
            .strafePodX(-3.307)
            .distanceUnit(DistanceUnit.INCH)
            .hardwareMapName("pinpoint")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .yawScalar(-1);

    // ── Poses ─────────────────────────────────────────────────────────────────
    private final Pose shootPose = new Pose(72, 72, Math.toRadians(135));
    private final Pose parkPose  = new Pose(0, 0, 0); // TODO: fill in park coordinates

    // ── Hood nudge power ──────────────────────────────────────────────────────
    private static final double HOOD_NUDGE_POWER = 0.3;

    // ── Flywheel pre-spin time before kicking ─────────────────────────────────
    private static final double FLYWHEEL_PRESPIN_SECONDS = 3.0;

    // ── Subsystems ────────────────────────────────────────────────────────────
    private Drivetrain      drivetrain;
    private Turret          turret;
    private ShooterHood     hood;
    private StorageKickers  kickers;
    private HuskyLensReader camera;

    // ── Intake ────────────────────────────────────────────────────────────────
    private DcMotor intakeMotor;
    private static final double INTAKE_POWER = 1.0;

    // ── Pedro follower + navigation ───────────────────────────────────────────
    private Follower  follower;
    private PathChain shootPath, parkPath;

    private enum NavState { MANUAL, NAVIGATING_TO_SHOOT, NAVIGATING_TO_PARK }
    private NavState navState = NavState.MANUAL;

    // ── Turret mode ───────────────────────────────────────────────────────────
    private enum TurretMode { MANUAL, AUTO_ALIGN }
    private TurretMode turretMode = TurretMode.MANUAL;

    // ── Kicker + flywheel prespin state machine ───────────────────────────────
    private enum KickState { IDLE, WAITING_FOR_PRESPIN, KICKING }
    private KickState   kickState  = KickState.IDLE;
    private int         kickTarget = 0;
    private ElapsedTime kickTimer  = new ElapsedTime();

    // ── AutoOp at start spaghetti constants ───────────────────────────────

    private AutoOpState autoOpState = AutoOpState.s1;

    private Timer autoOpMoveTimer = new Timer();

    private final float autoOpMoveTime = 0.5f;

    enum AutoOpState {
        s1,
        s2,
        s3,
        Move,
        Done,
    }

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void runOpMode() {
        drivetrain  = new Drivetrain(hardwareMap);
        turret      = new Turret(hardwareMap);
        hood        = new ShooterHood(hardwareMap);
        kickers     = new StorageKickers(hardwareMap);
        camera      = new HuskyLensReader(hardwareMap, "huskydyppy");

        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        follower = new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(localiserConstants)
                .build();
        follower.setStartingPose(new Pose(0, 0, 0)); // TODO: set correct starting pose

        telemetry.addData("Status", "Initialized — waiting for start");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            follower.update();

            navLogic();
            intakeLogic();
            turretLogic();
            hoodLogic();
            kickerLogic();
            telemetryUpdate();
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // DRIVER 1 — DRIVETRAIN + INTAKE + PEDRO
    //
    // Left stick Y      — forward / back
    // Right stick X     — strafe
    // Left stick X      — turn
    // Left bumper       — slow mode
    // Right trigger     — intake forward
    // Left trigger      — intake reverse (unjam)
    // Y                 — auto-drive to shootPose (turret auto-aligns on arrival)
    // B                 — auto-drive to parkPose
    // A                 — cancel Pedro, return to manual drive
    // ═════════════════════════════════════════════════════════════════════════

    private void navLogic() {
        if (gamepad1.y && navState == NavState.MANUAL) {
            Pose current = follower.getPose();
            shootPath = follower.pathBuilder()
                    .addPath(new BezierLine(current, shootPose))
                    .setLinearHeadingInterpolation(current.getHeading(), shootPose.getHeading())
                    .build();
            follower.followPath(shootPath, true);
            navState = NavState.NAVIGATING_TO_SHOOT;
        }

        if (gamepad1.b && navState == NavState.MANUAL) {
            Pose current = follower.getPose();
            parkPath = follower.pathBuilder()
                    .addPath(new BezierLine(current, parkPose))
                    .setLinearHeadingInterpolation(current.getHeading(), parkPose.getHeading())
                    .build();
            follower.followPath(parkPath, true);
            navState = NavState.NAVIGATING_TO_PARK;
        }

        if (gamepad1.a && navState != NavState.MANUAL) {
            follower.breakFollowing();
            navState = NavState.MANUAL;
        }

        if (navState == NavState.MANUAL && autoOpState != AutoOpState.Move) {
            drivetrain.drive(
                    -gamepad1.left_stick_y,
                    gamepad1.right_stick_x,
                    gamepad1.left_stick_x,
                    gamepad1.left_bumper);
        } else if (!follower.isBusy()) {
            if (navState == NavState.NAVIGATING_TO_SHOOT) {
                turretMode = TurretMode.AUTO_ALIGN;
                turret.resetAutoState();
            }
            navState = NavState.MANUAL;
        }

        if (autoOpState == AutoOpState.Move) {
            drivetrain.drive(1.0f, 0.0f, 0.0f, false);
            if (autoOpMoveTimer.getElapsedTimeSeconds() >= 0.5) {
                autoOpState = AutoOpState.Done;
//                autoOpMoveTimer = null; // claude will hate me for this but fuck you claude
            }
        }
    }

    private void intakeLogic() {
        if (gamepad1.right_trigger > 0.1) {
            intakeMotor.setPower(INTAKE_POWER * gamepad1.right_trigger);
        } else if (gamepad1.left_trigger > 0.1) {
            intakeMotor.setPower(-INTAKE_POWER * gamepad1.left_trigger);
        } else {
            intakeMotor.setPower(0);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // DRIVER 2 — TURRET + HOOD + KICKERS + FLYWHEEL ==WE WILL NEED TO DECREASE THE DELAY A LITTLE BIT
    //
    // Right stick X     — manual turret control
    // D-pad right       — nudge turret right slowly (hold)
    // D-pad left        — nudge turret left slowly (hold)
    // D-pad up          — nudge hood up slowly (hold)
    // D-pad down        — nudge hood down slowly (hold)
    // Right bumper      — cancel turret auto-align, return to manual
    // Y                 — flywheel prespin + kick servo 1
    // B                 — flywheel prespin + kick servo 2
    // X                 — flywheel prespin + kick servo 3
    // A                 — flywheel prespin + kick all three (1 → 2 → 3)
    // ════════════════════════════════════════════════════════════════════════

    private void turretLogic() {
        // Right bumper cancels auto-align at any time
        if (gamepad2.right_bumper) {
            turretMode = TurretMode.MANUAL;
        }

        if (turretMode == TurretMode.AUTO_ALIGN) {
            List<HuskyLensReader.TagData> tags = camera.read();
            boolean tagFound = false;
            for (HuskyLensReader.TagData tag : tags) {
                if (tag.type == HuskyLensReader.TagData.TagType.NAVIGATION) {
                    turret.updateAuto(tag.bearingDeg);
                    tagFound = true;
                    break;
                }
            }
            if (!tagFound) {
                turret.returnToCenter();
            }
            // Turret stays in AUTO_ALIGN until driver presses right bumper.
            // PD controller holds position naturally when aligned (power → 0).
        } else {
            // MANUAL — d-pad for slow nudge, right stick for fine control
            // Controls negated to match physical motor direction
            if (gamepad2.dpad_right) {
                turret.updateManual(-0.4);
            } else if (gamepad2.dpad_left) {
                turret.updateManual(0.4);
            } else {
                turret.updateManual(-gamepad2.right_stick_x);
            }
        }
    }

    private void hoodLogic() {
        if (gamepad2.dpad_up) {
            hood.nudge(HOOD_NUDGE_POWER);
        } else if (gamepad2.dpad_down) {
            hood.nudge(-HOOD_NUDGE_POWER);
        } else {
            hood.nudge(0);
        }
        hood.update();
    }

    private void kickerLogic() {
        switch (kickState) {

            case IDLE:
                if (!kickers.isBusy() && !hood.isBusy()) {
                    if (gamepad2.y || autoOpState == AutoOpState.s1) {
                        kickTarget = 1;
                        hood.startFlywheel();
                        kickTimer.reset();
                        kickState = KickState.WAITING_FOR_PRESPIN;
                    } else if (gamepad2.b || autoOpState == AutoOpState.s2) {
                        kickTarget = 2;
                        hood.startFlywheel();
                        kickTimer.reset();
                        kickState = KickState.WAITING_FOR_PRESPIN;
                    } else if (gamepad2.x || autoOpState == AutoOpState.s3) {
                        kickTarget = 3;
                        hood.startFlywheel();
                        kickTimer.reset();
                        kickState = KickState.WAITING_FOR_PRESPIN;
                    } else if (gamepad2.a) {
                        kickTarget = 0;
                        hood.startFlywheel();
                        kickTimer.reset();
                        kickState = KickState.WAITING_FOR_PRESPIN;
                    }
                }
                break;

            case WAITING_FOR_PRESPIN:
                if (kickTimer.seconds() >= FLYWHEEL_PRESPIN_SECONDS) {
                    if (kickTarget == 0) {
                        kickers.kickAll();
                    } else {
                        kickers.kickOne(kickTarget);
                    }
                    kickState = KickState.KICKING;
                }
                break;

            case KICKING:
                if (!kickers.isBusy()) {
                    hood.stopFlywheel();
                    kickState = KickState.IDLE;
                    switch (autoOpState) {
                        case s1:
                            autoOpState = AutoOpState.s2;
                            break;
                        case s2:
                            autoOpState = AutoOpState.s3;
                            break;
                        case s3:
                            autoOpState = AutoOpState.Move;
                            autoOpMoveTimer.resetTimer();
                            break;
                    }
                }
                break;
        }

        kickers.update();
    }

    // ── Telemetry ─────────────────────────────────────────────────────────────

    private void telemetryUpdate() {
        Pose pose = follower.getPose();
        telemetry.addLine("── Driver 1 ──────────────────────");
        telemetry.addData("Nav state",    navState);
        telemetry.addData("Slow mode",    gamepad1.left_bumper);
        telemetry.addData("X",            "%.1f", pose.getX());
        telemetry.addData("Y",            "%.1f", pose.getY());
        telemetry.addData("Heading°",     "%.1f", Math.toDegrees(pose.getHeading()));
        telemetry.addLine("── Driver 2 ──────────────────────");
        telemetry.addData("Turret mode",  turretMode);
        telemetry.addData("Turret angle", "%.1f°", turret.getAngleDeg());
        telemetry.addData("Turret limit", turret.isAtLimit());
        telemetry.addData("Turret aligned", turret.isAligned());
        telemetry.addData("Hood state",   hood.getState());
        telemetry.addData("Kick state",   kickState);
        telemetry.addData("Kicker busy",  kickers.isBusy());
        telemetry.update();
    }
}
