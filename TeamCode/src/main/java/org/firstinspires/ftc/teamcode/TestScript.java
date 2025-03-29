package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp
public class TestScript extends OpMode {
    private DcMotorEx Test;

    @Override
    public void init() {
        Test = hardwareMap.get(DcMotorEx.class, "Motor");
    }

    @Override
    public void init_loop() {
        telemetry.addData("Please start the script", null);
        telemetry.update();
        Test.setPower(-0.5);
    }

    @Override
    public void start() {
        telemetry.clear();
    }

    @Override
    public void loop() {
        telemetry.addData("Thank you", null);
        telemetry.update();
        Test.setPower(0.5);
    }
}
