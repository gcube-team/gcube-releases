package gr.cite.gaap.servicelayer;

import java.util.UUID;

import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.gaap.datatransferobjects.GeocodeMessenger;
import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;

public class ShapeInfo {
	
	private Shape shape = null;
	private UUID layerID = null;

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public UUID getLayerID() {
		return layerID;
	}

	public void setLayerID(UUID layerID) {
		this.layerID = layerID;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(!(obj instanceof ShapeInfo))
			return false;
		
		ShapeInfo other = (ShapeInfo)obj;
		return this.getShape().equals(other.getShape()) &&
				this.getLayerID().equals(other.getLayerID());
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result += 37 * result + this.getShape().hashCode();
		result += 37 * result + this.getLayerID().hashCode();
		return result;
	}
	
	public static class ShapeInfoMessenger{
		
		private ShapeMessenger shapeMessenger = new ShapeMessenger();
		private GeocodeMessenger geocodeMessenger = new GeocodeMessenger();
		
		public ShapeInfoMessenger(){}
		
		public ShapeInfoMessenger(Shape shape, Geocode geocode) throws Exception{
			
			this.shapeMessenger.setCode(shape.getCode());
			this.shapeMessenger.setExtraData(shape.getExtraData());
			this.shapeMessenger.setGeometry(shape.getGeography().toText());
//			this.shapeMessenger.setImportId(shape.getShapeImport().toString());
			this.shapeMessenger.setName(shape.getName());
			this.shapeMessenger.setId(shape.getId().toString());
			
			this.geocodeMessenger = new GeocodeMessenger(geocode);
			
		}
		
		public ShapeInfoMessenger(ShapeMessenger shapeMessenger, GeocodeMessenger taxonomyTermMessenger){
			this.shapeMessenger = shapeMessenger;
			this.geocodeMessenger = taxonomyTermMessenger;
		}
		
		public ShapeMessenger getShapeMessenger() {
			return shapeMessenger;
		}
		public void setShapeMessenger(ShapeMessenger shapeMessenger) {
			this.shapeMessenger = shapeMessenger;
		}

		public GeocodeMessenger getGeocodeMessenger() {
			return geocodeMessenger;
		}

		public void setGeocodeMessenger(GeocodeMessenger geocodeMessenger) {
			this.geocodeMessenger = geocodeMessenger;
		}
	}
}