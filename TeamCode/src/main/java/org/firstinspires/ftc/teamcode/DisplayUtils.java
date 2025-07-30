package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.*;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.threadopmode.ThreadOpMode;

import java.util.LinkedList;

@SuppressWarnings("unused") // If a function is not used in any other code, the compiler labels if as "unused"
public class DisplayUtils {

    //.init.*
    private static Telemetry SysTelemetry;
    private static Gamepad SysGamepad1;
    private static Gamepad SysGamepad2;

    //.gamepad.*
    public enum GamepadTarget {
        GAMEPAD1, GAMEPAD2, BOTH
    }
    public enum BlinkType {
        EVEN, ODD_HIGH, ODD_LOW
    }

    private static double lastGamepad1R = 0, lastGamepad1G = 0, lastGamepad1B = 0;
    private static double lastGamepad2R = 0, lastGamepad2G = 0, lastGamepad2B = 0;

    //.telemetry.*
    private static final LinkedList<TelemetryEntry> telemetryBuffer = new LinkedList<>();
    private static int maxLogLines = 15; // 15 default
    private static boolean logVisible = false;
    private static boolean autoDisplayLog = true; // TODO: True by default

    private static class TelemetryEntry {
        final String key;
        String value;

        TelemetryEntry(String message) {
            this.key   = null;
            this.value = message;
        }

        boolean isPlain() { return key == null; }
    }

    //endregion

    //region DisplayUtils.helpReference:
     /**
      * -----------------------------------For best readability, please expand this JavaDoc window so you can view this sentence on a single line-----------------------------------
      * <br>
      * <h1>DisplayUtils.java functions list and documentation key (helpReference)</h1>
      * <p>
      * <strong>Things to note:</strong><br>
      * You must call one or more of the following from your OpMode’s <code>mainInit()</code> method to use DisplayUtils features:
      * <code>DisplayUtils.init.initTelemetry(telemetry)</code>, <code>initGamepad1(gamepad1)</code>, or <code>initGamepad2(gamepad2)</code>.
      * </p>
      * <br>
      * <h3>DisplayUtils Documentation Key:</h3>
      * <p>
      * <strong>[Short Explanation]</strong><br>
      * [Expanded Description]<br>
      * Dependencies (List)<br>
      * - Parameter(s)<br>
      * - Return Value (If any)
      * </p>
      * <br>
      * <h3>DisplayUtils Functions List:</h3>
      * <strong>All Public DisplayUtils Functions:</strong>
      * <ul>
      *     <li><code>DisplayUtils.helpReference();</code></li>
      *     <br>
      *     <li><code>DisplayUtils.init.initTelemetry(telemetry);</code></li>
      *     <li><code>DisplayUtils.init.initGamepad1(gamepad1);</code></li>
      *     <li><code>DisplayUtils.init.initGamepad2(gamepad2);</code></li>
      *     <li><code>DisplayUtils.init.setTelemetryTransmissionRate(milliseconds);</code></li>
      *     <br>
      *     <li><code>DisplayUtils.gamepad.led.setLED(GamepadTarget, r, g, b, duration);</code></li>
      *     <li><code>DisplayUtils.gamepad.led.floatLED(GamepadTarget, r, g, b, speed, resolution);</code></li>
      *     <li><code>DisplayUtils.gamepad.led.sharpBlinkLED(GamepadTarget, r1, g1, b1, r2, g2, b2, speed, blinkType);</code></li>
      *     <li><code>DisplayUtils.gamepad.led.softPulseLED(GamepadTarget, r1, g1, b1, r2, g2, b2, speed, resolution, blinkType);</code></li>
      *     <li><code>DisplayUtils.gamepad.led.rainbowLED(GamepadTarget, speed, resolution);</code></li>
      *     <br>
      *     <li><code>DisplayUtils.gamepad.rumble.advRumble(GamepadTarget, rumbleLeft, rumbleRight, duration);</code></li>
      *     <br>
      *     <li><code>DisplayUtils.telemetry.menu.createMenu(menuId);</code></li>
      *     <li><code>DisplayUtils.telemetry.menu.removeMenu(menuId);</code></li>
      *     <li><code>DisplayUtils.telemetry.menu.addMenuItem(menuId, itemName);</code></li>
      *     <li><code>DisplayUtils.telemetry.menu.addMenuItem(menuId, itemName, itemVariable);</code></li>
      *     <li><code>DisplayUtils.telemetry.menu.addMenuItem(menuId, itemName, itemVariable, defaultVal);</code></li>
      *     <li><code>DisplayUtils.telemetry.menu.removeMenuItem(menuId, itemName);</code></li>
      *     <li><code>DisplayUtils.telemetry.menu.addMenuData(menuId, caption, dataVariable);</code></li>
      *     <li><code>DisplayUtils.telemetry.menu.clearMenuData(menuId);</code></li>
      *     <br>
      *     <li><code>DisplayUtils.telemetry.log.showLog(visible);</code></li>
      *     <li><code>DisplayUtils.telemetry.log.setAutoDisplay(displayAfterMenu);</code></li>
      *     <li><code>DisplayUtils.telemetry.log.setMaxLines(maxLines);</code></li>
      *     <li><code>DisplayUtils.telemetry.log.addLine(message);</code></li>
      *     <li><code>DisplayUtils.telemetry.log.throwSoftError(object, error, gamepadNotice);</code></li>
      *     <li><code>DisplayUtils.telemetry.log.throwHardError(object, error, safeShutdown);</code></li>
      *     <li><code>DisplayUtils.telemetry.log.clearLog(displayClearEvent);</code></li>
      * </ul>
      * <br>
      * <strong>All Internal DisplayUtils Functions:</strong>
      * <ul>
      *     <li><code>Gamepad.LedEffect LEDSmoothTransition(r1, g1, b1, r2, g2, b2, speed, resolution).setRepeating(repeating).build();</code></li>
      *     <li><code>double interpolate(start, end, progress);</code></li>
      * </ul>
      * <br><br><br>
      */
    public static void helpReference() {}
    //endregion

