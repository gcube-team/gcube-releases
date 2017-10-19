//package gr.cite.geoanalytics.dataaccess.entities.shape.dao;
//
//import java.util.List;
//import java.util.UUID;
//
//import gr.cite.geoanalytics.dataaccess.dao.Dao;
//import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
//import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
//import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeLayer;
//import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeLayerPK;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Geocode;
//
//public interface ShapeLayerDao extends Dao<ShapeLayer, ShapeLayerPK>
//{
//	public ShapeLayer find(UUID layerID, Shape s);
//	public UUID findLayerIDOfShape(Shape s);
//	public List<Shape> findShapesOfLayer(UUID layerID);
//	public int deleteByShapeID(UUID shapeID);
//	
//	
//}
