package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;


@Autonomous
public class ColorTest extends OpMode {

    private ColorSensor colorSensor;
    private CRServo Servo1;
    private CRServo Servo2;

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
        if (red > 20 && red < 50 && green > 50 && green < 80 && blue > 70 && blue < 110) {
            Servo1.setPower(0.5);
            Servo2.setPower(-0.5);
        } else if (red > 170 && red < 200 && green > 100 && green < 120 && blue > 50 && blue < 80) {
            Servo1.setPower(-0.5);
            Servo2.setPower(0.5);
        }
    }
}
