/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared.filter;

import java.io.Serializable;


/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ResultFilter implements FilterCriteria, Serializable  {

	private static final long serialVersionUID = -1263032818979002405L;
	
	private boolean isByClassification;
	private String classificationId;
	private String rankClassification;
	
	private boolean isByDataProvider;
	private String dataProviderName;

	private boolean isByDataSourceName;
	private String dataSourceName;

	private boolean isByRank;
	private String rankName;
	
	private int numberOfData;
	
	private String filterValue;
	
	private boolean loadCommonName;
	
	private boolean activeFilters;

	private boolean loadAllProperties;
	
	/**
	 * default behavior activeFilters is true, loadCommonName = false
	 */
	public ResultFilter(){
		super();
		this.activeFilters = true;
		this.loadCommonName = false;
		this.loadAllProperties = false;
		initFilter();
	}
	
	/**
	 * 
	 * @param activeFilters
	 * @param loadAllCommonName
	 */
	public ResultFilter(boolean activeFilters, boolean loadAllCommonName, boolean loadAllProperties) {
		this.loadCommonName = loadAllCommonName;
		this.loadAllProperties = loadAllProperties;
		this.activeFilters = activeFilters;
		initFilter();
	}

	
	private void initFilter(){
		filterValue = "";
		isByClassification = false;
		isByDataProvider = false;
		isByDataSourceName = false;
		isByRank = false;
		
		dataProviderName = null;
		classificationId = null;
		rankClassification = null;
		rankName = null;
		dataSourceName = null;
		numberOfData = 0;
		filterValue = null;

//		listByClassification = null;
	}
	
	public boolean isByClassification() {
		return isByClassification;
	}

	public void setByClassification(boolean isByClassification) {
		this.isByClassification = isByClassification;
	}
	
	public void setClassification(String rank, String classificationId, int numberOfData) {
		this.rankClassification = rank;
		this.classificationId = classificationId;
		this.numberOfData = numberOfData;
	}

	public boolean isByDataProvider() {
		return isByDataProvider;
	}
	
	public boolean isByDataSourceName() {
		return isByDataSourceName ;
	}

	public void setByDataProvider(boolean isByDataProvider) {
		this.isByDataProvider = isByDataProvider;
	}
	
	public String getDataProviderName() {
		return dataProviderName;
	}

	public void setDataProviderName(String dataProviderName, int numberOfData) {
		this.dataProviderName = dataProviderName;
		this.numberOfData = numberOfData;
	}
	
	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName, int numberOfData) {
		this.dataSourceName = dataSourceName;
		this.numberOfData = numberOfData;
	}
	
	public void resetAllFilters(){
		initFilter();
	}
	
	public int getNumberOfData(){
		return numberOfData;
	}

	public void setByDataSourceName(boolean isByDataSourceName) {
		this.isByDataSourceName = isByDataSourceName;
	}

	public void setByRankFilter(boolean b) {
		this.isByRank = b;
	}

	public String getRankName() {
		return rankName;
	}

	public void setRankName(String rankName, int numberOfData) {
		this.rankName = rankName;
		this.numberOfData = numberOfData;
	}

	public boolean isByRank() {
		return isByRank;
	}

	public String getFilterValue() {
		return filterValue;
	}

	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}

	public String getClassificationId() {
		return classificationId;
	}

	public String getRankClassification() {
		return rankClassification;
	}

	public void setRankClassification(String rankClassification) {
		this.rankClassification = rankClassification;
	}

	public boolean isLoadCommonName() {
		return loadCommonName;
	}

	public boolean isActiveFilters() {
		return activeFilters;
	}

	public boolean isLoadAllProperties() {
		return loadAllProperties;
	}
	
}
