package gr.cite.regional.data.collection.dataaccess.daos;

import gr.cite.regional.data.collection.dataaccess.entities.UserReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.TypedQuery;
import java.util.List;

public class UserReferenceDaoImpl extends JpaDao<UserReference, Integer> implements UserReferenceDao {
	private static final Logger log = LogManager.getLogger(UserReferenceDaoImpl.class);
	@Override
	public UserReference loadDetails(UserReference t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserReference> getUserReferenceByUsername(String username) {
		List<UserReference> result;

		TypedQuery<UserReference> query = entityManager.createQuery("from UserReference ur where ur.label = :name", UserReference.class);
		query.setParameter("name", username);

		result = query.getResultList();

		log.debug("UserReference by label: " + username);
		log.debug((result != null ? result.size() : 0) + " results");
		if (log.isDebugEnabled() && result != null) {
			for (UserReference d: result)
				log.debug("UserReference (" + d.getLabel() + ")");
		}

		return result;
	}
}
