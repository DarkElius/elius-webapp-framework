package elius.webapp.framework.db;

/**
 * Utility class for database-related operations.
 */
public final class DBUtils {

    private static final String TRUNCATED_MARKER = "... [TRUNCATED]";

    // Private constructor to prevent instantiation
    private DBUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Truncates a string to the specified maximum length.
     *
     * @param input The string to check and truncate
     * @param maxLength The maximum allowed length
     * @param appendMarker If true, appends "... [TRUNCATED]" while keeping total length within maxLength
     * @return The truncated string, or the original text if within limits
     */
    public static String truncate(String input, int maxLength, boolean appendMarker) {
        // Validate inputs
        if (input == null || input.length() <= maxLength) {
            return input;
        }

        if (appendMarker) {
            // Fallback to plain truncation if maxLength is smaller than or equal to the marker length
            if (maxLength <= TRUNCATED_MARKER.length()) {
                return input.substring(0, maxLength);
            }
            int cutoff = maxLength - TRUNCATED_MARKER.length();
            return input.substring(0, cutoff) + TRUNCATED_MARKER;
        }

        return input.substring(0, maxLength);
    }
}