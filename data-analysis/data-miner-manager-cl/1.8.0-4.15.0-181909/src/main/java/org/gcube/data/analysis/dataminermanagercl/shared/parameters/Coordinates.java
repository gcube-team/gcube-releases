package org.gcube.data.analysis.dataminermanagercl.shared.parameters;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class Coordinates implements Serializable {
	private static final long serialVersionUID = -1387634056697911513L;
	private String projection;
	private String x;
	private String y;
	private String zoom;

	public Coordinates() {
		super();
	}

	public Coordinates(String projection, String x, String y, String zoom) {
		super();
		this.projection = projection;
		this.x = x;
		this.y = y;
		this.zoom = zoom;
	}

	public String getProjection() {
		return projection;
	}

	public void setProjection(String projection) {
		this.projection = projection;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getZoom() {
		return zoom;
	}

	public void setZoom(String zoom) {
		this.zoom = zoom;
	}

	@Override
	public String toString() {
		return "Coordinates [projection=" + projection + ", x=" + x + ", y=" + y + ", zoom=" + zoom + "]";
	}

}
