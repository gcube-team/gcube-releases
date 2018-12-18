package gr.cite.geoanalytics.dataaccess.entities.shape.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeDocument;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.GeocodeShape;

public interface ShapeDocumentDao extends Dao<ShapeDocument, UUID>
{
//	public ShapeDocument find(GeocodeShape tts, Document d);
	public List<ShapeDocument> findByDocument(Document d);
//	public List<ShapeDocument> findByGeocodeShape(GeocodeShape tts);
//	public List<Document> findDocumentsByGeocodeShape(GeocodeShape tts);
//	public Document findUniqueByGeocodeShape(GeocodeShape tts);
//	public void deleteByGeocodeShape(GeocodeShape tts);
	public void deleteByDocument(Document d);	
}
