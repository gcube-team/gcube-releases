package gr.cite.geoanalytics.dataaccess.entities.security.accessright.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.principal.AccessRight;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;

@Repository
public class AccessRightDaoImpl extends JpaDao<AccessRight, UUID> implements AccessRightDao {
	
	@Override
	public List<AccessRight> findByPrincipal(Principal principal) {
		
		StringBuilder queryB = new StringBuilder("from AccessRight ar where ar.principal = :principal");
		
		TypedQuery<AccessRight> query = entityManager.createQuery(queryB.toString(), AccessRight.class);
		query.setParameter("principal", principal);
		
		List<AccessRight> result =  query.getResultList();
		
		if(result == null) return new ArrayList<AccessRight>();
		return result;
	}
	
	@Override
	public List<AccessRight> findByRight(UUID right) {
		
		StringBuilder queryB = new StringBuilder("from AccessRight ar where ar.right = :right");
		
		TypedQuery<AccessRight> query = entityManager.createQuery(queryB.toString(), AccessRight.class);
		query.setParameter("right", right);
		
		List<AccessRight> result =  query.getResultList();
		
		if(result == null) return new ArrayList<AccessRight>();
		return result;
	}
	
	@Override
	public AccessRight findByPrincipalAndRight(Principal principal, UUID right) {
		
		StringBuilder queryB = new StringBuilder("from AccessRight ar where ar.principal = :principal and ar.right = :right");
		
		TypedQuery<AccessRight> query = entityManager.createQuery(queryB.toString(), AccessRight.class);
		query.setParameter("principal", principal);
		query.setParameter("right", right);
		
		AccessRight result =  null;
		try {
			result = query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
		
		return result;
	}

	@Override
	public AccessRight loadDetails(AccessRight ar) {
		ar.getPrincipal().getName();
		return ar;
	}

}
