package gr.cite.geoanalytics.dataaccess.entities.tag.dao;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.tag.Tag;

public interface TagDao extends Dao<Tag, UUID> {
	 public Tag findTagByName(String name)  throws Exception;
	 public List<Tag> findTagsByNames(Collection<String> tagNames);
}
