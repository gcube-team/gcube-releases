/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class SearchFilters implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -608272336048083389L;
	
	protected Coordinate upperBound;
	protected Coordinate lowerBound;
	protected Date fromDate;
	protected Date toDate;
	private List<DataSourceModel> listDataSources;
	private String classificationGroupByRank;
	private SpeciesCapability resultType;
	
	private List<DataSourceModel> listDataSourcesForSynonyms;

	private List<DataSourceModel> listDataSourcesForUnfold;

	
	public SearchFilters(){};
	

	/**
	 * 
	 * @param upperBound
	 * @param lowerBound
	 * @param fromDate
	 * @param toDate
	 * @param listDataSources
	 * @param classificationGroupRank
	 * @param resultType
	 * @param listDataSourcesForSynonyms
	 */
	public SearchFilters(Coordinate upperBound, Coordinate lowerBound, Date fromDate, Date toDate, List<DataSourceModel> listDataSources, String classificationGroupRank, SpeciesCapability resultType, List<DataSourceModel> listDataSourcesForSynonyms, List<DataSourceModel> listDataSourcesForUnfold) {
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.listDataSources = listDataSources;
		this.classificationGroupByRank = classificationGroupRank;
		this.resultType = resultType;
		this.listDataSourcesForSynonyms = listDataSourcesForSynonyms;
		this.listDataSourcesForUnfold = listDataSourcesForUnfold;
	}

	/**
	 * @return the upperBound
	 */
	public Coordinate getUpperBound() {
		return upperBound;
	}

	/**
	 * @param upperBound the upperBound to set
	 */
	public void setUpperBound(Coordinate upperBound) {
		this.upperBound = upperBound;
	}

	/**
	 * @return the lowerBound
	 */
	public Coordinate getLowerBound() {
		return lowerBound;
	}

	/**
	 * @param lowerBound the lowerBound to set
	 */
	public void setLowerBound(Coordinate lowerBound) {
		this.lowerBound = lowerBound;
	}

	/**
	 * @return the fromDate
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return the toDate
	 */
	public Date getToDate() {
		return toDate;
	}

	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}


	public List<DataSourceModel> getListDataSources() {
		return listDataSources;
	}

	public String getClassificationGroupByRank() {
		return classificationGroupByRank;
	}

	public SpeciesCapability getResultType() {
		return resultType;
	}

	public void setResultType(SpeciesCapability resultType) {
		this.resultType = resultType;
	}

	public void setListDataSources(List<DataSourceModel> listDataSources) {
		this.listDataSources = listDataSources;
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
		builder.append("SearchFilters [upperBound=");
		builder.append(upperBound);
		builder.append(", lowerBound=");
		builder.append(lowerBound);
		builder.append(", fromDate=");
		builder.append(fromDate);
		builder.append(", toDate=");
		builder.append(toDate);
		builder.append(", listDataSources=");
		builder.append(listDataSources);
		builder.append(", classificationGroupByRank=");
		builder.append(classificationGroupByRank);
		builder.append(", resultType=");
		builder.append(resultType);
		builder.append(", listDataSourcesForSynonyms=");
		builder.append(listDataSourcesForSynonyms);
		builder.append(", listDataSourcesForUnfold=");
		builder.append(listDataSourcesForUnfold);
		builder.append("]");
		return builder.toString();
	}
	
}
