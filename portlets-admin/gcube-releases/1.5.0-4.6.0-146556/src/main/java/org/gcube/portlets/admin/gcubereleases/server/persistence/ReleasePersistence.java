/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.server.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.gcube.portlets.admin.gcubereleases.server.exception.DatabaseServiceException;
import org.gcube.portlets.admin.gcubereleases.shared.Package;
import org.gcube.portlets.admin.gcubereleases.shared.Release;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ReleasePersistence.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class ReleasePersistence extends AbstractPersistence<Release>{

	public static final String tableName = "Release";
	protected static Logger logger = LoggerFactory.getLogger(ReleasePersistence.class);
	
	private PackagePersistence packagesPersistence; //REF TO PackagePersistence
	
	/**
	 * Instantiates a new release persistence.
	 *
	 * @param factory the factory
	 */
	public ReleasePersistence(EntityManagerFactory factory) {
		super(factory, tableName);
		packagesPersistence = new PackagePersistence(factory);
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.persistence.AbstractPersistence#rootFrom(javax.persistence.criteria.CriteriaQuery)
	 */
	@Override
	public Root<Release> rootFrom(CriteriaQuery<Object> cq){
		return cq.from(Release.class);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.server.persistence.AbstractPersistence#deleteItemByIdField(java.lang.String)
	 */
	@Override
	public int deleteItemByIdField(String idField)
			throws DatabaseServiceException {
		
		EntityManager em = createNewManager();
		int removed = 0;

		try {
			em.getTransaction().begin();
			removed = em.createQuery(
					"DELETE FROM "+tableName+" t WHERE t." + Release.ID_FIELD
							+ "='" + idField + "'").executeUpdate();
			em.getTransaction().commit();
			logger.trace("Item " + idField + " was deleted from "+tableName);

		} catch (Exception e) {
			logger.error(
					"Error in "+tableName+" - deleteItemByIdField: " + e.getMessage(), e);

		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}

		return removed;
	}
	
	/**
	 * Gets the package for id.
	 *
	 * @param packageID the package id
	 * @return the package for id
	 * @throws DatabaseServiceException the database service exception
	 */
	public List<Package> getPackageForID(String packageID) throws DatabaseServiceException {
		EntityManager em = createNewManager();
		List<Package> rows = new ArrayList<Package>();
		try {
			Query query = em.createQuery("select p from Package p" + " WHERE p."+Package.ID_FIELD+ "='" + packageID + "'");
			rows = query.getResultList();
		} catch (Exception e) {
			logger.error(
					"Error in " + tableName + " - getRows: " + e.getMessage(),
					e);
		} finally {
			em.close();
		}
		return rows;

	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.server.persistence.AbstractPersistence#removeReleations()
	 */
	@Override
	public int removeAllReleations() {
		
		try {
			int packagesDeleted = packagesPersistence.deleteAllPackages();
			packagesPersistence.getAccountingPersistence().deleteAllAccountingPackages();
			return packagesDeleted;
		} catch (DatabaseServiceException e) {
			logger.error("Error in removeRelations: " + e.getMessage(),e);
			return -1;
		}
		
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.server.persistence.AbstractPersistence#removeReleation(java.lang.Object)
	 */
	/**
	 * Delete all relations (packages) for input item
	 */
	@Override
	public int removeRelations(Release item) {
		try {
			int deleted = packagesPersistence.deletePackagesForRelease(item, true);
//			System.out.println("Packages removed : " + deleted);
			logger.trace("Packages removed : " + deleted);
			return deleted;
		} catch (DatabaseServiceException e) {
			e.printStackTrace();
			logger.error("Error in removeReleation: " + e.getMessage(),e);
			return -1;
		}
	}


	/**
	 * Gets the packages persistence.
	 *
	 * @return the packages persistence
	 */
	public PackagePersistence getPackagesPersistence() {
		return packagesPersistence;
	}


	/**
	 * Update release info.
	 *
	 * @param release the release
	 * @return the release
	 * @throws DatabaseServiceException the database service exception
	 */
	public Release updateReleaseInfo(Release release) throws DatabaseServiceException {
		
		EntityManager em = createNewManager();
		Release daoRelease = null;
		try {
			
			daoRelease = em.find(Release.class, release.getInternalId());
			//SET NEW DATA
			daoRelease.setDescription(release.getDescription());
			daoRelease.setName(release.getName());
			daoRelease.setOnLine(release.isOnLine());
			daoRelease.setLatestUpdate(release.getLatestUpdate());
			daoRelease.setReleaseDate(release.getReleaseDate());
			daoRelease = super.update(daoRelease);

		} catch (Exception e) {
			logger.error("Error in "+tableName+" - updateReleaseInfo: " + e.getMessage(), e);

		} finally {
			em.close();
		}

		return daoRelease;
	}

}
