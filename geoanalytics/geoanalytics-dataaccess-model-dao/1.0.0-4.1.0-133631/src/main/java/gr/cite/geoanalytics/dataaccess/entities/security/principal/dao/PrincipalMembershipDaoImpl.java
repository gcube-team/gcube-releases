package gr.cite.geoanalytics.dataaccess.entities.security.principal.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.ActiveStatus;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalMembership;

@Repository
public class PrincipalMembershipDaoImpl extends JpaDao<PrincipalMembership, UUID> implements PrincipalMembershipDao {
	
	@Override
	public List<Principal> findRolesByPrincipal(Principal principal) {
		List<PrincipalMembership> result = null;

		StringBuilder queryB = new StringBuilder("from PrincipalMembership pm where pm.member = :p");
		queryB.append(" and pm.member.isActive = :active");
		TypedQuery<PrincipalMembership> query = entityManager.createQuery(queryB.toString(), PrincipalMembership.class);

		query.setParameter("p", principal);
		query.setParameter("active", ActiveStatus.ACTIVE.code());
		
		result = query.getResultList();

		List<Principal> roles = new ArrayList<Principal>();
		for(PrincipalMembership pm : result)
			roles.add(pm.getGroup());
		return roles;
	}

	@Override
	public List<PrincipalMembership> findPrincipalMembershipByUser(Principal principal) {
		List<PrincipalMembership> result = null;

		StringBuilder queryB = new StringBuilder("from PrincipalMembership pm where pm.member = :p");
		queryB.append(" and pm.member.isActive = :active");
		TypedQuery<PrincipalMembership> query = entityManager.createQuery(queryB.toString(), PrincipalMembership.class);

		query.setParameter("p", principal);
		query.setParameter("active", ActiveStatus.ACTIVE.code());
		
		result = query.getResultList();

		if (result == null) return new ArrayList<>();
		return result;
	}

	@Override
	public PrincipalMembership loadDetails(PrincipalMembership pm) {
		pm.getGroup().getName();
		pm.getMember().getName();
		return pm;
	}

}