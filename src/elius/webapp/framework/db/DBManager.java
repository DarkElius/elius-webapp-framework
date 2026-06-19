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

package elius.webapp.framework.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import elius.webapp.framework.security.secret.SecretCredentials;

public class DBManager {

	// Get logger
	private static Logger logger = LogManager.getLogger(DBManager.class);
	
	// Database connection
	private Connection connection;
	
	// Connection type
	private DBConnectionType connectionType;
	
	// Data source
	private DataSource dataSource;
	
	// Data source name
	private String dataSourceName;
	
	// Connection URL
	private String connUrl;
	
	// Driver class name
	private String driver;
	
	// Authentication credentials
	private SecretCredentials credentials;
	
	// Fill flag
	private DBDataConversionSettings dataConversionSettings;
	
	
	/**
	 * Constructor for datasource connections
	 * @param dataSourceName Data source name
	 */
	public DBManager(String dataSourceName) {
		// Set connection type to jdbc datasource
		connectionType = DBConnectionType.JDBC;
		
		// Initialize data source
		dataSource = null;
		
		// Initialize connection
		connection = null;
		
		// Data source name
		this.dataSourceName = dataSourceName;
		
		// Set data conversion setting to default
		dataConversionSettings = DBDataConversionSettings.DEFAULT;
		
	}
	

	/**
	 * Constructor do direct connections
	 * @param connUrl Database connection url
	 * @param driver Class name driver for database connection
	 * @param credentials Authorization credentials
	 */
	public DBManager(String connUrl, String driver, SecretCredentials credentials) {
		// Set connection type to direct
		connectionType = DBConnectionType.DIRECT;
		
		// Initialize connection
		connection = null;

		// Set connection URL
		this.connUrl = connUrl;
		
		// Set Driver class name
		this.driver = driver;
		
		// Set authorization credentials
		this.credentials = credentials;
		
		// Set data conversion setting to default
		dataConversionSettings = DBDataConversionSettings.DEFAULT;
		
	}
	
	
	/**
	 * Execute SQL code
	 * @param sql SQL
	 * @param parms SQL parameters
	 * @return Object table
	 */
	public List<Map<String, Object>> executeQuery(String sql, Object... parms) {
		
		// Log SQL, do not trace SQL code for security reasons
		logger.trace("Execute sql");

		// Error during connection
		if(0 != connect()) {
			return null;
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
			logger.trace("Number of row selected is " + table.size());

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
		// Log SQL, do not trace SQL code for security reasons
		logger.trace("Execute sql");
		
		// Error during connection
		if(0 != connect()) {
			return 1;
		}

		// Return Code
		int rc = 0;		
		
		// Start DROP / CREATE
		try {
			// Create /drop statement from connection
			Statement statement = connection.createStatement();
			
			// Create / drop table
			statement.execute(sql);

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
			logger.trace("Number of row updated is " + rows);
			
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
	private static final void fillPreparedStatement(final PreparedStatement pStmt, DBDataConversionSettings fillFlag, final Object... parms)
			throws SQLException {
		

		// Convert data if specified
		if(DBDataConversionSettings.EMPTY_STRING_TO_NULL == fillFlag) {
		
			// Convert empty strings to null
			DBDataConversion.convertEmptyStrings(null, parms);
 
		} else if(DBDataConversionSettings.EMPTY_STRING_TO_SPACE == fillFlag) {
			
			// Convert empty strings to space
			DBDataConversion.convertEmptyStrings(" ", parms);		
			
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
		// Return code
		int rc = 0;
		
		// Database connection based on type
		switch(connectionType) {
			
			case DIRECT:
				// Log messages
				logger.trace("Direct database connection selected");
				// Call direct connection
				rc = connectDirect();
				break;
				
			case JDBC:
				// Log messages
				logger.trace("Jdbc database connection selected");
				// Call jdbc connection
				rc = connectJdbc();
				break;
				
			default:
				rc = 1;
		}
		
		// Exit with connect return code
		return rc;
	}
	
	
	/**
	 * Connect directly to database
	 * @return 0 Opened, 1 Error
	 */
	private int connectDirect() {
		
		// Set default return code
		int rc = 0;
		
		try {
			//Class.forName(driver).newInstance();
			Class.forName(driver);
			connection = DriverManager.getConnection(connUrl, credentials.getUserId(), credentials.getPassword());
		} catch (Exception e) {
			// Log error
			logger.catching(e);
			// Set error
			connection = null;
			// Set return code
			rc = 1;
		}

		// Return code
		return rc;
	}
	
	
	
	/**
	 * Connect to database via jdbc
	 * @return 0 Opened, 1 Error
	 */
	private int connectJdbc() {
		
		// Set default return code
		int rc = 0;
		
		try {
			// Initialize context
			InitialContext ic = new InitialContext();

			// Get application context
			Context xmlContext = (Context) ic.lookup("java:/comp/env");

			// Get data source
			dataSource = (DataSource) xmlContext.lookup(dataSourceName);				

			// Create connection
			connection = dataSource.getConnection();

			// Log the connection
			logger.trace("Connected to database " + connection.getMetaData().getURL());

		} catch (Exception e) {
			// Log error
			logger.catching(e);
			// Set error
			connection = null;
			// Set return code
			rc = 1;
		}

		// Return code
		return rc;
	}
	
	
	/**
	 * Close database connection
	 * @return 0 Closed, 1 Error
	 */
	private int close() {
		
		// Set default return code
		int rc = 0;
		
		try {
			// Close connection
			connection.close();
			
			// Log close connection
			logger.trace("Database connection closed");
			
		} catch (SQLException e) {
			// Log error
			logger.catching(e);
			
			// Set error code
			rc = 1;
		} 
		
		// Return code
		return rc;
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
	public DBDataConversionSettings getDataConversionSettings() {
		return dataConversionSettings;
	}



	/**
	 * Set data conversion settings
	 * @param dataConversionSettings Data conversion settings
	 */
	public void setDataConversionSettings(DBDataConversionSettings dataConversionSettings) {
		this.dataConversionSettings = dataConversionSettings;
	}

	
}