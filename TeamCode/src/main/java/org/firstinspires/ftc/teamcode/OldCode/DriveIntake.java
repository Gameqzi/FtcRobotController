package org.firstinspires.ftc.teamcode.OldCode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;



import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;


@TeleOp
public class DriveIntake extends OpMode {
    private DcMotorEx frontLeft, frontRight, backLeft, backRight;
    private ColorSensor colorSensor;
    private CRServo Servo1;
    private CRServo Servo2;
    int Dir = 3;

    @Override
    public void init() {
        // Map your hardware. Make sure the names match your configuration.
        frontLeft  = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft   = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight  = hardwareMap.get(DcMotorEx.class, "backRight");
        colorSensor = hardwareMap.colorSensor.get("ColorSensor");
        Servo1 = hardwareMap.get(CRServo.class, "Servo1");
        Servo2 = hardwareMap.get(CRServo.class, "Servo2");

        // Reverse right side motors if needed to ensure proper direction
        frontRight.setDirection(DcMotorEx.Direction.REVERSE);
        backRight.setDirection(DcMotorEx.Direction.REVERSE);

    }

    @Override
    public void loop() {
        // Read gamepad inputs:
        double drive  = gamepad1.left_stick_y;  // Forward/backward (inverted as necessary)
        double strafe = -gamepad1.left_stick_x;     // Left/right
        double rotate = -gamepad1.right_stick_x;     // Rotation

        // Calculate motor powers for mecanum drive
        double frontLeftPower  = drive + strafe + rotate;
        double frontRightPower = drive - strafe - rotate;
        double backLeftPower   = drive - strafe + rotate;
        double backRightPower  = drive + strafe - rotate;

        float red = colorSensor.red();
        float green = colorSensor.green();
        float blue = colorSensor.blue();

        telemetry.addData("Red", red);
        telemetry.addData("Green", green);
        telemetry.addData("Blue", blue);
        telemetry.update();

        Intake();

        if (gamepad1.cross) {
            Dir = 1;
            if (blue > 45) {
                Out();
            }
        }
        else {
            Dir = 3;
        }

        // Set the calculated power to each motor
        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
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

    private void Out() {
        Dir = 2;
    }

    @Override
    public void stop() {
        // Optionally, stop all motors when the opmode is stopped.
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
}
