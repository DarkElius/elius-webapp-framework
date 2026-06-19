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

package elius.webapp.framework.security.authentication;

import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

import elius.webapp.framework.application.ApplicationAttributes;
import elius.webapp.framework.application.ApplicationUserRole;
import elius.webapp.framework.properties.PropertiesManager;
import elius.webapp.framework.properties.PropertiesManagerFactory;
import elius.webapp.framework.security.SecurityTrustAllCertificates;
import elius.webapp.framework.security.secret.SecretCredentials;

public class AuthenticationLdap {
	
	// Get logger
	private static Logger logger = LogManager.getLogger(AuthenticationLdap.class);

	// Properties file
	private PropertiesManager appProperties;
	
	// Server address
	private String server;
	// Port
	private int port;
	// Use secure connection (LDAPs)
	private boolean useSecure;
	// Trust all server certificates
	private boolean trustAllCertificates;
	// Connection
	private LDAPConnection connection;
	// Base distinguished name
	private String baseDn;
	// Group base distinguished name
	private String groupBaseDn;
	// Search guest group
	private String groupSearchGuests;
	// Search user group
	private String groupSearchUsers;
	// Search power user group
	private String groupSearchPowerUsers;
	// Search administrators group
	private String groupSearchAdministrators;
	// Attribute for userId
	private String attUserId;
	// Attribute for complete name
	private String attCompleteName;


	
	/**
	 * Constructor
	 */
	public AuthenticationLdap() {
		// Load application properties
		appProperties = PropertiesManagerFactory.getInstance(ApplicationAttributes.APP_PROPERTIES_FILE);
		
		// Server
		server = appProperties.get(ApplicationAttributes.PROP_LDAP_SERVER);
		
		// Port
		port = appProperties.getInt(ApplicationAttributes.PROP_LDAP_PORT, ApplicationAttributes.DEFAULT_LDAP_PORT);

		// Enable LDAPs (secure), set default to yes
		useSecure = true;
		// Enable LDAPs (secure) (y/n)
		if(!"Y".equalsIgnoreCase(appProperties.get(ApplicationAttributes.PROP_LDAP_SECURE)))
			useSecure = false;

		// Trust all server certificates, set default to false
		trustAllCertificates = false;
		// Trust all certificates (y/n)
		if("Y".equalsIgnoreCase(appProperties.get(ApplicationAttributes.PROP_LDAP_TRUST_ALL_CERTIFICATES)))
			trustAllCertificates = true;
		
		// Base distinguished name
		baseDn = appProperties.get(ApplicationAttributes.PROP_LDAP_BASEDN);
		// Group base distinguished name
		groupBaseDn = appProperties.get(ApplicationAttributes.PROP_LDAP_GROUP_BASEDN);
		// Guest group search
		groupSearchGuests = appProperties.get(ApplicationAttributes.PROP_LDAP_GROUP_SEARCH_GUESTS);
		// User group search
		groupSearchUsers = appProperties.get(ApplicationAttributes.PROP_LDAP_GROUP_SEARCH_USERS);
		// Power user group search
		groupSearchPowerUsers = appProperties.get(ApplicationAttributes.PROP_LDAP_GROUP_SEARCH_POWERUSERS);
		// Administrator group search
		groupSearchAdministrators = appProperties.get(ApplicationAttributes.PROP_LDAP_GROUP_SEARCH_ADMINISTRATORS);
		// Attribute for userId, default is uid
		attUserId = appProperties.get(ApplicationAttributes.PROP_LDAP_USER_ID, "uid");
		// Attribute for complete name, default is cn
		attCompleteName = appProperties.get(ApplicationAttributes.PROP_LDAP_USER_CN, "cn");
		
	}
	

