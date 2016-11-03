package gr.cite.geoanalytics.dataaccess.entities.security.principal.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalMembership;

public interface PrincipalMembershipDao extends Dao<PrincipalMembership, UUID> {
	public List<Principal> findRolesByPrincipal(Principal principal);
	public List<PrincipalMembership> findPrincipalMembershipByUser(Principal principal);
}
