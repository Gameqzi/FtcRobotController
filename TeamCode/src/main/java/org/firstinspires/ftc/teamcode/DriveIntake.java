package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;

@Config
@TeleOp
public class DriveIntake extends OpMode {
    private DcMotorEx frontLeft, frontRight, backLeft, backRight;
    private ColorSensor colorSensor;
    private CRServo Servo1;
    private CRServo Servo2;
    boolean Dir;

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
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
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

        if (gamepad1.triangle) {
            Dir = true;
            if (blue > 45) {
                Out();
            }
        } else {
            Dir = null;
        }

        // Set the calculated power to each motor
        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);

        // Send telemetry data for debugging and feedback
        telemetry.addData("Front Left Power", frontLeftPower);
        telemetry.addData("Front Right Power", frontRightPower);
        telemetry.addData("Back Left Power", backLeftPower);
        telemetry.addData("Back Right Power", backRightPower);
        telemetry.update();
    }

    private void Intake() {
        if (Dir == true) {
            Servo1.setPower(0.1);
            Servo2.setPower(-0.1);
        }
        else if (Dir == false) {
            Servo1.setPower(-2);
            Servo2.setPower(2);
        }
    }

    private void Out() {
        Dir = false;
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
