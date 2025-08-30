package org.firstinspires.ftc.teamcode;




import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.threadopmode.ThreadOpMode;


@TeleOp
public class MainTeleOp extends ThreadOpMode {
    ColorSensor colorSensor;
    CRServo Servo1;
    CRServo Servo2;
    DcMotorEx frontLeft, frontRight, backLeft, backRight;
    DcMotorEx lift;

    public static double P = 10, I = 3, D = 0, F = 8;

    public static double LP = 10, LI = 3, LD = 0, LF = 8;

    int Dir = 1;

    boolean BlockIn = false;

    int LiftPos = 0;

    @Override
    public void mainInit() {
        frontLeft  = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft   = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight  = hardwareMap.get(DcMotorEx.class, "backRight");
        lift       = hardwareMap.get(DcMotorEx.class, "lift");
        Servo1 = hardwareMap.get(CRServo.class, "IntakeServo1");
        Servo2 = hardwareMap.get(CRServo.class, "IntakeServo2");
        colorSensor = hardwareMap.colorSensor.get("ColorSensor");

        frontRight.setDirection(DcMotorEx.Direction.REVERSE);
        backRight.setDirection(DcMotorEx.Direction.FORWARD);
        frontLeft.setDirection(DcMotorEx.Direction.REVERSE);
        backLeft.setDirection(DcMotorEx.Direction.FORWARD);
        lift.setDirection(DcMotorEx.Direction.REVERSE);

        ResetEncoders();


        telemetry.clearAll();
    }

    @Override
    public void mainLoop() {
        // Add PIDF coefficients to each motor
        frontRight.setVelocityPIDFCoefficients(P, I, D, F);
        backRight.setVelocityPIDFCoefficients(P, I, D, F);
        frontLeft.setVelocityPIDFCoefficients(P, I, D, F);
        backLeft.setVelocityPIDFCoefficients(P, I, D, F);
        lift.setVelocityPIDFCoefficients(LP, LI, LD, LF);

        float red = colorSensor.red();
        float green = colorSensor.green();
        float blue = colorSensor.blue();

        double drive  = gamepad1.left_stick_y;  // Forward/backward (inverted as necessary)
        double strafe = -gamepad1.right_stick_x;     // Left/right
        double rotate = gamepad1.left_trigger - gamepad1.right_trigger;     // Rotation

        double frontLeftPower  = drive + strafe + rotate;
        double frontRightPower = drive - strafe - rotate;
        double backLeftPower   = drive - strafe + rotate;
        double backRightPower  = drive + strafe - rotate;

        // Set the calculated power to each motor
        frontLeft.setVelocity(-frontLeftPower*500);
        frontRight.setVelocity(frontRightPower*500);
        backLeft.setVelocity(backLeftPower*500);
        backRight.setVelocity(-backRightPower*500);

        if (gamepad1.cross && Math.abs(lift.getCurrentPosition()) < 2900) {
            if (Math.abs(LiftPos) > 2900) {
                LiftPos -= 5;
            } else {
                LiftPos += 5;
            }
            lift.setVelocity(-1750);
        } else if (gamepad1.circle && lift.getCurrentPosition() < 70) {
            if (Math.abs(LiftPos) > 70) {
                LiftPos -= 5;
            } else {
                LiftPos += 5;
            }
        } else {
            lift.setVelocity(0);
        }

        lift.setVelocity(5000);

        if (gamepad1.triangle) {
            LiftPos = 2850;
        }

        if (gamepad1.square) {
            LiftPos = 70;
        }

        if (gamepad1.left_bumper) {Dir = 2;} else if (gamepad1.right_bumper) {Dir = 1;} else {Dir = 0;}

        if (red > 180) {
            BlockIn = true;
        }

        if (red > 170 && Dir == 2) {
            BlockIn = false;
        }

        if (!BlockIn) {
            if (Dir == 0) {
                Servo1.setPower(0.15);
                Servo2.setPower(-0.15);
            }
        }
        if (BlockIn) {
            if (Dir == 2) {
                Servo1.setPower(-0.7);
                Servo2.setPower(0.7);
            } else if (Dir == 0) {
                Servo1.setPower(0);
                Servo2.setPower(0);
            } else if (Dir == 1) {
                Servo1.setPower(-1);
                Servo2.setPower(1);
            }
        }

        lift.setTargetPosition(LiftPos);
        lift.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);

        telemetry.addData("ticks", lift.getCurrentPosition());
        telemetry.addData("LiftPos", LiftPos);
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
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
}
