package org.gcube.search.sru.consumer.parser.sruparser.tree;

import org.gcube.search.sru.consumer.parser.sruparser.tree.geo.GeoPoint;

public class GCQLDistanceNode extends GCQLNode {

	private static final long serialVersionUID = -426157917522653058L;
	
	String index;
	Double distance;
	GeoPoint point;

	public GCQLDistanceNode(String index, Double distance, GeoPoint point) {
		this.index = index;
		this.distance = distance;
		this.point = point;
	}

	public String getIndex() {
		return index;
	}
	
	public void setIndex(String index) {
		this.index = index;
	}
	
	public Double getDistance() {
		return distance;
	}
	
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	
	public GeoPoint getPoint() {
		return point;
	}
	
	public void setPoint(GeoPoint point) {
		this.point = point;
	}
	
	@Override
	public String toCQL() {
		String cqlStr = "distance("+index + ", " + distance + ", " + point.toString()+")"; 
		return cqlStr;
	}
	
	@Override
	public void printNode(int numStars) {
		System.out.println();
		for (int i = 0; i < numStars; i++) {
			System.out.print("*");
		}
		System.out.println(this.getClass().getName() + " ---- " + toCQL() + " ---- ");
	}

}
