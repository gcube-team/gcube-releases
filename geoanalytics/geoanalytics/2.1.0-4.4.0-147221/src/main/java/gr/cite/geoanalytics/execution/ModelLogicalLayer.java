//package gr.cite.geoanalytics.execution;
//
//import java.util.List;
//import java.util.UUID;
//
//import javax.inject.Inject;
//
//import org.springframework.stereotype.Component;
//
//import com.vividsolutions.jts.geom.Geometry;
//
//import gr.cite.gaap.geospatialbackend.GeospatialBackend;
//import gr.cite.gaap.servicelayer.ShapeManager;
//import gr.cite.gaap.servicelayer.GeocodeManager;
//import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
//import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
//import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
//
//@Component
//public class ModelLogicalLayer implements ModelLayer {
//
//	private GeospatialBackend geospatialBackend;
//	private Layer layer;
//	
//	public ModelLogicalLayer(Layer layer) {
//		this.layer = layer;
//	}
//	
//	@Inject
//	public void setLayer(Layer layer) {
//		this.layer = layer;
//	}
//	
//	public Layer getLayer() {
//		return layer;
//	}
//
//	@Inject
//	public void setGeospatialBackend(GeospatialBackend geospatialBackend) {
//		this.geospatialBackend = geospatialBackend;
//	}
//	
//	@Override
//	public List<Shape> getGeometry() throws Exception {
//		return geospatialBackend.getShapesOfLayerID(layer.getId());
//	}
//
//
//	@Override
//	public Shape locate(Geometry geometry) throws Exception {
//		List<Shape> shapes = geospatialBackend.findShapesEnclosingGeometry(geometry);
//		if(shapes.size() > 1)
//			throw new Exception("Found more than one shapes enclosing provided geometry for layer " + layer.getName() + "(" + layer.getId() + ")");
//		if(shapes.isEmpty())
//			return null;
//		return shapes.get(0);
//	}
//
//}
