package org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data;

import java.sql.Time;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.ClientResourceType;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ClientResource extends BaseModelData implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TITLE="title";
	public static final String SEARCH_ID="searchid";
	public static final String TABLE_NAME="tablename";
	public static final String DESCRIPTION="description";
	public static final String AUTHOR="author";
	public static final String DISCLAIMER="disclaimer";
	public static final String PROVENANCE="provenience";
	public static final String GENERATION_TIME="generationtime";
	public static final String SOURCE_HCAF="sourcehcafids";
	public static final String SOURCE_HSPEN="sourcehspenids";
	public static final String SOURCE_HSPEC="sourcehspecids";
	public static final String SOURCE_OCCURRENCE="sourceoccurrencecellsids";
	public static final String SOURCE_HCAF_TABLE="sourcehcaftables";
	public static final String SOURCE_HSPEN_TABLE="sourcehspentables";
	public static final String SOURCE_HSPEC_TABLE="sourcehspectables";
	public static final String SOURCE_OCCURRENCE_TABLE="sourceoccurrencecellstables";
	public static final String PARAMETERS="parameters";
	public static final String STATUS="status";
	public static final String TYPE="type";
	public static final String ALGORITHM="algorithm";
	public static final String DEFAULT="defaultsource";
	public static final String ROW_COUNT="rowcount";


	

	public ClientResource() {
		// TODO Auto-generated constructor stub
	}







	public ClientResource(String title, Integer searchId, String tableName,
			String description, String author, String disclaimer,
			String provenance, Time time, String hcafIds, String hspenIds,
			String hspecIds, String occurrIds, String hcafNames,
			String hspenNames, String hspecNames, String parameters,
			String occurNames, String status, ClientResourceType type,
			AlgorithmType algorithm, Boolean isDefault,Long rowCount) {
		super();
		setTitle(title);		
		setSearchId(searchId);
		setTableName(tableName);
		setDescription(description);
		setAuthor(author);
		setDisclaimer(disclaimer);
		setProvenance(provenance);
		setTime(time);
		setHcafIds(hcafIds);
		setHspenIds(hspenIds);
		setHspecIds(hspecIds);
		setOccurrIds(occurrIds);
		setHcafNames(hcafNames);
		setHspenNames(hspenNames);
		setHspecNames(hspecNames);
		setParameters(parameters);
		setOccurNames(occurNames);
		setStatus(status);
		setType(type);
		setAlgorithm(algorithm);
		setIsDefault(isDefault);
		setRowCount(rowCount);
	}

	public ClientResource(Map<String, Object> properties){
		try{if(properties.containsKey(ALGORITHM))setAlgorithm(AlgorithmType.valueOf((String) properties.get(ALGORITHM)));}catch(Exception e){}
		try{if(properties.containsKey(TITLE))setTitle((String) properties.get(TITLE));		}catch(Exception e){}
		try{if(properties.containsKey(SEARCH_ID))setSearchId(Integer.parseInt((String) properties.get(SEARCH_ID)));}catch(Exception e){}
		try{if(properties.containsKey(TABLE_NAME))setTableName((String) properties.get(TABLE_NAME));}catch(Exception e){}
		try{if(properties.containsKey(DESCRIPTION))setDescription((String) properties.get(DESCRIPTION));}catch(Exception e){}
		try{if(properties.containsKey(AUTHOR))setAuthor((String) properties.get(AUTHOR));}catch(Exception e){}
		try{if(properties.containsKey(DISCLAIMER))setDisclaimer((String) properties.get(DISCLAIMER));}catch(Exception e){}
		try{if(properties.containsKey(PROVENANCE))setProvenance((String) properties.get(PROVENANCE));}catch(Exception e){}
		try{if(properties.containsKey(GENERATION_TIME))setTime(new Time(Long.parseLong((String) properties.get(GENERATION_TIME))));}catch(Exception e){}
		try{if(properties.containsKey(SOURCE_HCAF))setHcafIds((String) properties.get(SOURCE_HCAF));}catch(Exception e){}
		try{if(properties.containsKey(SOURCE_HSPEN))setHspenIds((String) properties.get(SOURCE_HSPEN));}catch(Exception e){}
		try{if(properties.containsKey(SOURCE_HSPEC))setHspecIds((String) properties.get(SOURCE_HSPEC));}catch(Exception e){}
		try{if(properties.containsKey(SOURCE_OCCURRENCE))setOccurrIds((String) properties.get(SOURCE_OCCURRENCE));}catch(Exception e){}
		try{if(properties.containsKey(SOURCE_HCAF_TABLE))setHcafNames((String) properties.get(SOURCE_HCAF_TABLE));}catch(Exception e){}
		try{if(properties.containsKey(SOURCE_HSPEN_TABLE))setHspenNames((String) properties.get(SOURCE_HSPEN_TABLE));}catch(Exception e){}
		try{if(properties.containsKey(SOURCE_HSPEC_TABLE))setHspecNames((String) properties.get(SOURCE_HSPEC_TABLE));}catch(Exception e){}
		try{if(properties.containsKey(PARAMETERS))setParameters((String) properties.get(PARAMETERS));}catch(Exception e){}
		try{if(properties.containsKey(SOURCE_OCCURRENCE_TABLE))setOccurNames((String) properties.get(SOURCE_OCCURRENCE_TABLE));}catch(Exception e){}
		try{if(properties.containsKey(STATUS))setStatus((String) properties.get(STATUS));}catch(Exception e){}
		try{if(properties.containsKey(TYPE))setType(ClientResourceType.valueOf((String) properties.get(TYPE)));}catch(Exception e){}
		try{if(properties.containsKey(DEFAULT))setIsDefault(Integer.parseInt((String) properties.get(DEFAULT))==1);}catch(Exception e){}
		try{if(properties.containsKey(ROW_COUNT))setRowCount(Long.parseLong((String) properties.get(ROW_COUNT)));}catch(Exception e){}
		
	}
	

	public String getOccurrIds() {return get(SOURCE_OCCURRENCE);}

	public void setOccurrIds(String occurrIds) {set(SOURCE_OCCURRENCE,occurrIds);}

	public String getOccurNames() {return get(SOURCE_OCCURRENCE_TABLE);}

	public void setOccurNames(String occurNames) {set(SOURCE_OCCURRENCE_TABLE,occurNames);}

	public Long getRowCount() {	return get(ROW_COUNT);}
	
	public void setRowCount(Long rowCount) {set(ROW_COUNT,rowCount);}
	
	public String getTitle() {	return get(TITLE);}
	
	public void setTitle(String title) {set(TITLE,title);}
	
	public Integer getSearchId() {	return get(SEARCH_ID);}
	
	public void setSearchId(Integer searchId) {	set(SEARCH_ID,searchId);}
	
	public String getTableName() {return get(TABLE_NAME);}
	
	public void setTableName(String tableName) {set(TABLE_NAME,tableName);}
	
	public String getDescription() {return get(DESCRIPTION);}
		
	public void setDescription(String description) {set(DESCRIPTION,description);}
		
	public String getAuthor() {return get(AUTHOR);}
		
	public void setAuthor(String author) {set(AUTHOR,author);}
		
	public String getDisclaimer() {return get(DISCLAIMER);}
		
	public void setDisclaimer(String disclaimer) {set(DISCLAIMER,disclaimer);}
		
	public String getProvenance() {return get(PROVENANCE);}
		
	public void setProvenance(String provenance) {set(PROVENANCE,provenance);}
		
	public Time getTime() {return get(GENERATION_TIME);}
		
	public void setTime(Time time) {set(GENERATION_TIME,time);}
		
	public String getHcafIds() {return get(SOURCE_HCAF);}
		
	public void setHcafIds(String hcafIds) {set(SOURCE_HCAF,hcafIds);}
		
	public String getHspenIds() {return get(SOURCE_HSPEN);}
		
	public void setHspenIds(String hspenIds) {set(SOURCE_HSPEN,hspenIds);}
		
	public String getHspecIds() {return get(SOURCE_HSPEC);}
		
	public void setHspecIds(String hspecIds) {set(SOURCE_HSPEC,hspecIds);}
		
	public String getHcafNames() {return get(SOURCE_HCAF_TABLE);}
		
	public void setHcafNames(String hcafNames) {set(SOURCE_HCAF_TABLE,hcafNames);}
		
	public String getHspenNames() {return get(SOURCE_HSPEN_TABLE);}
		
	public void setHspenNames(String hspenNames) {set(SOURCE_HSPEN_TABLE,hspenNames);}
		
	public String getHspecNames() {return get(SOURCE_HSPEC_TABLE);}
		
	public void setHspecNames(String hspecNames) {set(SOURCE_HSPEC_TABLE,hspecNames);}
		
	public String getParameters() {return get(PARAMETERS);}
		
	public void setParameters(String parameters) {set(PARAMETERS,parameters);}
		
	public String getStatus() {return get(STATUS);}
		
	public void setStatus(String status) {set(STATUS,status);}
		
	public ClientResourceType getType() {return ClientResourceType.valueOf((String)get(TYPE));}
		
	public void setType(ClientResourceType type) {set(TYPE,type+"");}
		
	public AlgorithmType getAlgorithm() {return AlgorithmType.valueOf((String)get(ALGORITHM));}
		
	public void setAlgorithm(AlgorithmType algorithm) {set(ALGORITHM,algorithm+"");}
		
	public Boolean getIsDefault() {return get(DEFAULT);}
		
	public void setIsDefault(Boolean isDefault) {set(DEFAULT,isDefault);}
		
}