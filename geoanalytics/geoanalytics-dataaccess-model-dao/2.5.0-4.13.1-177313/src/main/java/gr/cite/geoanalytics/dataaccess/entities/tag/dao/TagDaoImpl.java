package gr.cite.geoanalytics.dataaccess.entities.tag.dao;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.tag.Tag;

@Repository
public class TagDaoImpl  extends JpaDao<Tag, UUID> implements TagDao {
	public static Logger log = LoggerFactory.getLogger(TagDaoImpl.class);
	
	@Override
	public Tag findTagByName(String name)  throws Exception{
		log.debug("Retrieving tag by name: "+ name);
		
		Tag result = null;
		
		try {
			TypedQuery<Tag> query = entityManager.createQuery("from Tag t where t.name = :name", Tag.class);
			query.setParameter("name", name);
			result = query.getSingleResult();
		} catch(NoResultException e){
			log.error("No tag with name " + name + " exists");
		} catch(NonUniqueResultException e){
			log.error("More than 1 tags with name " + name + " exists");
		} catch(Exception e){
			throw new Exception("Could not retrieve tag with name: " + name, e);
		}
		
		return result;
	}
	
	@Override
	public List<Tag> findTagsByNames(Collection<String> tagNames) {
		log.debug("Retrieving tags by names: "+ tagNames);
		
		List<Tag> result = null;
		
		TypedQuery<Tag> query = entityManager.createQuery("FROM Tag t WHERE t.name IN :tagNames", Tag.class);
		query.setParameter("tagNames", tagNames);
		result = query.getResultList();
		
		return result;
	}

	@Override
	public Tag loadDetails(Tag t) {
		return null;
	}	
}
