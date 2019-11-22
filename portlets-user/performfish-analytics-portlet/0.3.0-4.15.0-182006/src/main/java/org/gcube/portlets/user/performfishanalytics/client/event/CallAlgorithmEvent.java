package org.gcube.portlets.user.performfishanalytics.client.event;

import java.util.List;

import org.gcube.portlets.user.performfishanalytics.client.DataMinerAlgorithms;
import org.gcube.portlets.user.performfishanalytics.shared.KPI;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class CallAlgorithmEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Mar 5, 2019
 */
public class CallAlgorithmEvent extends GwtEvent<CallAlgorithmEventHandler> {
	public static Type<CallAlgorithmEventHandler> TYPE = new Type<CallAlgorithmEventHandler>();
	private DataMinerAlgorithms algorithm;
	private String focusID;
	private List<KPI> inputKPI;
	private List<KPI> outputKPI;


	/**
	 * Instantiates a new call algorithm event.
	 *
	 * @param algorithm the algorithm
	 * @param focusID the focus id
	 * @param inputKPI the input kpi
	 * @param outputKPI the output kpi
	 */
	public CallAlgorithmEvent(DataMinerAlgorithms algorithm, String focusID, List<KPI> inputKPI, final List<KPI> outputKPI) {
		this.algorithm = algorithm;
		this.focusID = focusID;
		this.inputKPI = inputKPI;
		this.outputKPI = outputKPI;

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<CallAlgorithmEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(CallAlgorithmEventHandler handler) {
		handler.onCall(this);
	}


	/**
	 * @return the algorithm
	 */
	public DataMinerAlgorithms getAlgorithm() {

		return algorithm;
	}


	/**
	 * @return the focusID
	 */
	public String getFocusID() {

		return focusID;
	}


	/**
	 * @return the inputKPI
	 */
	public List<KPI> getInputKPI() {

		return inputKPI;
	}


	/**
	 * @return the outputKPI
	 */
	public List<KPI> getOutputKPI() {

		return outputKPI;
	}




}
