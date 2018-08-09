package org.gcube.data.analysis.tabulardata.operation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SQLHelper {

	private static final Logger log = LoggerFactory.getLogger(SQLHelper.class);

	
	public static void executeSQLCommand(String sqlCommand, DatabaseConnectionProvider connectionProvider)
			throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = connectionProvider.getConnection();
			stmt = conn.createStatement();
			StatementContainer.set(stmt);
			log.debug("Executing SQL command: " + sqlCommand);
			stmt.execute(sqlCommand);
		} catch (SQLException e) {
			String msg = "Unable to execute SQL command: " + sqlCommand;
			log.error(msg, e);
			SQLException next=e.getNextException();
			if (next!=null) throw next;
			else throw e;
		} finally {
			DbUtils.closeQuietly(stmt);
			StatementContainer.reset();
			DbUtils.closeQuietly(conn);
		}
	}

	public static QueryProgress SQLInsertCommandWithProgress(Table table, String insertCommand, int extimatedAffectedRows, DatabaseConnectionProvider connectionProvider)
			throws SQLException {
		QueryThread thread = new QueryThread(connectionProvider, insertCommand);
		
		QueryProgress progress = new QueryProgress(connectionProvider, table, extimatedAffectedRows, thread);
		new Thread(thread).start();
		return progress;
	}
	

	public static int[] executeSQLBatchCommands(DatabaseConnectionProvider connectionProvider, String... sqlCommands)
			throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = connectionProvider.getConnection();
			stmt = conn.createStatement();
			StatementContainer.set(stmt);
			for (int i = 0; i < sqlCommands.length; i++) {
				log.debug("Adding to batch: " + sqlCommands[i]);
				stmt.addBatch(sqlCommands[i]);
			}

			return stmt.executeBatch();
		} catch (SQLException e) {
			String msg = "Unable to execute batch sql command";
			log.error(msg, e);
			SQLException next=e.getNextException();
			if (next!=null) throw next;
			else throw e;
		} finally {
			DbUtils.closeQuietly(stmt);
			StatementContainer.reset();
			DbUtils.closeQuietly(conn);
		}
	}

	/**
	 * Generate a list containing a comma separated list of column names
	 * 
	 * @param columnsToCopy
	 *            the columns to write on string
	 * @return the result comma separated list of column names
	 */
	public static String generateColumnNameSnippet(List<Column> columnsToCopy) {
		StringBuilder stringBuilder = new StringBuilder();
		for (Column column : columnsToCopy) {
			stringBuilder.append(" " + column.getName());
			if (columnsToCopy.indexOf(column) != (columnsToCopy.size() - 1))
				stringBuilder.append(", ");
		}
		return stringBuilder.toString();
	}


	/**
	 * Creates a prepared statement to iterate over a column specified values
	 * 
	 * @param toCheckColumn must be either a idColumn or a validation column
	 * @param sqlCommand
	 * @param connectionProvider
	 * @return
	 * @throws SQLException 
	 */
	public static void iteratePreparedStatementOverColumnValues(Column toCheckColumn, String sqlCommand,DatabaseConnectionProvider connectionProvider, List<Object> values) throws SQLException,Exception{		

		if(!toCheckColumn.getColumnType().equals(new IdColumnType())&&!toCheckColumn.getColumnType().equals(new ValidationColumnType())) throw new Exception("Invalid column type "+toCheckColumn.getColumnType());

		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			conn = connectionProvider.getConnection();
			stmt = conn.prepareStatement(sqlCommand);
			StatementContainer.set(stmt);
			for(Object value : values){
				if(toCheckColumn.getColumnType().equals(new IdColumnType())) stmt.setInt(1, (Integer) value);
				else stmt.setBoolean(1, (Boolean) value); //Validation

				stmt.execute();
			}
		}catch(SQLException e){
			String msg = "Unable to execute batch sql command";
			log.error(msg, e);
			SQLException next=e.getNextException();
			if (next!=null) throw next;
			else throw e;
		}finally{
			DbUtils.closeQuietly(stmt);
			StatementContainer.reset();
			DbUtils.closeQuietly(conn);
		}
	}



	public static int getSpecificCount(DatabaseConnectionProvider connectionProvider,String tableName,String countArgument, String condition) throws SQLException{
		Connection conn=null;
		Statement stmt=null;
		try{
			conn = connectionProvider.getConnection();
			stmt= conn.createStatement();
			StatementContainer.set(stmt);
			if(condition==null) condition="true";
			ResultSet rs= stmt.executeQuery("SELECT count("+countArgument+") FROM "+tableName+" WHERE "+condition);
			rs.next();
			return rs.getInt(1);
		}catch(SQLException e){
			String msg = "Unable to execute batch sql command";
			log.error(msg, e);
			SQLException next=e.getNextException();
			if (next!=null) throw next;
			else throw e;
		}finally{
			DbUtils.closeQuietly(stmt);
			StatementContainer.reset();
			DbUtils.closeQuietly(conn);
		}
	}
	
	
	public static int getCount(DatabaseConnectionProvider connectionProvider,String tableName,String condition) throws SQLException{
		return getSpecificCount(connectionProvider, tableName,"*", condition);
	}
	
	
	public static int getCountEstimation(DatabaseConnectionProvider connectionProvider,String query) throws SQLException{
		Connection conn=null;
		Statement stmt=null;
		try{
			conn = connectionProvider.getConnection();
			stmt= conn.createStatement();
			StatementContainer.set(stmt);
			ResultSet rs= stmt.executeQuery("SELECT count_estimate('"+query.replace("'", "''")+"')");
			rs.next();
			return rs.getInt(1);
		}catch(SQLException e){
			String msg = "Unable to execute batch sql command";
			log.error(msg, e);
			SQLException next=e.getNextException();
			if (next!=null) throw next;
			else throw e;
		}finally{
			DbUtils.closeQuietly(stmt);
			StatementContainer.reset();
			DbUtils.closeQuietly(conn);
		}
	}
	
	
	public static Object sampleColumn(DatabaseConnectionProvider connectionProvider,Table table, Column col)throws SQLException{
		Connection conn=null;
		Statement stmt=null;
		try{
			conn = connectionProvider.getConnection();
			stmt= conn.createStatement();
			StatementContainer.set(stmt);
			ResultSet rs= stmt.executeQuery(String.format("SELECT %1$s from %2$s ORDER BY %1$s LIMIT 1 OFFSET 0 ",col.getName(),table.getName())); 
			rs.next();
			return rs.getObject(1);
		}catch(SQLException e){
			String msg = "Unable to execute batch sql command";
			log.error(msg, e);
			SQLException next=e.getNextException();
			if (next!=null) throw next;
			else throw e;
		}finally{
			DbUtils.closeQuietly(stmt);
			StatementContainer.reset();
			DbUtils.closeQuietly(conn);
		}
	}
}
