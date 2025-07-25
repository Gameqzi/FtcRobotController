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
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

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
    public static final double LP = 8, LI = 1, LD = 0.1, LF = 10; // Lift PIDF Values                                       | Already Tuned
    public static final double panMin = 0.210, panMax = 0.590, tiltMin = 0.350, tiltMax = 0.800; // Camera Servo Limits     | Already Tuned
                               // Left Max     // Right Max    // Down Max      // Up Max
    public static final double panHome = 0.500, tiltHome = 0.475; // Camera "Home" Position                                 | Already Tuned
    public static final double panScore = 0.500, tiltScore = 0.800; // Camera "Score" Position                              | Already Tuned
    public static final int colorThresholdDefault = 30; // ToDo:                                                            | Change For Each Environment?
    public static final int alphaThresholdDefault = 210; // ToDo:                                                           | Change For Each Environment?
    public static final boolean robotCanMove = false; // ToDo: Can the robot move on the table?
    public static final boolean robotQuietMode = false; // If we want less motor wining. Maybe for enclosed environments?
    boolean liftActive = !robotQuietMode; // Sub-Variable for Quiet Mode

    // Telemetry:
    private static final int maxTelemetryLines = 15;
    private final ArrayDeque<TelemetryEntry> telemetryBuffer = new ArrayDeque<>(maxTelemetryLines);

    // Others:
    boolean WiggleDir = false; // True for In, False for Out
    public int colorThreshold, alphaThreshold; // Used for active tuning
    public int gamepadLastR = 0, gamepadLastG = 0, gamepadLastB = 0;

    // Tuning Mode Vars:
    boolean tuningFirstTime = true;
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
        telemetry.setMsTransmissionInterval(50);  // Send at most 20x/second

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
        Lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Lift.setDirection(DcMotor.Direction.REVERSE);

        Lift.setVelocityPIDFCoefficients(LP, LI, LD, LF);

        addTelemetryLine("Setup ~50% Complete: Motor Config... (2/2)");

        frontLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        addTelemetryLine("Setup ~66% Complete: Init Robot.java & SystemUtils.java Link...");

        // [SETUP] Robot.java & SystemUtils.java Link
        robot = Robot
                .getInstance(frontLeft, frontRight, backLeft, backRight)
                .setImu(sparkFun);
        //SystemUtils.setupSystemUtils(telemetry, gamepad1, gamepad2);

        SystemUtils.helpReference();

        SystemUtils     .init       .initTelemetry(telemetry);
        SystemUtils     .init       .initGamepad1(gamepad1);
        SystemUtils     .init       .initGamepad2(gamepad2);

        SystemUtils     .gamepad    .led    .setLED(SystemUtils.GamepadTarget.BOTH, 0, 0, 0, 0);
        SystemUtils     .gamepad    .led    .floatLED(SystemUtils.GamepadTarget.BOTH, 0, 0, 0, 0, 0);
        SystemUtils     .gamepad    .led    .advBlinkLED(SystemUtils.GamepadTarget.BOTH, 0, 0, 0, 0, 0, SystemUtils.BlinkType.EVEN, SystemUtils.BlinkAction.SHARP);
        SystemUtils     .gamepad    .led    .rainbowLED(SystemUtils.GamepadTarget.BOTH, 0, 0);

        SystemUtils     .gamepad    .rumble .advRumble(SystemUtils.GamepadTarget.BOTH, 0, 0, 0);



        addTelemetryLine("Setup ~83% Complete: IMU Config...");

        // [SETUP] IMU Config.
        configureOtos();

        addTelemetryLine("Setup ~99% Complete: Setting Robot Defaults...");

        // [SETUP] Defaults
        robot.stopMotors();
        cameraGotoPos(panHome, tiltHome);
        liftGotoPos(30);

        colorThreshold = colorThresholdDefault;
        alphaThreshold = alphaThresholdDefault;

        addTelemetryLine("RobotDefaults: colorThreshold:" + colorThresholdDefault + ", alphaThreshold:" + alphaThresholdDefault + ", canRobotMove?:" + robotCanMove);

        addTelemetryLine("Setup 100% Complete, Status: Waiting for start...");

        //SystemUtils.gamepad.floatLED(GamepadTarget.BOTH, 0, 1, 0, 5000, 500);

        //SystemUtils.gamepad.advRumble(GamepadTarget.BOTH, 0.3, 0.3, 1000);
    }

    //endregion

    //region Main

    @Override
    public void mainLoop() {

        if (IdleModeActive) {
            updateTelemetry();
            //fadeGamepadLED(1, 1, 0);
            sleepForRand(500, 2000);

            int idleChance = ThreadLocalRandom.current().nextInt(1, 101);

            if (robotQuietMode) {
                if (idleChance <= 80) {
                    // 80% chance - Move Camera
                    addTelemetryLine("Current Idle Movement: Move Camera");
                    cameraGotoPos(ThreadLocalRandom.current().nextDouble(panMin, panMax - 0.05), ThreadLocalRandom.current().nextDouble(tiltMin, tiltMax - 0.2));
                } else {
                    // 20% chance - Wiggle Intake
                    addTelemetryLine("Current Idle Movement: Wiggle Intake");
                    intakeIdleWiggle();
                }
            } else {
            if (idleChance <= 70) {
                // 70% chance - Move Camera
                addTelemetryLine("Current Idle Movement: Move Camera");
                cameraGotoPos(ThreadLocalRandom.current().nextDouble(panMin, panMax - 0.05), ThreadLocalRandom.current().nextDouble(tiltMin, tiltMax - 0.2));
            } else if (idleChance <= 90) {
                // 20% chance - Wiggle Intake
                addTelemetryLine("Current Idle Movement: Wiggle Intake");
                intakeIdleWiggle();
            } else {
                // 10% chance - Move Lift
                addTelemetryLine("Current Idle Movement: Move Lift");
                liftGotoPos(ThreadLocalRandom.current().nextInt(10, 301));
            }
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
            addTelemetryLine("Status: Running Active Mode...");
            gamepad1.rumble(0.2, 0.2, 500);
            //fadeGamepadLED(1, 0, 0);

            liftActive = true;
            liftGotoPos(30);
            cameraGotoPos(panHome, tiltMin);

            boolean correctBlock = false;
            while (!correctBlock) {
                boolean block = false;

                intakeMove(IntakeAction.COLLECT);
                while (!block && !gamepad1.triangle) {
                    block = colorSensor.alpha() > alphaThreshold;
                }
                intakeMove(IntakeAction.STOP);

                if (block && colorSensor.red() > colorSensor.blue() + colorThreshold) { // If Red Block, Collect
                    correctBlock = true;
                    addTelemetryLine("ActiveMode: Detected block: RED -> Scoring...");
                    sleep(1000);
                } else if (block && colorSensor.blue() > colorSensor.red() + colorThreshold) { // If Blue Block, Reject
                    intakeMove(IntakeAction.REJECT);
                    addTelemetryLine("ActiveMode: Detected block: BLUE -> Rejecting...");
                    sleep(500);

                    // NEW: *Shakes Head* LoL
                    cameraGotoPos(panMin, tiltHome);
                    sleep(500);
                    cameraGotoPos(panMax, tiltHome);
                    sleep(500);
                    cameraGotoPos(panMin, tiltHome);
                    sleep(500);
                    cameraGotoPos(panMax, tiltHome);

                    sleep(1000);
                    cameraGotoPos(panHome, tiltMin);
                } else if (block) { // If Unidentifiable, Reject
                    intakeMove(IntakeAction.REJECT);
                    addTelemetryLine("<ERROR> Block color could not be identified! Rejecting...");
                    sleep(1000);
                }
            }

            cameraGotoPos(panHome, tiltHome);
            if (robotCanMove) {robot.goTo(0.3, "0", "12", "0");}

            sleep(500);

            cameraGotoPos(panScore, tiltScore);
            liftGotoPos(2850);

            sleep(500);
            intakeMove(IntakeAction.SCORE);

            sleep(1000);
            intakeMove(IntakeAction.STOP);
            liftGotoPos(30);

            sleep(500);
            cameraGotoPos(panHome, tiltHome);
            if (robotCanMove) {robot.goTo(0.3, "0", "0", "0");}
            sleep(500);

            gamepad1.rumble(0.1, 0.1, 300);
            liftActive = !robotQuietMode;
            liftGotoPos(0);

            ActiveModeActive = false;
            IdleModeActive = true;
        }


        if (TuningModeActive) {
            if (tuningFirstTime) {
                addTelemetryLine("Status: Running Tuning Mode...");
                gamepad1.rumble(0.2, 0.2, 500);
                liftGotoPos(200);
                cameraGotoPos(panScore, tiltScore);

                telemetry.setAutoClear(false);
                tuningFirstTime = false;
            }

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
                    sleep(100);
                    if (selectedAction != 1) {
                        selectedAction -= 1;
                    }
                }
                if (gamepad1.dpad_down) {
                    sleep(100);
                    if (selectedAction != 3) {
                        selectedAction += 1;
                    }
                }
                if (gamepad1.dpad_right) {
                    sleep(100);
                    if (selectedAction != 3) {
                        editing = true;
                    } else {
                        selectedAction = 1;
                        tuningFirstTime = true;
                        TuningModeActive = false;
                        IdleModeActive = true;
                        editing = false;
                    }
                }
            } else {
                if (gamepad1.dpad_up) {
                    sleep(100);
                    if (selectedAction == 1) {
                        colorThreshold += 5;
                    } else {
                        alphaThreshold += 5;
                    }
                }
                if (gamepad1.dpad_down) {
                    sleep(100);
                    if (selectedAction == 1) {
                        colorThreshold -= 5;
                    } else {
                        alphaThreshold -= 5;
                    }
                }
                if (gamepad1.dpad_left) {
                    sleep(100);
                    editing = false;
                }
                sleep(80);
            }
        }
        // ToDo: Note for Part 2: setTheToggle(</NaN/>), THEN: goToPos(30);
    }

    //endregion

    //region Exe Functions

    // Less Main Stuffs:
    public void liftGotoPos(int LiftPos) {
        if (liftActive) {
            Lift.setMotorEnable();
            if (LiftPos > 2900 || LiftPos < -10) {
                throw new IllegalStateException("Lift Pos is OUT OF BOUNDS!");
            }

            Lift.setTargetPosition(LiftPos);
            Lift.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            Lift.setVelocity(500); // This is the MAX velocity the internal PID will aim for

            //noinspection StatementWithEmptyBody
            while (Lift.isBusy()) ; // THIS IS REQUIRED!
        } else {
            Lift.setMotorDisable();
            addTelemetryLine("<ERROR> [SILENT] Lift was called, but lift is not active!");
        }
    }


    public void cameraGotoPos(double pan, double tilt) {
        if (!(pan >= panMin && pan <= panMax && tilt >= tiltMin && tilt <= tiltMax)) {
            throw new IllegalStateException("Camera Pos is OUT OF BOUNDS!");
        }
        CamServoPan.setPosition(pan);
        CamServoTilt.setPosition(tilt);
    }

    public void intakeMove(IntakeAction action) {
        switch (action) {
            case COLLECT:
                IntakeServo1.setPower(0.1);
                IntakeServo2.setPower(-0.1);
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
            sleep(1000);
            intakeMove(IntakeAction.STOP);
            WiggleDir = false;
        } else {
            intakeMove(IntakeAction.REJECT);
            sleep(500);
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

    public void fadeGamepadLED(int R, int G, int B) {
        ElapsedTime timer = new ElapsedTime();
        timer.reset();

        int duration = 800;
        
        while (timer.milliseconds() < duration) {
            double progress = timer.milliseconds() / duration;

            double currentRed = gamepadLastR + (R - gamepadLastR) * progress;
            double currentGreen = gamepadLastG + (G - gamepadLastG) * progress;
            double currentBlue = gamepadLastB + (B - gamepadLastB) * progress;
            
            currentRed = Math.max(0.0, Math.min(1.0, currentRed));
            currentGreen = Math.max(0.0, Math.min(1.0, currentGreen));
            currentBlue = Math.max(0.0, Math.min(1.0, currentBlue));

            gamepad1.setLedColor(currentRed, currentGreen, currentBlue, -1);

            sleep(20);
        }

        gamepadLastR = R; gamepadLastG = G; gamepadLastB = B;

        gamepad1.setLedColor(R, G, B, -1);
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
        telemetry.setAutoClear(true);
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

        intakeMove(IntakeAction.STOP);
        cameraGotoPos(panScore, tiltScore);
        liftGotoPos(0);
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
