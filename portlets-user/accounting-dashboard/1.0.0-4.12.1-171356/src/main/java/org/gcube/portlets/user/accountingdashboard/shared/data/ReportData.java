package org.gcube.portlets.user.accountingdashboard.shared.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ReportData implements Serializable {

	private static final long serialVersionUID = -2420024317463146907L;
	private ArrayList<ReportElementData> elements;

	public ReportData() {
		super();
	}

	public ReportData(ArrayList<ReportElementData> elements) {
		super();
		this.elements = elements;
	}

	public ArrayList<ReportElementData> getElements() {
		return elements;
	}

	public void setElements(ArrayList<ReportElementData> elements) {
		this.elements = elements;
	}

	@Override
	public String toString() {
		return "ReportData [elements=" + elements + "]";
	}

}
