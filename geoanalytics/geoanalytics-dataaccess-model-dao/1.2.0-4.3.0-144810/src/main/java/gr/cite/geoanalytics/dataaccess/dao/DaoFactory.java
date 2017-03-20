package gr.cite.geoanalytics.dataaccess.dao;

import java.util.Map;

import gr.cite.geoanalytics.dataaccess.entities.Entity;

public interface DaoFactory
{
	public Dao getDao(Class<? extends Entity> type) throws Exception;
	public void overrideMappings(Map<String, String> mappings) throws Exception;
}
