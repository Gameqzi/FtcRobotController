package org.firstinspires.ftc.teamcode.OldCode;




import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;


@Autonomous
public class MotorVolOutPut extends OpMode {

    private DcMotorEx frontLeft, frontRight, backLeft, backRight;

    public static int P, I, D, F;

    @Override public void init() {
        frontLeft  = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft   = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight  = hardwareMap.get(DcMotorEx.class, "backRight");


        telemetry.clear();
    }

    @Override public void loop() {
        PIDFCoefficients FrontLeftPIDF = frontLeft.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        PIDFCoefficients FrontRightPIDF = frontRight.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);

        double FrontLeftP = FrontLeftPIDF.p, FrontLeftI = FrontLeftPIDF.i, FrontLeftD = FrontLeftPIDF.d, FrontLeftF = FrontLeftPIDF.f;
        double FrontRightP = FrontRightPIDF.p, FrontRightI = FrontRightPIDF.i, FrontRightD = FrontRightPIDF.d, FrontRightF = FrontRightPIDF.f;

        telemetry.addData("Front Left P", FrontLeftP);
        telemetry.addData("Front Left I", FrontLeftI);
        telemetry.addData("Front Left D", FrontLeftD);
        telemetry.addData("Front Left F", FrontLeftF);
        telemetry.addData("Front Right P", FrontRightP);
        telemetry.addData("Front Right I", FrontRightI);
        telemetry.addData("Front Right D", FrontRightD);
        telemetry.addData("Front Right F", FrontRightF);
    }
}
