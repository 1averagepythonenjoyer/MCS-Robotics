package org.firstinspires.ftc.teamcode;




import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;





@TeleOp(name = "meow")
public class hoodAngleChange extends LinearOpMode {
    private CRServo s1;

    private DigitalChannel hoodSwitch;

    private DcMotor flywheel;





    public boolean switchStatus() {
        telemetry.addData("Switch State", !hoodSwitch.getState());
        return !hoodSwitch.getState();
    }



    @Override
    public void runOpMode() {
        s1 = hardwareMap.get(CRServo.class, "s1AsServo");
        flywheel = hardwareMap.get(DcMotor.class, "flywheel");

        hoodSwitch = hardwareMap.get(DigitalChannel.class, "hoodSwitch");
        hoodSwitch.setMode(DigitalChannel.Mode.INPUT);

        double BEGINTIME = getRuntime();
        // fill in after calibration
        double TIME_FACTOR = 0.002;
        // input of function
        double ANGLE_TO_ROTATE = -1;


        boolean isButtonPressed = true;

//        double TURNSTARTTIME = getRuntime();

        s1.setDirection(CRServo.Direction.REVERSE);


        waitForStart();

        while (opModeIsActive()) {

            if (gamepad2.x) {
                ANGLE_TO_ROTATE = 10;


                while (getRuntime() < (TIME_FACTOR * ANGLE_TO_ROTATE) + BEGINTIME) {
                    s1.setPower(0.8);

                    telemetry.addData("angle:", ANGLE_TO_ROTATE);
                    telemetry.addData("time:", TIME_FACTOR * ANGLE_TO_ROTATE);
                    telemetry.update();
                }

                s1.setPower(0);  //stop servo

                double POWER_START_TIME = getRuntime();
                double TIME_TAKEN_TO_SHOOT = 5;

                while (getRuntime() < POWER_START_TIME + TIME_TAKEN_TO_SHOOT) {
                    flywheel.setPower(0.8);
                }
                flywheel.setPower(0);
            }

                //RESET SERVO POSITION
            if (!switchStatus()) {
                s1.setPower(-0.5);
            } else {
                s1.setPower(0);  //stop servo
            }
        }
    }
}
