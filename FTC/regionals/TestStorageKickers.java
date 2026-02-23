package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * TEST: StorageKickers (3 standard servos)
 *
 * Gamepad2 controls:
 *   Y   — kick servo 1 only
 *   B   — kick servo 2 only
 *   X   — kick servo 3 only
 *   A   — kick all three in sequence (1 → 2 → 3)
 *   D-pad up — kick in custom order: 3 → 1 → 2
 *
 * What to verify:
 *   1. Each servo kicks out and returns to starting position
 *   2. Starting positions are correct (servo doesn't fight at rest):
 *      - Servo 1: 0.21
 *      - Servo 2: 0.08
 *      - Servo 3: 0.30 (direction reversed)
 *   3. Kick amount (0.38) doesn't exceed servo range for any servo
 *      - Servo 1: 0.21 + 0.38 = 0.59 ✓
 *      - Servo 2: 0.08 + 0.38 = 0.46 ✓
 *      - Servo 3: 0.30 + 0.38 = 0.68 ✓ (reversed direction)
 *   4. Sequence timing: 2s up + 2s down = 4s per servo, 12s total for kickAll
 *   5. Button presses are ignored while busy (no queue corruption)
 *   6. kickInOrder() respects custom ordering
 *   7. isBusy() returns false only after full sequence completes
 */
@TeleOp(name = "TEST StorageKickers", group = "MCS Test")
public class TestStorageKickers extends LinearOpMode {

    @Override
    public void runOpMode() {
        StorageKickers kickers = new StorageKickers(hardwareMap);

        telemetry.addData("Status", "StorageKickers initialized. Press START.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            if (gamepad2.y) kickers.kickOne(1);
            if (gamepad2.b) kickers.kickOne(2);
            if (gamepad2.x) kickers.kickOne(3);
            if (gamepad2.a) kickers.kickAll();
            if (gamepad2.dpad_up) kickers.kickInOrder(new int[]{3, 1, 2});

            kickers.update();

            telemetry.addData("Busy?", kickers.isBusy());
            telemetry.update();
        }
    }
}