package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.threadopmode.ThreadOpMode;

@Config
@TeleOp
public class MainTeleOp extends ThreadOpMode {
    DcMotorEx frontLeft, frontRight, backLeft, backRight;
    DcMotorEx lift;

    public static double P = 10, I = 3, D = 0, F = 8;

    public static double LP = 10, LI = 3, LD = 0, LF = 8;

    @Override
    public void mainInit() {
        frontLeft  = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft   = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight  = hardwareMap.get(DcMotorEx.class, "backRight");
        lift       = hardwareMap.get(DcMotorEx.class, "lift");

        frontRight.setDirection(DcMotorEx.Direction.REVERSE);
        backRight.setDirection(DcMotorEx.Direction.FORWARD);
        frontLeft.setDirection(DcMotorEx.Direction.REVERSE);
        backLeft.setDirection(DcMotorEx.Direction.FORWARD);

        ResetEncoders();

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        telemetry.clearAll();
    }


    @Override
    public void mainLoop() {
        // Add PIDF coefficients to each motor
        frontRight.setVelocityPIDFCoefficients(P, I, D, F);
        backRight.setVelocityPIDFCoefficients(P, I, D, F);
        frontLeft.setVelocityPIDFCoefficients(P, I, D, F);
        backLeft.setVelocityPIDFCoefficients(P, I, D, F);
        lift.setVelocityPIDFCoefficients(LP, LI, LD, LF);

        double drive  = gamepad1.left_stick_y;  // Forward/backward (inverted as necessary)
        double strafe = -gamepad1.right_stick_x;     // Left/right
        double rotate = gamepad1.left_trigger - gamepad1.right_trigger;     // Rotation

        double frontLeftPower  = drive + strafe + rotate;
        double frontRightPower = drive - strafe - rotate;
        double backLeftPower   = drive - strafe + rotate;
        double backRightPower  = drive + strafe - rotate;

        // Set the calculated power to each motor
        frontLeft.setVelocity(frontLeftPower*1000);
        frontRight.setVelocity(-frontRightPower*1000);
        backLeft.setVelocity(backLeftPower*1000);
        backRight.setVelocity(-backRightPower*1000);

        if (gamepad1.cross && Math.abs(lift.getCurrentPosition()) < 2900) {
            lift.setVelocity(-500);
        } else if (gamepad1.circle && lift.getCurrentPosition() < -10) {
            lift.setVelocity(500);
        } else {
            lift.setVelocity(0);
        }
    }


    private void ResetEncoders() {
        frontLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}
