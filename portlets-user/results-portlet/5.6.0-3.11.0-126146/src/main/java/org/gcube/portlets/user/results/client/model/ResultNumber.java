package org.gcube.portlets.user.results.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ResultNumber implements IsSerializable{

	private boolean countEnded;
	private int resultsNoSofar;
	/**
	 * this var contains the extra text to display such as ... and counting or .. more than ...
	 */
	private String extraText;
	
	public ResultNumber() {
		super();
	}

	public ResultNumber(boolean countEnded, int resultsNoSofar, String extraText) {
		super();
		this.countEnded = countEnded;
		this.extraText = extraText;
		this.resultsNoSofar = resultsNoSofar;
	}

	public boolean isCountEnded() {
		return countEnded;
	}

	public void setCountEnded(boolean countEnded) {
		this.countEnded = countEnded;
	}

	public int getResultsNoSofar() {
		return resultsNoSofar;
	}

	public void setResultsNoSofar(int resultsNoSofar) {
		this.resultsNoSofar = resultsNoSofar;
	}

	public String getExtraText() {
		return extraText;
	}

	public void setExtraText(String extraText) {
		this.extraText = extraText;
	}
	
	
}
