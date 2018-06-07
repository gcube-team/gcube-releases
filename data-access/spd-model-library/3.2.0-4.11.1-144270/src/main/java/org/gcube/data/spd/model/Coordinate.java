package org.gcube.data.spd.model;

public class Coordinate implements Comparable<Coordinate>{

	private double latitude;
	private double longitude;
	
	public Coordinate(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	@Override
	public int compareTo(Coordinate o) {
		if (this.latitude>o.getLatitude()) return 1;
		else if (this.latitude<o.getLatitude()) return -1;
		
		if (this.longitude>o.getLongitude()) return 1;
		else if (this.longitude<o.getLongitude()) return -1;
		
		return 0;
	}
	
}
