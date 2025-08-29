package org.firstinspires.ftc.teamcode;

import android.annotation.SuppressLint;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Config
@TeleOp(name = "Concept: AprilTag Localization", group = "Concept")
public class ConceptAprilTagLocalization extends OpMode {

    private static final boolean USE_WEBCAM = true;

    // Camera pose on robot
    private final Position           cameraPosition    = new Position(DistanceUnit.INCH, 0, 0, 13, 0);
    private final YawPitchRollAngles cameraOrientation = new YawPitchRollAngles(
            AngleUnit.DEGREES, 0, -90, 0, 0);

    private AprilTagProcessor aprilTag;
    private VisionPortal       visionPortal;

    @Override
    public void init() {
        telemetry.addData("Status", "Initializing camera & AprilTag...");
        telemetry.update();
        initAprilTag();

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    public void init_loop() {
        telemetry.clearAll();
        telemetry.addData("DS preview on/off", "3 dots → Camera Stream");
        telemetry.addData(">", "Press PLAY to start");
        telemetry.update();
    }

    @Override
    public void start() {
        // Optionally reset any runtime counters here
        // Ensure streaming is active
        visionPortal.resumeStreaming();
    }

    @Override
    public void loop() {
        // Show detections
        telemetryAprilTag();
        telemetry.update();

        // Allow driver to pause/resume camera stream to save CPU
        if (gamepad1.dpad_down) {
            visionPortal.stopStreaming();
        } else if (gamepad1.dpad_up) {
            visionPortal.resumeStreaming();
        }
    }

    @Override
    public void stop() {
        // Clean up camera
        visionPortal.close();
    }

    // -------------------------------------------------------------------------
    //  Utility methods factored out from runOpMode()
    // -------------------------------------------------------------------------

    private void initAprilTag() {
        aprilTag = new AprilTagProcessor.Builder()
                .setCameraPose(cameraPosition, cameraOrientation)
                .build();

        VisionPortal.Builder builder = new VisionPortal.Builder();
        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }
        builder.setCameraResolution(new android.util.Size(1280, 720)); // <-- Change res here
        builder.setStreamFormat(VisionPortal.StreamFormat.MJPEG);
        builder.addProcessor(aprilTag);

        visionPortal = builder.build();
    }


    @SuppressLint("DefaultLocale")
    private void telemetryAprilTag() {
        List<AprilTagDetection> detections = aprilTag.getDetections();
        telemetry.addData("# Tags", detections.size());

        for (AprilTagDetection det : detections) {
            if (det.id == 17) {
                telemetry.addData("Basket in view", null);
            }
        }

        for (AprilTagDetection det : detections) {
            if (det.metadata != null) {
                telemetry.addLine(String.format("ID %d : %s", det.id, det.metadata.name));
                telemetry.addLine(String.format(
                        "Pos (in)  X=%4.1f Y=%4.1f Z=%4.1f",
                        det.robotPose.getPosition().x,
                        det.robotPose.getPosition().y,
                        det.robotPose.getPosition().z));
                telemetry.addLine(String.format(
                        "Ori (deg) P=%4.1f R=%4.1f Y=%4.1f",
                        det.robotPose.getOrientation().getPitch(AngleUnit.DEGREES),
                        det.robotPose.getOrientation().getRoll(AngleUnit.DEGREES),
                        det.robotPose.getOrientation().getYaw(AngleUnit.DEGREES)));
            } else {
                telemetry.addLine(String.format("ID %d: Unknown", det.id));
                telemetry.addLine(String.format(
                        "Center (px)  X=%4.0f Y=%4.0f",
                        det.center.x, det.center.y));
            }
        }

        telemetry.addLine("XYZ = Right, Forward, Up (in)");
        telemetry.addLine("PRY = Pitch, Roll, Yaw (deg)");
    }
}
