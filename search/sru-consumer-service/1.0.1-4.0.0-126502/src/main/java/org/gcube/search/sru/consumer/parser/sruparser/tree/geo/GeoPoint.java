package org.gcube.search.sru.consumer.parser.sruparser.tree.geo;

public class GeoPoint {

	private Double lat;
	private Double lon;
	
	public GeoPoint(Double lat, Double lon) {
		super();
		this.lat = lat;
		this.lon = lon;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
	}
	
	@Override
	public String toString()
	{
		return "["+lat+", "+lon+"]";
	}
	
	
}
