package org.firstinspires.ftc.teamcode.OldCode;

import android.annotation.SuppressLint;




import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.threadopmode.TaskThread;
import org.firstinspires.ftc.teamcode.threadopmode.ThreadOpMode;

import java.util.concurrent.CountDownLatch;


@TeleOp
public class PIDFTune extends ThreadOpMode {

    private DcMotorEx frontLeft, frontRight, backLeft, backRight;
    private SparkFunOTOS SparkFun;

    // PIDF coefficients
    public static double P = 10, I = 3, D = 0, F = 8;

    // Latches for synchronization
    private final CountDownLatch startLatch = new CountDownLatch(1);
    private final CountDownLatch stopLatch = new CountDownLatch(1);

    // Flag to indicate running state
    private volatile boolean running = true;

    @Override
    public void mainInit() {
        frontLeft  = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft   = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight  = hardwareMap.get(DcMotorEx.class, "backRight");
        SparkFun   = hardwareMap.get(SparkFunOTOS.class, "sensor_otos");

        // Motor directions (set as needed for your drivetrain)
        frontRight.setDirection(DcMotorEx.Direction.REVERSE);
        backRight.setDirection(DcMotorEx.Direction.FORWARD);
        frontLeft.setDirection(DcMotorEx.Direction.REVERSE);
        backLeft.setDirection(DcMotorEx.Direction.FORWARD);

        ResetEncoders();

        // --- Register threads for each motor ---
        registerThread(new TaskThread(() -> runMotorThread(frontLeft)));
        registerThread(new TaskThread(() -> runMotorThread(frontRight)));
        registerThread(new TaskThread(() -> runMotorThread(backLeft)));
        registerThread(new TaskThread(() -> runMotorThread(backRight)));


        telemetry.clearAll();
        configureOtos();

        // --- Release all threads to start at the same time ---
        startLatch.countDown();
    }

    /**
     * Main thread logic for each motor.
     */
    private void runMotorThread(DcMotorEx motor) {
        try { startLatch.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        while (running) {
            SparkFunOTOS.Pose2D pos = SparkFun.getPosition();
            if (pos.x < 48) {
                motor.setVelocity(1000);
            } else {
                stopLatch.countDown();  // lets all threads stop their motors together
            }
            try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        // Wait for stop signal from main thread for perfect sync
        try { stopLatch.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        motor.setVelocity(0);
    }

    @Override
    public void mainLoop() {
        // Update PIDF coefficients every loop (optional)
        frontRight.setVelocityPIDFCoefficients(P, I, D, F);
        backRight.setVelocityPIDFCoefficients(P, I, D, F);
        frontLeft.setVelocityPIDFCoefficients(P, I, D, F);
        backLeft.setVelocityPIDFCoefficients(P, I, D, F);

        SparkFunOTOS.Pose2D pos = SparkFun.getPosition();

        // --- Custom condition to stop all motors at the same instant ---
        // You can replace this with any condition you want
        if (pos.x > 48) {
            running = false;        // tells threads to exit their loop
            stopLatch.countDown();  // lets all threads stop their motors together
        }

        // Usual telemetry (optional)
        telemetry.addData("Front Left Position", frontLeft.getCurrentPosition());
        telemetry.addData("Front Right Position", frontRight.getCurrentPosition());
        telemetry.addData("Back Left Position", backLeft.getCurrentPosition());
        telemetry.addData("Back Right Position", backRight.getCurrentPosition());
        telemetry.addData("X", pos.x);
        telemetry.addData("Y", pos.y);
        telemetry.addData("H", pos.h);
        telemetry.update();
    }

    private void ResetEncoders() {
        frontLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @SuppressLint("DefaultLocale")
    private void configureOtos() {
        // ... (same as your existing sensor configuration logic) ...
        SparkFun.setLinearUnit(DistanceUnit.INCH);
        SparkFun.setAngularUnit(AngleUnit.DEGREES);
        SparkFun.setLinearScalar(1.0);
        SparkFun.setAngularScalar(1.0);
        SparkFun.calibrateImu();
        SparkFun.resetTracking();
        SparkFunOTOS.Pose2D currentPosition = new SparkFunOTOS.Pose2D(0, 0, 0);
        SparkFun.setPosition(currentPosition);
        SparkFunOTOS.Version hwVersion = new SparkFunOTOS.Version();
        SparkFunOTOS.Version fwVersion = new SparkFunOTOS.Version();
        SparkFun.getVersionInfo(hwVersion, fwVersion);
    }
}
