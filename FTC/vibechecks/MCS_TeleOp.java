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
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "MCS TeleOp", group = "MCS")
public class MCS_TeleOp extends LinearOpMode {

    // ── Pedro constants — must match MCS_AutoOp exactly ──────────────────────
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
    private final Pose shootPose = new Pose(68.57275902211875, 74.71944121071013, Math.toRadians(135));
    private final Pose parkPose  = new Pose(0, 0, 0); // TODO: fill in park coordinates

    // ── Hood angle constants — fill in after calibration ─────────────────────
    private static final double HOOD_ANGLE_SHOOT_POSE = 0.0;  // TODO: calibrate
    private static final double DPAD_UP_ANGLE         = 11.0; // max
    private static final double DPAD_RIGHT_ANGLE      =  8.0;
    private static final double DPAD_DOWN_ANGLE       =  5.0;
    private static final double DPAD_LEFT_ANGLE       =  3.0;

    // ── Subsystems ────────────────────────────────────────────────────────────
    private Drivetrain              drivetrain;
    private Turret                  turret;
    private ShooterHood             hood;
    private StorageKickers          kickers;
    private BallColorDetectorModule colorSensor;

    // ── Intake ────────────────────────────────────────────────────────────────
    private DcMotor intakeMotor;
    private static final double INTAKE_POWER = 1.0;

    // ── Pedro follower + navigation ───────────────────────────────────────────
    private Follower  follower;
    private PathChain shootPath, parkPath;

    private enum NavState { MANUAL, NAVIGATING_TO_SHOOT, NAVIGATING_TO_PARK }
    private NavState navState = NavState.MANUAL;

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void runOpMode() {
        // Init subsystems
        drivetrain  = new Drivetrain(hardwareMap);
        turret      = new Turret(hardwareMap);
        hood        = new ShooterHood(hardwareMap);
        kickers     = new StorageKickers(hardwareMap);
        colorSensor = new BallColorDetectorModule(hardwareMap);

        // Intake
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Pedro follower — keeps Pinpoint tracking alive during TeleOp
        follower = new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(localiserConstants)
                .build();
        follower.setPose(new Pose(0, 0, 0));

        // Home the hood before the match starts
        hood.homeBlocking();

        telemetry.addData("Status", "Initialized — waiting for start");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            follower.update(); // keeps Pinpoint alive every loop

            navLogic();
            intakeLogic();
            turret.updateManual(gamepad2.right_stick_x);
            hoodLogic();
            kickerLogic();
            colorSensor.update();
            telemetryUpdate();
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // DRIVER 1 — DRIVETRAIN + INTAKE + PEDRO NAVIGATION
    //
    // Left stick Y      — forward / back
    // Right stick X     — strafe
    // Left stick X      — turn
    // Left bumper       — slow mode
    // Right trigger     — intake forward
    // Left trigger      — intake reverse (unjam)
    // Y                 — auto-drive to shootPose + arm hood immediately
    // B                 — auto-drive to parkPose
    // A                 — cancel Pedro, return to manual drive
    // ═════════════════════════════════════════════════════════════════════════

    private void navLogic() {
        // Y — drive to shoot position and arm the hood at the same time
        if (gamepad1.y && navState == NavState.MANUAL) {
            Pose current = follower.getPose();
            shootPath = follower.pathBuilder()
                    .addPath(new BezierLine(current, shootPose))
                    .setLinearHeadingInterpolation(current.getHeading(), shootPose.getHeading())
                    .build();
            follower.followPath(shootPath, true);
            navState = NavState.NAVIGATING_TO_SHOOT;
            hood.triggerLaunch(HOOD_ANGLE_SHOOT_POSE); // arm hood immediately on button press
        }

        // B — drive to park position
        if (gamepad1.b && navState == NavState.MANUAL) {
            Pose current = follower.getPose();
            parkPath = follower.pathBuilder()
                    .addPath(new BezierLine(current, parkPose))
                    .setLinearHeadingInterpolation(current.getHeading(), parkPose.getHeading())
                    .build();
            follower.followPath(parkPath, true);
            navState = NavState.NAVIGATING_TO_PARK;
        }

        // A — cancel Pedro and take back manual control
        if (gamepad1.a && navState != NavState.MANUAL) {
            follower.breakFollowing();
            navState = NavState.MANUAL;
        }

        if (navState == NavState.MANUAL) {
            drivetrain.drive(
                    -gamepad1.left_stick_y,
                     gamepad1.right_stick_x,
                     gamepad1.left_stick_x,
                     gamepad1.left_bumper);
        } else if (!follower.isBusy()) {
            // Pedro finished — hand control back automatically
            navState = NavState.MANUAL;
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
    // DRIVER 2 — TURRET + HOOD + KICKERS
    //
    // Right stick X     — manual turret left / right
    // Right trigger     — fire at calibrated shoot pose angle
    // D-pad up          — manual override: fire at 11° (max)
    // D-pad right       — manual override: fire at 8°
    // D-pad down        — manual override: fire at 5°
    // D-pad left        — manual override: fire at 3°
    // A                 — kick storage servo 1
    // B                 — kick storage servo 2
    // X                 — kick storage servo 3
    // Y                 — kick all 3 in sequence (1 → 2 → 3)
    // ═════════════════════════════════════════════════════════════════════════

    private void hoodLogic() {
        if (!hood.isBusy()) {
            if      (gamepad2.right_trigger > 0.5) { hood.triggerLaunch(HOOD_ANGLE_SHOOT_POSE); }
            else if (gamepad2.dpad_up)             { hood.triggerLaunch(DPAD_UP_ANGLE);         }
            else if (gamepad2.dpad_right)          { hood.triggerLaunch(DPAD_RIGHT_ANGLE);      }
            else if (gamepad2.dpad_down)           { hood.triggerLaunch(DPAD_DOWN_ANGLE);       }
            else if (gamepad2.dpad_left)           { hood.triggerLaunch(DPAD_LEFT_ANGLE);       }
        }
        hood.update();
    }

    private void kickerLogic() {
        if (!kickers.isBusy()) {
            if      (gamepad2.a) { kickers.kickOne(1); }
            else if (gamepad2.b) { kickers.kickOne(2); }
            else if (gamepad2.x) { kickers.kickOne(3); }
            else if (gamepad2.y) { kickers.kickAll();  }
        }
        kickers.update();
    }

    // ── Telemetry ─────────────────────────────────────────────────────────────

    private void telemetryUpdate() {
        Pose pose = follower.getPose();
        telemetry.addLine("── Driver 1 ──────────────────────");
        telemetry.addData("Nav state",   navState);
        telemetry.addData("Slow mode",   gamepad1.left_bumper);
        telemetry.addData("X",           "%.1f", pose.getX());
        telemetry.addData("Y",           "%.1f", pose.getY());
        telemetry.addData("Heading°",    "%.1f", Math.toDegrees(pose.getHeading()));
        telemetry.addLine("── Driver 2 ──────────────────────");
        telemetry.addData("Hood state",  hood.getState());
        telemetry.addData("Kicker busy", kickers.isBusy());
        telemetry.addLine("── Storage ───────────────────────");
        telemetry.addData("Slot 1",      colorSensor.getSlot1Color());
        telemetry.addData("Slot 2",      colorSensor.getSlot2Color());
        telemetry.addData("Slot 3",      colorSensor.getSlot3Color());
        telemetry.update();
    }
}
