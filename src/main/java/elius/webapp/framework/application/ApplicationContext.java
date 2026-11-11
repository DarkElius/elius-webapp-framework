package elius.webapp.framework.application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Application context where register common instances
 */
public final class ApplicationContext {

    // Context container
    private static final Map<Class<?>, Object> registry = new ConcurrentHashMap<>();

    private ApplicationContext() {
        // Utility class
    }

    /**
     * Register a component inside context.
     * @param <T> Component type
     * @param clazz Instance class
     * @param instance Instantce to be registered
     */
    public static <T> void register(Class<T> clazz, T instance) {
        registry.put(clazz, instance);
    }

    /**
     * Get componente from context.
     * @param <T> Component type
     * @param clazz Instance class
     * @return Component instance or null if not present
     */
    public static <T> T get(Class<T> clazz) {
        return clazz.cast(registry.get(clazz));
    }

    /**
     * Remove component from context.
     * @param clazz Instance class
     */
    public static void remove(Class<?> clazz) {
        registry.remove(clazz);
    }

    /**
     * Clear the context.
     */
    public static void clear() {
        registry.clear();
    }
}