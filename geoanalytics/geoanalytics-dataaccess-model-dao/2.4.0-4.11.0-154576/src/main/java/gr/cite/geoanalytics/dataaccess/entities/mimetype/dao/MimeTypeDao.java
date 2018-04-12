package gr.cite.geoanalytics.dataaccess.entities.mimetype.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.mimetype.MimeType;

public interface MimeTypeDao extends Dao<MimeType, UUID>
{
	public List<MimeType> findByExtension(String extension);
}
