/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.portlets.admin.gcubereleases.server.GcubeReleasesServiceImpl;
import org.gcube.portlets.admin.gcubereleases.shared.Release;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 7, 2015
 *
 */
public class Fill_DB {
	
	public static GcubeReleasesServiceImpl buildReportServiceImpl = new GcubeReleasesServiceImpl();
	public static boolean onLine = true;
	public static Logger logger = Logger.getLogger(Fill_DB.class);
	
	public static void main(String[] args) {
		
		String releaseID;
		String releaseName;
		String url;
		String description;
		
		/*
		int releaseMax = 3;
		int releaseMin = 1;
		int minorMax = 17;
		int minorMin = 0;
		int revisionMax = 4;
		int revisionMin = 0;
		int totalInsert = 0;
//		final File folder = new File("//home/francesco-mangiacrapa/Downloads/distribution/org.gcube.3-0-0");
		
		for(int rl = releaseMin; rl<=releaseMax; rl++){
			for(int m= minorMin; m<=minorMax; m++){
				for(int rv=revisionMin; rv<=revisionMax; rv++){
					releaseID = "org.gcube."+rl+"-"+m+"-"+rv;
//					url = "//home/francesco-mangiacrapa/Downloads/distribution/"+releaseID+"/latest/reports/distribution/distribution.xml";
					String pathname = "/home/francesco-mangiacrapa/Downloads/distribution/"+releaseID;
					try{
						logger.debug("Tentative to: "+releaseID +", pathname: "+pathname);
						File folder = new File(pathname);
						if(folder.exists()){
							List<String> folders = listFolders(folder);
							Collections.sort(folders, ALPHABETICAL_ORDER);
							logger.trace(folders.toString());
							
							int lastDistribution = folders.size() - 1;
							if(lastDistribution<0)
								break;
							
							String build = folders.get(lastDistribution);
							logger.trace("Adding build: "+build +", for release id: "+releaseID);
							releaseName = "GCube "+rl+"."+m+"."+rv;
							description = "description GCube "+rl+"."+m+"."+rv;
//							url = "//home/francesco-mangiacrapa/Downloads/distribution/"+releaseID+"/latest/reports/distribution/distribution.xml";
							url = pathname+"/"+build+"/reports/distribution/distribution.xml";
							logger.trace("releaseName: "+releaseName);
							logger.trace("description: "+description);
							logger.trace("url: "+url);
							totalInsert++;

							storeReleaseFromLocal(releaseID, releaseName, description, url,null);
						}else
							throw new Exception("");
					}catch(Exception e){
						logger.warn("pathname not exists: "+pathname +", skipping release: "+releaseID);
					}
				}
			}
		}
		
		System.out.println("finish, total insert "+totalInsert);
		*/
		
//		String releaseID;
//		String releaseName;
//		String url;
//		String description;
		try {
			
			/*int releaseMax = 3;
			int releaseMin = 1;
			int minorMax = 17;
			int minorMin = 0;
			int revisionMax = 4;
			int revisionMin = 0;
			
			for(int rl = releaseMin; rl<=releaseMax; rl++){
				for(int m= minorMin; m<=minorMax; m++){
					for(int rv=revisionMin; rv<=revisionMax; rv++){
						releaseID = "org.gcube."+rl+"-"+m+"-"+rv;
						url = "http://grids16.eng.it/BuildReport/bdownload/Recent_Builds/"+releaseID+"/latest/reports/distribution/distribution.xml";
						logger.debug("Tentative to: "+releaseID);
						if(HttpRequestUtil.urlExists(url)){
							releaseName = "GCube "+rl+"."+m+"."+rv;
							description = "description GCube "+rl+"."+m+"."+rv;
//							storeRelease(releaseID, releaseName, description, url);
						}else
							logger.warn("Skipping release: "+releaseID);
					}
				}
			}*/
			
			releaseID = "org.gcube.3-6-0";
			releaseName = "GCube 3.6.0";
			url = "http://eticsbuild2.research-infrastructures.eu/BuildReport/bdownload/AllBuilds/org.gcube.3-6-0/BUILD_14/reports/distribution/distribution.xml";
			description = "description GCube 3.6.0";
			

			storeRelease(releaseID, releaseName, description, url, null);

			//DELETE RELEASE
//			BuildReportServiceImpl servlet = new BuildReportServiceImpl();

//			servlet.deleteRelease(release);
			
			/*
			DaoGcubeBuilderReportDBManager<Release> daoManager = new DaoGcubeBuilderReportDBManager<Release>();
			daoManager.instanceReleaseEntity();
			
			Release release = new Release();
			release.setInternalId(9);
			
			//RETRIEVE LIST PACKAGES
			EntityManager em = daoManager.getJavaPersistenceHandler().createNewManager();
			Query query = em.createQuery("select p FROM Package p WHERE p."+Package.RELEASE +"= :release");
			List<Package> listPackages = (List<Package> ) query.setParameter("release", release).getResultList();
			*/
//			daoManager.getJavaPersistenceHandler().r
//			Root<Bewerbung> bewerbung = criteriaQuery.from(Bewerbung.class);
//			Expression<Collection<String>> collections = new E
//			
//			cb.createQuery().where(i2)
//			Predicate i2 = param.in("English", "French");
			
			/*
			List<String> listString = new ArrayList<String>();
			for (Package pck : listPackages) {
				listString.add(pck.getID());
			}
			
			System.out.println("Numbs ids: "+listString.size());

			em.getTransaction().begin();
			Query query2 = em.createQuery("DELETE FROM AccountingPackage p WHERE p."+AccountingPackage.PACKAGE_REF +" IN :p");

			int deletedCount = query2.setParameter("p", listString).executeUpdate();
			em.getTransaction().commit();
			System.out.println(deletedCount);
			*/
//			daoManager.getReleasePersistenceEntity().getPackagesPersistence().deleteAllPackages();
//			daoManager.getReleasePersistenceEntity().removeAll();

//			List<Package> listPackages = daoManager.getReleasePersistenceEntity().getPackageForID("064a8d30-6f0b-11e4-b5fa-c9391d74ca82");
//			
//			for (Package package1 : listPackages) {
//				package1.getAccouting().setDownloadNmb(1);
//				daoManager.getReleasePersistenceEntity().updateAccountingForPackageId(package1.getAccouting());
//			}
//			
//			daoManager.getReleasePersistenceEntity().getMapFieldGroupedBy("groupID");
			
//			daoManager.getReleasePersistenceEntity().deleteAllAccountingPackages();
//			daoManager.getReleasePersistenceEntity().deletePackagesForID("7dda6970-6efb-11e4-b5f9-c9391d74ca82", true);
//			daoManager.getReleasePersistenceEntity().deleteAccountingPackageForPackageRef("a000b130-6f0f-11e4-b5fb-c9391d74ca82");
			
			
//			List<Release> releases = daoManager.getReleasePersistenceEntity().getRows();
			
//			for (Release release : releases) {
//				if(release.getInternalId()==3){
//					daoManager.getReleasePersistenceEntity().deletePackagesForRelease(release);
////					daoManager.getDaoUpdater().deleteItemByInternalId(3);
//				}
//			}
////			daoManager.getReleasePersistenceEntity().deleteAllAccountingPackages();
			
			
//			List<Package> packages = daoManager.getReleasePersistenceEntity().getPackageRows();
//			int i = 1;
//			for (Package package1 : packages) {
//				System.out.println(i++ +" "+package1);
//			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
  }
	
	public static void readFromEtics(){
		String releaseID;
		String releaseName;
		String url;
		String description;
		try {
			
			int releaseMax = 3;
			int releaseMin = 1;
			int minorMax = 17;
			int minorMin = 0;
			int revisionMax = 4;
			int revisionMin = 0;
			
			for(int rl = releaseMin; rl<=releaseMax; rl++){
				for(int m= minorMin; m<=minorMax; m++){
					for(int rv=revisionMin; rv<=revisionMax; rv++){
						releaseID = "org.gcube."+rl+"-"+m+"-"+rv;
						url = "http://grids16.eng.it/BuildReport/bdownload/Recent_Builds/"+releaseID+"/latest/reports/distribution/distribution.xml";
						logger.debug("Tentative to: "+releaseID);
						if(HttpRequestUtil.urlExists(url)){
							releaseName = "GCube "+rl+"."+m+"."+rv;
							description = "description GCube "+rl+"."+m+"."+rv;
//							storeRelease(releaseID, releaseName, description, url);
						}else
							logger.warn("Skipping release: "+releaseID);
					}
				}
			}
		}catch(Exception e){
			logger.error(e);
		}
	}
	
	public static List<String> listFolders(final File folder) {
		List<String> folders = new ArrayList<String>();
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	folders.add(fileEntry.getName());
//	        	listFolder(fileEntry);
//	        	System.out.println("Folder name: "+fileEntry.getName());
	        } else {
	        	System.out.println("is file: "+fileEntry.getName());
	        }
	    }
	    
