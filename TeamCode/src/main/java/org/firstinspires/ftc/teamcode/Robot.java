package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import java.util.Objects;

/**
 * Represents the robot chassis and provides methods to control its movement.
 * <p>
 * This class is implemented as a singleton to ensure that only one instance
 * of the robot hardware abstraction exists during the robot's operation.
 * This prevents conflicting commands and ensures consistent access to the
 * robot's motors and sensors throughout the codebase.
 * </p>
 * <p>
 * To obtain the instance, use {@link #getInstance(DcMotorEx, DcMotorEx, DcMotorEx, DcMotorEx)}
 * for initialization, and {@link #getInstance()} for subsequent access.
 * </p>
 */
public class Robot {
    //region Fields
    private final DcMotorEx frontLeftMotor;
    private final DcMotorEx frontRightMotor;
    private final DcMotorEx backLeftMotor;
    private final DcMotorEx backRightMotor;
    private SparkFunOTOS imu;
    //endregion

    //region Singleton implementation
    private static Robot instance = null;

    /**
     * Constructor for RobotChassis. This initializes the motors for the robot chassis.
     * @param frontLeft Front left motor
     * @param frontRight Front right motor
     * @param backLeft Back left motor
     * @param backRight Back right motor
     */
    private Robot(DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
        frontLeftMotor = frontLeft;
        frontRightMotor = frontRight;
        backLeftMotor = backLeft;
        backRightMotor = backRight;
    }

    /**
     * Returns the singleton instance of the Robot, initializing it if necessary.
     * <p>
     * This method is synchronized to ensure thread safety, preventing multiple threads
     * from creating separate instances or seeing a partially constructed object.
     * This is important if the robot hardware abstraction may be accessed from
     * multiple threads (such as different OpModes or background tasks).
     * </p>
     * <p>
     * Always use this method for the initial creation of the Robot instance.
     * For subsequent access, use {@link #getInstance()}.
     * </p>
     *
     * @param frontLeft  Front left motor
     * @param frontRight Front right motor
     * @param backLeft   Back left motor
     * @param backRight  Back right motor
     * @return The single, thread-safe instance of Robot.
     */
    public static synchronized Robot getInstance(DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight) {
        if (instance == null) {
            instance = new Robot(frontLeft, frontRight, backLeft, backRight);
        }
        return instance;
    }

