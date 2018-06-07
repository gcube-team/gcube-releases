package gr.cite.geoanalytics.functions.common.model;

public class BBox {

	double minLon;
	double maxLon;
	double minLat;
	double maxLat;
	
	
	public BBox(double minLon, double maxLon, double minLat, double maxLat) {
		super();
		this.minLon = minLon;
		this.maxLon = maxLon;
		this.minLat = minLat;
		this.maxLat = maxLat;
	}
	
	
	public double getMinLon() {
		return minLon;
	}
	public void setMinLon(double minLon) {
		this.minLon = minLon;
	}
	public double getMaxLon() {
		return maxLon;
	}
	public void setMaxLon(double maxLon) {
		this.maxLon = maxLon;
	}
	public double getMinLat() {
		return minLat;
	}
	public void setMinLat(double minLat) {
		this.minLat = minLat;
	}
	public double getMaxLat() {
		return maxLat;
	}
	public void setMaxLat(double maxLat) {
		this.maxLat = maxLat;
	}
	
	
	@Override
	public String toString(){
		return "[minLon: "+minLon +", maxLon: "+maxLon+", minLat: "+minLat+", maxLat: "+maxLat+"]";
	}
	
}
