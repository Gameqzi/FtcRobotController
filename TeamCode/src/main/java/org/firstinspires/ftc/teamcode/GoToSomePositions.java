package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.Utils.sleep;

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
import org.firstinspires.ftc.teamcode.threadopmode.TaskThread;
import org.firstinspires.ftc.teamcode.threadopmode.ThreadOpMode;

@Config
@TeleOp
public class GoToSomePositions extends ThreadOpMode {

    private Robot robot;

    @Override
    public void mainInit() {
        DcMotorEx frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        DcMotorEx frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        DcMotorEx backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        DcMotorEx backRight = hardwareMap.get(DcMotorEx.class, "backRight");
        SparkFunOTOS sparkFun = hardwareMap.get(SparkFunOTOS.class, "sensor_otos");

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        robot = Robot
                .getInstance(frontLeft, frontRight, backLeft, backRight)
                .setImu(sparkFun);

        Robot.getInstance().setVelocityPIDF(10, 3, 0, 8);

        configureOtos();

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        registerThread(new TaskThread(() -> SomeData()));
    }

    @Override
    public void mainLoop() {
        robot.goTo(0.2, "40", "90", "0");
        sleep(50000);
        robot.goTo(0.2, "80", "110", "0");
        sleep(50000);
        robot.goTo(0.2, "90", "40", "0");
        sleep(50000);
        requestOpModeStop();
        sleep(5000);
        robot.goTo(0.2, "72", "30", "0");
        sleep(1000);
        robot.goTo(0.2, "100", "100", "0");
        sleep(1000);
        robot.goTo(0.2, "72", "0", "0");
        sleep(1000);
        robot.goTo(0.2, "84", "0", "0");
        sleep(1000);
        requestOpModeStop();
        sleep(5000);
    }


    private void SomeData() {
        telemetry.addData("X", robot.getImu().getPosition().x);
        telemetry.addData("Y", robot.getImu().getPosition().y);
        telemetry.addData("H", robot.getImu().getPosition().h);
        telemetry.update();
    }

    @SuppressLint("DefaultLocale")
    private void configureOtos() {
        // SparkFun.setLinearUnit(DistanceUnit.METER);
        robot.getImu().setLinearUnit(DistanceUnit.INCH);
        // SparkFun.setAngularUnit(AngleUnit.RADIANS);
        robot.getImu().setAngularUnit(AngleUnit.DEGREES);

        SparkFunOTOS.Pose2D offset = new SparkFunOTOS.Pose2D(0, 0, 0.4);
        robot.getImu().setOffset(offset);

        robot.getImu().setLinearScalar(0.956);
        robot.getImu().setAngularScalar(0.9933);

        robot.getImu().calibrateImu();

        robot.getImu().resetTracking();

        SparkFunOTOS.Pose2D currentPosition = new SparkFunOTOS.Pose2D(0, 0, 0);
        robot.getImu().setPosition(currentPosition);
    }
}
