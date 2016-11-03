package gr.cite.geoanalytics.dataaccess.entities.sysconfig.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.SysConfig;
import gr.cite.geoanalytics.dataaccess.exception.DataLayerException;

@Repository
public class SysConfigDaoImpl extends JpaDao<SysConfig, UUID> implements SysConfigDao {
	public static Logger log = LoggerFactory.getLogger(SysConfigDaoImpl.class);

	@Override
	public List<SysConfig> findByClass(short configClass) {
		TypedQuery<SysConfig> query = entityManager.createQuery("from SysConfig where configClass = :code", SysConfig.class);
		query.setParameter("code", configClass);
			
		List<SysConfig> result = query.getResultList();
			
		log.debug("Find sys config with class: " + configClass);
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null) {
			for (SysConfig sh : (List<SysConfig>) result) {
				log.debug("SysConfig (" + sh.getId() + ")");
			}
		}
		
		SysConfig.SysConfigClass cClass = SysConfig.SysConfigClass.fromConfigClassCode(configClass);
		switch(cClass) {
		case GLOBALCONFIG:
			if(result != null && result.size() > 1)
				throw new DataLayerException("More than one global configuration instances detected");
			break;
		case LAYERCONFIG:
			break;
		case PRESENTATION:
			break;
		}
		return result;
	}

	@Override
	public SysConfig loadDetails(SysConfig sc) {
		sc.getCreator().getName();
		return sc;
	}
}
