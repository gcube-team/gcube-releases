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
 * The Class SaveJobErrorEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 */
public class SaveJobErrorEvent extends GwtEvent<SaveJobErrorEventHandler> {

	public static final GwtEvent.Type<SaveJobErrorEventHandler> TYPE = new Type<SaveJobErrorEventHandler>();
	private String jobIdentifier;
	private String scientificName;
	private String rank;
	private SearchResultType itemType;
	private List<DataSource> listDataSources;
	private JobTaxonomyModel jobTaxonomyModel;
	private JobOccurrencesModel jobOccurrenceModel;
	private String layerTitle;
	private JobGisLayerModel jobGisLayer;

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<SaveJobErrorEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(SaveJobErrorEventHandler handler) {
		handler.onSaveJobError(this);
	}


	/**
	 * Instantiates a new save job error event.
	 *
	 * @param jobIdentifier the job identifier
	 * @param scientificName the scientific name
	 * @param dataSourceList the data source list
	 * @param rank the rank
	 * @param type the type
	 * @param jobTaxonomyModel the job taxonomy model
	 */
	public SaveJobErrorEvent(String jobIdentifier, String scientificName, List<DataSource> dataSourceList, String rank, SearchResultType type, JobTaxonomyModel jobTaxonomyModel) {
		this.jobIdentifier = jobIdentifier;
		this.scientificName = scientificName;
		this.listDataSources = dataSourceList;
		this.rank = rank;
		this.itemType = type;
		this.jobTaxonomyModel = jobTaxonomyModel;

	}


	/**
	 * Instantiates a new save job error event.
	 *
	 * @param jobIdentifier the job identifier
	 * @param layerTitle the layer title
	 * @param jobGisLayer the job gis layer
	 * @param type the type
	 */
	public SaveJobErrorEvent(String jobIdentifier, String layerTitle, JobGisLayerModel jobGisLayer, SearchResultType type) {
		this.jobIdentifier = jobIdentifier;
		this.layerTitle = layerTitle;
		this.jobGisLayer = jobGisLayer;
		this.itemType = type;
	}

	/**
	 * Instantiates a new save job error event.
	 *
	 * @param jobIdentifier the job identifier
	 * @param scientificName the scientific name
	 * @param dataSourceList the data source list
	 * @param type the type
	 * @param jobOccurrenceModel the job occurrence model
	 */
	public SaveJobErrorEvent(String jobIdentifier, String scientificName, List<DataSource> dataSourceList, SearchResultType type, JobOccurrencesModel jobOccurrenceModel) {
		this.jobIdentifier = jobIdentifier;
		this.scientificName = scientificName;
		this.listDataSources = dataSourceList;
		this.itemType = type;
		this.jobOccurrenceModel = jobOccurrenceModel;
	}


	/**
	 * Gets the job identifier.
	 *
	 * @return the job identifier
	 */
	public String getJobIdentifier() {
		return jobIdentifier;
	}

	/**
	 * Sets the job identifier.
	 *
	 * @param jobIdentifier the new job identifier
	 */
	public void setJobIdentifier(String jobIdentifier) {
		this.jobIdentifier = jobIdentifier;
	}

	/**
	 * Gets the scientific name.
	 *
	 * @return the scientific name
	 */
	public String getScientificName() {
		return scientificName;
	}

	/**
	 * Gets the rank.
	 *
	 * @return the rank
	 */
	public String getRank() {
		return rank;
	}

	/**
	 * Gets the item type.
	 *
	 * @return the item type
	 */
	public SearchResultType getItemType() {
		return itemType;
	}

	/**
	 * Gets the job taxonomy model.
	 *
	 * @return the job taxonomy model
	 */
	public JobTaxonomyModel getJobTaxonomyModel() {
		return jobTaxonomyModel;
	}

	/**
	 * Gets the job occurrence model.
	 *
	 * @return the job occurrence model
	 */
	public JobOccurrencesModel getJobOccurrenceModel() {
		return jobOccurrenceModel;
	}

	/**
	 * Sets the job taxonomy model.
	 *
	 * @param jobTaxonomyModel the new job taxonomy model
	 */
	public void setJobTaxonomyModel(JobTaxonomyModel jobTaxonomyModel) {
		this.jobTaxonomyModel = jobTaxonomyModel;
	}

	/**
	 * Sets the job occurrence model.
	 *
	 * @param jobOccurrenceModel the new job occurrence model
	 */
	public void setJobOccurrenceModel(JobOccurrencesModel jobOccurrenceModel) {
		this.jobOccurrenceModel = jobOccurrenceModel;
	}

	/**
	 * Gets the list data sources.
	 *
	 * @return the list data sources
	 */
	public List<DataSource> getListDataSources() {
		return listDataSources;
	}

	/**
	 * Sets the list data sources.
	 *
	 * @param listDataSources the new list data sources
	 */
	public void setListDataSources(List<DataSource> listDataSources) {
		this.listDataSources = listDataSources;
	}

}
