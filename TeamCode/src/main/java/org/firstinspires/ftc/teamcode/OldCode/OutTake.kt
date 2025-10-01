package org.firstinspires.ftc.teamcode.OldCode

import com.bylazar.configurables.annotations.Configurable
import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple

@Configurable
@TeleOp
class OutTake : OpMode() {
    private var panels: TelemetryManager? = null
    private lateinit var motor1: DcMotorEx
    private lateinit var motor2: DcMotorEx

    companion object {
        @JvmField
        var power1 = 0.toDouble()
        var power2 = 0.toDouble()
    }

    override fun init() {
        motor1 = hardwareMap.get(DcMotorEx::class.java, "motor1")
        motor2 = hardwareMap.get(DcMotorEx::class.java, "motor2")
        motor2.direction = DcMotorSimple.Direction.REVERSE
        panels = PanelsTelemetry.telemetry
    }

    override fun loop() {
        motor1.power = power1
        motor2.power = power2
        panels!!.addData("Power1", power1)
        panels!!.addData("Power2", power2)
        panels!!.addData("Real Motor 1 Power", motor1.power)
        panels!!.addData("Real Motor 2 Power", motor2.power)
    }
}
