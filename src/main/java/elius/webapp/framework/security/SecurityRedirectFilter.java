package elius.webapp.framework.security;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import elius.webapp.framework.application.ApplicationAttributes;
import elius.webapp.framework.properties.PropertiesManager;
import elius.webapp.framework.properties.PropertiesManagerFactory;
import elius.webapp.framework.security.authentication.AuthenticationManager;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SecurityRedirectFilter implements Filter {

	// Get logger
	private static Logger logger = LogManager.getLogger(SecurityRedirectFilter.class);

	// Properties file
	private PropertiesManager appProperties;
	
	// Redirect mask, regular expression
	private String redirectMask;
	
	// Redirect page
	private String redirectPage;

	
	/**
	 * Constructor
	 */
	public SecurityRedirectFilter() {
	
		// Load application properties
		appProperties = PropertiesManagerFactory.getInstance(ApplicationAttributes.APP_PROPERTIES_FILE);
			
		// Get redirect mask
		redirectMask = appProperties.get(ApplicationAttributes.PROP_SECURITY_REDIRECT_MASK, ApplicationAttributes.DEFAULT_SECURITY_REDIRECT_MASK);
		
		// Get redirect page
		redirectPage = appProperties.get(ApplicationAttributes.PROP_SECURITY_REDIRECT_PAGE, ApplicationAttributes.DEFAULT_SECURITY_REDIRECT_PAGE);
	
	}

	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		
		// Get requested resource
		String path = ((HttpServletRequest) servletRequest).getServletPath();
		
		// Log
		logger.trace("Requested resource (" + path + ")");
		
		// Process HTTP request
		if (servletRequest instanceof HttpServletRequest) {
			// Cast request
			HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
			// Cast response
			HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
			
	        // Disable caching
			httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			httpServletResponse.setHeader("Oragma", "no-cache");
			httpServletResponse.setDateHeader("Expires", 0); 
			
			// Get Session
			HttpSession httpSession = httpServletRequest.getSession();

			// Valid user found in session, skip authorization
			if(AuthenticationManager.isUserIdLogged(httpSession)) {
				
				// Ignore, userId already logged in
				filterChain.doFilter(httpServletRequest, httpServletResponse);
				
			} else if(path.matches(redirectMask) ) {
				
				// Set redirect
				httpServletResponse.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
				
				// Redirect to login page
				httpServletResponse.sendRedirect( ((HttpServletRequest) servletRequest).getContextPath() + redirectPage);
				
			} else {
				
				// Ignore, free access
				filterChain.doFilter(httpServletRequest, httpServletResponse);
				
	        } 			

		}
	}
	
}