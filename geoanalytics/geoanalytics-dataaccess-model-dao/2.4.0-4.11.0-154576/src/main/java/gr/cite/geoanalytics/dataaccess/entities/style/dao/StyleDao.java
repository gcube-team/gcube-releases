package gr.cite.geoanalytics.dataaccess.entities.style.dao;

import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.style.Style;

public interface StyleDao extends Dao<Style, UUID>{

	Style findStyleByName(String name) throws Exception;

}
