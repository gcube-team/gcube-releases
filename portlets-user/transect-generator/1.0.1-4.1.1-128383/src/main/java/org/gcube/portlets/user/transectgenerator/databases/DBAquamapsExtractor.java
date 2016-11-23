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

public class DBAquamapsExtractor {

	private SessionFactory dbSession;
	private static final String hibFile = "AquamapsHibernate.cfg.xml";
	private HashMap<Integer,String> FAOAreasNames;
	private HashMap<Integer,String> EEZAreasNames;
	private HashMap<Integer,String> LMEAreasNames;
	
	public void setDbSession(SessionFactory dbSession) {
		this.dbSession = dbSession;
	}

	public SessionFactory getDbSession() {
		return dbSession;
	}

	public DBAquamapsExtractor (String cfg, LexicalEngineConfiguration config){
		
		//initialize DB
		try {
			dbSession = DatabaseFactory.initDBConnection(cfg+hibFile, config);
//			dbSession = DatabaseFactory.initDBConnection(cfg+hibFile);
		} catch (Exception e) {
			AnalysisLogger.getLogger().error("Error in starting connection to Aquamaps "+e.getLocalizedMessage());
		}
		
		FAOAreasNames = new HashMap<Integer, String>();
		EEZAreasNames = new HashMap<Integer, String>();
		LMEAreasNames = new HashMap<Integer, String>();
		
		AnalysisLogger.getLogger().error("DBAquamapsExtractor->Retrieving info about Areas");
		fillAreas();
		
	}
	
	private static final String areasNamesQuery = "SELECT code,name,type FROM area where code IS NOT NULL";
	private static final String nationsQuery = "SELECT countryname FROM countriessquares c where csquarecode ='%1$s'";
	
	private void fillAreas(){
		
		try{
			List<Object> resultSet = DatabaseFactory.executeSQLQuery(areasNamesQuery, dbSession);
			// for each result row
			for (Object result : resultSet) {
				// take the single row
				Object[] resultArray = (Object[]) result;
				Integer code = Integer.parseInt((String)resultArray[0]);
				
				String name = (String)resultArray[1];
				String type = (String)resultArray[2];
				if (type.equalsIgnoreCase("FAO"))
					FAOAreasNames.put(code, name);
				else if (type.equalsIgnoreCase("LME"))
					LMEAreasNames.put(code, name);
				else if (type.equalsIgnoreCase("EEZ"))
					EEZAreasNames.put(code, name);
			}
		}catch (Exception e){
			AnalysisLogger.getLogger().error("DBAquamapsExtractor-> Error in getting Areas or missing AREA table");
		}
		
	}
	
	
	
	static final String extractionQuery = "SELECT CSquareCode,CenterLat,CenterLong,EEZFirst,FAOAreaM FROM hcaf_s h where (%1$s) order by CenterLong;";
	static final String whereQuery = " CSquareCode='%1$s' ";
	

	LinkedHashMap<String,String> areasCodes = new LinkedHashMap<String, String>();
	
	//gets triples to be displayed (Longitude,Latitude,value)
	public Map<String,String> getLongLatBioDiversity(List<String> csquarecodes, Map<String,String> areaNames){
		
		LinkedHashMap<String,String> longlatcouples = new LinkedHashMap<String, String>();
		
		String selectQuery = String.format(extractionQuery, DBprocessing.buildWhereQuery(csquarecodes,whereQuery));
		AnalysisLogger.getLogger().debug("Executing query on Aquamaps: "+selectQuery);
		List<Object> resultSet = DatabaseFactory.executeSQLQuery(selectQuery, dbSession);
		
		// for each result row
		for (Object result : resultSet) {
			// take the single row
			Object[] resultArray = (Object[]) result;
			String code = ""+resultArray[0];
			String Lat$ = ""+resultArray[1];
			String Long$ = ""+resultArray[2];
			String couple = "("+Lat$+","+Long$+")";
			
			String area = "";
			if (resultArray[3]!=null){
				area = EEZAreasNames.get(resultArray[3]);
				/*
				if (area!=null){
					area = "EEZ_"+area;
				}
				*/
				//area = " EEZ_"+resultArray[3]+",";
			}
			
			if ((area==null) || (area.length()==0)){
				String FAOAr = FAOAreasNames.get(resultArray[4]);
				if (FAOAr!=null)
					area = "FAO_"+FAOAr;
				else{
//					area = "FAOArea_"+resultArray[4];
					if (areaNames==null){
						area = getNation(code);	
					}
					else{
						area = areaNames.get(code);
					}
					if (area == null)
						area = "FAOArea_LAND";
				}
			}
			
			
			longlatcouples.put(code,couple);
			areasCodes.put(couple,area);
			
		}
		
		return longlatcouples;
	}
	
	
	public String getNation (String csquarecode){
		String nation = null;
		try{
			List<Object> resultSet = DatabaseFactory.executeSQLQuery(String.format(nationsQuery, csquarecode), dbSession);
			if (resultSet.size()>0){
					nation = (String)resultSet.get(0);
			}
		}catch (Exception e){
			
			e.printStackTrace();
		}
		return nation;
	}
	
	
	
	public List<String> getAreaAnotations(List<String> couples){
		
		ArrayList<String> anotations = new ArrayList<String>();
		
		for (String couple:couples){
			
			anotations.add(areasCodes.get(couple));
		}
		
		return anotations;
	}
}
