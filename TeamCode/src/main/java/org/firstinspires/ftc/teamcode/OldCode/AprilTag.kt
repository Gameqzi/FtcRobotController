package org.firstinspires.ftc.teamcode.OldCode

import android.util.Size
import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor

@Disabled
@TeleOp
class AprilTag : OpMode() {
    private var panels: TelemetryManager? = null
    private lateinit var aprilTagProcessor: AprilTagProcessor
    private lateinit var visionPortal: VisionPortal
    var camH = 1920
    var camW = 1080

    override fun init() {
        panels = PanelsTelemetry.telemetry
        // Initialize AprilTag processor
        aprilTagProcessor = AprilTagProcessor.Builder()
            .setDrawAxes(true)  // Draw 3D axes on detected tags
            .setDrawCubeProjection(true)  // Draw cube projection
            .setDrawTagOutline(true)  // Draw outline around tags
            .build()

        // Initialize Vision Portal
        visionPortal = VisionPortal.Builder()
            .setCamera(hardwareMap.get(WebcamName::class.java, "Webcam 1"))
            .addProcessor(aprilTagProcessor)
            .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
            .setCameraResolution(Size(1280, 720))
            .build()

        panels?.addData("Status", "Initialized")
        panels?.addData("Info", "Camera starting...")
        panels?.update()
    }

    override fun init_loop() {
        // Optional: Display camera status during init
        panels?.addData("Camera State", visionPortal.cameraState)
        panels?.update()
    }

    override fun start() {
        panels?.addData("Status", "Running")
        panels?.update()
    }

    override fun loop() {
        // Get all detected AprilTags
        val detections = aprilTagProcessor.detections

        panels?.addData("# AprilTags Detected", detections.size)
        panels?.addData("Camera FPS", visionPortal.fps)

        // Loop through each detection
        for (detection in detections) {
            displayDetectionInfo(detection)
        }

        panels?.update()
    }

    override fun stop() {
        // Clean up when OpMode stops
        visionPortal.close()
    }

    private fun displayDetectionInfo(detection: AprilTagDetection) {
        panels?.addLine("\n--- AprilTag ID ${detection.id} ---")

        // Basic detection info
        panels?.addData("Center X", detection.center.x)
        panels?.addData("Center y", detection.center.y)
        panels?.addData("Decision Margin", detection.decisionMargin)
        panels?.addData("Hamming Distance", detection.hamming)

        // Pose information (position and orientation)
        detection.ftcPose?.let { pose ->
            panels?.addLine("Pose Information:")
            panels?.addData("  X (inches)", pose.x)
            panels?.addData("  Y (inches)", pose.y)
            panels?.addData("  Z (inches)", pose.z)
            panels?.addData("  Roll (deg)", pose.roll)
            panels?.addData("  Pitch (deg)", pose.pitch)
            panels?.addData("  Yaw (deg)", pose.yaw)
            panels?.addData("  Range (inches)", pose.range)
            panels?.addData("  Bearing (deg)", pose.bearing)
            panels?.addData("  Elevation (deg)", pose.elevation)
        }
    }
}