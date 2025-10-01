package org.firstinspires.ftc.teamcode.OldCode;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Limelight extends OpMode {

    private TelemetryManager panels;

    private Limelight3A limelight;
    @Override
    public void init(){
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        telemetry.setMsTransmissionInterval(11);

        limelight.pipelineSwitch(0);

        /*
         * Starts polling for data.  If you neglect to call start(), getLatestResult() will return null.
         */
        limelight.start();

        panels = PanelsTelemetry.INSTANCE.getTelemetry();

        panels.addData(">", "Robot Ready.  Press Play.");
        panels.update();
    }

    @Override
    public void loop() {
        LLStatus status = limelight.getStatus();
        panels.debug("Name", "%s", status.getName());
        panels.debug("LL", "Temp: %.1fC, CPU: %.1f%%, FPS: %d", status.getTemp(), status.getCpu(),(int)status.getFps());
        panels.debug("Pipeline", "Index: %d, Type: %s", status.getPipelineIndex(), status.getPipelineType());

        LLResult result = limelight.getLatestResult();
        if (result != null) {
            double tx = result.getTx();
            double ty = result.getTy();
            if (result.isValid()) {
                panels.addData("tx", tx);
                panels.addData("ty", ty);
            }
        }
        panels.update();
    }

    @Override
    public void stop() {
        limelight.stop();
    }
}