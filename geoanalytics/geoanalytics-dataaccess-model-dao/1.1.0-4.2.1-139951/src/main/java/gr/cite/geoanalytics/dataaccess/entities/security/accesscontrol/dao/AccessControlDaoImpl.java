package gr.cite.geoanalytics.dataaccess.entities.security.accesscontrol.dao;

import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.principal.AccessControl;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;

@Repository
public class AccessControlDaoImpl extends JpaDao<AccessControl, UUID> implements AccessControlDao {
	
	@Override
	public AccessControl findByPrincipalAndEntity (Principal principal, UUID entity) {
		
		StringBuilder queryB = new StringBuilder("from AccessControl ac where ac.principal = :principal and ac.entity = :entity");
		
		TypedQuery<AccessControl> query = entityManager.createQuery(queryB.toString(), AccessControl.class);
		query.setParameter("principal", principal);
		query.setParameter("entity", entity);
		
		AccessControl result =  null;
		try {
			result = query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
		
		return result;
	}
	
	@Override
	public Boolean hasPrincipalPermissionForEntity(Principal principal, UUID entity) {
		Boolean result = false;
		StringBuilder queryB = new StringBuilder("select ac from AccessControl ac, PrincipalMembership pm where ((ac.principal = :principal and ac.entity = :entity)");
		queryB.append(" or (pm.member = :principal and pm.group = ac.principal and ac.entity = :entity))");
		
		TypedQuery<AccessControl> query = entityManager.createQuery(queryB.toString(), AccessControl.class);
		query.setParameter("principal", principal);
		query.setParameter("entity", entity);
		
		result = !(query.getResultList().isEmpty());
		
		return result;
	}

	@Override
	public AccessControl loadDetails(AccessControl ac) {
		ac.getPrincipal().getName();
		return ac;
	}

}
