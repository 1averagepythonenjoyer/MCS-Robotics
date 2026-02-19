package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "bluetop2 (Blocks to Java)")
public class bluetop extends LinearOpMode {

  @Override
  public void runOpMode() {
    float[] coords = {
        -1260, 1575,  270 // top left corner, facing down
    };
    waitForStart();
    if (opModeIsActive()) {
      // move towards obelisk
      // scan pattern
      // rotate to face target
      // shoot
    }
  }
}
