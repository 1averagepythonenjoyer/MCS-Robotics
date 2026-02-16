package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;

@TeleOp(name = "SwitchChecking")
public class SwitchChecking extends LinearOpMode {
    private DigitalChannel hoodSwitch;

    @Override
    public void runOpMode() {
        // Port 1
        hoodSwitch = hardwareMap.get(DigitalChannel.class, "hoodSwitch");
        hoodSwitch.setMode(DigitalChannel.Mode.INPUT);

        waitForStart();
//        if (opModeIsActive()) return;

        telemetry.addLine("Loop start.");
        while (opModeIsActive()) {
            telemetry.addData("Switch State", hoodSwitch.getState());
        }
        telemetry.addLine("Loop end.");
    }
}
