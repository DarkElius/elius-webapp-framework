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

import java.nio.charset.StandardCharsets;
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

	// Messages
	private static String INVALID_AUTH_HEADER = "Invalid authentication header";
	private static String USER_NOT_AUTHORIZED = "User not authorized";
	
	/**
	 * Default constructor
	 */
	public AuthenticationFilter() {
		/* Nothing to initialize */
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) 
			throws IOException, ServletException {
		
		// Ignore not http requests
		if (!(servletRequest instanceof HttpServletRequest httpServletRequest) || 
			!(servletResponse instanceof HttpServletResponse httpServletResponse)) {
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}

		// Log requested resource path
		logger.trace("Requested resource: {}", httpServletRequest.getServletPath());

		HttpSession httpSession = httpServletRequest.getSession();

		// Check if user is already logged
		if (AuthenticationManager.isUserIdLogged(httpSession)) {
			filterChain.doFilter(httpServletRequest, httpServletResponse);
			return;
		}

		// Missing authorization header
		String authHeader = httpServletRequest.getHeader("Authorization");
		if (authHeader == null) {
			unauthorized(httpServletResponse);
			return;
		}

		// Process basic authentication
		processBasicAuthentication(httpServletRequest, httpServletResponse, filterChain, authHeader, httpSession);
	}


	/**
	 * Handles Basic HTTP Authentication.
	 */
	private void processBasicAuthentication(HttpServletRequest httpServletRequest, 
										HttpServletResponse httpServletResponse, 
										FilterChain filterChain, 
										String authHeader, 
										HttpSession httpSession) throws IOException, ServletException {
		
		StringTokenizer st = new StringTokenizer(authHeader);
		
		if (!st.hasMoreTokens() || !st.nextToken().equalsIgnoreCase("Basic")) {
			unauthorized(httpServletResponse, INVALID_AUTH_HEADER);
			return;
		}

		logger.trace("Basic authentication filter found");

		try {
			if (!st.hasMoreTokens()) {
				unauthorized(httpServletResponse, INVALID_AUTH_HEADER);
				return;
			}

			// Decode Base64 credentials
			String auth = new String(Base64.getDecoder().decode(st.nextToken()), StandardCharsets.UTF_8);
			int p = auth.indexOf(":");
			
			if (p == -1) {
				logger.error(INVALID_AUTH_HEADER);
				unauthorized(httpServletResponse, INVALID_AUTH_HEADER);
				return;
			}

			// Extract credentials and login
			SecretCredentials credentials = new SecretCredentials();
			credentials.setUserId(auth.substring(0, p).trim());
			credentials.setPassword(auth.substring(p + 1).trim());

			AuthenticationManager appUserProfile = new AuthenticationManager();
			ApplicationUser appUser = appUserProfile.login(credentials, httpSession);

			if (appUser == null) {
				logger.warn(USER_NOT_AUTHORIZED);
				unauthorized(httpServletResponse, USER_NOT_AUTHORIZED);
			} else {
				filterChain.doFilter(httpServletRequest, httpServletResponse);
			}

		} catch (IllegalArgumentException e) {
			// Gestisce errori di decoding Base64 o problemi simili
			logger.error("Couldn't retrieve authentication", e);
			unauthorized(httpServletResponse, INVALID_AUTH_HEADER);
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

