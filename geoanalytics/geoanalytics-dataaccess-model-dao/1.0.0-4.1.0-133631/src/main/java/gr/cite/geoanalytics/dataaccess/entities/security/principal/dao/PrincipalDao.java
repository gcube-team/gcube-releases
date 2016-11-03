package gr.cite.geoanalytics.dataaccess.entities.security.principal.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.ActiveStatus;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalClass;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

public interface PrincipalDao extends Dao<Principal, UUID> {
	public List<Principal> findActivePrincipals();
	
	public List<Principal> findByClass(PrincipalClass classType, ActiveStatus activeStatus);
	public List<Principal> findByClass(PrincipalClass classType);
	
	public Principal findActivePrincipalByName(String name);
	public Principal findPrincipalByNameAndActivityStatus(String name, ActiveStatus activeStatus);

	public Principal systemPrincipal();
	
	public Principal findPrincipalByNameTenantAndActiveStatus(String principalName, String tenantName, ActiveStatus activeStatus);
	public Principal findActivePrincipalByNameAndTenant(String principalName, String tenantName);
	public Principal findPrincipalByNameAndTenant(String principalName, String tenantName);

	public List<String> listActivePrincipalNames();
	public List<String> listPrincipalNames();
	public List<String> listPrincipalNamesByTenant(Tenant tenant);

	public List<String> searchByPrincipalNamesAndTenant(List<String> principalNames, Tenant tenant);

	public List<PrincipalProjectInfoDao> findByClassReturnsPrincipalProjectInfo(PrincipalClass classType,
			ActiveStatus activeStatus);
}
