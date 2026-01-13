package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.Rev9AxisImuOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

@Autonomous(name = "coordinates (Blocks to Java)")
public class coordinates extends LinearOpMode {

  private IMU dypycutieimu;

  /**
   * This sample contains the bare minimum Blocks for any regular OpMode. The 3 blue
   * Comment Blocks show where to place Initialization code (runs once, after touching the
   * DS INIT button, and before touching the DS Start arrow), Run code (runs once, after
   * touching Start), and Loop code (runs repeatedly while the OpMode is active, namely not
   * Stopped).
   */
  @Override
  public void runOpMode() {
    // Put initialization blocks here.
    // Create a Rev9AxisImuOrientationOnRobot object for use with a REV 9-Axis IMU,
    // specifying the IMU's orientation on the robot via the direction that the REV logo
    // on the IMU is facing and the direction that the I2C port on the IMU is facing.
    dypycutieimu = hardwareMap.get(IMU.class, "dypycutie imu");
    dypycutieimu.initialize(new IMU.Parameters(new Rev9AxisImuOrientationOnRobot(Rev9AxisImuOrientationOnRobot.LogoFacingDirection.UP, Rev9AxisImuOrientationOnRobot.I2cPortFacingDirection.LEFT)));
    waitForStart();
    if (opModeIsActive()) {
      // Put run blocks here.
      while (opModeIsActive()) {
        // Put loop blocks here.
        telemetry.addData("orientation", dypycutieimu.getRobotYawPitchRollAngles());
        telemetry.update();
      }
    }
  }
}
