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

package elius.webapp.framework.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


/**
 * DbManager
 * <p>
 * Class that provide standards method to execute SQL code.
 */
public class DbManager {

	// Get logger
	private static Logger logger = LogManager.getLogger(DbManager.class);
	
	// Data source
	private final DataSource dataSource;

	// Database connection
	private Connection connection;

	// Fill flag
	private DbDataConversionSettings dataConversionSettings;
	
	/**
	 * Constructor for datasource connections
	 * @param dataSource Data source
	 */
	public DbManager(DataSource dataSource) {
		// Data source name
		this.dataSource = dataSource;
		
		// Set data conversion setting to default
		dataConversionSettings = DbDataConversionSettings.DEFAULT;	
	}
	
	
	/**
	 * Execute SQL code
	 * @param sql SQL
	 * @param parms SQL parameters
	 * @return Object table
	 */
	public List<Map<String, Object>> executeQuery(String sql, Object... parms) {

		// Error during connection
		if(0 != connect()) {
			return Collections.emptyList();
		}
		
		// List of rows (columns / value)
		List<Map<String, Object>> table = null;			
			
		// Insert object
		try {
			
			// Prepare statement from connection
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			
			// Fill parameters in the statement
			fillPreparedStatement(preparedStatement, dataConversionSettings, parms);
		
			// Insert row
			ResultSet rs = preparedStatement.executeQuery();
			
			// Create list
			table = new ArrayList<>();

			// Get meta data
			ResultSetMetaData metaData = rs.getMetaData();
			
			// Read rows from database
			while ( rs.next() ) {
				// Allocate row
				Map<String, Object> tableRow = new LinkedHashMap<>();
				
				// Get every column
				for (int c = 1; c <= metaData.getColumnCount(); c++) {
					// Insert column label/value in the row
					tableRow.put(metaData.getColumnLabel(c), rs.getObject(c));
				}
				
				// Add row to table
				table.add(tableRow);
			}
			
			// Log number of rows
			logger.traceExit("Number of row selected is %d ", table.size());

			// Close result set
			rs.close();

		} catch (SQLException e) {
			// Log SQL error
			logger.warn("SQLState={}, ErrorCode={}", e.getSQLState(), e.getErrorCode());
			
			// Log Error
			logger.catching(e);

		} catch (Exception e) {
			// Log Error
			logger.catching(e);

		} finally {	
			// Close database connection
			close();
		}		

		// Return object table
		return table;
	}
	
	
	
	/**
	 * Execute table drop / create
	 * @param sql SQL to be executed
	 * @return 0 Successful, 1 Error
	 */
	public int execute(String sql) {
		
		// Error during connection
		if(0 != connect()) {
			return 1;
		}

		// Return Code
		int rc = 0;		
		
		// Start DROP / CREATE
		try (Statement statement = connection.createStatement()) {
			
			// Create / drop table
			statement.execute(sql);

		} catch (SQLException e) {
			// Log SQL error
		logger.catching(e);
			rc = 1;
			
		} finally { 
			// Close database connection
			close();
		}

		// Exit with return code
		return rc;
	}
	
	
	
	/**
	 * Update 
	 * @param sql SQL to be executed
	 * @param parms Parameters
	 * @return 0 Successfully, 1 Error
	 */
	public int update(String sql, Object... parms) {
		// Return Code
		int rc = 0;
			
		// Error during connection
		if(0 != connect()) {
			return 1;
		}
		
		try {
			// Prepare statement from connection
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			
			// Fill parameters in the statement
			fillPreparedStatement(preparedStatement, dataConversionSettings, parms);

			// Update row
			int rows = preparedStatement.executeUpdate();
			
			// Log inserted rows
			logger.traceExit("Number of row updated is {}", rows);
			
			// Row not updated
			if (rows <= 0) {
				// Log the error
				logger.error("No row uptdated");
				
				// Set response code for client
				return 1;
			}			
		} catch (SQLException e) {
			// Log SQL error
			logger.warn("SQLState={}, ErrorCode={}", e.getSQLState(), e.getErrorCode());
			
			// Log Error
			logger.catching(e);
			
			// Set return code
			rc = 1;
		} catch (Exception e) {
			// Log the error
			logger.catching(e);

			// Set return code
			rc = 1;
		} finally {		
			// Close database connection
			if (0 != close())
				rc = 1;
		}
		
		return rc;		
	}
	
	
	
	
 	/**
 	 * Fill parameters inside prepared statement
 	 * @param pStmt Prepared statement
 	 * @param fillFlag Fill flag for data conversion
 	 * @param parms Parameters
 	 * @throws SQLException
 	 */
	private static final void fillPreparedStatement(final PreparedStatement pStmt, DbDataConversionSettings fillFlag, final Object... parms)
			throws SQLException {
		

		// Convert data if specified
		if(DbDataConversionSettings.EMPTY_STRING_TO_NULL == fillFlag) {
		
			// Convert empty strings to null
			DbDataConversion.convertEmptyStrings(null, parms);
 
		} else if(DbDataConversionSettings.EMPTY_STRING_TO_SPACE == fillFlag) {
			
			// Convert empty strings to space
			DbDataConversion.convertEmptyStrings(" ", parms);		
			
		}
		
		// Read parameters
		for (int i = 0; i < parms.length; i++) {
			
			// Null objects
			if(null == parms[i]) {
				
				// Set null parameter to statement starting from 1
				pStmt.setNull(i + 1, java.sql.Types.NULL);
				
			} else {

				// Set parameter to statement starting from 1
				pStmt.setObject(i + 1, parms[i]);
				
			}
		}	
		
	}
	
	
	
	/**
	 * Connect to database
	 * @return 0 Opened, 1 Error
	 */
	private int connect() {
		try {
            if (dataSource == null) {
                logger.error("DataSource not initialized!");
                return 1;
            }
            // Get connection from pool
            this.connection = dataSource.getConnection();
            return 0;
        } catch (Exception e) {
            logger.catching(e);
            this.connection = null;
            return 1;
        }
	}

	
	
	/**
	 * Close database connection
	 * @return 0 Closed, 1 Error
	 */
	private int close() {
		
        if (connection != null) {
            try {
				// Connection back to the pool
                connection.close();
                logger.trace("Connection back to the pool");
            } catch (SQLException e) {
                logger.catching(e);
                return 1;
            } finally {
                connection = null;
            }
        }
        return 0;
	}

	

	/**
	 * Get connection instance
	 * @return Connection or null
	 */
	public Connection getConnection() {
		return connection;
	}


	
	/**
	 * Get data conversion settings
	 * @return Data conversion settings
	 */
	public DbDataConversionSettings getDataConversionSettings() {
		return dataConversionSettings;
	}



	/**
	 * Set data conversion settings
	 * @param dataConversionSettings Data conversion settings
	 */
	public void setDataConversionSettings(DbDataConversionSettings dataConversionSettings) {
		this.dataConversionSettings = dataConversionSettings;
	}

	
}