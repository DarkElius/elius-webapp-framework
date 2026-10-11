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


package elius.webapp.framework.db;


/**
 * DBDataConversion.
 * <p>
 * Class that provide data conversion utilities.
 */
public class DBDataConversion {
	
	/**
	 * Default constructor
	 */
	public DBDataConversion() {
	}

	/**
	 * Convert all empty strings objects to specified value
	 * @param destinationValue Destination value
	 * @param list Object list
	 */
	public static void convertEmptyStrings(String destinationValue, final Object... list) {
		
		// Read parameters
		for (int i = 0; i < list.length; i++) {
		
			// Not null objects
			if(null != list[i]) {
				
				// Get empty String 
				if(list[i].getClass().equals(String.class) && ("".equals((String)list[i])) ) 	
					list[i] = (Object)destinationValue;
				
			}
		}
		
	}
	
}
