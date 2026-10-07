package elius.webapp.framework.properties;

import java.util.concurrent.ConcurrentHashMap;


public class PropertiesManagerFactory {
	
	// Concurrent HashMap of the properties instances
	private static final ConcurrentHashMap<String, PropertiesManager> instances = new ConcurrentHashMap<>();
	
	/**
	 * Get the new or already allocated instance for the selected filename
	 * @param filename Filename to load
	 * @return PropertiesManager instance
	 */
	public static synchronized PropertiesManager getInstance(String filename) {
		
		return instances.computeIfAbsent(filename, key -> new PropertiesManager(key));
		
	}
	
}
