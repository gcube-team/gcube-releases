/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.server.persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.gcube.portlets.admin.gcubereleases.server.exception.DatabaseServiceException;
import org.gcube.portlets.admin.gcubereleases.server.pool.ThreadPoolMng;
import org.gcube.portlets.admin.gcubereleases.server.pool.ThreadPoolQuery;
import org.gcube.portlets.admin.gcubereleases.server.pool.ThreadWorker;
import org.gcube.portlets.admin.gcubereleases.shared.Package;
import org.gcube.portlets.admin.gcubereleases.shared.Release;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class PackagePersistence.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class PackagePersistence extends AbstractPersistence<Package>{
	
	protected static Logger logger = LoggerFactory.getLogger(PackagePersistence.class);
	public static final String tableName = "Package";
	
	private AccountingPersistence accountingPersistence; //REF TO AccountingPersistence
	
	/**
	 * Instantiates a new package persistence.
	 *
	 * @param factory the factory
	 */
	public PackagePersistence(EntityManagerFactory factory) {
		super(factory, tableName);
		accountingPersistence = new AccountingPersistence(factory);
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.server.persistence.AbstractPersistence#rootFrom(javax.persistence.criteria.CriteriaQuery)
	 */
	@Override
	public Root<Package> rootFrom(CriteriaQuery<Object> cq){
		return cq.from(Package.class);
	}
	
	/**
	 * Gets the map field grouped by.
	 *
	 * @param releaseID the release id
	 * @param fieldName the field name
	 * @return the map field grouped by
	 * @throws DatabaseServiceException the database service exception
	 */
	
	//TODO ADD RELEASE ID
	public Map<String, Long> getMapFieldGroupedBy(String releaseID, String fieldName) throws DatabaseServiceException{
		
		EntityManager em = createNewManager();
		Map<String, Long> mapFields = null;
		try {
			Query query = em.createQuery("select Count(p."+fieldName+"), p."+fieldName+" from "+tableName+" p WHERE p."+Package.RELEASE_ID_REF +"= '"+releaseID+"' GROUP BY p."+fieldName +" ORDER BY p."+fieldName);
			List<Object[]>  resultList = query.getResultList();
			
			mapFields = new LinkedHashMap<String, Long>(resultList.size());
			for (Object[] result : resultList)
				mapFields.put((String)result[1], (Long)result[0]);

		} catch (Exception e) {
			logger.error("Error in getAccountingPackageRows: " + e.getMessage(),e);
		} finally {
			em.close();
		}
		return mapFields;
	}
	
	/**
	 * Delete packages for release.
	 *
	 * @param release the release
	 * @param deleteAccouting the delete accouting
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	public int deletePackagesForRelease(Release release, boolean deleteAccouting) throws DatabaseServiceException {
		EntityManager em = createNewManager();
		int removed = 0;
		try {
			
			//RETRIEVE LIST PACKAGES
			Query query = em.createQuery("select p FROM "+tableName+" p WHERE p."+Package.RELEASE +"= :release");
			List<Package> listPackages = (List<Package>) query.setParameter("release", release).getResultList();
			
			removed = deletePackagesForRelease(release);
//			System.out.println("removed? "+removed);
//			System.out.println("listPackages? "+listPackages.size());
			if(listPackages==null || listPackages.size()==0){
				logger.warn("No packages foud for: "+release.getName() +", returning");
//				em.close();
//				System.out.println("No packages foud for: "+release.getName() +", returning");
				return removed;
			}
			
			if(removed>0 && deleteAccouting){
				
				List<String> accountingIDs = new ArrayList<String>(listPackages.size());
				for (Package package1 : listPackages) {
					accountingIDs.add(package1.getAccouting().getInternalId()+"");
				}
				removed = accountingPersistence.deleteAccountingForInternalIDs(accountingIDs);
			}
		} catch (Exception e) {
			logger.error("Error in Package - deletePackagesForRelease: " + e.getMessage(), e);
			throw new DatabaseServiceException("Service error when deleting packages for release id: "+release.getId());
		} finally {
			em.close();
		}

		return removed;
	}
	
	/**
	 * Delete packages for release.
	 *
	 * @param release the release
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	private int deletePackagesForRelease(Release release) throws DatabaseServiceException{
		
		EntityManager em = createNewManager();
		int removed = 0;
		try {

			Release daoRelease = em.find(Release.class, release.getInternalId());
			
			if(daoRelease==null)
				throw new Exception("Release with id "+release.getInternalId() +" not found");
			
			//REMOVE PACKAGES ONE SHOT
			em.getTransaction().begin();
			Query query = em.createQuery("DELETE FROM "+tableName+" p WHERE p."+Package.RELEASE +"= :release");
			removed = query.setParameter("release", daoRelease).executeUpdate();
			em.getTransaction().commit();
			logger.trace("DELETED FROM Package " + removed + " items");

		} catch (Exception e) {
			logger.error("Error in Package - deletePackagesForRelease: " + e.getMessage(), e);
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}

		return removed;
	}
	
	/**
	 * Delete package for id.
	 *
	 * @param packageID the package id
	 * @param deleteAccounting the delete accounting
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	public int deletePackageForID(String packageID, boolean deleteAccounting) throws DatabaseServiceException {
		EntityManager em = createNewManager();
		int removed = 0;
		try {

			em.getTransaction().begin();
			Query query = em.createQuery("DELETE FROM "+tableName+" p WHERE p."+Package.ID_FIELD +"= :packageID");
			removed = query.setParameter("packageID", packageID).executeUpdate();
			em.getTransaction().commit();
			logger.trace("DELETED FROM Package " + removed + " items");
			
			if(deleteAccounting)
				accountingPersistence.deleteAccountingPackageForPackageRef(packageID);

		} catch (Exception e) {
			logger.error(
					"Error in Package - deletePackageForID: " + e.getMessage(),
					e);
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}

		return removed;
	}
	
	/**
	 * Delete package for internal id.
	 *
	 * @param internalId the internal id
	 * @param deleteAccounting the delete accounting
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	public int deletePackageForInternalId(int internalId, boolean deleteAccounting) throws DatabaseServiceException {
		EntityManager em = createNewManager();
		int removed = 0;
		try {
			
			Package pck = em.find(Package.class, internalId);
			
			if(pck==null)
				throw new Exception("Package with internalId: "+internalId +" not found");
			
			int accountingId = pck.getAccouting().getInternalId();

			em.getTransaction().begin();
			em.remove(pck);
			em.getTransaction().commit();
			removed = 1;
			logger.trace("DELETED FROM Package " + removed + " items");
			
			if(deleteAccounting){
				List<String> accountingIDs = new ArrayList<String>(1);
				accountingIDs.add(accountingId+"");
				accountingPersistence.deleteAccountingForInternalIDs(accountingIDs);
			}

		} catch (Exception e) {
			logger.error(
					"Error in Package - deletePackageForInternalId: " + e.getMessage(),
					e);
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}

		return removed;
	}
	
	/**
	 * Delete all packages.
	 *
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	public int deleteAllPackages() throws DatabaseServiceException {
		EntityManager em = createNewManager();
		int removed = 0;
		try {

			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM "+tableName).executeUpdate();
			em.getTransaction().commit();
			logger.trace("DELETED FROM Package " + removed + " items");

		} catch (Exception e) {
			logger.error(
					"Error in Package - deleteAllPackages: " + e.getMessage(),
					e);
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}

		return removed;
	}
	
	/**
	 * Gets the package rows.
	 *
	 * @return the package rows
	 * @throws DatabaseServiceException the database service exception
	 */
	public List<Package> getPackageRows() throws DatabaseServiceException {
		EntityManager em = createNewManager();
		List<Package> rows = new ArrayList<Package>();
		try {
			Query query = em.createQuery("select t from "+tableName+" t");
			rows = query.getResultList();
		} catch (Exception e) {
			logger.error("Error in getPackageRows: " + e.getMessage(), e);
		} finally {
			em.close();
		}
		return rows;
	}

	/**
	 * Delete packages for release id ref.
	 *
	 * @param releaseId the release id
	 * @return the int
	 * @throws DatabaseServiceException the database service exception
	 */
	public int deletePackagesForReleaseIdRef(String releaseId) throws DatabaseServiceException {
		EntityManager em = createNewManager();
		int removed = 0;
		try {

			em.getTransaction().begin();
			removed = em.createQuery("DELETE FROM "+tableName+" p WHERE p."+Package.RELEASE_ID_REF +"= '"+releaseId+"'").executeUpdate();
			em.getTransaction().commit();
			logger.trace("DELETED FROM Package " + removed + " items");

		} catch (Exception e) {
			logger.error(
					"Error in Package - deleteAllPackages: " + e.getMessage(),
					e);
		} finally {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			em.close();
		}

		return removed;
	}
	
	/**
	 * Gets the package ordered.
	 *
	 * @param releaseId the release id
	 * @param orderByField the order by field
	 * @param equalMapFilter the equal map filter
	 * @return the package ordered
	 * @throws DatabaseServiceException the database service exception
	 */
	
	static int increment = 1;
	
	/**
	 * Gets the package ordered.
	 *
	 * @param releaseId the release id
	 * @param orderByField the order by field
	 * @param equalMapFilter the equal map filter
	 * @return the package ordered
	 * @throws DatabaseServiceException the database service exception
	 */
	public List<Package> getPackageOrdered(String releaseId, String orderByField, Map<String, String> equalMapFilter) throws DatabaseServiceException {
		EntityManager em = createNewManager();
		List<Package> rows = new ArrayList<Package>();
		try {

			String queryString = "Select p FROM Package p where p.releaseIdRef = '"+releaseId+"'";
			if (equalMapFilter != null && equalMapFilter.size() > 0) {
				for (String param : equalMapFilter.keySet()) {
					queryString += " "+AND;
					String value = equalMapFilter.get(param);
					queryString += " p." + param + "='" + value+"'";	
				}
			}		
			queryString+= " order by p."+orderByField;
			
			/*Query query = em.createQuery(queryString);
			rows = query.getResultList();*/

			ThreadPoolQuery<Package> pool = ThreadPoolMng.getInstance("getPackageOrdered", entityManagerFactory);

			ThreadWorker<Package> worker = pool.createWorker(queryString);
			if(worker==null)
				throw new Exception("An error occurred when instancing worker");
			
			pool.executeWorker(worker);
			
			synchronized(worker){
	            try{
	            	 logger.trace("Waiting for ThreadWorker complete...");
	            	 worker.wait();
	            }catch(InterruptedException e){
	            	logger.error("Waiting for ThreadWorker error: ",e);
	            }
	            logger.trace("ThreadWorker completed");
	            rows = worker.getQueryResults();
	        }
			
//			pool.getExecutor().shutdown();
			
		} catch (Exception e) {
			logger.error("Error in " + tableName + " - getRows: " + e.getMessage(), e);

		} finally {
			em.close();
		}
		
		logger.trace("Returning "+rows.size()+" row/s");
		return rows;
	}
	
	/**
	 * Gets the package ordered like filter.
	 *
	 * @param releaseId the release id
	 * @param orderByField the order by field
	 * @param likeMapFilter the like map filter
	 * @return the package ordered like filter
	 * @throws DatabaseServiceException the database service exception
	 */
	public List<Package> getPackageOrderedLikeFilter(String releaseId, String orderByField, Map<String, String> likeMapFilter) throws DatabaseServiceException {
		EntityManager em = createNewManager();
		List<Package> rows = new ArrayList<Package>();
		try {

			String queryString = "Select pp FROM Package pp where pp.releaseIdRef = '"+releaseId+"'";
			if (likeMapFilter != null && likeMapFilter.size() > 0) {
				for (String param : likeMapFilter.keySet()) {
					queryString += " "+AND;
					String value = likeMapFilter.get(param);
					queryString += " pp." + param + " LIKE '%" + value+"%'";	
				}
			}
			
			queryString+= " order by pp."+orderByField;
			
			Query query = em.createQuery(queryString);
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


	/**
	 * Gets the accounting persistence.
	 *
	 * @return the accounting persistence
	 */
	public AccountingPersistence getAccountingPersistence() {
		return accountingPersistence;
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
	public int removeRelations(Package item) {
		// TODO Auto-generated method stub
		return 0;
	}
}
