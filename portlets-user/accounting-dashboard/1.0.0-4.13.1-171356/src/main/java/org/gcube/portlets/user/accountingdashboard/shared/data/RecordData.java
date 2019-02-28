package org.gcube.portlets.user.accountingdashboard.shared.data;

import java.io.Serializable;

import jsinterop.annotations.JsType;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
@JsType
public class RecordData implements Serializable {

	private static final long serialVersionUID = -7526935477801214643L;
	private String x;
	private double y;

	public RecordData() {
		super();
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "RecordData [x=" + x + ", y=" + y + "]";
	}

}
