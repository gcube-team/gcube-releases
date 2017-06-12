package gr.cite.geoanalytics.dataaccess.entities.style.dao;

import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.style.Style;

@Repository
public class StyleDaoImpl extends JpaDao<Style, UUID> implements StyleDao{

	public static Logger log = LoggerFactory.getLogger(StyleDaoImpl.class);
	
	@Override
	public Style findStyleByName(String name)  throws Exception{
		log.debug("Retrieving style by name: "+ name);
		
		Style result = null;
		
		try {
			TypedQuery<Style> query = entityManager.createQuery("from Style t where t.name = :name", Style.class);
			query.setParameter("name", name);
			result = query.getSingleResult();
		} catch(NoResultException e){
			log.error("No style with name " + name + " exists");
		} catch(NonUniqueResultException e){
			log.error("More than 1 styles with name " + name + " exists");
		} catch(Exception e){
			throw new Exception("Could not retrieve style with name: " + name, e);
		}
		
		return result;
	}
	
	@Override
	public Style loadDetails(Style t) {
		return null;
	}


}
