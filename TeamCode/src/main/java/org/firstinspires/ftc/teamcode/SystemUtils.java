package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class SystemUtils {
    //region Global Variables:

    private Telemetry telemetry;
    private int MAX_TELEMETRY_LINES = 10;

    //endregion

    // ToDo: MAKE THIS BETTER!!!:
    //region Help Reference:
    /**
     * <strong>Soley here to list all of the functions in SystemUtils & provide a documentation key.</strong>
     * <p>
     * Things to note:<br>
     * You MUST call at LEAST "SystemUtils SystemUtils = new SystemUtils();" in order to use SystemUtils.java.
     * <br><br>SystemUtils Documentation Key:<br>
     * TypeType<br>
     * </p>
     * All SystemUtils Functions:
     * <ul>
     * <li>HelpReference();</li>
     * <li>t</li>
     * </ul>
     */
    public void HelpReference() {}
    //endregion

    //region Gamepad Functions:

    // INIT LED AT BLANK/BLACK ToDo: Make better, GPT?
    public void InitGamepadLEDController() {

    }

    //endregion

    /**
     * <strong>Sets the telemetry object for SystemUtils</strong>
     * <p>
     * ToDo: Write something useful here...
     * </p>
     *
     * <ul>
     * <li>Level:    REQUIRED</li>
     * <li>Location: MainINIT();</li>
     * </ul>
     *
     * @param telemetry The Telemetry object to associate with the script.
     * @return This TelemetryUtils instance, for method chaining.
     */
    public SystemUtils setTelemetry(Telemetry telemetry) {
        this.telemetry = telemetry;
        return this;
    }

    /**
     * Sets the maximum amount of lines that the telemetry log retains.
     * <p>
     * Thinking of a good note to put here...
     * Level: REQUIRED - DEFAULT FAILSAFE
     * </p>
     * @param MaxLines ..
     */
    public void SetMaxLogLines(int MaxLines) {
        MAX_TELEMETRY_LINES = MaxLines;
    }

    /**
     * Sets the maximum amount of lines that the telemetry log retains.
     * <p>
     * Thinking of a good note to put here...
     * Level: REQUIRED - NULL OK FAILSAFE
     * </p>
     * @param MS ..
     */
    public void SetTelemetryTransmissionRate(int MS) {
        telemetry.setMsTransmissionInterval(MS);
    }
}