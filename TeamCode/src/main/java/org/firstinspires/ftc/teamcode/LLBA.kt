package org.firstinspires.ftc.teamcode

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.hardware.limelightvision.LLResult
import com.qualcomm.hardware.limelightvision.Limelight3A
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp
class LLBA : OpMode() {
    private var panels: TelemetryManager? = null

    private lateinit var limelight: Limelight3A

    override fun init() {
        panels = PanelsTelemetry.telemetry
        // Initialize Limelight
        limelight = hardwareMap.get(Limelight3A::class.java, "limelight")
        limelight.pipelineSwitch(0) // Switch to your Python pipeline (adjust index as needed)
        limelight.start()

        panels?.addData("Status", "Initialized")
        panels?.update()
    }

    override fun loop() {
        val colorArray = getColorArray()

        // Display results
        panels?.addData("Color Array", colorArray.contentToString())
        panels?.addData("First Color", colorArray[0].ifEmpty { "None" })
        panels?.addData("Second Color", colorArray[1].ifEmpty { "None" })
        panels?.addData("Third Color", colorArray[2].ifEmpty { "None" })
        panels?.update()
    }

    override fun stop() {
        limelight.stop()
    }

    /**
     * Gets the first 3 detected colors from the Limelight Python pipeline
     * @return Array of 3 strings: "G" for green, "P" for purple, "" for empty
     */
    private fun getColorArray(): Array<String> {
        val colorArray = arrayOf("", "", "")

        // Get the latest result from Limelight
        val result: LLResult? = limelight.latestResult

        if (result != null && result.isValid) {
            // Get Python pipeline outputs (llpython array from your script)
            val pythonOutputs: DoubleArray? = result.pythonOutput

            if (pythonOutputs != null && pythonOutputs.isNotEmpty()) {
                // Each target has 6 floats: [tx, ty, ta, color_id, tl, th]
                // color_id is at index 3 (0-indexed), then 9, then 15, etc.

                var arrayIndex = 0
                var i = 0

                // Process up to 3 targets (or until we run out of data)
                while (i + 5 < pythonOutputs.size && arrayIndex < 3) {
                    val colorId = pythonOutputs[i + 3]  // Get color_id (4th element)

                    when (colorId) {
                        1.0 -> colorArray[arrayIndex] = "G"  // Green
                        2.0 -> colorArray[arrayIndex] = "P"  // Purple
                        else -> colorArray[arrayIndex] = ""   // Unknown/invalid
                    }

                    arrayIndex++
                    i += 6  // Move to next target (skip 6 floats)
                }
            }
        }

        return colorArray
    }
}