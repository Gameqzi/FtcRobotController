package org.firstinspires.ftc.teamcode.OldCode;

public class Utils {
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }

    /**
     * Normalizes an angle to the range [-180, 180) degrees.
     * <p>
     * This ensures that any input angle, regardless of its magnitude or sign,
     * is converted to an equivalent angle within the standard range for heading
     * calculations. This is important for consistent angle comparisons and
     * control logic, as angles outside this range can cause discontinuities
     * or incorrect behavior in robot movement and rotation algorithms.
     * </p>
     *
     * @param angle The angle in degrees to normalize.
     * @return The normalized angle in the range [-180, 180).
     */
    public static double normalizeAngle(double angle) {
        // O(n) time complexity. Add this back in if my angle normalization is not working. -NP
        /* while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360; */

        // O(1) time complexity. This should be a more efficient way to normalize angles.
        angle = ((angle + 180) % 360 + 360) % 360 - 180;
        return angle;
    }
}
