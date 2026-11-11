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

package elius.webapp.framework.plugin;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import elius.webapp.framework.exceptions.PluginException;


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
	 * Default constructor
	 */
	public PluginManager() {
        /* Nothing to initialize */
	}


    /**
     * Loaded plugins indexed by plugin name.
     */
    private final Map<String, PluginHandle> plugins =
            new LinkedHashMap<>();

            
    /**
     * Scans the specified directory and loads all JAR files found.
     *
     * @param directoryPath plugin directory
     * @throws PluginException if a plugin cannot be loaded
     */
    public void loadPlugins(String directoryPath) throws PluginException {

        // Log entry parameters
        logger.traceEntry("directoryPath={}", directoryPath);

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

        // Log exit
        logger.traceExit("Number of loaded plugins: {}", plugins.size());
    }

    /**
     * Loads a single plugin JAR file.
     *
     * @param jarFile plugin JAR file
     * @throws PluginException if loading fails
     */
    public void loadPlugin(File jarFile) throws PluginException {

        try {
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

                logger.traceExit("Plugin loaded={}", plugin);
            }

        } catch (MalformedURLException e) {
            logger.error("Invalid plugin JAR URL for file: {}", jarFile.getAbsolutePath(), e);
            throw new PluginException("Invalid plugin URL: " + jarFile.getName(), e);
            
        } catch (ServiceConfigurationError e) {
            logger.error("Error loading plugin configuration from JAR: {}", jarFile.getAbsolutePath(), e);
            throw new PluginException("Error loading plugin configuration: " + jarFile.getName(), e);
            
        } catch (Exception e) {
            logger.error("Error starting plugin from JAR: {}", jarFile.getAbsolutePath(), e);
            throw new PluginException("Failed to start plugin: " + jarFile.getName(), e);
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

        // Log entry parameters
        logger.traceEntry("pluginName={}", pluginName);        

        PluginHandle handle =
                plugins.remove(pluginName);

        if (handle == null) {
            return;
        }

        try {

            handle.getPlugin().stop();

            handle.getClassLoader().close();

            logger.info("Unloaded plugin: {}", pluginName);

        } catch (Exception e) {

            logger.error("Failed to unload plugin: {}", pluginName);

            logger.catching(e);
        }

    }


    /**
     * Unloads all currently loaded plugins.
     */
    public void unloadAllPlugins() {
        // Log entry
        logger.traceEntry();             

        String[] pluginNames =
                plugins.keySet().toArray(new String[0]);

        for (String pluginName : pluginNames) {
            unloadPlugin(pluginName);
        }

        // Log exit
        logger.traceExit();        
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
        return plugins.values().stream()
                .map(PluginHandle::getPlugin)
                .toList();
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