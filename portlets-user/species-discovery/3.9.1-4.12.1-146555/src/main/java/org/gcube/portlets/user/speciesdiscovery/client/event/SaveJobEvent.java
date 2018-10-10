/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.shared.DataSource;
import org.gcube.portlets.user.speciesdiscovery.shared.JobGisLayerModel;
import org.gcube.portlets.user.speciesdiscovery.shared.JobOccurrencesModel;
import org.gcube.portlets.user.speciesdiscovery.shared.JobTaxonomyModel;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchResultType;

import com.google.gwt.event.shared.GwtEvent;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class SaveJobEvent extends GwtEvent<SaveJobEventHandler> {

	public static final GwtEvent.Type<SaveJobEventHandler> TYPE = new Type<SaveJobEventHandler>();
	private String jobIdentifier;
	private String scientificName;
	private String rank;
	private SearchResultType itemType;
	private List<DataSource> listDataSources;
	private JobTaxonomyModel jobTaxonomyModel;
	private JobOccurrencesModel jobOccurrenceModel;
	private String layerTitle;
	private JobGisLayerModel jobGisLayer;

	@Override
	public Type<SaveJobEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SaveJobEventHandler handler) {
		handler.onSaveJob(this);
	}


	/**
	 *
	 * @param jobIdentifier
	 * @param scientificName
	 * @param listDataSource
	 * @param rank
	 * @param type
	 * @param jobTaxonomyModel
	 */
	public SaveJobEvent(String jobIdentifier, String scientificName, List<DataSource> dataSourceList, String rank, SearchResultType type, JobTaxonomyModel jobTaxonomyModel) {
		this.jobIdentifier = jobIdentifier;
		this.scientificName = scientificName;
		this.listDataSources = dataSourceList;
		this.rank = rank;
		this.itemType = type;
		this.jobTaxonomyModel = jobTaxonomyModel;

	}

	/**
	 *
	 * @param jobIdentifier
	 * @param scientificName
	 * @param dataSourceList
	 * @param type
	 * @param jobOccurrenceModel
	 */
	public SaveJobEvent(String jobIdentifier, String scientificName, List<DataSource> dataSourceList, SearchResultType type, JobOccurrencesModel jobOccurrenceModel) {
		this.jobIdentifier = jobIdentifier;
		this.scientificName = scientificName;
		this.listDataSources = dataSourceList;
		this.itemType = type;
		this.jobOccurrenceModel = jobOccurrenceModel;
	}



	/**
	 *
	 * @param jobIdentifier
	 * @param scientificName
	 * @param dataSourceList
	 * @param type
	 * @param jobOccurrenceModel
	 */
	public SaveJobEvent(String jobIdentifier, String layerTitle, SearchResultType type, JobGisLayerModel jobGisLayer) {
		this.jobIdentifier = jobIdentifier;
		this.itemType = type;
		this.layerTitle = layerTitle;
		this.itemType = type;
		this.jobGisLayer = jobGisLayer;
	}




	/**
	 * @return the jobGisLayer
	 */
	public JobGisLayerModel getJobGisLayer() {

		return jobGisLayer;
	}

	public String getJobIdentifier() {
		return jobIdentifier;
	}

	public void setJobIdentifier(String jobIdentifier) {
		this.jobIdentifier = jobIdentifier;
	}

	public String getScientificName() {
		return scientificName;
	}

	public String getRank() {
		return rank;
	}

	public SearchResultType getItemType() {
		return itemType;
	}

	public JobTaxonomyModel getJobTaxonomyModel() {
		return jobTaxonomyModel;
	}

	public JobOccurrencesModel getJobOccurrenceModel() {
		return jobOccurrenceModel;
	}

	public void setJobTaxonomyModel(JobTaxonomyModel jobTaxonomyModel) {
		this.jobTaxonomyModel = jobTaxonomyModel;
	}

	public void setJobOccurrenceModel(JobOccurrencesModel jobOccurrenceModel) {
		this.jobOccurrenceModel = jobOccurrenceModel;
	}

	public List<DataSource> getListDataSources() {
		return listDataSources;
	}

	public void setListDataSources(List<DataSource> listDataSources) {
		this.listDataSources = listDataSources;
	}

}
