package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.Utils.sleep;

import android.annotation.SuppressLint;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.threadopmode.ThreadOpMode;

import java.util.ArrayDeque;
import java.util.concurrent.ThreadLocalRandom;

@Config
@TeleOp
public class StationaryShowcase extends ThreadOpMode {

    //region GLOBAL VARIABLES

    // Hardware Variables:
    private Robot robot;
    private ColorSensor colorSensor;
    private CRServo IntakeServo1, IntakeServo2; private Servo CamServoPan, CamServoTilt;
    DcMotorEx Lift;

    // Global Variables:

    // Tunables:
    public static final double LP = 10, LI = 3, LD = 0, LF = 8; // Lift PIDF Values | ToDo: Maybe Tune These?
    public static final double panMin = 0.370, panMax = 0.630, tiltMin = 0.350, tiltMax = 0.505; // Camera Servo Limits | ToDo: Maybe Tune These?
    public static final int colorThresholdDefault = 20; // ToDo: Tune This!
    public static final int alphaThresholdDefault = 40; // ToDo: Tune This!
    public static final boolean robotCanMove = false; // ToDo: Can the robot move on the table?

    // Telemetry:
    private static final int maxTelemetryLines = 10;
    private final ArrayDeque<TelemetryEntry> telemetryBuffer = new ArrayDeque<>(maxTelemetryLines);

    // Others:
    boolean WiggleDir = false; // True for In, False for Out
    public int colorThreshold, alphaThreshold; // Used for active tuning

    // Tuning Mode Vars:
    boolean blockDetected;
    String blockColor;
    int selectedAction = 1;
    String selector;
    boolean editing = false;

    // Active Mode Latches:
    public boolean IdleModeActive = true;
    public boolean ActiveModeActive = false;
    public boolean TuningModeActive = false;

    // Global Enums:
    public enum IntakeAction {COLLECT, REJECT, SCORE, STOP}

    //endregion

    //region MainInit

    @Override
    public void mainInit() { // TODO: DO NOT ADD MultipleTelemetry(FtcDashboard), the telemetry is setup for the DRIVER HUB, NOT FTC DASHBOARD!

        // Fix POTENTIAL telemetry flooding lag
        telemetry.setMsTransmissionInterval(100);  // Send at most 10x/second

        // Required for the telemetry method used
        telemetry.setAutoClear(false);

        clearTelemetry();
        addTelemetryLine("Setup ~1% Complete: Int Hardware Map...");

        // [SETUP] Hardware Map
        DcMotorEx frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        DcMotorEx frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        DcMotorEx backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        DcMotorEx backRight = hardwareMap.get(DcMotorEx.class, "backRight");
        SparkFunOTOS sparkFun = hardwareMap.get(SparkFunOTOS.class, "sensor_otos");
        colorSensor = hardwareMap.colorSensor.get("ColorSensor");
        IntakeServo1 = hardwareMap.get(CRServo.class, "IntakeServo1");
        IntakeServo2 = hardwareMap.get(CRServo.class, "IntakeServo2");
        CamServoPan = hardwareMap.get(Servo.class, "CamServoPan");
        CamServoTilt = hardwareMap.get(Servo.class, "CamServoTilt");
        Lift = hardwareMap.get(DcMotorEx.class, "lift");

        addTelemetryLine("Setup ~16% Complete: Motor Config... (1/2)");

        // [SETUP] Motor Config.
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        Lift.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);

        addTelemetryLine("Setup ~50% Complete: Motor Config... (2/2)");

        frontLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        addTelemetryLine("Setup ~66% Complete: Int Robot.java Link...");

        // [SETUP] Robot.java Link
        robot = Robot
                .getInstance(frontLeft, frontRight, backLeft, backRight)
                .setImu(sparkFun);

        addTelemetryLine("Setup ~83% Complete: IMU Config...");

        // [SETUP] IMU Config.
        configureOtos();

        addTelemetryLine("Setup ~99% Complete: Setting Robot Defaults...");

        // [SETUP] Defaults
        robot.stopMotors();
        cameraGotoPos(0.505, 0.470);
        liftGotoPos(20);

        colorThreshold = colorThresholdDefault;
        alphaThreshold = alphaThresholdDefault;

        addTelemetryLine("RobotDefaults: colorThreshold:" + colorThresholdDefault + ", alphaThreshold:" + alphaThresholdDefault + ", canRobotMove?:" + robotCanMove);

        addTelemetryLine("Setup 100% Complete, Status: Waiting for start...");

