package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "meow")
public class servoContinuous extends LinearOpMode {
  private Servo s1;
  @Override
  public void runOpMode() {
    s1 = hardwareMap.get(Servo.class, "s1AsServo");
    waitforstart();
    // place below at start of code
    double BEGINTIME = getruntime();
    // fill in after calibration
    double TIME_MULTI
    // input of function
    double ANGLE_TO_ROTATE
    s1.setPwmEnable();
    double TURNSTARTTIME = getruntime();
    s1.setDirection(Servo.Direction.REVERSE);
      while (getruntime() < TIME_MULTI*ANGLE_TO_ROTATE + BEGINTIME) {
        telemetry.addData("angle:", ANGLE_TO_ROTATE);
        telemetry.addData("time:", TIME_MULTI*ANGLE_TO_ROTATE);
        telemetry.update();
      }
    s1.stop();
    }
  }
}
