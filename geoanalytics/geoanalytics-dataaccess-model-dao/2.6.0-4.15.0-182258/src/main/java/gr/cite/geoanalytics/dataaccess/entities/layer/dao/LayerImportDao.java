package gr.cite.geoanalytics.dataaccess.entities.layer.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerImport;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;

public interface LayerImportDao extends Dao<LayerImport, UUID> {
	public List<LayerImport> findLayerImportsOfPrincipal(Principal principal);
}
