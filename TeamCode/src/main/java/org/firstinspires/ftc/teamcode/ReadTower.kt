package org.firstinspires.ftc.teamcode

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import java.io.File

@Autonomous
class ReadTower : OpMode() {
    private var panels: TelemetryManager? = null
    private val fileName = "array.txt"
    var order = arrayOf("", "", "") //Place holder at start of match will change when tag is detected
    override fun init() {
        var currentOrder = getOrder()
        panels = PanelsTelemetry.telemetry
    }

    override fun loop() {
        panels?.addData("order", order)
    }

    private fun read(): Int {
        val file = File(hardwareMap.appContext.filesDir, fileName)
        return (if (file.exists()) {
            file.readText()
        } else {
            0
        }) as Int
    }

    fun getOrder() {
        var tagID = read()
        when (tagID) {
            21 -> {
                order[0] = "G"
                order[1] = "P"
                order[2] = "P"
            }
            22 -> {
                order[0] = "P"
                order[1] = "G"
                order[2] = "P"
            }
            23 -> {
                order[0] = "P"
                order[1] = "P"
                order[2] = "G"
            }
        }
    }
}