package gr.cite.geoanalytics.dataaccess.entities.shape.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeDocument;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermShape;

@Repository
public class ShapeDocumentDaoImpl extends JpaDao<ShapeDocument, UUID> implements ShapeDocumentDao
{
	@Override
	public ShapeDocument find(TaxonomyTermShape tts, Document d) {
		TypedQuery<ShapeDocument> query = entityManager.createQuery("from ShapeDocument sd where sd.taxonomyTermShape = :tts and sd.document = :d", 
				ShapeDocument.class);
		query.setParameter("tts", tts);
		query.setParameter("d", d);
		
		try {
			return query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}
	
	@Override
	public List<ShapeDocument> findByDocument(Document d) {
		TypedQuery<ShapeDocument> query = entityManager.createQuery("from ShapeDocument sd  where sd.document = :d", 
				ShapeDocument.class);
		query.setParameter("d", d);
		
		return query.getResultList();
	}
	
	@Override
	public List<Document> findDocumentsByTaxonomyTermShape(TaxonomyTermShape tts) {
		TypedQuery<Document> query = entityManager.createQuery("select sd.document from ShapeDocument sd  where sd.taxonomyTermShape = :tts", 
				Document.class);
		query.setParameter("tts", tts);
		
		return query.getResultList();
	}
	
	@Override
	public List<ShapeDocument> findByTaxonomyTermShape(TaxonomyTermShape tts) {
		TypedQuery<ShapeDocument> query = entityManager.createQuery("from ShapeDocument sd  where sd.taxonomyTermShape = :tts", 
				ShapeDocument.class);
		query.setParameter("tts", tts);
		
		return query.getResultList();
	}
	
	@Override
	public Document findUniqueByTaxonomyTermShape(TaxonomyTermShape tts) {
		TypedQuery<Document> query = entityManager.createQuery("select sd.document from ShapeDocument sd  where sd.taxonomyTermShape = :tts", 
				Document.class);
		query.setParameter("tts", tts);
		
		try {
			return query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}
	
	@Override
	public void deleteByTaxonomyTermShape(TaxonomyTermShape tts) {
		Query query = entityManager.createQuery("delete ShapeDocument sd where sd.taxonomyTermShape = :tts");
		query.setParameter("tts", tts);
		query.executeUpdate();
	}
	
	@Override
	public void deleteByDocument(Document d) {
		Query query = entityManager.createQuery("delete ShapeDocument sd where sd.document = :d");
		query.setParameter("d", d);
		query.executeUpdate();
	}

	@Override
	public ShapeDocument loadDetails(ShapeDocument sd) {
		sd.getTaxonomyTermShape().getId();
		sd.getCreator().getName();
		sd.getDocument().getId();
		return sd;
	}
}
