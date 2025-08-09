package org.firstinspires.ftc.teamcode;

import android.annotation.SuppressLint;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.threadopmode.ThreadOpMode;

@Config
@TeleOp
public class RotateTest extends ThreadOpMode {
    public static final double P = 10, I = 3, D = 0, F = 8;

    private Robot robot;

    @Override
    public void mainInit() {
        DcMotorEx frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        DcMotorEx frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        DcMotorEx backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        DcMotorEx backRight = hardwareMap.get(DcMotorEx.class, "backRight");
        SparkFunOTOS sparkFun = hardwareMap.get(SparkFunOTOS.class, "sensor_otos");

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        frontLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        frontRight.setVelocityPIDFCoefficients(P, I, D, F);
        backRight.setVelocityPIDFCoefficients(P, I, D, F);
        frontLeft.setVelocityPIDFCoefficients(P, I, D, F);
        backLeft.setVelocityPIDFCoefficients(P, I, D, F);

        robot = Robot
                .getInstance(frontLeft, frontRight, backLeft, backRight)
                .setImu(sparkFun);

        configureOtos();

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    public void mainLoop() {
        robot.rotateTo(1000, 400, 90);
        telemetry.addData("H", robot.getImu().getPosition().h);
        telemetry.update();
    }

    @SuppressLint("DefaultLocale")
    private void configureOtos() {
        // SparkFun.setLinearUnit(DistanceUnit.METER);
        robot.getImu().setLinearUnit(DistanceUnit.INCH);
        // SparkFun.setAngularUnit(AngleUnit.RADIANS);
        robot.getImu().setAngularUnit(AngleUnit.DEGREES);

        robot.getImu().setLinearScalar(1.0);
        robot.getImu().setAngularScalar(0.9933);

        robot.getImu().calibrateImu();

        robot.getImu().resetTracking();

        SparkFunOTOS.Pose2D currentPosition = new SparkFunOTOS.Pose2D(0, 0, 0);
        robot.getImu().setPosition(currentPosition);
    }
}
