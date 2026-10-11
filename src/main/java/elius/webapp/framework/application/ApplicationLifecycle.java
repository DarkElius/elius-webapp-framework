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
 * ApplicationLifecycle.
 * <p>
 * Define the application entrypoints for startup and graceful shutdown.
 */
public interface ApplicationLifecycle {
    
    /**
     * Called before web engine start.
     * @throws Exception during startup.
     */
    void onStartup() throws Exception;

    /**
     * Called after web engine stop / JVM shutdown.
     * @throws Exception during shutdown.
     */
    void onShutdown() throws Exception;

}
