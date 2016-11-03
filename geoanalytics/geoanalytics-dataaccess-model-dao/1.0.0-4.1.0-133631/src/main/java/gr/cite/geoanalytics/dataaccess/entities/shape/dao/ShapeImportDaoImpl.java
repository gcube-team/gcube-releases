package gr.cite.geoanalytics.dataaccess.entities.shape.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeImport;

@Repository
public class ShapeImportDaoImpl extends JpaDao<ShapeImport, UUID> implements ShapeImportDao {
	private static Logger log = LoggerFactory.getLogger(ShapeImportDaoImpl.class);
	
	@Override
	public List<ShapeImport> getImport(UUID importId) {
		List<ShapeImport> result = null;
			
		TypedQuery<ShapeImport> query = entityManager.createQuery("from ShapeImport si where si.shapeImport = :iId", ShapeImport.class);
		query.setParameter("iId", importId);
		
		result = query.getResultList();
			
		log.debug("Get shape import by id: " + result.size() + " results");
		for (ShapeImport si : (List<ShapeImport>) result) {
			log.debug("ShapeImport (" + si.getId() + ")");
		}
	
		return result;
	}
	
	@Override
	public List<ShapeImport> findByIdentity(String identity) {
		List<ShapeImport> result = null;
			
		TypedQuery<ShapeImport> query = entityManager.createQuery("from ShapeImport si where si.shapeIdentity = :identity", ShapeImport.class);
		query.setParameter("identity", identity);
		
		result = query.getResultList();
			
			log.debug("Get shape import by identity: " + result.size() + " results");
			for (ShapeImport si : (List<ShapeImport>) result) {
				log.debug("ShapeImport (" + si.getId() + ")");
			}
		
			return result;
	}
	
	@Override
	public List<UUID> listImports() {
		TypedQuery<UUID> query = entityManager.createQuery("select distinct(si.shapeImport) from ShapeImport si", UUID.class);
		return query.getResultList();
	}

	@Override
	public ShapeImport loadDetails(ShapeImport si) {
		si.getCreator().getName();
		return si;
	}

}
