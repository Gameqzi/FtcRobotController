package org.firstinspires.ftc.teamcode

//noinspection SuspiciousImport
import android.util.Size
import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import java.io.File

@Disabled
@Autonomous
class SaveTower : OpMode() {
    private var panels: TelemetryManager? = null
    private var visionPortal: VisionPortal? = null
    private var tagProcessor: AprilTagProcessor? = null

    private var anyTagSeen: Boolean = false
    private var currentTagIds: List<Int> = emptyList()

    private val fileName = "array.txt"
    var order = arrayOf("", "", "") //Place holder at start of match will change when tag is detected

    override fun init() {
        tagProcessor = AprilTagProcessor.easyCreateWithDefaults()
        visionPortal = VisionPortal.Builder()
            .setCamera(hardwareMap.get(WebcamName::class.java, "Webcam 1"))
            .addProcessor(tagProcessor)
            .setCameraResolution(Size(1280, 720))
            .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
            .build()

        panels = PanelsTelemetry.telemetry
    }

    override fun loop() {
        val detections = tagProcessor?.detections.orEmpty()
        currentTagIds = detections.map { it.id }.toSet().toList().sorted()
        anyTagSeen = currentTagIds.isNotEmpty()

        for (id in currentTagIds) {
            when (id) {
                21 -> {
                    panels?.addLine("21")
                    order[0] = "G"
                    order[1] = "P"
                    order[2] = "P"
                    saveNumber(21)
                    break
                }
                22 -> {
                    panels?.addLine("22")
                    order[0] = "P"
                    order[1] = "G"
                    order[2] = "P"
                    saveNumber(22)
                    break
                }
                23 -> {
                    panels?.addLine("23")
                    order[0] = "P"
                    order[1] = "P"
                    order[2] = "G"
                    saveNumber(23)
                    break
                }
            }
        }

        panels?.addData("Order", order.joinToString())
        panels?.update()
    }

    private fun saveNumber(num: Int) {
        val file = File(hardwareMap.appContext.filesDir, fileName)
        file.writeText(num as String)
    }
}
