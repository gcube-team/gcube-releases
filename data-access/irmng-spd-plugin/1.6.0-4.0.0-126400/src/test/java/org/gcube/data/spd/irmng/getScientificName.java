package org.gcube.data.spd.irmng;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.gcube.data.spd.irmng.dbconnection.ConnectionPool;
import org.gcube.data.spd.irmng.dbconnection.ConnectionPoolException;

public class getScientificName {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		final BufferedWriter out = new BufferedWriter(new FileWriter("scientificNameList.txt"));

		ConnectionPool pool = null;
		Connection con = null;
		ResultSet results = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			Statement statement = con.createStatement();
			results = statement.executeQuery("select taxonid, taxonrank from taxon");			
			if (results!=null){
				while(results.next()) {
					String name = (Utils.setScName(results.getString(1), results.getString(2)));
					out.write(name);
					out.newLine();
				}
			}
		}catch (SQLException sqlExcept) {
//			System.out.println("SQLException error");
		} catch (ConnectionPoolException e) {
//			System.out.println("ConnectionPoolException error");
		}finally{
			out.close();
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
			try {
				if (results != null) {
					results.close();
				}
			} catch (SQLException ex) {
//				System.out.println("SQLException error");
			}
		}
	}
}
