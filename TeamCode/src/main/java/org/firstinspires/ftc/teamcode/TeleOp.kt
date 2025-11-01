package org.firstinspires.ftc.teamcode

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo

@TeleOp
class TeleOp : OpMode() {
    private var panels: TelemetryManager? = null
    private lateinit var frontLeft: DcMotorEx
    private lateinit var frontRight: DcMotorEx
    private lateinit var backLeft: DcMotorEx
    private lateinit var backRight: DcMotorEx
    private lateinit var intakeServo1: CRServo
    private lateinit var intakeServo2: CRServo
    private lateinit var camServo : Servo
    val p = 10.toDouble()
    val i = 3.toDouble()
    val d = 0.toDouble()
    val f = 8.toDouble()
    var driveSpeed = false
    var servoSpeed = 0.0
    var moveMode   = 0
    private var lastDpadLeftState = false
    private var lastDpadRightState = false

    override fun init() {
        frontLeft  = hardwareMap.get(DcMotorEx::class.java, "frontLeft")
        frontRight = hardwareMap.get(DcMotorEx::class.java, "frontRight")
        backLeft   = hardwareMap.get(DcMotorEx::class.java, "backLeft")
        backRight  = hardwareMap.get(DcMotorEx::class.java, "backRight")
        intakeServo1     = hardwareMap.get(CRServo::class.java, "intakeServo1")
        intakeServo2     = hardwareMap.get(CRServo::class.java, "intakeServo2")
        camServo  = hardwareMap.get(Servo::class.java, "CamServo")
        frontRight.direction = DcMotorSimple.Direction.REVERSE
        backRight.direction  = DcMotorSimple.Direction.FORWARD
        frontLeft.direction  = DcMotorSimple.Direction.REVERSE
        backLeft.direction   = DcMotorSimple.Direction.FORWARD
        intakeServo2.direction     = DcMotorSimple.Direction.REVERSE
        frontRight.setVelocityPIDFCoefficients(p, i, d, f)
        backRight.setVelocityPIDFCoefficients(p, i, d, f)
        frontLeft.setVelocityPIDFCoefficients(p, i, d, f)
        backLeft.setVelocityPIDFCoefficients(p, i, d, f)
        panels = PanelsTelemetry.telemetry
        resetEncoders()
    }

    override fun loop() {
        var drive  = gamepad1.left_stick_y.toDouble() // Forward/backward
        var strafe = -gamepad1.right_stick_x.toDouble() // Left/right
        var rotate = (gamepad1.left_trigger - gamepad1.right_trigger).toDouble() // Rotation

        var frontLeftPower  = drive + strafe + rotate
        var frontRightPower = drive - strafe - rotate
        var backLeftPower   = drive - strafe + rotate
        var backRightPower  = drive + strafe - rotate

        if (gamepad1.left_bumper) {
            driveSpeed = false
        } else if (gamepad1.right_bumper) {
            driveSpeed = true
        }

        if (gamepad1.cross) {
            servoSpeed = 1.0
            intakeServo1.power = servoSpeed
            intakeServo2.power = servoSpeed
        } else if (gamepad1.circle) {
            servoSpeed = -1.0
            intakeServo1.power = servoSpeed
            intakeServo2.power = servoSpeed
        } else {
            servoSpeed = 0.0
            intakeServo1.power = servoSpeed
            intakeServo2.power = servoSpeed
        }

        if (!driveSpeed) {
            frontLeft.velocity  = -frontLeftPower * 1000
            frontRight.velocity = frontRightPower * 1000
            backLeft.velocity   = backLeftPower * 1000
            backRight.velocity  = -backRightPower * 1000
        } else {
            frontLeft.velocity  = -frontLeftPower * 8000
            frontRight.velocity = frontRightPower * 8000
            backLeft.velocity   = backLeftPower * 8000
            backRight.velocity  = -backRightPower * 8000
        }

        if (gamepad1.dpad_left && !lastDpadLeftState) {
            moveMode -= 1
            if (moveMode < 0) {
                moveMode = 0
            }
        }
        lastDpadLeftState = gamepad1.dpad_left

        if (gamepad1.dpad_right && !lastDpadRightState) {
            moveMode += 1
            if (moveMode > 2) {
                moveMode = 2
            }
        }
        lastDpadRightState = gamepad1.dpad_right

        when (moveMode) {
            0 -> {
                camServo.position = 0.0
            }
            1 -> {
                camServo.position = 0.5
            }
            2 -> {
                camServo.position = 1.toDouble()
            }
        }

        panels?.addData("MoveMode", moveMode)
        panels?.addData("MoveServo", camServo.position)
        panels?.update()
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
        frontLeft.zeroPowerBehavior  = DcMotor.ZeroPowerBehavior.BRAKE
        frontRight.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        backLeft.zeroPowerBehavior   = DcMotor.ZeroPowerBehavior.BRAKE
        backRight.zeroPowerBehavior  = DcMotor.ZeroPowerBehavior.BRAKE
    }
}