    /**
     * Returns the singleton instance of Robot. Throws an exception if not initialized.
     * @return The single instance of Robot.
     */
    public static Robot getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Robot not initialized. Call getInstance with motor parameters first.");
        }
        return instance;
    }
    //endregion

    //region Getters
    public DcMotorEx getFrontLeftMotor() {
        return frontLeftMotor;
    }

    public DcMotorEx getFrontRightMotor() {
        return frontRightMotor;
    }

    public DcMotorEx getBackLeftMotor() {
        return backLeftMotor;
    }

    public DcMotorEx getBackRightMotor() {
        return backRightMotor;
    }

    public SparkFunOTOS getImu() {
        return imu;
    }
    //endregion

    //region Setters
    /**
     * Sets the IMU (Inertial Measurement Unit) for the robot.
     * <p>
     * This method uses the fluent interface pattern, allowing method chaining
     * by returning the current Robot instance.
     * </p>
     *
     * @param imu The SparkFunOTOS IMU to associate with this robot.
     * @return This Robot instance, for method chaining.
     */
    public Robot setImu(SparkFunOTOS imu) {
        this.imu = imu;
        return this;
    }
    //endregion

    //region BASIC RELATIVE ROBOT MOVEMENT COMMANDS
    /**
     * Strafe left using mecanum drive.
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     */
    public void strafeLeft(double power) {
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        frontLeftMotor.setPower(power);
        frontRightMotor.setPower(power);
        backLeftMotor.setPower(-power);
        backRightMotor.setPower(-power);
    }

    /**
     * Strafe right using mecanum drive.
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     */
    public void strafeRight(double power) {
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        frontLeftMotor.setPower(-power);
        frontRightMotor.setPower(-power);
        backLeftMotor.setPower(power);
        backRightMotor.setPower(power);
    }

    /**
     * Rotates the robot counterclockwise in place using mecanum drive.
     * Useful for turning the robot to a new heading.
     * @param power Motor power, between -1.0 and 1.0.
     */
    public void rotateLeft(double power) {
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        frontLeftMotor.setPower(power);
        frontRightMotor.setPower(power);
        backLeftMotor.setPower(power);
        backRightMotor.setPower(power);
    }

    /**
     * Rotates the robot clockwise in place using mecanum drive.
     * Useful for turning the robot to a new heading.
     * @param power Motor power, between -1.0 and 1.0.
     */
    public void rotateRight(double power) {
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        frontLeftMotor.setPower(-power);
        frontRightMotor.setPower(-power);
        backLeftMotor.setPower(-power);
        backRightMotor.setPower(-power);
    }

    /**
     * Drives the robot forward in a straight line using mecanum drive.
     * @param power Motor power, between -1.0 and 1.0.
     */
    public void driveForward(double power) {
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        frontLeftMotor.setPower(power);
        frontRightMotor.setPower(power);
        backLeftMotor.setPower(power);
        backRightMotor.setPower(power);
    }

    /**
     * Drives the robot backward in a straight line using mecanum drive.
     * @param power Motor power, between -1.0 and 1.0.
     */
    public void driveBackward(double power) {
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        frontLeftMotor.setPower(-power);
        frontRightMotor.setPower(-power);
        backLeftMotor.setPower(-power);
        backRightMotor.setPower(-power);
    }

    /**
     * Immediately stops all drive motors, halting the robot's movement.
     */
    public void stopMotors() {
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
    }
    //endregion

    //region ADVANCED RELATIVE ROBOT MOVEMENT COMMANDS
    /**
     * Strafe for a set distance on the robot's relative X axis. Negative = Left, Positive = Right. NOTE: CURRENTLY ONLY FOR SPARKFUNOTOS!
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     * @param targetDist The desired distance you want the robot to move. (Relative Distance)
     */
    public void strafeRelDist(double power, float targetDist) {
        if(imu == null) {
            throw new IllegalStateException("IMU not initialized. Set the IMU using setImu() before calling this method.");
        }

        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        final double THRESHOLD = 0.5; // Acceptable distance error in units
        SparkFunOTOS.Pose2D startPos = imu.getPosition();

        while (Math.abs(getRobotXDisplacement(startPos, imu.getPosition())) < Math.abs(targetDist) - THRESHOLD) {
            double direction = Math.signum(targetDist); // Basically gets the sign of the target distance
            frontLeftMotor.setPower(power * direction);
            frontRightMotor.setPower(power * direction);
            backLeftMotor.setPower(-power * direction);
            backRightMotor.setPower(-power * direction);
        }
        stopMotors();
    }

    /**
     * Drive for a set distance on the robot's relative Y axis. Negative = Backward, Positive Forward. NOTE: CURRENTLY ONLY FOR SPARKFUNOTOS!
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     * @param targetDist The desired distance you want the robot to move.
     */
    public void driveRelDist(double power, float targetDist) {
        if(imu == null) {
            throw new IllegalStateException("IMU not initialized. Set the IMU using setImu() before calling this method.");
        }

        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        final double THRESHOLD = 0.5; // Acceptable distance error in units
        SparkFunOTOS.Pose2D startPos = imu.getPosition();

        while (Math.abs(getRobotYDisplacement(startPos, imu.getPosition())) < Math.abs(targetDist) - THRESHOLD) {
            double direction = Math.signum(targetDist); // Basically gets the sign of the target distance
            frontLeftMotor.setPower(power * direction);
            frontRightMotor.setPower(power * direction);
            backLeftMotor.setPower(power * direction);
            backRightMotor.setPower(power * direction);
        }
        stopMotors();
    }
    //endregion

    //region ADVANCED GLOBAL ROBOT MOVEMENT COMMANDS:
    /**
     * Rotate to a set global angle. NOTE: CURRENTLY ONLY FOR SPARKFUNOTOS!
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     * @param targetAngle The desired angle you want the robot to rotate to. (Global Angle)
     */
    public void rotateTo(double power, float targetAngle) {
        if (imu == null) {
            throw new IllegalStateException("IMU not initialized. Set the IMU using setImu() before calling this method.");
        }
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        final double ANGLE_THRESHOLD = 2.0; // Acceptable error in degrees

        // Calculate initial angle error
        double angleError = normalizeAngle(targetAngle - imu.getPosition().h);

        // Continue rotating while the error is outside the threshold
        while (Math.abs(angleError) > ANGLE_THRESHOLD) {
            double direction = Math.signum(angleError); // +1 for CCW, -1 for CW
            // Set all motors to rotate in the same direction
            frontLeftMotor.setPower(power * direction);
            frontRightMotor.setPower(power * direction);
            backLeftMotor.setPower(power * direction);
            backRightMotor.setPower(power * direction);

            // Update angle error
            angleError = normalizeAngle(targetAngle - imu.getPosition().h);
        }
        stopMotors();
    }

    /**
     * Move to a set global X & Y coordinate, as well as a set heading angle. NOTE: CURRENTLY ONLY FOR SPARKFUNOTOS!
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     * @param TX The desired X coordinate in the field that you want the robot to move to. (NOTE: SET TO "~" FOR 'NO CHANGE')
     * @param TY The desired Y coordinate in the field that you want the robot to move to. (NOTE: SET TO "~" FOR 'NO CHANGE')
     * @param H The desired heading angle in the field that you want the robot to move to. (NOTE: SET TO "~" FOR 'NO CHANGE')
     */
    public void goTo(double power, String TX, String TY, String H) {
        if (imu == null) {
            throw new IllegalStateException("IMU not initialized. Set the IMU using setImu() before calling this method.");
        }
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        final double DIST_THRESHOLD = 0.5;    // Acceptable position error
        final double ANGLE_THRESHOLD = 2.0;   // Acceptable heading error
        final double ROT_SPEED = 0.01;        // Proportional gain for rotation

        // Use Objects.equals for null-safe comparison (will not compare memory addresses, but rather, values)
        boolean moveX = !Objects.equals(TX, "~"); // true if TX is not "~"
        boolean moveY = !Objects.equals(TY, "~"); // true if TY is not "~"
        boolean rotate = !Objects.equals(H, "~"); // true if H is not "~"

        double targetX = moveX ? Double.parseDouble(TX) : 0;
        double targetY = moveY ? Double.parseDouble(TY) : 0;
        double targetHeading = rotate ? Double.parseDouble(H) : 0;

        // Calculate initial errors
        SparkFunOTOS.Pose2D pos = imu.getPosition();
        double dx = targetX - pos.x;
        double dy = targetY - pos.y;
        double distanceRemaining = Math.hypot(moveX ? dx : 0, moveY ? dy : 0);

        double headingError = rotate ? normalizeAngle(targetHeading - pos.h) : 0;

        // Continue moving while either position or heading is not reached
        while (((moveX || moveY) && distanceRemaining > DIST_THRESHOLD) ||
                (rotate && Math.abs(headingError) > ANGLE_THRESHOLD)) {

            // Recalculate errors each loop
            pos = imu.getPosition();
            dx = targetX - pos.x;
            dy = targetY - pos.y;
            distanceRemaining = Math.hypot(moveX ? dx : 0, moveY ? dy : 0);

            headingError = rotate ? normalizeAngle(targetHeading - pos.h) : 0;

            // Calculate movement direction and power
            double moveAngle = Math.atan2(dy, dx); // Direction to target
            double movePower = (distanceRemaining > DIST_THRESHOLD) ? Math.min(power, distanceRemaining * 0.1 + 0.2) : 0;

            // Calculate X and Y speed components in field coordinates
            double xSpeed = moveX ? Math.cos(moveAngle) * movePower : 0;
            double ySpeed = moveY ? Math.sin(moveAngle) * movePower : 0;
            // Calculate turn power proportional to heading error
            double turnPower = rotate ? headingError * ROT_SPEED : 0;

            // Convert field-centric speeds to robot-centric speeds
            double HR = Math.toRadians(pos.h);
            double XSpeed = xSpeed * Math.cos(-HR) - ySpeed * Math.sin(-HR);
            double YSpeed = xSpeed * Math.sin(-HR) + ySpeed * Math.cos(-HR);

            // Calculate individual motor powers for mecanum drive
            double frontLeftPower = YSpeed + XSpeed + turnPower;
            double frontRightPower = YSpeed - XSpeed - turnPower;
            double backLeftPower = YSpeed - XSpeed + turnPower;
            double backRightPower = YSpeed + XSpeed - turnPower;

            // Normalize powers if any exceed 1.0
            double maxPower = Math.max(Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                    Math.max(Math.abs(backLeftPower), Math.abs(backRightPower)));
            if (maxPower > 1.0) {
                frontLeftPower /= maxPower;
                frontRightPower /= maxPower;
                backLeftPower /= maxPower;
                backRightPower /= maxPower;
            }

            // Set motor powers
            frontLeftMotor.setPower(frontLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backLeftMotor.setPower(backLeftPower);
            backRightMotor.setPower(backRightPower);
        }
        stopMotors();
    }
    //endregion

    //region Helper Methods
    /** @noinspection BooleanMethodIsAlwaysInverted*/ // Remove the unnecessary warning
    private boolean isValidPower(double power) {
        return power >= -1.0 && power <= 1.0;
    }

    /**
     * Calculates the robot's displacement along its relative X axis (strafe direction)
     * between two positions, accounting for heading.
     *
     * @param start   The starting position (Pose2D).
     * @param current The current position (Pose2D).
     * @return The relative X displacement in the robot's frame of reference.
     */
    private double getRobotXDisplacement(SparkFunOTOS.Pose2D start, SparkFunOTOS.Pose2D current) {
        double dx = current.x - start.x;
        double dy = current.y - start.y;
        double hR = Math.toRadians(current.h);
        return dx * Math.cos(-hR) - dy * Math.sin(-hR);
    }

    /**
     * Calculates the robot's displacement along its relative Y axis (forward/backward direction)
     * between two positions, accounting for heading.
     *
     * @param start   The starting position (Pose2D).
     * @param current The current position (Pose2D).
     * @return The relative Y displacement in the robot's frame of reference.
     */
    private double getRobotYDisplacement(SparkFunOTOS.Pose2D start, SparkFunOTOS.Pose2D current) {
        double dx = current.x - start.x;
        double dy = current.y - start.y;
        double hR = Math.toRadians(current.h);
        return dx * Math.sin(-hR) + dy * Math.cos(-hR);
    }

    /**
     * Normalizes an angle to the range [-180, 180) degrees.
     * <p>
     * This ensures that any input angle, regardless of its magnitude or sign,
     * is converted to an equivalent angle within the standard range for heading
     * calculations. This is important for consistent angle comparisons and
     * control logic, as angles outside this range can cause discontinuities
     * or incorrect behavior in robot movement and rotation algorithms.
     * </p>
     *
     * @param angle The angle in degrees to normalize.
     * @return The normalized angle in the range [-180, 180).
     */
    private double normalizeAngle(double angle) {
        // O(n) time complexity. Add this back in if my angle normalization is not working. -NP
        /* while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360; */

        // O(1) time complexity. This should be a more efficient way to normalize angles.
        angle = ((angle + 180) % 360 + 360) % 360 - 180;
        return angle;
    }
    //endregion
}
