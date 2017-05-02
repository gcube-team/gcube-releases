package org.gcube.portlets.user.databasesmanager.shared.computation;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class ComputationOutput implements Serializable {

	private static final long serialVersionUID = -5508917907248426765L;
	private LinkedHashMap<String, String> mapValues;
	private LinkedHashMap<String, String> mapKeys;
	private String urlFile;
	private int submitQueryTotalRows;

	public ComputationOutput() {
		mapKeys = new LinkedHashMap<String, String>();
		mapValues = new LinkedHashMap<String, String>();
		urlFile = "";
	}

	public void setMapValues(LinkedHashMap<String, String> mapValues) {
		this.mapValues = mapValues;
	}

	public void setmapKeys(LinkedHashMap<String, String> mapKeys) {
		this.mapKeys = mapKeys;
	}

	public LinkedHashMap<String, String> getMapValues() {
		return mapValues;
	}

	public LinkedHashMap<String, String> getmapKeys() {
		return mapKeys;
	}

	public void setUrlFile(String url) {
		this.urlFile = url;
	}

	public String getUrlFile() {
		return this.urlFile;
	}

	public void setSubmitQueryTotalRows(int val) {
		this.submitQueryTotalRows = val;
	}

	public int getSubmitQueryTotalRows() {
		return this.submitQueryTotalRows;
	}

	@Override
	public String toString() {
		return "ComputationOutput [mapValues=" + mapValues + ", mapKeys="
				+ mapKeys + ", urlFile=" + urlFile + ", submitQueryTotalRows="
				+ submitQueryTotalRows + "]";
	}

}
