package org.gcube.dataanalysis.geo.charts;

import java.util.Date;

import org.gcube.dataanalysis.ecoengine.utils.TimeAnalyzer;

public class GeoTemporalPoint {

	public double x;
	public double y;
	public double z;
	public double weight;
	public Date time;
	public String timePattern = "MM/dd/yyyy";

	public GeoTemporalPoint(double x, double y, double z, double weight, String time) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.weight = weight;
		if (time == null)
			this.time = new Date(System.currentTimeMillis());
		else {
			TimeAnalyzer tsa = new TimeAnalyzer();
			Date timedate = tsa.string2Date(time);
			String timePattern = tsa.getPattern();
			this.time = timedate;
			this.timePattern = timePattern;
		}

	}

	public GeoTemporalPoint(double x, double y, String time) {
		this(x, y, 0d, 1d, time);
	}

	public GeoTemporalPoint(double x, double y, double weight, String time) {
		this(x, y, 0d, weight, time);
	}

	public GeoTemporalPoint(double x, double y, double weight) {
		this(x, y, 0d, weight, null);
	}

}