        addTelemetryLine("Status: Running main loop...");
    }

    //endregion

    //region Main

    @Override
    public void mainLoop() {

        if (IdleModeActive) {
            updateTelemetry();
            sleepForRand(500, 1000);

            int idleChance = ThreadLocalRandom.current().nextInt(1, 101);
            addTelemetryLine("Current Idle Movement: ...");

            if (idleChance <= 50) {
                // 50% chance - Move Camera
                addTelemetryLine("Current Idle Movement: Move Camera");
                cameraGotoPos(ThreadLocalRandom.current().nextDouble(panMin, panMax), ThreadLocalRandom.current().nextDouble(tiltMin, tiltMax));
            } else if (idleChance <= 85) {
                // 35% chance - Move Lift
                addTelemetryLine("Current Idle Movement: Move Lift");
                liftGotoPos(ThreadLocalRandom.current().nextInt(10, 101));
            } else {
                // 15% chance - Wiggle Intake
                addTelemetryLine("Current Idle Movement: Wiggle Intake");
                intakeIdleWiggle();
            }

            if (gamepad1.cross) {
                ActiveModeActive = true;
                IdleModeActive = false;
            }
            if (gamepad1.triangle) {
                TuningModeActive = true;
                IdleModeActive = false;
            }
        }


        if (ActiveModeActive) {
            liftGotoPos(10);
            cameraGotoPos(0.505, tiltMin);

            boolean correctBlock = false;
            while (!correctBlock) {
                boolean block = false;

                intakeMove(IntakeAction.COLLECT);
                while (!block && !gamepad1.triangle) {
                    block = colorSensor.alpha() > alphaThreshold;
                }
                intakeMove(IntakeAction.STOP);
                addTelemetryLine("ActiveMode: Block detected, Identifying the color...");

                if (block && colorSensor.red() > colorSensor.blue() + colorThreshold) { // If Red Block, Collect
                    correctBlock = true;
                    addTelemetryLine("ActiveMode: Detected color RED -> Scoring...");
                    sleep(1000);
                } else if (block && colorSensor.blue() > colorSensor.red() + colorThreshold) { // If Blue Block, Reject
                    intakeMove(IntakeAction.REJECT);
                    addTelemetryLine("ActiveMode: Detected color BLUE -> Rejecting...");
                    sleep(1000);
                } else if (block) { // If Unidentifiable, Reject
                    intakeMove(IntakeAction.REJECT);
                    addTelemetryLine("ActiveMode: <ERROR> Color could not be identified! Rejecting...");
                    sleep(1000);
                }
            }

            cameraGotoPos(0.505, 0.470);
            if (robotCanMove) {robot.goTo(0.3, "0", "12", "0");}

            sleep(500);

            cameraGotoPos(0.800, 0.505);
            liftGotoPos(2850);

            sleep(500);
            intakeMove(IntakeAction.SCORE);

            sleep(1000);
            intakeMove(IntakeAction.STOP);
            liftGotoPos(10);

            sleep(500);
            if (robotCanMove) {robot.goTo(0.3, "0", "0", "0");sleep(500);}

            ActiveModeActive = false;
            IdleModeActive = true;
        }


        if (TuningModeActive) {
            clearTelemetry();
            telemetry.addLine("TUNING MODE\n");

            if (selectedAction == 1 && editing) {
                selector = ">>";
            } else if (selectedAction == 1) {
                selector = "> ";
            } else {
                selector = "  ";
            }
            telemetry.addLine(selector + "Color Threshold : " + colorThreshold + "(Default: " + colorThresholdDefault + ")");

            if (selectedAction == 2 && editing) {
                selector = ">>";
            } else if (selectedAction == 2) {
                selector = "> ";
            } else {
                selector = "  ";
            }
            telemetry.addLine(selector + "Alpha Threshold : " + alphaThreshold + "(Default: " + alphaThresholdDefault + ")");

            if (selectedAction == 3) {
                selector = "> ";
            } else {
                selector = "  ";
            }
            telemetry.addLine(selector + "EXIT TUNING MODE\n\n");

            telemetry.addLine("OUTPUT:\n");

            blockDetected = colorSensor.alpha() > alphaThreshold;
            telemetry.addLine("Block Detected? : " + blockDetected);

            if (colorSensor.red() > colorSensor.blue() + colorThreshold) {
                blockColor = "RED";
            } else if (colorSensor.blue() > colorSensor.red() + colorThreshold) {
                blockColor = "BLUE";
            } else {
                blockColor = "UNKNOWN";
            }
            telemetry.addLine("Block Color : " + blockColor);

            if (!editing) {
                if (gamepad1.dpad_up) {
                    while (gamepad1.dpad_up);
                    if (selectedAction != 1) {
                        selectedAction -= 1;
                    }
                }
                if (gamepad1.dpad_down) {
                    while (gamepad1.dpad_down);
                    if (selectedAction != 3) {
                        selectedAction += 1;
                    }
                }
                if (gamepad1.dpad_right) {
                    while (gamepad1.dpad_right);
                    if (selectedAction != 3) {
                        editing = true;
                    } else {
                        selectedAction = 1;
                        TuningModeActive = false;
                        IdleModeActive = true;
                        editing = false;
                    }
                }
            } else {
                if (gamepad1.dpad_up) {
                    while (gamepad1.dpad_up);
                    if (selectedAction == 1) {
                        colorThreshold += 1;
                    } else {
                        alphaThreshold += 1;
                    }
                }
                if (gamepad1.dpad_down) {
                    while (gamepad1.dpad_down);
                    if (selectedAction == 1) {
                        colorThreshold -= 1;
                    } else {
                        alphaThreshold -= 1;
                    }
                }
                if (gamepad1.dpad_left) {
                    while (gamepad1.dpad_left);
                    editing = false;
                }
                sleep(100);
            }
        }

    }

    //endregion

    //region Exe Functions

    // Less Main Stuffs:
    public void liftGotoPos(int LiftPos) {
        if (LiftPos > 2900 || LiftPos < -10) {
            throw new IllegalStateException("Lift Pos is OUT OF BOUNDS!");
        }

        Lift.setVelocityPIDFCoefficients(LP, LI, LD, LF);
        Lift.setTargetPosition(LiftPos);
        Lift.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);

        // Optionally set power (or velocity) once
        Lift.setVelocity(Math.abs(LiftPos - Lift.getCurrentPosition()) > 0 ? 1500 : 0);

        // Stop the motor
        Lift.setVelocity(0);
    }


    public void cameraGotoPos(double pan, double tilt) {
        /*if ((pan != 0.800 && tilt != 0.505) && (pan <= panMin || pan >= panMax || tilt <= tiltMin || tilt >= tiltMax)) {
            throw new IllegalStateException("Camera Pos is OUT OF BOUNDS!");
        }*/
        CamServoPan.setPosition(pan);
        CamServoTilt.setPosition(tilt);
    }

    public void intakeMove(IntakeAction action) {
        switch (action) {
            case COLLECT:
                IntakeServo1.setPower(0.25);
                IntakeServo2.setPower(-0.25);
                break;
            case REJECT:
                IntakeServo1.setPower(-0.5);
                IntakeServo2.setPower(0.5);
                break;
            case SCORE:
                IntakeServo1.setPower(-1);
                IntakeServo2.setPower(1);
                break;
            case STOP:
                IntakeServo1.setPower(0);
                IntakeServo2.setPower(0);
                break;
            default:
                throw new IllegalStateException(action + " is an INVALID intake action!");
        }
    }

    public void intakeIdleWiggle() {
        if (WiggleDir) {
            intakeMove(IntakeAction.COLLECT);
            sleep(300);
            intakeMove(IntakeAction.STOP);
            WiggleDir = false;
        } else {
            intakeMove(IntakeAction.REJECT);
            sleep(300);
            intakeMove(IntakeAction.STOP);
            WiggleDir = true;
        }
    }

    public void sleepForRand(long MinMillis, long MaxMillis) {
        if (MinMillis >= MaxMillis || MinMillis < 0) {
            throw new IllegalStateException("MIN value is greater than or equal to MAX value! OR MIN is < 0!");
        }
        if (MaxMillis >= Long.MAX_VALUE - 3) {
            throw new IllegalStateException("Why wait so long!?");
        }
        long randomTime = ThreadLocalRandom.current().nextLong(MinMillis, MaxMillis + 1);
        sleep(randomTime);
    }

    //endregion

    // region Telemetry

    private static class TelemetryEntry {
        final String key;
        String value;
        TelemetryEntry(String message) {
            this.key   = null;
            this.value = message;
        }
        boolean isPlain() { return key == null; }
    }

    private void clearTelemetry() {
        telemetry.clearAll();
    }

    private void updateTelemetry() {
        clearTelemetry();
        for (TelemetryEntry e : telemetryBuffer) {
            if (e.isPlain()) {
                telemetry.addLine(e.value);
            } else {
                telemetry.addData(e.key, e.value);
            }
        }
        telemetry.update();
    }

    public void addTelemetryLine(String msg) {
        if (telemetryBuffer.size() == maxTelemetryLines) {
            telemetryBuffer.removeFirst();
        }
        telemetryBuffer.addLast(new TelemetryEntry(msg));
        updateTelemetry();
    }

    //endregion

    //region OpModeShutdown

    @Override
    protected void onOpModeStop() {
        addTelemetryLine("Status: Shutting Down...");

        cameraGotoPos(0.800, 0.505);
        liftGotoPos(10);
        intakeMove(IntakeAction.STOP);
    }

    //endregion

    //region SparkFunOTOS Config
    @SuppressLint("DefaultLocale")
    private void configureOtos() {
        // SparkFun.setLinearUnit(DistanceUnit.METER);
        robot.getImu().setLinearUnit(DistanceUnit.INCH);
        // SparkFun.setAngularUnit(AngleUnit.RADIANS);
        robot.getImu().setAngularUnit(AngleUnit.DEGREES);

        robot.getImu().setLinearScalar(1.0);
        robot.getImu().setAngularScalar(1.0);

        robot.getImu().calibrateImu();

        robot.getImu().resetTracking();

        SparkFunOTOS.Pose2D currentPosition = new SparkFunOTOS.Pose2D(0, 0, 0);
        robot.getImu().setPosition(currentPosition);
    }
    //endregion
}
