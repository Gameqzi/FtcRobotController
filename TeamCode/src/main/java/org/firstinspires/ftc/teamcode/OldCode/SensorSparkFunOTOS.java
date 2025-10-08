package org.firstinspires.ftc.teamcode.OldCode;

import android.annotation.SuppressLint;

import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Disabled
@TeleOp(name = "Sensor: SparkFun OTOS", group = "Sensor")
public class SensorSparkFunOTOS extends LinearOpMode {
    // SparkFun OTOS sensor
    SparkFunOTOS myOtos;

    @Override
    public void runOpMode() {
        // Get a reference to the OTOS sensor
        myOtos = hardwareMap.get(SparkFunOTOS.class, "sensor_otos");

        // Configure OTOS and IMU
        configureOtos();

        waitForStart();

        while (opModeIsActive()) {
            // OTOS position
            SparkFunOTOS.Pose2D pos = myOtos.getPosition();


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

        myOtos.setLinearScalar(0.989);
        myOtos.setAngularScalar(0.9933);

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
}
