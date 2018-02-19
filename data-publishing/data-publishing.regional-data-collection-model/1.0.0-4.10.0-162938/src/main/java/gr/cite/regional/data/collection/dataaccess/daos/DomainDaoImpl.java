package gr.cite.regional.data.collection.dataaccess.daos;

import gr.cite.regional.data.collection.dataaccess.entities.Domain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class DomainDaoImpl extends JpaDao<Domain, Integer> implements DomainDao {
	private static final Logger log = LogManager.getLogger(DomainDaoImpl.class);
	
	@Override
	public Domain loadDetails(Domain t) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Domain> getDomainByLabel(String domainName) {
		List<Domain> result;
		
		TypedQuery<Domain> query = entityManager.createQuery("from Domain d where d.label = :name", Domain.class);
		query.setParameter("name", domainName);
		
		result = query.getResultList();
		
		log.debug("Domain by name: " + domainName);
		log.debug((result != null ? result.size() : 0) + " results");
		if (log.isDebugEnabled() && result != null) {
			for (Domain d: result)
				log.debug("Domain (" + d.getLabel() + ")");
		}
		
		return result;
	}
	
}
