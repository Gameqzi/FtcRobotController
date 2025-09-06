package org.firstinspires.ftc.teamcode

import android.annotation.SuppressLint
import com.qualcomm.hardware.sparkfun.SparkFunOTOS
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

@Autonomous
class Move : OpMode() {

    private lateinit var frontLeft: DcMotorEx
    private lateinit var frontRight: DcMotorEx
    private lateinit var backLeft: DcMotorEx
    private lateinit var backRight: DcMotorEx

    private lateinit var sparkFun: SparkFunOTOS

    companion object {
        // P = 10, I = 3, D = 0, F = 8 (set as needed)
        var P = 10.toDouble()
        var I = 3.toDouble()
        var D = 0.toDouble()
        var F = 8.toDouble()
    }

    override fun init() {
        frontLeft  = hardwareMap.get(DcMotorEx::class.java, "frontLeft")
        frontRight = hardwareMap.get(DcMotorEx::class.java, "frontRight")
        backLeft   = hardwareMap.get(DcMotorEx::class.java, "backLeft")
        backRight  = hardwareMap.get(DcMotorEx::class.java, "backRight")
        sparkFun   = hardwareMap.get(SparkFunOTOS::class.java, "sensor_otos")

        // Use DcMotor.RunMode
        frontLeft.mode  = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        frontLeft.mode  = DcMotor.RunMode.RUN_USING_ENCODER
        frontRight.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        frontRight.mode = DcMotor.RunMode.RUN_USING_ENCODER
        backLeft.mode   = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        backLeft.mode   = DcMotor.RunMode.RUN_USING_ENCODER
        backRight.mode  = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        backRight.mode  = DcMotor.RunMode.RUN_USING_ENCODER

        // Use DcMotorSimple.Direction
        backRight.direction = DcMotorSimple.Direction.REVERSE
        backLeft.direction  = DcMotorSimple.Direction.REVERSE

        frontRight.setVelocityPIDFCoefficients(P, I, D, F)
        backRight.setVelocityPIDFCoefficients(P, I, D, F)
        frontLeft.setVelocityPIDFCoefficients(P, I, D, F)
        backLeft.setVelocityPIDFCoefficients(P, I, D, F)

        configureOtos()
    }

    override fun loop() {
        var frontLeftPosition = frontLeft.currentPosition
        var frontRightPosition = frontRight.currentPosition
        var backLeftPosition = backLeft.currentPosition
        var backRightPosition = backRight.currentPosition

        var pos: SparkFunOTOS.Pose2D = sparkFun.position

        if (gamepad1.triangle) sparkFun.resetTracking()
        if (gamepad1.square) sparkFun.calibrateImu()

        if (pos.x < 72.0) {
            val v = -0.2 * 2000.0
            frontLeft.velocity = v
            frontRight.velocity = v
            backLeft.velocity = v
            backRight.velocity = v
        } else if (pos.x > 72.0) {
            frontLeft.velocity = 0.0
            frontRight.velocity = 0.0
            backLeft.velocity = 0.0
            backRight.velocity = 0.0
        }

        telemetry.addLine("Press triangle on Gamepad to reset tracking")
        telemetry.addLine("Press square on Gamepad to calibrate the IMU")
        telemetry.addLine()
        telemetry.addData("X coordinate", pos.x)
        telemetry.addData("Y coordinate", pos.y)
        telemetry.addData("Heading angle", pos.h)
        telemetry.addData("frontLeft", frontLeftPosition)
        telemetry.addData("frontRight", frontRightPosition)
        telemetry.addData("backLeft", backLeftPosition)
        telemetry.addData("backRight", backRightPosition)
        telemetry.update()
    }

    @SuppressLint("DefaultLocale")
    private fun configureOtos() {
        telemetry.addLine("Configuring OTOS...")
        telemetry.update()

        sparkFun.setLinearUnit(DistanceUnit.INCH)
        sparkFun.setAngularUnit(AngleUnit.DEGREES)

        sparkFun.setLinearScalar(1.0)
        sparkFun.setAngularScalar(1.0)

        sparkFun.calibrateImu()
        sparkFun.resetTracking()

        var currentPosition = SparkFunOTOS.Pose2D(0.0, 0.0, 0.0)
        sparkFun.position = currentPosition

        val hwVersion = SparkFunOTOS.Version()
        val fwVersion = SparkFunOTOS.Version()
        sparkFun.getVersionInfo(hwVersion, fwVersion)

        telemetry.addLine("OTOS configured! Press start to get position data!")
        telemetry.addLine()
        telemetry.addLine("OTOS Hardware Version: v${hwVersion.major}.${hwVersion.minor}")
        telemetry.addLine("OTOS Firmware Version: v${fwVersion.major}.${fwVersion.minor}")
        telemetry.update()
    }

    override fun stop() {
        frontLeft.power = 0.0
        frontRight.power = 0.0
        backLeft.power = 0.0
        backRight.power = 0.0
    }
}
