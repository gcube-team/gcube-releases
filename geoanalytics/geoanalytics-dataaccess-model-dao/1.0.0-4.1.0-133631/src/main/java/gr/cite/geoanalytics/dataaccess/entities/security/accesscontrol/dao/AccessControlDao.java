package gr.cite.geoanalytics.dataaccess.entities.security.accesscontrol.dao;

import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.principal.AccessControl;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;

public interface AccessControlDao extends Dao<AccessControl, UUID> {
	
	AccessControl findByPrincipalAndEntity(Principal principal, UUID entityId);

	Boolean hasPrincipalPermissionForEntity(Principal principal, UUID entityId);
}
