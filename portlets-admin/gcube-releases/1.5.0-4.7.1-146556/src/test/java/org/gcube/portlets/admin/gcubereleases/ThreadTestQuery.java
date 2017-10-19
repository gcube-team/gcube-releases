/**
 *
 */
package org.gcube.portlets.admin.gcubereleases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.gcube.portlets.admin.gcubereleases.server.database.DaoGcubeBuilderReportDBManager;
import org.gcube.portlets.admin.gcubereleases.server.database.EntityManagerFactoryCreator;
import org.gcube.portlets.admin.gcubereleases.server.exception.DatabaseServiceException;
import org.gcube.portlets.admin.gcubereleases.server.persistence.ReleasePersistence;
import org.gcube.portlets.admin.gcubereleases.server.pool.ThreadPoolMng;
import org.gcube.portlets.admin.gcubereleases.server.pool.ThreadPoolQuery;
import org.gcube.portlets.admin.gcubereleases.server.pool.ThreadWorker;
import org.gcube.portlets.admin.gcubereleases.shared.AccountingPackage;
import org.gcube.portlets.admin.gcubereleases.shared.AccountingReport;
import org.gcube.portlets.admin.gcubereleases.shared.AccoutingReference;
import org.gcube.portlets.admin.gcubereleases.shared.Package;
import org.gcube.portlets.admin.gcubereleases.shared.Release;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 2, 2015
 */
public class ThreadTestQuery {

	protected static Logger logger = LoggerFactory.getLogger(ThreadTestQuery.class);
	private static EntityManagerFactoryCreator mng;
	private static EntityManagerFactory entityManagerFactory;

	public static void main(String[] args) throws Exception {
//		new ThreadTestQuery();
		getAccountingReportForRelease(20);
	}

	public static void getAccountingReportForRelease(int releaseInternalId){

		logger.trace("Get AccountingReportForRelease..");
		try{

			mng = EntityManagerFactoryCreator.getInstanceTestMode("/gcube");
			entityManagerFactory = mng.getEntityManagerFactory();
			DaoGcubeBuilderReportDBManager<Release> daoManager = new DaoGcubeBuilderReportDBManager<Release>(entityManagerFactory);
			daoManager.instanceReleaseEntity();

			ReleasePersistence releasePeristence = daoManager.getReleasePersistenceEntity();
			EntityManager em = null;

			try{
				em = releasePeristence.createNewManager();

				Release daoRelease = em.find(Release.class, releaseInternalId);

				List<Package> packages = daoRelease.getListPackages();

				//RETRIEVE LIST PACKAGE IDS
				String queryString = "select p.internalId"
						+ " FROM "+Package.class.getSimpleName()+" p"
						+ " WHERE p."+Package.RELEASE +"= :release";

				Query query = em.createQuery(queryString);
				List<String> packageIds = query.setParameter("release", daoRelease).getResultList();

				//RETRIEVE LIST ACCOUNTING IDS
				queryString = "select p."+Package.ACCOUNTING+".internalId"
						+ " FROM "+Package.class.getSimpleName()+" p"
						+ " WHERE p.internalId IN :packages";

				query = em.createQuery(queryString);
				List<Integer> listAccountingIds = query.setParameter("packages", packageIds).getResultList();

				//RETRIEVE LIST ACCOUNTING IDS
				queryString = "select sum(p.downloadNmb) as totaldownloadNmb, sum(p.javadocNmb) as totaljavadocNmb,  sum(p.sourcecodeNmb) as totalsourcecodeNmb, sum(p.wikiNmb) as totalwikiNmb, sum(p.gitHubNmb) as totalgitHubNmb"
						+ " FROM "+AccountingPackage.class.getSimpleName()+" p"
						+ " WHERE p.internalId IN :accountings";

				query = em.createQuery(queryString);
				Object[] sums = (Object[]) query.setParameter("accountings", listAccountingIds).getSingleResult();

				AccountingReport report = new AccountingReport();
				report.put(AccoutingReference.DOWNLOAD, (int) (long) sums[0]);
				report.put(AccoutingReference.JAVADOC, (int) (long) sums[1]);
				report.put(AccoutingReference.MAVEN_REPO, (int) (long) sums[2]);
				report.put(AccoutingReference.WIKI, (int) (long) sums[3]);
				report.put(AccoutingReference.GITHUB, (int) (long) sums[4]);

				System.out.println(report);

			}catch (Exception e) {
				logger.error("getAccountingReportForRelease error: ",e);
			}finally{
				if(em!=null)
					em.close();
			}
		}catch(Exception e){
			logger.warn("getAccountingReportForRelease error: ",e);
		}

	}

