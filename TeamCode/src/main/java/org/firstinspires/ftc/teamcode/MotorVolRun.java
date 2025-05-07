package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
@Autonomous
public class MotorVolRun extends OpMode {

    private DcMotorEx frontLeft, frontRight, backLeft, backRight;

    public static double P, I, D, F; //P = 10, I = 3, D = 0, F = 8

    @Override public void init() {
        frontLeft  = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft   = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight  = hardwareMap.get(DcMotorEx.class, "backRight");


        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);

        ElapsedTime timer = new ElapsedTime();

        P = 10;
        I = 3;
        D = 0;
        F = 8;

        frontRight.setVelocityPIDFCoefficients(P, I, D, F);
        backRight.setVelocityPIDFCoefficients(P, I, D, F);
        frontLeft.setVelocityPIDFCoefficients(P, I, D, F);
        backLeft.setVelocityPIDFCoefficients(P, I, D, F);

        ResetEncoders();

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        telemetry.clearAll();
    }

    @Override public void loop() {
        frontLeft.setVelocity(1000);
        frontRight.setVelocity(1000);
        backLeft.setVelocity(1000);
        backRight.setVelocity(1000);

        if (Math.abs(time) >= 10) {
            telemetry.addData("Front Left Position", frontLeft.getCurrentPosition());
            telemetry.addData("Front Right Position", frontRight.getCurrentPosition());
            telemetry.addData("Back Left Position", backLeft.getCurrentPosition());
            telemetry.addData("Back Right Position", backRight.getCurrentPosition());
            telemetry.setMsTransmissionInterval(100000);
            requestOpModeStop();
        }
    }

    @Override public void stop() {
        frontLeft.setVelocity(0);
        frontRight.setVelocity(0);
        backLeft.setVelocity(0);
        backRight.setVelocity(0);
    }

    private void ResetEncoders() {
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}
