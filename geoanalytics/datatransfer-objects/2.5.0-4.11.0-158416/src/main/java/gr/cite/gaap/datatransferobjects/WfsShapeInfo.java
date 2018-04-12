package gr.cite.gaap.datatransferobjects;

import java.util.List;

import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;

public class WfsShapeInfo {
	List<Shape> listShape;
	Bounds bounds;
	
	
	public List<Shape> getListShape() {
		return listShape;
	}
	public void setListShape(List<Shape> listShape) {
		this.listShape = listShape;
	}
	public Bounds getBounds() {
		return bounds;
	}
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}
	
	

}
