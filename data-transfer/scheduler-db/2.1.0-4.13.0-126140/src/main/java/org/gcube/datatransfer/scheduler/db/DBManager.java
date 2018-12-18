package org.gcube.datatransfer.scheduler.db;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.gcube.common.core.utils.logging.GCUBELog;
//import org.gcube.datatransfer.scheduler.impl.porttype.ServiceContext;


public abstract class DBManager {

	GCUBELog logger = new GCUBELog(DBManager.class);

	protected  PersistenceManagerFactory persistenceFactory;


	protected static File backupFolder = null;
	protected static Properties prop=null;
	/**
	 * dbFileName is used to open or create files that hold the state
	 * of the db. It can contain directory names relative to the current 
	 * working directory
	 * 
	 */

	protected String dbFileBaseFolder;

	protected String dbFileName;

	protected String dbName;



	/**
	 * Constructor for SQLDBManager
	 *
	 */
	public DBManager() {}


	protected static int backupIntervalMS;

	public static interface BaseConsumer {
		/**
		 * @param resultset the ResultSet to consume
		 * @throws Exception if the Consuming of the ResultSet fails
		 */
		void consume(ResultSet resultset) throws Exception;
	}


	/**
	 * Shutdown the db and close the connection to the db.
	 * @throws Exception 
	 */
	public synchronized void close() throws Exception {

		try {
			persistenceFactory.getPersistenceManager().getDataStoreConnection().close();
		} 
		catch (Exception e) {
			throw e;
		}
	}
	public synchronized boolean startBackUp(){
		String dbName=null;
		String dbUserName=null;
		String dbPassword=null;
		String path=null;
		int numberOfBackups=0;
		try{
			dbName=this.getDBName();
			dbUserName=this.getUsername();
			dbPassword = this.getPass();
			path = backupFolder.getAbsolutePath();
			numberOfBackups=Integer.valueOf(this.getNumberOfBackups());
		}
		catch(Exception e ){
			e.printStackTrace();
			logger.debug("startBackUp - Exception right before back up procedure...");
			return false;
		}
		if(dbName==null || dbUserName==null || dbPassword==null || path==null || numberOfBackups==0){
			logger.debug("startBackUp - One of these parameters are null/0 : \n" +
					"dbName/dbUserNamedb/Password/path/numberOfBackups\ncheck db.properties file ... ");
			return false;
		}

		return backupDB(dbName,dbUserName,	dbPassword,path,numberOfBackups	);
	}

	private synchronized boolean backupDB(String dbName, String dbUserName, String dbPassword, String path,int numberOfBackups) {
		if(!path.endsWith("/"))path=path+"/";

		String backupFilePath = getBackupFilePath(path, dbName, numberOfBackups);

		String executeCmd = "mysql -u " + dbUserName + " -p " + dbPassword + " " + dbName + " > " + backupFilePath;
		//ProcessBuilder builder = new ProcessBuilder("/bin/sh","-c",executeCmd);
		String[] executeCmdArray={"/bin/sh","-c",executeCmd};

		//logger.debug("command: '"+executeCmd+"'");
		Process runtimeProcess;
		try {
			runtimeProcess = Runtime.getRuntime().exec(executeCmdArray);
			runtimeProcess.waitFor();  
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug("Could not create the backup");
			return false;
		}
		logger.debug("Backup created successfully");
		return true;
	}

	private synchronized String getBackupFilePath(String path, String dbName , int maxBackup){
		File folder = new File (path);
		//in case of first backup
		if(!folder.exists()){
			folder.mkdirs();
			return path + dbName+".sql";
		}

		int num=0;
		while(num<maxBackup){
			String backupFile=null;
			if(num==0)backupFile = dbName+".sql";
			else backupFile = dbName+"_"+num+".sql";

			String backupFilePath=path+backupFile;
			File tmpFile = new File(backupFilePath);
			if(!tmpFile.exists())return backupFilePath;
			else ++num;			
		}
		//if we have exceeded the number of backups we replace the older one..

		num=0;
		Date present = new Date();
		long oldModifiedFile = present.getTime();
		String olderBackupFilePath="";
		while(num<maxBackup){
			String backupFile=null;
			if(num==0)backupFile = dbName+".sql";
			else backupFile = dbName+"_"+num+".sql";

			String backupFilePath=path+backupFile;
			File tmpFile = new File(backupFilePath);

			long lastMofidied = tmpFile.lastModified();
			if(num==0){ //initial value
				oldModifiedFile=lastMofidied;
				olderBackupFilePath=backupFilePath;
			}//we replace the initial value with the older one
			else if(lastMofidied < oldModifiedFile){
				oldModifiedFile=lastMofidied;
				olderBackupFilePath=backupFilePath;
			}
			++num;			
		}
		Date oldBackupDate = new Date(oldModifiedFile);
		//	logger.debug("getBackupFilePath - We're going to replace an old backupFile - lastModifiedDate:"+oldBackupDate);
		return olderBackupFilePath;
	}

	//NOT USED...
	protected void zipFolder (File [] files) throws Exception {
		// Create a buffer for reading the files
		byte[] buf = new byte[1024];


		// Create the ZIP file
		DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmssZ");
		String outFilename = backupFolder+File.separator+dateFormat.format(new Date().getTime())+".zip";
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));

		// Compress the files
		for (int i=0; i<files.length; i++) {
			FileInputStream in = new FileInputStream(files[i]);

			// Add ZIP entry to output stream.
			out.putNextEntry(new ZipEntry(files[i].getAbsolutePath()));

			// Transfer bytes from the file to the ZIP file
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			// Complete the entry
			out.closeEntry();
			in.close();
		}

		// Complete the ZIP file
		out.close();
		logger.debug("DB Backup created @ "+outFilename);
	}

	private synchronized String getDBName(){
		if(prop==null){
			logger.debug("prop is null");
			return null;
		}

		String value=null;
		value = prop.getProperty("datanucleus.ConnectionURL");
		if(value==null)return null;

		String[] parts=value.split("/");
		return parts[parts.length-1];
	}
	private synchronized String getUsername(){
		if(prop==null){
			logger.debug("prop is null");
			return null;
		}

		String value=null;
		value = prop.getProperty("javax.jdo.option.ConnectionUserName");

		return value;
	}
	private synchronized String getPass(){
		if(prop==null){
			logger.debug("prop is null");
			return null;
		}

		String value=null;
		value = prop.getProperty("javax.jdo.option.ConnectionPassword");

		return value;
	}

	private synchronized String getNumberOfBackups(){
		if(prop==null){
			logger.debug("prop is null");
			return null;
		}

		String value=null;
		value = prop.getProperty("datanucleus.numberOfBackUps");

		return value;
	}
	public synchronized String getScheduledBackupInHours(){
		if(prop==null){
			logger.debug("prop is null");
			return null;
		}

		String value=null;
		value = prop.getProperty("datanucleus.scheduledBackupInHours");

		return value;
	}
}

