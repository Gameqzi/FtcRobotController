package org.firstinspires.ftc.teamcode

import com.bylazar.configurables.annotations.Configurable
import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorSimple

@Configurable
@TeleOp
class CRServoTest : OpMode() {
    var panels: TelemetryManager? = null
    lateinit var servo : CRServo
    lateinit var servo2 : CRServo
    companion object {
        @JvmField
        var power = 0.toDouble()
        var power2 = 0.toDouble()
    }

    override fun init() {
        servo = hardwareMap.get(CRServo::class.java, "Servo")
        servo2 = hardwareMap.get(CRServo::class.java, "Servo2")
        servo2.direction = DcMotorSimple.Direction.REVERSE
        panels = PanelsTelemetry.telemetry
    }

    override fun loop() {
        servo.power = power
        servo2.power = power2
    }
}