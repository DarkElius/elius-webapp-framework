package elius.webapp.framework.exceptions;

import elius.webapp.framework.application.ApplicationLifecycle;

/**
 * Exception thrown when an error occurs during the lifecycle of an application
 * or a component, such as during the startup or shutdown phases.
 * 
 * <p>This is a checked exception used to signal that a component failed to 
 * initialize or terminate properly, providing a clean alternative to generic exceptions.</p>
 * 
 * @see ApplicationLifecycle
 */
public class LifecycleException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new lifecycle exception with {@code null} as its detail message.
     */
    public LifecycleException() {
        super();
    }

    /**
     * Constructs a new lifecycle exception with the specified detail message.
     * 
     * @param message the detail message, saved for later retrieval by the {@link #getMessage()} method
     */
    public LifecycleException(String message) {
        super(message);
    }

    /**
     * Constructs a new lifecycle exception with the specified detail message and cause.
     * 
     * @param message the detail message (saved for later retrieval by the {@link #getMessage()} method)
     * @param cause   the cause (saved for later retrieval by the {@link #getCause()} method). 
     *                (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public LifecycleException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new lifecycle exception with the specified cause and a detail message 
     * of {@code (cause==null ? null : cause.toString())}.
     * 
     * @param cause the cause (saved for later retrieval by the {@link #getCause()} method). 
     *              (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public LifecycleException(Throwable cause) {
        super(cause);
    }
}