    //region DisplayUtils.init.*
    public static class init {

        public static void initTelemetry(Telemetry telemetry) { // Required
            SysTelemetry = telemetry;
            logVisible = true;
        }

        public static void initGamepad1(Gamepad gamepad) { // Optional, Required for .gamepad (1) functions
            SysGamepad1 = gamepad;
            SysGamepad1.setLedColor(0, 0, 0, -1); // Sets to black as default
            lastGamepad1R = 0; lastGamepad1G = 0; lastGamepad1B = 0;
        }

        public static void initGamepad2(Gamepad gamepad) { // Optional, Required for .gamepad (2) functions
            SysGamepad2 = gamepad;
            SysGamepad2.setLedColor(0, 0, 0, -1); // Sets to black as default
            lastGamepad2R = 0; lastGamepad2G = 0; lastGamepad2B = 0;
        }

        public static void setTelemetryTransmissionRate(int milliseconds) {
            SysTelemetry.setMsTransmissionInterval(milliseconds);
        }
    }
    //endregion

    //region DisplayUtils.gamepad.*
    public static class gamepad {

        //region DisplayUtils.gamepad.led.*
        public static class led {

            // ToDo: Note: Set Duration to -1 for inf
            public static void setLED(GamepadTarget Gamepad, double r, double g, double b, int Duration) {
                if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {
                    SysGamepad1.setLedColor(r, g, b, Duration);
                    if (Duration == -1) {lastGamepad1R = r; lastGamepad1G = g; lastGamepad1B = b;}
                }
                if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {
                    SysGamepad2.setLedColor(r, g, b, Duration);
                    if (Duration == -1) {lastGamepad2R = r; lastGamepad2G = g; lastGamepad2B = b;}
                }
            }

