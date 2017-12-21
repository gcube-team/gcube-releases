package gr.cite.geoanalytics.functions.common.model;

public class GeoPoint {

	double longitude;
	double latitude;
	
	
	
	
	public GeoPoint(double longitude, double latitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	@Override
	public String toString(){
		return "[longitude: "+longitude+" latitude: "+latitude+"]";
	}
	
	
}
