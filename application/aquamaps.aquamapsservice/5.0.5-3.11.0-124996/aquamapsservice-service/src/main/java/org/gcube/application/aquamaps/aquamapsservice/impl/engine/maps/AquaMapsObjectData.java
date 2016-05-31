package org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps;

public class AquaMapsObjectData {

	private int submittedId;
	
	private String path;
	private String speciesCSVList;
	
	//********* PERL
	private String csq_str;
	private int min;
	private int max;
	
	//********* GIS
	private String csvFile;

	public String getCsq_str() {
		return csq_str;
	}

	public void setCsq_str(String csq_str) {
		this.csq_str = csq_str;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public String getCsvFile() {
		return csvFile;
	}

	public void setCsvFile(String csvFile) {
		this.csvFile = csvFile;
	}
public int getSubmittedId() {
	return submittedId;
}
public void setSubmittedId(int submittedId) {
	this.submittedId = submittedId;
}
public void setPath(String path) {
	this.path = path;
}
public String getPath() {
	return path;
}
	public AquaMapsObjectData(int submittedId,String csq_str, int min, int max,
			String csvFile,String path, String speciesList) {
		super();
		this.submittedId=submittedId;
		this.csq_str = csq_str;
		this.min = min;
		this.max = max;
		this.csvFile = csvFile;
		this.path=path;
		this.speciesCSVList=speciesList;
	}
	
	public String getSpeciesCSVList() {
		return speciesCSVList;
	}
	
	
}
