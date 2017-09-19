package org.gcube.portlets.user.transectgenerator.databases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.hibernate.SessionFactory;

public class DBPostGISExtractror {

	private SessionFactory dbSession;
	private static final String hibFile = "GeoHibernate.cfg.xml";

	public void setDbSession(SessionFactory dbSession) {
		this.dbSession = dbSession;
	}

	public SessionFactory getDbSession() {
		return dbSession;
	}

	public DBPostGISExtractror(String cfg, LexicalEngineConfiguration config) {

		// initialize DB
		try {
//			dbSession = DatabaseFactory.initDBConnection(cfg + hibFile);
			dbSession = DatabaseFactory.initDBConnection(cfg + hibFile, config);
		} catch (Exception e) {
			AnalysisLogger.getLogger().error("Error in starting connection to PostGIS " + e.getLocalizedMessage());
		}
	}

	static final String extractionQuery = "select csquarecode,cntry_name from new_all_world s where (intersects(s.the_geom, ST_GeomFromText('LINESTRING(%1$s %2$s, %3$s %4$s)',%5$s)));";

	static final String completeExtractionQuery = "select distinct ss.csquarecode,d.%7$s,ss.cntry_name from (select csquarecode,cntry_name from new_all_world s where (intersects(s.the_geom, ST_GeomFromText('LINESTRING(%1$s %2$s, %3$s %4$s)',%5$s)))) ss left join %6$s d on (d.csquarecode=ss.csquarecode);";
	
	
	private LinkedHashMap<String,Double> bioValues;
	public Map<String,String> countryNames;
	
	public List<String> getAllInfo(String x1, String y1, String x2, String y2, String SRID,String tableName,String fieldName) {

		ArrayList<String> cSquareCodes = new ArrayList<String>();
		bioValues = new LinkedHashMap<String,Double>();
		countryNames = new HashMap<String, String>();
		
		String selectQuery = String.format(completeExtractionQuery, x1, y1, x2, y2, SRID, tableName, fieldName);
		AnalysisLogger.getLogger().debug("Executing PostGIS query: " + selectQuery);
		// selectQuery = "select * from all_world";
		try {
			List<Object> resultSet = DatabaseFactory.executeSQLQuery(selectQuery, dbSession);
			
			int c=0;
			// for each result row
			for (Object result : resultSet) {
				// take the single row
				Object[] resultArray = (Object[]) result;
				String square = (String) resultArray[0];
				Double value;
				try{
					value = (Double) resultArray[1];
					}catch (Exception e1){
						try{
							value = ((Float) resultArray[1]).doubleValue();
						}catch(Exception e2){
							value = ((Integer) resultArray[1]).doubleValue();
						}
				}
				
				if (value==null)
					value = Double.valueOf(0);
				
				String country_name = (String) resultArray[2];
				
				bioValues.put(square,value.doubleValue());
				cSquareCodes.add(square);
				if (country_name!=null)
					countryNames.put(square, country_name);
				
				
				c++;
			}
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug("DBPostGISExtractror-> Error in getting info from DB: " + e.getLocalizedMessage());
			
//			e.printStackTrace();
		}
		return cSquareCodes;
	}

	public Map<String,Double> getCalculatedBioValues(){
		return bioValues;
	}
	
	
	// static final String extractionQuery = "select csquarecode from all_world s where (s.csquarecode= '5002:391:2');";
	// static final String extractionQuery = "select csquarecode from all_world s;";
	// gets squarecodes for intersection
	
	public List<String> getCSquareCodes(String x1, String y1, String x2, String y2, String SRID) {

		ArrayList<String> cSquareCodes = new ArrayList<String>();
		countryNames = new HashMap<String, String>();
		
		String selectQuery = String.format(extractionQuery, x1, y2, x2, y2, SRID);
		AnalysisLogger.getLogger().debug("Executing PostGIS query: " + selectQuery);
		// selectQuery = "select * from all_world";
		try {
			List<Object> resultSet = DatabaseFactory.executeSQLQuery(selectQuery, dbSession);

			// for each result row
			for (Object result : resultSet) {
				// take the single row
				Object[] resultArray = (Object[]) result;
//				String square = (String) result;
				String square = (String) resultArray[0];
				String countryName = (String) resultArray[0];
				countryNames.put(square, countryName);
				
				cSquareCodes.add(square);
			}
		} catch (Exception e) {

		}
		return cSquareCodes;
	}

	static final String biodiversityQuery = "select distinct f.\"%1$s\",f.\"csquarecode\" from %2$s f where (%3$s);";
	static final String whereQuery = "csquarecode='%1$s'";

	// gets biodiversity value for selected squares
	public Map<String,Double> getBioDiversity(String bioDivTable, String bioDivField, List<String> cSquareCodes) {

		 LinkedHashMap<String,Double> bioValues = new LinkedHashMap<String,Double>();

		String selectQuery = String.format(biodiversityQuery, bioDivField, bioDivTable, DBprocessing.buildWhereQuery(cSquareCodes, whereQuery));

		AnalysisLogger.getLogger().debug("Executing PostGIS query: " + selectQuery);

		List<Object> resultSet = DatabaseFactory.executeSQLQuery(selectQuery, dbSession);
		if (resultSet != null) {
			// for each result row
			for (Object result : resultSet) {
				// take the single row
				Object[] resultArray = (Object[]) result;
				
				
				Double value = (Double) resultArray[0];
				
				
				String  cell = (String) resultArray[1];
				bioValues.put(cell,value.doubleValue());
			}
		}
		return bioValues;
	}

}
