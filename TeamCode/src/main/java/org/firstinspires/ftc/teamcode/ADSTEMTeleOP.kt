package org.firstinspires.ftc.teamcode

import android.util.Size
import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.hardware.limelightvision.LLResult
import com.qualcomm.hardware.limelightvision.Limelight3A
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import java.lang.Thread.sleep
import kotlin.collections.isEmpty

@TeleOp
class ADSTEMTeleOP : OpMode() {
    private var panels: TelemetryManager? = null
    val lP1 = 0.059
    val lP2 = 0.13
    val lP3 = 0.204
    val fP1 = 0.167
    val fP2 = 0.02
    val fP3 = 0.0945
    var cLP = 1
    var servoSpeed = 0.0
    var held = 0
    val ord = arrayOf("N", "N", "N")
    val eord = arrayOf("", "", "")
    private lateinit var intakeServo1: CRServo
    private lateinit var intakeServo2: CRServo
    private lateinit var outTake1: DcMotorEx
    private lateinit var outTake2: DcMotorEx
    private lateinit var bowlServo: Servo
    private lateinit var outServo: Servo
    private var visionPortal: VisionPortal? = null
    private var tagProcessor: AprilTagProcessor? = null
    private var anyTagSeen: Boolean = false
    private var currentTagIds: List<Int> = emptyList()
    data class Target(
        val tx: Double,    // normalized X (-1..1)
        val ty: Double,    // normalized Y (-1..1)
        val ta: Double,    // area (pixels)
        val colorId: Int,  // 1=green, 2=purple
        val width: Double,
        val height: Double
    )
    private lateinit var limelight: Limelight3A
    private val stride = 6
    override fun init() {
        panels = PanelsTelemetry.telemetry
        intakeServo1 = hardwareMap.get(CRServo::class.java, "intakeServo1")
        intakeServo2 = hardwareMap.get(CRServo::class.java, "intakeServo2")
        outTake1 = hardwareMap.get(DcMotorEx::class.java, "outTake1")
        outTake2 = hardwareMap.get(DcMotorEx::class.java, "outTake2")
        bowlServo  = hardwareMap.get(Servo::class.java, "bowlServo")
        outServo  = hardwareMap.get(Servo::class.java, "outServo")
        intakeServo2.direction = DcMotorSimple.Direction.REVERSE
        outTake2.direction = DcMotorSimple.Direction.REVERSE
        limelight = hardwareMap.get(Limelight3A::class.java, "limelight")
        limelight.setPollRateHz(100)     // fast polling
        limelight.pipelineSwitch(0)      // <- change to your pipeline slot
        tagProcessor = AprilTagProcessor.easyCreateWithDefaults()
        visionPortal = VisionPortal.Builder()
            .setCamera(hardwareMap.get(WebcamName::class.java, "Webcam 1"))
            .addProcessor(tagProcessor)
            .setCameraResolution(Size(1280, 720))
            .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
            .build()
        panels?.addLine("Limelight init complete.")
    }

    override fun start() {
        bowlServo.position = lP1
        outServo.position = 0.0
    }

