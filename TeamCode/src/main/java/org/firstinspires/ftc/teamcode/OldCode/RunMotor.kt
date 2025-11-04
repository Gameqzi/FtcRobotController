package org.firstinspires.ftc.teamcode.OldCode

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx

@Disabled
@TeleOp
class RunMotor: OpMode() {
    private var panels: TelemetryManager? = null
    private lateinit var testMotor : DcMotorEx
    val targetPosition = 1425
    val p = 10.toDouble()
    val i = 3.toDouble()
    val d = 0.toDouble()
    val f = 8.toDouble()
    override fun init() {
        testMotor = hardwareMap.get(DcMotorEx::class.java, "testMotor")
        testMotor.zeroPowerBehavior  = DcMotor.ZeroPowerBehavior.BRAKE
        testMotor.setVelocityPIDFCoefficients(p, i, d, f)
        testMotor.mode  = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        testMotor.targetPosition = targetPosition
        testMotor.mode = DcMotor.RunMode.RUN_TO_POSITION
        panels = PanelsTelemetry.telemetry
    }

    override fun loop() {
        if (testMotor.currentPosition < targetPosition) {
            testMotor.velocity = 4000.toDouble()
        } else if (testMotor.currentPosition > targetPosition) {
            testMotor.velocity = (-1100).toDouble()
        }
        panels?.addData("CurrentPosition", testMotor.currentPosition)
        panels?.addData("velocity", testMotor.velocity)
        panels?.update()
    }
}