            public static void floatLED(GamepadTarget Gamepad, double r, double g, double b, int speed, int resolution) { // TODO: Add a notice: Speed/Res = MS per step, recommended: 10 - 40 ms per step (usually Res = Speed/10 is good)
                if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {
                    Gamepad.LedEffect GP1_Effect = LEDSmoothTransition(lastGamepad1R, lastGamepad1G, lastGamepad1B, r, g, b, speed, resolution).setRepeating(false).build();
                    SysGamepad1.runLedEffect(GP1_Effect);
                    lastGamepad1R = r; lastGamepad1G = g; lastGamepad1B = b;
                }

                if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {
                    Gamepad.LedEffect GP2_Effect = LEDSmoothTransition(lastGamepad2R, lastGamepad2G, lastGamepad2B, r, g, b, speed, resolution).setRepeating(false).build();
                    SysGamepad2.runLedEffect(GP2_Effect);
                    lastGamepad2R = r; lastGamepad2G = g; lastGamepad2B = b;
                }
            }

            // ToDo: Note: less precise bcs of required double to int conversion
            public static void sharpBlinkLED(GamepadTarget Gamepad, double r1, double g1, double b1, double r2, double g2, double b2 , int speed, BlinkType blinkType) {
                double onDuration = speed * 0.50, offDuration = speed * 0.50;
                if (blinkType == DisplayUtils.BlinkType.EVEN)        {onDuration = speed * 0.50; offDuration = speed * 0.50;}
                if (blinkType == DisplayUtils.BlinkType.ODD_HIGH)    {onDuration = speed * 0.75; offDuration = speed * 0.25;}
                if (blinkType == DisplayUtils.BlinkType.ODD_LOW)     {onDuration = speed * 0.25; offDuration = speed * 0.75;}

                Gamepad.LedEffect.Builder SBLED_Builder = new Gamepad.LedEffect.Builder();

                SBLED_Builder.addStep(r1, g1, b1, (int) onDuration);
                SBLED_Builder.addStep(r2, g2, b2, (int) offDuration);

                if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {
                    Gamepad.LedEffect GP1_Effect = SBLED_Builder.setRepeating(true).build();
                    SysGamepad1.runLedEffect(GP1_Effect);
                }
                if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {
                    Gamepad.LedEffect GP2_Effect = SBLED_Builder.setRepeating(true).build();
                    SysGamepad1.runLedEffect(GP2_Effect);
                }
            }

            // @throws IllegalArgumentException if ...
            // ToDo: Note: less precise bcs of required double to int conversion
            public static void softPulseLED(GamepadTarget Gamepad, double r1, double g1, double b1, double r2, double g2, double b2, int speed, int resolution, BlinkType blinkType) {
                if (resolution <= 0) {throw new IllegalArgumentException("[DisplayUtils.gamepad.led.advBlinkLED]: <ERROR> When calculating smooth LED transition, precation caught DIVIDE BY ZERO (Variable: 'resolution' <= 0)!");}

                int step;

                double onDuration = speed * 0.50, offDuration = speed * 0.50;
                if (blinkType == DisplayUtils.BlinkType.EVEN)        {onDuration = speed * 0.50; offDuration = speed * 0.50;}
                if (blinkType == DisplayUtils.BlinkType.ODD_HIGH)    {onDuration = speed * 0.75; offDuration = speed * 0.25;}
                if (blinkType == DisplayUtils.BlinkType.ODD_LOW)     {onDuration = speed * 0.25; offDuration = speed * 0.75;}

                int res = resolution / 2;
                step = (int) onDuration / res;

                Gamepad.LedEffect.Builder SPLED_Builder = new Gamepad.LedEffect.Builder();
                double currentR = 0, currentG = 0, currentB = 0;

                for (int i = 0; i <= res + 1; i++) {
                    double progress = (double) i / res;

                    currentR = interpolate(r2, r1, progress);
                    currentG = interpolate(g2, g1, progress);
                    currentB = interpolate(b2, b1, progress);
                    SPLED_Builder.addStep(currentR, currentG, currentB, step);
                }

                step = (int) offDuration / res;

                for (int i = 0; i <= res + 1; i++) {
                    double progress = (double) i / res;

                    currentR = interpolate(r1, r2, progress);
                    currentG = interpolate(g1, g2, progress);
                    currentB = interpolate(b1, b2, progress);
                    SPLED_Builder.addStep(currentR, currentG, currentB, step);
                }
                SPLED_Builder.addStep(currentR, currentG, currentB, step * 5);

                if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {
                    Gamepad.LedEffect GP1_Effect = SPLED_Builder.setRepeating(true).build();
                    SysGamepad1.runLedEffect(GP1_Effect);
                }
                if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {
                    Gamepad.LedEffect GP2_Effect = SPLED_Builder.setRepeating(true).build();
                    SysGamepad1.runLedEffect(GP2_Effect);
                }
            }

