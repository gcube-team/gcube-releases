package gr.cite.geoanalytics.dataaccess.entities.plugin.dao;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.plugin.Plugin;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

public interface PluginDao extends Dao<Plugin, UUID> {
	public void deleteAll() throws Exception;
	public List<PluginInfoDaoUtil> listPluginsByTenantOrNullTenant(Tenant tenant);
	public List<Plugin> listPluginsByTenant(Tenant tenant);
	
	public String getPluginMetadataByID(UUID pluginID);
	public String getPluginNameByID(UUID pluginID);
	
	public Plugin getPluginByNameAndTenantName(String pluginName, String tenantName);
	public void deletePluginByPluginID(UUID pluginID);
}
