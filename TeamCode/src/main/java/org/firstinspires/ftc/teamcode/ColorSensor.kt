package org.firstinspires.ftc.teamcode

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.ColorSensor
import java.lang.Thread.sleep

@TeleOp
class ColorSensor: OpMode() {
    private var panels: TelemetryManager? = null
    private lateinit var colorSensor: ColorSensor
    private var list = arrayOf("null", "null", "null") // Placeholder until we get the values
    private var realList = arrayOf("p", "p", "g")
    override fun init() {
        colorSensor = hardwareMap.get(ColorSensor::class.java, "ColorSensor")
        panels = PanelsTelemetry.telemetry
    }

    override fun loop() {
        var r = colorSensor.red() + 500
        var g = colorSensor.green() + 500
        var b = colorSensor.blue() + 500
        if (g >= 9000) {
            if (list[0] == "null") {
                if (list[0] != "null") {
                    return
                }
                if (list[0] == "g" || list[1] == "g" || list[2] == "g" || list[0] == "p" || list[1] == "p" || list[2] == "p") {
                    return
                }
                list[0] = "g"
            } else if (list[1] == "null") {
                if (list[1] != "null") {
                    return
                }
                if (list[0] == "g" || list[1] == "g" || list[2] == "g" || list[0] == "p" || list[1] == "p" || list[2] == "p") {
                    return
                }
                list[1] = "g"
            } else if (list[2] == "null") {
                if (list[2] != "null") {
                    return
                }
                if (list[0] == "g" || list[1] == "g" || list[2] == "g" || list[0] == "p" || list[1] == "p" || list[2] == "p") {
                    return
                }
                list[2] = "g"
            } else {
                return
            }
            sleep(500)
        }

        if (r >= 900 && b >= 900) {
            if (list[0] == "null") {
                if (list[0] != "null") {
                    return
                }
                if (list[0] == "p" || list[1] == "p" || list[2] == "p" || list[0] == "g" || list[1] == "g" || list[2] == "g") {
                    return
                }
                list[0] = "p"
            } else if (list[1] == "null") {
                if (list[1] != "null") {
                    return
                }
                if (list[0] == "p" || list[1] == "p" || list[2] == "p" || list[0] == "g" || list[1] == "g" || list[2] == "g") {
                    return
                }
                list[1] = "p"
            } else if (list[2] == "null") {
                if (list[2] != "null") {
                    return
                }
                if (list[0] == "p" || list[1] == "p" || list[2] == "p" || list[0] == "g" || list[1] == "g" || list[2] == "g") {
                    return
                }
                list[2] = "p"
            } else {
                return
            }
            sleep(500)
        }

        panels?.addData("List 1", list[0])
        panels?.addData("List 2", list[1])
        panels?.addData("List 3", list[2])
        panels?.addData("R", r)
        panels?.addData("G", g)
        panels?.addData("B", b)
        panels?.update()
    }
}