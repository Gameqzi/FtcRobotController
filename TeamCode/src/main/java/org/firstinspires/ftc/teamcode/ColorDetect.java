package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;

@Config
@TeleOp
public class ColorDetect extends OpMode {
    private ColorSensor colorSensor;
    private CRServo Servo1;
    private CRServo Servo2;
    boolean Dir = true;

    @Override
    public void init() {
        colorSensor = hardwareMap.colorSensor.get("ColorSensor");
        Servo1 = hardwareMap.get(CRServo.class, "Servo1");
        Servo2 = hardwareMap.get(CRServo.class, "Servo2");
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    public void loop() {
        float red = colorSensor.red();
        float green = colorSensor.green();
        float blue = colorSensor.blue();

        telemetry.addData("Red", red);
        telemetry.addData("Green", green);
        telemetry.addData("Blue", blue);
        telemetry.update();

        Intake();

        if (blue > 45) {
            Out();
            telemetry.addData("BLUE!!1!!1!", blue);
        }

        if (blue < 36) {
            In();
            telemetry.addData("NO BLUE!!1!!1!", blue);
        }
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
        telemetry.addData("Out is running", Dir);
        Dir = false;
    }

    private void In() {
        telemetry.addData("In is running", Dir);
        Dir = true;
    }
}
