package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.threadopmode.ThreadOpMode;

@TeleOp
public class servothing extends ThreadOpMode {
    Servo servo1;
    Servo servo2;



    @Override
    public void mainInit() {
        servo1 = hardwareMap.get(Servo.class, "test");
        servo2 = hardwareMap.get(Servo.class, "test2");
    }

    @Override
    public void mainLoop() {
        telemetry.addData("Pos1", servo1.getPosition());
        telemetry.addData("Pos2", servo2.getPosition());
    }
}
