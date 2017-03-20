package gr.cite.geoanalytics.dataaccess.entities.security.accesscontrol.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.principal.AccessControl;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;

@Repository
public class AccessControlDaoImpl extends JpaDao<AccessControl, UUID> implements AccessControlDao {

	@Override
	public AccessControl findByPrincipalUUIDAndEntity(UUID principalId, UUID entityId) {
		
		StringBuilder queryB = new StringBuilder();
		queryB.append("FROM AccessControl ac");
		queryB.append(" WHERE ac.principal.id = :principalId");
		queryB.append(" AND ac.entity = :entityId");
		
		TypedQuery<AccessControl> query = entityManager.createQuery(queryB.toString(), AccessControl.class);
		query.setParameter("principalId", principalId);
		query.setParameter("entityId", entityId);
		
		AccessControl result =  null;
		result = query.getSingleResult();
		
		return result;
	}
	
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
	public AccessControl findByPrincipal(Principal principal) {
		
		StringBuilder queryB = new StringBuilder("from AccessControl ac where ac.principal = :principal");
		
		TypedQuery<AccessControl> query = entityManager.createQuery(queryB.toString(), AccessControl.class);
		query.setParameter("principal", principal);
		
		AccessControl result =  null;
		try {
			result = query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
		
		return result;
	}

	@Override
	public List<AccessControl> findByEntity(UUID entityId) {
		
		StringBuilder queryB = new StringBuilder("from AccessControl ac where ac.entity = :entity");
		
		TypedQuery<AccessControl> query = entityManager.createQuery(queryB.toString(), AccessControl.class);
		query.setParameter("entity", entityId);
		
		List<AccessControl> result =  new ArrayList<AccessControl>();
		try {
			result = query.getResultList();
		}catch(NoResultException e) {
			return new ArrayList<AccessControl>();
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

	@Override
	public Boolean hasPrincipalReadPermissionForEntity(Principal principal, UUID entityId) {
		Boolean result = false;
		StringBuilder queryB = new StringBuilder();
		queryB.append("SELECT ac FROM AccessControl ac, PrincipalMembership pm");
		queryB.append(" WHERE ((ac.principal = :principal AND ac.entity = :entity AND ac.readRight = 1)");
		queryB.append(" OR (pm.member = :principal AND pm.group = ac.principal AND ac.entity = :entity AND ac.readRight = 1))");
		
		TypedQuery<AccessControl> query = entityManager.createQuery(queryB.toString(), AccessControl.class);
		query.setParameter("principal", principal);
		query.setParameter("entity", entityId);
		
		result = !(query.getResultList().isEmpty());
		
		return result;
	}

	@Override
	public Boolean hasPrincipalEditPermissionForEntity(Principal principal, UUID entityId) {
		Boolean result = false;
		StringBuilder queryB = new StringBuilder();
		queryB.append("SELECT ac FROM AccessControl ac, PrincipalMembership pm");
		queryB.append(" WHERE ((ac.principal = :principal AND ac.entity = :entity AND ac.editRight = 1)");
		queryB.append(" OR (pm.member = :principal AND pm.group = ac.principal AND ac.entity = :entity AND ac.editRight = 1))");
		
		TypedQuery<AccessControl> query = entityManager.createQuery(queryB.toString(), AccessControl.class);
		query.setParameter("principal", principal);
		query.setParameter("entity", entityId);
		
		result = !(query.getResultList().isEmpty());
		
		return result;
	}

	@Override
	public Boolean hasPrincipalDeletePermissionForEntity(Principal principal, UUID entityId) {
		Boolean result = false;
		StringBuilder queryB = new StringBuilder();
		queryB.append("SELECT ac FROM AccessControl ac, PrincipalMembership pm");
		queryB.append(" WHERE ((ac.principal = :principal AND ac.entity = :entity AND ac.deleteRight = 1)");
		queryB.append(" OR (pm.member = :principal AND pm.group = ac.principal AND ac.entity = :entity AND ac.deleteRight = 1))");
		
		TypedQuery<AccessControl> query = entityManager.createQuery(queryB.toString(), AccessControl.class);
		query.setParameter("principal", principal);
		query.setParameter("entity", entityId);
		
		result = !(query.getResultList().isEmpty());
		
		return result;
	}

	@Override
	public void deleteByEntityId(UUID accessControlId) {
		StringBuilder queryStr = new StringBuilder();
		queryStr.append("DELETE FROM AccessControl ac WHERE ac.entity = :accessControlId");
		
		Query query = entityManager.createQuery(queryStr.toString());
		query.setParameter("accessControlId", accessControlId);
		query.executeUpdate();
	}

	@Override
	public void deleteByPrincipalAndEntityId(Principal principal, UUID entityId) {
		StringBuilder queryStr = new StringBuilder();
		queryStr.append("DELETE FROM AccessControl ac WHERE ac.entity = :entityId AND ac.principal = :principal");
		
		Query query = entityManager.createQuery(queryStr.toString());
		query.setParameter("accessControlId", entityId);
		query.setParameter("principal", principal);
		query.executeUpdate();
	}

	@Override
	public void deleteByPrincipalUUIDsAndEntityId(List<UUID> principalID, UUID entityId) {
		StringBuilder queryStr = new StringBuilder();
		queryStr.append("DELETE FROM AccessControl ac");
		queryStr.append(" WHERE ac.entity = :entityId");
		queryStr.append(" AND ac.principal.id IN :principalID");
		
		Query query = entityManager.createQuery(queryStr.toString());
		query.setParameter("entityId", entityId);
		query.setParameter("principalID", principalID);
		query.executeUpdate();
	}
}
