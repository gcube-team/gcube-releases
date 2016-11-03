package gr.cite.bluebridge.analytics.model;

public class Economics {

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
}
