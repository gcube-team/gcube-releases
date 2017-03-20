package gr.cite.geoanalytics.dataaccess.entities.plugin.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.plugin.Plugin;

/**
 * @author vfloros
 *
 */

@Repository
public class PluginDaoImpl extends JpaDao<Plugin, UUID> implements PluginDao {

	@Override
	public Plugin loadDetails(Plugin t) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Plugin> listPluginsByTenant(String tenantName) {
		TypedQuery<Plugin> query = entityManager.createQuery("FROM Plugin pl WHERE pl.tenant.name = :tenantName", Plugin.class);
		query.setParameter("tenantName", tenantName);
		
		List<Plugin> result = null;
		try {
			result = query.getResultList();
		} catch(Exception e){
			result = new ArrayList<Plugin>();
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public Plugin getPluginByNameAndTenantName(String pluginName, String tenantName) {
		Plugin result = null;
		
		try {
			String queryString = "FROM Plugin pl WHERE pl.name = :pluginName AND pl.tenant.name = :tenantName";
			TypedQuery<Plugin> query = entityManager.createQuery(queryString, Plugin.class);
			query.setParameter("pluginName", pluginName);
			query.setParameter("tenantName", tenantName);

			result = query.getSingleResult();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
