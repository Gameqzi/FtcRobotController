package org.firstinspires.ftc.teamcode

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.Utils.sleep

@TeleOp
class OutTake : OpMode() {
    private var panels: TelemetryManager? = null
    private lateinit var motor1: DcMotorEx
    private lateinit var motor2: DcMotorEx
    var power = 1.toDouble()
    var ar = arrayOf("p", "p", "g") // Test to make a array

    override fun init() {
        motor1 = hardwareMap.get(DcMotorEx::class.java, "motor1")
        motor2 = hardwareMap.get(DcMotorEx::class.java, "motor2")
        motor2.direction = DcMotorSimple.Direction.REVERSE
        panels = PanelsTelemetry.telemetry
    }

    override fun loop() {
        if (gamepad1.dpad_up) {
            power + 0.1
            sleep(1000)
        } else if (gamepad1.dpad_down) {
            power - 0.1
            sleep(1000)
        }
        motor1.power = power
        motor2.power = power
        panels!!.addData("Power", power)
        panels!!.addData("Real Motor 1 Power", motor1.power)
        panels!!.addData("Real Motor 2 Power", motor2.power)
    }
}
