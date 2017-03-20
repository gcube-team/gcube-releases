//package gr.cite.geoanalytics.dataaccess.entities.shape.dao;
//
//import java.util.List;
//import java.util.UUID;
//
//import javax.persistence.NoResultException;
//import javax.persistence.Query;
//
//import org.springframework.stereotype.Repository;
//
//import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
//import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
//import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
//import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeLayer;
//import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeLayerPK;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Geocode;
//
////TODO: CHANGE ALL NOT TO USE  	Term  (NIKOLAS)
//
//@Repository
//public class ShapeLayerDaoImpl extends JpaDao<ShapeLayer, ShapeLayerPK> implements ShapeLayerDao
//{
//	@Override
//	public ShapeLayer find(UUID layerID, Shape s) {
//		Query query = entityManager.createQuery("from ShapeLayer sl where sl.layerID = :layerID and sl.shape = :s", 
//				ShapeLayer.class);
//		query.setParameter("layerID", layerID);
//		query.setParameter("s", s);
//		
//		try {
//			return (ShapeLayer)query.getSingleResult();
//		}catch(NoResultException e) {
//			return null;
//		}
//	}
//	
//	@Override
//	public UUID findLayerIDOfShape(Shape s) {
//		
//		Query query = entityManager.createQuery("select sl.layerID from ShapeLayer sl where sl.shape = :s", UUID.class);
//		query.setParameter("s", s);
//		try {
//			return (UUID)query.getSingleResult();
//		}catch(NoResultException e) {
//			return null;
//		}
//	}
//	
//	
//
//	@Override
//	public ShapeLayer loadDetails(ShapeLayer st) {
//		st.getShape().getId();
//		return st;
//	}
//
//	@Override
//	public List<Shape> findShapesOfLayer(UUID layerID) {
//		Query query = entityManager.createQuery("select shape from ShapeLayer sl where sl.layerID = :layerID", Shape.class);
//		query.setParameter("layerID", layerID);
//		try {
//			return (List<Shape>) query.getResultList();
//		}catch(NoResultException e) {
//			return null;
//		}
//	}
//
//	@Override
//	public int deleteByShapeID(UUID shapeID) {
//		Query query = entityManager.createQuery("delete from ShapeLayer sl where sl.shape.id = :shapeID");
//		query.setParameter("shapeID", shapeID);
////		try {
//			return query.executeUpdate();
////		}catch(NoResultException e) {
////			return -1;
////		}
//	}
//}
