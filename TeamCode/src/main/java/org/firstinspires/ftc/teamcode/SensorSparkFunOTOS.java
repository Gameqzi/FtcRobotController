package org.firstinspires.ftc.teamcode;

import android.annotation.SuppressLint;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@TeleOp(name = "Sensor: SparkFun OTOS + REV IMU", group = "Sensor")
public class SensorSparkFunOTOS extends LinearOpMode {
    // SparkFun OTOS sensor
    SparkFunOTOS myOtos;

    private BNO055IMU imu;
    private Orientation imuAngles;

    @Override
    public void runOpMode() {
        // Get a reference to the OTOS sensor
        myOtos = hardwareMap.get(SparkFunOTOS.class, "sensor_otos");

        // ✅ Get a reference to the REV IMU (must match name in RC config)
        imu = hardwareMap.get(BNO055IMU.class, "imu");

        // Configure OTOS and IMU
        configureOtos();
        configureRevImu();

        waitForStart();

        while (opModeIsActive()) {
            // OTOS position
            SparkFunOTOS.Pose2D pos = myOtos.getPosition();

            // REV IMU heading
            imuAngles = imu.getAngularOrientation();
            double imuHeading = imuAngles.firstAngle; // Z-axis = yaw

            // Gamepad controls
            if (gamepad1.triangle) {
                myOtos.resetTracking();
            }
            if (gamepad1.square) {
                myOtos.calibrateImu();
            }

            // Telemetry
            telemetry.addLine("Press triangle on Gamepad to reset tracking");
            telemetry.addLine("Press square on Gamepad to calibrate the IMU");
            telemetry.addLine();

            telemetry.addData("OTOS X coordinate", pos.x);
            telemetry.addData("OTOS Y coordinate", pos.y);
            telemetry.addData("OTOS Heading", pos.h);

            telemetry.addData("REV IMU Heading", imuHeading);

            telemetry.update();
        }
    }

    @SuppressLint("DefaultLocale")
    private void configureOtos() {
        telemetry.addLine("Configuring OTOS...");
        telemetry.update();

        myOtos.setLinearUnit(DistanceUnit.INCH);
        myOtos.setAngularUnit(AngleUnit.DEGREES);

        SparkFunOTOS.Pose2D offset = new SparkFunOTOS.Pose2D(0, 0, 0);
        myOtos.setOffset(offset);

        myOtos.setLinearScalar(1.0);
        myOtos.setAngularScalar(1.0);

        myOtos.calibrateImu();
        myOtos.resetTracking();

        SparkFunOTOS.Pose2D currentPosition = new SparkFunOTOS.Pose2D(0, 0, 0);
        myOtos.setPosition(currentPosition);

        SparkFunOTOS.Version hwVersion = new SparkFunOTOS.Version();
        SparkFunOTOS.Version fwVersion = new SparkFunOTOS.Version();
        myOtos.getVersionInfo(hwVersion, fwVersion);

        telemetry.addLine("OTOS configured! Press start to get position data!");
        telemetry.addLine();
        telemetry.addLine(String.format("OTOS Hardware Version: v%d.%d", hwVersion.major, hwVersion.minor));
        telemetry.addLine(String.format("OTOS Firmware Version: v%d.%d", fwVersion.major, fwVersion.minor));
        telemetry.update();
    }

    private void configureRevImu() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;

        imu.initialize(parameters);
        telemetry.addLine("REV IMU initialized!");
        telemetry.update();
    }
}
