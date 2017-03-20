package org.gcube.search.sru.consumer.parser.sruparser.tree;

import java.util.List;

import org.gcube.search.sru.consumer.parser.sruparser.tree.geo.GeoPoint;

public class GCQLOverlapsNode extends GCQLNode {

	private static final long serialVersionUID = 6467981641947364794L;
	
	String index;
	List<GeoPoint> geopoints;
	
	public GCQLOverlapsNode(String index, List<GeoPoint> geopoints) {
		this.index = index;
		this.geopoints = geopoints;
	}
	
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public List<GeoPoint> getGeopoints() {
		return geopoints;
	}
	public void setGeopoints(List<GeoPoint> geopoints) {
		this.geopoints = geopoints;
	}
	
	@Override
	public String toCQL() {
		String cqlStr = "overlaps("+index + ", " + geopoints.toString()+")"; 
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
