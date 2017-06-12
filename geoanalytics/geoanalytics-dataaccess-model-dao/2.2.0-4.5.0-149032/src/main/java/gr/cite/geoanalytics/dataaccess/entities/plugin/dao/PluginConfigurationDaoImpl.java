package gr.cite.geoanalytics.dataaccess.entities.plugin.dao;

import java.util.UUID;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.plugin.Plugin;
import gr.cite.geoanalytics.dataaccess.entities.plugin.PluginConfiguration;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;

@Repository
public class PluginConfigurationDaoImpl extends JpaDao<PluginConfiguration, UUID> implements PluginConfigurationDao {

	@Override
	public PluginConfiguration loadDetails(PluginConfiguration t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PluginConfiguration findByPluginAndProject(Plugin plugin, Project project) {
		String queryString = "FROM PluginConfiguration plconf WHERE plconf.project = :project AND plconf.plugin = :plugin";
		
		TypedQuery<PluginConfiguration> query = entityManager.createQuery(queryString, PluginConfiguration.class);
		query.setParameter("project", project);
		query.setParameter("plugin", plugin);
		
		PluginConfiguration result = null;
		
		try {
			result = query.getSingleResult();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
