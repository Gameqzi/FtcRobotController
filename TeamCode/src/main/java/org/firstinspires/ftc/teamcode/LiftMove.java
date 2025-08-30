package org.firstinspires.ftc.teamcode;




import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.threadopmode.ThreadOpMode;



@TeleOp
public class LiftMove extends ThreadOpMode {
    DcMotorEx lift;

    public static double LP = 10, LI = 3, LD = 0, LF = 8;

    int LiftPos = 0;

    @Override
    public void mainInit() {
        lift = hardwareMap.get(DcMotorEx.class, "lift");
        resetEncoders();


        telemetry.clearAll();
    }

    @Override
    public void mainLoop() {
        lift.setVelocityPIDFCoefficients(LP, LI, LD, LF);
        if (gamepad1.cross && Math.abs(lift.getCurrentPosition()) < 2900) {
            if (Math.abs(LiftPos) > 2900) {
                LiftPos += 15;
            } else {
                LiftPos -= 15;
            }
            lift.setVelocity(-1750);
        } else if (gamepad1.circle && lift.getCurrentPosition() < -10) {
            if (LiftPos > -10) {
                LiftPos -= 25;
            } else {
                LiftPos += 25;
            }
            lift.setVelocity(1000);
        } else {
            lift.setVelocity(0);
        }
        lift.setTargetPosition(LiftPos);
        lift.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);

        telemetry.addData("ticks", lift.getCurrentPosition());
        telemetry.addData("LiftPos", LiftPos);
        telemetry.update();
    }

    private void resetEncoders() {
        // Reset the encoders for each motor
        lift.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
    }
}
