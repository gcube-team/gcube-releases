
package org.gcube.portlets.user.tdw.datasource.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.gcube.portlets.user.tdw.datasource.jdbc.dialect.SQLDialect;
import org.gcube.portlets.user.tdw.datasource.jdbc.dialect.SQLDialectManager;
import org.gcube.portlets.user.tdw.server.datasource.DataSource;
import org.gcube.portlets.user.tdw.server.datasource.DataSourceException;
import org.gcube.portlets.user.tdw.server.datasource.Direction;
import org.gcube.portlets.user.tdw.server.datasource.util.TableJSonBuilder;
import org.gcube.portlets.user.tdw.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdw.shared.model.ColumnType;
import org.gcube.portlets.user.tdw.shared.model.TableDefinition;
import org.gcube.portlets.user.tdw.shared.model.TableId;
import org.gcube.portlets.user.tdw.shared.model.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class JDBCDataSource implements DataSource {

	public static final String JSON_ROWS_FIELD = "ROWS";
	public static final String JSON_TOTAL_LENGTH_FIELD = "total";
	public static final String JSON_OFFSET_FIELD = "offset";
	
	
	public static JDBCDataSource createJDBCDataSource(String dataSourceFactoryId, Connection connection, String tableName, SQLDialect dialect)
	{
		JDBCDataSource dataSource = new JDBCDataSource(dataSourceFactoryId, connection, tableName, dialect);
		return dataSource;
	}
	
	public static JDBCDataSource createJDBCDataSource(String dataSourceFactoryId, Connection connection, String tableName) throws DataSourceException
	{
		SQLDialect dialect;
		try {
			DatabaseMetaData metaData = connection.getMetaData();
			dialect = SQLDialectManager.getDialect(metaData.getDatabaseProductName(), metaData.getDatabaseMajorVersion(), metaData.getDatabaseMinorVersion());
		} catch(Exception e)
		{
			throw new DataSourceException("An error occurred initializing the SQL dialect", e);
		}
		
		JDBCDataSource dataSource = createJDBCDataSource(dataSourceFactoryId, connection, tableName, dialect);
		return dataSource;
	}
	
	public static JDBCDataSource createJDBCDataSource(String dataSourceFactoryId, String jdbcConnectionUrl, String tableName) throws DataSourceException
	{
		Connection connection;
		try {
			connection = DriverManager.getConnection(jdbcConnectionUrl);
		} catch (SQLException e) {
			throw new DataSourceException("An error occurred initializing the DB connection", e);
		}
		
		JDBCDataSource dataSource = createJDBCDataSource(dataSourceFactoryId, connection, tableName);
		return dataSource;
	}
	
	public static JDBCDataSource createJDBCDataSource(String dataSourceFactoryId, String jdbcConnectionUrl, String tableName, SQLDialect dialect) throws DataSourceException
	{
		Connection connection;
		try {
			connection = DriverManager.getConnection(jdbcConnectionUrl);
		} catch (SQLException e) {
			throw new DataSourceException("An error occurred initializing the DB connection", e);
		}
		
		JDBCDataSource dataSource = createJDBCDataSource(dataSourceFactoryId, connection, tableName, dialect);
		return dataSource;
	}
	

	protected Logger logger = LoggerFactory.getLogger(JDBCDataSource.class);
	protected String dataSourceFactoryId;
	protected Connection connection;
	protected SQLDialect dialect;
	protected String tableName;
	protected TableDefinition tableDefinition;
	protected int tableSize = -1;
	protected Map<String, PreparedStatement> preparedStatementCache;
	protected ColumnDefinition autogeneratePrimaryColumn = null;
	
	protected TableJSonBuilder jsonBuilder;
	
	public JDBCDataSource(String dataSourceFactoryId, Connection connection, String tableName, SQLDialect dialect)
	{
		this.dataSourceFactoryId = dataSourceFactoryId;
		this.connection = connection;
		this.tableName = tableName;
		this.dialect = dialect;
		this.preparedStatementCache = new HashMap<String, PreparedStatement>();
	}
	
	/**
	 * @param dataSourceFactoryId
	 * @param connection
	 * @param tableName
	 * @throws DataSourceException
	 * @Deprecated please use the new constructor helper
	 */
	public JDBCDataSource(String dataSourceFactoryId, Connection connection, String tableName) throws DataSourceException
	{
		try {
			DatabaseMetaData metaData = connection.getMetaData();
			dialect = SQLDialectManager.getDialect(metaData.getDatabaseProductName(), metaData.getDatabaseMajorVersion(), metaData.getDatabaseMinorVersion());
		} catch(Exception e)
		{
			logger.error("An error occurred initializing the SQL dialect", e);
			throw new DataSourceException("An error occurred initializing the SQL dialect", e);
		}
		
		this.dataSourceFactoryId = dataSourceFactoryId;
		this.tableName = tableName;
		this.preparedStatementCache = new HashMap<String, PreparedStatement>();
	}

	/**
	 * @param dataSourceFactoryId
	 * @param jdbcConnectionUrl
	 * @param tableName
	 * @throws DataSourceException
	 * @Deprecated please use the new constructor helper
	 */
	public JDBCDataSource(String dataSourceFactoryId, String jdbcConnectionUrl, String tableName) throws DataSourceException
	{
		try {
			connection = DriverManager.getConnection(jdbcConnectionUrl);
		} catch (SQLException e) {
			logger.error("An error occurred initializing the DB connection", e);
			throw new DataSourceException("An error occurred initializing the DB connection", e);
		}

		try {
			DatabaseMetaData metaData = connection.getMetaData();
			dialect = SQLDialectManager.getDialect(metaData.getDatabaseProductName(), metaData.getDatabaseMajorVersion(), metaData.getDatabaseMinorVersion());
		} catch(Exception e)
		{
			logger.error("An error occurred initializing the SQL dialect", e);
			throw new DataSourceException("An error occurred initializing the SQL dialect", e);
		}

		this.dataSourceFactoryId = dataSourceFactoryId;
		this.tableName = tableName;
		this.preparedStatementCache = new HashMap<String, PreparedStatement>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getDataSourceFactoryId() {
		return dataSourceFactoryId;
	}
	
	/**
	 * Close this DataSource releasing all the allocated resources.
	 * @throws DataSourceException 
	 */
	public void close() throws DataSourceException
	{
		try {
			connection.close();
		} catch (SQLException e) {
			logger.warn("An error occurred closing the database connection", e);
			throw new DataSourceException("An error occurred closing the database connection", e);
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public TableDefinition getTableDefinition() throws DataSourceException {

		if (tableDefinition==null) tableDefinition = extractTableDefinition();
		return tableDefinition;
	}

	protected TableDefinition extractTableDefinition() throws  DataSourceException
	{
		try {
			List<ColumnDefinition> columns = getColumnDefinitions();

			TableId id = new TableId(dataSourceFactoryId, tableName);
			
			TableDefinition tableDefinition = new TableDefinition(id, tableName, JSON_ROWS_FIELD, JSON_TOTAL_LENGTH_FIELD, JSON_OFFSET_FIELD, columns);

			String primaryKey = getPrimaryKey();

			if (primaryKey == null) {
				autogeneratePrimaryColumn = createPrimaryKeyColumn(columns);
				tableDefinition.addColumn(autogeneratePrimaryColumn);
				primaryKey = autogeneratePrimaryColumn.getId();
			}
			tableDefinition.setModelKeyColumnId(primaryKey);			

			return tableDefinition;
		} catch (SQLException e) {
			logger.error("An error occurred retrieving columns informations", e);
			throw new DataSourceException("An error occurred retrieving columns informations", e);
		}
	}

	protected List<ColumnDefinition> getColumnDefinitions() throws SQLException, DataSourceException
	{
		ResultSet columnsResultSet = connection.getMetaData().getColumns(null, null, tableName, null);
		if (!columnsResultSet.next()) {
			logger.error("Columns definitions for table \""+tableName+"\" not found!");
			throw new DataSourceException("Columns definitions for table \""+tableName+"\" not found!");
		}

		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		do {
			ColumnDefinition column = getColumnDefinition(columnsResultSet);
			columns.add(column);
		} while(columnsResultSet.next());

		return columns;
	}

	protected ColumnDefinition getColumnDefinition(ResultSet columns) throws SQLException
	{
		String columnName = columns.getString("COLUMN_NAME");
		ColumnDefinition columnDefinition = new ColumnDefinition(columnName, columnName);
		
		int dataType = columns.getInt("DATA_TYPE");
		ValueType type = getValueType(dataType);
		columnDefinition.setValueType(type);
		
		int ordinalPosition = columns.getInt("ORDINAL_POSITION");
		columnDefinition.setPosition(ordinalPosition);
		
		columnDefinition.setWidth(100);
		columnDefinition.setEditable(false);
		columnDefinition.setVisible(true);
		
		return columnDefinition;
	}

	protected String getPrimaryKey() throws SQLException, DataSourceException
	{
		ResultSet columnsResultSet = connection.getMetaData().getPrimaryKeys(null, null, tableName);
		if (!columnsResultSet.next()) {
			logger.trace("Primary key not found");
			return null;
		}

		String primaryKeyColumnName = columnsResultSet.getString("COLUMN_NAME");
		columnsResultSet.close();

		return primaryKeyColumnName;
	}

	protected ColumnDefinition createPrimaryKeyColumn(List<ColumnDefinition> columns)
	{
		List<String> ids = new ArrayList<String>(columns.size());
		for (ColumnDefinition column:columns) ids.add(column.getId());

		String id = "idColumn";
		for (int i = 0; ids.contains(id); id = "idColumn"+i++);

		return new ColumnDefinition(id, id, ValueType.INTEGER, -1, false, false, ColumnType.SYSTEM);
	}

	protected ValueType getValueType(int sqlType)
	{
		switch(sqlType){

			case Types.BOOLEAN: return ValueType.BOOLEAN;
			case Types.DATE: return ValueType.DATE;
			case Types.DECIMAL: return ValueType.DOUBLE;
			case Types.REAL: return ValueType.FLOAT;
			case Types.DOUBLE: return ValueType.DOUBLE;
			case Types.FLOAT: return ValueType.FLOAT;
			case Types.BIGINT: return ValueType.LONG;
			case Types.NUMERIC:
			case Types.INTEGER: return ValueType.INTEGER;

			//FIXME DERBY case
			case Types.SMALLINT: return ValueType.BOOLEAN;

			/*case Types.TIME: return ValueType.TIME;*/
			//FIXME 
			case Types.TIMESTAMP: return ValueType.DATE;
			case Types.CHAR: return ValueType.STRING;
			case Types.VARCHAR: return ValueType.STRING;

			default: {
				logger.error("Unsupported type sqlType "+sqlType);
				return ValueType.STRING;
			}
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDataAsJSon(int start, int limit, String sortingColumn, Direction direction) throws DataSourceException {
		logger.trace("getDataAsJSon start: "+start+" limit: "+limit+" sortingColumn: "+sortingColumn+" direction: "+direction);

		int tableSize = getTableSize();
		start = Math.max(0, start);
		start = Math.min(start, tableSize);
		if (start+limit>tableSize) limit = tableSize-start;
		logger.trace("checked bounds start: "+start+" limit: "+limit);

		TableDefinition tableDefinition = getTableDefinition();
		if (sortingColumn != null && tableDefinition.getColumns().get(sortingColumn) == null) {
			logger.error("The specified sorting column \""+sortingColumn+"\" don't exists");
			throw new DataSourceException("The specified sorting column \""+sortingColumn+"\" don't exists");
		}

		try {
			PreparedStatement preparedStatement = getPreparedStatement(sortingColumn, direction, start, limit);
			logger.trace("Querying database");
			ResultSet resultSet = preparedStatement.executeQuery();
			logger.trace("Processing database results");
			String json = getJSon(resultSet, start);
			logger.trace("Returning json");
			return json;
		} catch (SQLException e)
		{
			logger.error("An error occurred extracting json from database");
			throw new DataSourceException("An error occurred extracting json from database", e);
		}
	}

	protected PreparedStatement getPreparedStatement(String sortingColumn, Direction sortingDirection, int start, int limit) throws DataSourceException, SQLException
	{
		String key = String.valueOf(sortingDirection)+"-"+String.valueOf(sortingColumn);
		PreparedStatement preparedStatement = preparedStatementCache.get(key);
		if (preparedStatement == null) {
			preparedStatement = dialect.createDataPreparedStatement(connection, tableName, sortingColumn, sortingDirection, start, limit);
			preparedStatementCache.put(key, preparedStatement);
		}

		preparedStatement.clearParameters();
		dialect.setDataPreparedStatementParameters(preparedStatement, tableName, sortingColumn, sortingDirection, start, limit);
		return preparedStatement;
	}

	protected String getJSon(ResultSet resultSet, int start) throws SQLException, DataSourceException
	{
		TableJSonBuilder json = getBuilder();

		TableDefinition tableDefinition = getTableDefinition();
		Collection<ColumnDefinition> columns = tableDefinition.getColumns().values();

		json.startRows();
		
		int id = start;
		while(resultSet.next()){

			json.startRow();

			for (ColumnDefinition column:columns){

				String columnId = column.getId();

				if (autogeneratePrimaryColumn!=null && columnId.equals(autogeneratePrimaryColumn.getId())) {
					json.addValue(columnId, id);
				} else {

					switch (column.getValueType()) {
						case BOOLEAN: {
							Boolean b = resultSet.getBoolean(columnId);
							json.addValue(columnId, b);
						}  break;
						case DATE:{
							Date date = resultSet.getDate(columnId);
							json.addValue(columnId, date); 
						} break;
						case DOUBLE:{
							Double d = resultSet.getDouble(columnId);
							json.addValue(columnId, d);
						} break;
						case FLOAT:{
							Float f = resultSet.getFloat(columnId);
							json.addValue(columnId, f);
						} break;
						case INTEGER:{
							Integer i = resultSet.getInt(columnId);
							json.addValue(columnId, i);
						} break;
						case LONG:{
							Long l = resultSet.getLong(columnId);
							json.addValue(columnId, l);
						} break;
						case STRING: {
							String s = resultSet.getString(columnId);
							json.addValue(columnId, s);
						}break;
						default: logger.warn("Unknow value type "+column.getValueType());
					}
				}

			}
			id++;
			
			json.endRow();
		}

		json.endRows();
		
		int tableSize = getTableSize();
		json.setTotalLength(tableSize);
		
		json.setOffset(start);

		resultSet.close();

		logger.trace("produced "+(id-start)+" rows");

		json.close();

		return json.toString();
	}
	
	protected TableJSonBuilder getBuilder() throws DataSourceException
	{
		if (jsonBuilder == null) jsonBuilder = new TableJSonBuilder(getTableDefinition());
		else jsonBuilder.clean();
		
		return jsonBuilder;
	}

	protected int getTableSize() throws DataSourceException
	{
		if (tableSize<0) tableSize = calculateTableSize();
		return tableSize;
	}

	protected int calculateTableSize() throws DataSourceException
	{
		try {
			String query = dialect.getTableSizeQuery(tableName);
			logger.trace("table size query: "+query);
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				logger.error("An error occurred calculating the table size, no results.");
				throw new DataSourceException("An error occurred calculating the table size");
			}
			int size = resultSet.getInt(1);
			resultSet.close();
			return size;
		} catch (SQLException e)
		{
			logger.error("An error occurred calculating the table size.", e);
			throw new DataSourceException("An error occurred calculating the table size", e);
		}
	}



}
