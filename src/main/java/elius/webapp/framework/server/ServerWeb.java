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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import elius.webapp.framework.application.ApplicationLifecycle;
import jakarta.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ServerWeb.
 * <p>
 * Start the web server.
 */
public class ServerWeb {
    
    // Get logger
	private static Logger logger = LogManager.getLogger(ServerWeb.class);

	/**
	 * Default constructor
	 */
	private ServerWeb() {
        /* This class should not be instantiated */
	}
    
    /**
     * Start web application
     * @param port TCP Server Port
     * @param config Engine configuration
     * @param lifecycle Application lifecycle
     */
    public static void startWebApp(int port, ServerConfig config, ApplicationLifecycle lifecycle) {
        try {
            // Execute startup initialization before engine start
            if (lifecycle != null) {
                logger.info("Execute startup initialization...");
                lifecycle.onStartup();
            }

            // Configure virtual threads
            QueuedThreadPool threadPool = new QueuedThreadPool();
            threadPool.setVirtualThreadsExecutor(Executors.newVirtualThreadPerTaskExecutor());

            Server server = new Server(threadPool);
            ServerConnector connector = new ServerConnector(server);
            connector.setPort(port);
            server.addConnector(connector);

            // Servlet Context
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);

            // Filter Map
            if (config.getFilters() != null) {
                config.getFilters().forEach((pattern, filterClass) -> {
                    context.addFilter(new FilterHolder(filterClass), pattern, EnumSet.of(DispatcherType.REQUEST));
                });
            }

            // API JAX-RS (Jersey) Mapping
            if (config.getPackageJAXRS() != null) {
                ServletHolder jerseyServlet = new ServletHolder(new ServletContainer());
                jerseyServlet.setInitParameter("jersey.config.server.provider.packages", config.getPackageJAXRS());

                // Disable WADL and linked warning
                jerseyServlet.setInitParameter("jersey.config.server.wadl.disableWadl", "true");

                context.addServlet(jerseyServlet, "/api/*");
            }

            // Static file Mapping
            if (config.getStaticResourcePath() != null) {
                ServletHolder staticServlet = context.addServlet(org.eclipse.jetty.ee10.servlet.DefaultServlet.class, "/");
                
                // Convert textual path into URI (es. "file:///./frontend") to remove resourceBase deprecation
                String baseResourceUri = java.nio.file.Paths.get(config.getStaticResourcePath())
                                            .toAbsolutePath()
                                            .toUri()
                                            .toString();

                staticServlet.setInitParameter("baseResource", baseResourceUri);
                staticServlet.setInitParameter("dirAllowed", "false");
                staticServlet.setInitParameter("welcomeServlets", "true");
            }            

            // Shutdown Hook for graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    logger.info("Graceful shutdown...");
                    if (server.isRunning()) server.stop();
                    if (lifecycle != null) lifecycle.onShutdown();
                } catch (Exception e) {
                    logger.catching(e);
                }
            }));

            server.start();

            logger.info("Server started on port: {}", port);

            server.join();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.catching(e);
            System.exit(1);
        } catch (Exception e) { 
            logger.catching(e);
            System.exit(1);
        }
    }
}
