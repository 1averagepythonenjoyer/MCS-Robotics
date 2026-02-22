package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * TEST: Full Shoot Sequence (Flywheel prespin → Kicker fire)
 * Mirrors the kickerLogic() state machine from MCS_TeleOp_Vibed.
 *
 * Gamepad2 controls:
 *   Y   — prespin + kick servo 1
 *   B   — prespin + kick servo 2
 *   X   — prespin + kick servo 3
 *   A   — prespin + kick all
 *
 * What to verify:
 *   1. Flywheel starts immediately on button press
 *   2. 1.0s prespin delay before kicker fires
 *   3. Kicker sequence plays out fully while flywheel stays running
 *   4. Flywheel stops ONLY after kicker sequence finishes
 *   5. Button presses are ignored while a sequence is active
 *   6. Total timing for kickAll: 1s prespin + 12s kicks + flywheel stop = ~13s
 *   7. Hood state remains IDLE throughout (no launch sequence triggered)
 */
@TeleOp(name = "TEST Full Shoot Sequence", group = "MCS Test")
public class TestFullShootSequence extends LinearOpMode {

    private enum KickState { IDLE, WAITING_FOR_PRESPIN, KICKING }

    private static final double FLYWHEEL_PRESPIN_SECONDS = 1.0;

    @Override
    public void runOpMode() {
        ShooterHood hood = new ShooterHood(hardwareMap);
        StorageKickers kickers = new StorageKickers(hardwareMap);

        KickState kickState = KickState.IDLE;
        int kickTarget = 0;
        ElapsedTime kickTimer = new ElapsedTime();

        telemetry.addData("Status", "Full Shoot Sequence test ready. Press START.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            switch (kickState) {
                case IDLE:
                    if (!kickers.isBusy() && !hood.isBusy()) {
                        if (gamepad2.y) {
                            kickTarget = 1;
                            hood.startFlywheel();
                            kickTimer.reset();
                            kickState = KickState.WAITING_FOR_PRESPIN;
                        } else if (gamepad2.b) {
                            kickTarget = 2;
                            hood.startFlywheel();
                            kickTimer.reset();
                            kickState = KickState.WAITING_FOR_PRESPIN;
                        } else if (gamepad2.x) {
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
                    }
                    break;
            }

            kickers.update();
            hood.update();

            telemetry.addData("Kick state",    kickState);
            telemetry.addData("Kick target",   kickTarget == 0 ? "ALL" : "Servo " + kickTarget);
            telemetry.addData("Prespin time",  "%.2f", kickTimer.seconds());
            telemetry.addData("Kicker busy",   kickers.isBusy());
            telemetry.addData("Hood state",    hood.getState());
            telemetry.addData("Hood busy",     hood.isBusy());
            telemetry.update();
        }

        hood.stopFlywheel();
    }
}
