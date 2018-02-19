package gr.cite.regional.data.collection.dataaccess.daos;

import java.util.Map;

import gr.cite.regional.data.collection.dataaccess.entities.Entity;

public interface DaoFactory
{
	@SuppressWarnings("rawtypes")
	public Dao getDao(Class<? extends Entity> type) throws Exception;
	public void overrideMappings(Map<String, String> mappings) throws Exception;
}
