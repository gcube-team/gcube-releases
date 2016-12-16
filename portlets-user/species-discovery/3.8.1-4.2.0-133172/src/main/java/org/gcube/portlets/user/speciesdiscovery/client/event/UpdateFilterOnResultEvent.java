/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import org.gcube.portlets.user.speciesdiscovery.client.util.SpeciesGridFields;
import org.gcube.portlets.user.speciesdiscovery.shared.filter.ResultFilter;

import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class UpdateFilterOnResultEvent extends GwtEvent<UpdateFilterOnResultEventHandler> {
	
	public static final GwtEvent.Type<UpdateFilterOnResultEventHandler> TYPE = new Type<UpdateFilterOnResultEventHandler>();
	private ResultFilter activeFilterObject;
	private SpeciesGridFields updateFilterId;

	public SpeciesGridFields getUpdateFilterId() {
		return updateFilterId;
	}

	@Override
	public Type<UpdateFilterOnResultEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UpdateFilterOnResultEventHandler handler) {
		handler.onUpdateFilter(this);	
	}
	
	public UpdateFilterOnResultEvent(SpeciesGridFields filterId) {
		this.updateFilterId = filterId;
	}
}
