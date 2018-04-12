package org.gcube.application.datamanagementfacilityportlet.client.rpc.data;

import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.AlgorithmType;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientResourceType;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ClientResource implements IsSerializable{

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


	private String title;
	private Integer searchId;
	private String tableName;
	private String description;
	private String author;
	private String disclaimer; 
	private String provenance;
	private String date; 
	private String hcafId;
	private String hspenId;
	private String hspecId;
	private String occurrId;
	private String hcafName;
	private String hspenName;
	private String hspecName;
	private String parameters;
	private String occurName;
	private String status; 
	private ClientResourceType type;
	private AlgorithmType algorithm;
	private Boolean isDefault;
	private Long rowCount;



	public ClientResource() {
		// TODO Auto-generated constructor stub
	}







	public ClientResource(String title, Integer searchId, String tableName,
			String description, String author, String disclaimer,
			String provenance, String date, String hcafId, String hspenId,
			String hspecId, String occurrId, String hcafName,
			String hspenName, String hspecName, String parameters,
			String occurName, String status, ClientResourceType type,
			AlgorithmType algorithm, Boolean isDefault,Long rowCount) {
		super();
		this.title = title;
		this.searchId = searchId;
		this.tableName = tableName;
		this.description = description;
		this.author = author;
		this.disclaimer = disclaimer;
		this.provenance = provenance;
		this.date = date;
		this.hcafId = hcafId;
		this.hspenId = hspenId;
		this.hspecId = hspecId;
		this.occurrId = occurrId;
		this.hcafName = hcafName;
		this.hspenName = hspenName;
		this.hspecName = hspecName;
		this.parameters = parameters;
		this.occurName = occurName;
		this.status = status;
		this.type = type;
		this.algorithm = algorithm;
		this.isDefault = isDefault;
		this.rowCount=rowCount;
	}







	public String getOccurrId() {
		return occurrId;
	}

	public void setOccurrId(String occurrId) {
		this.occurrId = occurrId;
	}


	public String getOccurName() {
		return occurName;
	}







	public void setOccurName(String occurName) {
		this.occurName = occurName;
	}



	public Long getRowCount() {
		return rowCount;
	}
	public void setRowCount(Long rowCount) {
		this.rowCount = rowCount;
	}


	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getSearchId() {
		return searchId;
	}
	public void setSearchId(Integer searchId) {
		this.searchId = searchId;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDisclaimer() {
		return disclaimer;
	}
	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}
	public String getProvenance() {
		return provenance;
	}
	public void setProvenance(String provenance) {
		this.provenance = provenance;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getHcafId() {
		return hcafId;
	}
	public void setHcafId(String hcafId) {
		this.hcafId = hcafId;
	}
	public String getHspenId() {
		return hspenId;
	}
	public void setHspenId(String hspenId) {
		this.hspenId = hspenId;
	}
	public String getHspecId() {
		return hspecId;
	}
	public void setHspecId(String hspecId) {
		this.hspecId = hspecId;
	}
	public String getHcafName() {
		return hcafName;
	}
	public void setHcafName(String hcafName) {
		this.hcafName = hcafName;
	}
	public String getHspenName() {
		return hspenName;
	}
	public void setHspenName(String hspenName) {
		this.hspenName = hspenName;
	}
	public String getHspecName() {
		return hspecName;
	}
	public void setHspecName(String hspecName) {
		this.hspecName = hspecName;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ClientResourceType getType() {
		return type;
	}
	public void setType(ClientResourceType type) {
		this.type = type;
	}
	public AlgorithmType getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(AlgorithmType algorithm) {
		this.algorithm = algorithm;
	}
	public Boolean getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}





}
