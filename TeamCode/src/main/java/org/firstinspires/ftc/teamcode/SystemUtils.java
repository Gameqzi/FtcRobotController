package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.Utils.sleep;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

enum GamepadTarget {
    GAMEPAD1, GAMEPAD2, BOTH
}
enum BlinkType {
    EVEN, ODD_HIGH, ODD_LOW
}
enum BlinkAction {
    SHARP, SOFT
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

        public void advRumble(GamepadTarget Gamepad, double RumbleLeft, double RumbleRight, int Duration) {
            if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {gamepad1.rumble(RumbleLeft, RumbleRight, Duration);}
            if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {gamepad2.rumble(RumbleLeft, RumbleRight, Duration);}
        }

        public void advBlinkLED(GamepadTarget Gamepad, double R, double G, double B, int Speed, BlinkType BlinkType, BlinkAction BlinkAction, int Steps) {
            double onDuration = Speed * 0.50, offDuration = Speed * 0.50;
            if (BlinkType == BlinkType.EVEN)        {onDuration = Speed * 0.50; offDuration = Speed * 0.50;}
            if (BlinkType == BlinkType.ODD_HIGH)    {onDuration = Speed * 0.75; offDuration = Speed * 0.25;}
            if (BlinkType == BlinkType.ODD_LOW)     {onDuration = Speed * 0.25; offDuration = Speed * 0.75;}

            com.qualcomm.robotcore.hardware.Gamepad.LedEffect advBlinkLED;

            if (BlinkAction == BlinkAction.SHARP) {
                advBlinkLED = new com.qualcomm.robotcore.hardware.Gamepad.LedEffect.Builder()
                        .addStep(R, G, B, (int) onDuration)
                        .addStep(0.0, 0.0, 0.0, (int) offDuration)
                        .setRepeating(true)
                        .build();
            } else {
                advBlinkLED = createLedEffect(R, G, B, 0, 0, 0, Speed, Steps, true);
            }
            if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {gamepad1.runLedEffect(advBlinkLED);}
            if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {gamepad2.runLedEffect(advBlinkLED);}

        }

        // The amount of time, in ms, that it takes to complete one full loop.
        public void rainbowLED(GamepadTarget Gamepad, int Speed, int Steps) {

        }

        private com.qualcomm.robotcore.hardware.Gamepad.LedEffect createLedEffect(double R1, double G1, double B1, double R2, double G2, double B2, int Duration, int Steps, boolean Repeating) {
            com.qualcomm.robotcore.hardware.Gamepad.LedEffect.Builder builder = new com.qualcomm.robotcore.hardware.Gamepad.LedEffect.Builder();
            int totalIntervalsPerWay = Steps - 1;
            int baseStepDuration = Steps / totalIntervalsPerWay;
            int remainderDuration = Steps % totalIntervalsPerWay;
            baseStepDuration = Math.max(1, baseStepDuration);

            for (int i = 0; i < Steps; i++) {
                double progress = (double) i / (Steps - 1);

                double currentRed = interpolate(R1, R2, progress);
                double currentGreen = interpolate(G1, G2, progress);
                double currentBlue = interpolate(B1, B2, progress);

                int actualStepDuration = baseStepDuration;
                if (i < remainderDuration) {
                    actualStepDuration++;
                }
                builder.addStep(currentRed, currentGreen, currentBlue, actualStepDuration);
            }
            if (Repeating) {
                for (int i = 1; i < Steps; i++) {
                    double progress = (double) i / (Steps - 1);

                    double currentR = interpolate(R2, R1, progress);
                    double currentG = interpolate(G2, G1, progress);
                    double currentB = interpolate(B2, B1, progress);

                    int actualStepDuration = baseStepDuration;
                    if (i - 1 < remainderDuration) {
                        actualStepDuration++;
                    }
                    builder.addStep(currentR, currentG, currentB, actualStepDuration);
                }
            }
            return builder.setRepeating(Repeating).build();
        }

        private double interpolate(double Start, double End, double Progress) {
            return Math.max(0.0, Math.min(1.0, Start + (End - Start) * Progress));
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