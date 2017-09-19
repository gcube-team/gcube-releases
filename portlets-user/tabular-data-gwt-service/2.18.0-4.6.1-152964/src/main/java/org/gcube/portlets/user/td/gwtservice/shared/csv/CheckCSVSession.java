package org.gcube.portlets.user.td.gwtservice.shared.csv;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class CheckCSVSession implements Serializable {

	private static final long serialVersionUID = 7775024048559018931L;

	private ArrayList<CSVRowError> csvRowErrorList;
	private boolean csvFileUpperMaxSizeCheck;

	public CheckCSVSession() {
		super();
	}

	public CheckCSVSession(ArrayList<CSVRowError> csvRowErrorList,
			boolean csvFileUpperMaxSizeCheck) {
		super();
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
