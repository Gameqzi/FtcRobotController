package org.firstinspires.ftc.teamcode.OldCode

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.hardware.limelightvision.LLResult
import com.qualcomm.hardware.limelightvision.Limelight3A
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@Disabled
@TeleOp(name = "Limelight 3A — Python Output Test (OpMode)", group = "Vision")
class LimelightPythonOpModeTest : OpMode() {
    private var panels: TelemetryManager? = null

    val lP1 = 0.059
    val lP2 = 0.13
    val lP3 = 0.204
    val fP1 = 0.167
    val fP2 = 0.02
    val fP3 = 0.0945
    var cLP = 1

    val ord = arrayOf("N", "N", "N")
    val eord = arrayOf("P", "G", "P")

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
        limelight.setPollRateHz(100)     // fast polling
        limelight.pipelineSwitch(0)      // <- change to your pipeline slot
        panels?.addLine("Limelight init complete.")
    }

    override fun start() {
        limelight.start()
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
    override fun stop() {
        limelight.stop()
        panels?.addData("Status", "Stopped")
        panels?.update()
    }
}