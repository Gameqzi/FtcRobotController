package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.Utils.sleep;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

enum GamepadTarget {
    GAMEPAD1, GAMEPAD2, BOTH
}

public class SystemUtils {
    //region SystemUtils Global Functions:
    private Telemetry telemetry;
    private com.qualcomm.robotcore.hardware.Gamepad gamepad1;
    private com.qualcomm.robotcore.hardware.Gamepad gamepad2;

    public final Gamepad gamepad = new Gamepad();

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
     * @param telemetry The telemetry object to associate with the script.
     * @return This SystemUtils instance, for method chaining.
     */
    public SystemUtils setTelemetry(Telemetry telemetry) {
        this.telemetry = telemetry;
        return this;
    }

    /**
     * <strong>Sets the gamepad1 object for SystemUtils</strong>
     * <p>
     * ToDo: Write something useful here...
     * </p>
     *
     * <ul>
     * <li>Level:    REQUIRED</li>
     * <li>Location: MainINIT();</li>
     * </ul>
     *
     * @param gamepad1 The gamepad1 object to associate with the script.
     * @return This SystemUtils instance, for method chaining.
     */
    public SystemUtils setGamepad1(com.qualcomm.robotcore.hardware.Gamepad gamepad1) {
        this.gamepad1 = gamepad1;
        return this;
    }

    /**
     * <strong>Sets the gamepad2 object for SystemUtils</strong>
     * <p>
     * ToDo: Write something useful here...
     * </p>
     *
     * <ul>
     * <li>Level:    REQUIRED</li>
     * <li>Location: MainINIT();</li>
     * </ul>
     *
     * @param gamepad2 The gamepad2 object to associate with the script.
     * @return This SystemUtils instance, for method chaining.
     */
    public SystemUtils setGamepad2(com.qualcomm.robotcore.hardware.Gamepad gamepad2) {
        this.gamepad2 = gamepad2;
        return this;
    }
    //endregion

    // ToDo: MAKE THIS BETTER!!!:
    //region Help Reference:
    /**
     * <strong>Soley here to list all of the functions in SystemUtils & provide a documentation key.</strong>
     * <p>
     * Things to note:<br>
     * You MUST call AT LEAST "SystemUtils SystemUtils = new SystemUtils();" in order to use SystemUtils.java's functions.
     * <br><br>SystemUtils Documentation Key:<br>
     * TypeType<br>
     * </p>
     * All SystemUtils Functions:
     * <ul>
     * <li>HelpReference();</li>
     * <li>.setTelemetry();</li>
     * <li>.setGamepad1();</li>
     * <li>.setGamepad2();</li>
     * </ul>
     */
    public void helpReference() {}
    //endregion

    //region MAIN EXE FUNCTIONS:


    //subregion Gamepad Functions:
    public class Gamepad {
        private double lastGamepad1R = 0, lastGamepad1G = 0, lastGamepad1B = 0;
        private double lastGamepad2R = 0, lastGamepad2G = 0, lastGamepad2B = 0;

        /**
         * Sets RGB value,
         * @param Gamepad  The gamepad that is effected by the RGB value.
         * @param R        The RED color value. (0 - 1)
         * @param G        The GREEN color value. (0 - 1)
         * @param B        The BLUE color value. (0 - 1)
         * @param Duration How long, in ms, that the RGB effect lasts. (Set to -1 for inf)
         */
        public void setLED(GamepadTarget Gamepad, double R, double G, double B, int Duration) {
            if (Gamepad == GamepadTarget.GAMEPAD1) {
                gamepad1.setLedColor(R, G, B, Duration);
                if (Duration == -1) {lastGamepad1R = R; lastGamepad1G = G; lastGamepad1B = B;}
            } else if (Gamepad == GamepadTarget.GAMEPAD2) {
                gamepad2.setLedColor(R, G, B, Duration);
                if (Duration == -1) {lastGamepad2R = R; lastGamepad2G = G; lastGamepad2B = B;}
            } else if (Gamepad == GamepadTarget.BOTH) {
                gamepad1.setLedColor(R, G, B, Duration);
                gamepad2.setLedColor(R, G, B, Duration);
                if (Duration == -1) {lastGamepad1R = R; lastGamepad1G = G; lastGamepad1B = B; lastGamepad2R = R; lastGamepad2G = G; lastGamepad2B = B;}
            }
        }

        /**
         * Recommended: 300 - 600 ms, 20 - 40 ms
         * @param Gamepad  The gamepad that is effected by the RGB value.
         * @param R        The RED color value. (0 - 1)
         * @param G        The GREEN color value. (0 - 1)
         * @param B        The BLUE color value. (0 - 1)
         * @param Duration The how fast, in ms, that the float effect lasts.
         * @param UpdateRate The rate, in ms, at which the gamepad LED updates during the effect.
         */
        public void floatLED(GamepadTarget Gamepad, double R, double G, double B, int Duration, int UpdateRate) {
            ElapsedTime timer = new ElapsedTime();
            timer.reset();

            while (timer.milliseconds() < Duration) {
                double progress = timer.milliseconds() / Duration;

                if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {
                    double currentRed = interpolate(lastGamepad1R, R, progress);
                    double currentGreen = interpolate(lastGamepad1G, G, progress);
                    double currentBlue = interpolate(lastGamepad1B, B, progress);
                    gamepad1.setLedColor(currentRed, currentGreen, currentBlue, -1);
                }
                if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {
                    double currentRed = interpolate(lastGamepad2R, R, progress);
                    double currentGreen = interpolate(lastGamepad2G, G, progress);
                    double currentBlue = interpolate(lastGamepad2B, B, progress);
                    gamepad2.setLedColor(currentRed, currentGreen, currentBlue, -1);
                }
                sleep(UpdateRate);
            }
            if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {gamepad1.setLedColor(R, G, B, -1); lastGamepad1R = R; lastGamepad1G = G; lastGamepad1B = B;}
            if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {gamepad2.setLedColor(R, G, B, -1); lastGamepad2R = R; lastGamepad2G = G; lastGamepad2B = B;}
        }
        private double interpolate(double start, double end, double progress) {
            return Math.max(0.0, Math.min(1.0, start + (end - start) * progress));
        }

        public void advRumble(GamepadTarget Gamepad, double RumbleLeft, double RumbleRight, int Duration) {
            if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {gamepad1.rumble(RumbleLeft, RumbleRight, Duration);}
            if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {gamepad2.rumble(RumbleLeft, RumbleRight, Duration);}
        }
    }
    //endregion

    //subregion Telemetry LogFunctions:
    private int maxTelemetryLines = 10;
    /**
     * Sets the maximum amount of lines that the telemetry log retains.
     * <p>
     * Thinking of a good note to put here...
     * Level: REQUIRED - DEFAULT FAILSAFE
     * </p>
     * @param MaxLines ..
     */
    public void SetMaxLogLines(int MaxLines) {
        maxTelemetryLines = MaxLines;
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
    //endregion

    //endregion
}