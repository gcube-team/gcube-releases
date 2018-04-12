package gr.cite.geoanalytics.dataaccess.entities.plugin.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.Query;
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
	public List<PluginConfiguration> findByPluginAndProject(UUID pluginID, Project project) {
		String queryString = "FROM PluginConfiguration plconf WHERE plconf.project = :project AND plconf.plugin.id = :pluginID";
		
		TypedQuery<PluginConfiguration> query = entityManager.createQuery(queryString, PluginConfiguration.class);
		query.setParameter("project", project);
		query.setParameter("pluginID", pluginID);
		
		List<PluginConfiguration> result = null;
		
		try {
			result = query.getResultList();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	@Override
	public void deletePluginConfigurationByPluginID(UUID pluginID) {
		String queryString = "DELETE PluginConfiguration plconf WHERE plconf.plugin.id = :pluginID";
		
		Query query = entityManager.createQuery(queryString);
		query.setParameter("pluginID", pluginID);
		
		query.executeUpdate();
	}
}
