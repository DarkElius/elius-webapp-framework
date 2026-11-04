package elius.webapp.framework.properties;

import java.util.concurrent.ConcurrentHashMap;

/**
 * PropertiesManagerFactory.
 * <p>
 * Factory class for properties manager initialization.
 */
public class PropertiesManagerFactory {
	
	// Concurrent HashMap of the properties instances
	private static final ConcurrentHashMap<String, PropertiesManager> instances = new ConcurrentHashMap<>();
	
	/**
	 * Default constructor
	 */
	private PropertiesManagerFactory() {
		/* This class should not be instantiated */
	}

	/**
	 * Get the new or already allocated instance for the selected filename
	 * @param filename Filename to load
	 * @return PropertiesManager instance
	 */
	public static synchronized PropertiesManager getInstance(String filename) {
		return instances.computeIfAbsent(filename, PropertiesManager::new);
	}
	
}
