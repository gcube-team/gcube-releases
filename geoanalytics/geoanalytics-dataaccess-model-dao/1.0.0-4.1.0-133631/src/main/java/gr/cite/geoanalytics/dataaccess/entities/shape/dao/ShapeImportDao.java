package gr.cite.geoanalytics.dataaccess.entities.shape.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeImport;

public interface ShapeImportDao extends Dao<ShapeImport, UUID>
{
	public List<ShapeImport> getImport(UUID importId);
	public List<ShapeImport> findByIdentity(String identity);
	public List<UUID> listImports();
}
