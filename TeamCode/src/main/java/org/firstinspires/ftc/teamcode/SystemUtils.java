package org.firstinspires.ftc.teamcode;

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
    private static Telemetry telemetry;
    private static com.qualcomm.robotcore.hardware.Gamepad gamepad1;
    private static com.qualcomm.robotcore.hardware.Gamepad gamepad2;

    public static final UtilsGamepad gamepad = new UtilsGamepad();
    /*
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
     */

        // TODO: Add custom telemetry, then add it here: "[setupSystemUtils]: Waiting on Gamepad(s) to init..."
        // TODO: ALSO: Add an addTelemetryDebug(String Function, String Message); --> Outputs: "[Function]: Message"
        // TODO: ALSO ALSO: Add an addTelemetryStatus(String Status); --> Outputs: "[CurrentMS STATUS]: Status"

        // TODO: Add a "Complete/Done Setting up" message here. WITH: GamepadID: gamepad1.getGamepadId()

    public static void setupSystemUtils(Telemetry telemetry, com.qualcomm.robotcore.hardware.Gamepad gamepad1, com.qualcomm.robotcore.hardware.Gamepad gamepad2) {
        SystemUtils.telemetry = telemetry;
        SystemUtils.gamepad1 = gamepad1;
        SystemUtils.gamepad2 = gamepad2;
    }

    //endregion

    //region Help Reference:
    /**
     * <strong>Soley here to list all of the functions in SystemUtils & provide a documentation key.</strong>
     * <p>
     * Things to note:<br>
     * You MUST call AT LEAST "SystemUtils.initialize(gamepad1, gamepad2, telemetry);" in your OpMode's runOpMode() to use SystemUtils.java's functions.
     * <br><br>SystemUtils Documentation Key:<br>
     * TypeType<br>
     * </p>
     * All SystemUtils Callable Functions:
     * <ul>
     * <li>HelpReference();</li>
     * </ul>
     * All Internal SystemUtils Functions:
     * <ul>
     * <li></li>
     * </ul>
     */
    public static void helpReference() {}
    //endregion

    //region MAIN EXE FUNCTIONS:

    //subregion Gamepad Functions:
    public static class UtilsGamepad {
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
            if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {
                SystemUtils.gamepad1.setLedColor(R, G, B, Duration);
                if (Duration == -1) {lastGamepad1R = R; lastGamepad1G = G; lastGamepad1B = B;}
            }
            if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {
                SystemUtils.gamepad2.setLedColor(R, G, B, Duration);
                if (Duration == -1) {lastGamepad2R = R; lastGamepad2G = G; lastGamepad2B = B;}
            }
        }

        /**
         * Recommended: 300 - 600 ms, 20 - 40 ms
         * @param Gamepad  The gamepad that is effected by the RGB value.
         * @param R        The RED color value. (0 - 1)
         * @param G        The GREEN color value. (0 - 1)
         * @param B        The BLUE color value. (0 - 1)
         * @param Duration How fast, in ms, that the float effect lasts.
         * @param Steps How many times the LED will update during the change.
         */
        public void floatLED(GamepadTarget Gamepad, double R, double G, double B, int Duration, int Steps) {
            if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {
                gamepad1.setLedColor(R, G, B, -1);
                lastGamepad1R = R; lastGamepad1G = G; lastGamepad1B = B;
                com.qualcomm.robotcore.hardware.Gamepad.LedEffect GP1_Effect = LEDSmoothTransition(lastGamepad1R, lastGamepad1G, lastGamepad1B, R, G, B, Duration, Steps);
                gamepad1.runLedEffect(GP1_Effect);
            }

            if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {
                gamepad2.setLedColor(R, G, B, -1);
                lastGamepad2R = R; lastGamepad2G = G; lastGamepad2B = B;
                com.qualcomm.robotcore.hardware.Gamepad.LedEffect GP2_Effect = LEDSmoothTransition(lastGamepad2R, lastGamepad2G, lastGamepad2B, R, G, B, Duration, Steps);
                gamepad2.runLedEffect(GP2_Effect);
            }
        }

        public void advRumble(GamepadTarget Gamepad, double RumbleLeft, double RumbleRight, int Duration) {
            if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {SystemUtils.gamepad1.rumble(RumbleLeft, RumbleRight, Duration);}
            if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {SystemUtils.gamepad2.rumble(RumbleLeft, RumbleRight, Duration);}
        }

        // FIXME: DO NOT USE, WIP!
        @Deprecated
        public void advBlinkLED(GamepadTarget Gamepad, double R, double G, double B, int Speed, BlinkType BlinkType, BlinkAction BlinkAction, int Steps) {
            double onDuration = Speed * 0.50, offDuration = Speed * 0.50;
            if (BlinkType == BlinkType.EVEN)        {onDuration = Speed * 0.50; offDuration = Speed * 0.50;}
            if (BlinkType == BlinkType.ODD_HIGH)    {onDuration = Speed * 0.75; offDuration = Speed * 0.25;}
            if (BlinkType == BlinkType.ODD_LOW)     {onDuration = Speed * 0.25; offDuration = Speed * 0.75;}

            com.qualcomm.robotcore.hardware.Gamepad.LedEffect advBlinkLED_Effect;

            if (BlinkAction == BlinkAction.SHARP) {
                advBlinkLED_Effect = new com.qualcomm.robotcore.hardware.Gamepad.LedEffect.Builder()
                        .addStep(R, G, B, (int) onDuration)
                        .addStep(0.0, 0.0, 0.0, (int) offDuration)
                        .setRepeating(true)
                        .build();
            } else { // ELSE: BlinkAction.SOFT
                int oneWayDuration = Speed / 2;
                advBlinkLED_Effect = createLedEffect(R, G, B, 0, 0, 0, oneWayDuration, Steps, true);
            }
            if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {SystemUtils.gamepad1.runLedEffect(advBlinkLED_Effect);}
            if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {SystemUtils.gamepad2.runLedEffect(advBlinkLED_Effect);}

        }

        // FIXME: DO NOT USE, WIP!
        @Deprecated
        public void rainbowLED(GamepadTarget Gamepad, int Speed, int Steps) {
            com.qualcomm.robotcore.hardware.Gamepad.LedEffect.Builder builder = new com.qualcomm.robotcore.hardware.Gamepad.LedEffect.Builder();

            int totalIntervals = Steps;
            // The total Speed duration divided by the number of steps to determine base duration per step
            int baseStepDuration = Speed / totalIntervals;
            int remainderDuration = Speed % totalIntervals;
            baseStepDuration = Math.max(1, baseStepDuration); // Ensure minimum 1ms duration

            for (int i = 0; i < Steps; i++) { // Loop through steps to generate colors
                double progress = (double) i / Steps; // Progress from 0.0 to 1.0

                // --- REPLACE THIS WITH YOUR ACTUAL RAINBOW COLOR INTERPOLATION LOGIC ---
                // This is a simple example for demonstration:
                double currentR = 0.0, currentG = 0.0, currentB = 0.0;
                if (progress < 1.0/6.0) { // Red to Yellow
                    currentR = 1.0; currentG = progress * 6.0;
                } else if (progress < 2.0/6.0) { // Yellow to Green
                    currentR = 1.0 - (progress * 6.0 - 1.0); currentG = 1.0;
                } else if (progress < 3.0/6.0) { // Green to Cyan
                    currentG = 1.0; currentB = progress * 6.0 - 2.0;
                } else if (progress < 4.0/6.0) { // Cyan to Blue
                    currentG = 1.0 - (progress * 6.0 - 3.0); currentB = 1.0;
                } else if (progress < 5.0/6.0) { // Blue to Magenta
                    currentR = progress * 6.0 - 4.0; currentB = 1.0;
                } else { // Magenta to Red
                    currentR = 1.0; currentB = 1.0 - (progress * 6.0 - 5.0);
                }
                // --- END RAINBOW COLOR INTERPOLATION ---

                int actualStepDuration = baseStepDuration;
                if (i < remainderDuration) {
                    actualStepDuration++; // Distribute leftover milliseconds
                }

                builder.addStep(
                        interpolate(0,1,currentR), // Use interpolate to clip colors (0.0 to 1.0)
                        interpolate(0,1,currentG),
                        interpolate(0,1,currentB),
                        actualStepDuration
                );
            }
            com.qualcomm.robotcore.hardware.Gamepad.LedEffect rainbowEffect = builder.setRepeating(true).build();
            if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {SystemUtils.gamepad1.runLedEffect(rainbowEffect);}
            if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {SystemUtils.gamepad2.runLedEffect(rainbowEffect);}
        }

        // FIXME: DO NOT USE, BROKEN & UNUSED!
        @Deprecated
        private com.qualcomm.robotcore.hardware.Gamepad.LedEffect createLedEffect(double R1, double G1, double B1, double R2, double G2, double B2, int Duration, int Steps, boolean Repeating) {

            com.qualcomm.robotcore.hardware.Gamepad.LedEffect.Builder builder = new com.qualcomm.robotcore.hardware.Gamepad.LedEffect.Builder();

            int totalIntervalsPerWay = Steps - 1;
            int baseStepDuration = Duration / totalIntervalsPerWay;
            int remainderDuration = Duration % totalIntervalsPerWay;
            baseStepDuration = Math.max(1, baseStepDuration); // Ensure minimum 1ms duration

            // 1. Fade from Color1 to Color2
            for (int i = 0; i < Steps; i++) {
                double progress = (double) i / totalIntervalsPerWay;

                double currentRed = interpolate(R1, R2, progress);
                double currentGreen = interpolate(G1, G2, progress);
                double currentBlue = interpolate(B1, B2, progress);

                int actualStepDuration = baseStepDuration;
                if (i < remainderDuration) {
                    actualStepDuration++; // Distribute leftover milliseconds
                }
                builder.addStep(currentRed, currentGreen, currentBlue, actualStepDuration);
            }

            // 2. Fade from Color2 back to Color1 (if repeating)
            if (Repeating) {
                for (int i = 1; i < Steps; i++) { // Start from 1 to avoid duplicating the middle step
                    double progress = (double) i / totalIntervalsPerWay;

                    double currentR = interpolate(R2, R1, progress);
                    double currentG = interpolate(G2, G1, progress);
                    double currentB = interpolate(B2, B1, progress);

                    int actualStepDuration = baseStepDuration;
                    if (i - 1 < remainderDuration) { // Adjust index for remainder distribution in second loop
                        actualStepDuration++;
                    }
                    builder.addStep(currentR, currentG, currentB, actualStepDuration);
                }
            }
            return builder.setRepeating(Repeating).build();
        }

        private com.qualcomm.robotcore.hardware.Gamepad.LedEffect LEDSmoothTransition(double R1, double G1, double B1, double R2, double G2, double B2, int Duration, int Steps) {
            if (Steps <= 0) {throw new IllegalArgumentException("SystemUtils.java: <ERROR> When calculating smooth LED transition, precation caught DIVIDE BY ZERO (Var: 'Steps' <= 0)!");}

            int Step = Duration / Steps;

            com.qualcomm.robotcore.hardware.Gamepad.LedEffect.Builder LEDST_Builder = new com.qualcomm.robotcore.hardware.Gamepad.LedEffect.Builder();

            for (int i = 0; i < Steps; i++) {
                double progress = (double) i / Steps;

                double currentR = interpolate(R1, R2, progress);
                double currentG = interpolate(G1, G2, progress);
                double currentB = interpolate(B1, B2, progress);
                LEDST_Builder.addStep(currentR, currentG, currentB, Step);
            }

            return LEDST_Builder.setRepeating(false).build();
        }

        private double interpolate(double Start, double End, double Progress) {
            return Math.max(0.0, Math.min(1.0, Start + (End - Start) * Progress));
        }
    }
    //endregion

    //subregion Telemetry Log Functions:
    public static class TelemetryLog {
        private static int maxTelemetryLines = 10;

        /**
         * Sets the maximum amount of lines that the telemetry log retains.
         * <p>
         * Thinking of a good note to put here...
         * Level: REQUIRED - DEFAULT FAILSAFE (10)
         * </p>
         *
         * @param MaxLines ..
         */
        public static void SetMaxLogLines(int MaxLines) {
            maxTelemetryLines = MaxLines;
        }

        /**
         * Sets the maximum amount of lines that the telemetry log retains.
         * <p>
         * Thinking of a good note to put here...
         * Level: REQUIRED - NULL OK FAILSAFE
         * </p>
         *
         * @param MS ..
         */
        public static void SetTelemetryTransmissionRate(int MS) {
            SystemUtils.telemetry.setMsTransmissionInterval(MS);
        }
    }
    //endregion

    //endregion
}