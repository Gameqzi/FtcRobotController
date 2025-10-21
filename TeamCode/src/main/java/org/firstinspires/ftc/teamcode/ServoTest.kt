package org.firstinspires.ftc.teamcode

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import java.lang.Thread.sleep
import kotlin.concurrent.thread

@TeleOp
class ServoTest : OpMode() {
    var panels: TelemetryManager? = null
    lateinit var servo : Servo
    override fun init() {
        servo = hardwareMap.get(Servo::class.java, "Servo")
        panels = PanelsTelemetry.telemetry
        var t = thread(start = true, name = "ServoTelemetryThread") {
            while (!Thread.currentThread().isInterrupted) {
                panels?.apply {
                    addLine("Servo Position: ${"%.2f".format(servo.position)}")
                    update()
                }
            }
        }
    }

    override fun loop() {
        servo.position = 0.0
        sleep(4000)
        servo.position = 0.4
        sleep(4000)
        servo.position = 0.8
        sleep(4000)
        servo.position = 1.0
        sleep(4000)
        servo.position = 0.6
        sleep(4000)
        servo.position = 0.3
        sleep(4000)
        stop()
    }

    override fun stop() {
        Thread.currentThread().interrupt()
        super.stop()
    }
}