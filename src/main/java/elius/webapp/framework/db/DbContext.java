package elius.webapp.framework.db;

import javax.sql.DataSource;

/**
 * Global holder for the application database DataSource.
 * Provides thread-safe static access to the database pool initialized during startup.
 */
public final class DbContext {

    private static DataSource dataSource;

    private DbContext() {
        // Prevent instantiation of utility class
    }

    /**
     * Sets the application DataSource.
     * 
     * @param dataSource the DataSource to set
     */
    public static void setDataSource(DataSource dataSource) {
        DbContext.dataSource = dataSource;
    }

    /**
     * Retrieves the global application DataSource.
     * 
     * @return the active DataSource, or null if not initialized
     */
    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Clears the DataSource reference during shutdown.
     */
    public static void clear() {
        DbContext.dataSource = null;
    }
}