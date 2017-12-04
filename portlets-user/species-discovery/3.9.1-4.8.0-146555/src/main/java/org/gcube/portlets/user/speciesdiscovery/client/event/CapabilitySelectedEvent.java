/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;

import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class CapabilitySelectedEvent extends GwtEvent<CapabilitySelectedEventHandler> {
	
	public static final GwtEvent.Type<CapabilitySelectedEventHandler> TYPE = new Type<CapabilitySelectedEventHandler>();
	
	private SpeciesCapability capability;

	public CapabilitySelectedEvent(SpeciesCapability capability) {
		this.capability = capability;
	}

	@Override
	public Type<CapabilitySelectedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CapabilitySelectedEventHandler handler) {
		handler.onCapabilitySelected(this);	
	}

	public SpeciesCapability getCapability() {
		return capability;
	}

	public void setCapability(SpeciesCapability capability) {
		this.capability = capability;
	}
}
