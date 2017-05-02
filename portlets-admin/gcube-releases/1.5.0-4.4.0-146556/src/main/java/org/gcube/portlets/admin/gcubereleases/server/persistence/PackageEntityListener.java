/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.server.persistence;

import javax.persistence.EntityManager;
import javax.persistence.PostRemove;

import org.gcube.portlets.admin.gcubereleases.server.database.DaoGcubeBuilderReportDBManager;
import org.gcube.portlets.admin.gcubereleases.server.database.EntityManagerFactoryCreator;
import org.gcube.portlets.admin.gcubereleases.shared.Package;
import org.gcube.portlets.admin.gcubereleases.shared.Release;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The listener interface for receiving packageEntity events.
 * The class that is interested in processing a packageEntity
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addPackageEntityListener<code> method. When
 * the packageEntity event occurs, that object's appropriate
 * method is invoked.
 *
 * @see PackageEntityEvent
 */
public class PackageEntityListener {
	
	protected static Logger logger = LoggerFactory.getLogger(PackageEntityListener.class);

	/**
	 * On post remove.
	 *
	 * @param entity the entity
	 */
	@PostRemove void onPostRemove(org.gcube.portlets.admin.gcubereleases.shared.Package entity) {
//		System.out.println("onPostRemove "+entity);
		logger.trace("onPostRemove Package: "+entity.getInternalId());
		decrementPackages(entity);
	}
	
	/**
	 * Decrement packages.
	 *
	 * @param pck the pck
	 */
	private void decrementPackages(Package pck){
		logger.info("Decrementing packages..");
		DaoGcubeBuilderReportDBManager<Release> daoManager = new DaoGcubeBuilderReportDBManager<Release>(EntityManagerFactoryCreator.getEntityManagerFactory());
		daoManager.instanceReleaseEntity();
		ReleasePersistence relPersistence = daoManager.getReleasePersistenceEntity();

		Release daoRelease = null;
		EntityManager em = null;
		try {
			em = relPersistence.createNewManager();
			daoRelease = em.find(Release.class, pck.getRelease().getInternalId());
			int newnop = daoRelease.getPackagesNmb()-1;
			daoRelease.setPackagesNmb(newnop);
			em.getTransaction().begin();
			em.merge(daoRelease);
			em.getTransaction().commit();
			logger.info("Updated number of packages at " + newnop + " for "+daoRelease);
		} catch (Exception e) {
			logger.error("Error in decrementPackages for " + daoRelease.getId() + " Name: "+daoRelease.getName(), e);

		} finally {
			if(em!=null && em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}
	}
}
