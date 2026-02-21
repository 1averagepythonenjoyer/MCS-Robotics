package org.firstinspires.ftc.teamcode;

//import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@TeleOp(name = "Odometry Standalone")
public class OdometryStandalone extends OpMode {
    public static final double X_OFFSET = -84.0;
    public static final double Y_OFFSET = -168.0;
//        private final PinpointConstants localiserConstants = new PinpointConstants()
//            .forwardPodY(Y_OFFSET)
//            .strafePodX(X_OFFSET)
//            .distanceUnit(DistanceUnit.MM)
//            .hardwareMapName("pinpoint")
//            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
//            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
//            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
//            .yawScalar(-1);
    private GoBildaPinpointDriver odometry;
    private double lastTime = 0.0;

    @Override
    public void init() {
        odometry = hardwareMap.get(GoBildaPinpointDriver.class, "odo");
        odometry.setOffsets(X_OFFSET, Y_OFFSET, DistanceUnit.MM);
        odometry.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        odometry.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
        odometry.resetPosAndIMU();
        Pose2D startPos = new Pose2D(DistanceUnit.MM, 0.0, 0.0, AngleUnit.DEGREES, 0.0);
        odometry.setPosition(startPos);
        odometry.setYawScalar(-1.0);
    }

    @Override
    public void loop() {
        double time = getRuntime();
        double dt = time - lastTime;
        double updateRate = 1.0 / dt;
        lastTime = time;
        odometry.update();
        Pose2D pose = odometry.getPosition();
        telemetry.addLine("Units: mm, °, Hz");
        telemetry.addData("X", "%.1f", pose.getX(DistanceUnit.MM));
        telemetry.addData("Y", "%.1f", pose.getY(DistanceUnit.MM));
        double degrees = pose.getHeading(AngleUnit.DEGREES);
        telemetry.addData("Angle", "%.1f", degrees);
        telemetry.addData("Angle (Normalised)", "%.1f", AngleUnit.normalizeDegrees(degrees));
        telemetry.addData("Update Rate", "%.1f", updateRate);
        telemetry.update();
    }
}
