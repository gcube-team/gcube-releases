package gr.cite.geoanalytics.dataaccess.entities.layer.dao;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerTag;
import gr.cite.geoanalytics.dataaccess.entities.tag.Tag;

public interface LayerTagDao extends Dao<LayerTag, UUID>{
	public List<Layer> findLayersOfTag(Tag tag) throws Exception;
	public List<LayerTagInfo> findTagsOfLayer(Layer layer) throws Exception;
	public List<LayerTag> findLayerTagsByLayer(Layer layer) throws Exception;
	public List<LayerTag> findLayerTagsByLayerAndTagName(Layer layer, Collection<String> tags) throws Exception;
	public List<LayerTag> findLayerTagsByLayerAndTagNameNotInTagNamesList(Layer layer, Collection<String> tags) throws Exception;
	public List<LayerTag> findLayerTagsByTag(Tag tag) throws Exception;
}