package org.gcube.datatransfer.agent.impl.db;


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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;


/**
 * 
 * @author   Andrea Manzi(CERN)
 *
 */
public abstract class DBManager {

	GCUBELog logger = new GCUBELog(DBManager.class);
	
	protected  PersistenceManagerFactory persistenceFactory;

	
	protected static File backupFolder = null;

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


	protected static int backupIntervalMS = 3600 * 1000 * Integer.valueOf((String) ServiceContext.getContext().getProperty("scheduledBackupInHours", true));

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


	/**
	 * checkpoint  the db and create a backup
	 * 
	 * @throws SQLException if the CHECKPOINT query fails
	 */
	public synchronized void backup() throws SQLException, Exception {

	
		Query query = persistenceFactory.getPersistenceManager().newQuery("javax.jdo.query.SQL", "CHECKPOINT");
		try {
			query.execute();
		} catch (Exception e) {
			throw e;
		}
		backupFolder.mkdirs();
		new Thread() {
			public void run() {
				try {
					zipFolder(new File(dbFileBaseFolder).listFiles());
				}catch (Exception e) {
					logger.error("Error creating a backup for the DB",e);
				}
			}
		}.start();
	}
	
	/**
	 * Executes SQL command SELECT and invokes the given consumer, giging back JSON formatetted output.
	 * 
	 * @param expression the SQL expression to evaluate
	 * @return Result Set
	 * @throws SQLException if the query fails
	 * @throws Exception if the given resultset consumer fails
	 */
	public synchronized String  queryJSON(String expression) throws SQLException,Exception {
		Statement statement = null;	
		try {
			
			Connection conn = (Connection)  persistenceFactory.getPersistenceManager().getDataStoreConnection().getNativeConnection();
			statement = conn.createStatement();
			return toJSon(statement.executeQuery(expression));
		} 
		catch (Exception e){
			throw e;
		} finally {
			if (statement != null) statement.close();
		}
	}



	/**
	 * 
	 * @param resultSet
	 * @return Json String
	 * @throws SQLException
	 */
	public static String toJSon(ResultSet resultSet ) throws SQLException
	{

		StringBuilder json = new StringBuilder();

		json.append("{\"data\":[");

		ResultSetMetaData metaData = resultSet.getMetaData();
		int numberOfColumns = metaData.getColumnCount();

		int row = 0;
		while(resultSet.next()){
			
			if (row>0) json.append(",{");
			else json.append('{');

			for (int column = 1; column <=numberOfColumns; column++){
				if (column>1) json.append(',');					
				json.append(quote(metaData.getColumnName(column)));
				json.append(':');
				json.append(quote(resultSet.getString(column)));
			}

			json.append('}');

			row++;
		}

		json.append("],\"total_count\":");
		json.append(row);
		json.append("}");

		return json.toString();

	}

	/**
	 * Produce a string in double quotes with backslash sequences in all the
	 * right places. A backslash will be inserted within </, allowing JSON
	 * text to be delivered in HTML. In JSON text, a string cannot contain a
	 * control character or an unescaped quote or backslash.
	 * @param string A String
	 * @return  A String correctly formatted for insertion in a JSON text.
	 */
	protected static String quote(String string) {
		if (string == null || string.length() == 0) {
			return "\"\"";
		}

		char         b;
		char         c = 0;
		int          i;
		int          len = string.length();
		StringBuffer sb = new StringBuffer(len + 4);
		String       t;

		sb.append('"');
		for (i = 0; i < len; i += 1) {
			b = c;
			c = string.charAt(i);
			switch (c) {
			case '\\':
			case '"':
				sb.append('\\');
				sb.append(c);
				break;
			case '/':
				if (b == '<') {
					sb.append('\\');
				}
				sb.append(c);
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
				break;
			default:
				if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
						(c >= '\u2000' && c < '\u2100')) {
					t = "000" + Integer.toHexString(c);
					sb.append("\\u" + t.substring(t.length() - 4));
				} else {
					sb.append(c);
				}
			}
		}
		sb.append('"');
		return sb.toString();
	}


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
	
		

}

