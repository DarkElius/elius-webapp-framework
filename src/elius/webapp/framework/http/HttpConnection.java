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

package elius.webapp.framework.http;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import elius.webapp.framework.security.SecurityTrustAllCertificates;
import elius.webapp.framework.security.secret.SecretCredentials;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class HttpConnection {
	
	// Get logger
	private static Logger logger = LogManager.getLogger(HttpConnection.class);

	// Request response content
	private String httpResponseContent;
	
	
	/**
	 * Trust all certificates
	 * @throws Exception
	 */
	private SSLContext trustAll() throws Exception {
		// Get context instance
		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
		// Initialize it
		sslContext.init(null, null, null);
		// Trust settings
		TrustManager[] trustAll = new TrustManager[] {new SecurityTrustAllCertificates()};
		// Set parameters in the context
		sslContext.init(null, trustAll, new java.security.SecureRandom());
		// Set trust all certificates context to HttpsURLConnection
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());	
		// Return context
		return sslContext;
	}
	
	
	/**
	 * Execute a GET call to the specified URL
	 * @param uri URI to be called
	 * @param credentials Credentials or null for unauthenticated connection
	 * @param trustAllCertificates True to trust all certificates
	 * @return 0 Successful, 1 HTTP Error, 2 Generic Error
	 */
	public int get(String uri, SecretCredentials credentials, boolean trustAllCertificates) {
		// Log get
		logger.debug("GET URI(" + uri + ") User(" + credentials.getUserId() + ") + TrustAll(" + trustAllCertificates + ")");		
		
		// Initialized response content
		httpResponseContent = "";
		
		try {
			Client client = null;
			
			// Trust all certificates
			if(trustAllCertificates) {
				client = ClientBuilder.newBuilder().sslContext(trustAll()).build();
			} else {
				client = ClientBuilder.newClient();
			}
	
			// Set authentication
			if(null != credentials) {
				// Set authentication basic parameters
				HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(credentials.getUserId(), credentials.getPassword());
				// Set authentication in the client
			    client.register(feature);				
			}
			
			// Execute call
			Response response = client.target(uri)
	          .request(MediaType.APPLICATION_FORM_URLENCODED)
	          .get();
	     
			// Set response content
			httpResponseContent = response.readEntity(String.class);
			
			// Get status code
			int httpRc = response.getStatus();

			// Check HTTP status code
			if((httpRc < 200) || (208 < httpRc)) {
				// Log error
				logger.error("HTTP error");
				// Log message
				logger.error(httpResponseContent);
				// Return HTTP error
				return 1;
			}
			
		} catch (Exception e) {
			// Log message
			logger.catching(e);
			// Return generic error
			return 2;
		}
		
		// Log get
		logger.debug("Get successfully executed");		

		// Return successful
		return 0;
	}
	
	
	/**
	 * Execute a POST call to the specified URL
	 * @param uri URI to be called
	 * @param credentials Credentials or null for unauthenticated connection
	 * @param trustAllCertificates True to trust all certificates
	 * @param body Body
	 * @param content Set content type
	 * @param accept Set accept type
	 * @return 0 Successful, 1 HTTP Error, 2 Generic Error 
	 */
	public int post(String uri, SecretCredentials credentials, boolean trustAllCertificates, Entity<?> body, MediaType content, MediaType accept) {
		// Log get
		logger.debug("POST URI(" + uri + ") User(" + credentials.getUserId() + ") + TrustAll(" + trustAllCertificates + ")");		
		
		// Initialized response content
		httpResponseContent = "";
		
		try {
			Client client = null;
			
			// Trust all certificates
			if(trustAllCertificates) {
				client = ClientBuilder.newBuilder().sslContext(trustAll()).build();
			} else {
				client = ClientBuilder.newClient();
			}
	
			// Set authentication
			if(null != credentials) {
				// Set authentication basic parameters
				HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(credentials.getUserId(), credentials.getPassword());
				// Set authentication in the client
			    client.register(feature);				
			}
			
			// Execute call
			Response response = client.target(uri)
	          .request(content)
	          .accept(accept)
	          .post(body);
	     
			// Get status code
			int httpRc = response.getStatus();

			// Check HTTP status code
			httpResponseContent = response.readEntity(String.class);
			
			// Check HTTP return code
			if((httpRc < 200) || (208 < httpRc)) {
				// Log error
				logger.error("HTTP error");
				// Log message
				logger.error(httpResponseContent);
				// Return HTTP error
				return 1;
			}
			
		} catch (Exception e) {
			// Log message
			logger.catching(e);
			// Return generic error
			return 2;
		}
		
		// Log get
		logger.debug("POST successfully executed");		

		// Return successful
		return 0;

	}
	
	
	/**
	 * Return the response content of the last call
	 * @return Response content
	 */
	public String getResponseContent() {
		return httpResponseContent;
	}
}
