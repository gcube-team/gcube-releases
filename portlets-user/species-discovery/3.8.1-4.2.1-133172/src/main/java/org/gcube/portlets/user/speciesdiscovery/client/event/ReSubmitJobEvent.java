/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import org.gcube.portlets.user.speciesdiscovery.shared.JobOccurrencesModel;
import org.gcube.portlets.user.speciesdiscovery.shared.JobTaxonomyModel;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchResultType;

import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ReSubmitJobEvent extends GwtEvent<ReSubmitJobEventHandler> {
	
	public static final GwtEvent.Type<ReSubmitJobEventHandler> TYPE = new Type<ReSubmitJobEventHandler>();

	private SearchResultType loadType;

	private JobOccurrencesModel jobOccurrenceModel;

	private JobTaxonomyModel jobTaxonomyModel;
	
	@Override
	public Type<ReSubmitJobEventHandler> getAssociatedType() {
		return TYPE;
	}

	public ReSubmitJobEvent(SearchResultType loadType, JobOccurrencesModel jobOccurrenceModel, JobTaxonomyModel jobTaxonomyModel){
		this.loadType = loadType;
		this.jobOccurrenceModel = jobOccurrenceModel;
		this.jobTaxonomyModel = jobTaxonomyModel;
	}

	public SearchResultType getLoadType() {
		return loadType;
	}

	@Override
	protected void dispatch(ReSubmitJobEventHandler handler) {
		handler.onResubmitJob(this);
		
	}

	public JobOccurrencesModel getJobOccurrenceModel() {
		return jobOccurrenceModel;
	}

	public JobTaxonomyModel getJobTaxonomyModel() {
		return jobTaxonomyModel;
	}
	
}
