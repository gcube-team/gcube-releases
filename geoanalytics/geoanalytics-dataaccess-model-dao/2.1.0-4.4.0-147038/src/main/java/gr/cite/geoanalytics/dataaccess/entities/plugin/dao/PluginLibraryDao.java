package gr.cite.geoanalytics.dataaccess.entities.plugin.dao;

import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.plugin.PluginLibrary;

public interface PluginLibraryDao extends Dao<PluginLibrary, UUID> {
	public void deleteAll() throws Exception;

}
