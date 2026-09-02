/**
 * Copyright 2023 Elia Milioni
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package elius.webapp.framework.security.authentication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import elius.webapp.framework.application.ApplicationAttributes;
import elius.webapp.framework.application.ApplicationUser;
import elius.webapp.framework.application.ApplicationUserRole;
import elius.webapp.framework.properties.PropertiesManager;
import elius.webapp.framework.properties.PropertiesManagerFactory;
import elius.webapp.framework.security.secret.SecretCredentials;
import jakarta.servlet.http.HttpSession;

public class AuthenticationManager {

	// Get logger
	private static Logger logger = LogManager.getLogger(AuthenticationManager.class);
	
	// Enable authentication
	private String enableAuthentication;
	
	// LDAP Manager
	private AuthenticationLdap ldapManager;
	
	// Response message
	private String response;
	
	// Property file
	private PropertiesManager appProperties;
	
	
	/**
	 * Constructor
	 */
	public AuthenticationManager() {
		// Initialize LDAP Manager
		ldapManager = new AuthenticationLdap();
		// Load application properties
		appProperties = PropertiesManagerFactory.getInstance(ApplicationAttributes.APP_PROPERTIES_FILE);
		
		// Enable authentication
		enableAuthentication = appProperties.get(ApplicationAttributes.PROP_SECURITY_AUTHENTICATION_ENABLE, ApplicationAttributes.DEFAULT_SECURITY_AUTHENTICATION_ENABLE);
		
		// Set default in case of invalid value
		if(!"y".equalsIgnoreCase(enableAuthentication) && !"n".equalsIgnoreCase(enableAuthentication)) {
			// Set default application value
			enableAuthentication = ApplicationAttributes.DEFAULT_SECURITY_AUTHENTICATION_ENABLE;
			// Log the error
			logger.warn("Invalid value of properties (" + ApplicationAttributes.PROP_SECURITY_AUTHENTICATION_ENABLE + "), default was set (" + ApplicationAttributes.DEFAULT_SECURITY_AUTHENTICATION_ENABLE + ")");
		}		
	}
	
	
	/**
	 * Execute login procedure
	 * @param credentials Credentials
	 * @return Application user or null in case of errors
	 */
	public ApplicationUser login(SecretCredentials credentials, HttpSession httpSession) {
		// Response message
		response = "";
		
		// User authenticated without error: create response user
		ApplicationUser appUser = new ApplicationUser();
		
		// Set userId
		appUser.setUserId(credentials.getUserId());

		// Application authentication disabled
		if("n".equalsIgnoreCase(enableAuthentication)) {
			// Get default role for the unauthenticated users
			String uRole = appProperties.get(ApplicationAttributes.PROP_SECURITY_UNAUTHENTICATED_ROLE);
			//Set role
			appUser.setUserRole(ApplicationUserRole.getByName(uRole));
			
			// Check if default role it's unauthorized
			if(ApplicationUserRole.UNAUTHORIZED == appUser.getUserRole()) {
				// Set error
				logger.warn("User (" + appUser.getUserId() + ") not authorized");
				// Return error
				return null;
			}
			
			// Log the unauthenticated user
			logger.warn("User (" + appUser.getUserId() + ") Role(" + appUser.getUserRole().getName() + ") logged in without authentication (disabled)");
			
			// Return successful
			return appUser;
		}
		
		// Connect to LDAP
		if(0 != ldapManager.connect()) {
			// Set error response message
			response = "Error during ldap connection";
			// return error
			return null;
		}
		
		// Authentication
		response = ldapManager.authenticate(credentials);
		
		// Check errors
		if(!response.isEmpty()) {
			// Close LDAP connection
			ldapManager.close();
			// Return error
			return null;		
		}
	
		// Get and set user role
		appUser.setUserRole(ldapManager.getRole(credentials.getUserId()));
		
		// Check if user has a role to proceed with the application usage
		if(ApplicationUserRole.UNAUTHORIZED == appUser.getUserRole()) {
			// Set error
			logger.warn("UserId (" + appUser.getUserId() + ") not authorized");
			// Close LDAP connection
			ldapManager.close();
			// Return error
			return null;
		}
		// Set error
		logger.debug("UserId (" + appUser.getUserId() + ") has role " + appUser.getUserRole().getName());
		
		// Get complete name
		String userCN = ldapManager.getAttribute(credentials.getUserId(), ldapManager.getAttCompleteName());
		// Check result
		if(null != userCN) {
			// Set complete name
			appUser.setCompleteName(userCN);
		}
			
		// Close LDAP connection
		ldapManager.close();
		
		// Save userId in session
		httpSession.setAttribute(ApplicationAttributes.APP_USER_INFO, appUser);
		
		// Return successful
		return appUser;
	}


	/**
	 * Get last response message
	 * @return Message
	 */
	public String getResponse() {
		return response;
	}
	
	/**
	 * Return true if the userId is logged
	 * @param httpSession
	 * @return true if userId is logged
	 */
	public static boolean isUserIdLogged(HttpSession httpSession) {
		
		// UserId not logged
		if(null == httpSession.getAttribute(ApplicationAttributes.APP_USER_INFO))
			return false;
		
		// UserId is logged
		return true;
	}
	
}
