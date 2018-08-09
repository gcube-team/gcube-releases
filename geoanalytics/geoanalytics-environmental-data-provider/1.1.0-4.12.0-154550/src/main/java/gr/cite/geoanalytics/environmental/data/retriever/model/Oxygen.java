package gr.cite.geoanalytics.environmental.data.retriever.model;

public class Oxygen extends Data {

	public Oxygen(Data data) {
		super(data);
	}
	
	@Override
	public Integer getValueAsInt(Unit unit) {	
		if (value != null && Math.abs(value) < Integer.MAX_VALUE) {
			return super.getValueAsInt();
		} 
		
		return null;
	}
}
