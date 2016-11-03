package org.gcube.portlets.user.td.gwtservice.shared.csv;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class CheckCSVSession implements Serializable {

	private static final long serialVersionUID = 7775024048559018931L;

	protected ArrayList<CSVRowError> csvRowErrorList;
	protected boolean csvFileUpperMaxSizeCheck;

	public CheckCSVSession() {

	}

	public CheckCSVSession(ArrayList<CSVRowError> csvRowErrorList,
			boolean csvFileUpperMaxSizeCheck) {
		this.csvFileUpperMaxSizeCheck = csvFileUpperMaxSizeCheck;
		this.csvRowErrorList = csvRowErrorList;
	}

	public ArrayList<CSVRowError> getCsvRowErrorList() {
		return csvRowErrorList;
	}

	public void setCsvRowErrorList(ArrayList<CSVRowError> csvRowErrorList) {
		this.csvRowErrorList = csvRowErrorList;
	}

	public boolean isCsvFileUpperMaxSizeCheck() {
		return csvFileUpperMaxSizeCheck;
	}

	public void setCsvFileUpperMaxSizeCheck(boolean csvFileUpperMaxSizeCheck) {
		this.csvFileUpperMaxSizeCheck = csvFileUpperMaxSizeCheck;
	}

	@Override
	public String toString() {
		return "CheckCSVSession [csvRowErrorList=" + csvRowErrorList
				+ ", csvFileUpperMaxSizeCheck=" + csvFileUpperMaxSizeCheck
				+ "]";
	}

}