            // @throws IllegalArgumentException if ...
            public static void rainbowLED(GamepadTarget Gamepad, int speed, int resolution) {
                if (resolution <= 0) {throw new IllegalArgumentException("[DisplayUtils.gamepad.led.rainbowLED]: <ERROR> When calculating smooth LED transition, precation caught DIVIDE BY ZERO (Variable: 'resolution' <= 0)!");}

                int res = resolution / 3;
                int step = (speed / 3) / res;

                Gamepad.LedEffect.Builder RBLED_Builder = new Gamepad.LedEffect.Builder();
                double currentR = 0, currentG = 0, currentB = 0;

                for (int i = 0; i <= res + 1; i++) {
                    double progress = (double) i / res;

                    currentR = interpolate(0, 1, progress);
                    currentG = interpolate(0, 0, progress);
                    currentB = interpolate(1, 0, progress);
                    RBLED_Builder.addStep(currentR, currentG, currentB, step);
                }
                for (int i = 0; i <= res + 1; i++) {
                    double progress = (double) i / res;

                    currentR = interpolate(1, 0, progress);
                    currentG = interpolate(0, 1, progress);
                    currentB = interpolate(0, 0, progress);
                    RBLED_Builder.addStep(currentR, currentG, currentB, step);
                }
                for (int i = 0; i <= res + 1; i++) {
                    double progress = (double) i / res;

                    currentR = interpolate(0, 0, progress);
                    currentG = interpolate(1, 0, progress);
                    currentB = interpolate(0, 1, progress);
                    RBLED_Builder.addStep(currentR, currentG, currentB, step);
                }
                RBLED_Builder.addStep(currentR, currentG, currentB, step * 5);

                if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {
                    Gamepad.LedEffect GP1_Effect = RBLED_Builder.setRepeating(true).build();
                    SysGamepad1.runLedEffect(GP1_Effect);
                }
                if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {
                    Gamepad.LedEffect GP2_Effect = RBLED_Builder.setRepeating(true).build();
                    SysGamepad1.runLedEffect(GP2_Effect);
                }
            }

            // @throws IllegalArgumentException if ...
            private static Gamepad.LedEffect.Builder LEDSmoothTransition(double R1, double G1, double B1, double R2, double G2, double B2, int speed, int resolution) {
                if (resolution <= 0) {throw new IllegalArgumentException("[DisplayUtils.gamepad.led.LEDSmoothTransition]: <ERROR> When calculating smooth LED transition, precation caught DIVIDE BY ZERO (Variable: 'resolution' <= 0)!");}

                int step = speed / resolution;

                Gamepad.LedEffect.Builder LEDST_Builder = new Gamepad.LedEffect.Builder();

                for (int i = 0; i <= resolution + 1; i++) {
                    double progress = (double) i / resolution;

                    double currentR = interpolate(R1, R2, progress);
                    double currentG = interpolate(G1, G2, progress);
                    double currentB = interpolate(B1, B2, progress);
                    LEDST_Builder.addStep(currentR, currentG, currentB, step);
                }
                LEDST_Builder.addStep(R2, G2, B2, Integer.MAX_VALUE);

                return LEDST_Builder;
            }

