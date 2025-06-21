package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Deprecated
public class MotorUtils {



    // ADVANCED GLOBAL ROBOT  MOVEMENT COMMANDS:

    /**
     * Rotate to a set global angle. NOTE: CURRENTLY ONLY FOR SPARKFUNOTOS!
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

    public static void GoTo(double power, String TX, String TY, String H, SparkFunOTOS imu, DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
        throw new IllegalArgumentException("This Function Is No Longer Valid!");
    }


    // ADVANCED RELATIVE ROBOT MOVEMENT COMMANDS:

    /**
     * Strafe for a set distance on the robot's relative X axis. Negative = Left, Positive = Right. NOTE: CURRENTLY ONLY FOR SPARKFUNOTOS!
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     * @param targetDist The desired distance you want the robot to move. (Relative Distance)
     * @param imu The name of the used IMU, so it can be called inside the function.
     * @param frontLeft Front left motor
     * @param frontRight Front right motor
     * @param backLeft Back left motor
     * @param backRight Back right motor
     */
    public static void StrafeRelDist(double power, float targetDist, SparkFunOTOS imu, DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        double robotXDisplacement = 0;
        SparkFunOTOS.Pose2D  startPos = imu.getPosition();

        while (Math.abs(robotXDisplacement) == targetDist) {
            SparkFunOTOS.Pose2D pos = imu.getPosition();

            double dx = pos.x - startPos.x;
            double dy = pos.y - startPos.y;
            double hR = Math.toRadians(pos.h);

            robotXDisplacement = dx * Math.cos(-hR) - dy * Math.sin(-hR);

            if (robotXDisplacement < targetDist) {
                frontLeft.setPower(power);
                frontRight.setPower(power);
                backLeft.setPower(-power);
                backRight.setPower(-power);
            }
            if (robotXDisplacement > targetDist) {
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
     * Drive for a set distance on the robot's relative Y axis. Negative = Backward, Positive Forward. NOTE: CURRENTLY ONLY FOR SPARKFUNOTOS!
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     * @param targetDist The desired distance you want the robot to move.
     * @param imu The name of the used IMU, so it can be called inside the function.
     * @param frontLeft Front left motor
     * @param frontRight Front right motor
     * @param backLeft Back left motor
     * @param backRight Back right motor
     */
    public static void DriveRelDist(double power, float targetDist, SparkFunOTOS imu, DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        double robotYDisplacement = 0;
        SparkFunOTOS.Pose2D  startPos = imu.getPosition();

        while (Math.abs(robotYDisplacement) == targetDist) {
            SparkFunOTOS.Pose2D pos = imu.getPosition();

            double dx = pos.x - startPos.x;
            double dy = pos.y - startPos.y;
            double hR = Math.toRadians(pos.h);

            robotYDisplacement = dx * Math.sin(-hR) + dy * Math.cos(-hR);

            if (robotYDisplacement < targetDist) {
                frontLeft.setPower(-power);
                frontRight.setPower(-power);
                backLeft.setPower(-power);
                backRight.setPower(-power);
            }
            if (robotYDisplacement > targetDist) {
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



    // BASIC RELATIVE ROBOT MOVEMENT COMMANDS:

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
     * Drive forward using mecanum drive.
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     * @param frontLeft Front left motor
     * @param frontRight Front right motor
     * @param backLeft Back left motor
     * @param backRight Back right motor
     */
    public static void DriveForward(double power, DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);
    }


    /**
     * Drive backward using mecanum drive.
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     * @param frontLeft Front left motor
     * @param frontRight Front right motor
     * @param backLeft Back left motor
     * @param backRight Back right motor
     */
    public static void DriveBackward(double power, DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
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
