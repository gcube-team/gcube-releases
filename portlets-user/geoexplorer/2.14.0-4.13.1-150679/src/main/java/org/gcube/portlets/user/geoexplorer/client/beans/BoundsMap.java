package org.gcube.portlets.user.geoexplorer.client.beans;

import com.google.gwt.user.client.rpc.IsSerializable;

public class BoundsMap implements IsSerializable{
	
	private double lowerLeftX = 0.0;
	private double lowerLeftY = 0.0;
	private double upperRightX = 0.0;
	private double upperRightY = 0.0;
	private String crs = "";
	
	public BoundsMap() {
		super();
		// TODO Auto-generated constructor stub
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
}
