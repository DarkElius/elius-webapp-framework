package elius.webapp.framework.exceptions;

/**
 * Exception thrown when an error occurs during the loading, initialization, 
 * or lifecycle management of a plugin.
 * 
 * <p>This is a checked exception used to signal that a plugin failed to load 
 * properly (e.g., due to invalid JAR URLs, configuration issues, or failures 
 * during the plugin's start phase).</p>
 * 
 * @see elius.webapp.framework.plugin.Plugin
 */
public class PluginException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new plugin exception with {@code null} as its detail message.
     */
    public PluginException() {
        super();
    }

    /**
     * Constructs a new plugin exception with the specified detail message.
     * 
     * @param message the detail message, saved for later retrieval by the {@link #getMessage()} method
     */
    public PluginException(String message) {
        super(message);
    }

    /**
     * Constructs a new plugin exception with the specified detail message and cause.
     * 
     * @param message the detail message (saved for later retrieval by the {@link #getMessage()} method)
     * @param cause   the cause (saved for later retrieval by the {@link #getCause()} method). 
     *                (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new plugin exception with the specified cause and a detail message 
     * of {@code (cause==null ? null : cause.toString())}.
     * 
     * @param cause the cause (saved for later retrieval by the {@link #getCause()} method). 
     *              (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public PluginException(Throwable cause) {
        super(cause);
    }
}