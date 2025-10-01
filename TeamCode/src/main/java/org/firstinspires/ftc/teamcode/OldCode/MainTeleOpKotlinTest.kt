package org.firstinspires.ftc.teamcode.OldCode

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.ColorSensor
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import kotlin.math.abs

class MainTeleOpKotlinTest : OpMode() {
    private var panels: TelemetryManager? = null
    private lateinit var frontLeft: DcMotorEx
    private lateinit var frontRight: DcMotorEx
    private lateinit var backLeft: DcMotorEx
    private lateinit var backRight: DcMotorEx
    private lateinit var lift: DcMotorEx
    private lateinit var colorSensor: ColorSensor
    private lateinit var servo1: CRServo
    private lateinit var servo2: CRServo
    val p = 10.toDouble()
    val i = 3.toDouble()
    val d = 0.toDouble()
    val f = 8.toDouble()

    val lp = 10.toDouble()
    val li = 3.toDouble()
    val ld = 0.toDouble()
    val lf = 8.toDouble()

    var dir = 1

    var blockin = false

    var liftPos = 0

    override fun init() {
        frontLeft = hardwareMap.get(DcMotorEx::class.java, "frontLeft")
        frontRight = hardwareMap.get(DcMotorEx::class.java, "frontRight")
        backLeft = hardwareMap.get(DcMotorEx::class.java, "backLeft")
        backRight = hardwareMap.get(DcMotorEx::class.java, "backRight")
        lift = hardwareMap.get(DcMotorEx::class.java, "lift")
        servo1 = hardwareMap.get(CRServo::class.java, "IntakeServo1")
        servo2 = hardwareMap.get(CRServo::class.java, "IntakeServo2")
        colorSensor = hardwareMap.get(colorSensor::class.java, "ColorSensor")

        frontRight.direction = DcMotorSimple.Direction.REVERSE
        backRight.direction = DcMotorSimple.Direction.FORWARD
        frontLeft.direction = DcMotorSimple.Direction.REVERSE
        backLeft.direction = DcMotorSimple.Direction.FORWARD
        lift.direction = DcMotorSimple.Direction.REVERSE

        resetEncoders()

        panels = PanelsTelemetry.telemetry
    }

    override fun loop() {
        frontRight.setVelocityPIDFCoefficients(p, i, d, f)
        backRight.setVelocityPIDFCoefficients(p, i, d, f)
        frontLeft.setVelocityPIDFCoefficients(p, i, d, f)
        backLeft.setVelocityPIDFCoefficients(p, i, d, f)
        lift.setVelocityPIDFCoefficients(lp, li, ld ,lf)

        var red = colorSensor.red()
        colorSensor.green()
        colorSensor.blue()

        val drive = gamepad1.left_stick_y.toDouble() // Forward/backward (inverted as necessary)
        val strafe = -gamepad1.right_stick_x.toDouble() // Left/right
        val rotate = (gamepad1.left_trigger - gamepad1.right_trigger).toDouble() // Rotation

        val frontLeftPower = drive + strafe + rotate
        val frontRightPower = drive - strafe - rotate
        val backLeftPower = drive - strafe + rotate
        val backRightPower = drive + strafe - rotate

        // Set the calculated power to each motor
        frontLeft.velocity = -frontLeftPower * 500
        frontRight.velocity = frontRightPower * 500
        backLeft.velocity = backLeftPower * 500
        backRight.velocity =  -backRightPower * 500

        if (gamepad1.cross && abs(lift.currentPosition) < 2900) {
            if (abs(liftPos) > 2900) {
                liftPos -= 5
            } else {
                liftPos += 5
            }
            lift.velocity = -1750.0
        } else if (gamepad1.circle && lift.currentPosition < 70) {
            if (abs(liftPos) > 70) {
                liftPos -= 5
            } else {
                liftPos += 5
            }
        } else {
            lift.velocity = 0.0
        }

        lift.velocity = 5000.toDouble()

        if (gamepad1.triangle) {
            liftPos = 2850
        }

        if (gamepad1.square) {
            liftPos = 70
        }

        dir = if (gamepad1.left_bumper) {
            2
        } else if (gamepad1.right_bumper) {
            1
        } else {
            0
        }

        if (red > 180) {
            blockin = true
        }

        if (red > 170 && dir == 2) {
            blockin = false
        }

        if (!blockin) {
            if (dir == 0) {
                servo1.power = 0.15
                servo2.power = -0.15
            }
        }
        if (blockin) {
            when (dir) {
                2 -> {
                    servo1.power = -0.7
                    servo2.power = 0.7
                }
                0 -> {
                    servo1.power = 0.0
                    servo2.power = 0.0
                }
                1 -> {
                    servo1.power = -1.0
                    servo2.power = 1.0
                }
            }
        }

        lift.targetPosition = liftPos
        lift.mode = DcMotor.RunMode.RUN_TO_POSITION

        panels!!.addData("ticks", lift.currentPosition)
        panels!!.addData("LiftPos", liftPos)
        panels!!.update()
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
        lift.mode       = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        frontLeft.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        frontRight.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        backLeft.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        backRight.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }
}