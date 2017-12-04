package gr.cite.geoanalytics.dataaccess.entities.tenant.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

public interface TenantDao extends Dao<Tenant, UUID>
{
	public List<Tenant> findByName(String name);
	public List<String> listNames();
	
	public List<String> listNamesByCode(String code);
	public List<Tenant> findByCode(String code);
	
	public List<Tenant> searchByName(List<String> names);
	public Tenant getTenantByUUID(UUID id);
}
