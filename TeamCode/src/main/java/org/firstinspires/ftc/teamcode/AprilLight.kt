package org.firstinspires.ftc.teamcode

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.hardware.limelightvision.Limelight3A
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp
class AprilLight : OpMode() {
    private var panels: TelemetryManager? = null
    private lateinit var limelight : Limelight3A

    override fun init() {
        panels = PanelsTelemetry.telemetry
        limelight = hardwareMap.get(Limelight3A::class.java, "limelight")
        limelight.pipelineSwitch(0)
        limelight.start()
    }

    override fun loop() {
        var status = limelight.getStatus()
        var result = limelight.getLatestResult()

        if (result != null) {
            // Access general information
            var botpose = result.botpose
            var captureLatency = result.captureLatency
            var targetingLatency = result.targetingLatency
            var parseLatency = result.parseLatency

            if(result.isValid) {
                panels?.addData("tx", result.tx)
                panels?.addData("txnc", result.txNC)
                panels?.addData("ty", result.ty)
                panels?.addData("tync", result.tyNC)
                panels?.addData("Botpose", botpose.toString())

                // Get AprilTag detections
                var fiducials = result.fiducialResults
                panels?.addData("Tags Detected", fiducials.size)

                // Iterate through each detected AprilTag
                for (fiducial in fiducials) {
                    // Get the AprilTag ID
                    var tagID = fiducial.fiducialId

                    // Get robot pose relative to the AprilTag (most useful)
                    var robotPoseTargetSpace = fiducial.robotPoseTargetSpace

                    // Get camera pose relative to the AprilTag
                    var cameraPoseTargetSpace = fiducial.cameraPoseTargetSpace

                    // Get robot pose in field coordinate system
                    var robotPoseFieldSpace = fiducial.robotPoseFieldSpace

                    // Get AprilTag pose in camera coordinate system
                    var targetPoseCameraSpace = fiducial.targetPoseCameraSpace

                    // Display data for each tag
                    panels?.addData("Tag ID", tagID)
                    panels?.addData("Robot Pose (Target Space)", robotPoseTargetSpace.toString())
                    panels?.addData("Camera Pose (Target Space)", cameraPoseTargetSpace.toString())
                    panels?.addData("Robot Pose (Field Space)", robotPoseFieldSpace.toString())
                }
            }
        }
    }
}