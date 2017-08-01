package gr.cite.geoanalytics.dataaccess.entities.tenant.dao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.tenant.TenantActivation;

public interface TenantActivationDao extends Dao<TenantActivation, UUID>
{
	public List<TenantActivation> findActive(Tenant c);
	public List<TenantActivation> findActiveActivations(Tenant c);
	public List<TenantActivation> findAll(Tenant c);
	
	public List<TenantActivation> findWithin(Date start, Date end);
	public List<TenantActivation> findWithin(Tenant c, Date start, Date end);
	
	public List<TenantActivation> findWithinActive(Date start, Date end);
	public List<TenantActivation> findWithinActive(Tenant c, Date start, Date end);
}
