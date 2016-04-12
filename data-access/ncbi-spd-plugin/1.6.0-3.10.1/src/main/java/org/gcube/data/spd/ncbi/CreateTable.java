package org.gcube.data.spd.ncbi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.ncbi.connection.ConnectionPool;
import org.gcube.data.spd.ncbi.connection.ConnectionPoolException;

public class CreateTable extends Thread{

	//	ArrayList<String> tables = new ArrayList<String>();


	static GCUBELog logger = new GCUBELog(CreateTable.class);

	CreateTable() {
		//	super("Thread");
		start(); // Start the thread
	}


	public void run() {

		//		if (!SQLTableExists("taxon")){
		//		createTaxonTable();
		//	}		
		retriveCommonNames();
		//retriveBasicInfo();	

	}


//	public void retriveBasicInfo() {
//
//		ArrayList<String> terms =  null;
//		String taxonID;
//		String scientificName;
//		String taxonRank;
//
//		//		String parentNameUsageID;
//		//		String taxonomicStatus;
//		String taxonRemarks;
//
//		String taxonomicstatus;
//		//		String acceptednameusageid;
//
//		ConnectionPool pool = null;
//		Connection con = null;
//		ResultSet rs = null;
//		try {
//			terms = new ArrayList<String>();
//			pool = ConnectionPool.getConnectionPool();
//			con = pool.getConnection();
//			//			logger.trace("select b.name_txt, a.rank, a.parent_tax_id from nodes as a NATURAL JOIN names as b where b.name_class = 'synonym' and a.tax_id = " + id  );
//			Statement statement = con.createStatement();
//			rs = statement.executeQuery("select a.tax_id, b.name_txt, a.rank, b.name_class, a.parent_tax_id from nodes as a NATURAL JOIN names as b where b.name_class!='authority' and b.name_class!='genbank common name' and b.name_class!='common names' and tax_id > 1152691 order by tax_id+0 asc");
//			logger.trace("select a.tax_id, b.name_txt, a.rank, b.name_class, a.parent_tax_id from nodes as a NATURAL JOIN names as b where b.name_class!='authority' and b.name_class!='genbank common name' and b.name_class!='common names' and tax_id > 1152691 order by tax_id+0 asc");
//			PreparedStatement stmt = null;
//			while(rs.next()) {	
//
//				taxonID = rs.getString(1);
//				scientificName = rs.getString(2);
//				taxonRank = rs.getString(3);
//				taxonRemarks = rs.getString(4);
//				//				parent_tax_id = rs.getString(5);
//
//				taxonomicstatus = setTaxonomicStatus(taxonRemarks);
//				//				acceptednameusageid = getacceptedname(taxonID);
//
//				terms.add(taxonID);
//				terms.add(scientificName);
//				terms.add(taxonRank);
//				terms.add(taxonRemarks);
//				terms.add(taxonomicstatus);
//
//				List<String> scientificnameauthorship = null;
//				scientificnameauthorship = Utils.setAuthorship(taxonID, scientificName);
//
//				if (scientificnameauthorship!=null){
//					for (String author: scientificnameauthorship){
//						//						logger.trace("author " + author);
//						terms.add(author);
//						String query = ("insert into taxon (taxonID, scientificName, taxonRank, taxonRemarks, taxonomicstatus, scientificnameauthorship) values (?,?,?,?,?, '" + author + "')");		
//						if (!pool.preStatement(query, terms, stmt))
//							logger.trace("error");
//					}
//				}else{
//					String query =("insert into taxon (taxonID, scientificName, taxonRank, taxonRemarks, taxonomicstatus, scientificnameauthorship) values (?,?,?,?,?, null)");
//					if (!pool.preStatement(query, terms, stmt))
//						logger.trace("error");
//				}				
//				terms.clear();
//			}
//		} catch (SQLException ex) {
//			logger.error("Can't retriveBasicInfo", ex);
//		} catch (ConnectionPoolException e) {
//			logger.error("ConnectionPoolException", e);
//		} finally {
//			try {
//				if (rs != null) 
//					rs.close();
//				if ((pool!=null) && (con!=null)){
//					pool.releaseConnection(con);
//				}
//
//				//				logger.trace("close1");
//			} catch (SQLException ex) {
//				logger.error("sql Error", ex);
//			}
//		}
//	}



