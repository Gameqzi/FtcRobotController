package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

/**
 * AprilTag #17 tracker that keeps the tag centred using **only camera‑space data**—no field map.
 * <p>
 * Key tweak in this revision:
 * <ul>
 *   <li>Re‑inverted the forward/backward calculation so <b>positive power always means
 *       “drive toward the tag.”</b> This fixes the symptom you described where the robot backed up
 *       when it was far away and lunged when it was too close.</li>
 *   <li>Lowered <code>MAX_DRIVE</code> to tame the maximum forward/backward speed.  Increase or
 *       decrease this cap as needed.</li>
 * </ul>
 */
@TeleOp(name = "AprilTagCenterRotate", group = "Concept")
public class AprilTagCenterRotate extends OpMode {

    // ==================== Hardware ====================
    private DcMotor lf, rf, lr, rr;

    // ==================== Tunables ====================
    private static final int    TARGET_TAG_ID = 17;          // 36h11 family

    private static final double CAM_WIDTH_PX  = 640;
    private static final double CAM_HEIGHT_PX = 480;

    /** Desired tag width in pixels at the ideal stand‑off distance. */
    private static final double DESIRED_TAG_WIDTH_PX = 110;  // calibrate!

    // Gains (tweak to taste)
    private static final double KP_ROTATE  = 0.003;  // px  → power (rotation)
    private static final double KP_STRAFE  = 0.004;  // px  → power (strafe)
    private static final double KP_FORWARD = 0.004;  // px  → power (forward)

    // Power caps
    private static final double MAX_ROTATE = 0.40;
    private static final double MAX_DRIVE  = 0.35;  // ↓ from 0.50 to soften lurches

    // Pixel thresholds
    private static final double ROTATE_DEAD_BAND_PX  = 15;
    private static final double STRAFE_TRIGGER_PX    = 4;

    // ==================================================
    private VisionPortal visionPortal;
    private AprilTagProcessor tagProcessor;

    // -------------------- Init --------------------
    @Override public void init() {
        lf = hardwareMap.get(DcMotor.class, "frontLeft");
        rf = hardwareMap.get(DcMotor.class, "frontRight");
        lr = hardwareMap.get(DcMotor.class, "backLeft");
        rr = hardwareMap.get(DcMotor.class, "backRight");

        // Make +power drive forward (reverse left side)
        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        lr.setDirection(DcMotorSimple.Direction.REVERSE);

        tagProcessor = AprilTagProcessor.easyCreateWithDefaults();
        visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap.get(WebcamName.class, "Webcam 1"), tagProcessor);

        telemetry.addLine("Init complete – press ▶ to run");
        telemetry.update();
    }

    // -------------------- Main loop --------------------
    @Override public void loop() {
        double rotate = 0, forward = 0, strafe = 0;

        // Locate our target tag
        AprilTagDetection target = null;
        for (AprilTagDetection det : tagProcessor.getDetections()) {
            if (det.id == TARGET_TAG_ID) { target = det; break; }
        }

        if (target != null) {
            // Pixel‑space centre errors
            double xErrPx = target.center.x - (CAM_WIDTH_PX  / 2.0);
            double yErrPx = target.center.y - (CAM_HEIGHT_PX / 2.0);

            // Tag width in pixels → distance proxy
            double tagWidthPx = Math.hypot(target.corners[1].x - target.corners[0].x,
                    target.corners[1].y - target.corners[0].y);
            double widthErrPx = DESIRED_TAG_WIDTH_PX - tagWidthPx; // + ⇒ robot too far

            // ---------- ROTATE ----------
            if (Math.abs(xErrPx) > ROTATE_DEAD_BAND_PX) {
                rotate = clip(KP_ROTATE * xErrPx, -MAX_ROTATE, MAX_ROTATE);
            }
            // ---------- STRAFE ----------
            else if (Math.abs(xErrPx) > STRAFE_TRIGGER_PX) {
                strafe = clip(KP_STRAFE * xErrPx, -MAX_DRIVE, MAX_DRIVE);
            }
            // ---------- FORWARD / BACKWARD ----------
            //   * If robot is FAR, widthErrPx > 0  → drive forward  (+ power)
            //   * If robot is CLOSE, widthErrPx < 0 → drive backward (− power)
            // The sign flip ensures correct direction regardless of motor wiring.
            forward = clip(KP_FORWARD * widthErrPx, -MAX_DRIVE, MAX_DRIVE);

            telemetry.addData("xErr", "%.1f px", xErrPx);
            telemetry.addData("yErr", "%.1f px", yErrPx);
            telemetry.addData("tagWidth", "%.1f px", tagWidthPx);
            telemetry.addData("widthErr", "%.1f px", widthErrPx);
        } else {
            telemetry.addLine("Target tag NOT in view – robot idle");
        }

        // --------------- Mecanum mixing ---------------
        double fl = forward + strafe + rotate;
        double bl = forward - strafe + rotate;
        double fr = forward - strafe - rotate;
        double br = forward + strafe - rotate;

        // Normalise
        double max = Math.max(1.0, Math.max(Math.abs(fl),
                Math.max(Math.abs(fr), Math.max(Math.abs(bl), Math.abs(br)))));
        lf.setPower(fl / max);
        lr.setPower(bl / max);
        rf.setPower(fr / max);
        rr.setPower(br / max);

        telemetry.addData("forward", "%.2f", forward);
        telemetry.addData("strafe",  "%.2f", strafe);
        telemetry.addData("rotate",  "%.2f", rotate);
        telemetry.update();
    }

    // -------------------- Shutdown --------------------
    @Override public void stop() {
        if (visionPortal != null) visionPortal.close();
    }

    private double clip(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
