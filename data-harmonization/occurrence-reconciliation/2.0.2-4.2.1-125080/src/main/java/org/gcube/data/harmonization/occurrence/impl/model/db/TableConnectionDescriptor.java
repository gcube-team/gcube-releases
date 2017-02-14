package org.gcube.data.harmonization.occurrence.impl.model.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.gcube.data.harmonization.occurrence.impl.model.PagedRequestSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableConnectionDescriptor {

	private static final Logger logger = LoggerFactory.getLogger(TableConnectionDescriptor.class);
	
	private String connectionUrl;	
	private long count;
	private Connection conn;
	private List<String> columns=new ArrayList<String>();
	private String tableName;
	
	public TableConnectionDescriptor(String connectionUrl,String tableName) throws SQLException  {
		logger.trace("Instatiating, connection url : "+connectionUrl+", tableName : "+tableName);
		this.tableName=tableName;
		this.connectionUrl=connectionUrl;
		conn=DriverManager.getConnection(connectionUrl);
		Statement stmt=null;
		ResultSet rs=null;
		try{
			logger.debug("Acquiring metadata for table "+tableName+"...");
			stmt=conn.createStatement();
			rs=stmt.executeQuery("SELECT * FROM "+tableName+" LIMIT 1 OFFSET 0");
			ResultSetMetaData meta =rs.getMetaData();
			for(int i=1;i<=meta.getColumnCount();i++){
				columns.add(meta.getColumnName(i));
			}
			rs.close();
			logger.debug("Acquiring total count for table "+tableName);
			rs=stmt.executeQuery("SELECT COUNT (*) FROM "+tableName);
			rs.next();
			count=rs.getLong(1);
		}catch(SQLException e){
			throw e;
		}finally{
			if(rs!=null) rs.close();
			if(stmt!=null) stmt.close();
		}
	}
	
	
	public String getJSON(PagedRequestSettings settings)throws SQLException{
		logger.debug("Getting JSON view "+settings+" on table "+tableName);
		Statement stmt=null;
		ResultSet rs=null;
		try{
			stmt=conn.createStatement();
			rs=stmt.executeQuery("SELECT * FROM "+tableName+" ORDER BY "+settings.getOrderByField()+" "+settings.getOrder()+" LIMIT "+settings.getPageSize()+" OFFSET "+settings.getOffset());
			return Utils.toJSon(rs, count);
		}catch(SQLException e){
			throw e;
		}finally{
			if(rs!=null) rs.close();
			if(stmt!=null) stmt.close();
		}
	}
	
	public List<String> getColumns() {
		return columns;
	}
	
		
	public void close() throws SQLException{
		conn.close();
	}
	
	public Connection getConn() {
		return conn;
	}
}
