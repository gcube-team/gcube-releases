/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchType;
import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;
import org.gcube.portlets.user.speciesdiscovery.shared.filter.ResultFilter;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 17, 2013
 *
 */
public class SearchEvent extends GwtEvent<SearchEventHandler> {
	
	public static final GwtEvent.Type<SearchEventHandler> TYPE = new Type<SearchEventHandler>();

	@Override
	public Type<SearchEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SearchEventHandler handler) {
		handler.onSearch(this);	
	}
	
	protected String searchTerm; //USED FROM SEARCH BY GUI - SIMPLE QUERY
	protected SearchType type;
	protected Number upperBoundLongitude; 
	protected Number upperBoundLatitude; 
	protected Number lowerBoundLongitude; 
	protected Number lowerBoundLatitude; 
	protected Date fromDate; 
	protected Date toDate;
	private List<DataSourceModel> lstDataSources;
	private String groupByRank;
	private SpeciesCapability resultType;
	private String query;
	private ResultFilter activeFilterOnResult;
	private Map<SearchType, List<String>> mapTermsSearched; //USED FOR SEARCH BY QUERY - TEXT QUERY
	
	private List<DataSourceModel> listDataSourcesForSynonyms; //USED FOR EXTEND
	private List<DataSourceModel> listDataSourcesForUnfold; //USED FOR UNFOLD
	/**
	 * 
	 * @param type
	 * @param searchTerm
	 * @param upperBoundLongitude
	 * @param upperBoundLatitude
	 * @param lowerBoundLongitude
	 * @param lowerBoundLatitude
	 * @param fromDate
	 * @param toDate
	 * @param listDataSources
	 * @param rank
	 * @param resultType
	 * @param listDataSourcesForSynonyms
	 */
	public SearchEvent(SearchType type, String searchTerm, Number upperBoundLongitude, Number upperBoundLatitude, Number lowerBoundLongitude, Number lowerBoundLatitude,
			Date fromDate, Date toDate, List<DataSourceModel> listDataSources, String rank, SpeciesCapability resultType,List<DataSourceModel> listDataSourcesForSynonyms, List<DataSourceModel> listDataSourcesForUnfold) {
		this.type = type;
		this.searchTerm = searchTerm;
		this.upperBoundLongitude = upperBoundLongitude;
		this.upperBoundLatitude = upperBoundLatitude;
		this.lowerBoundLongitude = lowerBoundLongitude;
		this.lowerBoundLatitude = lowerBoundLatitude;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.lstDataSources = listDataSources;
		this.groupByRank = rank;
		this.resultType = resultType;
		this.listDataSourcesForSynonyms = listDataSourcesForSynonyms;
		this.listDataSourcesForUnfold = listDataSourcesForUnfold;
	}

	public SearchEvent(SearchType byQuery, String query) {
		this.type = byQuery;
		this.query = query;
	}

	/**
	 * @return the type
	 */
	//USED BY SIMPLE QUERY
	public SearchType getType() {
		return type;
	}

	/**
	 * @return the searchTerm
	 */
	public String getSearchTerm() {
		return searchTerm;
	}

	/**
	 * @return the upperBoundLongitude
	 */
	public Number getUpperBoundLongitude() {
		return upperBoundLongitude;
	}

	/**
	 * @return the upperBoundLatitude
	 */
	public Number getUpperBoundLatitude() {
		return upperBoundLatitude;
	}

	/**
	 * @return the lowerBoundLongitude
	 */
	public Number getLowerBoundLongitude() {
		return lowerBoundLongitude;
	}

	/**
	 * @return the lowerBoundLatitude
	 */
	public Number getLowerBoundLatitude() {
		return lowerBoundLatitude;
	}

	/**
	 * @return the fromDate
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * @return the toDate
	 */
	public Date getToDate() {
		return toDate;
	}

	public List<DataSourceModel> getLstDataSources() {
		return lstDataSources;
	}

	public String getGroupByRank() {
		return groupByRank;
	}

	public SpeciesCapability getResultType() {
		return resultType;
	}

	public String getQuery() {
		return query;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public void setType(SearchType type) {
		this.type = type;
	}

	public void setUpperBoundLongitude(Number upperBoundLongitude) {
		this.upperBoundLongitude = upperBoundLongitude;
	}

	public void setUpperBoundLatitude(Number upperBoundLatitude) {
		this.upperBoundLatitude = upperBoundLatitude;
	}

	public void setLowerBoundLongitude(Number lowerBoundLongitude) {
		this.lowerBoundLongitude = lowerBoundLongitude;
	}

	public void setLowerBoundLatitude(Number lowerBoundLatitude) {
		this.lowerBoundLatitude = lowerBoundLatitude;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public void setLstDataSources(List<DataSourceModel> lstDataSources) {
		this.lstDataSources = lstDataSources;
	}

	public void setGroupByRank(String groupByRank) {
		this.groupByRank = groupByRank;
	}

	public void setResultType(SpeciesCapability resultType) {
		this.resultType = resultType;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setActiveFilterOnResult(ResultFilter activeFilterOnResult) {
		this.activeFilterOnResult = activeFilterOnResult;
	}
	
	//USED BY TEXT QUERY
	public Map<SearchType, List<String>> getMapTermsSearched() {
		return mapTermsSearched;
	}

	//USED BY TEXT QUERY
	public void setMapTermsSearched(Map<SearchType, List<String>> mapTermsSearched) {
		this.mapTermsSearched = mapTermsSearched;
	}

	public List<DataSourceModel> getListDataSourcesForSynonyms() {
		return listDataSourcesForSynonyms;
	}

	public void setListDataSourcesForSynonyms(
			List<DataSourceModel> listDataSourcesForSynonyms) {
		this.listDataSourcesForSynonyms = listDataSourcesForSynonyms;
	}


	public List<DataSourceModel> getListDataSourcesForUnfold() {
		return listDataSourcesForUnfold;
	}

	public void setListDataSourcesForUnfold(
			List<DataSourceModel> listDataSourcesForUnfold) {
		this.listDataSourcesForUnfold = listDataSourcesForUnfold;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SearchEvent [searchTerm=");
		builder.append(searchTerm);
		builder.append(", type=");
		builder.append(type);
		builder.append(", upperBoundLongitude=");
		builder.append(upperBoundLongitude);
		builder.append(", upperBoundLatitude=");
		builder.append(upperBoundLatitude);
		builder.append(", lowerBoundLongitude=");
		builder.append(lowerBoundLongitude);
		builder.append(", lowerBoundLatitude=");
		builder.append(lowerBoundLatitude);
		builder.append(", fromDate=");
		builder.append(fromDate);
		builder.append(", toDate=");
		builder.append(toDate);
		builder.append(", lstDataSources=");
		builder.append(lstDataSources);
		builder.append(", groupByRank=");
		builder.append(groupByRank);
		builder.append(", resultType=");
		builder.append(resultType);
		builder.append(", query=");
		builder.append(query);
		builder.append(", activeFilterOnResult=");
		builder.append(activeFilterOnResult);
		builder.append(", mapTermsSearched=");
		builder.append(mapTermsSearched);
		builder.append(", listDataSourcesForSynonyms=");
		builder.append(listDataSourcesForSynonyms);
		builder.append(", listDataSourcesForUnfold=");
		builder.append(listDataSourcesForUnfold);
		builder.append("]");
		return builder.toString();
	}
}