	public void startNewThread(final String subsystem){

		new Thread(){
			@Override
			public void run() {

				try {
					Map<String, String> mapFilter = new HashMap<String, String>();
//					mapFilter.put("groupID", "org.gcube.portlets.admin");
					mapFilter.put("groupID", subsystem);
					List<Package> packages = getPackageOrdered("org.gcube.3-5-0", "artifactID", mapFilter);

					System.out.println("packages found: "+packages.size());
				} catch (DatabaseServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();
	}


	public ThreadTestQuery() throws Exception {
		mng = EntityManagerFactoryCreator.getInstanceTestMode("/gcube");
		entityManagerFactory = mng.getEntityManagerFactory();

		startNewThread("org.gcube.data.spd");
		startNewThread("org.cotrix");
		startNewThread("org.gcube.dataanalysis");
		startNewThread("org.gcube.vremanagement");
		startNewThread("org.gcube.data.spd");
		startNewThread("org.cotrix");
		startNewThread("org.gcube.dataanalysis");
		startNewThread("org.gcube.vremanagement");
		startNewThread("org.gcube.application");
		startNewThread("org.gcube.index");
		startNewThread("workspace-index-updater-service");
		startNewThread("org.gcube.core");
		startNewThread("org.gcube.common.rest");
		startNewThread("org.gcube.portal");
		startNewThread("org.gcube.data.publishing");


		startNewThread("org.gcube.data.spd");
		startNewThread("org.cotrix");
		startNewThread("org.gcube.dataanalysis");
		startNewThread("org.gcube.vremanagement");
		startNewThread("org.gcube.data.spd");
		startNewThread("org.cotrix");
		startNewThread("org.gcube.dataanalysis");
		startNewThread("org.gcube.vremanagement");
		startNewThread("org.gcube.application");
		startNewThread("org.gcube.index");
		startNewThread("workspace-index-updater-service");
		startNewThread("org.gcube.core");
		startNewThread("org.gcube.common.rest");
		startNewThread("org.gcube.portal");
		startNewThread("org.gcube.data.publishing");


		startNewThread("org.gcube.data.spd");
		startNewThread("org.cotrix");
		startNewThread("org.gcube.dataanalysis");
		startNewThread("org.gcube.vremanagement");
		startNewThread("org.gcube.data.spd");
		startNewThread("org.cotrix");
		startNewThread("org.gcube.dataanalysis");
		startNewThread("org.gcube.vremanagement");
		startNewThread("org.gcube.application");
		startNewThread("org.gcube.index");
		startNewThread("workspace-index-updater-service");
		startNewThread("org.gcube.core");
		startNewThread("org.gcube.common.rest");
		startNewThread("org.gcube.portal");
		startNewThread("org.gcube.data.publishing");

		startNewThread("org.gcube.data.spd");
		startNewThread("org.cotrix");
		startNewThread("org.gcube.dataanalysis");
		startNewThread("org.gcube.vremanagement");
		startNewThread("org.gcube.data.spd");
		startNewThread("org.cotrix");
		startNewThread("org.gcube.dataanalysis");
		startNewThread("org.gcube.vremanagement");
		startNewThread("org.gcube.application");
		startNewThread("org.gcube.index");
		startNewThread("workspace-index-updater-service");
		startNewThread("org.gcube.core");
		startNewThread("org.gcube.common.rest");
		startNewThread("org.gcube.portal");
		startNewThread("org.gcube.data.publishing");

		System.out.println("Thread main waiting..");
		Thread.sleep(20000);
		System.out.println("Thread main Stop");

		/*org.gcube.data.harmonization.occurrence
		org.gcube.search.sru
		org.gcube.personalisation
		org.gcube.informationsystem
		org.gcube.index
		org.gcube.application
		workspace-index-updater-service
		org.gcube.opensearch
		org.gcube.data.analysis
		org.gcube.portlets.user.tdtemplate
		org.gcube.core
		org.gcube.messaging.accounting.portal
		org.gcube.resourcemanagement
		org.gcube.resources
		org.gcube.dataanalysis
		org.gcube.portal
		org.gcube.data.access
		org.gcube.vremanagement
		org.gcube.common
		org.gcube.data.publishing
		org.gcube.common.portal
		org.gcube.spatial.data
		org.gcube.data-transformation
		org.gcube.dbinterface
		org.gcube.dataaccess
		org.gcube.common.rest
		org.gcube.data.fishfinder.tmplugin
		org.gcube.portlets.widgets
		org.gcube.applicationsupportlayer
		org.gcube.data.analysis.tabulardata
		org.gcube.data.spd
		org.gcube.execution
		org.gcube.dataaccess.algorithms
		org.gcube.portlets.user
		org.gcube.messaging
		org.gcube.dvos
		org.gcube.portlet
		org.gcube.portlets.admin.manageusers
		org.gcube.externalsoftware
		org.gcube.data.transfer
		org.gcube.portlets-admin
		${groupId}
		org.gcube.dvos.soa3
		org.gcube.g_cqlparser
		org.gcube-portlets.user.tdcolumnoperation
		org.gcube.commons
		org.gcube.data.oai.tmplugin
		org.gcube.resources.discovery
		org.gcube.distribution
		org.gcube.contentmanagement
		org.gcube.portlets.user.tdcolumnoperation
		org.gcube.portlets.admin
		org.gcube.search
		org.gcube.datatransfer
		org.gcube.portlet.user
		org.gcube.accounting*/


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
	public List<Package> getPackageOrdered(String releaseId, String orderByField, Map<String, String> equalMapFilter) throws DatabaseServiceException {
		List<Package> rows = new ArrayList<Package>();
		try {

//			increment++;
//			if(increment>=4)
//				Thread.sleep((increment)*200+1000);

			String queryString = "Select p FROM Package p where p.releaseIdRef = '"+releaseId+"'";
			if (equalMapFilter != null && equalMapFilter.size() > 0) {
				for (String param : equalMapFilter.keySet()) {
					queryString += " AND";
					String value = equalMapFilter.get(param);
					queryString += " p." + param + "='" + value+"'";
				}
			}
			queryString+= " order by p."+orderByField;

			/*Query query = em.createQuery(queryString);
			rows = query.getResultList();*/

			ThreadPoolQuery<Package> pool = ThreadPoolMng.getInstance("getPackageOrdered", entityManagerFactory);
			ThreadWorker<Package> worker = pool.createWorker(queryString);
			pool.executeWorker(worker);

			synchronized(worker){
	            try{
	                logger.info("Waiting for ThreadWorker complete...");
	                worker.wait();
//	                worker.wait(3000);
	            }catch(InterruptedException e){
	            	logger.error("Waiting for ThreadWorker error: ",e);
	            }

	            logger.info("ThreadWorker completed");
	            rows = worker.getQueryResults();
	        }

			pool.getExecutor().shutdown();

		} catch (Exception e) {
			logger.error("Error in package - getRows: " + e.getMessage(), e);

		}

		logger.trace("Returning "+rows.size()+" row/s");
		return rows;
	}

}
