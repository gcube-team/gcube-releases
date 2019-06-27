package org.gcube.portlets.user.performfishanalytics.client.event;

import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class SelectedPopulationTypeEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 18, 2019
 */
public class SelectedPopulationTypeEvent extends GwtEvent<SelectedPopulationTypeEventHandler> {
	public static Type<SelectedPopulationTypeEventHandler> TYPE = new Type<SelectedPopulationTypeEventHandler>();
	private PopulationType selectedPopulationType;


	/**
	 * Instantiates a new selected population type event.
	 *
	 * @param selectedPopulationType the selected population type
	 */
	public SelectedPopulationTypeEvent(PopulationType selectedPopulationType) {
		this.selectedPopulationType = selectedPopulationType;

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<SelectedPopulationTypeEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(SelectedPopulationTypeEventHandler handler) {
		handler.onSelectedPopulationType(this);
	}


	/**
	 * Gets the selected population type.
	 *
	 * @return the selectedPopulationType
	 */
	public PopulationType getSelectedPopulationType() {

		return selectedPopulationType;
	}
}
