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

package elius.webapp.framework.plugin;


import java.io.File;
import java.net.URLClassLoader;


/**
 * Runtime handle for a loaded plugin.
 *
 * <p>This class groups together the plugin instance and all resources
 * required to manage its lifecycle, such as the dedicated class loader
 * and the source JAR file.</p>
 *
 * <p>The handle is used by the PluginManager to:</p>
 * <ul>
 *   <li>track loaded plugins</li>
 *   <li>perform unload and reload operations</li>
 *   <li>access plugin metadata and runtime resources</li>
 * </ul>
 */
public class PluginHandle {

    /**
     * The instantiated plugin implementation.
     */
    private final Plugin plugin;

    /**
     * The class loader used to load the plugin and its dependencies.
     * Closing this class loader is required before unloading the plugin.
     */
    private final URLClassLoader classLoader;

    /**
     * The JAR file from which the plugin was loaded.
     * This reference can be used for reload operations.
     */
    private final File jarFile;

    /**
     * Creates a new plugin handle.
     *
     * @param plugin the instantiated plugin
     * @param classLoader the class loader associated with the plugin
     * @param jarFile the plugin JAR file
     */
    public PluginHandle(
            Plugin plugin,
            URLClassLoader classLoader,
            File jarFile) {

        this.plugin = plugin;
        this.classLoader = classLoader;
        this.jarFile = jarFile;
    }

    /**
     * Returns the plugin instance.
     *
     * @return the loaded plugin
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Returns the class loader used by the plugin.
     *
     * @return the plugin class loader
     */
    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Returns the JAR file from which the plugin was loaded.
     *
     * @return the plugin JAR file
     */
    public File getJarFile() {
        return jarFile;
    }
}