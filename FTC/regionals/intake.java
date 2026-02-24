package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class intake {

    private final DcMotor intakeMotor;
    private static final double INTAKE_POWER = 1.0;

    private enum IntakeState {IDLE, RUNNING, REVERSING}
    private IntakeState state = IntakeState.IDLE;

    private final ElapsedTime timer = new ElapsedTime();
    private double duration = 0;

    public Intake(HardwareMap hardwareMap) {
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void run(double seconds) {
        duration = seconds;
        intakeMotor.setPower(INTAKE_POWER);
        timer.reset();
        state = IntakeState.RUNNING;
    }

    public void reverse(double seconds) {
        duration = seconds;
        intakeMotor.setPower(-INTAKE_POWER);
        timer.reset();
        state = IntakeState.REVERSING;
    }

    public void stop() {
        intakeMotor.setPower(0);
        state = IntakeState.IDLE;
    }

    public void update() {
        if (state != IntakeState.IDLE && timer.seconds() >= duration) {
            stop();
        }
    }

    public boolean isBusy() {
        if (state != IntakeState.IDLE) {
            return true
        }
        return false
    }
    
    public IntakeState getState() {
        return state;
    }
}
