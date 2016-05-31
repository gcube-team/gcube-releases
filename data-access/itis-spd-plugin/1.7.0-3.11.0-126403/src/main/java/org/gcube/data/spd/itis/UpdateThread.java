package org.gcube.data.spd.itis;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.itis.dbconnection.ConnectionPool;
import org.gcube.data.spd.itis.dbconnection.ConnectionPoolException;



/**
 * Thread to update db
 */
class UpdateThread extends Thread {
	static GCUBELog logger = new GCUBELog(UpdateThread.class);
	private long nextup;

	UpdateThread(long update) {
		this.nextup = update;
		//	super("Thread");
		start(); // Start the thread

	}

	public void run() {
		Date date = new Date();		
		logger.trace("ITIS DB will be updated on " + nextUpdateDate( nextup + date.getTime() ));
		try {
			while (true){
				sleep(nextup);
				update();
				this.nextup = 2592000000L;		
				createTableUpdates();
				setlastupdate();
			}
		} catch (IOException e) {
			logger.error("IOException", e);
		} catch (InterruptedException e) {
			logger.error("InterruptedException", e);
		}
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

			result =  pool.insertPreStatement(query);
			if (!result){
				logger.trace("problem setting last update date");

			}
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		} catch (SQLException e) {
			logger.error("SQLException", e);
		}finally {
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}			
		} 


	}




	/**
	 * Create table updates
	 */
	public static void createTableUpdates() {
		ConnectionPool pool = null;
		Connection con = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			logger.trace("Create table updates...");

			String query = "create table updates (id serial NOT NULL PRIMARY KEY, date date)";	
			boolean result = pool.insertPreStatement(query);
			if (!result) 
				logger.trace(query);

		} catch (SQLException e) {
			logger.error("SQLException", e);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		}finally {
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		} 
	}





	private String nextUpdateDate(long input){  
		Date date = new Date(input);  
		Calendar cal = new GregorianCalendar();  
		cal.setTime(date);  
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return(dateFormat.format(date));  

	}


	/**
	 * Update information
	 */
	public void update() throws IOException{
		File tempFolder = null;
		try {
			//			String folder = "itisMySQL042712/";

			URL url = new URL(ItisPlugin.urlDump);
			//get dump from URL http://www.itis.gov/downloads/itisMySQLTables.tar.gz
			tempFolder = File.createTempFile("itis-folder", "" );

			tempFolder.delete();
			tempFolder.mkdir();
			String folder = null;
			if (downloadFromUrl(url, tempFolder + "/" + ItisPlugin.zipFileName)) {
				Runtime rt1 = Runtime.getRuntime(); 
				//				//Untar file itisMySQLTables.tar.gz
				Process runtimeProcess1 = null; 
				logger.trace("tar xzvf " + tempFolder + "/" + ItisPlugin.zipFileName + " -C " + tempFolder);
				runtimeProcess1 = rt1.exec("tar xzvf " + tempFolder + "/" + ItisPlugin.zipFileName + " -C " + tempFolder);			
				runtimeProcess1.waitFor();
				File f = new File(tempFolder + "/" + ItisPlugin.zipFileName);
				if (f.exists()) 
					f.delete();

				//				logger.trace("ls " + tempFolder);  
				Process p = Runtime.getRuntime().exec("ls " + tempFolder);  
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));  
				String line = null;  

				if ((line = in.readLine()) != null)  
					folder = line + "/";

				if (folder!=null){

					//Replace local path in dropcreateloaditis.sql
					String fileContent = getFileContent( tempFolder + "/" + folder + ItisPlugin.fileDump).replace("LOAD DATA LOCAL INFILE '", "LOAD DATA LOCAL INFILE '" + tempFolder + "/"+ folder);
					//Write in file dropcreateloaditis.sql
					setFileContent(tempFolder + "/" + folder + ItisPlugin.fileDump, fileContent);

					Runtime rt = Runtime.getRuntime(); 
					logger.trace("mysql -h " + ItisPlugin.hostName + " -u " + ItisPlugin.user + " --password=" + ItisPlugin.password + " --enable-local-infile < " + tempFolder + "/" + folder + ItisPlugin.fileDump);
					String[] executeCmd = new String[]{"/bin/sh", "-c", "mysql -h " + ItisPlugin.hostName + " -u " + ItisPlugin.user + " --password=" + ItisPlugin.password + " --enable-local-infile < " + tempFolder + "/" +folder + ItisPlugin.fileDump }; 

					Process runtimeProcess = null;  

					try {  
						runtimeProcess = rt.exec(executeCmd);
						runtimeProcess.waitFor();
						//				logger.trace(loadStream(runtimeProcess.getInputStream())); 

						int processComplete = runtimeProcess.waitFor();
						if (processComplete == 0) {  
							logger.trace("Database created successfully");  
						} else {  
							logger.trace("Could not create the database");  
						}  


					} catch (Exception ex) {  
						logger.error(loadStream(runtimeProcess.getErrorStream()), ex); 
						//				ex.printStackTrace();  
					} 
				}

			}
			else
				logger.warn("Download failed");

		} catch (Exception e) {
			logger.error("General Error", e);
		}finally{
			//delete tmp content
			emptyFolder(tempFolder);
		}

	}

	/**
	 * Show terminal
	 */
	private String loadStream(InputStream in) throws IOException 
	{ 
		int ptr = 0; 
		in = new BufferedInputStream(in); 
		StringBuffer buffer = new StringBuffer(); 
		while( (ptr = in.read()) != -1 ) { 
			buffer.append((char)ptr); 
		} 
		return buffer.toString(); 
	} 



	/**
	 * Delete tmp folder
	 */
	public void emptyFolder(File f) {
		try{
			if (f.exists()) {
				for (File c : f.listFiles()) {

					if (c.isDirectory())
						emptyFolder(c);

					c.delete();
				}
				f.delete();
			}
		}catch (Exception e) {
			logger.error("Error deleting folder " + f, e);
		}
	}

	/**
	 * Write file
	 */
	public void setFileContent(String string, String content) throws FileNotFoundException, IOException{ 
		FileOutputStream fos;       
		fos = new FileOutputStream(string); 
		fos.write(content.getBytes()); 
		fos.close();  
	} 



	/**
	 * Get file content
	 */
	public String getFileContent(String path) throws FileNotFoundException, IOException{ 
		String content = ""; 
		BufferedReader br =  
				new BufferedReader(new FileReader(path)); 

		while (br.ready()) 
			content += "\n" + br.readLine(); 

		br.close(); 
		return content; 
	} 


	/**
	 * Download dump DB from URL http://www.itis.gov/downloads/itisMySQLTables.tar.gz
	 */
	public boolean downloadFromUrl(URL url, String localFilename) throws IOException {

		boolean flag = false;
		logger.trace("Downloading " + localFilename);
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
			flag = true;

		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} finally {
				if (fos != null) {
					fos.close();
				}
			}
		}
		return flag;
	}

}
