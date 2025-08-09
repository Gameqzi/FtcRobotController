package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

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
    private Telemetry telemetry;
    //endregion

    //region Singleton implementation
    private static Robot instance = null;

    /**
     * Constructor for RobotChassis. This initializes the motors for the robot chassis.
     *
     * @param frontLeft  Front left motor
     * @param frontRight Front right motor
     * @param backLeft   Back left motor
     * @param backRight  Back right motor
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
     *
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

    /**
     * Sets the telemetry object for the robot.
     * <p>
     * This allows the robot to send data and status updates to the driver station
     * or other monitoring interfaces during operation. The method uses the fluent
     * interface pattern, enabling method chaining by returning the current Robot instance.
     * </p>
     *
     * @param telemetry The Telemetry object to associate with this robot.
     * @return This Robot instance, for method chaining.
     */
    public Robot setTelemetry(Telemetry telemetry) {
        this.telemetry = telemetry;
        return this;
    }
    //endregion


    //region BASIC RELATIVE ROBOT MOVEMENT COMMANDS

    /**
     * Strafe left using mecanum drive.
     *
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
     *
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
     * Rotates the robot in place using mecanum drive.
     * <p>
     * If the power is positive, the robot rotates clockwise (right).
     * If the power is negative, the robot rotates counterclockwise (left).
     * </p>
     *
     * @param power Motor power, between -1.0 and 1.0. Positive for right, negative for left.
     * @throws IllegalArgumentException if the power is outside the range \[-1.0, 1.0\].
     */
    public void rotate(double power) {
        if (power > 0) {
            rotateRight(power);
        } else {
            rotateLeft(power);
        }
    }

    /**
     * Rotates the robot counterclockwise in place using mecanum drive.
     * Useful for turning the robot to a new heading.
     *
     * @param power Motor power, between -1.0 and 1.0.
     */
    public void rotateLeft(double power) {
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        final double RLfl = power;
        final double RLfr =  power;
        final double RLbl = power;
        final double RLbr =  power;

        CountDownLatch RLstartSignal = new CountDownLatch(1);

        Thread RLflThread = new Thread(() -> {
            try {
                RLstartSignal.await();
                frontLeftMotor.setPower(RLfl);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "rotateLeft-FL-Thread");

        Thread RLfrThread = new Thread(() -> {
            try {
                RLstartSignal.await();
                frontRightMotor.setPower(RLfr);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "rotateLeft-FR-Thread");

        Thread RLblThread = new Thread(() -> {
            try {
                RLstartSignal.await();
                backLeftMotor.setPower(RLbl);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "RotateLeft-BL-Thread");

        Thread RLbrThread = new Thread(() -> {
            try {
                RLstartSignal.await();
                backRightMotor.setPower(RLbr);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "RotateLeft-BR-Thread");

        // start all four threads
        RLflThread.start();
        RLfrThread.start();
        RLblThread.start();
        RLbrThread.start();

        // “green light” — they all fire almost simultaneously
        RLstartSignal.countDown();

        // wait for each to apply its power before continuing
        try {
            RLflThread.join();
            RLfrThread.join();
            RLblThread.join();
            RLbrThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Rotates the robot clockwise in place using mecanum drive.
     * Useful for turning the robot to a new heading.
     *
     * @param power Motor power, between -1.0 and 1.0.
     */
    public void rotateRight(double power) {
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        final double RRfl = -power;
        final double RRfr =  -power;
        final double RRbl = -power;
        final double RRbr =  -power;

        CountDownLatch RRstartSignal = new CountDownLatch(1);

        Thread RRflThread = new Thread(() -> {
            try {
                RRstartSignal.await();
                frontLeftMotor.setPower(RRfl);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "rotateRight-FL-Thread");

        Thread RRfrThread = new Thread(() -> {
            try {
                RRstartSignal.await();
                frontRightMotor.setPower(RRfr);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "rotateRight-FR-Thread");

        Thread RRblThread = new Thread(() -> {
            try {
                RRstartSignal.await();
                backLeftMotor.setPower(RRbl);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "RotateRight-BL-Thread");

        Thread RRbrThread = new Thread(() -> {
            try {
                RRstartSignal.await();
                backRightMotor.setPower(RRbr);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "RotateRight-BR-Thread");

        // start all four threads
        RRflThread.start();
        RRfrThread.start();
        RRblThread.start();
        RRbrThread.start();

        // “green light” — they all fire almost simultaneously
        RRstartSignal.countDown();

        // wait for each to apply its power before continuing
        try {
            RRflThread.join();
            RRfrThread.join();
            RRblThread.join();
            RRbrThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Drives the robot forward in a straight line using mecanum drive.
     *
     * @param power Motor power, between -1.0 and 1.0.
     */
    public void driveForward(double power) {
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        frontLeftMotor.setPower(-power);
        frontRightMotor.setPower(power);
        backLeftMotor.setPower(-power);
        backRightMotor.setPower(power);
    }

    /**
     * Drives the robot backward in a straight line using mecanum drive.
     *
     * @param power Motor power, between -1.0 and 1.0.
     */
    public void driveBackward(double power) {
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        frontLeftMotor.setPower(power);
        frontRightMotor.setPower(-power);
        backLeftMotor.setPower(power);
        backRightMotor.setPower(-power);
    }

    /**
     * Immediately stops all drive motors, halting the robot's movement.
     */
    public void stopMotors() {
        // --- MULTI-THREADED LATCH START ---
        final double SMfl = 0;
        final double SMfr =  0;
        final double SMbl = 0;
        final double SMbr =  0;

        CountDownLatch SMstartSignal = new CountDownLatch(1);

        Thread SMflThread = new Thread(() -> {
            try {
                SMstartSignal.await();
                frontLeftMotor.setPower(SMfl);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "stopMotors-FL-Thread");

        Thread SMfrThread = new Thread(() -> {
            try {
                SMstartSignal.await();
                frontRightMotor.setPower(SMfr);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "stopMotors-FR-Thread");

        Thread SMblThread = new Thread(() -> {
            try {
                SMstartSignal.await();
                backLeftMotor.setPower(SMbl);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "stopMotors-BL-Thread");

        Thread SMbrThread = new Thread(() -> {
            try {
                SMstartSignal.await();
                backRightMotor.setPower(SMbr);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "stopMotors-BR-Thread");

        // start all four threads
        SMflThread.start();
        SMfrThread.start();
        SMblThread.start();
        SMbrThread.start();

        // “green light” — they all fire almost simultaneously
        SMstartSignal.countDown();

        // wait for each to apply its power before continuing
        try {
            SMflThread.join();
            SMfrThread.join();
            SMblThread.join();
            SMbrThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // --- MULTI-THREADED LATCH END ---
    }
    //endregion

    //region ADVANCED RELATIVE ROBOT MOVEMENT COMMANDS

    /**
     * Strafe for a set distance on the robot's relative X axis. Negative = Left, Positive = Right. NOTE: CURRENTLY ONLY FOR SPARKFUNOTOS!
     *
     * @param power      The power level to set for the motors, typically between -1.0 and 1.0.
     * @param targetDist The desired distance you want the robot to move. (Relative Distance)
     */
    public void strafeRelDist(double power, double targetDist) {
        if (imu == null) {
            throw new IllegalStateException("IMU not initialized. Set the IMU using setImu() before calling this method.");
        }

        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        final double THRESHOLD = 0.5; // Acceptable distance error in units
        final double Kp = 0.02; // Proportional gain for heading correction, tune this value
        SparkFunOTOS.Pose2D startPos = imu.getPosition();

        while (Math.abs(getRobotXDisplacement(startPos, imu.getPosition())) < Math.abs(targetDist) - THRESHOLD) {
            double strafe = Math.signum(targetDist); // Basically gets the sign of the target distance

            double headingError = startPos.h - imu.getPosition().h;
            double headingCorrection = Kp * Utils.normalizeAngle(headingError);

            // --- MULTI-THREADED LATCH START ---
            final double SRDfl = (power * -strafe) + headingCorrection;
            final double SRDfr =  (power * -strafe) + headingCorrection;
            final double SRDbl = (power * strafe) + headingCorrection;
            final double SRDbr =  (power * strafe) + headingCorrection;

            CountDownLatch SRDstartSignal = new CountDownLatch(1);

            Thread SRDflThread = new Thread(() -> {
                try {
                    SRDstartSignal.await();
                    frontLeftMotor.setPower(SRDfl);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "strafeRelDist-FL-Thread");

            Thread SRDfrThread = new Thread(() -> {
                try {
                    SRDstartSignal.await();
                    frontRightMotor.setPower(SRDfr);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "strafeRelDist-FR-Thread");

            Thread SRDblThread = new Thread(() -> {
                try {
                    SRDstartSignal.await();
                    backLeftMotor.setPower(SRDbl);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "strafeRelDist-BL-Thread");

            Thread SRDbrThread = new Thread(() -> {
                try {
                    SRDstartSignal.await();
                    backRightMotor.setPower(SRDbr);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "strafeRelDist-BR-Thread");

            // start all four threads
            SRDflThread.start();
            SRDfrThread.start();
            SRDblThread.start();
            SRDbrThread.start();

            // “green light” — they all fire almost simultaneously
            SRDstartSignal.countDown();

            // wait for each to apply its power before continuing
            try {
                SRDflThread.join();
                SRDfrThread.join();
                SRDblThread.join();
                SRDbrThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // --- MULTI-THREADED LATCH END ---

            // TODO: !IMPORTANT! - Needs some sort of cooldown. Apparently Thread.sleep(10); will not work. Thread.yield();?
        }
        stopMotors();
    }

    /**
     * Drive for a set distance on the robot's relative Y axis. Negative = Backward, Positive = Forward. NOTE: CURRENTLY ONLY FOR SPARKFUNOTOS!
     *
     * @param power      The power level to set for the motors, typically between -1.0 and 1.0.
     * @param targetDist The desired distance you want the robot to move.
     */
    public void driveRelDist(double power, double targetDist) { // FIXME add threading latch
        if (imu == null) {
            throw new IllegalStateException("IMU not initialized. Set the IMU using setImu() before calling this method.");
        }

        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        final double THRESHOLD = 0.5; // Acceptable distance error in units
        final double Kp = 0.02; // Proportional gain for heading correction, tune this value
        SparkFunOTOS.Pose2D startPos = imu.getPosition();

        while (Math.abs(getRobotYDisplacement(startPos, imu.getPosition())) < Math.abs(targetDist) - THRESHOLD) {
            double drive = Math.signum(targetDist); // Basically gets the sign of the target distance

            double headingError = startPos.h - imu.getPosition().h;
            double headingCorrection = Kp * Utils.normalizeAngle(headingError);

            frontLeftMotor.setPower((power * -drive) + headingCorrection);
            frontRightMotor.setPower((power * drive) + headingCorrection);
            backLeftMotor.setPower((power * -drive) + headingCorrection);
            backRightMotor.setPower((power * drive) + headingCorrection);
        }
        stopMotors();
    }
    //endregion

    //region ADVANCED GLOBAL ROBOT MOVEMENT COMMANDS:

    /**
     * Rotate to a set global angle. Negative = CCW, Positive = CW. NOTE: CURRENTLY ONLY FOR SPARKFUNOTOS!
     *
     * @param maxPower The power level to set for the motors, typically between -1.0 and 1.0.
     * @param minPower The power level to set for the motors, typically between -1.0 and 1.0.
     * @param TH    The desired angle you want the robot to rotate to. (Global Angle)
     */
    public void rotateTo(double maxPower, double minPower, double TH) {
        if (imu == null) {
            throw new IllegalStateException("IMU not initialized. Set the IMU using setImu() before calling this method.");
        }
        /*if (!isValidPower(maxPower) || !isValidPower(minPower)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }*/

        final double ANGLE_THRESHOLD = 1; // Acceptable error in degrees

        // Calculate initial angle error
        double angleError = Utils.normalizeAngle(-TH - imu.getPosition().h);

        // Continue rotating while the error is outside the threshold
        while (Math.abs(angleError) > ANGLE_THRESHOLD) {
            angleError = Utils.normalizeAngle(-TH - imu.getPosition().h);
            double power = Math.copySign(Math.max(minPower, Math.min(angleError, maxPower)), angleError);
            // Set all motors to rotate in the same direction
            // --- MULTI-THREADED LATCH START ---
            final double RTfl = power;
            final double RTfr =  power;
            final double RTbl = power;
            final double RTbr =  power;

            CountDownLatch RTstartSignal = new CountDownLatch(1);

            Thread RTflThread = new Thread(() -> {
                try {
                    RTstartSignal.await();
                    frontLeftMotor.setVelocity(RTfl);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "rotateTo-FL-Thread");

            Thread RTfrThread = new Thread(() -> {
                try {
                    RTstartSignal.await();
                    frontRightMotor.setVelocity(RTfr);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "rotateTo-FR-Thread");

            Thread RTblThread = new Thread(() -> {
                try {
                    RTstartSignal.await();
                    backLeftMotor.setVelocity(RTbl);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "rotateTo-BL-Thread");

            Thread RTbrThread = new Thread(() -> {
                try {
                    RTstartSignal.await();
                    backRightMotor.setVelocity(RTbr);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "rotateTo-BR-Thread");

            // start all four threads
            RTflThread.start();
            RTfrThread.start();
            RTblThread.start();
            RTbrThread.start();

            // “green light” — they all fire almost simultaneously
            RTstartSignal.countDown();

            // wait for each to apply its power before continuing
            try {
                RTflThread.join();
                RTfrThread.join();
                RTblThread.join();
                RTbrThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // --- MULTI-THREADED LATCH END ---

            // Update angle error
            angleError = Utils.normalizeAngle(-TH - imu.getPosition().h);
        }
        stopMotors();
    }

    /**
     * Move to a set global X & Y coordinate, as well as a set heading angle. NOTE: CURRENTLY ONLY FOR SPARKFUNOTOS!
     *
     * @param power The power level to set for the motors, typically between -1.0 and 1.0.
     * @param FX    The desired X coordinate in the field that you want the robot to move to. (NOTE: SET TO "~" FOR 'NO CHANGE')
     * @param FY    The desired Y coordinate in the field that you want the robot to move to. (NOTE: SET TO "~" FOR 'NO CHANGE')
     * @param FH    The desired heading angle in the field that you want the robot to move to. (NOTE: SET TO "~" FOR 'NO CHANGE')
     */
    public void goTo(double power, String FX, String FY, String FH) {
        if (imu == null) {
            throw new IllegalStateException("IMU not initialized. Set the IMU using setImu() before calling this method.");
        }
        if (!isValidPower(power)) {
            throw new IllegalArgumentException("Power must be between -1.0 and 1.0");
        }

        final double distThreshold = 0.5;       // Acceptable position error
        final double angleThreshold = 2.0;      // Acceptable heading error

        final double minSpeed = 0.15;           // Minimum speed the robot can drive at
        final double maxSpeed = 0.50;           // Maximum speed the robot can drive at

        final double maxRotSpeed = 0.5;
        final double minRotSpeed = 0.1;           // Proportional gain for rotation

        SparkFunOTOS.Pose2D startPos = imu.getPosition();

        double TX, TY, TH;
        if (Objects.equals(FX, "~")) {
            TX = startPos.x;
        } else {
            TX = Double.parseDouble(FX);
        }
        if (Objects.equals(FY, "~")) {
            TY = startPos.y;
        } else {
            TY = Double.parseDouble(FY);
        }
        if (Objects.equals(FH, "~")) {
            TH = startPos.h;
        } else {
            TH = Double.parseDouble(FH);
        }

        // ToDo NOTICE: This while loop is still here because I want to later change this function.
        //while (Math.hypot(imu.getPosition().x - TX, imu.getPosition().y - TY) > distThreshold || Math.abs(Utils.normalizeAngle(TH - imu.getPosition().h)) > angleThreshold) {

        while (Math.hypot(imu.getPosition().x - TX, imu.getPosition().y - TY) > distThreshold) {
            SparkFunOTOS.Pose2D currentPos = imu.getPosition();

            double dx = TX - currentPos.x;
            double dy = TY - currentPos.y;

            double toTargetAngle = Math.atan2(dy, dx);

            double toTargetDist = Math.hypot(dx, dy);
            double motorPower = Math.max(minSpeed, Math.min(power, toTargetDist * maxSpeed));

            double xSpeed = Math.cos(toTargetAngle) * motorPower;
            double ySpeed = Math.sin(toTargetAngle) * motorPower;

            double HR = Math.toRadians(currentPos.h);
            double xPower = xSpeed * Math.cos(-HR) - ySpeed * Math.sin(-HR);
            double yPower = xSpeed * Math.sin(-HR) + ySpeed * Math.cos(-HR);

            double frontLeftPower = yPower + xPower;
            double frontRightPower = yPower - xPower;
            double backLeftPower = yPower - xPower;
            double backRightPower = yPower + xPower;

            double maxPower = Math.max(Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)), Math.max(Math.abs(backLeftPower), Math.abs(backRightPower)));
            if (maxPower > 1.0) {
                frontLeftPower /= maxPower;
                frontRightPower /= maxPower;
                backLeftPower /= maxPower;
                backRightPower /= maxPower;
            }
            // --- MULTI-THREADED LATCH START ---
            final double GTfl = -frontLeftPower;
            final double GTfr =  frontRightPower;
            final double GTbl = -backLeftPower;
            final double GTbr =  backRightPower;

            CountDownLatch GTstartSignal = new CountDownLatch(1);

            Thread GTflThread = new Thread(() -> {
                try {
                    GTstartSignal.await();
                    frontLeftMotor.setPower(GTfl);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "GoTo-FL-Thread");

            Thread GTfrThread = new Thread(() -> {
                try {
                    GTstartSignal.await();
                    frontRightMotor.setPower(GTfr);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "GoTo-FR-Thread");

            Thread GTblThread = new Thread(() -> {
                try {
                    GTstartSignal.await();
                    backLeftMotor.setPower(GTbl);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "GoTo-BL-Thread");

            Thread GTbrThread = new Thread(() -> {
                try {
                    GTstartSignal.await();
                    backRightMotor.setPower(GTbr);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "GoTo-BR-Thread");

            // start all four threads
            GTflThread.start();
            GTfrThread.start();
            GTblThread.start();
            GTbrThread.start();

            // “green light” — they all fire almost simultaneously
            GTstartSignal.countDown();

            // wait for each to apply its power before continuing
            try {
                GTflThread.join();
                GTfrThread.join();
                GTblThread.join();
                GTbrThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // --- MULTI-THREADED LATCH END ---
        }

        stopMotors();

        rotateTo(maxRotSpeed, minRotSpeed, TH);
        //}
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
    //endregion
}
