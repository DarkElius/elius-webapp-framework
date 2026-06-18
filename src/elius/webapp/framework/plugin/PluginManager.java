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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Manages plugin discovery, loading and unloading.
 *
 * <p>Each plugin is loaded using its own dedicated
 * {@link URLClassLoader}. This design allows future support
 * for plugin isolation, unloading and hot reloading.</p>
 *
 * <p>Plugins are stored using their name as the unique key.</p>
 */
public class PluginManager {

	private static Logger logger = LogManager.getLogger(PluginManager.class);

    /**
     * Loaded plugins indexed by plugin name.
     */
    private final Map<String, PluginHandle> plugins =
            new LinkedHashMap<>();

    /**
     * Scans the specified directory and loads all JAR files found.
     *
     * @param directoryPath plugin directory
     * @throws Exception if a plugin cannot be loaded
     */
    public void loadPlugins(String directoryPath) throws Exception {

        File directory = new File(directoryPath);

        File[] jarFiles =
                directory.listFiles((dir, name) ->
                        name.toLowerCase().endsWith(".jar"));

        if (jarFiles == null) {
            return;
        }

        for (File jarFile : jarFiles) {
            loadPlugin(jarFile);
        }
    }

    /**
     * Loads a single plugin JAR file.
     *
     * @param jarFile plugin JAR file
     * @throws Exception if loading fails
     */
    public void loadPlugin(File jarFile) throws Exception {

        URLClassLoader classLoader =
                new URLClassLoader(
                        new URL[]{jarFile.toURI().toURL()},
                        Plugin.class.getClassLoader());

        ServiceLoader<Plugin> serviceLoader =
                ServiceLoader.load(
                        Plugin.class,
                        classLoader);

        for (Plugin plugin : serviceLoader) {

            plugin.start();

            PluginHandle handle =
                    new PluginHandle(
                            plugin,
                            classLoader,
                            jarFile);

            plugins.put(
                    plugin.getName(),
                    handle);

            logger.info(
                    "Loaded plugin: "
                    + plugin.getName()
                    + " v"
                    + plugin.getVersion());
        }
    }

    /**
     * Unloads the specified plugin.
     *
     * <p>The plugin lifecycle is terminated by invoking
     * {@code stop()} before the associated class loader
     * is closed.</p>
     *
     * @param pluginName plugin name
     */
    public void unloadPlugin(String pluginName) {

        PluginHandle handle =
                plugins.remove(pluginName);

        if (handle == null) {
            return;
        }

        try {

            handle.getPlugin().stop();

            handle.getClassLoader().close();

            logger.info(
                    "Unloaded plugin: "
                    + pluginName);

        } catch (Exception e) {

            logger.error(
                    "Failed to unload plugin: "
                    + pluginName);

            logger.error(e.getStackTrace());
        }

        /*
         * Suggest garbage collection in order to release
         * class loaders and plugin classes no longer referenced.
         */
        System.gc();
    }

    /**
     * Unloads all currently loaded plugins.
     */
    public void unloadAllPlugins() {

        String[] pluginNames =
                plugins.keySet().toArray(new String[0]);

        for (String pluginName : pluginNames) {
            unloadPlugin(pluginName);
        }
    }

    /**
     * Finds a loaded plugin by name.
     *
     * @param pluginName plugin name
     * @return the plugin instance, or null if not found
     */
    public Plugin findPlugin(String pluginName) {

        PluginHandle handle =
                plugins.get(pluginName);

        if (handle == null) {
            return null;
        }

        return handle.getPlugin();
    }

    /**
     * Returns all loaded plugins.
     *
     * <p>The returned collection is read-only.</p>
     *
     * @return loaded plugins
     */
    public Collection<Plugin> getLoadedPlugins() {

        List<Plugin> result = new ArrayList<>();

        for (PluginHandle handle : plugins.values()) {
            result.add(handle.getPlugin());
        }

        return Collections.unmodifiableCollection(result);
    }

    /**
     * Returns the number of loaded plugins.
     *
     * @return plugin count
     */
    public int size() {
        return plugins.size();
    }

    /**
     * Checks whether a plugin with the specified name
     * is currently loaded.
     *
     * @param pluginName plugin name
     * @return true if the plugin is loaded
     */
    public boolean isLoaded(String pluginName) {
        return plugins.containsKey(pluginName);
    }
}