package org.gcube.application.aquamaps.aquamapsspeciesview.servlet.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;
import org.apache.derby.tools.ij;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionProvider {
	protected static final String DB_DIR = "AquaMapsSpeciesViewServlet_DB";
	protected static final String DB_DRIVER_CLASS = "org.apache.derby.jdbc.EmbeddedDriver";

	protected static String dbDirPath;

	private static final ConcurrentHashMap<String,CountDownLatch> initializedScopes=new ConcurrentHashMap<String, CountDownLatch>();

	private static final Logger log = LoggerFactory.getLogger(ConnectionProvider.class);


	public static Set<String> initializedScopes(){
		return initializedScopes.keySet();
	}
	

	public static Connection connect(String scope) throws Exception{
		try{
			if(initializedScopes.putIfAbsent(scope, new CountDownLatch(1))==null){
				log.debug("First Access to "+scope+" db, initialiizing...");
				
				
				log.debug("Nobody else is constructing DB, preceding...");
				String tmpDirName = System.getProperty("java.io.tmpdir");

				File tmpDir = new File(tmpDirName);

				dbDirPath=getDBPath(scope);
				File dbDir = new File(tmpDir, dbDirPath);

				
				initializeDB(dbDir);		
				
				initializedScopes.get(scope).countDown();
			}
//			}else if(initializedScopes.get(scope).hasQueuedThreads()){				
//				log.debug("DB under scope "+scope+" seems to be still under construction, waiting for process to complete ... ");
//				// Subsequent attempts before init completion stop here
//				initializedScopes.get(scope).acquire();
//			}
		}catch(InterruptedException e){
			log.debug("Woken up by end of DB Construction, going to connect..");
		}catch(Exception e){
			log.error("UNABLE TO INIT DB UNDER SCOPE "+scope,e);
			initializedScopes.remove(scope);			
		}
		return connectDB(scope);
	}





	public static void dropDataBase(String scope){
		log.debug("Removing db instance for scope "+scope);
		initializedScopes.remove(scope);
		String tmpDirName = System.getProperty("java.io.tmpdir");

		File tmpDir = new File(tmpDirName);
		String dbDirPath=getDBPath(scope);
		File dbDir = new File(tmpDir, dbDirPath);
		if(dbDir.exists()){
			try{
				Class.forName(DB_DRIVER_CLASS).newInstance();

				String connectionUrl = "jdbc:derby:"+dbDir.getAbsolutePath()+";shutdown=true";
				log.debug("Connection url: "+connectionUrl);

				DriverManager.getConnection(connectionUrl);
			}catch(Exception e){
				//				log.warn("Unable to shutdown DB "+dbDir.getAbsolutePath(),e);
				//Shutdown raises an exception
			}
			try {
				log.debug("Deleting dbDir: "+dbDir.getAbsolutePath());
				FileUtils.deleteDirectory(dbDir);
			} catch (IOException e) {
				log.warn("Unable to delete folder "+dbDir.getAbsolutePath(), e);
			}
		}

	}


	private static String getDBPath(String scope){
		return DB_DIR+scope.replaceAll("/", "-");
	}


	private static void initializeDB(File dbDir) throws Exception
	{
		try{
			log.debug("Initializing DB Dir: "+dbDir.getAbsolutePath());
			dbDirPath = dbDir.getAbsolutePath();

			if (dbDir.exists()){
				log.debug("Init Process, Deleting existing dir "+dbDir.getAbsolutePath());
				try {
					FileUtils.deleteDirectory(dbDir);
				} catch (IOException e) {
					log.error("Error removing the dbDir: "+dbDir.getAbsolutePath(), e);
					throw new Exception("Error initializing the db",e);
				}
			}

			try {
				Class.forName(DB_DRIVER_CLASS).newInstance();
			} catch (ClassNotFoundException e) {
				throw new Exception("Error loading jdbc driver class: "+DB_DRIVER_CLASS,e);
			}

			String connectionUrl = "jdbc:derby:"+dbDir.getAbsolutePath()+";create=true";
			log.debug("Connection url: "+connectionUrl);

			Connection connection;
			try {
				connection = DriverManager.getConnection(connectionUrl);
			} catch (SQLException e) {
				throw new Exception("Error connecting to the db with url: ",e);
			}


			try {
				executeBatch(connection, "sql/createTables.sql");
			} catch (Exception e) {
				log.error("Error creating the schemas", e);
				throw new Exception("Error initializing the db",e);
			}
		}catch(Exception e){
			if (dbDir.exists()){
				log.debug("Something went wrong, Deleting existing dir "+dbDir.getAbsolutePath());
				try {
					FileUtils.deleteDirectory(dbDir);
				} catch (IOException e1) {
					log.error("Error removing the dbDir: "+dbDir.getAbsolutePath(), e1);
					throw new Exception("Error initializing the db",e1);
				}
			}
			throw e;
		}
	}

	private static void executeBatch(Connection connection, String batchFile) throws Exception
	{
		log.debug("executeBatch "+batchFile);

		InputStream batchStream = DBManager.class.getResourceAsStream(batchFile);

		File tmpOut;
		FileOutputStream fos;
		try {
			tmpOut = File.createTempFile("batchExecution", "log");
			fos = new FileOutputStream(tmpOut);
		} catch (IOException e) {
			log.error("Error creating the tmp out file", e);
			throw e;
		}

		int exceptions;
		try {
			exceptions = ij.runScript(connection, batchStream, "UTF-8",fos, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("Error executing the batch sql", e);
			throw e;
		}

		if (log.isTraceEnabled() || exceptions>0){
			//			log.debug("Batch log:");

			try {
				LineNumberReader lnr = new LineNumberReader(new FileReader(tmpOut));
				String line;
				while((line = lnr.readLine())!=null){
					if (exceptions>0) log.error(line);
					//					else log.debug(line);
				}
				lnr.close();
			} catch (FileNotFoundException e) {
				log.error("Error reading the output batch file", e);
			} catch (IOException e) {
				log.error("Error reading the output batch file", e);
			}
		}

		tmpOut.delete();

		if (exceptions>0){
			throw new Exception(exceptions+" exceptions during script batch execution.");
		}

	}

	private static Connection connectDB(String scope) throws Exception
	{
		try{
			initializedScopes.get(scope).await();
		}catch(InterruptedException e){}
		String tmpDirName = System.getProperty("java.io.tmpdir");

		File tmpDir = new File(tmpDirName);

		dbDirPath=getDBPath(scope);
		File dbDir = new File(tmpDir, dbDirPath);
		dbDir.mkdirs();

		log.debug("Connecting dbDir: "+dbDir.getAbsolutePath());
		dbDirPath = dbDir.getAbsolutePath();

		try {
			Class.forName(DB_DRIVER_CLASS).newInstance();
		} catch (ClassNotFoundException e) {
			log.error("Error loading jdbc driver class: "+DB_DRIVER_CLASS, e);
			throw new Exception("Error initializing the db",e);
		}

		String connectionUrl = "jdbc:derby:"+dbDir.getAbsolutePath();
		log.debug("Connection url: "+connectionUrl);

		Connection connection;
		try {
			connection = DriverManager.getConnection(connectionUrl);
		} catch (SQLException e) {
			log.error("Error connecting to the db with url: "+connectionUrl, e);
			throw new Exception("Error initializing the db",e);
		}

		return connection;

	}
}
