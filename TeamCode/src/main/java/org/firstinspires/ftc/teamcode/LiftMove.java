package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.threadopmode.ThreadOpMode;


@Config
@TeleOp
public class LiftMove extends ThreadOpMode {
    DcMotorEx lift;

    public static double LP = 10, LI = 3, LD = 0, LF = 8;

    int LiftPos = 0;

    @Override
    public void mainInit() {
        lift = hardwareMap.get(DcMotorEx.class, "lift");
        resetEncoders();


        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        telemetry.clearAll();
    }

    @Override
    public void mainLoop() {
        lift.setVelocityPIDFCoefficients(LP, LI, LD, LF);
        if (gamepad1.cross && Math.abs(lift.getCurrentPosition()) < 2900) {
            if (Math.abs(LiftPos) > 2900) {
                LiftPos += 10;
            } else {
                LiftPos -= 10;
            }
            lift.setVelocity(-1500);
        } else if (gamepad1.circle && lift.getCurrentPosition() < -10) {
            if (LiftPos > -10) {
                LiftPos -= 20;
            } else {
                LiftPos += 20;
            }
            lift.setVelocity(1000);
        } else {
            lift.setVelocity(0);
        }
        lift.setTargetPosition(LiftPos);
        lift.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);

        telemetry.addData("ticks", lift.getCurrentPosition());
        telemetry.addData("LiftPos", LiftPos);
    }

    private void resetEncoders() {
        // Reset the encoders for each motor
        lift.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
    }
}
