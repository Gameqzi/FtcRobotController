package org.firstinspires.ftc.teamcode.OldCode;




import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.threadopmode.ThreadOpMode;

import java.util.List;


@TeleOp
public class servothing extends ThreadOpMode {

    Limelight3A limelight;
    Servo servo1;
    Servo servo2;

    public static double Servo1Pos = 0.5;
    public static double Servo2Pos = 0.47;

    @Override
    public void mainInit() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        servo1 = hardwareMap.get(Servo.class, "test");
        servo2 = hardwareMap.get(Servo.class, "test2");
        telemetry.setMsTransmissionInterval(11);
        limelight.pipelineSwitch(0);
        limelight.start();

    }

    @Override
    public void mainLoop() {
        LLStatus status = limelight.getStatus();
        LLResult result = limelight.getLatestResult();
        if (result != null) {
            if (result.isValid()) {
                List<LLResultTypes.DetectorResult> detectorResults = result.getDetectorResults();
                for (LLResultTypes.DetectorResult dr : detectorResults) {
                    if (dr.getTargetArea() < 0.14) {
                        telemetry.addData("Detector", "Class: %s, Area: %.2f", dr.getClassName(), dr.getTargetArea());
                    } else {
                        telemetry.addData("big block", dr.getTargetArea());
                    }
                }
            }
        }
        telemetry.addData("Pos1", servo1.getPosition());
        telemetry.addData("Pos2", servo2.getPosition());
        servo1.setPosition(Servo1Pos);
        servo2.setPosition(Servo2Pos);
    }
}
