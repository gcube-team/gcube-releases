package gr.cite.geoanalytics.dataaccess.entities.auditing.dao;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing;
import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing.AuditingType;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.exception.DataLayerException;

import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class AuditingDaoImpl extends JpaDao<Auditing, UUID> implements AuditingDao 
{
	private static Logger log = LoggerFactory.getLogger(AuditingDaoImpl.class);
	
	@Override
	public List<Auditing> findByType(Auditing.AuditingType type) {
		List<Auditing> result = null;
		
		TypedQuery<Auditing> query = entityManager.createQuery("from Auditing where type= :typeCode", Auditing.class);
		query.setParameter("typeCode", type.typeCode());
		
		result = query.getResultList();
			
		log.debug("Find auditing entries with type: " + type);
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null) {
			for (Auditing a : (List<Auditing>) result) {
				log.debug("Auditing (" + a.getId() + ")");
			}
		}
		validateByType(result, type, null);
		
		return result;
	}
	
	@Override
	public List<Auditing> findByTypeOrdered(Auditing.AuditingType type) {
		List<Auditing> result = null;
		
		TypedQuery<Auditing> query = entityManager.createQuery("from Auditing where type= :typeCode order by date desc", Auditing.class);
		query.setParameter("typeCode", type.typeCode());
		
		result = query.getResultList();
			
		log.debug("Find auditing entries with type: " + type);
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null) {
			for (Auditing a : (List<Auditing>) result) {
				log.debug("Auditing (" + a.getId() + ")");
			}
		}
		validateByType(result, type, null);
		
		return result;
	}
	
	
	@Override
	public long countByType(Auditing.AuditingType type) {
		TypedQuery<Long> query = entityManager.createQuery("select count(aud) from Auditing aud where aud.type= :typeCode", Long.class);
		query.setParameter("typeCode", type.typeCode());
		
		return query.getSingleResult();
	}
	
	@Override
	public Auditing findByTypeAndCreator(Auditing.AuditingType type, Principal principal) {
		List<Auditing> result = null;
		
		TypedQuery<Auditing> query = entityManager.createQuery("from Auditing where type= :typeCode and creator= :creator", Auditing.class);
		query.setParameter("typeCode", type.typeCode());
		query.setParameter("creator", principal);
		
		result = query.getResultList();
			
		log.debug("Find accounting entries with type: " + type + " and creator: " + principal);
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null) {
			for (Auditing a : (List<Auditing>) result) {
				log.debug("Auditing (" + a.getId() + ")");
			}
		}
		validateByType(result, type, principal);
		
		if(result.isEmpty()) return null;
		return result.get(0);
	}
	
	@Override
	public Auditing findByTypeAndUser(Auditing.AuditingType type, Principal principal) {
		List<Auditing> result = null;
		
		TypedQuery<Auditing> query = entityManager.createQuery("from Auditing where type= :typeCode and principal= :principal", Auditing.class);
		query.setParameter("typeCode", type.typeCode());
		query.setParameter("principal", principal);
		
		result = query.getResultList();
			
		log.debug("Find accounting entries with type: " + type + " and user: " + principal.getName());
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null) {
			for (Auditing a : (List<Auditing>) result) {
				log.debug("Auditing (" + a.getId() + ")");
			}
		}
		validateByType(result, type, principal);
		
		if(result.isEmpty()) return null;
		return result.get(0);
	}
	
	@Override
	public Auditing findMostRecentByType(Auditing.AuditingType type) {
		TypedQuery<Auditing> query = entityManager.createQuery("from Auditing where type= :typeCode order by date desc", Auditing.class);
		query.setParameter("typeCode", type.typeCode());
		
		query.setMaxResults(1);
		
		try {
			return query.getSingleResult();
		}catch(NoResultException e)
		{
			return null;
		}
	}
	
	private void validateByType(List<Auditing> result, AuditingType type, Principal principal) {
		switch(type) {
		case LastDataUpdate:
			if(result != null && result.size() > 1) throw new DataLayerException("Illegal state. Found more than one accounting entries for type " + type);
			break;
		case LastUserAction:
			if(result != null && principal != null && result.size() > 1) throw new DataLayerException("Illegal state. Found more than one accounting entries for type " + type);
		}
	}
	
	@Override
	public Auditing findLastDataUpdate() {
		List<Auditing> result = null;
		
		TypedQuery<Auditing> query = entityManager.createQuery("from Auditingwhere type= :typeCode", Auditing.class);
		query.setParameter("typeCode", AuditingType.LastDataUpdate.typeCode());
		
		result = query.getResultList();
			
		log.debug("Find accounting entries with type: " + AuditingType.LastDataUpdate);
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (Auditing a : (List<Auditing>) result) {
				log.debug("Auditing (" + a.getId() + ")");
			}
		}
		if(result != null && result.size() > 1) 
			throw new DataLayerException("Illegal state. Found more than one auditing entries with type " + AuditingType.LastDataUpdate);
		
		return result.get(0);
	}

	@Override
	public Auditing loadDetails(Auditing auditing) {
		auditing.getCreator().getName();
		if(auditing.getPrincipal() != null)
			auditing.getPrincipal().getId();
		if(auditing.getTenant() != null)
			auditing.getTenant().getId();
		return auditing;
	}
}
