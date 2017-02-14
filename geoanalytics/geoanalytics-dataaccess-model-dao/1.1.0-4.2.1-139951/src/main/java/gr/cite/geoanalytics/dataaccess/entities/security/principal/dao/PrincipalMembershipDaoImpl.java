package gr.cite.geoanalytics.dataaccess.entities.security.principal.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.ActiveStatus;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalClass;
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
	public List<String> listPrincipalNamesOfProjectGroupByNameAndTenant(
			String tenant, String groupName){
		
		List<String> principalNames = null;
		StringBuilder queryB = new StringBuilder();
		queryB.append("SELECT pm.member.name FROM PrincipalMembership pm");
		queryB.append(" WHERE pm.group.name = :groupName");
		queryB.append(" AND pm.group.tenant.name = :tenant");
		
		TypedQuery<String> query = entityManager.createQuery(queryB.toString(), String.class);
		query.setParameter("tenant", tenant);
		query.setParameter("groupName", groupName);
		
		principalNames = query.getResultList();
		if(principalNames == null){
			principalNames = new ArrayList<String>();
		}
		return principalNames;
	}

	@Override
	public PrincipalMembership loadDetails(PrincipalMembership pm) {
		pm.getGroup().getName();
		pm.getMember().getName();
		return pm;
	}

	@Override
	public void deletePrincipalGroupMembers(Principal projectGroup){
		StringBuilder queryB = new StringBuilder("delete from PrincipalMembership pm where pm.group = :projectGroup");
		
		Query query = entityManager.createQuery(queryB.toString());
		query.setParameter("projectGroup", projectGroup);
		query.executeUpdate();
	}

}