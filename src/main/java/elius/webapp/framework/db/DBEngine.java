package elius.webapp.framework.db;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class DBEngine {
    // Get logger
	private static Logger logger = LogManager.getLogger(DBEngine.class);

    private HikariDataSource dataSource;

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
        HikariConfig hConfig = new HikariConfig();
        hConfig.setJdbcUrl(jdbcUrl);
        hConfig.setUsername(username);
        hConfig.setPassword(password);
        
        // Optimization for virtual threads
        hConfig.setMaximumPoolSize(maxPoolSize); 
        hConfig.setMinimumIdle(Math.min(5, maxPoolSize));
        
        this.dataSource = new HikariDataSource(hConfig);

        logger.trace("Datasource pool created");

        return this.dataSource;
    }

    /**
     * Close pool at shutdown
     */
    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.trace("Datasource pool closed");
        }
    }
}