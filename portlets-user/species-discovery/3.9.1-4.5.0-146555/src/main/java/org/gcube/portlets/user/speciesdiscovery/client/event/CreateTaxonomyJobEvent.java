/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import org.gcube.portlets.user.speciesdiscovery.shared.LightTaxonomyRow;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class CreateTaxonomyJobEvent extends GwtEvent<CreateTaxonomyJobEventHandler> {
	
	public static final GwtEvent.Type<CreateTaxonomyJobEventHandler> TYPE = new Type<CreateTaxonomyJobEventHandler>();
	private LightTaxonomyRow taxonomy;
	private String dataSourceName;
	public enum TaxonomyJobType {BYCHILDREN, BYIDS};
	private TaxonomyJobType jobType;

	@Override
	public Type<CreateTaxonomyJobEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CreateTaxonomyJobEventHandler handler) {
		handler.onCreateSpeciesJob(this);	
	}
	
	/**
	 * 
	 * @param taxonomy
	 * @param dataSourceName
	 * @param type 
	 * @param rank
	 */
	public CreateTaxonomyJobEvent(LightTaxonomyRow taxonomy, String dataSourceName, TaxonomyJobType jobType) {
		this.taxonomy = taxonomy;
		this.dataSourceName = dataSourceName;
		this.jobType = jobType;
	}
	
	/**
	 * 
	 * @param jobType
	 */
	public CreateTaxonomyJobEvent(TaxonomyJobType jobType){
		this.jobType = jobType;
	}

	public TaxonomyJobType getJobType() {
		return jobType;
	}

	public LightTaxonomyRow getTaxonomy() {
		return taxonomy;
	}

	public void setTaxonomy(LightTaxonomyRow taxonomy) {
		this.taxonomy = taxonomy;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
}
