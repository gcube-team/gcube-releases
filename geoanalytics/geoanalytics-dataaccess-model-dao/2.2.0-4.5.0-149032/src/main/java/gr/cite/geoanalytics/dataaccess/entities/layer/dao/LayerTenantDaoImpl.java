package gr.cite.geoanalytics.dataaccess.entities.layer.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerTenant;

public class LayerTenantDaoImpl extends JpaDao<LayerTenant, UUID> implements LayerTenantDao{

	public static Logger log = LoggerFactory.getLogger(LayerDaoImpl.class);
	
	@Override
	public LayerTenant loadDetails(LayerTenant t) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public List<LayerTenant> getLayerTenantsByLayerId(UUID layerId) {
		log.debug("Searching for layer tenants of layer (id): " + layerId);
		TypedQuery<LayerTenant> query = entityManager
				.createQuery("from LayerTenant where lt_lay_id = :val", LayerTenant.class);
		query.setParameter("val", layerId);
		return query.getResultList();
	}
	
	public LayerTenant findLayerTenantByLayer(Layer layer){
		log.debug("Searching for layer tenant of layer : " + layer);
		TypedQuery<LayerTenant> query = entityManager
				.createQuery("from LayerTenant where layer = :layer", LayerTenant.class);
		query.setParameter("layer", layer);
		if(query.getResultList().size() != 0)
			return query.getResultList().get(0);
		else
			return null;
		
	}

//	@Override
//	public List<LayerTenant> deleteLayerTenantsOfLayer(UUID layerId) {
//		log.debug("for layer tenants of layer (id): " + layerId);
//		TypedQuery<LayerTenant> query = entityManager
//				.createQuery("from LayerTenant where lt_lay_id = :val", LayerTenant.class);
//		query.setParameter("val", layerId);
//		return query.getResultList();
//	}
	
	
}
