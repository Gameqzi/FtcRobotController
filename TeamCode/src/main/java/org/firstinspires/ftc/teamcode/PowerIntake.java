package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;

import org.firstinspires.ftc.teamcode.threadopmode.ThreadOpMode;

@TeleOp
public class PowerIntake extends ThreadOpMode {

    CRServo Servo1;
    CRServo Servo2;

    @Override
    public void mainInit() {
        Servo1 = hardwareMap.get(CRServo.class, "Servo1");
        Servo2 = hardwareMap.get(CRServo.class, "Servo2");
    }

    @Override
    public void mainLoop() {
        Servo1.setPower(0.15);
        Servo2.setPower(-0.15);
    }
}
