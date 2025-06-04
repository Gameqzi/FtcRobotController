package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;

public class MotorUtils {

    /**
     * Strafe left using mecanum drive.
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     * @param frontLeft Front left motor
     * @param frontRight Front right motor
     * @param backLeft Back left motor
     * @param backRight Back right motor
     */
    public static void StrafeLeft(double power, DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
        frontLeft.setPower(-power);
        frontRight.setPower(-power);
        backLeft.setPower(power);
        backRight.setPower(power);
    }

    /**
     * Strafe right using mecanum drive.
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     * @param frontLeft Front left motor
     * @param frontRight Front right motor
     * @param backLeft Back left motor
     * @param backRight Back right motor
     */
    public static void StrafeRight(double power, DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(-power);
        backRight.setPower(-power);
    }

    /**
     * Rotate left using mecanum drive.
     * @param power - The power level to set for the motors, typically between -1.0 and 1.0.
     * @param frontLeft - Front left motor
     * @param frontRight - Front right motor
     * @param backLeft - Back left motor
     * @param backRight - Back right motor
     */
    public static void RotateLeft(double power, DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
        frontLeft.setPower(-power);
        frontRight.setPower(power);
        backLeft.setPower(-power);
        backRight.setPower(power);
    }

    /**
     * Rotate right using mecanum drive.
     * @param power - The power level to set for the motors, typically between -1.0 and 1.0.
     * @param frontLeft - Front left motor
     * @param frontRight - Front right motor
     * @param backLeft - Back left motor
     * @param backRight - Back right motor
     */
    public static void RotateRight(double power, DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
        frontLeft.setPower(power);
        frontRight.setPower(-power);
        backLeft.setPower(power);
        backRight.setPower(-power);
    }

    /**
     * Move forward using mecanum drive.
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     * @param frontLeft Front left motor
     * @param frontRight Front right motor
     * @param backLeft Back left motor
     * @param backRight Back right motor
     */
    public static void MoveForward(double power, DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);
    }

    /**
     * Move backward using mecanum drive.
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     * @param frontLeft Front left motor
     * @param frontRight Front right motor
     * @param backLeft Back left motor
     * @param backRight Back right motor
     */
    public static void MoveBackward(double power, DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
        frontLeft.setPower(-power);
        frontRight.setPower(-power);
        backLeft.setPower(-power);
        backRight.setPower(-power);
    }

    /**
     * Stop all motors.
     * @param frontLeft Front left motor
     * @param frontRight Front right motor
     * @param backLeft Back left motor
     * @param backRight Back right motor
     */
    public static void StopMotors(DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
}
