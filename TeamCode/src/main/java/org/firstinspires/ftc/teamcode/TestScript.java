package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp
public class TestScript extends LinearOpMode {
    private DcMotorEx Test;

    @Override
    public void runOpMode() {
        Test = hardwareMap.get(DcMotorEx.class, "Motor");

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Test", null);
        }
    }
}
