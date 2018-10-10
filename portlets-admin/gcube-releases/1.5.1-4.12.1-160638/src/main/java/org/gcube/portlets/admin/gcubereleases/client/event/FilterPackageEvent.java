package org.gcube.portlets.admin.gcubereleases.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class FilterPackageEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class FilterPackageEvent extends GwtEvent<FilterPackageEventHandler> {
	public static Type<FilterPackageEventHandler> TYPE = new Type<FilterPackageEventHandler>();
	private String value;


	/**
	 * Instantiates a new filter package event.
	 *
	 * @param value the value
	 */
	public FilterPackageEvent(String value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<FilterPackageEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(FilterPackageEventHandler handler) {
		handler.onFilterPackage(this);
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
