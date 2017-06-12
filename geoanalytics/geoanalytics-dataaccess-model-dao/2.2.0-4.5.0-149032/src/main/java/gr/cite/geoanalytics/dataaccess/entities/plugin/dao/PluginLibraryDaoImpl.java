package gr.cite.geoanalytics.dataaccess.entities.plugin.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.plugin.PluginLibrary;

@Repository
public class PluginLibraryDaoImpl extends JpaDao<PluginLibrary, UUID> implements PluginLibraryDao {

	@Override
	public List<PluginLibrary> getAll() {
		StringBuilder queryStr = new StringBuilder();
		queryStr.append("FROM PluginLibrary");
		
		TypedQuery<PluginLibrary> query = entityManager.createQuery(queryStr.toString(), PluginLibrary.class);
		
		return query.getResultList();
	}

	@Override
	public long count() {
		StringBuilder queryStr = new StringBuilder();
		queryStr.append("SLECT COUNT(pl) FROM PluginLibrary pl");
		
		Query query = entityManager.createQuery(queryStr.toString());
		
		return (long)query.getSingleResult();
	
	}

	@Override
	public PluginLibrary loadDetails(PluginLibrary t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLoaded(PluginLibrary t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void deleteAll() throws Exception {
		StringBuilder queryStr = new StringBuilder("");
		queryStr.append("DELETE FROM PluginLibrary");
		
		Query query = entityManager.createQuery(queryStr.toString());
		query.executeUpdate();
	}

}
