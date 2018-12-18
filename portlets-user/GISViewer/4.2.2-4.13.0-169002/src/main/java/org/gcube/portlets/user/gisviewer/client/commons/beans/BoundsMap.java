package org.gcube.portlets.user.gisviewer.client.commons.beans;

import com.google.gwt.user.client.rpc.IsSerializable;

public class BoundsMap implements IsSerializable{
	
	private double lowerLeftX = 0.0;
	private double lowerLeftY = 0.0;
	private double upperRightX = 0.0;
	private double upperRightY = 0.0;
	private String crs = "";
	
	public BoundsMap() {
		super();
	}
		
	public BoundsMap(double lowerLeftX, double lowerLeftY, double upperRightX,
			double upperRightY, String crs) {
		super();
		this.lowerLeftX = lowerLeftX;
		this.lowerLeftY = lowerLeftY;
		this.upperRightX = upperRightX;
		this.upperRightY = upperRightY;
		this.crs = crs;
	}

	public double getLowerLeftX() {
		return lowerLeftX;
	}

	public void setLowerLeftX(double lowerLeftX) {
		this.lowerLeftX = lowerLeftX;
	}

	public double getLowerLeftY() {
		return lowerLeftY;
	}

	public void setLowerLeftY(double lowerLeftY) {
		this.lowerLeftY = lowerLeftY;
	}

	public double getUpperRightX() {
		return upperRightX;
	}

	public void setUpperRightX(double upperRightX) {
		this.upperRightX = upperRightX;
	}

	public double getUpperRightY() {
		return upperRightY;
	}

	public void setUpperRightY(double upperRightY) {
		this.upperRightY = upperRightY;
	}

	public String getCrs() {
		return crs;
	}

	public void setCrs(String crs) {
		this.crs = crs;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BoundsMap [lowerLeftX=");
		builder.append(lowerLeftX);
		builder.append(", lowerLeftY=");
		builder.append(lowerLeftY);
		builder.append(", upperRightX=");
		builder.append(upperRightX);
		builder.append(", upperRightY=");
		builder.append(upperRightY);
		builder.append(", crs=");
		builder.append(crs);
		builder.append("]");
		return builder.toString();
	}
}
