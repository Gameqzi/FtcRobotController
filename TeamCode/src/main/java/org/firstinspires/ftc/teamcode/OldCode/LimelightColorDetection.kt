package org.firstinspires.ftc.teamcode.OldCode

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.hardware.limelightvision.LLResult
import com.qualcomm.hardware.limelightvision.Limelight3A
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@Disabled
@TeleOp(name = "Limelight Color Detection")
class LimelightColorDetection : OpMode() {
    private var panels: TelemetryManager? = null

    private lateinit var limelight: Limelight3A

    /**
     * Data class representing a detected color target from the Limelight
     */
    data class ColorTarget(
        val normalizedX: Double,      // tx: -1.0 to 1.0 (left to right)
        val normalizedY: Double,      // ty: -1.0 to 1.0 (bottom to top)
        val area: Double,             // ta: area in pixels
        val colorId: Int,             // ts: 1 = GREEN, 2 = PURPLE
        val width: Double,            // tl: bounding box width
        val height: Double            // th: bounding box height
    ) {
        val isGreen: Boolean get() = colorId == 1
        val isPurple: Boolean get() = colorId == 2

        fun getColorName(): String = when (colorId) {
            1 -> "GREEN"
            2 -> "PURPLE"
            else -> "UNKNOWN"
        }
    }

    override fun init() {
        panels = PanelsTelemetry.telemetry
        limelight = hardwareMap.get(Limelight3A::class.java, "limelight")
        limelight.pipelineSwitch(0)  // Switch to pipeline 0 (your Python pipeline)
        limelight.start()

        panels?.addData("Status", "Initialized")
        panels?.update()
    }

    override fun init_loop() {
        panels?.addData("Status", "Waiting for start")
        panels?.addData("Limelight", "Connected: ${limelight.isConnected}")
        panels?.update()
    }

    override fun start() {
        panels?.addData("Status", "Started")
        panels?.update()
    }

    override fun loop() {
        // Get all targets
        val allTargets = getColorTargets()

        // Get specific color targets
        val greenTargets = allTargets.filter { it.isGreen }
        val purpleTargets = allTargets.filter { it.isPurple }

        // Get largest targets
        val largestGreen = greenTargets.firstOrNull()
        val largestPurple = purpleTargets.firstOrNull()

        // Example: Drive towards the largest green target
        largestGreen?.let { target ->
            panels?.addData("Targeting", "GREEN")
        }

        // Display panels?
        updateTelemetry(allTargets)
        panels?.update()
    }

    override fun stop() {
        limelight.stop()
        panels?.addData("Status", "Stopped")
        panels?.update()
    }

    /**
     * Parse the llpython array into a list of ColorTarget objects
     */
    private fun getColorTargets(): List<ColorTarget> {
        val result: LLResult = limelight.latestResult ?: return emptyList()

        // Check if we have Python output
        if (!result.isValid) {
            return emptyList()
        }

        // Get the llpython array (flat list of doubles)
        val pythonOutput: DoubleArray = result.pythonOutput ?: return emptyList()

        // Each target has 6 values: [tx, ty, ta, ts, tl, th]
        if (pythonOutput.size % 6 != 0) {
            return emptyList()
        }

        val targets = mutableListOf<ColorTarget>()

        // Parse targets (already sorted by area in Python)
        for (i in pythonOutput.indices step 6) {
            val target = ColorTarget(
                normalizedX = pythonOutput[i],
                normalizedY = pythonOutput[i + 1],
                area = pythonOutput[i + 2],
                colorId = pythonOutput[i + 3].toInt(),
                width = pythonOutput[i + 4],
                height = pythonOutput[i + 5]
            )
            targets.add(target)
        }

        return targets
    }

    /**
     * Display panels? data
     */
    private fun updateTelemetry(targets: List<ColorTarget>) {
        val greenCount = targets.count { it.isGreen }
        val purpleCount = targets.count { it.isPurple }

        panels?.addData("Limelight", "Connected: ${limelight.isConnected}")
        panels?.addData("Green Targets", greenCount)
        panels?.addData("Purple Targets", purpleCount)
        panels?.addData("Total Targets", targets.size)

        targets.forEachIndexed { index, target ->
            panels?.addLine("")
            panels?.addData("Target $index", target.getColorName())
            panels?.addData("  Position", "X: %.2f, Y: %.2f".format(target.normalizedX, target.normalizedY))
            panels?.addData("  Size", "Area: %.0f, W: %.0f, H: %.0f".format(target.area, target.width, target.height))
        }
    }
}