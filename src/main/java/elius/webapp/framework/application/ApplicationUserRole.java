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

/**
 * Definition of all possible user authorization roles
 * 
 * @author Elia Milioni
 *
 */
public enum ApplicationUserRole {

	// Unauthorized
	UNAUTHORIZED(0, "Unauthorized"),
	// Guest
	GUEST(1, "Guest"), 
	// USer
	USER(2, "User"), 
	// PowerUser
	POWERUSER(3, "PowerUser"),
	// Administrator
	ADMINISTRATOR(4, "Administrator");

	// User role name
	private final String name;
	// User role id
	private final int id;

	
	/**
	 * Constructor
	 * @param id User role id
	 * @param name User type name
	 */
	ApplicationUserRole(int id, String name) {
		this.name = name;
		this.id = id;
	}

	
	/**
	 * Get user role name
	 * @return User type name
	 */
	public String getName() {
		return name;
	}

	
	/**
	 * Get user role id
	 * @return User role id
	 */
	public int getId() {
		return id;
	}

	
	/**
	 * Get user role by id
	 * @param id User role id
	 * @return User role
	 */
	public static ApplicationUserRole getById(int id) {
	    for(ApplicationUserRole e : values()) {
	        if(e.id == id) return e;
	    }
	    return UNAUTHORIZED;
	}
	
	
	/**
	 * Get user role by name
	 * @param name User role name
	 * @return User role
	 */
	public static ApplicationUserRole getByName(String name) {
	    for(ApplicationUserRole e : values()) {
	        if(e.name.equalsIgnoreCase(name)) return e;
	    }
	    return UNAUTHORIZED;
	}
}
