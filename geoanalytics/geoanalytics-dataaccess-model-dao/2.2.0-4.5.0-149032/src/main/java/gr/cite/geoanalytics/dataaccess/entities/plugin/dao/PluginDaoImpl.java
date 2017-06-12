package gr.cite.geoanalytics.dataaccess.entities.plugin.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;


import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.plugin.Plugin;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

/**
 * @author vfloros
 *
 */

@Repository
public class PluginDaoImpl extends JpaDao<Plugin, UUID> implements PluginDao {
	private static final Logger log = LoggerFactory.getLogger(PluginDaoImpl.class);

	@Override
	public void deleteAll() throws Exception {
		log.debug("Deleting all plugins");
		
		StringBuilder queryB = new StringBuilder("");
		queryB.append("DELETE FROM Plugin");
		
		Query query = entityManager.createQuery(queryB.toString());
		query.executeUpdate();
	}

	@Override
	public Plugin loadDetails(Plugin t) {
		return null;
	}
	
	@Override
	public List<Plugin> listPluginsByTenant(Tenant tenant) {
		log.debug("Listing plugins by tenant: " + tenant.getName());
		
//		TypedQuery<Plugin> query = entityManager.createQuery("FROM Plugin pl WHERE pl.tenant = :tenant", Plugin.class);
TypedQuery<Plugin> query = entityManager.createQuery("FROM Plugin pl WHERE pl.tenant = :tenant", Plugin.class);
		
		query.setParameter("tenant", tenant);
		
		List<Plugin> result = null;
		try {
			result = query.getResultList();
			log.debug( result.size() + " plugins were retrieved for tenant: " + tenant.getName());
		} catch(Exception e){
			result = new ArrayList<Plugin>();
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public List<PluginInfoDaoUtil> listPlugins(Tenant tenant) {
		log.debug("Listing plugins by tenant: " + tenant.getName() + " or no tenant at all");
		StringBuilder queryStr = new StringBuilder();
		queryStr.append("SELECT new gr.cite.geoanalytics.dataaccess.entities.plugin.dao.PluginInfoDaoUtil(p.id, p.name, p.descrtiption)");
		queryStr.append(" FROM Plugin p WHERE p.tenant = :tenant OR p.tenant IS NULL");
		queryStr.append(" GROUP BY p.id, p.name, p.descrtiption");
		
		TypedQuery<PluginInfoDaoUtil> query = entityManager.createQuery(queryStr.toString(), PluginInfoDaoUtil.class);
		query.setParameter("tenant", tenant);
		
		List<PluginInfoDaoUtil> result = null;
		try {
			result = query.getResultList();
			log.debug( result.size() + " plugins were retrieved");
		} catch(Exception e){
			result = new ArrayList<PluginInfoDaoUtil>();
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public Plugin getPluginByNameAndTenantName(String pluginName, String tenantName) {
		log.debug("Attempting to retrieve plugin : " + pluginName + " under the tenant: " + tenantName);
		Plugin result = null;
		
		try {
			String queryString = "FROM Plugin pl WHERE pl.name = :pluginName AND pl.tenant.name = :tenantName";
			TypedQuery<Plugin> query = entityManager.createQuery(queryString, Plugin.class);
			query.setParameter("pluginName", pluginName);
			query.setParameter("tenantName", tenantName);

			result = query.getSingleResult();
			
			log.debug("Plugin: " + pluginName + " was retrieved successfully");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public String getPluginMetadataByID(UUID pluginID) {
		log.debug("Attempting to retrieve plugin metadata by ID: " + pluginID);
		StringBuilder queryStr = new StringBuilder();
		queryStr.append("SELECT p.metadata FROM Plugin p WHERE p.id =:pluginID");
		
		String result = null;
		
		try {
			Query query = entityManager.createQuery(queryStr.toString());
			query.setParameter("pluginID", pluginID);
			result = (String) query.getSingleResult();
			
			log.debug("Successfully retrieved plugin metadata by ID: " + pluginID);
		} catch(Exception e){
			log.debug("Failed to retrieve plugin metadata by ID: " + pluginID);
			e.printStackTrace();
		}
		
		return result;
		
	}
	
}
