package org.gcube.data.spd.asfis.dbconnection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.gcube.data.spd.asfis.AsfisPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author "Valentina Marioli valentina.marioli@isti.cnr.it"
 *
 */
public class AsfisTable {

	private static Logger log = LoggerFactory.getLogger(AsfisTable.class);
	private ArrayList<String> fields = new ArrayList<String>();
	private File tempFolder = null;


	public Boolean createDb() throws SQLException {
		int[] dimension;		
		try {
			dimension = getDimensions();
			fillDb(dimension);
		} catch (SQLException e) {
			return false;		
		} catch (IOException e) {
			return false;		
		}	finally{			
			if (tempFolder != null)
				clearTmp(tempFolder);
		}	
		return true;	
	}


	private void fillDb(int[] dimension) throws SQLException, IOException {

		log.info("Creating table asfis... ");
		ConnectionPool pool = null;
		Connection con = null;
		Boolean firstLine = true;
		try{
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			InputStream file = AsfisPlugin.class.getResourceAsStream(AsfisPlugin.xslFile);
			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();

			String query = null;
			boolean result = false;
			PreparedStatement stmtInsert = null;

			while(rowIterator.hasNext()) {

				Row row = rowIterator.next();

				if (firstLine){
					getFields(row);
					firstLine = false;	
					query = createTable(dimension);
					//					System.out.println(query);
					result = pool.insertPreStatement(query);
					if (!result)
						log.error("problem creating table");
				}else{
					query = createInsertQuery();
					ArrayList<String> terms = getTerms(row);
					//insert line in table			

					if (!pool.preStatement(query, terms, stmtInsert))
						log.error("error ");	
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ConnectionPoolException e) {
			log.error("ConnectionPoolException", e);
		} finally {	
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}

	}




	private String createInsertQuery() {
		StringBuilder query = new StringBuilder();
		Boolean first = true;
		query.append("insert into " + AsfisPlugin.table + " (");

		for (String field: fields){
			//						log.info("entry " + field);
			if (!first)
				query.append(", ");
			else
				first = false;
			query.append(field);		
		}
		query.append(")");
		query.append(" values (");

		first = true;

		for (int i=0; i< fields.size(); i++){
			if (!first)
				query.append(", ");
			else
				first = false;
			query.append("?");			
		}
		query.append(")");

		return (query.toString());

	}


	//get fields
	private void getFields(Row row) {

		//For each row, iterate through each columns
		Iterator<Cell> cellIterator = row.cellIterator();

		while(cellIterator.hasNext()) {

			Cell cell = cellIterator.next();
			fields.add(cell.toString());
		}

	}


	//get fields
	private ArrayList<String> getTerms(Row row) {

		ArrayList<String> terms = new ArrayList<String>();
		//For each row, iterate through each columns
		Iterator<Cell> cellIterator = row.cellIterator();

		while(cellIterator.hasNext()) {

			Cell cell = cellIterator.next();

			terms.add(cell.toString());
		}
		return terms;

	}

	private String createTable(int[] dimension) {
		Boolean firstField = true;
		StringBuilder query = new StringBuilder();
		query.append("create table " + AsfisPlugin.table + " (");
		int i = 0;
		for (String field: fields){

			//length for type varchar must be at least 1
			if (dimension[i] <1)
				dimension[i] = 1;

			//				this.newFields.add(field);
			if (!firstField)
				query.append(", ");
			else
				firstField = false;

			if (field.equals("3A_CODE")){
				String newField = field.replace("3", "three");
				fields.set(i, newField);

				query.append("threeA_CODE");
			}
			else if (field.equals("Order")){
				String newField = field.replace("Order", "Order_rank");
				this.fields.set(i, newField);
				query.append("Order_rank");
			}
			else
				query.append(field);
			query.append(" varchar(" + dimension[i] + ")");

			i++;
		}
		query.append(")");

		return (query.toString());
	}

	/**
	 * Get max table field dimension
	 */
	public int[] getDimensions() throws SQLException, IOException {

		int[] maxDimension = new int[0];
		int size = 0;

		try {

			InputStream file = AsfisPlugin.class.getResourceAsStream(AsfisPlugin.xslFile);
			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();

			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();

				if (size==0){
					size = row.getLastCellNum();
					//					System.out.println("#columns: " + size);
					maxDimension = new int[size];
				}

				//For each row, iterate through each columns
				Iterator<Cell> cellIterator = row.cellIterator();

				int i=0;

				while(cellIterator.hasNext()) {

					Cell cell = cellIterator.next();

					if (cell.toString().length() > maxDimension[i]){
						maxDimension[i] = cell.toString().length();		
						//						System.out.println("maxDimension[" + i + "]" + maxDimension[i]);
					}

					i++;
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return maxDimension;
	}


	/**
	 * delete content folder
	 */
	private void clearTmp(File f) {
		if (f.exists()) {
			for (File c : f.listFiles()) {
				if (c.isDirectory())
					clearTmp(c);
				c.delete();
			}
			f.delete();
		}
		//		logger.trace("Deleted files");
	}


	/**
	 * get a result set by a scientific name
	 */
	public ResultSet getAllRecords() {
		ConnectionPool pool = null;
		Connection con = null;
		ResultSet results = null;

		try {
			pool = ConnectionPool.getConnectionPool();			
			con = pool.getConnection();
			String query = "select * from figis";	
			results =  pool.selectPrestatement(query, null);
		}
		catch (Throwable e) {
			log.error("general Error", e);
		}finally{
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}			
		}
		return results;
	}




	/**
	 * get a result set by a scientific name
	 */
	public AsfisRecord getRecordByID(String id) {
		log.info("getRecordByID " + id);
		ConnectionPool pool = null;
		Connection con = null;
		ResultSet result = null;
		AsfisRecord record = null;

		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			String three_alpha_code = "%" + id + "%";
			String query = "select * from " + AsfisPlugin.table + " where UPPER(three_alpha_code) like UPPER(?)";	

			result =  pool.selectPrestatement(query, three_alpha_code);

			if (result.next())
				record = new AsfisRecord(result);

		}
		catch (Throwable e) {
			log.error("general Error", e);
		}finally{
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}	
			try {
				if (result != null) {
					result.close();
				}
			} catch (SQLException ex) {
				log.error("sql Error", ex);
			}
		}
		return record;
	}


	public boolean createTabUpdates() throws SQLException {
		// create table updates (id serial NOT NULL PRIMARY KEY, date date);
		log.info("Creating table figis... ");
		ConnectionPool pool = null;
		Connection con = null;

		try{
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			boolean result;

			String query = "create table updates (id serial NOT NULL PRIMARY KEY, date date)";
			result = pool.insertPreStatement(query);
			if (!result)
				log.error("problem creating table");

		} catch (ConnectionPoolException e) {
			log.error("ConnectionPoolException", e);
		} finally {	
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}
		return true;

	}


	//add orders
	public void addOrders() {
		// select distinct(order_rank) from asfis;
//		System.out.println("select distinct(order_rank) from asfis ");
		ConnectionPool pool = null;
		ResultSet result = null;

		try {
			pool = ConnectionPool.getConnectionPool();

			String query = "select distinct(order_rank) from " + AsfisPlugin.table + " where order_rank is not null";	

			result =  pool.selectPrestatement(query, null);

			if (result!=null){

				while (result.next()){
					String name = result.getString(1);

					String queryInsert = "insert into " + AsfisPlugin.table + " (Scientific_name, parent_id, rank) values ('" + name + "' , '" + 12422 + "' , 'Order')";

					//insert line in table			

					if (!pool.insertPreStatement(queryInsert)){
						log.error("error ");
//						System.out.println("error");
					}

				}
			}
		}
		catch (Throwable e) {
			log.error("general Error", e);
		}finally{

			try {
				if (result != null) {
					result.close();
				}
			} catch (SQLException ex) {
				log.error("sql Error", ex);
			}
		}


	}


	public void addFamilies() {

		ConnectionPool pool = null;
		ResultSet result = null;

		try {
			pool = ConnectionPool.getConnectionPool();

			String query = "select family, order_rank from asfis where family!='' group by family, order_rank order by family";	

			result =  pool.selectPrestatement(query, null);

			if (result!=null){
				while (result.next()){
					String family = result.getString(1);
					String order = result.getString(2);
					//					System.out.println(order);
					int idOrder = getRankID(order, "Order");
					//					System.out.println(idOrder);
					String queryInsert = "insert into " + AsfisPlugin.table + " (Scientific_name, parent_id, rank) values ('" + family + "' , '" + idOrder + "' , 'Family')";
					//						System.out.println(queryInsert);
					//insert line in table			

					if (!pool.insertPreStatement(queryInsert)){
						log.error("error ");
						
					}


				}
			}

		}
		catch (Throwable e) {
			log.error("general Error", e);
		}finally{

			try {
				if (result != null) {
					result.close();
				}
			} catch (SQLException ex) {
				log.error("sql Error", ex);
			}
		}
	}


	//update parent to species
	public void updateParentSpecies() {

		ConnectionPool pool = null;
		ResultSet result = null;

		try {
			pool = ConnectionPool.getConnectionPool();

			String query = "select id, family, order_rank from asfis where rank='Species'";	

			result =  pool.selectPrestatement(query, null);

			if (result!=null){

				while (result.next()){
		
					String id = result.getString(1);
					if (id!=""){
						String family = result.getString(2);
						String order = result.getString(3);

						int parentID = 0;
						if (!family.equals(""))
							parentID = getRankID(family, "Family");
						else
							parentID = getRankID(order, "Order");

						//						UPDATE asfis set parent_id=12425 where rank='Kingdom'
						String queryInsert = "UPDATE " + AsfisPlugin.table + " set parent_id = " + parentID + " where id =" + id + "";
						//						System.out.println(queryInsert);
						//insert line in table			

						if (!pool.insertPreStatement(queryInsert)){
							log.error("error ");
						}
					}

				}
			}

		}
		catch (Throwable e) {
			log.error("general Error", e);
		}finally{

			try {
				if (result != null) {
					result.close();
				}
			} catch (SQLException ex) {
				log.error("sql Error", ex);
			}
		}
	}



	//get order id by name
	private int getRankID(String order, String rank) {
		ConnectionPool pool = null;
		ResultSet result = null;
		int id = 0;

		try {
			pool = ConnectionPool.getConnectionPool();

			String query = "select id from " + AsfisPlugin.table + " where Scientific_name = '" + order + "' and rank='" + rank + "'";	
			//			System.out.println(query);
			result =  pool.selectPrestatement(query, null);

			if (result.next()){
				id = result.getInt(1);
				//				System.out.println("ID " + id);
			}
		}
		catch (Throwable e) {
			log.error("general Error", e);
		}finally{

			try {
				if (result != null) {
					result.close();
				}
			} catch (SQLException ex) {
				log.error("sql Error", ex);
			}

		}
		return id;

	}



}
