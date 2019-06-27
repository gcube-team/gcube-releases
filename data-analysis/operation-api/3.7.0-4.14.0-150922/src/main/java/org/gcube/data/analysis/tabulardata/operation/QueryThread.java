package org.gcube.data.analysis.tabulardata.operation;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.DbUtils;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryThread implements Runnable {

	private static Logger log = LoggerFactory.getLogger(QueryThread.class);
	
	public enum STATE {
		PENDING, STARTED, FINISHED, STOPPED
	}
		
	private STATE state = STATE.PENDING;
	
	private SQLException error = null;
	
	private int totalUpdated = -1;
	
	private DatabaseConnectionProvider connectionProvider;
	private String query;
	
	
	
	protected QueryThread(DatabaseConnectionProvider connectionProvider,
			String query) {
		super();
		this.connectionProvider = connectionProvider;
		this.query = query;
	}


	public void run() {
		this.state = STATE.STARTED;		
		Connection conn= null;
		Statement stmt = null;
		try {
			conn = connectionProvider.getConnection();
			stmt = conn.createStatement();
			StatementContainer.set(stmt);
			totalUpdated = stmt.executeUpdate(query);
			this.state = STATE.FINISHED;
		} catch (SQLException e) {
			this.state = STATE.STOPPED;
			String msg = "Unable to execute SQL command: " + query;
			log.error(msg, e);
			this.error = e;
		} finally {
			DbUtils.closeQuietly(stmt);
			StatementContainer.reset();
			DbUtils.closeQuietly(conn);
		}
		
	}
		
	
	public int getTotalUpdated() {
		return totalUpdated;
	}


	public STATE getState() {
		return state;
	}

	public SQLException getError() {
		return error;
	}
	
	
	

}
