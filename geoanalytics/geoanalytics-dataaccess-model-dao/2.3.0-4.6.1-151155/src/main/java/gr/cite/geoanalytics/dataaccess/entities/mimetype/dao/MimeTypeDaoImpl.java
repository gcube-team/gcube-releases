package gr.cite.geoanalytics.dataaccess.entities.mimetype.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.mimetype.MimeType;

@Repository
public class MimeTypeDaoImpl extends JpaDao<MimeType, UUID> implements MimeTypeDao
{

	@Override
	public List<MimeType> findByExtension(String extension)
	{
		TypedQuery<MimeType> query = entityManager.createQuery("from MimeType mt where mt.fileNameExtension = :ext", MimeType.class);
		query.setParameter("ext", extension);
		
		return query.getResultList();
	}

	@Override
	public MimeType loadDetails(MimeType mt) {
		mt.getCreator().getName();
		return mt;
	}

}
