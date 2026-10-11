/*
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

import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import elius.webapp.framework.application.ApplicationUser;
import elius.webapp.framework.security.secret.SecretCredentials;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


/**
 * AuthenticationFilter.
 * <p>
 * Define the filter for the basic authentication.
 */
public class AuthenticationFilter implements jakarta.servlet.Filter {

	// Get logger
	private static Logger logger = LogManager.getLogger(AuthenticationFilter.class);
	
	/**
	 * Default constructor
	 */
	public AuthenticationFilter() {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		
		// Get requested resource
		String path = ((HttpServletRequest) servletRequest).getServletPath();
		
		// Log
		logger.trace("Requested resource (" + path + ")");
		
		// Process HTTP request
		if (servletRequest instanceof HttpServletRequest) {
			// Cast
			HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
			HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
	
			// Get Session
			HttpSession httpSession = httpServletRequest.getSession();
			
			// Valid user found in session, skip authorization
			if(AuthenticationManager.isUserIdLogged(httpSession)) {
				// Finish
				filterChain.doFilter(httpServletRequest, httpServletResponse);				
			} else {

				// Get Authorization
				String authHeader = httpServletRequest.getHeader("Authorization");
				
				// Valid
				if (authHeader != null) {
					
					// Get headers
					StringTokenizer st = new StringTokenizer(authHeader);
					
					// Scroll list
					if (st.hasMoreTokens()) {
						
						// Get token
						String basic = st.nextToken();
						
						// Basic Authorization
						if (basic.equalsIgnoreCase("Basic")) {
							
							// Basic authentication
							logger.trace("Basic authentication filter found");
												
							try {
								// Convert from Base64
								String auth = new String(Base64.getDecoder().decode(st.nextToken()));
	
								// Parsing
								int p = auth.indexOf(":");
								
								// Correct value
								if (p != -1) {
									// Authentication credentials
									SecretCredentials credentials = new SecretCredentials();
									
									// Extract userId
									credentials.setUserId(auth.substring(0, p).trim());
									
									// Extract password
									credentials.setPassword(auth.substring(p + 1).trim());
									
									// Create instance for user profile
									AuthenticationManager appUserProfile = new AuthenticationManager();
									
									// Execute login
									ApplicationUser appUser = appUserProfile.login(credentials, httpSession);
									
									// Check for errors
									if(null == appUser) {
										// Log unauthorized used
										logger.warn("User not authorized");
										
										// Set response, invalid authentication header
										unauthorized(httpServletResponse, "User not authorized");
									} else {
										// Finish
										filterChain.doFilter(httpServletRequest, httpServletResponse);
									}
									
								} else {
									// Invalid authorization header
									logger.error("Invalid authentication header");
									
									// Set response, invalid authentication header
									unauthorized(httpServletResponse, "Invalid authentication header");
								}
	
							} catch (UnsupportedEncodingException e) {
								throw new Error("Couldn't retrieve authentication", e);
							}							
						}
					}
				} else {
					unauthorized(httpServletResponse);
				}
			}
		}
	}
	
	/**
	 * Unauthorized response
	 * @param response Response
	 * @param message Message
	 * @throws IOException
	 */
	private void unauthorized(HttpServletResponse response, String message) throws IOException {
		response.setHeader("WWW-Authenticate", "Basic realm=\"Protected\"");
		response.sendError(401, message);		
	}
	
	/**
	 * Unauthorized response
	 * @param response Response
	 * @throws IOException
	 */
	private void unauthorized(HttpServletResponse response) throws IOException {
		unauthorized(response, "Unauthorized");
	}

}