    override fun loop() {
        val detections = tagProcessor?.detections.orEmpty()
        currentTagIds = detections.map { it.id }.toSet().toList().sorted()
        anyTagSeen = currentTagIds.isNotEmpty()

        for (id in currentTagIds) {
            when (id) {
                21 -> {
                    panels?.addLine("21")
                    eord[0] = "G"
                    eord[1] = "P"
                    eord[2] = "P"
                    break
                }
                22 -> {
                    panels?.addLine("22")
                    eord[0] = "P"
                    eord[1] = "G"
                    eord[2] = "P"
                    break
                }
                23 -> {
                    panels?.addLine("23")
                    eord[0] = "P"
                    eord[1] = "P"
                    eord[2] = "G"
                    break
                }
            }
        }

        if (gamepad1.cross) {
            servoSpeed = 1.0
            intakeServo1.power = servoSpeed
            intakeServo2.power = servoSpeed
        } else if (gamepad1.circle) {
            servoSpeed = -1.0
            intakeServo1.power = servoSpeed
            intakeServo2.power = servoSpeed
        } else {
            servoSpeed = 0.0
            intakeServo1.power = servoSpeed
            intakeServo2.power = servoSpeed
        }
        if (gamepad1.triangle) {
            held = 1
        }

        val result: LLResult? = limelight.latestResult
        if (result == null) {
            panels?.addLine("Limelight: no result yet")
            panels?.update()
            return
        }

        panels?.addData("Data age (ms)", result.staleness)

        // Read Python output FIRST — do not require result.isValid
        val py = result.pythonOutput
        if (py == null || py.isEmpty()) {
            // Fall back to built-in validity for info only
            panels?.addLine(
                if (result.isValid) "Python output empty (but built-in valid)"
                else "Python output empty"
            )
            panels?.update()
            return
        }

        // Parse 6-float tuples
        val stride = 6
        val targets = ArrayList<ADSTEMTeleOP.Target>(py.size / stride)
        var i = 0
        while (i + stride - 1 < py.size) {
            targets.add(
                Target(
                    tx = py[i + 0],
                    ty = py[i + 1],
                    ta = py[i + 2],
                    colorId = py[i + 3].toInt(),
                    width = py[i + 4],
                    height = py[i + 5]
                )
            )
            i += stride
        }

        val greenCount = targets.count { it.colorId == 1 }
        val purpleCount = targets.count { it.colorId == 2 }
        val best = targets.maxByOrNull { it.ta }

        panels?.addData("Targets", targets.size)
        panels?.addData("Green", greenCount)
        panels?.addData("Purple", purpleCount)

        best?.let {
            when (purpleCount) {
                1 -> {
                    if (it.width >= 280 && it.height >= 170) {
                        // Fill the list array similar to color sensor logic
                        if (ord[0] == "N") {
                            // Check if already detected
                            if (ord[0] == "P" || ord[1] == "P" || ord[2] == "P") {
                                return
                            }
                            ord[0] = "P"

                            // Update servo position
                            if (cLP == 1) {
                                bowlServo.position = lP2
                                cLP = 2
                            }
                        } else if (ord[1] == "N") {
                            // Check if already detected
                            if (ord[1] == "P" || ord[2] == "P") {
                                return
                            }
                            ord[1] = "P"

                            // Update servo position
                            if (cLP == 2) {
                                bowlServo.position = lP3
                                cLP = 3
                            }
                        } else if (ord[2] == "N") {
                            // Check if already detected
                            if (ord[2] == "P") {
                                return
                            }
                            ord[2] = "P"

                            // No more servo positions (already at lP3)
                        } else {
                            // List is full
                            return
                        }

                        sleep(800)
                    }
                }
            }
            when (greenCount) {
                1 -> {
                    if (it.width >= 280 && it.height >= 170) {
                        // Fill the list array for green pieces
                        if (ord[0] == "N") {
                            // Check if green already detected
                            if (ord[0] == "G" || ord[1] == "G" || ord[2] == "G") {
                                return
                            }
                            ord[0] = "G"

                            // Update servo position
                            if (cLP == 1) {
                                bowlServo.position = lP2
                                cLP = 2
                            }
                        } else if (ord[1] == "N") {
                            // Check if green already detected
                            if (ord[0] == "G" || ord[1] == "G" || ord[2] == "G") {
                                return
                            }
                            ord[1] = "G"

                            // Update servo position
                            if (cLP == 2) {
                                bowlServo.position = lP3
                                cLP = 3
                            }
                        } else if (ord[2] == "N") {
                            // Check if green already detected
                            if (ord[0] == "G" || ord[1] == "G" || ord[2] == "G") {
                                return
                            }
                            ord[2] = "G"

                            // No more servo positions (already at lP3)
                        } else {
                            // List is full
                            return
                        }

                        sleep(800)
                    }
                }
            }

            panels?.addData("order 1", ord[0])
            panels?.addData("order 2", ord[1])
            panels?.addData("order 3", ord[2])
            panels?.addLine("Best (by area)")
            panels?.addData(" colorId", it.colorId)
            panels?.addData(" tx(norm)", it.tx)
            panels?.addData(" ty(norm)", it.ty)
            panels?.addData(" area(px)", it.ta)
            panels?.addData(" W", it.width)
            panels?.addData(" H", it.height)
        }

        when (held) {
            0 -> {
                // Do nothing
            }
            1 -> {
                outTake1.power = 0.3
                outTake2.power = 0.3
                sleep(1000)
                if (ord[0] != "N" && ord[1] != "N" && ord[2] != "N") {
                    if (eord[0] == "G" && eord[1] == "P" && eord[2] == "P") {
                        if (ord.contentEquals(eord)) {
                            sleep(5000)
                            bowlServo.position = fP1
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            bowlServo.position = fP2
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            bowlServo.position = fP3
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            eord[0] = ""
                            eord[1] = ""
                            eord[2] = ""
                            ord[0] = "N"
                            ord[1] = "N"
                            ord[2] = "N"
                        }
                        if (ord[0] == "P" && ord[1] == "P" && ord[2] == "G") {
                            sleep(5000)
                            bowlServo.position = fP3
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            bowlServo.position = fP2
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            bowlServo.position = fP1
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            eord[0] = ""
                            eord[1] = ""
                            eord[2] = ""
                            ord[0] = "N"
                            ord[1] = "N"
                            ord[2] = "N"
                        }
                        if (ord[0] == "P" && ord[1] == "G" && ord[2] == "P") {
                            sleep(5000)
                            bowlServo.position = fP2
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            bowlServo.position = fP1
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            bowlServo.position = fP3
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            eord[0] = ""
                            eord[1] = ""
                            eord[2] = ""
                            ord[0] = "N"
                            ord[1] = "N"
                            ord[2] = "N"
                        }
                    }
                    if (eord[0] == "P" && eord[1] == "G" && eord[2] == "P") {
                        if (ord.contentEquals(eord)) {
                            sleep(5000)
                            bowlServo.position = fP1
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            bowlServo.position = fP2
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            bowlServo.position = fP3
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            eord[0] = ""
                            eord[1] = ""
                            eord[2] = ""
                            ord[0] = "N"
                            ord[1] = "N"
                            ord[2] = "N"
                        }
                        if (ord[0] == "P" && ord[1] == "P" && ord[2] == "G") {
                            sleep(5000)
                            bowlServo.position = fP1
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            bowlServo.position = fP3
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            bowlServo.position = fP2
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            eord[0] = ""
                            eord[1] = ""
                            eord[2] = ""
                            ord[0] = "N"
                            ord[1] = "N"
                            ord[2] = "N"
                        }
                        if (ord[0] == "G" && ord[1] == "P" && ord[2] == "P") {
                            sleep(5000)
                            bowlServo.position = fP2
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            bowlServo.position = fP1
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            bowlServo.position = fP3
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            eord[0] = ""
                            eord[1] = ""
                            eord[2] = ""
                            ord[0] = "N"
                            ord[1] = "N"
                            ord[2] = "N"
                        }
                    }
                    if (eord[0] == "P" && eord[1] == "P" && eord[2] == "G") {
                        if (ord.contentEquals(eord)) {
                            sleep(5000)
                            bowlServo.position = fP1
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            bowlServo.position = fP2
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            bowlServo.position = fP3
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            eord[0] = ""
                            eord[1] = ""
                            eord[2] = ""
                            ord[0] = "N"
                            ord[1] = "N"
                            ord[2] = "N"
                        }
                        if (ord[0] == "P" && ord[1] == "G" && ord[2] == "P") {
                            sleep(5000)
                            bowlServo.position = fP1
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            bowlServo.position = fP3
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            bowlServo.position = fP2
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            eord[0] = ""
                            eord[1] = ""
                            eord[2] = ""
                            ord[0] = "N"
                            ord[1] = "N"
                            ord[2] = "N"
                        }
                        if (ord[0] == "G" && ord[1] == "P" && ord[2] == "P") {
                            sleep(5000)
                            bowlServo.position = fP2
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            bowlServo.position = fP3
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            bowlServo.position = fP1
                            sleep(1000)
                            outServo.position = 0.25
                            sleep(500)
                            outServo.position = 0.0
                            sleep(5000)
                            eord[0] = ""
                            eord[1] = ""
                            eord[2] = ""
                            ord[0] = "N"
                            ord[1] = "N"
                            ord[2] = "N"
                        }
                    }
                }
                sleep(1000)
                outTake1.power = 0.0
                outTake2.power = 0.0
                bowlServo.position = lP1
                sleep(1000)
                held = 0
                return
            }
        }

        panels?.update()
    }

    override fun stop() {
        limelight.stop()
        panels?.addData("Status", "Stopped")
        panels?.update()
    }
}