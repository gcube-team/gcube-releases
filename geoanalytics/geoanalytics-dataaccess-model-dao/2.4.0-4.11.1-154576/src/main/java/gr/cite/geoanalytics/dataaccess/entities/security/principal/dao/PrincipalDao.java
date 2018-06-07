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
	
	public List<Principal> findByClass(PrincipalClass classType, ActiveStatus activeStatus, String tenant);
	public List<Principal> findByClass(PrincipalClass classType, ActiveStatus activeStatus);
	public List<Principal> findByClass(PrincipalClass classType);
	
	public List<Principal> findByClassAndCreatorAndTenantAndStatus(PrincipalClass classType, ActiveStatus activeStatus, UUID tenantID, Principal principal);
	
	public Principal findProjectCreatorByProjectId(UUID projectId);
	
	public Principal findActivePrincipalByName(String name);
	public Principal findPrincipalByNameAndActivityStatus(String name, ActiveStatus activeStatus);

	public Principal systemPrincipal();
	
	public Principal findPrincipalByNameTenantAndActiveStatus(String principalName, String tenantName, ActiveStatus activeStatus);
	public List<Principal> findPrincipalByNameListTenantAndActiveStatus(List<String> principalName, String tenantName, ActiveStatus activeStatus);
	public Principal findActivePrincipalByNameAndTenant(String principalName, String tenantName);
	public Principal findPrincipalByNameAndTenant(String principalName, String tenantName);

	public List<String> listActivePrincipalNames();
	public List<String> listPrincipalNames();
	public List<String> listPrincipalNamesByTenant(Tenant tenant);

	public List<String> searchByPrincipalNamesAndTenant(List<String> principalNames, Tenant tenant);

	public List<PrincipalProjectInfoDao> findByClassReturnsPrincipalProjectInfo(PrincipalClass classType,
			ActiveStatus activeStatus, Tenant tenant);
	public List<PrincipalProjectInfoDao> retrieveProjectGroupByTenant(PrincipalClass classType,
			ActiveStatus activeStatus, Tenant tenant, Principal principal);

	public Principal findByClassTenantAndName(PrincipalClass classType, ActiveStatus activeStatus, String tenant,
			String principalName);
	
	public Principal findByVreIdAndTenant(UUID vreId, Tenant tenant) throws Exception;
	
	public Principal findByVreIdAndTenantID(UUID vreId, UUID tenantID) ;
}