	/**
	 * Connect to LDAP
	 * @return 0 Successful, 1 Error
	 */
	public int connect() {
		// Log connection
		logger.trace("Connect to server(" + server + ") port(" + port + ") ldaps(" + useSecure + ") trustAllCertificates(" + trustAllCertificates + ")");
		
		// LDAP connection
		connection = null;
		
		// SSL Context for secure connections
		SSLContext sslContext = null;
			
		try {
			// Enable secure LDAP (LDAPs)
			if(useSecure) {
				// Get context for TLS 1.2
				sslContext = SSLContext.getInstance("TLSv1.2");
				// Initialize context
				sslContext.init(null, null, null);
				
				// Trust all certificates
				if(trustAllCertificates) {
					// Create Trust Manager
					TrustManager[] trustAll = new TrustManager[] {new SecurityTrustAllCertificates()};
					// Set trust manager in the context
					sslContext.init(null, trustAll, new java.security.SecureRandom());
				}
				// Start connection / authentication
				connection = new LDAPConnection(sslContext.getSocketFactory(), server, port);
			} else {
				// Start connection / authentication
				connection = new LDAPConnection(server, port);
			}
			
			// Log successful
			logger.trace("Connected");		
		} catch (Exception e) {
			// Log error
			logger.catching(e);
			// Set error code
			return 1;
		}
		
		// Return successful
		return 0;
	}
	
	
	/**
	 * Close LDAP connection
	 */
	public void close() {
		// Verify connection
		if(null != connection) {
			// Close connection
			connection.close();			
			// Reset connection status
			connection = null;
			// Log closure
			logger.trace("Connection closed");
		}
	}
	
	
	/**
	 * Authenticate user
	 * @param credentials Credentials
	 * @return Blank for successful or the error message
	 */
	public String authenticate(SecretCredentials credentials) {
		// Log authentication
		logger.trace("Authenticate userId(" + credentials.getUserId() + ") on baseDn(" + baseDn + ")");
		
		// Response message
		String response = "";
		
		// Check for connection
		if(null == connection) {
			// Set error message
			response = "Connection not initialized";
		}
		
		try {
			// Authentication
			connection.bind(attUserId + "=" + credentials.getUserId() + "," + baseDn, credentials.getPassword()); 
			// Log logged in
			logger.info("UserId(" + credentials.getUserId() + ") authenticated");
		} catch (LDAPException e) {
			// Log error message
			logger.catching(e);
			// Set generic response message for security reasons
			response = "Invalid Credentials";
		}
		
		// Return response message
		return response;
	}
	
	
	/**
	 * Execute a search to verify if user is present in a specific group
	 * @param groupBaseDn Group base distinguished name
	 * @param groupSearch Group search string
	 * @param userId UserId
	 * @return True if search return at least one element
	 */
	public boolean searchGroup(String groupBaseDn, String groupSearch, String userId) {
		
		// Set user in search string
		groupSearch = groupSearch.replaceAll("\\?", userId);
		
		// Log request
		logger.trace("Check GroupBaseDn(" + groupBaseDn + ") GroupSearch(" + groupSearch + ")");
		
		// Set default to false
		boolean found = false;
		
		// User group specified or blank
		if(!"n/a".equalsIgnoreCase(groupSearch)) {
			
			// No search string specified
			if(groupSearch.isEmpty()) {
				
				// User has the role
				found = true;
				// Log information about the role
				logger.trace("Search filter is empty and userId(" + userId + ") has the role");
				
			} else {
				
				try {
					// Prepare search request
					SearchRequest request = new SearchRequest(groupBaseDn, SearchScope.SUB, groupSearch);
					
					// No limit
		            request.setSizeLimit(0);
		            
		            // Execute search
		            SearchResult searchResult = connection.search(request);
	
		            // Get results
		            List<SearchResultEntry> result = searchResult.getSearchEntries();
	
		            // Empty list
		            if (!result.isEmpty()) {
		            	// User has  the role
		            	found = true;
						// Log value
						logger.trace("UserId(" + userId + ") found in group");
		            } else {
						// Log value
						logger.trace("UserId(" + userId + ") not found in group");		            	
		            }
		            
				} catch (LDAPException e) {
					// Log error message
					logger.catching(e);
	            	// User has not the role
	            	found = false;
				}

			}
			
		} else {
			
			// User has not the role
			found = false;
			// Log information about the role
			logger.trace("Search filter is set to n/a and userId(" + userId + ") has not the role");
			
		}

		// Return search result
		return found;	
	}
	
	
	/**
	 * Get a specific LDAP attribute for selected user
	 * @param userId UserId
	 * @param attribute LDAP attribute
	 * @return The value of specific attribute if present, otherwise null
	 */
	public String getAttribute(String userId, String attribute) {
		// Log request
		logger.debug("Get attribute(" + attribute + ") for userId(" + userId + ")");

		// Value
		String value = "";
		
		// Search instance
		SearchResultEntry sre;
		
		try {
			// Get attribute for selected user
			sre = connection.getEntry(attUserId + "=" + userId + "," + baseDn, attribute);
		} catch (LDAPException e) {
			// Log error message
			logger.catching(e);
			//Return error
			return null;
		}
		
		// Successfully fetched
		if (null != sre) {
			// Get value
			value = sre.getAttributeValue(attribute);			
		}
		
		// Log value
		logger.trace("Value(" + value + ") of attribute(" + attribute + ") for userId(" + userId + ")");

		// Return value for selected attribute-userId
		return value;
	}
	
	
	/**
	 * Get the user role by searching in groups
	 * @param userId
	 * @return Application user role from unauthorized to administrator
	 */
	public ApplicationUserRole getRole(String userId) {
		// Log
		logger.debug("Get role for userId(" + userId + ")");
		
		// Initialize role to unauthorized
		ApplicationUserRole appUserRole = ApplicationUserRole.UNAUTHORIZED;
		
		// Log
		logger.trace("Looking for GUEST authorization...");
		// Check for guest authorization
		if(searchGroup(groupBaseDn, groupSearchGuests, userId))
			appUserRole = ApplicationUserRole.GUEST;
		
		// Log
		logger.trace("Looking for  USER authorization...");
		// Check for user authorization
		if(searchGroup(groupBaseDn, groupSearchUsers, userId))
			appUserRole = ApplicationUserRole.USER;

		// Log
		logger.trace("Looking for  POWERUSER authorization...");
		// Check for power user authorization
		if(searchGroup(groupBaseDn, groupSearchPowerUsers, userId))
			appUserRole = ApplicationUserRole.POWERUSER;
			
		// Log
		logger.trace("Looking for  ADMINISTRATOR authorization...");
		// Check for administrator authorization
		if(searchGroup(groupBaseDn, groupSearchAdministrators, userId))
			appUserRole = ApplicationUserRole.ADMINISTRATOR;
		
		// Log
		logger.trace("UserId has the " + appUserRole.toString() + " role");
		
		// Return role for the specified user
		return appUserRole;
	}

	
	/**
	 * Return the complete attribute name
	 * @return Complete attribute name
	 */
	public String getAttCompleteName() {
		return attCompleteName;
	}
	
}
