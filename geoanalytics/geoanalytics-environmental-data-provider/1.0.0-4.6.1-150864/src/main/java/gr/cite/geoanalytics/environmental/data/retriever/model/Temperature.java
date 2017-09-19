package gr.cite.geoanalytics.environmental.data.retriever.model;

public class Temperature extends Data {

	public Temperature(Data data) {
		super(data);
	}

	@Override
	public Integer getValueAsInt(Unit unit) {
		if (unit != null && value != null) {
			Float value = (unit == Unit.CELCIUS) ? getCelciusTemperature() : getKelvinTemperature();
			return value != null ? Math.round(value) : null;	
		} 
		
		return null;
	}

	private Float getKelvinTemperature() {
		if(value == null || value < 0){
			return null;
		}
		
		return value;
	}

	private Float getCelciusTemperature() {
		if(value == null || value < 0){
			return null;
		}
		
		return value - 273.15f;
	}
}
