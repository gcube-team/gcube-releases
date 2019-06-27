package org.gcube.portlets.user.performfishanalytics.client.event;

import org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishInitParameter;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class LoadPopulationTypeEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 22, 2019
 */
public class LoadPopulationTypeEvent extends GwtEvent<LoadPopulationTypeEventHandler> {
	public static Type<LoadPopulationTypeEventHandler> TYPE = new Type<LoadPopulationTypeEventHandler>();
	private String populationName;
	private PerformFishInitParameter initParameters;

	/**
	 * Instantiates a new load population type event.
	 *
	 * @param populationName the population name
	 * @param result
	 */
	public LoadPopulationTypeEvent(String populationName, PerformFishInitParameter result) {

		if(populationName==null)
			this.populationName = "BATCH";

		this.populationName = populationName;
		this.initParameters = result;

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<LoadPopulationTypeEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(LoadPopulationTypeEventHandler handler) {
		handler.onLoadPopulationType(this);
	}


	/**
	 * @return the initParameters
	 */
	public PerformFishInitParameter getInitParameters() {

		return initParameters;
	}

	/**
	 * Gets the population name.
	 *
	 * @return the populationName
	 */
	public String getPopulationName() {

		return populationName;
	}
}
