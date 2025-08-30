package org.firstinspires.ftc.teamcode;




import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;


@Autonomous
public class RunTest extends OpMode {
    private DcMotorEx frontLeft, frontRight, backLeft, backRight;

    @Override public void init() {
        frontLeft  = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft   = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight  = hardwareMap.get(DcMotorEx.class, "backRight");

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);

    }

    @Override public void loop() {
        int TargetPosition = frontLeft.getCurrentPosition();
        frontRight.setTargetPosition(TargetPosition);
        backRight.setTargetPosition(TargetPosition);
        backLeft.setTargetPosition(TargetPosition);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        int FrontLeftPosition = frontLeft.getCurrentPosition();
        int FrontRightPosition = frontRight.getCurrentPosition();
        int BackLeftPosition = backLeft.getCurrentPosition();
        int BackRightPosition = backRight.getCurrentPosition();

        double drive  = gamepad1.left_stick_y;  // Forward/backward (inverted as necessary)
        double strafe = -gamepad1.right_stick_x;     // Left/right
        double rotate = gamepad1.left_trigger - gamepad1.right_trigger;     // Rotation

        // Calculate motor powers for mecanum drive
        double frontLeftPower  = drive + strafe + rotate;
        double frontRightPower = drive - strafe - rotate;
        double backLeftPower   = drive - strafe + rotate;
        double backRightPower  = drive + strafe - rotate;

        // Set the calculated power to each motor
        frontLeft.setVelocity(frontLeftPower/2);
        frontRight.setVelocity(-frontRightPower/2);
        backLeft.setVelocity(backLeftPower/2);
        backRight.setVelocity(-backRightPower/2);

        telemetry.addData("frontLeft", FrontLeftPosition);
        telemetry.addData("frontRight", FrontRightPosition);
        telemetry.addData("backLeft", BackLeftPosition);
        telemetry.addData("backRight", BackRightPosition);
        // Update the telemetry on the driver station
        telemetry.update();
    }
}
