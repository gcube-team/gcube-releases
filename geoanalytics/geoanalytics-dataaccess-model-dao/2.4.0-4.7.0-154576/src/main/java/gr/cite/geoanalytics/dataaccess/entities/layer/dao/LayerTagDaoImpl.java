package gr.cite.geoanalytics.dataaccess.entities.layer.dao;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerTag;
import gr.cite.geoanalytics.dataaccess.entities.tag.Tag;

@Repository
public class LayerTagDaoImpl extends JpaDao<LayerTag, UUID> implements LayerTagDao {
	
	public static Logger log = LoggerFactory.getLogger(LayerTagDaoImpl.class);

	@Override
	public List<LayerTagInfo> findTagsOfLayer(Layer layer) throws Exception {
		log.debug("Retrieving tags by layer : "+ layer);

		List<LayerTagInfo> result = null;
		
		StringBuilder queryB = new StringBuilder("SELECT new gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerTagInfo(lt.tag.id, lt.tag.name)");
		queryB.append(" FROM LayerTag lt WHERE lt.layer = :layer");
		
		try {
			TypedQuery<LayerTagInfo> query = entityManager.createQuery(queryB.toString(), LayerTagInfo.class);
			query.setParameter("layer", layer);
			result = query.getResultList();
		} catch(Exception e){
			throw new Exception("Could not retrieve tags of layer: " + layer, e);
		}
		
		return result;
	}
	
	@Override
	public List<Layer> findLayersOfTag(Tag tag) throws Exception {
		log.debug("Retrieving layers by tag : "+ tag);

		List<Layer> result = null;
		
		try {
			TypedQuery<Layer> query = entityManager.createQuery("select lt.layer from LayerTag lt where lt.tag = :tag", Layer.class);
			query.setParameter("tag", tag);
			result = query.getResultList();
		} catch(Exception e){
			throw new Exception("Could not retrieve layers of tag: " + tag, e);
		}
		
		return result;
	}

	@Override
	public List<LayerTag> findLayerTagsByLayer(Layer layer) throws Exception {
		log.debug("Retrieving layer tags by layer : "+ layer);

		List<LayerTag> result = null;
		
		try {
			TypedQuery<LayerTag> query = entityManager.createQuery("from LayerTag lt where lt.layer = :layer", LayerTag.class);
			query.setParameter("layer", layer);
			result = query.getResultList();
		} catch(Exception e){
			throw new Exception("Could not retrieve tags of layer: " + layer, e);
		}
		
		return result;
	}

	@Override
	public List<LayerTag> findLayerTagsByLayerAndTagName(Layer layer, Collection<String> tags) throws Exception {
		log.debug("Retrieving layer tags by layer : "+ layer);

		List<LayerTag> result = null;
		
		try {
			TypedQuery<LayerTag> query = entityManager.createQuery("FROM LayerTag lt WHERE lt.layer = :layer AND lt.tag.name IN :tags", LayerTag.class);
			query.setParameter("layer", layer);
			query.setParameter("tags", tags);
			result = query.getResultList();
		} catch(Exception e){
			throw new Exception("Could not retrieve tags of layer: " + layer, e);
		}
		
		return result;
	}
	
	@Override
	public List<LayerTag> findLayerTagsByLayerAndTagNameNotInTagNamesList(Layer layer, Collection<String> tags) throws Exception {
		log.debug("Retrieving layer tags by layer : "+ layer);
		
		if(tags.isEmpty()){
			return this.findLayerTagsByLayer(layer);
		}

		List<LayerTag> result = null;
		
		try {
			TypedQuery<LayerTag> query = entityManager.createQuery("FROM LayerTag lt WHERE lt.layer = :layer AND lt.tag.name NOT IN :tags", LayerTag.class);
			query.setParameter("layer", layer);
			query.setParameter("tags", tags);
			result = query.getResultList();
		} catch(Exception e){
			throw new Exception("Could not retrieve tags of layer: " + layer, e);
		}
		
		return result;
	}
	
	@Override
	public List<LayerTag> findLayerTagsByTag(Tag tag) throws Exception {
		log.debug("Retrieving layer tags by tag : "+ tag);

		List<LayerTag> result = null;
		
		try {
			TypedQuery<LayerTag> query = entityManager.createQuery("from LayerTag lt where lt.tag = :tag", LayerTag.class);
			query.setParameter("tag", tag);
			result = query.getResultList();
		} catch(Exception e){
			throw new Exception("Could not retrieve tags of layer: " + tag, e);
		}
		
		return result;
	}	
		
	@Override
	public LayerTag loadDetails(LayerTag t) {
		return null;
	}
}
