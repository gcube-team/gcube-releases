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
import org.gcube.portlets.admin.gcubereleases.shared.AccountingPackage;
import org.gcube.portlets.admin.gcubereleases.shared.AccoutingReference;
import org.gcube.portlets.admin.gcubereleases.shared.Package;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class AccountingPersistence.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class AccountingPersistence extends AbstractPersistence<AccountingPackage>{

	protected static Logger logger = LoggerFactory.getLogger(AccountingPackage.class);
	public static final String tableName = "AccountingPackage";


	/**
	 * Instantiates a new accounting persistence.
	 *
	 * @param factory the factory
	 */
	public AccountingPersistence(EntityManagerFactory factory) {
		super(factory, tableName);

	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.persistence.AbstractPersistence#rootFrom(javax.persistence.criteria.CriteriaQuery)
	 */
	@Override
	public Root<AccountingPackage> rootFrom(CriteriaQuery<Object> cq){
		return cq.from(AccountingPackage.class);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.server.persistence.AbstractPersistence#deleteItemByIdField(java.lang.String)
	 */
	@Override
	public int deleteItemByIdField(String idField)
			throws DatabaseServiceException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.server.persistence.AbstractPersistence#removeAllReleations()
	 */
	@Override
	public int removeAllReleations() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.server.persistence.AbstractPersistence#removeReleation(java.lang.Object)
	 */
	@Override
	public int removeRelations(AccountingPackage item) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Delete accounting package for package ref.
	 *
	 * @param packageId the package id
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	public int deleteAccountingPackageForPackageRef(String packageId) throws DatabaseServiceException {
		EntityManager em = createNewManager();
		int removed = 0;
		try {

			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM AccountingPackage p WHERE p."+AccountingPackage.PACKAGE_REF +"= '"+packageId+"'").executeUpdate();
			em.getTransaction().commit();
			logger.trace("DELETED FROM AccountingPackage  " + removed + " items");

		} catch (Exception e) {
			logger.error("Error in deleteAccountingPackageForPackageId: " + e.getMessage(), e);
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}

		return removed;
	}

	public void incrementPackageAccounting(Package pck, AccoutingReference reference) throws Exception{
		logger.trace("Updating accounting for package: "+pck.getID());
		logger.trace("Reference: "+reference);

		EntityManager em = createNewManager();
		try {

			if(reference==null)
				return;

			Package managedPackage = em.find(Package.class, pck.getInternalId());

			AccountingPackage acc = managedPackage.getAccouting();

			switch (reference) {
			case DOWNLOAD:
				acc.setDownloadNmb(acc.getDownloadNmb() + 1);
				break;
			case JAVADOC:
				acc.setJavadocNmb(acc.getJavadocNmb() + 1);
				break;
			case MAVEN_REPO:
				acc.setMavenRepoNmb(acc.getMavenRepoNmb() + 1);
				break;
			case WIKI:
				acc.setWikiNmb(acc.getWikiNmb() + 1);
				break;
			case GITHUB:
				acc.setGitHubNmb(acc.getGitHubNmb() + 1);
				break;
			}

			try{
				update(acc);
				logger.trace("Updated accounting for: "+acc);
			}catch(Exception e){
				logger.error("Error in update: " + e.getMessage(), e);
			}finally{
				em.close();
			}

		} catch (Exception e) {
			logger.error("incrementPackageAccounting error: ",e);
			throw new Exception("Sorry, an error occurred on update accounting, try again later");
		}
	}


	/**
	 * Update accounting for package id.
	 *
	 * @param accouting the accouting
	 * @return true, if successful
	 * @throws DatabaseServiceException the database service exception
	 */
	public boolean updateAccountingForPackageId(AccountingPackage accouting) throws DatabaseServiceException{
		EntityManager em = createNewManager();
		try {

			em.getTransaction().begin();
			accouting = em.merge(accouting);
			em.getTransaction().commit();
			logger.trace("MERGED AccountingPackage " + accouting);

		} catch (Exception e) {
			logger.error("Error in updateAccountingForPackageId: " + e.getMessage(), e);
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}

		return false;
	}

	/**
	 * Delete all accounting packages.
	 *
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	public int deleteAllAccountingPackages() throws DatabaseServiceException {
		EntityManager em = createNewManager();
		int removed = 0;
		try {

			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM AccountingPackage").executeUpdate();
			em.getTransaction().commit();
			logger.trace("DELETED FROM AccountingPackage " + removed + " items");

		} catch (Exception e) {
			logger.error(
					"Error in deleteAllAccountingPackage: " + e.getMessage(),
					e);
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}

		return removed;
	}

	/**
	 * Gets the accounting package rows.
	 *
	 * @return the accounting package rows
	 * @throws DatabaseServiceException the database service exception
	 */
	public List<Package> getAccountingPackageRows() throws DatabaseServiceException {
		EntityManager em = createNewManager();
		List<Package> rows = new ArrayList<Package>();
		try {
			Query query = em.createQuery("select t from AccountingPackage t");
			rows = query.getResultList();
		} catch (Exception e) {
			logger.error(
					"Error in getAccountingPackageRows: " + e.getMessage(),
					e);
		} finally {
			em.close();
		}
		return rows;
	}

	/**
	 * Delete accounting for package refs.
	 *
	 * @param packageIDs the package i ds
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	public int deleteAccountingForPackageRefs(List<String> packageIDs) throws DatabaseServiceException{

		EntityManager em = createNewManager();
		int removed = 0;
		try {

			em.getTransaction().begin();
			Query query = em.createQuery("DELETE FROM AccountingPackage p WHERE p."+AccountingPackage.PACKAGE_REF +" IN :listRefs");
			removed = query.setParameter("listRefs", packageIDs).executeUpdate();
			em.getTransaction().commit();
			logger.trace("DELETED FROM AccountingPackage  " + removed + " items");

		} catch (Exception e) {
			logger.error("Error in deleteAccountingForPackageRefs: " + e.getMessage(), e);
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}

		return removed;
	}

	/**
	 * Delete accounting for internal i ds.
	 *
	 * @param accountingIDs the accounting i ds
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	public int deleteAccountingForInternalIDs(List<String> accountingIDs) throws DatabaseServiceException{

		EntityManager em = createNewManager();
		int removed = 0;
		try {

			em.getTransaction().begin();
			Query query = em.createQuery("DELETE FROM AccountingPackage p WHERE p.internalId IN :listIDs");
			removed = query.setParameter("listIDs", accountingIDs).executeUpdate();
			em.getTransaction().commit();
			logger.trace("DELETED FROM AccountingPackage  " + removed + " items");

		} catch (Exception e) {
			logger.error("Error in deleteAccountingForPackageRefs: " + e.getMessage(), e);
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}

		return removed;
	}

}
