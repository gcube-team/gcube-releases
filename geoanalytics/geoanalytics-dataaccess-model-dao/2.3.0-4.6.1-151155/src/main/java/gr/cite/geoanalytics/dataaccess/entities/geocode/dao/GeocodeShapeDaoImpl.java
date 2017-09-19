package gr.cite.geoanalytics.dataaccess.entities.geocode.dao;
//package gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao;
//
//import java.util.List;
//import java.util.UUID;
//
//import javax.persistence.NoResultException;
//import javax.persistence.Query;
//import javax.persistence.TypedQuery;
//
//import org.springframework.stereotype.Repository;
//
//import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
//import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Geocode;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.GeocodeShape;
//
//@Repository
//public class GeocodeShapeDaoImpl extends JpaDao<GeocodeShape, UUID> implements GeocodeShapeDao {
//	
//	@Override
//	public GeocodeShape find(Geocode tt, Shape s) {
//		Query query = entityManager.createQuery("from GeocodeShape tts where tts.geocode= :t and tts.shape = :s", 
//				GeocodeShape.class);
//		query.setParameter("t", tt);
//		query.setParameter("s", s);
//		
//		try {
//			return (GeocodeShape)query.getSingleResult();
//		}catch(NoResultException e) {
//			return null;
//		}
//	}
//	
//	@Override
//	public void deleteByGeocode(Geocode tt) {
//		Query query = entityManager.createQuery("delete GeocodeShape tts where tts.geocode= :tt");
//		query.setParameter("tt", tt);
//		query.executeUpdate();
//	}
//	
//	@Override
//	public GeocodeShape findUniqueByGeocode(Geocode tt) {
//		TypedQuery<GeocodeShape> query = entityManager.createQuery(
//				"select tts from GeocodeShape tts where tts.geocode= :tt", GeocodeShape.class);
//		query.setParameter("tt", tt);
//
//		try {
//			return query.getSingleResult();
//		}catch(NoResultException e) {
//			return null;
//		}
//	}
//	
//	@Override
//	public List<GeocodeShape> findByGeocode(Geocode tt) {
//		TypedQuery<GeocodeShape> query = entityManager.createQuery(
//				"select tts from GeocodeShape tts where tts.geocode= :tt", GeocodeShape.class);
//		query.setParameter("tt", tt);
//
//		return query.getResultList();
//	}
//	
//	@Override
//	public List<GeocodeShape> findNonProjectByGeocode(Geocode tt) {
//		/*select tts.taxts_id, tts.taxts_term, tts.taxts_shape, t.taxt_name, s.shp_name
//from taxonomytermshape tts, shape s,taxonomyterm t
//where tts.taxts_id not in(
//	select distinct tts2.taxts_id
//	from taxonomytermshape tts2, shape s2,taxonomyterm t2, project p
//	where tts2.taxts_shape = s2.shp_id 
//	and s2.shp_id = p.prj_shape
//	and t2.taxt_id=tts2.taxts_term
//	and t2.taxt_name='TestFek1')
//and tts.taxts_shape = s.shp_id 
//and t.taxt_id=tts.taxts_term
//and t.taxt_name='TestFek1'
//
//*/
//		TypedQuery<GeocodeShape> query = entityManager.createQuery(
//				"select distinct tts from GeocodeShape tts " +
//				"where tts not in ("+
//				"select distinct tts2 from GeocodeShape tts2, Shape s2, Project p " +
//				"where tts2.shape = s2 and tts2.geocode= :tt and s2.id = p.shape) " +
//				"and tts.geocode= :tt", GeocodeShape.class);
//		query.setParameter("tt", tt);
//
//		return query.getResultList();
//	}
//	
//	@Override
//	public List<GeocodeShape> findByShape(Shape s) {
//		TypedQuery<GeocodeShape> query = entityManager.createQuery(
//				"select tts from GeocodeShape tts where tts.shape = :s", GeocodeShape.class);
//		query.setParameter("s", s);
//
//		return query.getResultList();
//	}
//
//	@Override
//	public GeocodeShape loadDetails(GeocodeShape tts) {
//		tts.getCreator().getName();
//		tts.getGeocode().getId();
//		tts.getShape().getId();
//		return tts;
//	}
//}
