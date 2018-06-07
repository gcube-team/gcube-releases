package gr.cite.geoanalytics.dataaccess.entities.plugin.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.plugin.PluginConfiguration;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;

public interface PluginConfigurationDao extends Dao<PluginConfiguration, UUID>  {
	public List<PluginConfiguration> findByPluginAndProject(UUID plugin, Project project);
	public void deletePluginConfigurationByPluginID(UUID pluginID);
}
