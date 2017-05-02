package gr.cite.bluebridge.analytics.model;

import gr.cite.bluebridge.analytics.web.Parameters;

public class Economics {
	
	private Parameters parameters;
	private Values undepreciatedValues;
	private Values depreciatedValues;	

	public Economics() {
		undepreciatedValues = new Values();
		depreciatedValues = new Values();
	}
	
	public void InitYearEntries(int startYear, int endYear) {
		this.getUndepreciatedValues().InitYearEntries(startYear, endYear);
		this.getDepreciatedValues().InitYearEntries(startYear, endYear);
	}
	
	public Values getUndepreciatedValues() {
		return undepreciatedValues;
	}

	public void setUndepreciatedValues(Values undepreciatedValues) {
		this.undepreciatedValues = undepreciatedValues;
	}

	public Values getDepreciatedValues() {
		return depreciatedValues;
	}

	public void setDepreciatedValues(Values depreciatedValues) {
		this.depreciatedValues = depreciatedValues;
	}
	
	public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}
}
