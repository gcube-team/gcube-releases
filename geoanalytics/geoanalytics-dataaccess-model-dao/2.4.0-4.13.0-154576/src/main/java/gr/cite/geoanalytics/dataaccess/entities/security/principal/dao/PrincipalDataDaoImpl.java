package gr.cite.geoanalytics.dataaccess.entities.security.principal.dao;

import java.util.UUID;
import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalData;

@Repository
public class PrincipalDataDaoImpl extends JpaDao<PrincipalData, UUID> implements PrincipalDataDao {

	@Override
	public PrincipalData loadDetails(PrincipalData pd) {
		pd.getPrincipals().forEach(p -> p.getName());
		return pd;
	}
	
}