            private static double interpolate(double start, double end, double progress) {
                return Math.max(0.0, Math.min(1.0, start + (end - start) * progress));
            }
        }
        //endregion

        //region DisplayUtils.gamepad.rumble.*
        public static class rumble {

            public static void advRumble(GamepadTarget Gamepad, double rumbleLeft, double rumbleRight, int duration) {
                if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {
                    DisplayUtils.SysGamepad1.rumble(rumbleLeft, rumbleRight, duration);}
                if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {
                    DisplayUtils.SysGamepad2.rumble(rumbleLeft, rumbleRight, duration);}
            }
        }
        //endregion
    }
    //endregion

    //region DisplayUtils.telemetry.*
    public static class telemetry {

        //region DisplayUtils.telemetry.menu.*
        public static class menu {

        }
        //endregion

        //region DisplayUtils.telemetry.log.*
        public static class log {

            public static void showLog(boolean visible) {
                logVisible = visible;
                updateLog();
            }

            public static void setMaxLines(int maxLines) {
                maxLogLines = maxLines;
                updateLog();
            }

            public static void setAutoDisplay(boolean displayAfterMenu) { // TODO: REMEMBER ME!!!
                autoDisplayLog = displayAfterMenu;
                updateLog();
            }

            public static void addLine(String message) {
                if (telemetryBuffer.size() == maxLogLines) {
                    telemetryBuffer.removeFirst();
                }

                telemetryBuffer.addLast(new TelemetryEntry(message));

                updateLog();
            }

            public static void throwSoftError(String object, String error, boolean gamepadNotice) {
                addLine("[" + object + "]: <ERROR> [SOFT] " + error);

                if (gamepadNotice) {
                    gamepad.rumble.advRumble(GamepadTarget.BOTH, 0.3, 0.3, 1000);
                    gamepad.led.sharpBlinkLED(GamepadTarget.BOTH, 1, 0, 0, 0, 0, 0, 1000, BlinkType.EVEN);
                }
            }

            public static void throwHardError(String object, String error, boolean safeShutdown) {
                addLine("[" + object + "] <ERROR> [HARD] " + error);

                if (safeShutdown) {
                    addLine("SafeShutdown enabled, shutting down...");
                    ThreadOpMode.activeInstance.requestAutoOpModeStop();
                } else {
                    addLine("SafeShutdown disabled, throwing runtime exception...");
                    throw new RuntimeException("[" + object + "] <ERROR> [HARD] " + error);
                }
            }

            public static void clearLog(boolean displayClearEvent) {
                addLine("[Clearing Log...]");
                telemetryBuffer.clear();

                if (displayClearEvent) {
                    addLine("[Log Cleared]");
                } else {
                    updateLog();
                }
            }

            private static void updateLog() {
                SysTelemetry.setAutoClear(true);
                SysTelemetry.clearAll();
                for (TelemetryEntry entry : telemetryBuffer) {
                    SysTelemetry.addLine(entry.value);
                }
                if (logVisible) {SysTelemetry.update();}
            }
        }
        //endregion
    }
    //endregion
}

// softError(...);
// region OLD STUFFS


