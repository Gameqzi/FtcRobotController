package org.firstinspires.ftc.teamcode

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import java.io.File
import java.io.Serializable

@Autonomous
class ReadTower : OpMode() {
    private var panels: TelemetryManager? = null
    private val fileName = "array.txt"
    override fun init() {
        panels = PanelsTelemetry.telemetry
    }

    override fun loop() {
        panels?.addData("Order", read())
        panels?.update()
    }

    private fun read(): Serializable {
        val file = File(hardwareMap.appContext.filesDir, fileName)
        return if (file.exists()) {
            file.readText().toCharArray()
        } else {
            0
        }
    }
}