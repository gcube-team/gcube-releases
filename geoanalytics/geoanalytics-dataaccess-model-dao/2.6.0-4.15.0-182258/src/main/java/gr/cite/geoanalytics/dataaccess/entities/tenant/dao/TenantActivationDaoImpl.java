package gr.cite.geoanalytics.dataaccess.entities.tenant.dao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.tenant.TenantActivation;

@Repository
public class TenantActivationDaoImpl extends JpaDao<TenantActivation, UUID> implements TenantActivationDao {
	
	private static final Logger log = LoggerFactory.getLogger(TenantActivationDaoImpl.class);
	@Override
	public List<TenantActivation> findActive(Tenant t) {
		List<TenantActivation> result = null;
		
		TypedQuery<TenantActivation> query = entityManager.createQuery(
				"from TenantActivation ca where ca.isActive = 1 and ca.tenant = :t and ca.start < :now and ca.end > :now",
				TenantActivation.class);
		query.setParameter("t", t);
		query.setParameter("now", new Date());
		result = query.getResultList();
		
		log.debug("Find tenant activation");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (TenantActivation ca : (List<TenantActivation>) result) {
				log.debug("TenantActivation (" + ca.getTenant().getName() + 
						" start: " + ca.getStart().toString() +
						" end: " + ca.getEnd().toString() +
						((ca.getShapeID() != null) ? ("shape: " + ca.getShapeID().toString()) : "for all data"));
			}
		}
	
		return result;
	}
	
	@Override
	public List<TenantActivation> findAll(Tenant t) {
		List<TenantActivation> result = null;
		
		TypedQuery<TenantActivation> query = entityManager.createQuery("from TenantActivation ca where ca.tenant = :t", TenantActivation.class);
		query.setParameter("t", t);
		result = query.getResultList();
		
		log.debug("Find tenant activation");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (TenantActivation ca : (List<TenantActivation>) result) {
				log.debug("TenantActivation (" + ca.getTenant().getName() + 
						" start: " + ca.getStart().toString() +
						" end: " + ca.getEnd().toString() +
						((ca.getShapeID() != null) ? ("shape: " + ca.getShapeID().toString()) : "for all data"));
			}
		}
	
		return result;
	}
	
	@Override
	public List<TenantActivation> findActiveActivations(Tenant t)
	{
		List<TenantActivation> result = null;
		
		TypedQuery<TenantActivation> query = entityManager.createQuery(
				"from TenantActivation ca where ca.tenant = :c and ca.isActive = 1", 
				TenantActivation.class);
		query.setParameter("t", t);
		result = query.getResultList();
		
		log.debug("Find tenant activation");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (TenantActivation ca : (List<TenantActivation>) result) {
				log.debug("TenantActivation (" + ca.getTenant().getName() + 
						" start: " + ca.getStart().toString() +
						" end: " + ca.getEnd().toString() +
						((ca.getShapeID() != null) ? ("shape: " + ca.getShapeID().toString()) : "for all data"));
			}
		}
	
		return result;
	}
	
	@Override
	public List<TenantActivation> findWithin(Date start, Date end) {
		List<TenantActivation> result = null;
		
		TypedQuery<TenantActivation> query = entityManager.createQuery(
				"from TenantActivation ca where and ca.start >= :st and ca.end <= :en", 
				TenantActivation.class);
		query.setParameter("st", start);
		query.setParameter("en", end);
		
		result = query.getResultList();
		
		log.debug("Find tenant activations within date");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null) {
			for (TenantActivation ca : (List<TenantActivation>) result) {
				log.debug("TenantActivation (" + ca.getTenant().getName() + 
						" start: " + ca.getStart().toString() +
						" end: " + ca.getEnd().toString() +
						((ca.getShapeID() != null) ? ("shape: " + ca.getShapeID().toString()) : "for all data"));
			}
		}
	
		return result;
	}
	
	@Override
	public List<TenantActivation> findWithinActive(Date start, Date end) {
		List<TenantActivation> result = null;
		
		TypedQuery<TenantActivation> query = entityManager.createQuery(
				"from TenantActivation ca where ca.isActive=1 and ca.start >= :st and ca.end <= :en", 
				TenantActivation.class);
		query.setParameter("st", start);
		query.setParameter("en", end);
		
		result = query.getResultList();
		
		log.debug("Find tenant activations within date");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null) {
			for (TenantActivation ca : (List<TenantActivation>) result) {
				log.debug("CustomerActivation (" + ca.getTenant().getName() + 
						" start: " + ca.getStart().toString() +
						" end: " + ca.getEnd().toString() +
						((ca.getShapeID() != null) ? ("shape: " + ca.getShapeID().toString()) : "for all data"));
			}
		}
	
		return result;
	}
	
	@Override
	public List<TenantActivation> findWithin(Tenant t, Date start, Date end) {

		List<TenantActivation> result = null;
		
		TypedQuery<TenantActivation> query = entityManager.createQuery("from TenantActivation ca where and ca.start >= :st and " +
																		"ca.end <= :en and ca.tenant = :t", TenantActivation.class);
		query.setParameter("st", start);
		query.setParameter("en", end);
		query.setParameter("t", t);
		
		result = query.getResultList();
		
		log.debug("Find tenant activations for tenant " + t.getName() + " within date");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null) {
			for (TenantActivation ca : (List<TenantActivation>) result) {
				log.debug("CustomerActivation (" + ca.getTenant().getName() + 
						" start: " + ca.getStart().toString() +
						" end: " + ca.getEnd().toString() +
						((ca.getShapeID() != null) ? ("shape: " + ca.getShapeID().toString()) : "for all data"));
			}
		}
	
		return result;
	}
	
	@Override
	public List<TenantActivation> findWithinActive(Tenant t, Date start, Date end) {

		List<TenantActivation> result = null;
		
		TypedQuery<TenantActivation> query = entityManager.createQuery("from TenantActivation ca where ca.isActive=1 and ca.start >= :st and " +
																		"ca.end <= :en and ca.tenant = :t", TenantActivation.class);
		query.setParameter("st", start);
		query.setParameter("en", end);
		query.setParameter("t", t);
		
		result = query.getResultList();
		
		log.debug("Find tenant activations for tenant " + t.getName() + " within date");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (TenantActivation ca : (List<TenantActivation>) result) {
				log.debug("CustomerActivation (" + ca.getTenant().getName() + 
						" start: " + ca.getStart().toString() +
						" end: " + ca.getEnd().toString() +
						((ca.getShapeID() != null) ? ("shape: " + ca.getShapeID().toString()) : "for all data"));
			}
		}
	
		return result;
	}

	@Override
	public TenantActivation loadDetails(TenantActivation ta) {
		ta.getCreator().getName();
		ta.getTenant().getId();
		if(ta.getShapeID() != null)
			ta.getShapeID().toString();
		return ta;
	}
}
