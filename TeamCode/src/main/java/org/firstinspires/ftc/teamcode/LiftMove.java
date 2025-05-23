package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.threadopmode.ThreadOpMode;


@TeleOp
public class LiftMove extends ThreadOpMode {
    DcMotorEx lift;

    @Override
    public void mainInit() {
        lift = hardwareMap.get(DcMotorEx.class, "lift");
        resetEncoders();
    }

    @Override
    public void mainLoop() {
        if (gamepad1.cross && Math.abs(lift.getCurrentPosition()) < 2900) {
            lift.setPower(-0.5);
        } else if (gamepad1.circle && lift.getCurrentPosition() < -10) {
            lift.setPower(0.5);
        } else {
            lift.setPower(0);
        }

        telemetry.addData("ticks", lift.getCurrentPosition());
    }

    private void resetEncoders() {
        lift.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }
}
