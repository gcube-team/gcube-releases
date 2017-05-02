package org.gcube.data.spd.irmng;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.gcube.data.spd.irmng.dbconnection.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddColumnName {

	static Logger logger = LoggerFactory.getLogger(AddColumnName.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	
		try{
			getIdAndRank();
				
		}catch (Exception e) {
			logger.error("exception: " + e, e);
		}
		

	}

	
	private static void getIdAndRank() {

		ConnectionPool pool = null;
		Connection con = null;
		ResultSet rs = null;

		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			String query = "select taxonid, taxonrank from taxon where name is null";	
//			rs =  pool.selectPrestatement(query, null);

			PreparedStatement stmt = con.prepareStatement(query);	
			PreparedStatement stmt1 = null;
			rs = stmt.executeQuery();
			if (rs!=null){
				while(rs.next()) {	

					String id = rs.getString(1);
					String rank = rs.getString(2);
					String name = Utils.setScName(id, rank);
					String query1 = ("update taxon set name = ? where taxonid ='" + id + "'");
					
					
					ArrayList<String> terms = new ArrayList<String>();
					terms.add(name);
					if (!pool.preStatement(query1, terms, stmt1)){
						logger.error("Error  during update: " + query1);
					}
					
//					try{					
//					stmt1 = con.prepareStatement(query1);
////					logger.trace(query1);
//					stmt1.executeUpdate();	
//					}catch (Exception e) {
//						logger.error("Error  during update: " + query1 , e);
//					}
				}
			}
			
		}
		catch (Throwable e) {
			logger.error("general Error", e);
		}finally{
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}	
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
