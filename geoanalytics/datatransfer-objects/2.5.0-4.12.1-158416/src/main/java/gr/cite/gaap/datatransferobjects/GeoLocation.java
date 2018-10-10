package gr.cite.gaap.datatransferobjects;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;

public class GeoLocation {
	private static Logger logger = LoggerFactory.getLogger(GeoLocation.class);
	private List<GeoLocationTag> tags = new ArrayList<GeoLocationTag>();
	private double x = 0.0f;
	private double y = 0.0f;
	private Bounds bounds = null;
	private String name = null;
	private String id = null;

	public GeoLocation() {
		logger.trace("Initialized default contructor for GeoLocation");
	}

	public GeoLocation(List<GeoLocationTag> tags, double x, double y, Bounds bounds) {
		logger.trace("Initializing GeoLocation...");
		this.tags = tags;
		this.bounds = bounds;
		this.x = x;
		this.y = y;
		logger.trace("Initialized AuditingMessenger");
	}

	public GeoLocation(List<GeoLocationTag> tags, double x, double y, Bounds bounds, String name, String id) {
		this(tags, x, y, bounds);
		logger.trace("Initializing GeoLocation...");
		this.name = name;
		this.id = id;
		logger.trace("Initialized AuditingMessenger");
	}

	public List<GeoLocationTag> getTags() {
		return tags;
	}

	public void setTags(List<GeoLocationTag> tags) {
		this.tags = tags;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public Bounds getBounds() {
		return bounds;
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
