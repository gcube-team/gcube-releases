package gr.cite.geoanalytics.dataaccess.entities.security.accessright.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.principal.AccessRight;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;

public interface AccessRightDao extends Dao<AccessRight, UUID> {
	public List<AccessRight> findByPrincipal(Principal principal);
	public List<AccessRight> findByRight(UUID rightId);
	public AccessRight findByPrincipalAndRight(Principal principal, UUID rightId);
}
