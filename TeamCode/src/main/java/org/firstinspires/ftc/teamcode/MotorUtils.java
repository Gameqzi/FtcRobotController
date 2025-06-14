package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class MotorUtils {

    /**
     * Rotate to a set position using mecanum drive. NOTE: CURRENTLY ONLY FOR SPARKFUNOTOS!
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     * @param targetAngle The desired angle you want the robot to rotate to. (Global Angle)
     * @param imu The name of the used IMU, so it can be called inside the function.
     * @param frontLeft Front left motor
     * @param frontRight Front right motor
     * @param backLeft Back left motor
     * @param backRight Back right motor
     */
    public static void RotateTo(double power, float targetAngle, SparkFunOTOS imu, DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }
        SparkFunOTOS.Pose2D pos = imu.getPosition();

        while (((float) pos.h) != targetAngle) {
            pos = imu.getPosition();

            if (((float) pos.h) < targetAngle) {
                frontLeft.setPower(-power);
                frontRight.setPower(-power);
                backLeft.setPower(-power);
                backRight.setPower(-power);
            }
            if (((float) pos.h) > targetAngle) {
                frontLeft.setPower(power);
                frontRight.setPower(power);
                backLeft.setPower(power);
                backRight.setPower(power);
            }
        }
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    /**
     * Strafe for a set distance using mecanum drive. Negative values are left, positive values are right. NOTE: CURRENTLY ONLY FOR SPARKFUNOTOS!
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     * @param targetDis The desired distance you want the robot to move. (Relative Distance)
     * @param imu The name of the used IMU, so it can be called inside the function.
     * @param frontLeft Front left motor
     * @param frontRight Front right motor
     * @param backLeft Back left motor
     * @param backRight Back right motor
     */
    public static void StrafeDis(double power, float targetDis, SparkFunOTOS imu, DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }
        SparkFunOTOS.Pose2D pos = imu.getPosition();
        float startPos = (float) pos.x;

        while ((startPos - ((float) pos.x)) != 0) {
            pos = imu.getPosition();

            if ((startPos - ((float) pos.x)) < 0) {
                frontLeft.setPower(power);
                frontRight.setPower(power);
                backLeft.setPower(-power);
                backRight.setPower(-power);
            }
            if ((startPos - ((float) pos.x)) > 0) {
                frontLeft.setPower(-power);
                frontRight.setPower(-power);
                backLeft.setPower(power);
                backRight.setPower(power);
            }
        }
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    /**
     * Drive for a set distance using mecanum drive. Negative values are backward, positive values are forward. NOTE: CURRENTLY ONLY FOR SPARKFUNOTOS!
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     * @param targetDis The desired distance you want the robot to move. (Relative Distance)
     * @param imu The name of the used IMU, so it can be called inside the function.
     * @param frontLeft Front left motor
     * @param frontRight Front right motor
     * @param backLeft Back left motor
     * @param backRight Back right motor
     */
    public static void MoveDis(double power, float targetDis, SparkFunOTOS imu, DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }
        SparkFunOTOS.Pose2D pos = imu.getPosition();
        float startPos = (float) pos.y;

        while ((startPos - ((float) pos.y)) != 0) {
            pos = imu.getPosition();

            if ((startPos - ((float) pos.y)) < 0) {
                frontLeft.setPower(-power);
                frontRight.setPower(-power);
                backLeft.setPower(-power);
                backRight.setPower(-power);
            }
            if ((startPos - ((float) pos.y)) > 0) {
                frontLeft.setPower(power);
                frontRight.setPower(power);
                backLeft.setPower(power);
                backRight.setPower(power);
            }
        }
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    /**
     * Strafe left using mecanum drive.
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     * @param frontLeft Front left motor
     * @param frontRight Front right motor
     * @param backLeft Back left motor
     * @param backRight Back right motor
     */
    public static void StrafeLeft(double power, DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(-power);
        backRight.setPower(-power);
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
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        frontLeft.setPower(-power);
        frontRight.setPower(-power);
        backLeft.setPower(power);
        backRight.setPower(power);
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
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
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
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        frontLeft.setPower(-power);
        frontRight.setPower(-power);
        backLeft.setPower(-power);
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
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

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
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

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

    private static boolean isValidPower(double power) {
        return power >= -1.0 && power <= 1.0;
    }
}
