package gr.cite.geoanalytics.dataaccess.entities.shape.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeDocument;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermShape;

public interface ShapeDocumentDao extends Dao<ShapeDocument, UUID>
{
	public ShapeDocument find(TaxonomyTermShape tts, Document d);
	public List<ShapeDocument> findByDocument(Document d);
	public List<ShapeDocument> findByTaxonomyTermShape(TaxonomyTermShape tts);
	public List<Document> findDocumentsByTaxonomyTermShape(TaxonomyTermShape tts);
	public Document findUniqueByTaxonomyTermShape(TaxonomyTermShape tts);
	public void deleteByTaxonomyTermShape(TaxonomyTermShape tts);
	public void deleteByDocument(Document d);	
}
