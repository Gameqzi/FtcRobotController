package org.firstinspires.ftc.teamcode

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple

@TeleOp
class TeleOp : OpMode() {
    private var panels: TelemetryManager? = null
    private lateinit var frontLeft: DcMotorEx
    private lateinit var frontRight: DcMotorEx
    private lateinit var backLeft: DcMotorEx
    private lateinit var backRight: DcMotorEx
    val p = 10.toDouble()
    val i = 3.toDouble()
    val d = 0.toDouble()
    val f = 8.toDouble()
    var speed = false
    override fun init() {
        frontLeft = hardwareMap.get(DcMotorEx::class.java, "frontLeft")
        frontRight = hardwareMap.get(DcMotorEx::class.java, "frontRight")
        backLeft = hardwareMap.get(DcMotorEx::class.java, "backLeft")
        backRight = hardwareMap.get(DcMotorEx::class.java, "backRight")
        frontRight.direction = DcMotorSimple.Direction.REVERSE
        backRight.direction = DcMotorSimple.Direction.FORWARD
        frontLeft.direction = DcMotorSimple.Direction.REVERSE
        backLeft.direction = DcMotorSimple.Direction.FORWARD
        frontRight.setVelocityPIDFCoefficients(p, i, d, f)
        backRight.setVelocityPIDFCoefficients(p, i, d, f)
        frontLeft.setVelocityPIDFCoefficients(p, i, d, f)
        backLeft.setVelocityPIDFCoefficients(p, i, d, f)
        resetEncoders()
        panels = PanelsTelemetry.telemetry
    }

    override fun loop() {
        var drive = gamepad1.left_stick_y.toDouble() // Forward/backward (inverted as necessary)
        var strafe = -gamepad1.right_stick_x.toDouble() // Left/right
        var rotate = (gamepad1.left_trigger - gamepad1.right_trigger).toDouble() // Rotation

        var frontLeftPower = drive + strafe + rotate
        var frontRightPower = drive - strafe - rotate
        var backLeftPower = drive - strafe + rotate
        var backRightPower = drive + strafe - rotate

        
        if (gamepad1.left_bumper) {
            speed = false
        } else if (gamepad1.right_bumper) {
            speed = true
        }

        if (!speed) {
            frontLeft.velocity = -frontLeftPower * 1000
            frontRight.velocity = frontRightPower * 1000
            backLeft.velocity = backLeftPower * 1000
            backRight.velocity =  -backRightPower * 1000
        } else {
            frontLeft.velocity = -frontLeftPower * 5000
            frontRight.velocity = frontRightPower * 5000
            backLeft.velocity = backLeftPower * 5000
            backRight.velocity =  -backRightPower * 5000
        }
    }

    fun resetEncoders() {
        frontLeft.mode  = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        frontLeft.mode  = DcMotor.RunMode.RUN_USING_ENCODER
        frontRight.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        frontRight.mode = DcMotor.RunMode.RUN_USING_ENCODER
        backLeft.mode   = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        backLeft.mode   = DcMotor.RunMode.RUN_USING_ENCODER
        backRight.mode  = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        backRight.mode  = DcMotor.RunMode.RUN_USING_ENCODER
        frontLeft.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        frontRight.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        backLeft.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        backRight.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }
}