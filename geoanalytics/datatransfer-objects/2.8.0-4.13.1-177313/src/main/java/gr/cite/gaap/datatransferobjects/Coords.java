package gr.cite.gaap.datatransferobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Coords {
	
	private static Logger logger = LoggerFactory.getLogger(Coords.class);
	private double lon;
	private double lat;
	
	public Coords() { }
	
	public Coords(double lon, double lat) {
		logger.trace("Initializing Coords...");
		this.lon = lon;
		this.lat = lat;
		logger.trace("Initialized Coords");
	}
	
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	
}
