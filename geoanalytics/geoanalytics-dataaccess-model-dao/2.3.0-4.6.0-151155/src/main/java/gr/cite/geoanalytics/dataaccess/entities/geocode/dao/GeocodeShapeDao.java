package gr.cite.geoanalytics.dataaccess.entities.geocode.dao;
//package gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao;
//
//import java.util.List;
//import java.util.UUID;
//
//import gr.cite.geoanalytics.dataaccess.dao.Dao;
//import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Geocode;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.GeocodeShape;
//
//public interface GeocodeShapeDao extends Dao<GeocodeShape, UUID>
//{
//	public GeocodeShape find(Geocode tt, Shape s);
//	public GeocodeShape findUniqueByGeocode(Geocode tt);
//	public List<GeocodeShape> findByGeocode(Geocode tt);
//	public List<GeocodeShape> findNonProjectByGeocode(Geocode tt);
//	public List<GeocodeShape> findByShape(Shape s);
//	public void deleteByGeocode(Geocode tt);
//}