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

package elius.webapp.framework.application;

import java.io.Serializable;

/**
 * Application user information and authorization.
 */
public class ApplicationUser implements Serializable {

	/** User id */
	private String userId;
	/** Complete name */
	private String completeName;
	/** Role */
	private ApplicationUserRole role;
	
	
	/**
	 * Constructor
	 */
	public ApplicationUser() {
		// Set default role to unauthorized
		role = ApplicationUserRole.UNAUTHORIZED;
	}
	
	
	/**
	 * Get user id
	 * @return The user id
	 */
	public String getUserId() {
		return userId;
	}
	
	
	/**
	 * Set user id
	 * @param userId User id
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	/**
	 * Get complete name
	 * @return Complete name
	 */
	public String getCompleteName() {
		return completeName;
	}
	
	
	/**
	 * Set complete Name
	 * @param completeName Complete name
	 */
	public void setCompleteName(String completeName) {
		this.completeName = completeName;
	}
	
	
	/**
	 * Get user role
	 * @return User role
	 */
	public ApplicationUserRole getUserRole() {
		return role;
	}
	
	/**
	 * Set user role
	 * @param userRole User role
	 */
	public void setUserRole(ApplicationUserRole userRole) {
		this.role = userRole;
	}
	
}
