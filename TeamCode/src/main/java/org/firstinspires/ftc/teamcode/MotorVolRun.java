package org.firstinspires.ftc.teamcode;




import com.arcrobotics.ftclib.controller.PIDFController;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;


@Autonomous
public class MotorVolRun extends OpMode {

    private DcMotorEx frontLeft, frontRight, backLeft, backRight;

    public static double P, I, D, F; //P = 10, I = 3, D = 0, F = 8

    private PIDFController controller;

    @Override public void init() {
        controller = new PIDFController(P, I, D, F);

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

        ResetEncoders();


        telemetry.clearAll();
    }

    @Override public void loop() {
        controller.setPIDF(P, I, D, F);
        int FrontLeftPos = frontLeft.getCurrentPosition();
        int FrontRightPos = frontRight.getCurrentPosition();
        int BackLeftPos = backLeft.getCurrentPosition();
        int BackRightPos = backRight.getCurrentPosition();
        double PIDF = controller.calculate(FrontLeftPos, BackRightPos);
        double PosError = controller.getPositionError();

        double power = PIDF - PosError;

        /*frontRight.setVelocityPIDFCoefficients(P, I, D, F);
        backRight.setVelocityPIDFCoefficients(P, I, D, F);
        frontLeft.setVelocityPIDFCoefficients(P, I, D, F);
        backLeft.setVelocityPIDFCoefficients(P, I, D, F);*/

        frontLeft.setVelocity(power);
        frontRight.setVelocity(power);
        backLeft.setVelocity(power);
        backRight.setVelocity(power);

        telemetry.addData("PIDF", PIDF);
        telemetry.addData("Front Left Position", FrontLeftPos);
        telemetry.addData("Front Right Position", FrontRightPos);
        telemetry.addData("Back Left Position", BackLeftPos);
        telemetry.addData("Back Right Position", BackRightPos);


        RobotLog.dd("Drive", "FrontLeft=%d  FrontRight=%d BackLeft=%d BackRight=%d", FrontLeftPos, FrontRightPos, BackLeftPos, BackRightPos);

        /*if (Math.abs(time) >= 10) {
            telemetry.addData("Front Left Position", FrontLeftPos);
            telemetry.addData("Front Right Position", FrontRightPos);
            telemetry.addData("Back Left Position", BackLeftPos);
            telemetry.addData("Back Right Position", BackRightPos);
            telemetry.setMsTransmissionInterval(100000);
            requestOpModeStop();
        }*/
    }

    @Override public void stop() {
        frontLeft.setVelocity(0);
        frontRight.setVelocity(0);
        backLeft.setVelocity(0);
        backRight.setVelocity(0);
    }

    private void ResetEncoders() {
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
}
