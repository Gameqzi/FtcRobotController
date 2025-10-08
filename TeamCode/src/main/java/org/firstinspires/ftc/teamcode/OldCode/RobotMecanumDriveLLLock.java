// Java package declaration
package org.firstinspires.ftc.teamcode.OldCode;

// [IMPORT] FTC



import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;

// [IMPORT] LIMELIGHT 3A
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;


@Disabled
@TeleOp(name="Drive LL LOCK", group="TeleOp")
public class RobotMecanumDriveLLLock extends OpMode {

    private DcMotorEx frontLeft, frontRight, backLeft, backRight;
    private boolean lockMode = false;
    private boolean lastLockModeState = false;
    private Limelight3A limelight;
    private final double speedMult = 0.8;
    private ColorSensor colorSensor;
    private CRServo Servo1;
    private CRServo Servo2;
    int Dir = 1;

    @Override
    public void init() {
        // [SETUP] Hardware Map
        frontLeft  = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft   = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight  = hardwareMap.get(DcMotorEx.class, "backRight");
        colorSensor = hardwareMap.colorSensor.get("ColorSensor");
        Servo1 = hardwareMap.get(CRServo.class, "Servo1");
        Servo2 = hardwareMap.get(CRServo.class, "Servo2");

        // [SETUP] Motor Config.
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        // [SETUP] Limelight3A
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.start();
        limelight.pipelineSwitch(0);

        // [DEBUG] ScriptStatus
        telemetry.addData("Status:", "Initialized");
        telemetry.addData("!WARNING!:", "Potential robot spazzing, as this is a test");

    }

    @Override
    public void loop() {
        // [INPUT] Lock Mode (BOOLEAN, 'A')
        if (gamepad1.triangle && !lastLockModeState) {
            lockMode = !lockMode;
        }
        lastLockModeState = gamepad1.triangle;

        // [INPUT] Drive (DOUBLE, Jstick)
        double drive  = gamepad1.left_stick_y * speedMult;  // Forward/Backward
        double strafe = -gamepad1.left_stick_x * speedMult; // Left/Right
        double rotate = -gamepad1.right_stick_x * speedMult; // Rotation

        // [SCRIPT] Lock Mode
        if (lockMode) {
            drive *= 0.5;
            strafe *= 0.5;
            rotate *= 0.5;
            double[] movementAdjustments = getAlignMovement();
            strafe += movementAdjustments[0];
            drive += movementAdjustments[1];
            rotate += movementAdjustments[2];
        }

        // [SCRIPT] Movement Calc (DRIVE: MECANUM)
        double frontLeftPower  = drive + strafe + rotate;
        double frontRightPower = drive - strafe - rotate;
        double backLeftPower   = drive - strafe + rotate;
        double backRightPower  = drive + strafe - rotate;

        float red = colorSensor.red();
        float green = colorSensor.green();
        float blue = colorSensor.blue();

        // [OUTPUT] Motor Movement(s)
        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);

        Intake();

        if (blue > 45) {
            Out();
        }

        if (blue < 40) {
            In();
        }

        // [DEBUG] LockModeStatus, MotorPowers (Tele)
        telemetry.addData("Lock Mode", lockMode ? "ON" : "OFF");
        telemetry.addData("Front Left Power", frontLeftPower);
        telemetry.addData("Front Right Power", frontRightPower);
        telemetry.addData("Back Left Power", backLeftPower);
        telemetry.addData("Back Right Power", backRightPower);
        telemetry.update();
    }

    @Override
    // [HELPER-END] Stop: Motors, Limelight
    public void stop() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        limelight.stop();
    }

    private void Intake() {
        if (Dir == 1) {
            Servo1.setPower(0.25);
            Servo2.setPower(-0.25);
        }
        else if (Dir == 2) {
            Servo1.setPower(-2);
            Servo2.setPower(2);
        }
        else if (Dir == 3) {
            Servo1.setPower(0);
            Servo2.setPower(0);
        }
    }

    private void In() {
        Dir = 1;
    }

    private void Out() {
        Dir = 2;
    }


    // [FUNCTION] Alignment Calc (Limelight) (WITH DEBUG: TELE)
    private double[] getAlignMovement() {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            double tx = result.getTx(); // Left/Right Offset
            double ty = result.getTy(); // Up/Down Offset
            double ta = result.getTa(); // Target Area (Size)
            
            double strafeAdjust = tx * -0.02;  // Side/Side (X)
            double driveAdjust = (10 - ta) * -0.02; // Forward/Backward
            double rotateAdjust = tx * -0.015; // Rotate

            telemetry.addData("Target X", tx);
            telemetry.addData("Target Y", ty);
            telemetry.addData("Target Area", ta);
            return new double[]{strafeAdjust, driveAdjust, rotateAdjust};
        } else {
            telemetry.addData("Limelight", "No Targets");
            return new double[]{0, 0, 0};
        }
    }
}
