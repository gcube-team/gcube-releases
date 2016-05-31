package org.gcube.data.analysis.tabulardata.cube.data;

import java.util.List;

import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;

/**
 * Database Wrangler offers methods for managing relational tables on a DB:
 * - Creation of tables
 * - Removal of tables
 * 
 * @author "Luigi Fortunati"
 *
 */
public interface DatabaseWrangler {
	
	/**
	 * 
	 * @return the name of the created table
	 */
	public String createTable();
	
	/**
	 * 
	 * @return 
	 */
	public void createTable(String name);
	
	/**
	 * 
	 * @param unsafe a boolean telling if the table can should be unsafe or not (tradeoff performance/reliability)
	 * @return the name of the created table
	 */
	public String createTable(boolean unsafe);
	
	public void removeTable(String tableName);
	
	public boolean exists(String tableName);
	
	/**
	 * 
	 * @param tableName
	 * @param withData
	 * @param unsafe
	 * @return the name of the created table
	 */
	public String cloneTable(String tableName, boolean withData, boolean unsafe);
	
	public void addColumn(String tableName, String columnName, DataType type);
	
	public void addColumn(String tableName, String columnName, DataType type, TDTypeValue defaultValue);
	
	public void removeColumn(String tableName, String columnName);
	
	public void alterColumnType(String tableName, String columnName, DataType newType);
	
	public void createIndex(String tableName, String columnName);
	
	public void setNullable(String tableName, String columnName, boolean nullable);
	
	public void createUniqueIndex(String tableName, String columnName);
	
	public void createTriggerOnTable(String triggerName, List<Condition> condition, HTime htime, String targetTableName, String procedureName);

}
