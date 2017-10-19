package gr.cite.geoanalytics.dataaccess.entities.user.dao;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.dao.UUIDGenerator;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.user.UserOld;
import gr.cite.geoanalytics.dataaccess.exception.DataLayerException;

import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoOldImpl extends JpaDao<UserOld, UUID> implements UserDaoOld {

	Logger log = LoggerFactory.getLogger(UserDaoOldImpl.class);

	@Override
	public UserOld loadDetails(UserOld t) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*@Override
	public List<User> findActiveUsers() {
		List<User> result = null;
		
		result = entityManager.createQuery("from User us where us.isActive = 1", User.class).getResultList();
		
		log.debug("Find active users ");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (User us : (List<User>) result) {
				log.debug("User (" + us.getFullName() + ")");
			}
		}
	
		return result;
	}
	
	@Override
	public List<String> listSystemNames()
	{
		List<String> result = null;
		
		result = entityManager.createQuery("select us.systemName from User us", String.class).getResultList();
		
		if(log.isDebugEnabled())
		{
			log.debug("List user system names");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<String> listSystemNamesOfActive()
	{
		List<String> result = null;
		
		result = entityManager.createQuery("select us.systemName from User us where us.isActive=1", String.class).getResultList();
		
		if(log.isDebugEnabled())
		{
			log.debug("List user system names of active users");
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<String> listSystemNamesByCustomer(Tenant c)
	{
		List<String> result = null;
		
		TypedQuery<String> query = entityManager.createQuery("select us.systemName from User us where us.customer=:c", String.class);
		query.setParameter("c", c);
		
		result = query.getResultList();
		if(log.isDebugEnabled())
		{
			log.debug("List user system names for customer " + c.getName());
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public List<String> listSystemNamesByCustomerOfActive(Tenant c)
	{
		List<String> result = null;
		
		TypedQuery<String> query = entityManager.createQuery("select us.systemName from User us where us.customer=:c and us.isActive=1", String.class);
		query.setParameter("c", c);
		
		result = query.getResultList();
		if(log.isDebugEnabled())
		{
			log.debug("List user system names for customer " + c.getName());
			log.debug((result != null ? result.size() : 0) + " results");
		}
		return result;
	}
	
	@Override
	public long countActiveUsers() {

		return ((Number)entityManager.createQuery("select count(us) from User us where us.isActive = 1").getSingleResult()).longValue();
	}
	
	@Override
	public User findBySystemName(String name) {
		List<User> result = null;
		
		TypedQuery<User> query = entityManager.createQuery("from User us where us.systemName = :name", User.class);
		query.setParameter("name", name);
		
		result = query.getResultList();
		
		log.debug("Users by name: " + name);
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (User us : (List<User>) result) 
				log.debug("User (" + us.getFullName() + ")");
		}
		if(result != null && result.size() > 1) throw new DataLayerException("More than one user with name " + name + " was found");
		
		return result != null && !result.isEmpty() ? result.get(0) : null;
	}
	
	@Override
	public List<String> searchByName(List<String> names) {
		List<String> result = null;
		
		StringBuilder queryB = new StringBuilder();
		queryB.append("select us.systemName from User us");

		if(!names.isEmpty()) queryB.append(" where ");
		int j = 0;
		for(int i=0; i<names.size(); i++)
		{
			queryB.append("lower(us.systemName) = :name" + j);
			j++;
			queryB.append(" or lower(us.fullName) like :name" + j);
			j++;
			if(i < names.size()-1)
				queryB.append(" or ");
		}
		TypedQuery<String> query = entityManager.createQuery(queryB.toString(), String.class);
		j = 0;
		for(int i=0; i<names.size(); i++)
		{
			String lower = names.get(i).toLowerCase();
			query.setParameter("name"+(j++), lower);
			query.setParameter("name"+(j++), "%"+lower+"%");
		}

		result = query.getResultList();
		
		log.debug("Users by name pattern matching:");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (String us : (List<String>) result) 
				log.debug("User (" + us + ")");
		}
		
		return result;
	}
	
	@Override
	public List<String> searchByNameAndCustomer(List<String> names, Tenant c) {
		List<String> result = null;
		
		StringBuilder queryB = new StringBuilder();
		queryB.append("select us.systemName from User us where us.customer = :c");
		if(!names.isEmpty()) queryB.append(" and ( ");
		int j = 0;
		for(int i=0; i<names.size(); i++)
		{
			queryB.append("lower(us.systemName) = :name" + j);
			j++;
			queryB.append(" or lower(us.fullName) like :name" + j);
			j++;
			if(i < names.size()-1)
				queryB.append(" or ");
		}
		if(!names.isEmpty()) queryB.append(")");
		TypedQuery<String> query = entityManager.createQuery(queryB.toString(), String.class);
		
		j = 0;
		for(int i=0; i<names.size(); i++)
		{
			String lower = names.get(i).toLowerCase();
			query.setParameter("name"+(j++), lower);
			query.setParameter("name"+(j++), "%"+lower+"%");
		}
		query.setParameter("c", c);
		
		result = query.getResultList();
		
		log.debug("Users by customer and name pattern matching:");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null)
		{
			for (String us : (List<String>) result) 
				log.debug("User (" + us + ")");
		}
		
		return result;
	}
	
	@Override
	public User systemUser()
	{
		return read(UUIDGenerator.systemUserUUID());
	}*/

}
