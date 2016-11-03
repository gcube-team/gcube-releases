package gr.cite.geoanalytics.dataaccess.entities.shape.dao;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeTerm;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeTermPK;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;

@Repository
public class ShapeTermDaoImpl extends JpaDao<ShapeTerm, ShapeTermPK> implements ShapeTermDao
{
	@Override
	public ShapeTerm find(TaxonomyTerm tt, Shape s) {
		Query query = entityManager.createQuery("from ShapeTerm st where st.term = :t and st.shape = :s", 
				ShapeTerm.class);
		query.setParameter("t", tt);
		query.setParameter("s", s);
		
		try {
			return (ShapeTerm)query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}
	
	@Override
	public void deleteByTerm(TaxonomyTerm tt) {
		Query query = entityManager.createQuery("delete ShapeTerm st where st.term = :tt");
		query.setParameter("tt", tt);
		query.executeUpdate();
	}

	@Override
	public ShapeTerm loadDetails(ShapeTerm st) {
		st.getCreator().getName();
		st.getShape().getId();
		st.getTerm().getId();
		return st;
	}
}
