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

/**
 * Base contract for all plugins.
 *
 * <p>Implementations of this interface can be discovered and loaded
 * by the {@code PluginManager} through Java's {@code ServiceLoader}
 * mechanism.</p>
 *
 * <p>Plugins may optionally participate in the application lifecycle
 * by overriding the {@code start()} and {@code stop()} methods.</p>
 */
public interface Plugin {

    /**
     * Returns the unique plugin name.
     *
     * @return the plugin name
     */
    String getName();

    /**
     * Returns the unique plugin description.
     *
     * @return the plugin description
     */
    String getDescription();

    /**
     * Returns the plugin version.
     *
     * @return the plugin version
     */
    String getVersion();

    /**
     * Invoked after the plugin has been loaded.
     *
     * <p>Plugins can override this method to perform initialization
     * tasks such as allocating resources, registering listeners or
     * starting background services.</p>
     *
     * @throws Exception if the plugin cannot be started
     */
    default void start() throws Exception {
    }

    /**
     * Invoked before the plugin is unloaded.
     *
     * <p>Plugins can override this method to release resources,
     * unregister listeners, stop background services and perform
     * any required cleanup operations.</p>
     *
     * @throws Exception if an error occurs during shutdown
     */
    default void stop() throws Exception {
    }
}
