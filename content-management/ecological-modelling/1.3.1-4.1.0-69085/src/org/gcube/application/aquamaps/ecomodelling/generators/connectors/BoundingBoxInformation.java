package org.gcube.application.aquamaps.ecomodelling.generators.connectors;

public class BoundingBoxInformation {

	private boolean inBoundingBox;
	private boolean inFaoArea;
	
	public void setInBoundingBox(boolean inBoundingBox) {
		this.inBoundingBox = inBoundingBox;
	}
	public boolean isInBoundingBox() {
		return inBoundingBox;
	}
	public void setInFaoArea(boolean inFaoArea) {
		this.inFaoArea = inFaoArea;
	}
	public boolean isInFaoArea() {
		return inFaoArea;
	}
}
