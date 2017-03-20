package gr.cite.geoanalytics.dataaccess.entities.plugin.dao;

import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.plugin.Plugin;
import gr.cite.geoanalytics.dataaccess.entities.plugin.PluginConfiguration;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;

public interface PluginConfigurationDao extends Dao<PluginConfiguration, UUID>  {
	public PluginConfiguration findByPluginAndProject(Plugin plugin, Project project);
}
