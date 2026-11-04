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

package elius.webapp.framework.server;

import jakarta.servlet.Filter;

import java.util.HashMap;
import java.util.Map;


/**
 * ServerConfig.
 * <p>
 * Web server configuration.
 */
public class ServerConfig {

private String staticResourcePath;

    // Packages JAX-RS List
    private String packageJAXRS;
    // Filter Map
    private final Map<String, Class<? extends Filter>> filters = new HashMap<>();

    /**
	 * Default constructor
	 */
	private ServerConfig() {
        /* This class should not be instantiated */
	}

    /**
     * Set static resources
     * @param path Path
     * @return Engine config
     */
    public ServerConfig setStaticResources(String path) {
        this.staticResourcePath = path;
        return this;
    }

    /**
     * Set JAX-RS packages to be exposed by the application
     * @param packages Packages name separated by comma
     * @return Engine config
     */
    public ServerConfig setPackageJAXRS(String... packages) {
        if (packages != null && packages.length > 0) {
            this.packageJAXRS = String.join(",", packages);
        }
        return this;
    }

    /**
     * Add Filters
     * @param urlPattern URL
     * @param filterClass Filter Class
     * @return Engine config
     */
    public ServerConfig addFilter(String urlPattern, Class<? extends Filter> filterClass) {
        this.filters.put(urlPattern, filterClass);
        return this;
    }

    /**
     * Get static resource path
     * @return Static resource path
     */
    public String getStaticResourcePath() {
        return staticResourcePath;
    }

    /**
     * Get JAX-RS package list
     * @return JAX-RS package list
     */
    public String getPackageJAXRS() {
        return packageJAXRS;
    }

    /**
     * Get Filters
     * @return Filters
     */
    public Map<String, Class<? extends Filter>> getFilters() {
        return filters;
    }

}
