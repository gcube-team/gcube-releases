package gr.cite.geoanalytics.dataaccess.entities.annotation.dao;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.annotation.Annotation;

@Repository
public class AnnotationDaoImpl extends JpaDao<Annotation, UUID> implements AnnotationDao
{

	@Override
	public Annotation loadDetails(Annotation an) {
		an.getCreator().getName();
		if(an.getInResponseTo() != null)
			an.getInResponseTo().getId();
		if(an.getTenant() != null)
			an.getTenant().getId();
		return an;
	}

}
