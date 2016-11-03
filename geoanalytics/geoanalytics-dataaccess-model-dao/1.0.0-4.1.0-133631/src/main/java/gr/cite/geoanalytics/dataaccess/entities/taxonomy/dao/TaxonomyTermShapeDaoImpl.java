package gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermShape;

@Repository
public class TaxonomyTermShapeDaoImpl extends JpaDao<TaxonomyTermShape, UUID> implements TaxonomyTermShapeDao {
	
	@Override
	public TaxonomyTermShape find(TaxonomyTerm tt, Shape s) {
		Query query = entityManager.createQuery("from TaxonomyTermShape tts where tts.term = :t and tts.shape = :s", 
				TaxonomyTermShape.class);
		query.setParameter("t", tt);
		query.setParameter("s", s);
		
		try {
			return (TaxonomyTermShape)query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}
	
	@Override
	public void deleteByTerm(TaxonomyTerm tt) {
		Query query = entityManager.createQuery("delete TaxonomyTermShape tts where tts.term = :tt");
		query.setParameter("tt", tt);
		query.executeUpdate();
	}
	
	@Override
	public TaxonomyTermShape findUniqueByTerm(TaxonomyTerm tt) {
		TypedQuery<TaxonomyTermShape> query = entityManager.createQuery(
				"select tts from TaxonomyTermShape tts where tts.term = :tt", TaxonomyTermShape.class);
		query.setParameter("tt", tt);

		try {
			return query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}
	
	@Override
	public List<TaxonomyTermShape> findByTerm(TaxonomyTerm tt) {
		TypedQuery<TaxonomyTermShape> query = entityManager.createQuery(
				"select tts from TaxonomyTermShape tts where tts.term = :tt", TaxonomyTermShape.class);
		query.setParameter("tt", tt);

		return query.getResultList();
	}
	
	@Override
	public List<TaxonomyTermShape> findNonProjectByTerm(TaxonomyTerm tt) {
		/*select tts.taxts_id, tts.taxts_term, tts.taxts_shape, t.taxt_name, s.shp_name
from taxonomytermshape tts, shape s,taxonomyterm t
where tts.taxts_id not in(
	select distinct tts2.taxts_id
	from taxonomytermshape tts2, shape s2,taxonomyterm t2, project p
	where tts2.taxts_shape = s2.shp_id 
	and s2.shp_id = p.prj_shape
	and t2.taxt_id=tts2.taxts_term
	and t2.taxt_name='TestFek1')
and tts.taxts_shape = s.shp_id 
and t.taxt_id=tts.taxts_term
and t.taxt_name='TestFek1'

*/
		TypedQuery<TaxonomyTermShape> query = entityManager.createQuery(
				"select distinct tts from TaxonomyTermShape tts " +
				"where tts not in ("+
				"select distinct tts2 from TaxonomyTermShape tts2, Shape s2, Project p " +
				"where tts2.shape = s2 and tts2.term = :tt and s2.id = p.shape) " +
				"and tts.term = :tt", TaxonomyTermShape.class);
		query.setParameter("tt", tt);

		return query.getResultList();
	}
	
	@Override
	public List<TaxonomyTermShape> findByShape(Shape s) {
		TypedQuery<TaxonomyTermShape> query = entityManager.createQuery(
				"select tts from TaxonomyTermShape tts where tts.shape = :s", TaxonomyTermShape.class);
		query.setParameter("s", s);

		return query.getResultList();
	}

	@Override
	public TaxonomyTermShape loadDetails(TaxonomyTermShape tts) {
		tts.getCreator().getName();
		tts.getTerm().getId();
		tts.getShape().getId();
		return tts;
	}
}
