package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.threadopmode.ThreadOpMode;

import java.util.List;

@TeleOp
public class LimelightModel extends ThreadOpMode {

    // Int Var/Obj
    Limelight3A limelight;

    private DcMotorEx frontLeft, frontRight, backLeft, backRight;

    double frontLeftPower;
    double frontRightPower;
    double backLeftPower;
    double backRightPower;

    String blockColor;

    @Override
    public void mainInit() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();

        // [SETUP] Hardware Map
        frontLeft  = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft   = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight  = hardwareMap.get(DcMotorEx.class, "backRight");

        // [SETUP] Motor Config.
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

    }

    @Override
    public void mainLoop() {
        LLStatus status = limelight.getStatus();
        telemetry.addData("LL", "Temp: %.1fC, CPU: %.1f%%, FPS: %d",
                status.getTemp(), status.getCpu(),(int)status.getFps());

        LLResult result = limelight.getLatestResult();
        if (result != null) {
            List<LLResultTypes.DetectorResult> detectorResults = result.getDetectorResults();
            for (LLResultTypes.DetectorResult dr : detectorResults) {
                telemetry.addData("Detector", "Class: %s, Area: %.2f, X: %.2f, Y: %.2f", dr.getClassName(), dr.getTargetArea(), dr.getTargetXPixels(), dr.getTargetYPixels());
                blockColor = dr.getClassName();
                // [SCRIPT] Lock Mode
                    double[] movementAdjustments = getAlignMovement();
                    double speedMult = 0.8;
                    double drive = movementAdjustments[1] * speedMult;  // Forward/Backward
                    double strafe = movementAdjustments[0] * speedMult; // Left/Right
                    double rotate = movementAdjustments[2] * speedMult; // Rotation

                    // [SCRIPT] Movement Calc (DRIVE: MECANUM)
                    frontLeftPower = drive + strafe + rotate;
                    frontRightPower = drive - strafe - rotate;
                    backLeftPower = drive - strafe + rotate;
                    backRightPower = drive + strafe - rotate;

            }
        }




        // End-Lock-Mode
    }

    // [FUNCTION] Alignment Calc (Limelight) (WITH DEBUG: TELE)
    private double[] getAlignMovement() {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            double tx = result.getTx(); // Left/Right Offset
            double ty = result.getTy(); // Up/Down Offset
            double ta = result.getTa(); // Target Area (Size)

            double strafeAdjust = tx * -0.02;  // Side/Side (X)
            double driveAdjust = (10 - ta) * -0.02; // Forward/Backward
            double rotateAdjust = tx * -0.015; // Rotate

            telemetry.addData("Target X", tx);
            telemetry.addData("Target Y", ty);
            telemetry.addData("Target Area", ta);
            return new double[]{strafeAdjust, driveAdjust, rotateAdjust};
        } else {
            telemetry.addData("Limelight", "No Targets");
            return new double[]{0, 0, 0};
        }
    }

}
