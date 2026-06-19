/**
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at
    
      http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
*/

package elius.webapp.framework.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import elius.webapp.framework.application.ApplicationAttributes;



public class PropertiesManager {

	// Get logger
	private static Logger logger = LogManager.getLogger(PropertiesManager.class);
	
	// Properties file
	private final Properties properties = new Properties();

	// Application path
	private final String applicationPath;
	
	// Properties filename
	private final String filename;

	/**
	 * Constructor
	 * @param filename Properties filename
	 */
	public PropertiesManager(String filename) {
		// Set application file from properties
		applicationPath = System.getProperty(ApplicationAttributes.APP_PATH);

		// Set properties filename
		this.filename = filename;

		// Load properties from selected file
		load();
	}

	/**
	 * Get application path
	 * @return Application path
	 */
	public String getApplicationPath() {
		return applicationPath;
	}
	
	/**
	 * Get the properties load status
	 * @return true if properties file is loaded
	 */
	public boolean isLoaded() {
		return (null != properties);
	}
	
	
	/**
	 * Get properties filename
	 * @return Properties filename
	 */
	public String getFilename() {
		return filename;
	}
	
	
	/**
	 * Load properties file from the application path defined in the java parameters
	 * @param propertiesFile Properties file
	 * @return 0 Successful, 1 Error
	 */
	private int load() {
		
		// Log file name
		logger.traceEntry("Path({}), filename({})", applicationPath, filename );

		// Read property file from system property
        try (InputStream input = new FileInputStream(applicationPath + "/" + filename)) {

            // Load a properties file
            properties.load(input);

        } catch (IOException e) {  	
        	// Log trace
        	logger.error(e.getStackTrace());
			
			// Log error
			logger.traceExit("Error");
        	
        	// Return error
        	return 1;
        }	
		
		// Log successful
		logger.traceExit();
		
		// Return successful
		return 0;
	}
	
	
	/**
	 * Fetch the value of the property 
	 * @param key Key
	 * @return null in case of error or the value of the key
	 */
	public String get(String key) {
		// Set default value for error
        String value = null;
        
        if(null != properties) {
    		// Get property value
        	value = properties.getProperty(key);
    
        	// Resolve variables
        	value = resolveVariables(value);
        	
			// Log key request
			logger.trace("Key(" + key + ") value(" + value + ")");        	
        } 
    	
    	// Return value
    	return value;
	}
	
	
	/**
	 * Fetch the value of the property and default value is set if it's null or empty
	 * @param key Key
	 * @param defaultValue Default value
	 * @return Key value or default if the original value is null or empty
	 */
	public String get(String key, String defaultValue) {
		// Fetch the key value
		String value = get(key);
		
		// Set default value if it's null or empty
		if ((null == value) || value.trim().isEmpty()) {
			
			// Log warning
			logger.warn("Invalid or empty properties (" + key + "), default was set (" + defaultValue + ")");
			
			// Return the default value
			return defaultValue;
		}
		
		// Return value
		return value;
	}
	
	
	/**
	 * Fetch the value of the property and default value is set if it's null, empty or not an integer
	 * @param key Key
	 * @param defaultValue Default value
	 * @return Key value or default if the original value is null, empty or not an integer
	 */
	public int getInt(String key, int defaultValue) {
		// Initialize value
		int value = 0;
	
		try {
			
			// Fetch the key value
			value = Integer.parseInt(get(key));
			
		} catch (Exception e) {
			
			// Set default value
			value = defaultValue;
			
			// Log warning
			logger.warn("Invalid or empty properties (" + key + "), default was set (" + defaultValue + ")");		
		}
		
		// Return value
		return value;
	}
	
	
	/**
	 * Fetch the value of the property and default value is set if it's null, empty or not a long
	 * @param key Key
	 * @param defaultValue Default value
	 * @return Key value or default if the original value is null, empty or not a long
	 */
	public long getLong(String key, long defaultValue) {
		// Initialize value
		long value = 0;
	
		try {
			
			// Fetch the key value
			value = Long.parseLong(get(key));
			
		} catch (Exception e) {
			
			// Set default value
			value = defaultValue;
			
			// Log warning
			logger.warn("Invalid or empty properties (" + key + "), default was set (" + defaultValue + ")");		
		}
		
		// Return value
		return value;
	}
	
	
	/**
	 * Resolves placeholders within the provided string by replacing them with 
	 * their corresponding Environment Variables or JVM System Properties.
	 * <p>
	 * This method supports two types of placeholders:
	 * <ul>
	 *   <li>{@code ${VAR_NAME}} - Replaced by the Environment Variable (via {@link System#getenv(String)}).</li>
	 *   <li>{@code #{PROP_NAME}} - Replaced by the JVM System Property (via {@link System#getProperty(String)}).</li>
	 * </ul>
	 * If a variable or property is not defined, it is replaced with an empty string.
	 *
	 * @param value the string containing placeholders to resolve; may be {@code null}
	 * @return the resolved string, or {@code null} if the input value was {@code null}
	 * @since 1.0
	 */
	private String resolveVariables(String value) {
		// Check for null input to avoid NullPointerException
		if (value == null) {
			return null;
		}

		// Regex using named capturing groups:
		// "prefix" captures either '$' or '#'
		// "name" captures the alphanumeric variable name inside the curly braces
		Pattern pattern = Pattern.compile("(?<prefix>[\\$#])\\{(?<name>[\\w.]+)\\}");
		Matcher matcher = pattern.matcher(value);
		StringBuilder result = new StringBuilder();

		// Loop through all matches found in the input string
		while (matcher.find()) {
			String prefix = matcher.group("prefix");
			String varName = matcher.group("name");

			// Java 21 Switch Expression to determine the resolution strategy
			String resolvedValue = switch (prefix) {
				case "$" -> System.getenv(varName);    // Fetch from OS Environment Variables
				case "#" -> System.getProperty(varName); // Fetch from JVM System Properties (-Dkey=value)
				default  -> null; 
			};

			// If the variable is not found (null), replace it with an empty string.
			// Matcher.quoteReplacement prevents errors if the value contains special regex characters like '$' or '\'
			String replacement = (resolvedValue == null) ? "" : Matcher.quoteReplacement(resolvedValue);
			matcher.appendReplacement(result, replacement);
		}

		// Append any remaining text after the last match
		matcher.appendTail(result);
		
		return result.toString();
	}
}