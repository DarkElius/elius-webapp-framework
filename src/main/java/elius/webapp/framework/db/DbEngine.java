package elius.webapp.framework.db;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * DbEngine.
 * <p>
 * Class that managage the database connections.
 */
public class DbEngine {

    // Get logger
	private static Logger logger = LogManager.getLogger(DbEngine.class);

    // Data source
    private HikariDataSource dataSource;

    /**
	 * Default constructor
	 */
	public DbEngine() {
        /* Nothing to initialize */
	}

    // Metodo per inizializzare il pool partendo dai dati dell'applicazione
    /**
     * Create pool
     * @param jdbcUrl Jdbc Url
     * @param username Username
     * @param password Password
     * @param maxPoolSize Max pool size
     * @return Datasource pool
     */
    public DataSource createPool(String jdbcUrl, String username, String password, int maxPoolSize) {

        logger.traceEntry("jdbcUrl={} username={} maxPoolSize={}", jdbcUrl, username, maxPoolSize);

        HikariConfig hConfig = new HikariConfig();
        hConfig.setJdbcUrl(jdbcUrl);
        hConfig.setUsername(username);
        hConfig.setPassword(password);
        
        // Optimization for virtual threads
        hConfig.setMaximumPoolSize(maxPoolSize); 
        hConfig.setMinimumIdle(Math.min(5, maxPoolSize));
        
        this.dataSource = new HikariDataSource(hConfig);

        logger.traceExit();

        return this.dataSource;
    }

    /**
     * Close pool at shutdown
     */
    public void closePool() {
        logger.traceEntry();

        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.trace("Datasource pool closed");
        }

        logger.traceExit();
    }
}