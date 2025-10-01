package org.firstinspires.ftc.teamcode.OldCode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.threadopmode.ThreadOpMode;

@TeleOp
public class touchsensortest extends ThreadOpMode {
    TouchSensor touchSensor;
    private CRServo Servo1;
    private CRServo Servo2;
    @Override
    public void mainInit() {
        touchSensor = hardwareMap.get(TouchSensor.class, "TouchSensor");
        Servo1 = hardwareMap.get(CRServo.class, "Servo1");
        Servo2 = hardwareMap.get(CRServo.class, "Servo2");
    }
    @Override
    public void mainLoop() {
        if (touchSensor.isPressed()) {
            Servo1.setPower(0.5);
            Servo2.setPower(-0.5);
            telemetry.addData("Touched", null);
            telemetry.update();
        } else {
            Servo1.setPower(0);
            Servo2.setPower(0);
            telemetry.addData("Not Touched", null);
            telemetry.update();
        }
    }
}
