package org.firstinspires.ftc.teamcode

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.Servo
import java.lang.Thread.sleep

class ServoTest : OpMode() {
    private var panels: TelemetryManager? = null
    private lateinit var servo : Servo
    override fun init() {
        servo = hardwareMap.get(Servo::class.java, "Servo")
        panels = PanelsTelemetry.telemetry
    }

    override fun loop() {
        sleep(2000)
        servo.position = 0.4
        sleep(2000)
        servo.position = 0.8
        sleep(2000)
        servo.position = 1.toDouble()
        panels?.addData("position", servo.position)
    }
}