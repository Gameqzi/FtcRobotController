package org.firstinspires.ftc.teamcode

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.hardware.limelightvision.LLResult
import com.qualcomm.hardware.limelightvision.Limelight3A
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import java.lang.Thread.sleep

@TeleOp(name = "Limelight 3A — Python Output (OpMode)", group = "Vision")
class LimelightPythonOpMode : OpMode() {
    private var panels: TelemetryManager? = null

    val lP1 = 0.059
    val lP2 = 0.13
    val lP3 = 0.204
    val fP1 = 0.02
    val fP2 = 0.0945
    val fP3 = 0.167
    var cLP = 1

    val ord = arrayOf("N", "N", "N")
    val eord = arrayOf("P", "P", "G")

    lateinit var servo : Servo

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
        limelight = hardwareMap.get(Limelight3A::class.java, "limelight")
        servo = hardwareMap.get(Servo::class.java, "Servo")
        limelight.setPollRateHz(100)     // fast polling
        limelight.pipelineSwitch(0)      // <- change to your pipeline slot
        panels?.addLine("Limelight init complete.")
    }

    override fun start() {
        limelight.start()
        servo.position = lP1
    }

    override fun loop() {
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
        val targets = ArrayList<Target>(py.size / stride)
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
                    if (it.width >= 330 && it.height >= 220) {
                        if (cLP == 1) {
                            servo.position = lP2
                            cLP = 2
                        } else if (cLP == 2) {
                            servo.position = lP3
                            cLP = 3
                        }
                    }
                    if (ord[0] == "N") {
                        ord[0] = "P"
                    }
                    sleep(800)
                }
            }
            panels?.addData("Order 1", ord[0])
            panels?.addData("Order 2", ord[1])
            panels?.addData("Order 3", ord[2])
            panels?.addLine("Best (by area)")
            panels?.addData(" colorId", it.colorId)
            panels?.addData(" tx(norm)", it.tx)
            panels?.addData(" ty(norm)", it.ty)
            panels?.addData(" area(px)", it.ta)
            panels?.addData(" W", it.width)
            panels?.addData(" H", it.height)
        }
        panels?.update()
    }
}