/*

// .gamepad.LED/Rumble.~~~

public class DisplayUtils {
    //region DisplayUtils Global Functions:
    private static Telemetry telemetry;
    private static com.qualcomm.robotcore.hardware.Gamepad gamepad1;
    private static com.qualcomm.robotcore.hardware.Gamepad gamepad2;

    /*
    /**
     * <strong>Sets the telemetry object for DisplayUtils</strong>
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
     *

        // TODO: Add custom telemetry, then add it here: "[setupSystemUtils]: Waiting on Gamepad(s) to init..."
        // TODO: ALSO: Add an addTelemetryDebug(String Function, String Message); --> Outputs: "[Function]: Message"
        // TODO: ALSO ALSO: Add an addTelemetryStatus(String Status); --> Outputs: "[CurrentMS STATUS]: Status"

        // TODO: Add a "Complete/Done Setting up" message here. WITH: GamepadID: gamepad1.getGamepadId()

    public static void setupSystemUtils(Telemetry telemetry, com.qualcomm.robotcore.hardware.Gamepad gamepad1, com.qualcomm.robotcore.hardware.Gamepad gamepad2) {
        DisplayUtils.telemetry = telemetry;
        DisplayUtils.gamepad1 = gamepad1;
        DisplayUtils.gamepad2 = gamepad2;
    }

    //endregion

    //region Help Reference:
    /**
     * <strong>Soley here to list all of the functions in DisplayUtils & provide a documentation key.</strong>
     * <p>
     * Things to note:<br>
     * You MUST call AT LEAST "DisplayUtils.initialize(gamepad1, gamepad2, telemetry);" in your OpMode's runOpMode() to use DisplayUtils.java's functions.
     * <br><br>DisplayUtils Documentation Key:<br>
     * TypeType<br>
     * </p>
     * All DisplayUtils Callable Functions:
     * <ul>
     * <li>HelpReference();</li>
     * </ul>
     * All Internal DisplayUtils Functions:
     * <ul>
     * <li></li>
     * </ul>
     *
    public void helpReference() {}
    //endregion

    //region MAIN EXE FUNCTIONS:

    //subregion Gamepad Functions:
    public class UtilsGamepad {
        private double lastGamepad1R = 0, lastGamepad1G = 0, lastGamepad1B = 0;
        private double lastGamepad2R = 0, lastGamepad2G = 0, lastGamepad2B = 0;

        /**
         * Sets RGB value,
         * @param Gamepad  The gamepad that is effected by the RGB value.
         * @param R        The RED color value. (0 - 1)
         * @param G        The GREEN color value. (0 - 1)
         * @param B        The BLUE color value. (0 - 1)
         * @param Duration How long, in ms, that the RGB effect lasts. (Set to -1 for inf)
         *
        public void setLED(GamepadTarget Gamepad, double R, double G, double B, int Duration) {
            if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {
                DisplayUtils.gamepad1.setLedColor(R, G, B, Duration);
                if (Duration == -1) {lastGamepad1R = R; lastGamepad1G = G; lastGamepad1B = B;}
            }
            if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {
                DisplayUtils.gamepad2.setLedColor(R, G, B, Duration);
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
         *
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
            if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {DisplayUtils.gamepad1.rumble(RumbleLeft, RumbleRight, Duration);}
            if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {DisplayUtils.gamepad2.rumble(RumbleLeft, RumbleRight, Duration);}
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
            if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {DisplayUtils.gamepad1.runLedEffect(advBlinkLED_Effect);}
            if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {DisplayUtils.gamepad2.runLedEffect(advBlinkLED_Effect);}

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
            if (Gamepad == GamepadTarget.GAMEPAD1 || Gamepad == GamepadTarget.BOTH) {DisplayUtils.gamepad1.runLedEffect(rainbowEffect);}
            if (Gamepad == GamepadTarget.GAMEPAD2 || Gamepad == GamepadTarget.BOTH) {DisplayUtils.gamepad2.runLedEffect(rainbowEffect);}
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
            if (Steps <= 0) {throw new IllegalArgumentException("DisplayUtils.java: <ERROR> When calculating smooth LED transition, precation caught DIVIDE BY ZERO (Var: 'Steps' <= 0)!");}

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
         *
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
         *
        public static void SetTelemetryTransmissionRate(int MS) {
            DisplayUtils.telemetry.setMsTransmissionInterval(MS);
        }
    }
    //endregion

    //endregion
}

 */
// endregion