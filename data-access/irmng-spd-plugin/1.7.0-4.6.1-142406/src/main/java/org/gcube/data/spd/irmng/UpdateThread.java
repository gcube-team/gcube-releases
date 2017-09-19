package org.gcube.data.spd.irmng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.gbif.dwc.terms.DcTerm;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.Archive;
import org.gbif.dwc.text.ArchiveFactory;
import org.gbif.dwc.text.StarRecord;
import org.gbif.dwc.text.UnsupportedArchiveException;
import org.gcube.data.spd.irmng.dbconnection.ConnectionPool;
import org.gcube.data.spd.irmng.dbconnection.ConnectionPoolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UpdateThread extends Thread {

	long nextup;
	static Logger logger = LoggerFactory.getLogger(UpdateThread.class);

	UpdateThread(long update) {
		super("Thread");
		start(); // Start the thread
		this.nextup = update;
	}

	/**
	 * update db every 30 days
	 */
	public void run() {
		Date date = new Date();		
		logger.trace("Irmng DB will be updated on " + nextUpdateDate(nextup + date.getTime() ));
		while (true) {		
			try {
				sleep(nextup);
				update();	
				setlastupdate();
				this.nextup = 2592000000L;
			} catch (InterruptedException e) {
				logger.error("InterruptedException", e);
			}
		}
	} 

	/**
	 * Convert long in date
	 */
	private String nextUpdateDate(long input){  
		Date date = new Date(input);  
		Calendar cal = new GregorianCalendar();  
		cal.setTime(date);  
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return(dateFormat.format(date));  

	}  

	/**
	 * Download DwC-A file and open it
	 */
	protected void update(){
		File tempFolder = null;
		try {

			URL url = new URL(IrmngPlugin.dumpUrl);
			tempFolder = File.createTempFile("irmng-folder", "" );			
			tempFolder.delete();
			tempFolder.mkdir();	

			//			tempFolder = new File ("/tmp/irmng-folder7138471141385044811/");

			if (downloadFromUrl(url, tempFolder + "/IRMNG_DWC.zip")) {
				openArch(tempFolder);				
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally{			
			if (tempFolder != null)
				clearTmp(tempFolder);
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
		logger.trace("Deleted files");
	}

	/**
	 * Download dump DB from URL http://www.cmar.csiro.au/datacentre/downloads/IRMNG_DWC.zip
	 */
	public boolean downloadFromUrl(URL url, String string) throws IOException {

		boolean flag = false;
		logger.trace("Downloading " + string);
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URLConnection urlConn = url.openConnection();

			is = urlConn.getInputStream();
			fos = new FileOutputStream(string);

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

	/**
	 * Open DwC-A and read info
	 */
	protected void openArch(File tempFolder){

		try {
			
			Archive arch = ArchiveFactory.openArchive(new File(tempFolder + "/IRMNG_DWC.zip"), tempFolder);
			//			Archive arch = ArchiveFactory.openArchive(tempFolder);

			if (!arch.getCore().hasTerm(DwcTerm.scientificName)){
				logger.trace("This application requires dwc-a with scientific names");

			}else{
				ConnectionPool pool = null;
				Connection con = null;
				ResultSet result = null;
				try{
					pool = ConnectionPool.getConnectionPool();
					con = pool.getConnection();
					//					con.setAutoCommit(false);
					// loop over core darwin core records
					Iterator<StarRecord> iter = arch.iterator();
					StarRecord dwc;
					String stmt = null;

					ArrayList<String> terms = new ArrayList<String>();

					PreparedStatement stmtInsert = null;

					while(iter.hasNext()){
						dwc = iter.next();
						logger.trace(dwc.core().id());
						terms.add(dwc.core().id());
						terms.add(dwc.core().value(DwcTerm.scientificName));
						terms.add(dwc.core().value(DwcTerm.scientificNameAuthorship));
						terms.add(dwc.core().value(DwcTerm.genus));
						terms.add(dwc.core().value(DwcTerm.specificEpithet));
						terms.add(dwc.core().value(DwcTerm.family));
						terms.add(dwc.core().value(DwcTerm.taxonRank));
						terms.add(dwc.core().value(DwcTerm.taxonomicStatus));
						terms.add(dwc.core().value(DwcTerm.nomenclaturalStatus));
						terms.add(dwc.core().value(DwcTerm.nameAccordingTo));
						terms.add(dwc.core().value(DwcTerm.originalNameUsageID));
						terms.add(dwc.core().value(DwcTerm.namePublishedIn));
						terms.add(dwc.core().value(DwcTerm.acceptedNameUsageID));
						terms.add(dwc.core().value(DwcTerm.parentNameUsage));
						terms.add(dwc.core().value(DwcTerm.parentNameUsageID));
						terms.add(dwc.core().value(DwcTerm.taxonRemarks));
						terms.add(dwc.core().value(DcTerm.modified));
						terms.add(dwc.core().value(DwcTerm.nomenclaturalCode));

						//				for (Record extRecord : dwc.extension(GbifTerm.SpeciesProfile.qualifiedName())){
						//					if (!database.update("insert into speciesprofile (taxon_id, isextinct, ismarine) values ('" + dwc.core().id() + "', '" + extRecord.value(GbifTerm.isExtinct) + "', '" + extRecord.value(GbifTerm.isMarine) + "')"))
						//						logger.trace("insert into speciesprofile (taxon_id, isextinct, ismarine) values ('" + dwc.core().id() + "', '" + extRecord.value(GbifTerm.isExtinct) + "', '" + extRecord.value(GbifTerm.isMarine) + "')");
						//				}
						String query ="select count(*) from taxon where taxonid = ?";			
						result =  pool.selectPrestatement(query, dwc.core().id());	

						//				logger.trace("select count(*) from taxon where taxonid = '" + dwc.core().id() + "'");
						if (result.next()) {
							//if the row exists, update, else insert
							if (result.getInt(1) == 0)
								stmt = ("insert into taxon (taxonid, scientificname, scientificnameauthorship, genus, specificepithet, family, taxonrank, taxonomicstatus, nomenclaturalstatus, nameaccordingto, originalnameusageid, namepublishedin, acceptednameusageid, parentnameusage, parentnameusageid, taxonremarks, modified, nomenclaturalcode) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
							else
								stmt = ("update taxon set taxonid = ?, scientificname = ?, scientificnameauthorship = ?, genus = ?, specificepithet = ?, family = ?, taxonrank = ?, taxonomicstatus = ?, nomenclaturalstatus = ?, nameaccordingto = ?, originalnameusageid = ?, namepublishedin = ?, acceptednameusageid = ?, parentnameusage = ?, parentnameusageid = ?, taxonremarks = ?, modified = ?, nomenclaturalcode = ? where taxonid ='" + dwc.core().id() + "'");

							if (!pool.preStatement(stmt, terms, stmtInsert)){
								logger.trace("error");
							}
							terms.clear();
						}	
					}
				} catch (ConnectionPoolException e) {
					logger.error("ConnectionPoolException", e);					
				}
				finally{
					logger.trace("completed!");	
					//					con.commit();
					if (result!=null)
						result.close();
					if ((pool!=null) && (con!=null)){
						pool.releaseConnection(con);
					}
				}
			}
		} catch (UnsupportedArchiveException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * get next update date
	 */
	public static long lastupdate() {

		ConnectionPool pool = null;
		Connection con = null;
		ResultSet results = null;
		long nextUpdate = 0;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			Statement statement = con.createStatement();

			String query = "select date from updates where id= (select max(id) from updates)";    
			results = statement.executeQuery(query);
			Date lastUpdate = null;
			if (results.next()){
				lastUpdate = results.getDate(1);
				Date date = new Date();
				long days = date.getTime() - lastUpdate.getTime();
				if (days < 2592000000L){
					return (2592000000L - days);
				}
			}	

		} catch (SQLException e) {
			logger.error("SQLException", e);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		}finally {
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
			try {
				if (results != null) {
					results.close();
				}
			} catch (SQLException ex) {
				logger.error("sql Error", ex);
			}
		} 
		return nextUpdate;
	}


}



