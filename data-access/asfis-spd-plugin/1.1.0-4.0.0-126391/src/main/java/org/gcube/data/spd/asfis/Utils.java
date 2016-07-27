package org.gcube.data.spd.asfis;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.asfis.dbconnection.ConnectionPool;
import org.gcube.data.spd.asfis.dbconnection.ConnectionPoolException;
import org.gcube.data.spd.model.CommonName;

public class Utils {

	static GCUBELog logger = new GCUBELog(Utils.class);

	//format date
	public static String createDate() {
		Calendar now = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(now.getTime());
		return date;
	}

	public static String createCitation() {
		StringBuilder cit = new StringBuilder();
		cit.append(AsfisPlugin.citation);
		cit.append(createDate());
		return cit.toString();
	}

	public static String createCredits() {
		String cred = AsfisPlugin.credits;
		cred = cred.replace("XDATEX",createDate());	
		return cred;
	}

	//create commonNames List
	public static List<CommonName> setCommonNames(String englishName,
			String frenchName, String spanishName, String arabic_name, String chinese_name, String russian_name) {

		//		System.out.println(englishName + " " + frenchName  + " " + spanishName + " " + arabic_name+ " " +  chinese_name + " " + russian_name);
		List<CommonName> commonNames = null;
		try{
			commonNames = new ArrayList<CommonName>();

			if (englishName!=null)
				if (!englishName.isEmpty())
					commonNames.add(new CommonName(AsfisPlugin.ENG, englishName));

			if (frenchName!=null)
				if (!frenchName.isEmpty())
					commonNames.add(new CommonName(AsfisPlugin.FRA, frenchName));

			if (spanishName!=null)
				if (!spanishName.isEmpty())
					commonNames.add(new CommonName(AsfisPlugin.SPA, spanishName));

			if (arabic_name!=null)
				if (!arabic_name.isEmpty())
					commonNames.add(new CommonName(AsfisPlugin.ARA, arabic_name));

			if (chinese_name!=null)
				if (!chinese_name.isEmpty())
					commonNames.add(new CommonName(AsfisPlugin.CHI, chinese_name));

			if (russian_name!=null)
				if (!russian_name.isEmpty())
					commonNames.add(new CommonName(AsfisPlugin.RUS, russian_name));


		}catch (Exception e) {
			logger.error("Error creating common names list ", e);
		}
		return commonNames;

	}

	/**
	 * Return a ResultSet of scientific names
	 */
	public static ResultSet createResultSetByName(String scientificName) {

		ConnectionPool pool = null;
		ResultSet results = null;
		try {
			pool = ConnectionPool.getConnectionPool();

			String term = "%" + scientificName + "%";

			String query = "select id, Scientific_name, Author, English_name, French_name, Spanish_name, parent_id, TAXOCODE, ISSCAAP, threeA_CODE, rank, Arabic_name, Chinese_name, Russian_name from "+ AsfisPlugin.table + " where UPPER(Scientific_name) like UPPER(?)";
			results =  pool.selectPrestatement(query, term);
		}
		catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		}finally{

		}
		return results;
	}


	/**
	 * Return a ResultSet of scientific names
	 */
	public static ResultSet createResultSetByID(String id) {

		ConnectionPool pool = null;
		ResultSet results = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			String query = "select Scientific_name, parent_id, rank from "+ AsfisPlugin.table + " where id = " + id;
			results =  pool.selectPrestatement(query, null);
		}
		catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		}finally{

		}
		return results;
	}


	/**
	 * Return a ResultSet of scientific names
	 */
	public static ResultSet createCompleteResultSetByID(String id) {

		ConnectionPool pool = null;
		ResultSet results = null;
		try {
			pool = ConnectionPool.getConnectionPool();

			String query = "select Scientific_name, Author, English_name, French_name, Spanish_name, parent_id, TAXOCODE, ISSCAAP, threeA_CODE, rank, Arabic_name, Chinese_name, Russian_name from "+ AsfisPlugin.table + " where id = " + id;
			results =  pool.selectPrestatement(query, null);
		}
		catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		}finally{

		}
		return results;
	}
	

}



