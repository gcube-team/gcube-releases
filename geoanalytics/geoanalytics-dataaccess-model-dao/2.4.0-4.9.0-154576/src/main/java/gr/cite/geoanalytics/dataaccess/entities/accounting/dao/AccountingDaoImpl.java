package gr.cite.geoanalytics.dataaccess.entities.accounting.dao;

import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.accounting.Accounting;
import gr.cite.geoanalytics.dataaccess.entities.accounting.Accounting.AccountingType;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class AccountingDaoImpl extends JpaDao<Accounting, UUID> implements AccountingDao {
	private static Logger log = LoggerFactory.getLogger(AccountingDaoImpl.class);
	
	@Override
	public List<Accounting> findByType(Accounting.AccountingType type)
	{
		List<Accounting> result = null;
		
		TypedQuery<Accounting> query = entityManager.createQuery("from Accounting where type= :typeCode", Accounting.class);
		query.setParameter("typeCode", type.typeCode());
		
		result = query.getResultList();
			
		log.debug("Find accounting entries with type: " + type);
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (Accounting a : (List<Accounting>) result) {
				log.debug("Accounting (" + a.getId() + ")");
			}
		}
		validateByType(result, type, null);
		
		
		return result;
	}
	
	@Override
	public List<Accounting> findByTypeAndCreator(Accounting.AccountingType type, Principal creator)
	{
		List<Accounting> result = null;
		
		TypedQuery<Accounting> query = entityManager.createQuery("from Accounting where type= :typeCode and creator= :creatorId", Accounting.class);
		query.setParameter("typeCode", type.typeCode());
		query.setParameter("creatorId", creator);
		
		result = query.getResultList();
			
		log.debug("Find accounting entries with type: " + type + " and creator: " + creator);
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (Accounting a : (List<Accounting>) result) {
				log.debug("Accounting (" + a.getId() + ")");
			}
		}
		//validateByType(result, type, creator);
		
		return result;
	}
	
	private void validateByType(List<Accounting> result, AccountingType type, Principal creator)
	{

	}

	@Override
	public List<Accounting> validAccounting()
	{
		List<Accounting> result = null;
		
		TypedQuery<Accounting> query = entityManager.createQuery("from Accounting where valid = 1", Accounting.class);
		
		result = query.getResultList();
			
		log.debug("Find valid accounting entries with type: ");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (Accounting a : (List<Accounting>) result) {
				log.debug("Accounting (" + a.getId() + ")");
			}
		}
		//validateByType(result, type, creator);
		
		return result;
	}

	@Override
	public List<Accounting> findByCustomer(Tenant t)
	{
		List<Accounting> result = null;
		
		TypedQuery<Accounting> query = entityManager.createQuery("from Accounting where tenant= :t", Accounting.class);
		query.setParameter("t", t);
		
		result = query.getResultList();
			
		log.debug("Find accounting entries of customer: " + t.getName());
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (Accounting a : (List<Accounting>) result) {
				log.debug("Accounting (" + a.getId() + ")");
			}
		}
		//validateByType(result, type, creator);
		
		return result;
	}

	@Override
	public List<Accounting> findByUser(Principal principal)
	{
		List<Accounting> result = null;
		
		TypedQuery<Accounting> query = entityManager.createQuery("from Accounting where principal= :principal", Accounting.class);
		query.setParameter("principal", principal);
		
		result = query.getResultList();
			
		log.debug("Find accounting entries of user: " + principal.getName());
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (Accounting a : (List<Accounting>) result) {
				log.debug("Accounting (" + a.getId() + ")");
			}
		}
		//validateByType(result, type, creator);
		
		return result;
	}

	@Override
	public List<Accounting> findValidByCustomer(Tenant t) {
		List<Accounting> result = null;
		
		TypedQuery<Accounting> query = entityManager.createQuery("from Accounting where tenant= :t and valid=1", Accounting.class);
		query.setParameter("t", t);
		
		result = query.getResultList();
			
		log.debug("Find valid accounting entries of customer: " + t.getName());
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (Accounting a : (List<Accounting>) result) {
				log.debug("Accounting (" + a.getId() + ")");
			}
		}
		return result;
	}

	@Override
	public List<Accounting> findValidByUser(Principal principal) {
		List<Accounting> result = null;
		
		TypedQuery<Accounting> query = entityManager.createQuery("from Accounting where principal= :principal and valid=1", Accounting.class);
		query.setParameter("principal", principal);
		
		result = query.getResultList();
			
		log.debug("Find valid accounting entries of user: " + principal.getName());
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (Accounting a : (List<Accounting>) result) {
				log.debug("Accounting (" + a.getId() + ")");
			}
		}
		//validateByType(result, type, creator);
		
		return result;

	}
	
	public float aggregateByCustomer(Tenant t, Date from, Date to)
	{
		String queryString = "select sum(a.units) from Accounting a where a.tenant=:t and a.valid=1";
		if(from != null) queryString += " and a.date > :from";
		if(to != null) queryString += " and a.date < :to";
		TypedQuery<Float> query = entityManager.createQuery(queryString, Float.class);
		query.setParameter("t", t);
		if(from != null) query.setParameter("from", from, TemporalType.DATE);
		if(to != null) query.setParameter("to", to, TemporalType.DATE);
		
		return query.getSingleResult();
	}
	
	public float aggregateByUser(Principal principal, Date from, Date to)
	{
		String queryString = "select sum(a.units) from Accounting a where a.principal=:principal and a.valid=1";
		if(from != null) queryString += " and a.date > :from";
		if(to != null) queryString += " and a.date < :to";
		TypedQuery<Float> query = entityManager.createQuery(queryString, Float.class);
		query.setParameter("principal", principal);
		if(from != null) query.setParameter("from", from, TemporalType.DATE);
		if(to != null) query.setParameter("to", to, TemporalType.DATE);
		
		return query.getSingleResult();
	}

	@Override
	public Accounting loadDetails(Accounting ac) {
		ac.getCreator().getName();
		if(ac.getPrincipal() != null)
			ac.getPrincipal().getName();
		if(ac.getTenant() != null)
			ac.getTenant().getName();
		return ac;
	}
}
