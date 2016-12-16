package org.gcube.data.spd.flora;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.flora.dbconnection.ConnectionPool;
import org.gcube.data.spd.flora.dbconnection.ConnectionPoolException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Utils {

	//DERBY CONNECTION
	//	public static String dbURL = "jdbc:derby:classpath:FloraDB;create=true";
	//public static String dbURL = "jdbc:derby:FloraDB;create=true";

	static GCUBELog logger = new GCUBELog(Utils.class);

	/**
	 * Return true if a url exists
	 */
	public static boolean urlExists(String pathUrl) {
		HttpURLConnection con = null;
		try {
			HttpURLConnection.setFollowRedirects(false);
			con = (HttpURLConnection) new URL(pathUrl).openConnection();
			// Set timeouts in milliseconds
			con.setConnectTimeout(30000);
			con.setReadTimeout(30000);

			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		}
		catch (Exception e) {
			logger.error("Exception",e);
			return false;
		}finally{
			if( con != null ){
				con.disconnect();
			}
		}
	}

	/**
	 * Metod called by CreateDBThread
	 */
	public static boolean createDB() throws SQLException, IOException {
		BufferedReader br = null;
		ConnectionPool pool = null;
		try {
			pool = ConnectionPool.getConnectionPool();

				logger.trace("Create tables...");
				boolean updateQuery;
				br = new BufferedReader(new InputStreamReader(FloraPlugin.class.getResourceAsStream(FloraPlugin.dumpDb)));
				if (br!=null){
					String line;
					while ((line = br.readLine()) != null) {
						updateQuery = pool.insertPreStatement(line);	
						if (!updateQuery) 
							logger.trace(line);
					}
				}
		} catch (ConnectionPoolException e) {
//			logger.error("ConnectionPoolException", e);
			return false;
		}
		
		return true;

	}

	//format date
	public static String createDate() {
		Calendar now = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(now.getTime());
		return date;
	}

	public static String createCitation() {
		StringBuilder cit = new StringBuilder();
		cit.append(FloraPlugin.citation);
		cit.append(createDate());
		return cit.toString();
	}

	public static String createCredits() {
		String cred = FloraPlugin.credits;
		cred = cred.replace("XDATEX",createDate());	
		return cred;
	}
	/**
	 * getTagValue
	 */
	public static String getTagValue(String sTag, Element eElement) {	

		//		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();	

		String a = "";			
		NodeList nlList = eElement.getElementsByTagName(sTag);
		if (nlList!= null){	
			Element xmlNode = (Element)nlList.item(0);
			if (xmlNode != null){
				NodeList textFNList = xmlNode.getChildNodes();
				if (textFNList != null){
					Node nValue = (Node) textFNList.item(0);  
					if (nValue != null){
						a = nValue.getNodeValue(); 
					}
				}
			}
		}
		// System.out.println(a);
		return a;
	}


	/**
	 * Find info about a specific id
	 */

	public static Map<String, String> findTags(String id) throws SQLException, ConnectException {
		String pathUrl = FloraPlugin.baseurl + "/FULLRECORD/VERSION/2012/FORMAT/xml/LANG/en/ID/" + id;
		//		logger.trace("Find info " + tag);
		//				logger.trace(pathUrl);
		Map<String, String> values = new HashMap<String, String>();
		if (urlExists(pathUrl)){
			InputStream is = null;
			XMLInputFactory ifactory;
			XMLEventReader eventReader = null;
			try{
				//				logger.trace("open 1");
				is = URI.create(pathUrl).toURL().openStream();
				ifactory = XMLInputFactory.newInstance();
				eventReader = ifactory.createXMLEventReader(is, "utf-8");

				while (eventReader.hasNext()){
					XMLEvent event = eventReader.nextEvent();

					if (checkStartElement(event, "plain_name")){	
						//						logger.trace(readCharacters(eventReader));
						values.put("plain_name", readCharacters(eventReader));		
						continue;
					}else if (checkStartElement(event, "author")){
						//						logger.trace(readCharacters(eventReader));
						values.put("author", readCharacters(eventReader));			
						continue;
					}else if (checkStartElement(event, "rank")){
						//						logger.trace(readCharacters(eventReader));
						values.put("rank", readCharacters(eventReader));		
						continue;
					}else if (checkStartElement(event, "status")){	
						//						logger.trace(readCharacters(eventReader));
						values.put("status", readCharacters(eventReader));		
						continue;
					}else if (checkStartElement(event, "qualifier")){
						//						logger.trace(readCharacters(eventReader));
						values.put("qualifier", readCharacters(eventReader));		
						continue;
					}else if (checkEndElement(event, "record")){		
						break;
					}
				}

			} catch (Exception e) {
				logger.trace("Error reading " + pathUrl );
				//				logger.error("ExceptionPrintStackTrace",e);
			}			
			finally{
				//				logger.trace("close 1");
				try {
					if (eventReader != null)
						eventReader.close();
					if (is != null)
						is.close();
				} catch (XMLStreamException e) {
					logger.error("XMLStreamException",e);
				} catch (IOException e) {
					logger.error("IOException",e);
				}					
			}

		}
		return values;
	}


	protected static String readCharacters(XMLEventReader eventReader) throws Exception{
		String characters="";
		XMLEvent event = eventReader.nextEvent();
		while (eventReader.hasNext() && event.isCharacters() ){
			characters+= event.asCharacters().getData();
			event = eventReader.nextEvent();
		}		
		return characters.trim();
	}

	protected static boolean checkStartElement(XMLEvent event, String value){
		return event.getEventType() == XMLStreamConstants.START_ELEMENT && event.asStartElement().getName().getLocalPart().equals(value);
	}

	protected static boolean checkEndElement(XMLEvent event, String value){
		return event.getEventType() == XMLStreamConstants.END_ELEMENT && event.asEndElement().getName().getLocalPart().equals(value);
	}



	/**
	 * Parse every path and populate db
	 */
	public static ArrayList<ArrayList<String>> discoverPath(String pathUrl, String id_parent, String name_parent, Integer idThread, Boolean flag) throws SQLException, MalformedURLException, IOException {

		ArrayList<ArrayList<String>> listUrls = new ArrayList<ArrayList<String>>();
		if (Utils.urlExists(pathUrl)){
			String id = "";
			String path = "";
			String rank = "";
			String name = "";
			//			String status = "";

//						logger.trace(pathUrl);
			InputStream is = null;
			XMLInputFactory ifactory;
			XMLEventReader eventReader = null;
			try {	
				//				logger.trace("open 3");
				is =URI.create(pathUrl).toURL().openStream();
				ifactory = XMLInputFactory.newInstance();
				eventReader = ifactory.createXMLEventReader(is, "utf-8");
				while (eventReader.hasNext()){
					XMLEvent event = eventReader.nextEvent();

					if (Utils.checkStartElement(event, "id")){	
						id = readCharacters(eventReader);
						continue;		
					} else if (Utils.checkStartElement(event, "path")){
						path = readCharacters(eventReader);
						continue;
					} else if (Utils.checkStartElement(event, "rank")){
						rank = readCharacters(eventReader);
						continue;
					} else if (Utils.checkStartElement(event, "name")){
						name = readCharacters(eventReader);
						continue;					
					} else if (Utils.checkEndElement(event, "record")){		
						//												logger.trace(path);
						createOrUpdate(id_parent, id, name, path, rank);
						// child Threads check other urls
						if (flag) { 
							discoverPath("http://checklist.florabrasil.net/service/TREE/VERSION/2012/FORMAT/xml/LANG/en/PATH/"  + path, id, name, idThread, flag);
						}
						//			    		 // the main thread gets the urls
						else {
							ArrayList<String> elem = new ArrayList<String> ();
							elem.add(path);
							elem.add(id);
							elem.add(name);
							listUrls.add(elem);				    	  
						}
						continue;
					} else if (Utils.checkEndElement(event, "result")){		
						//						logger.trace("break");
						break;
					}
				}
			} catch (Exception e) {
				logger.error("ExceptionPrintStackTrace",e);
			}finally{				
				try {
					//					logger.trace("close 3");
					if (eventReader != null)
						eventReader.close();
					if (is != null)
						is.close();
				} catch (XMLStreamException e) {
					logger.error("XMLStreamException",e);
				}
			}
		}
		return listUrls;
	}



	protected static void createOrUpdate(String id_parent, String id, String name, String path, String rank) {

		//				logger.trace(path);
		ResultSet res = null;
		//		Database database = null;  
		String stmt = "";
		ArrayList<String> terms = new ArrayList<String>();
		Map<String, String> values = null;
		ConnectionPool pool = null;
		Connection con = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			res = pool.selectPrestatement("select count(*) from "+ FloraPlugin.tableName + " where id = ? ", id);

			if(res.next()) {						
				values = findTags(id);
				if (values.size()>0){
					terms.add(id);
					terms.add(rank);
					terms.add(name);
					terms.add(values.get("plain_name"));
					terms.add(id_parent);
					terms.add(values.get("author"));
					terms.add(values.get("status"));
					terms.add(path);
					terms.add(values.get("qualifier"));

					if  (res.getInt(1) == 0) {
						//Set values
						stmt = ("insert into "+ FloraPlugin.tableName + " (id, rank, name, scientific_name, id_parent, citation, status, path, qualifier) values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
					}
					else{	
						//										logger.trace("update "+ FloraPlugin.tableName + " set id = ?, rank = ?, scientific_name = ?, id_parent = ?, citation = ?, status = ? where id = '" + id + "'");
						stmt = ("update "+ FloraPlugin.tableName + " set id = ?, rank = ?, name = ?, scientific_name = ?, id_parent = ?, citation = ?, status = ?, path = ?, qualifier = ? where id = '" + id + "'");
					}

					if (!pool.preStatement(stmt, terms)){
						logger.trace("error");
					}
					terms.clear();
					values.clear();
				}
			}
		}
		catch (SQLException e) {
			logger.error("SQL Exception",e);
		} catch (ConnectException e) {
			logger.error("Connection timed out",e);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException",e);
		}finally{
			try {
				if (res != null) {
					res.close();
				}
				if ((pool!=null) && (con!=null)){
					pool.releaseConnection(con);
				}
			}catch (Exception e) {
				logger.error("Exception",e);
			}	
		}	


	}





	/**
	 * Return true if a table exists
	 */
	public static boolean SQLTableExists(String tableName) {
		boolean exists = false;
		ConnectionPool pool = null;
		Connection con = null;
		ResultSet results = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			if (pool!=null){
				con = pool.getConnection();
				if (con!=null){
					Statement statement = con.createStatement();

					String sqlText = "SELECT tables.table_name FROM information_schema.tables WHERE table_name = '" + tableName + "'";    

					results = statement.executeQuery(sqlText);
					if (results.next()){
						logger.trace(tableName + " already exists");
						exists = true;
					}
					else{
						logger.trace(tableName + " does not exists");
						exists = false;
					}
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

		return exists;
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
			if (con!=null){
				Statement statement = con.createStatement();

				String query = "select date from updates where id = (select max(id) from updates)";    
				logger.trace(query);
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

	/**
	 * Convert long in date
	 */
	public static String nextUpdateDate(long input){  
		Date date = new Date(input);  
		Calendar cal = new GregorianCalendar();  
		cal.setTime(date);  
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return(dateFormat.format(date));  

	} 

}


/**
 * Update floraDB thread
 */

class UpdateThread extends Thread {

	long nextup;
	static GCUBELog logger = new GCUBELog(UpdateThread.class);

	UpdateThread(long update) {
		super("Thread");
		this.nextup = update;
		start(); // Start the thread		
	}	

	// update db every month (30 days)
	public void run() {	

		while (true) {	
			logger.trace(createLabel(nextup));
			try {
				sleep(nextup);
				//Utils util = new Utils();
				if (upInfo()){	
					setlastupdate();		
					this.nextup = 2592000000L;
				}
			} catch (SQLException e) {
				logger.error("sql Error", e);
			} catch (Throwable e) {
				logger.error("General Error", e);
			}
		}
	} 


	/**
	 * Set next update date
	 */
	private String createLabel(long nextup) {
		Date date = new Date();		
		String label = "Flora DB will be updated on " + Utils.nextUpdateDate(nextup + date.getTime());
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

			result =  pool.insertPreStatement(query);
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

	/**
	 * Update db by new threads
	 */
	public static boolean upInfo() throws SQLException, MalformedURLException, IOException {

				logger.trace("Update DB in BRAZILIAN-FLORA-PLUGIN");
		try{
			ArrayList<ArrayList<String>> list = Utils.discoverPath("http://checklist.florabrasil.net/service/TREE/VERSION/2012/FORMAT/xml/LANG/en/", null, "", null, false);
			int sizeList = list.size();
			logger.trace(list);
			Thread[] threads = new Thread[sizeList];

			for (int i = 0; i < sizeList; i++) {
				logger.trace(list.get(i));
				threads[i] = new NewThread(list.get(i), i); // creo un nuovo thread			
		
			}

			for (int i = 0; i < sizeList; i++) {
				try {
					logger.trace("join");
					threads[i].join();
				} catch (InterruptedException e) {
					logger.trace("interrupted join");
					return false;
				}
			}
		}catch (Exception e) {
			logger.error("exc", e);
			logger.trace("Exception");
			return false;
		}  
		
		return true;
	}
}


/**
 * Update Synonyms table
 */

class UpdateSynonymsThread extends Thread {

	static GCUBELog logger = new GCUBELog(UpdateThread.class);
	long nextup;

	UpdateSynonymsThread(long update) {
		super("Thread");
		this.nextup = update;
		start(); // Start the thread
	}

	// update db every month (30 days)
	public void run() {
		Date date = new Date();	
		logger.trace("Synonyms table in Flora DB will be updated on " + Utils.nextUpdateDate(nextup + date.getTime() ));
		while (true) {		
			try {

				sleep(nextup);
				//Utils util = new Utils();
				if (synonyms()){		
					this.nextup = 2764800000L;
				}

			} catch (SQLException e) {
				logger.error("sql Error", e);
			} catch (Throwable e) {
				logger.error("General Error", e);
			}
		}
	} 


	/**
	 * Parse every path looking for synonyms
	 * @return 
	 */
	protected static boolean synonyms() throws SQLException {		

		ResultSet res = null;
		ConnectionPool pool = null;

		try {
			pool = ConnectionPool.getConnectionPool();
			logger.trace("select a.id, a.name, a.rank, b.name, c.name, a.id_parent from " + FloraPlugin.tableName + " as a join flora as b on (a.rank = 'SubSpecies' or a.rank = 'Species' or a.rank = 'Genus') and a.id_parent = b.id join flora as c on b.id_parent=c.id where a.status = 'Accepted name'");
			res = pool.selectPrestatement("select a.id, a.name, a.rank, b.name, c.name, a.id_parent from " + FloraPlugin.tableName + " as a join flora as b on (a.rank = 'SubSpecies' or a.rank = 'Species' or a.rank = 'Genus') and a.id_parent = b.id join flora as c on b.id_parent=c.id where a.status = 'Accepted name'", null);

			//			res = database.get("select a.id, a.name, a.rank, b.name, c.name, a.id_parent from " + FloraPlugin.tableName + " as a join flora as b on (a.rank = 'SubSpecies' or a.rank = 'Species' or a.rank = 'Genus') and a.id_parent = b.id join flora as c on b.id_parent=c.id where a.status = 'Accepted name' ORDER BY a.id");

			while(res.next()) {

				String id = res.getString(1);
				String name = res.getString(2);
				String rank = res.getString(3);
				String parent = res.getString(4);
				String grandparent = res.getString(5);
				String id_parent = res.getString(6);	
				String pathUrl = "";

				if (rank.equals("Genus")) {
					pathUrl = "http://www.checklist.florabrasil.net/service/VERSION/2012/FORMAT/xml/LANG/en/SYNONYMS/GENUS/" + name.replace(" ", "%20");
					//					rankParent = "family";
				}
				else if (rank.equals("Species")) {
					pathUrl = "http://www.checklist.florabrasil.net/service/VERSION/2012/FORMAT/xml/LANG/en/SYNONYMS/GENUS/" + parent + "/SPECIES/" + name.replace(" ", "%20");
					//					rankParent = "genus";
				}
				else if (rank.equals("SubSpecies")) {
					pathUrl = "http://www.checklist.florabrasil.net/service/VERSION/2012/FORMAT/xml/LANG/en/SYNONYMS/GENUS/" + grandparent + "/SPECIES/" + parent + "/SUBSPECIES/" + name.replace(" ", "%20");
					//					rankParent = "species";
				}

				//							logger.trace(pathUrl);
				insertSynonym(id, id_parent, pathUrl);						
			}			
		} catch (SQLException sqlExcept) {

			logger.error("SQL Exception", sqlExcept);
			return false;
		} catch (ConnectionPoolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (res != null)
				res.close();
		}
		return true;
	}

	/**
	 * Populate table synonyms
	 */
	protected static void insertSynonym(String id, String id_parent, String pathUrl) {
		try {	
			//								logger.trace(" pathUrl: " +pathUrl);
			String idSyn = "";
			String stmt = "";
			Map<String, String> values = null;
			if (Utils.urlExists(pathUrl)){

				InputStream is = null;
				XMLInputFactory ifactory;
				XMLEventReader eventReader = null;
				try{
					//					logger.trace("open 2");
					is = URI.create(pathUrl.replace(" ", "")).toURL().openStream();
					ifactory = XMLInputFactory.newInstance();
					eventReader = ifactory.createXMLEventReader(is, "utf-8");

					while (eventReader.hasNext()){
						XMLEvent event = eventReader.nextEvent();

						if (Utils.checkStartElement(event, "id")){
							idSyn = Utils.readCharacters(eventReader);

							continue;		
						} else if (Utils.checkEndElement(event, "result")){	
							break;
						} else if (Utils.checkEndElement(event, "record")){		

							if (!idSyn.equals(id)){
								//								logger.trace("found synonym");
								ConnectionPool pool = null;
								Connection con = null;
								ResultSet res = null;
								try {
									pool = ConnectionPool.getConnectionPool();
									con = pool.getConnection();
									ArrayList<String> terms = new ArrayList<String>();
									res = pool.selectPrestatement("select count(*) from " + FloraPlugin.tableName + " where id = ? ", idSyn);
									if(res.next()) {
										if  (res.getInt(1) == 0) { 
											values = Utils.findTags(idSyn);

											terms.add(idSyn);																						
											terms.add(values.get("rank"));
											terms.add(values.get("plain_name"));
											terms.add(id_parent);
											terms.add(values.get("author"));
											terms.add(id);
											terms.add(values.get("status"));
											terms.add(pathUrl);
											terms.add(values.get("qualifier"));


											stmt = ("insert into flora (id, rank, scientific_name, id_parent, citation, acceptednameusageid, status, path, qualifier) values (?, ?, ?, ?, ?, ?, ?, ?, ?)");

											//										else{	
											//											stmt = ("update flora set id = ?, rank = ?, scientific_name = ?, id_parent = ?, citation = ?, acceptednameusageid = ?, status = ?, path = ?, qualifier = ? where id = '" + idSyn + "'");
											//										}

											if (!pool.preStatement(stmt, terms)){
												logger.trace("error");
											}
											terms.clear();
											values.clear();
										}
									}
								}catch (Exception e) {
									logger.error("Error reading " + pathUrl);
									//									logger.error("General Error", e);
								}finally{			
									if (res != null)
										res.close();
									if ((pool!=null) && (con!=null)){
										pool.releaseConnection(con);
									}
								}
							} 
						}
					}
				} catch (Exception e) {
					logger.error("ExceptionPrintStackTrace",e);
				}finally{	
					//					logger.trace("close 2");
					try {
						if (eventReader != null)
							eventReader.close();
						if (is != null)
							is.close();
					} catch (XMLStreamException e) {
						logger.error("XMLStreamException",e);
					}
				}
			}
		}catch (Exception e) {
			logger.error("General Error", e);
		}
	}
}



class NewThread extends Thread {

	static GCUBELog logger = new GCUBELog(NewThread.class);

	ArrayList<String> elem;
	String pathUrls;
	String id_parent;
	Integer idThread;
	String sName;

	NewThread(ArrayList<String> arrayList, Integer idThread) {

		super("Thread");
		this.elem = arrayList;
		this.pathUrls = (elem.get(0));
		this.id_parent = (elem.get(1));
		this.sName = (elem.get(2));
		this.idThread = idThread;
		start(); // Start the thread

	}

	// This is the entry point for the child threads
	public void run() {
		try {
//						logger.trace("http://checklist.florabrasil.net/service/TREE/VERSION/2012/FORMAT/xml/LANG/en/PATH/" + pathUrls);
			Utils.discoverPath("http://checklist.florabrasil.net/service/TREE/VERSION/2012/FORMAT/xml/LANG/en/PATH/" + pathUrls, id_parent, sName, idThread,  true);		   	
			//			Discover.shutdown();
		}catch (SQLException e) {
			logger.error("SQL Exception", e);
		} catch (MalformedURLException e) {
			logger.error("MalformedURL Exception", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		}
	}

}