	    return folders;
	}
	
	private static Comparator<String> ALPHABETICAL_ORDER = new Comparator<String>() {
	    public int compare(String str1, String str2) {
	    	
	    	if(str1.length()<str2.length())
	    		return -1;
	    	if(str1.length()>str2.length())
	    		return 1;
	    	
	        int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
//	        System.out.println("Comparing: "+str1 +" "+str2+", res: "+res);
	        if (res == 0) {
	            res = str1.compareTo(str2);
	        }
	        return res;
	    }
	};

	
//	listFilesForFolder(folder);

	/**
	 * Store release.
	 *
	 * @param releaseID the release id
	 * @param releaseName the release name
	 * @param description the description
	 * @param url the url
	 * @param now 
	 */
	private static void storeRelease(String releaseID, String releaseName, String description, String url, Long releaseDate) {

		Release release = new Release(releaseID, releaseName, url, releaseDate);
		release.setDescription(description);
		release.setOnLine(onLine);
		try {
			//TODO IT MUST BE ACTIVATED DB OFF-LINE MODE
			buildReportServiceImpl.storeReleaseIntoDB(release);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Store release.
	 *
	 * @param releaseID the release id
	 * @param releaseName the release name
	 * @param description the description
	 * @param url the url
	 */
	private static void storeReleaseFromLocal(String releaseID, String releaseName, String description, String url, Long releaseDate) {

		Release release = new Release(releaseID, releaseName, url, releaseDate);
		release.setDescription(description);
		release.setOnLine(onLine);
		try {
			//TODO IT MUST BE ACTIVATED DB OFF-LINE MODE
			buildReportServiceImpl.storeLocalReleaseIntoDB(release);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
