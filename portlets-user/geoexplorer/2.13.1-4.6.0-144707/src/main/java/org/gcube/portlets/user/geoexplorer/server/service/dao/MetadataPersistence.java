/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.server.service.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.gcube.portlets.user.geoexplorer.server.service.DatabaseServiceException;
import org.gcube.portlets.user.geoexplorer.shared.GeoResourceParameters;
import org.gcube.portlets.user.geoexplorer.shared.GeoResourceParameters.RESOURCETYPE;
import org.gcube.portlets.user.geoexplorer.shared.GeonetworkMetadata;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 29, 2013
 *
 */
public class MetadataPersistence extends AbstractPersistence<GeonetworkMetadata>{

	/**
	 * @param factory
	 */
	public MetadataPersistence(EntityManagerFactory factory) {
		super(factory);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.service.AbstractPersistence#removeAll()
	 */
	@Override
	public int removeAll() throws DatabaseServiceException {
		EntityManager em = super.createNewManager();
		int removed = 0;
		try {

			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM GeonetworkMetadata").executeUpdate();
			em.getTransaction().commit();
			logger.trace("DELETE FROM GeonetworkMetadata " + removed +" items");

		} catch (Exception e) {
			logger.error("Error in removeAll: " + e.getMessage(), e);

		} finally {
			em.close();
		}

		return removed;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.service.AbstractPersistence#getList()
	 */
	@Override
	public List<GeonetworkMetadata> getList() throws DatabaseServiceException {
		EntityManager em = super.createNewManager();
		List<GeonetworkMetadata> list = new ArrayList<GeonetworkMetadata>();
		try {
			Query query = em.createQuery("select t from GeonetworkMetadata t");

			list = query.getResultList();
			
		} catch (Exception e) {
			logger.error("Error in GeonetworkMetadata - getList: " + e.getMessage(), e);
			
		} finally {
			em.close();
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.service.AbstractPersistence#getList(int, int)
	 */
	@Override
	public List<GeonetworkMetadata> getList(int startIndex, int offset)
			throws DatabaseServiceException {
		EntityManager em = super.createNewManager();
		List<GeonetworkMetadata> listOJ = new ArrayList<GeonetworkMetadata>();
		try {
			Query query = em.createQuery("select t from GeonetworkMetadata t");
			query.setFirstResult(startIndex);
			query.setMaxResults(offset);
			listOJ =  query.getResultList();
			
		} finally {
			em.close();
		}
		return listOJ;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.service.AbstractPersistence#countItems()
	 */
	@Override
	public int countItems() throws DatabaseServiceException {
		return getList().size();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.service.dao.AbstractPersistence#getLastResourceType(org.gcube.portlets.user.geoexplorer.shared.GeoResourceParameters.RESOURCETYPE)
	 */
	@Override
	public GeoResourceParameters getLastResourceType(RESOURCETYPE property) throws DatabaseServiceException {
		return null;
	}
	

}
