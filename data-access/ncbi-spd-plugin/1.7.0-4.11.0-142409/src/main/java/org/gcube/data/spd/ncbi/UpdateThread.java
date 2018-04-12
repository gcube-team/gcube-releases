package org.gcube.data.spd.ncbi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.gcube.data.spd.ncbi.connection.ConnectionPool;
import org.gcube.data.spd.ncbi.connection.ConnectionPoolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class UpdateThread extends Thread {

	//	ArrayList<String> tables = new ArrayList<String>();

	long nextup;
	static Logger logger = LoggerFactory.getLogger(UpdateThread.class);

	UpdateThread(long update) {
		//	super("Thread");
		this.nextup = update;
		start(); // Start the thread
	}

	public void run() {
		while (true){		
			logger.trace(createLabel(nextup));
			try {
				sleep(nextup);

				//				createNewDb();
				if (update()){
					//					logger.trace("switch to "+ NcbiPlugin.tmpjdbc);
					//					ConnectionPool.dbUrl = NcbiPlugin.tmpjdbc;
					//					
					//					updateDb();
					//					
					////					while (!dropTmpDb()){						
					////						Thread.sleep(40000);					
					////					}
					//					
					//					logger.trace("switch to "+ NcbiPlugin.jdbc);
					//					ConnectionPool.dbUrl = NcbiPlugin.jdbc;
					setlastupdate();
					this.nextup = 2592000000L;	
					//					dropTmpDb();

				}
			} catch (InterruptedException e) {
				logger.error("InterruptedException", e);
			} catch (IOException e) {
				logger.error("IOException", e);
			}
		}
	}





	//	private boolean updateDb() {
	//		
	//		
	//		Connection con = null;
	//		PreparedStatement stmt1 = null;
	//		PreparedStatement stmt2 = null;
	//		PreparedStatement stmt3 = null;
	//		PreparedStatement stmt4 = null;
	//		PreparedStatement stmt5 = null;
	//		logger.trace("******** ");
	//
	//		try{
	//			con = DriverManager.getConnection(NcbiPlugin.template1jdbc, NcbiPlugin.username, NcbiPlugin.password);		
	////			con.setAutoCommit(false);
	//
	//			//			stmt = con.prepareStatement("drop database ncbi_db");
	//			//			logger.trace("drop database ncbi_db");
	//			//			logger.trace(stmt.executeUpdate());
	//			
	//			stmt1 = con.prepareStatement("SELECT pg_terminate_backend(pg_stat_activity.procpid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'ncbi_db'");
	//			logger.trace(stmt1.executeQuery());
	//			
	//			stmt2 = con.prepareStatement("DROP DATABASE ncbi_db");
	//			logger.trace("DROP DATABASE ncbi_db");
	//			logger.trace(stmt2.executeUpdate());
	//						
	//			stmt3 = con.prepareStatement("CREATE DATABASE ncbi_db WITH TEMPLATE tmp_ncbi OWNER " + NcbiPlugin.username);
	//			logger.trace("CREATE DATABASE ncbi_db WITH TEMPLATE tmp_ncbi OWNER " + NcbiPlugin.username);
	//			logger.trace(stmt3.executeUpdate());
	//
	//			stmt4 = con.prepareStatement("SELECT pg_terminate_backend(pg_stat_activity.procpid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'tmp_ncbi'");
	//			logger.trace(stmt4.executeQuery());
	//			
	//			stmt5 = con.prepareStatement("DROP DATABASE tmp_ncbi");
	//			logger.trace("DROP DATABASE tmp_ncbi");
	//			logger.trace(stmt5.executeUpdate());
	//			
	//			//			stmt = con.prepareStatement("ALTER DATABASE tmp_ncbi RENAME TO ncbi_db");
	//			//			logger.trace("ALTER DATABASE tmp_ncbi RENAME TO ncbi_db");
	//			//			logger.trace(stmt.executeUpdate());			
	//
	////			stmt.clearParameters();
	////			con.commit();
	//		} catch (SQLException e) {
	//			logger.error("SQLException", e);
	//			return false;
	//		}finally{
	//			if (stmt1!=null)
	//				try {
	//					stmt1.close();
	//				} catch (SQLException e) {
	//					logger.error("SQLException", e);
	//				}
	//			if (stmt2!=null)
	//				try {
	//					stmt2.close();
	//				} catch (SQLException e) {
	//					logger.error("SQLException", e);
	//				}
	//			if (stmt3!=null)
	//				try {
	//					stmt3.close();
	//				} catch (SQLException e) {
	//					logger.error("SQLException", e);
	//				}
	//			if (con!=null)
	//				try {
	//					con.close();
	//					logger.trace("shout down connection " + NcbiPlugin.template1jdbc);
	//				} catch (SQLException e1) {
	//					logger.error("SQLException", e1);
	//				}
	//		}
	//		return true;
	//	}
	//	


	/**
	 * Set next update date
	 */
	private String createLabel(long nextup) {
		Date date = new Date();		
		String label = "NCBI DB will be updated on " + Utils.nextUpdateDate(nextup + date.getTime());
		return label;

	}

	/**
	 * Set next update date
	 */
	private void setlastupdate() {

		ConnectionPool pool = null;
		Connection con = null;
		boolean result;

		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			Date date = new Date();
			long dt = date.getTime();
			java.sql.Date today = new java.sql.Date (dt);
			String query = "insert into updates (date) values ('" + today + "')";    	

			result =  ConnectionPool.insertPreStatement(query);
			if (!result){
				logger.trace("problem setting last update date");
			}
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		}finally {
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}			
		} 
	}


	//	get information (author or rank) by Id
	protected boolean update() throws IOException{
		logger.trace("update");
		File tempFolder = null;

		try {

			URL url = new URL(NcbiPlugin.urlDump);

			//create a temp folder
			tempFolder = File.createTempFile("ncbi-folder", "" );
			tempFolder.delete();
			tempFolder.mkdir();

			//get dump from URL http://www.Ncbi.gov/downloads/NcbiMySQLTables.tar.gz

			if (downloadFromUrl(url, tempFolder + "/" + NcbiPlugin.zipFileName)) {

				//decompress
				Runtime rt1 = Runtime.getRuntime(); 
				Process runtimeProcess1 = null; 
				logger.trace("tar xzvf " + tempFolder + "/" + NcbiPlugin.zipFileName + " -C " + tempFolder);
				runtimeProcess1 = rt1.exec("tar xzvf " + tempFolder + "/" + NcbiPlugin.zipFileName + " -C " + tempFolder);			
				runtimeProcess1.waitFor();

				importData(tempFolder);

				//import data
				if (importData(tempFolder))
					//create table citation
					createTableCit();	


			}
		} catch (Exception e) {
			logger.error("Error Downloading Dump", e);
			return false;
		}
		finally{
			//delete temp folder
			if (tempFolder != null)
				clearTmp(tempFolder);
		}
		return true;
	}


	private Boolean createTableCit() {
		logger.trace("creating table citation");
		ResultSet rs = null;

		Connection con = null;  
		Statement statement = null;
		PreparedStatement stmt = null;
		try {
			con = DriverManager.getConnection(NcbiPlugin.jdbc, NcbiPlugin.username, NcbiPlugin.password);	
			//			logger.trace("open1");

			if (Utils.SQLTableExists("citation")){

				logger.trace("drop table citation cascade");

				stmt = con.prepareStatement("drop table citation cascade");
				stmt.executeUpdate();
				stmt.cancel();

			}
//			logger.trace(tables.get("citation"));	
//			database.update(tables.get("citation"));	

			statement = con.createStatement();  
			rs = statement.executeQuery("select cit_id, taxid_list from citations"); 

			while(rs.next()) {	
				String[] list;
				int cit_id = rs.getInt(1);
				String taxid_list = rs.getString(2);

				if (taxid_list != null) {
					list = taxid_list.split(" ");

					for (String  tax_id : list) {
						//logger.trace("insert into citation(tax_id, cit_id) values (" + tax_id + ", " + cit_id + ")");
						stmt = con.prepareStatement("insert into citation(tax_id, cit_id) values (" + tax_id + ", " + cit_id + ")");
						stmt.executeUpdate();
					}
				}			
			}	
		} catch (SQLException ex) {
			logger.error("Can't create citation table", ex);
			return false;
		} finally {
			try {

				if (rs != null) 
					rs.close();
				if (con!=null)
					con.close();
				//				logger.trace("close1");
			} catch (SQLException ex) {
				logger.error("sql Error", ex);
			}
		}
		return true;
	}


	//	//check if the tables already exist
	//	private boolean SQLTableExists(String tableName) {
	//
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
	//	}



	//download dump DB 
	private boolean downloadFromUrl(URL url, String localFilename) {

		Boolean flag = false;
		logger.trace("Downloading from " + url + " ...");
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URLConnection urlConn = url.openConnection();

			is = urlConn.getInputStream();
			fos = new FileOutputStream(localFilename);

			byte[] buffer = new byte[4096];
			int len;

			while ((len = is.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			logger.trace("Download completed successfully");
			flag = true;

		} catch(IOException e){
			logger.error("Error, check url", e);

		}finally {
			try {

			} finally {
				if (is != null)
					try {
						is.close();
					} catch (IOException e) {						
						logger.error("IOException Error", e);
					}
				if (fos != null)
					try {
						fos.close();
					} catch (IOException e) {
						logger.error("IOException Error", e);
					}
			}
		}
		return flag;
	}
	//delete content folder
	private void clearTmp(File f) {
		if (f.exists()) {
			for (File c : f.listFiles()) {
				if (c.isDirectory())
					clearTmp(c);
				c.delete();
			}
			f.delete();
		}
		logger.trace("Deleted files");
	}


	//metod called by CreateDBThread

	private Boolean importData(File tempFolder) throws SQLException, IOException {

		if (tempFolder.isDirectory()) {

			logger.trace("Starting import from:");

			for (String n : NcbiPlugin.names) {
				File f = new File(tempFolder + "/" + n + ".dmp");
				if (f.getName().toLowerCase().endsWith("dmp")) {												

					//get table name
					String table = null;					
					int index = f.getName().lastIndexOf('.');
					if (index>0 && index <= f.getName().length() - 2 )
						table = f.getName().substring(0, index);					

					getFileContent(tempFolder, f.getPath(), table);
					File tab = new File(tempFolder + "/" + table);

					ConnectionPool pool = null;
					try {

						pool = ConnectionPool.getConnectionPool();
						pool.copy("copy " + table + " from stdin DELIMITERS '\t' CSV", tab.getAbsolutePath());

						logger.trace(f.getName());
					} catch (Exception e) {
						logger.error("Exception", e);
						return false;
					} 
				}
			}
			logger.trace("Import completed successfully!");
		}
		return true;
	}

	//clean csv and create a new file with the same content
	private void getFileContent(File tempFolder, String path, String output) throws FileNotFoundException, IOException{ 
		logger.trace("getContent " + path +" " + output);

		//		BufferedReader br =  new BufferedReader(new FileReader(path)); 
		BufferedWriter bw = new BufferedWriter(new FileWriter(tempFolder + "/" + output));
		InputStream in = new FileInputStream(path);
		Reader r = new InputStreamReader(in);
		int intch;
		StringBuilder line = new StringBuilder();
		Integer count = 0;
		Integer tab = 0;
		Integer quot = 0;
		char before = 0;
		ResultSet rs = null;

		PreparedStatement stmt = null;
		Connection con = null;

		try {

			con = DriverManager.getConnection(NcbiPlugin.jdbc, NcbiPlugin.username, NcbiPlugin.password);					

			logger.trace("select * from " + output + " LIMIT 1");

			stmt = con.prepareStatement("select * from " + output + " LIMIT 1");
			rs = stmt.executeQuery();

			ResultSetMetaData rsmd = rs.getMetaData();
			tab = rsmd.getColumnCount();
			//						logger.trace(tab);
			//clean csv
			while ((intch = r.read()) != -1) {
				char ch = (char) intch;

				if ((ch == '|') & (before == '\t')) {			
					count++;
					if (count == tab) {		
						if ((quot != 0) & (quot%2!=0)) {
							String new_line =(line.toString()).replace("\""+"", "");
							bw.write(new_line.toString());
						}
						else
							bw.write(line.toString());
						bw.write('\n');
						line.delete(0, line.length());
						count = 0;
						quot = 0;
					}
					else
						line.append('\t');	
				}
				else if (((ch != '|') &(ch != '\n') & (ch != '\t'))) {
					line.append(ch);
					if (ch == '\"') {
						quot++;
					}
				}
				before = ch;
			}
			bw.close();
			in.close();
			r.close();
		} catch (SQLException e) {
			logger.error("sql Error", e);

		} finally {
			try {
				if (rs != null) 
					rs.close();	
				if (con != null)
					con.close();
			} catch (SQLException ex) {
				logger.error("sql Error", ex);

			}
		}
	} 

	//	public static void createNewDb() throws IllegalAccessException {
	//		BufferedReader br = null;
	//		String dbUrl = NcbiPlugin.template1jdbc;
	//		logger.trace("Db template1");
	//		try{
	//
	//			br = new BufferedReader(new InputStreamReader(NcbiPlugin.class.getResourceAsStream(NcbiPlugin.sqlfile)));
	//			String line;
	//			int count = 0;
	//			Class.forName(NcbiPlugin.dbDriver).newInstance(); 
	//			while ((line = br.readLine()) != null) {
	//				Connection con = null;
	//				PreparedStatement stmt = null;
	//				logger.trace("******** ");
	//				count++;
	//				try{
	//					if (count == 2){
	//						line = line.replace("USER", NcbiPlugin.username);		
	//					}	
	//					if (count == 3){
	//						dbUrl = NcbiPlugin.tmpjdbc;
	//						logger.trace("Switch to tmp database");						
	//					}
	//					con = DriverManager.getConnection(dbUrl, NcbiPlugin.username, NcbiPlugin.password);					
	//					stmt = con.prepareStatement(line);
	//
	//					logger.trace("count " + count + " - line " + line);
	//					logger.trace(dbUrl);
	//					logger.trace(stmt.executeUpdate());
	//
	//					stmt.clearParameters();
	//
	//				}finally{
	//					if (stmt!=null)
	//						stmt.close();
	//					if (con!=null)
	//						try {
	//							con.close();
	//							logger.trace("shout down connection " + dbUrl);
	//						} catch (SQLException e1) {
	//							logger.error("SQLException", e1);
	//						}
	//				}
	//			}
	//		} catch (IOException e) {
	//			logger.error("IOException", e);
	//		} catch (SQLException e) {
	//			logger.error("SQLException", e);
	//		} catch (InstantiationException e) {
	//			logger.error("InstantiationException", e);
	//		} catch (IllegalAccessException e) {
	//			logger.error("IllegalAccessException", e);
	//		} catch (ClassNotFoundException e) {
	//			logger.error("ClassNotFoundException", e);
	//		}
	//		finally{
	//			try {
	//				if (br!=null)
	//					br.close();
	//			} catch (IOException e) {
	//				logger.error("IOException", e);
	//			}
	//		}
	//
	//
	//	}



	//	public static boolean dropTmpDb() throws IllegalAccessException, InterruptedException {
	//
	//		Connection con = null;
	//		PreparedStatement stmt = null;
	//		logger.trace("******** ");
	//
	//		try{
	//			con = DriverManager.getConnection(NcbiPlugin.template1jdbc, NcbiPlugin.username, NcbiPlugin.password);		
	//			con.setAutoCommit(false);
	//
	//			//			stmt = con.prepareStatement("drop database ncbi_db");
	//			//			logger.trace("drop database ncbi_db");
	//			//			logger.trace(stmt.executeUpdate());
	//
	//			stmt = con.prepareStatement("SELECT pg_terminate_backend(pg_stat_activity.procpid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'ncbi_db'");
	//			logger.trace(stmt.executeQuery());
	//
	//			for (String table : NcbiPlugin.tables){
	//				stmt = con.prepareStatement("CREATE TABLE " + NcbiPlugin.template1jdbc + " AS (SELECT * FROM old_table)");
	//				logger.trace("drop database tmp_ncbi");
	//				logger.trace(stmt.executeUpdate());
	//			}
	//
	//			stmt = con.prepareStatement("drop database tmp_ncbi");
	//			logger.trace("drop database tmp_ncbi");
	//			logger.trace(stmt.executeUpdate());
	//
	//			//			stmt = con.prepareStatement("ALTER DATABASE tmp_ncbi RENAME TO ncbi_db");
	//			//			logger.trace("ALTER DATABASE tmp_ncbi RENAME TO ncbi_db");
	//			//			logger.trace(stmt.executeUpdate());			
	//
	//			stmt.clearParameters();
	//			con.commit();
	//		} catch (SQLException e) {
	//			logger.error("SQLException", e);
	//			return false;
	//
	//		}finally{
	//			if (stmt!=null)
	//				try {
	//					stmt.close();
	//				} catch (SQLException e) {
	//					logger.error("SQLException", e);
	//				}
	//			if (con!=null)
	//				try {
	//					con.close();
	//					logger.trace("shout down connection " + NcbiPlugin.template1jdbc);
	//				} catch (SQLException e1) {
	//					logger.error("SQLException", e1);
	//				}
	//		}
	//		return true;
	//	}

}

