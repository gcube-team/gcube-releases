package gr.cite.regional.data.collection.dataaccess.daos;

import gr.cite.regional.data.collection.dataaccess.entities.Annotation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author vfloros
 *
 */
public class AnnotationDaoImpl extends JpaDao<Annotation, Integer> implements AnnotationDao {
	@Override
	public Annotation loadDetails(Annotation t) {
		// TODO Auto-generated method stub
		return null;
	}
}