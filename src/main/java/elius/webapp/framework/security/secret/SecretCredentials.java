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

package elius.webapp.framework.security.secret;

public class SecretCredentials {
	// User id
	private String userId;
	// Password
	private String password;
	
	
	/**
	 * Get user id
	 * @return User id
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
	 * Get password
	 * @return Password
	 */
	public String getPassword() {
		return password;
	}
	
	
	/**
	 * Set password
	 * @param password Password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
}
