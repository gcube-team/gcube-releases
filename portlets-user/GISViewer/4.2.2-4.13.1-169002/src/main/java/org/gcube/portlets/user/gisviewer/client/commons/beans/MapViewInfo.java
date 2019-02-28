package org.gcube.portlets.user.gisviewer.client.commons.beans;

public class MapViewInfo {
	double lowerLeftX;
	double lowerLeftY;
	double upperRightX;
	double upperRightY;
	int width;
	int height;
	
	public MapViewInfo(double lowerLeftX, double lowerLeftY,
			double upperRightX, double upperRightY, int width, int height) {
		super();
		this.lowerLeftX = lowerLeftX;
		this.lowerLeftY = lowerLeftY;
		this.upperRightX = upperRightX;
		this.upperRightY = upperRightY;
		this.width = width;
		this.height = height;
	}


	public double getLowerLeftX() {
		return lowerLeftX;
	}


	public double getLowerLeftY() {
		return lowerLeftY;
	}


	public double getUpperRightX() {
		return upperRightX;
	}


	public double getUpperRightY() {
		return upperRightY;
	}


	public int getWidth() {
		return width;
	}


	public int getHeight() {
		return height;
	}
}
