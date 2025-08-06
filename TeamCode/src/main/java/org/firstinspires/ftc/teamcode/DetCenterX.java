package org.firstinspires.ftc.teamcode;

import android.annotation.SuppressLint;
import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.threadopmode.ThreadOpMode;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@Config
@TeleOp
public class DetCenterX extends ThreadOpMode {

    private VisionPortal visionPortal;
    private AprilTagProcessor tagProcessor;

    int CamW = 1280/2;
    int CamH = 720/2;

    private SparkFunOTOS SparkFun;

    @Override
    public void mainInit() {
        SparkFun   = hardwareMap.get(SparkFunOTOS.class, "sensor_otos");
        tagProcessor = AprilTagProcessor.easyCreateWithDefaults();
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(tagProcessor)
                // specify your streaming resolution here
                .setCameraResolution(new Size(1280, 720))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .build();


        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        configureOtos();
    }

    @Override
    public void mainLoop() {


        final double centerX = CamW;
        AprilTagDetection target = null;

        while (target == null) {
            for (AprilTagDetection det : tagProcessor.getDetections()) {
                if (det.id == 17) {
                    telemetry.addData("Tag Found", det.id);
                    telemetry.addData("Center X", det.center.x);
                    telemetry.addData("X", SparkFun.getPosition().x);
                    telemetry.addData("Y", SparkFun.getPosition().y);
                    telemetry.addData("H", SparkFun.getPosition().h);
                    telemetry.update();
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void configureOtos() {
        SparkFun.setLinearUnit(DistanceUnit.INCH);
        SparkFun.setAngularUnit(AngleUnit.DEGREES);
        SparkFun.setLinearScalar(1.0);
        SparkFun.setAngularScalar(1.0);
        SparkFun.calibrateImu();
        SparkFun.resetTracking();
        SparkFunOTOS.Pose2D currentPosition = new SparkFunOTOS.Pose2D(0, 0, 0);
        SparkFun.setPosition(currentPosition);
        SparkFunOTOS.Version hwVersion = new SparkFunOTOS.Version();
        SparkFunOTOS.Version fwVersion = new SparkFunOTOS.Version();
        SparkFun.getVersionInfo(hwVersion, fwVersion);
    }
}
