package org.firstinspires.ftc.teamcode;




import com.arcrobotics.ftclib.controller.PIDFController;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.RobotLog;
import android.util.Log;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


@TeleOp(name = "MecanumStraight", group = "Drive")
public class MecanumStraight extends OpMode {

    /* ──────────────────────────────────────────────────────────────
     * 1) Dashboard-tunable constants
     * ────────────────────────────────────────────────────────────── */
    public static double TARGET_TPS  = 600;   // keep as-is for now
    public static double kP_HEADING = 1.1;
    public static double kI_HEADING = 0.0;
    public static double kD_HEADING = 0.10;

    /*  NEW motor feed-forward gains – paste these four numbers  */
    public static double kF_FL = 7.5;
    public static double kF_FR = 10.8;
    public static double kF_BL = 13.6;
    public static double kF_BR =  9.8;
    public static double kP_MOTOR = 0.12;  // small P helps resist load change

    /* ──────────────────────────────────────────────────────────────
     * 2) Hardware
     * ────────────────────────────────────────────────────────────── */
    private DcMotorEx fl, fr, bl, br;
    private IMU imu;
    private PIDFController headingPID =
            new PIDFController(kP_HEADING, kI_HEADING, kD_HEADING, 0);

    @Override
    public void init() {

        /* ---- motor mapping & direction ---- */
        fl = hardwareMap.get(DcMotorEx.class, "frontLeft");
        fr = hardwareMap.get(DcMotorEx.class, "frontRight");
        bl = hardwareMap.get(DcMotorEx.class, "backLeft");
        br = hardwareMap.get(DcMotorEx.class, "backRight");

        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);

        /* ---- enable velocity closed-loop and set kF/P gains ---- */
        setMotorModeAndCoeffs(fl, kF_FL);
        setMotorModeAndCoeffs(fr, kF_FR);
        setMotorModeAndCoeffs(bl, kF_BL);
        setMotorModeAndCoeffs(br, kF_BR);

        /* ---- IMU in yaw-only mode ---- */
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP)));
        imu.resetYaw();

        /* ---- Dashboard + driver-station telemetry together ---- */

    }

    @Override
    public void loop() {

        /* 1) Re-sync heading PID gains if changed in Dashboard */
        headingPID.setPIDF(kP_HEADING, kI_HEADING, kD_HEADING, 0);

        /* 2) Outer loop: keep yaw ≈ 0° */
        double yaw = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        double correction = headingPID.calculate(yaw, 0);

        /* 3) Mix correction into left / right wheel targets */
        double leftTPS  = TARGET_TPS + correction;
        double rightTPS = TARGET_TPS - correction;

        /* 4) Command the per-wheel velocity loops */
        fl.setVelocity(leftTPS);
        bl.setVelocity(leftTPS);
        fr.setVelocity(rightTPS);
        br.setVelocity(rightTPS);

        RobotLog.dd("Strafe", "FrontLeft=%d  FrontRight=%d BackLeft=%d BackRight=%d", fl.getCurrentPosition(), fr.getCurrentPosition(), bl.getCurrentPosition(), br.getCurrentPosition());
        Log.w("test", "test");

        /* 5) Telemetry for live tuning */
        telemetry.addData("Yaw (°)", yaw);
        telemetry.addData("Correction", correction);
        telemetry.addData("FL tps", fl.getVelocity());
        telemetry.addData("FR tps", fr.getVelocity());
        telemetry.addData("BL tps", bl.getVelocity());
        telemetry.addData("BR tps", br.getVelocity());
        telemetry.addData("Fl Position", fl.getCurrentPosition());
        telemetry.addData("FR Position", fr.getCurrentPosition());
        telemetry.addData("BL Position", bl.getCurrentPosition());
        telemetry.addData("BR Position", br.getCurrentPosition());
    }

    @Override
    public void stop() {
        for (DcMotorEx m : new DcMotorEx[]{fl, fr, bl, br}) m.setVelocity(0);
    }

    /* ──────────────────────────────────────────────────────────────
     * helper: set mode & PIDF for one motor
     * ────────────────────────────────────────────────────────────── */
    private void setMotorModeAndCoeffs(DcMotorEx motor, double kF) {
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setVelocityPIDFCoefficients(kP_MOTOR, 0, 0, kF);
    }
}
