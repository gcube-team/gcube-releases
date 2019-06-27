package org.gcube.portlets.user.performfishanalytics.client.event;

import org.gcube.portlets.user.performfishanalytics.shared.KPI;
import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class SelectedKPIEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 22, 2019
 */
public class SelectedKPIEvent extends GwtEvent<SelectedKPIEventHandler> {
	public static Type<SelectedKPIEventHandler> TYPE = new Type<SelectedKPIEventHandler>();
	private PopulationType selectedPopulationType;
	private KPI kpi;
	private boolean checked;


	/**
	 * Instantiates a new selected population type event.
	 *
	 * @param selectedPopulationType the selected population type
	 * @param theKPI the the kpi
	 * @param checked the checked
	 */
	public SelectedKPIEvent(PopulationType selectedPopulationType, KPI theKPI, boolean checked) {
		this.selectedPopulationType = selectedPopulationType;
		this.kpi = theKPI;
		this.checked = checked;

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<SelectedKPIEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(SelectedKPIEventHandler handler) {
		handler.onSelectedKPI(this);
	}


	/**
	 * Gets the kpi.
	 *
	 * @return the kpi
	 */
	public KPI getKpi() {

		return kpi;
	}


	/**
	 * Gets the selected population type.
	 *
	 * @return the selectedPopulationType
	 */
	public PopulationType getSelectedPopulationType() {

		return selectedPopulationType;
	}


	/**
	 * Checks if is checked.
	 *
	 * @return the checked
	 */
	public boolean isChecked() {

		return checked;
	}
}
