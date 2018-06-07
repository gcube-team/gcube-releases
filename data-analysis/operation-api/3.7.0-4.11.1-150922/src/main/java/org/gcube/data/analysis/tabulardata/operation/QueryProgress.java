package org.gcube.data.analysis.tabulardata.operation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.DbUtils;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.QueryThread.STATE;

public class QueryProgress {

	
	//private static Logger logger = LoggerFactory.getLogger(QueryProgress.class);
	
	private DatabaseConnectionProvider connectionProvider;

	private Table table;
	
	private int countExtimation;
	
	private QueryThread thread;
	
	protected QueryProgress(DatabaseConnectionProvider conn, Table table, int countExtimation, QueryThread thread) {
		super();
		this.table = table;
		this.countExtimation = countExtimation;
		this.thread = thread;
		this.connectionProvider = conn;
	}
		
	
	public float getProgress() throws SQLException, Exception{
		if (thread.getState()== STATE.FINISHED) return 1f;
		if (thread.getState()== STATE.STOPPED) throw thread.getError();
				
		Statement stmt = null;
		Connection conn = null;
		try{
			conn = connectionProvider.getConnection();
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format("select last_value-start_value from %s_id_seq ",table.getName()));
			rs.next();
			int seqValue = rs.getInt(1);
			float progress = (float)seqValue/(float)countExtimation;
			return progress>=1 ? 0.99f :progress;
		}catch(Throwable exception){
			throw new Exception("progress can't be calculated", exception);
		} finally {
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
	}
	
	public int getTotalUpdated(){
		return this.thread.getTotalUpdated();
	}
	
}
