package gr.cite.gaap.datatransferobjects;

import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;

public class GeoLocationTag {
	private String termId;
	private String tag;

	private String classTermId;
	private String classTag;

	private double x = 0.0f;
	private double y = 0.0f;

	private Bounds bounds = null;

	public GeoLocationTag() {
	}

	public GeoLocationTag(String termId, String tag, String classTermId, String classTag, double centroidX,
			double centroidY, Bounds bounds) {
		this.termId = termId;
		this.tag = tag;
		this.classTermId = classTermId;
		this.classTag = classTag;
		this.x = centroidX;
		this.y = centroidY;
		this.bounds = bounds;
	}

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getClassTermId() {
		return classTermId;
	}

	public void setClassTermId(String classTermId) {
		this.classTermId = classTermId;
	}

	public String getClassTag() {
		return classTag;
	}

	public void setClassTag(String classTag) {
		this.classTag = classTag;
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
}