	private String getacceptednameID(String taxonID) {

		String id = null;
		ConnectionPool pool = null;
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			//			logger.trace("select b.name_txt, a.rank, a.parent_tax_id from nodes as a NATURAL JOIN names as b where b.name_class = 'synonym' and a.tax_id = " + id  );
			String query ="select id from taxon where taxonremarks = 'scientific name' and taxonid = ?";		
			ArrayList<String> terms = new ArrayList<String>();
			terms.add(taxonID);
			rs =  pool.selectPrestatement(query, terms, stmt);	

			if(rs!=null){
				while(rs.next()) {	
					id = rs.getString(1);
				}
			}
			terms.clear();
		} catch (SQLException ex) {
			logger.error("Can't get " +
					"acceptedname", ex);

		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		} finally {
			try {

				if (rs != null) 
					rs.close();
				if ((pool!=null) && (con!=null)){
					pool.releaseConnection(con);
				}
			} catch (SQLException ex) {
				logger.error("sql Error", ex);
			}
		}
		return id;
	}


	private String setTaxonomicStatus(String status) {
		//		logger.trace(status);
		String tax;
		if (status.equals("scientific name"))
			tax= "ACCEPTED";
		else if ((status.equals("synonym")) || (status.equals("genbank synonym")))
			tax= "SYNONYM";
		else
			tax= "UNKNOWN";
		return tax;
	}


	//check if the tables already exist
	//	private boolean SQLTableExists(String tableName) {
	//		boolean exists = false;
	//		ResultSet rs = null;
	//
	//		Database database = null;  
	//		try {
	//			database = new Database();  			
	//			database.connect();
	//
	//			String sqlText = "SELECT tables.table_name FROM information_schema.tables WHERE table_name = '" + tableName + "'";    
	//			rs = database.get(sqlText);
	//
	//			if (rs.next()) {
	//				exists = true;
	//			} else { 
	//				exists = false;
	//			}
	//		} catch (SQLException e) {
	//			logger.error("SQLException in SQLTableExists", e);
	//		}finally {
	//
	//			try {
	//				if (rs != null) 
	//					rs.close();
	//			} catch (SQLException e) {
	//
	//				logger.error("SQLException", e);
	//			}
	//		} 
	//		return exists;
	//
	//	}


	public void retriveCommonNames() {

		ResultSet rs = null;
		ResultSet rs1 = null;

		String taxonID;
		String commonName;
		String taxonRemarks;
		String id;
		String query;
		String queryInsert;
		PreparedStatement stmt = null;
		ConnectionPool pool = null;
		Connection con = null;

		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
//			logger.trace("select a.tax_id, b.name_txt, b.name_class from nodes as a NATURAL JOIN names as b where b.name_class='genbank common name' or b.name_class='common names'");
			query = ("select a.tax_id, b.name_txt, b.name_class from nodes as a NATURAL JOIN names as b where b.name_class='genbank common name' or b.name_class='common names'");

			Statement statement = con.createStatement();
			// Esecuzione di un comando sul DB
			rs = statement.executeQuery(query); 

			ArrayList<String> terms = new ArrayList<String>();
			while(rs.next()) {	

				taxonID = rs.getString(1);
				commonName = rs.getString(2);
				taxonRemarks = rs.getString(3);

				id = getacceptednameID(taxonID);
				
				queryInsert =  ("insert into vernacularnames (scientificnameid, commonName, taxonRemarks) values (?, ?, ?)");

				terms.add(id);
				terms.add(commonName);
				terms.add(taxonRemarks);
				pool.preStatement(queryInsert, terms, stmt);

				terms.clear();
			}
		} catch (SQLException ex) {
			logger.error("Can't popolate vernacularnames table", ex);

		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		} finally {
			try {

				if (rs != null) 
					rs.close();
				if (rs1 != null) 
					rs1.close();

				if ((pool!=null) && (con!=null)){
					pool.releaseConnection(con);
				}

			} catch (SQLException ex) {
				logger.error("sql Error", ex);
			}
		}
	}

}
