package gr.cite.geoanalytics.environmental.data.retriever.model;

public class Data {

	protected String date;

	protected double latitude;
	protected double longitude;

	protected Float value;

	public Data() {}

	public Data(Data other) {
		if(other != null){
			this.date = other.getDate();
			this.value = other.getValue();
			this.latitude = other.getLatitude();
			this.longitude = other.getLongitude();
		}
	}

	public Integer getValueAsInt() {		
		return value != null ? Math.round(value) : null;
	}

	public Float getValue() {
		return value;
	}
	
	public Integer getValueAsInt(Unit unit) {
		return value != null ? Math.round(value) : null;
	}

	public Float getValue(Unit unit) {
		return value;
	}
	
	public void setValue(Float value) {
		this.value = value;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return String.format("[ Date %10s,  Latitude  %f, Longitude %f, " + " Value  = %10.5f ]", date, latitude, longitude, value);
	}
}
