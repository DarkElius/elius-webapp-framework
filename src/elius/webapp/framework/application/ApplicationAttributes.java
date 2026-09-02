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


package elius.webapp.framework.application;

/**
 * Definition of all static strings related to application, default values and properties/resources.
 * 
 * @author Elia Milioni
 *
 */
public class ApplicationAttributes {
	
	// Attribute to identify user info
	public static final String APP_USER_INFO = "EWA_USER_INFO";
	
	// Attribute to identify the name of the system properties that contains the application path
	public static final String APP_CONFIG_PATH = "ewa.config.path";
	
	// Application properties file
	public static final String APP_PROPERTIES_FILE = "ewa.properties";
	
	
	
	// Default - Enable authentication
	public static final String DEFAULT_SECURITY_AUTHENTICATION_ENABLE = "y";
	
	// Default - User role for unauthenticated user
	public static final String DEFAULT_SECURITY_UNAUTHENTICATED_ROLE = "USER";
	
	// Default - Security repository type
	public static final String DEFAULT_SECURITY_REPOSITORY_TYPE = "KeyStore";

	// Default - Redirect mask, regular expression
	public static final String DEFAULT_SECURITY_REDIRECT_MASK = ".";
	
	// Default - Redirect page
	public static final String DEFAULT_SECURITY_REDIRECT_PAGE = "/login.html";	

	// Default - LDAP port
	public static final int DEFAULT_LDAP_PORT = 636;
		

	// Properties - Enable authentication
	public static final String PROP_SECURITY_AUTHENTICATION_ENABLE = "security.authentication.enable";
	
	// Properties - User role for unauthenticated user
	public static final String PROP_SECURITY_UNAUTHENTICATED_ROLE = "security.unauthenticated.role";	
		
	// Properties - Redirect mask, regular expression
	public static final String PROP_SECURITY_REDIRECT_MASK = "security.redirect.mask";
	
	// Properties - Redirect page
	public static final String PROP_SECURITY_REDIRECT_PAGE = "security.redirect.page";	
	
	// Properties - LDAP server
	public static final String PROP_LDAP_SERVER = "ldap.server";
	
	// Properties - LDAP port
	public static final String PROP_LDAP_PORT = "ldap.port";

	// Properties - LDAP enable LDAPs (secure) (y/n), default is y
	public static final String PROP_LDAP_SECURE = "ldap.secure";
	
	// Properties - LDAP trust all certificates (y/n), default is n
	public static final String PROP_LDAP_TRUST_ALL_CERTIFICATES = "ldap.trustAllCertificates";

	// Properties - LDAP base distinguished name
	public static final String PROP_LDAP_BASEDN = "ldap.baseDn";
	
	// Properties - LDAP group - base distinguished name
	public static final String PROP_LDAP_GROUP_BASEDN = "ldap.group.baseDn";
	
	// Properties - LDAP group search for guests (? is the userId), blank for authorize it
	public static final String PROP_LDAP_GROUP_SEARCH_GUESTS = "ldap.group.search.guests";
	
	// Properties - LDAP group search for users (? is the userId), blank for authorize it
	public static final String PROP_LDAP_GROUP_SEARCH_USERS = "ldap.group.search.users";
	
	// Properties - LDAP group search for power users (? is the userId), blank for authorize it
	public static final String PROP_LDAP_GROUP_SEARCH_POWERUSERS = "ldap.group.search.powerUsers";
	
	// Properties - LDAP group search for Administrators (? is the userId), blank for authorize it
	public static final String PROP_LDAP_GROUP_SEARCH_ADMINISTRATORS = "ldap.group.search.administrators";
	
	// Properties - LDAP userId attribute
	public static final String PROP_LDAP_USER_ID = "ldap.user.id";

	// Properties - LDAP complete name attribute
	public static final String PROP_LDAP_USER_CN = "ldap.user.cn";
	
}
