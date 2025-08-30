package org.firstinspires.ftc.teamcode;




import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;


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

        if (red > 190) {
            Out();
            telemetry.addData("RED!!1!!1!", red);
        }

        if (red < 170) {
            In();
            telemetry.addData("NO RED!!1!!1!", red);
        }
    }

    private void Intake() {
        if (Dir == true) {
            Servo1.setPower(0.1);
            Servo2.setPower(-0.1);
        }
        else if (Dir == false) {
            Servo1.setPower(0);
            Servo2.setPower(0);
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
