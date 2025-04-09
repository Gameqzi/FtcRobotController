package org.firstinspires.ftc.teamcode;

import static android.os.SystemClock.sleep;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;

@Config
@Autonomous
public class ColorDetect extends OpMode {
    private ColorSensor colorSensor;
    private CRServo Servo1;
    private CRServo Servo2;
    boolean Dir = true;

    @Override
    public void init() {
        colorSensor = hardwareMap.colorSensor.get("ColorSensor");
        Servo1 = hardwareMap.get(CRServo.class, "Servo1");
        Servo2 = hardwareMap.get(CRServo.class, "Servo2");
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    public void loop() {
        float red = colorSensor.red();
        float green = colorSensor.green();
        float blue = colorSensor.blue();

        telemetry.addData("Red", red);
        telemetry.addData("Green", green);
        telemetry.addData("Blue", blue);
        telemetry.update();

        Intake();

        if (blue > 45) {
            Dir = false;
            sleep(1000);
            Dir = true;
        }
    }

    private void Intake() {
        if (Dir == true) {
            Servo1.setPower(0.1);
            Servo2.setPower(-0.1);
        }
        else if (Dir == false) {
            Servo1.setPower(-0.1);
            Servo2.setPower(0.1);
        }
    }
}
