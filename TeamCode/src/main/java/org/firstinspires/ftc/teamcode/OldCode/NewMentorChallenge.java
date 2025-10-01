package org.firstinspires.ftc.teamcode.OldCode;

import static org.firstinspires.ftc.teamcode.OldCode.Utils.sleep;

import android.annotation.SuppressLint;
import android.util.Size;




import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.threadopmode.ThreadOpMode;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;


@TeleOp
public class NewMentorChallenge extends ThreadOpMode {

    DcMotorEx frontLeft, frontRight, backLeft, backRight;

    private static final double camWidthPX = 960;
    private static final double camHeightPX = 540;
    private VisionPortal visionPortal;
    private AprilTagProcessor tagProcessor;

    private Robot robot;

    int CamW = 1280/2;
    int CamH = 720/2;

    @Override
    public void mainInit() {
        tagProcessor = AprilTagProcessor.easyCreateWithDefaults();

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(tagProcessor)
                // specify your streaming resolution here
                .setCameraResolution(new Size(1280, 720))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .build();


        // Changed these to locals instead of class variables. -NP
        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");
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

        robot = Robot
                .getInstance(frontLeft, frontRight, backLeft, backRight)
                .setImu(sparkFun);

        configureOtos();


    }

    @Override
    public void mainLoop() {
        //robot.goTo(0.2, "12", "12", "0");

        SparkFunOTOS.Pose2D pos = robot.getImu().getPosition();

        CenterTag(17);
        sleep(50);
        requestOpModeStop();
        sleep(2000);
    }


    private void CenterTag(int tagID) {
        // compute the midpoint of the camera frame
        final double centerX = CamW;
        AprilTagDetection target = null;

        // 1) Spin until we see the tag at all
        while (target == null) {
            for (AprilTagDetection det : tagProcessor.getDetections()) {
                if (det.id == tagID) {
                    target = det;
                    telemetry.addData("Tag Found", det.id);
                    telemetry.addData("Center X", det.center.x);
                    telemetry.update();
                    break;
                }
            }
            if (target == null) {
                robot.rotateRight(0.1);
                sleep(50);
            }
        }
        // stop spinning as soon as we’ve locked onto the tag
        robot.stopMotors();



        // 2) Center on the tag
        if (target != null) {
            double targetError;
            targetError = target.center.x - centerX;

            while (Math.abs(targetError) > 10) {
                while (Math.abs(targetError) > 10) {
                    // re-fetch detections each pass
                    target = null;
                    for (AprilTagDetection det : tagProcessor.getDetections()) {
                        if (det.id == tagID) {
                            target = det;
                            break;
                        }
                    }
                    if (target == null) {
                        break;
                    }

                    // compute error relative to midpoint
                    targetError = target.center.x - centerX;
                    telemetry.addData("TargetError", targetError);
                    telemetry.update();

                    if (targetError > 10) {
                        robot.rotateRight(Math.max(Math.abs(targetError) / 1500, 0.05));
                    } else if (targetError < -10) {
                        robot.rotateLeft(Math.max(Math.abs(targetError) / 1500, 0.05));
                    }

                    sleep(25);
                }

                // finally, stop any motion
                robot.stopMotors();
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void configureOtos() {
        // SparkFun.setLinearUnit(DistanceUnit.METER);
        robot.getImu().setLinearUnit(DistanceUnit.INCH);
        // SparkFun.setAngularUnit(AngleUnit.RADIANS);
        robot.getImu().setAngularUnit(AngleUnit.DEGREES);
        robot.getImu().setLinearScalar(1.0);
        robot.getImu().setAngularScalar(1.0);
        robot.getImu().calibrateImu();
        robot.getImu().resetTracking();
        SparkFunOTOS.Pose2D currentPosition = new SparkFunOTOS.Pose2D(0, 0, 0);
        robot.getImu().setPosition(currentPosition);

        // Get the hardware and firmware version
        SparkFunOTOS.Version hwVersion = new SparkFunOTOS.Version();
        SparkFunOTOS.Version fwVersion = new SparkFunOTOS.Version();
        robot.getImu().getVersionInfo(hwVersion, fwVersion);
    }
}
