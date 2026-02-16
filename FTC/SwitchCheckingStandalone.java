package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DigitalChannel;

public class SwitchCheckingStandalone {
    private DigitalChannel hoodSwitch;

    public void switchInit() {
//        hoodSwitch = hardwareMap.get(DigitalChannel.class, "hoodSwitch");
        hoodSwitch.setMode(DigitalChannel.Mode.INPUT);
    }

    public boolean switchStatus() {
//        telemetry.addData("Switch State", !hoodSwitch.getState());
        return !hoodSwitch.getState();
    }
}
