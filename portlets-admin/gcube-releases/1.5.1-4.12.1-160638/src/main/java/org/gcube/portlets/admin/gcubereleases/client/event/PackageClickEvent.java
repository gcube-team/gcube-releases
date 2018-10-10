package org.gcube.portlets.admin.gcubereleases.client.event;

import org.gcube.portlets.admin.gcubereleases.shared.AccoutingReference;
import org.gcube.portlets.admin.gcubereleases.shared.Package;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class ReloadReleasesEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class PackageClickEvent extends GwtEvent<PackageClickEventHandler> {
	public static Type<PackageClickEventHandler> TYPE = new Type<PackageClickEventHandler>();
	
	private AccoutingReference accoutingReference;
	private Package pck;

	/**
	 * Gets the pck.
	 *
	 * @return the pck
	 */
	public Package getPck() {
		return pck;
	}

	/**
	 * Sets the pck.
	 *
	 * @param pck the pck to set
	 */
	public void setPck(Package pck) {
		this.pck = pck;
	}

	/**
	 * Instantiates a new reload releases event.
	 *
	 * @param pck the pck
	 * @param reference the reference
	 */
	public PackageClickEvent(Package pck, AccoutingReference reference) {
		this.accoutingReference = reference;
		this.pck = pck;
		
	}

	/**
	 * Gets the accouting reference.
	 *
	 * @return the accoutingReference
	 */
	public AccoutingReference getAccoutingReference() {
		return accoutingReference;
	}

	/**
	 * Sets the accouting reference.
	 *
	 * @param accoutingReference the accoutingReference to set
	 */
	public void setAccoutingReference(AccoutingReference accoutingReference) {
		this.accoutingReference = accoutingReference;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<PackageClickEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(PackageClickEventHandler handler) {
		handler.onClickEvent(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PackageClickEvent [accoutingReference=");
		builder.append(accoutingReference);
		builder.append(", pck=");
		builder.append(pck);
		builder.append("]");
		return builder.toString();
	}
}
