package gr.cite.geoanalytics.dataaccess.entities.security.accesscontrol.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.principal.AccessControl;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;

public interface AccessControlDao extends Dao<AccessControl, UUID> {
	AccessControl findByPrincipalUUIDAndEntity(UUID principalId, UUID entityId);
	AccessControl findByPrincipalAndEntity(Principal principal, UUID entityId);
	AccessControl findByPrincipal(Principal principal);
	
	List<AccessControl> findByEntity(UUID entityId);

	Boolean hasPrincipalPermissionForEntity(Principal principal, UUID entityId);
	
	Boolean hasPrincipalReadPermissionForEntity(Principal principal, UUID entityId);

	Boolean hasPrincipalEditPermissionForEntity(Principal principal, UUID entityId);

	Boolean hasPrincipalDeletePermissionForEntity(Principal principal, UUID entityId);
	
	void deleteByEntityId(UUID accessControlId);
	void deleteByPrincipalUUIDsAndEntityId(List<UUID> principalID, UUID entityId);
	void deleteByPrincipalAndEntityId(Principal principal, UUID entityId);
	void deleteByPrincipalId(UUID id) throws Exception;
}
