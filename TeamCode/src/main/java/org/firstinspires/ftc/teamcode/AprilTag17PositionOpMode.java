package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.threadopmode.ThreadOpMode;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp(name = "AprilTag‑Screen‑Position", group = "Vision")
public class AprilTag17PositionOpMode extends ThreadOpMode {

    private VisionPortal  visionPortal;
    private AprilTagProcessor tagProcessor;
    private static final int TARGET_TAG_ID = 17;      // 36h11‑17

    @Override
    public void mainInit() {

        // 1) Make an AprilTag processor (default tag‑size = 6 in; change in builder if needed)
        tagProcessor = AprilTagProcessor.easyCreateWithDefaults();

        // 2) Build a VisionPortal that uses the webcam named “Webcam 1”
        visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap.get(WebcamName.class, "Webcam 1"), tagProcessor);

        /*
         * The AprilTagProcessor automatically draws an overlay around every tag it
         * recognizes on the driver‑station video stream; you don’t need extra code to
         * see the outline.  What we add below is telemetry that tells you the tag’s
         * pixel coordinates in real time.
         */
    }

    @Override
    public void mainLoop() {

        List<AprilTagDetection> detections = tagProcessor.getDetections();

        boolean found = false;

        for (AprilTagDetection det : detections) {
            if (det.id == TARGET_TAG_ID) {           // only care about tag‑17
                found = true;

                // ---------- SCREEN‑SPACE COORDINATES ----------
                // (0,0) is the upper‑left pixel of the video frame
                double centerX = det.center.x;          // horizontal pixel
                double centerY = det.center.y;          // vertical pixel

                telemetry.addLine("AprilTag 17 FOUND on‑screen");
                telemetry.addData("Center (px)",  "[%f , %f]", centerX, centerY);
            }
        }

        if (!found) telemetry.addLine("AprilTag 17 NOT visible");

        telemetry.update();
    }
}
