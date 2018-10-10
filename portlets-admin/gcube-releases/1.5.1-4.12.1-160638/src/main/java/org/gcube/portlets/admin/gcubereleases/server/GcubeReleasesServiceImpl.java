package org.gcube.portlets.admin.gcubereleases.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesService;
import org.gcube.portlets.admin.gcubereleases.server.converter.EticsReportConverter;
import org.gcube.portlets.admin.gcubereleases.server.database.DaoGcubeBuilderReportDBManager;
import org.gcube.portlets.admin.gcubereleases.server.database.EntityManagerFactoryCreator;
import org.gcube.portlets.admin.gcubereleases.server.exception.DatabaseServiceException;
import org.gcube.portlets.admin.gcubereleases.server.persistence.AbstractPersistence.SQL_ORDER;
import org.gcube.portlets.admin.gcubereleases.server.persistence.AccountingPersistence;
import org.gcube.portlets.admin.gcubereleases.server.persistence.PackagePersistence;
import org.gcube.portlets.admin.gcubereleases.server.persistence.ReleaseFilePersistence;
import org.gcube.portlets.admin.gcubereleases.server.persistence.ReleasePersistence;
import org.gcube.portlets.admin.gcubereleases.server.util.HttpCallerUtil;
import org.gcube.portlets.admin.gcubereleases.server.util.LiferayUserUtil;
import org.gcube.portlets.admin.gcubereleases.server.util.ReadFile;
import org.gcube.portlets.admin.gcubereleases.server.util.ScopeUtil;
import org.gcube.portlets.admin.gcubereleases.server.util.ScopeUtilFilter;
import org.gcube.portlets.admin.gcubereleases.server.util.StringUtils;
import org.gcube.portlets.admin.gcubereleases.shared.AccountingPackage;
import org.gcube.portlets.admin.gcubereleases.shared.AccountingReport;
import org.gcube.portlets.admin.gcubereleases.shared.AccoutingReference;
import org.gcube.portlets.admin.gcubereleases.shared.Package;
import org.gcube.portlets.admin.gcubereleases.shared.Release;
import org.gcube.portlets.admin.gcubereleases.shared.ReleaseFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;


/**
 * The Class GcubeReleasesServiceImpl.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
@SuppressWarnings("serial")
public class GcubeReleasesServiceImpl extends RemoteServiceServlet implements GcubeReleasesService {

	protected static Logger logger = LoggerFactory.getLogger(GcubeReleasesServiceImpl.class);

	protected EntityManagerFactory entityManagerFactory;

	/**
	 * Gets the DB manager.
	 *
	 * @return the DB manager
	 * @throws Exception the exception
	 */
	public synchronized DaoGcubeBuilderReportDBManager<Release> getDBManager() throws Exception{
		ASLSession asl = ScopeUtil.getAslSession(this.getThreadLocalRequest().getSession());

		try{
			if(entityManagerFactory==null)
				entityManagerFactory = instanceUniqueFactoryForDB();

		}catch(Exception e){
			logger.error("An error occurred when creating Entity Factory", e);
			throw new Exception("Sorry, an error occurred on contacting Gcube Release DB");
		}

		return ScopeUtil.getDbMangerForRelease(asl, entityManagerFactory);
	}

	/**
	 * Instance unique factory for db.
	 *
	 * @return the entity manager factory
	 * @throws Exception the exception
	 */
	private EntityManagerFactory instanceUniqueFactoryForDB() throws Exception{
		ASLSession asl = ScopeUtil.getAslSession(this.getThreadLocalRequest().getSession());
		logger.info("ScopeUtilFilter working on scope: "+asl.getScope().toString());
		ScopeUtilFilter scopeUtilFilter = new ScopeUtilFilter(asl.getScope().toString(), false);

		/*
		//TODO TEST MODE!!!!
		if(!ScopeUtil.isWithinPortal()){
			logger.info("Instancing EntityManagerFactoryCreator TEST MODE");
			EntityManagerFactoryCreator.getInstanceTestMode(scopeUtilFilter.getScopeRoot());
		}else
			EntityManagerFactoryCreator.getInstance(scopeUtilFilter.getScopeRoot());
		// END TEST MODE
		*/

		//TODO PRODUCTION MODE
		EntityManagerFactoryCreator.getInstance(scopeUtilFilter.getScopeRoot());

		return EntityManagerFactoryCreator.getEntityManagerFactory();
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesService#getReleases(boolean)
	 */
	@Override
	public List<Release> getReleases(boolean onlyOnline) throws Exception{

		DaoGcubeBuilderReportDBManager<Release> daoManager = getDBManager();
		try {
			if(daoManager==null)
				throw new Exception("DaoManger is null");

			List<Release> releases = daoManager.getDaoViewer().getRowsOrdered("endReleaseDate", SQL_ORDER.DESC);

			//TO FIX Type 'org.eclipse.persistence.indirection.IndirectList'
			//was not included in the set of types which can be serialized
			//by this SerializationPolicy or its Class object could not be loaded.
			//For security purposes, this type will not be serialized.: instance = {IndirectList: not instantiated}

			List<Release> results = new ArrayList<Release>(releases.size());
			for (Release release2 : releases) {
				release2.setListPackages(null);
				if(onlyOnline){
					if(release2.isOnLine())
						results.add(release2);
				}else
					results.add(release2);
			}

//			logger.trace("Returning releases: ");
//			for (Release release : results) {
//				logger.trace(release.toString());
//			}
			return results;
		} catch (DatabaseServiceException e) {
			logger.error("getReleases error: ",e);
			throw new Exception("Sorry, an error occurred on reconvering releases, try again later");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.client.rpc.BuildReportService#getSubsystemsForReleaseID(java.lang.String)
	 */
	@Override
	public Map<String, Long> getSubsystemsForReleaseID(String releaseID) throws Exception {

		DaoGcubeBuilderReportDBManager<Release> daoManager = getDBManager();

		try {
			if(daoManager==null)
				throw new Exception("DaoManger is null");

			return daoManager.getReleasePersistenceEntity().getPackagesPersistence().getMapFieldGroupedBy(releaseID, "groupID");
		} catch (DatabaseServiceException e) {
			logger.error("getSubsystemsForReleaseID error: ",e);
			throw new Exception("Sorry, an error occurred on reconvering releases, try again later");
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.client.rpc.BuildReportService#getReleaseByID(java.lang.String)
	 */
	@Override
	public Release getReleaseByID(String releaseID) throws Exception {

		DaoGcubeBuilderReportDBManager<Release> daoManager = getDBManager();

		try {
			if(daoManager==null)
				throw new Exception("DaoManger is null");

			HashMap<String, String> where = new HashMap<String, String>();
			where.put(Release.ID_FIELD, releaseID);
			List<Release> releases = daoManager.getReleasePersistenceEntity().getRowsFiltered(where);

			if(releases==null || releases.size()==0)
				throw new Exception("Release with id "+releaseID +" not found");

			Release release = releases.get(0);
//			release.getListPackages().size();

			//TO FIX Type 'org.eclipse.persistence.indirection.IndirectList'
			//was not included in the set of types which can be serialized
			//by this SerializationPolicy or its Class object could not be loaded.
			//For security purposes, this type will not be serialized.: instance = {IndirectList: not instantiated}
			release.setListPackages(null);

//			release.setDescription(StringUtils.toTabularHTML(release.getDescription()));
			return release;

		} catch (DatabaseServiceException e) {
			logger.error("getReleaseByID error: ",e);
			throw new Exception("Sorry, an error occurred on reconvering releases, try again later");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesService#getPackagesForReleaseID(org.gcube.portlets.admin.gcubereleases.shared.Release)
	 */
	@Override
	public List<Package> getPackagesForReleaseID(Release release) throws Exception{

		DaoGcubeBuilderReportDBManager<Release> daoManager = getDBManager();

		try {
			if(daoManager==null)
				throw new Exception("DaoManger is null");

			List<Package> result = daoManager.getReleasePersistenceEntity().getPackagesPersistence().getPackageOrdered(release.getId(), "groupID", null);


			//FIX Type 'org.eclipse.persistence.indirection.IndirectList'on Release
			for (Package package1 : result) {
				package1.setRelease(null);
			}

			return result;
		} catch (DatabaseServiceException e) {
			logger.error("getPackagesByRelease error: ",e);
			throw new Exception("Sorry, an error occurred on reconvering packages, try again later");
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesService#insertNewRelease(org.gcube.portlets.admin.gcubereleases.shared.Release)
	 */
	@Override
	public boolean insertNewRelease(Release release) throws Exception{

		if(StringUtils.isStringEmpty(release.getId()))
			throw new Exception("Error, mandatory field 'release ID' not found");

		if(StringUtils.isStringEmpty(release.getName()))
			throw new Exception("Error, mandatory field 'release Name' not found");

		if(StringUtils.isStringEmpty(release.getUrl()))
			throw new Exception("Error, mandatory field 'release URL' not found");

		return storeReleaseIntoDB(release);
	}


	/**
	 * Store local release into db.
	 *
	 * @param release the release
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean storeLocalReleaseIntoDB(Release release) throws Exception {

		//UNCOMMENT THIS TO FILL DB OFF-LINE

		ScopeUtilFilter scopeUtilFilter = new ScopeUtilFilter(ScopeUtil.TEST_SCOPE, false);
		EntityManagerFactoryCreator factoryCreator = EntityManagerFactoryCreator.getInstanceTestMode(scopeUtilFilter.getScopeRoot());
		DaoGcubeBuilderReportDBManager<Release> daoManager = new DaoGcubeBuilderReportDBManager<Release>(factoryCreator.getEntityManagerFactory());
		daoManager.instanceReleaseEntity();

		//UNCOMMENT THIS TO FILL DB ON-LINE
//		DaoGcubeBuilderReportDBManager<Release> daoManager = getDBManager();

		File file = new File(release.getUrl());
		String response = "";

		try {

			response = ReadFile.read(file);
//			Release release = new Release(releaseID, releaseName, distributioXmlURL);

			//SETTINGS INSERT TIME AND LATEST UPDATE
			release.setInsertTime(Calendar.getInstance().getTimeInMillis());
			release.setLatestUpdate(Calendar.getInstance().getTimeInMillis());

			logger.info("Converting packages...");
			EticsReportConverter converter = new EticsReportConverter(response, release.getId());
			List<org.gcube.portlets.admin.gcubereleases.shared.Package> listPackage = converter.convertToListPackage(release);

			logger.info("Converted "+listPackage.size()+ " packages");
			logger.info("Storing data into DB...");
			release.setListPackages(listPackage);

			//****PATCH TO ETICS URL
			String url = "http://grids16.eng.it/BuildReport/bdownload/Recent_Builds/"+release.getId()+"/latest/reports/distribution/distribution.xml";
			release.setUrl(url);
			//******************

			daoManager.getDaoUpdater().create(release);

			logger.info("Data stored correctly for: "+release.toString());

			ReleaseFilePersistence releaseFilePersistence = new ReleaseFilePersistence(EntityManagerFactoryCreator.getEntityManagerFactory());
			releaseFilePersistence.create(new ReleaseFile(response, release.getInternalId()));
			logger.info("Release file stored correctly for: "+release.toString());

			List<Release> releases = daoManager.getDaoViewer().getRows();
			logger.info("Numb release: " + releases.size());

			logger.trace("Releases are: "
					+ daoManager.getDaoViewer().countItems());
			logger.trace("PackageRows are: "
					+ daoManager.getReleasePersistenceEntity()
							.getPackagesPersistence().countItems());
			logger.trace("AccountingPackageRows are: "
					+ daoManager.getReleasePersistenceEntity()
							.getPackagesPersistence()
							.getAccountingPersistence().countItems());

		} catch (Exception e) {
			logger.error("An error occurred when stroring data to: "+release.getUrl(), e);
			return false;
		}

		return true;
	}

	/**
	 * Store release into db.
	 *
	 * @param release the release
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean storeReleaseIntoDB(Release release) throws Exception {

		//TODO UNCOMMENT THIS TO FILL DB OFF-LINE
		/*
		ScopeUtilFilter scopeUtilFilter = new ScopeUtilFilter(ScopeUtil.TEST_SCOPE, false);
		EntityManagerFactoryCreator factoryCreator = EntityManagerFactoryCreator.getInstanceTestMode(scopeUtilFilter.getScopeRoot());
		DaoGcubeBuilderReportDBManager<Release> daoManager = new DaoGcubeBuilderReportDBManager<Release>(factoryCreator.getEntityManagerFactory());
		daoManager.instanceReleaseEntity();
		*/

		//TODO UNCOMMENT THIS TO FILL DB ON-LINE
		DaoGcubeBuilderReportDBManager<Release> daoManager = getDBManager();

		HttpCallerUtil httpCaller = new HttpCallerUtil(release.getUrl(), "", "");
		String response = "";

		try {

			response = httpCaller.callGet("", null);
//			Release release = new Release(releaseID, releaseName, distributioXmlURL);

			//SETTINGS INSERT TIME AND LATEST UPDATE
			release.setInsertTime(Calendar.getInstance().getTimeInMillis());
			release.setLatestUpdate(Calendar.getInstance().getTimeInMillis());

			logger.info("Converting packages...");
			EticsReportConverter converter = new EticsReportConverter(response, release.getId());
			List<org.gcube.portlets.admin.gcubereleases.shared.Package> listPackage = converter.convertToListPackage(release);

			logger.info("Converted "+listPackage.size()+ " packages");
			logger.info("Storing data into DB...");
			release.setListPackages(listPackage);
			daoManager.getDaoUpdater().create(release);

			logger.info("Data stored correctly for: "+release.toString());

			ReleaseFilePersistence releaseFilePersistence = new ReleaseFilePersistence(EntityManagerFactoryCreator.getEntityManagerFactory());
			releaseFilePersistence.create(new ReleaseFile(response, release.getInternalId()));
			logger.info("Release file stored correctly for: "+release.toString());

			List<Release> releases = daoManager.getDaoViewer().getRows();
			logger.info("Numb release: " + releases.size());

			logger.trace("Releases are: "
					+ daoManager.getDaoViewer().countItems());
			logger.trace("PackageRows are: "
					+ daoManager.getReleasePersistenceEntity()
							.getPackagesPersistence().countItems());
			logger.trace("AccountingPackageRows are: "
					+ daoManager.getReleasePersistenceEntity()
							.getPackagesPersistence()
							.getAccountingPersistence().countItems());

		} catch (Exception e) {
			logger.error("An error occurred when stroring data to: "+release.getUrl(), e);
			return false;
		}

		return true;
	}



	/**
	 * Force hard update release into db.
	 * Performs the delete and then the create afresh for the release in input
	 * @param release the release
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean forceHardUpdateReleaseIntoDB(Release release) throws Exception {

		boolean deleted = false;

		try{
			deleted = deleteRelease(release);
		}catch(Exception e){
			logger.error("An error occurred when deleting data for release: "+release, e);
			return false;
		}

		if(deleted)
			return storeReleaseIntoDB(release);

		return false;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.client.rpc.BuildReportService#loadPackagesForSubsystem(java.lang.String, java.lang.String)
	 */
	@Override
	public List<Package> loadPackagesForSubsystem(String releaseID, String subsystemID)
			throws Exception {

		DaoGcubeBuilderReportDBManager<Release> daoManager = getDBManager();

		try {
			if(daoManager==null)
				throw new Exception("DaoManger is null");

			Map<String, String> mapFilter = new HashMap<String, String>();
			mapFilter.put("groupID", subsystemID);

			List<Package> packages = daoManager.getReleasePersistenceEntity().getPackagesPersistence().getPackageOrdered(releaseID, "artifactID", mapFilter);

			//FIX Type 'org.eclipse.persistence.indirection.IndirectList'on Release
			for (Package package1 : packages) {
				package1.setRelease(null);
				logger.trace("package debug: "+package1);
			}
			logger.trace("Returning "+packages.size() +" packages for releaseID: "+releaseID +" subsystemID: "+subsystemID);
			return packages;
		} catch (DatabaseServiceException e) {
			logger.error("getPackagesByRelease error: ",e);
			throw new Exception("Sorry, an error occurred on reconvering packages, try again later");
		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesService#updateReleaseInfo(org.gcube.portlets.admin.gcubereleases.shared.Release)
	 */
	@Override
	public Release updateReleaseInfo(Release release, Boolean hardUpdate) throws Exception{

		DaoGcubeBuilderReportDBManager<Release> daoManager = getDBManager();

		try {
			if(daoManager==null)
				throw new Exception("DaoManger is null");

			if(release==null)
				throw new Exception("Release is null");

			logger.info("Trying to update release for key: "+release.getInternalId());
			logger.info("Is a hard update? "+hardUpdate);
			//UPDATING LATEST UPDATE
			release.setLatestUpdate(Calendar.getInstance().getTimeInMillis());

			if(hardUpdate){
				boolean updated = forceHardUpdateReleaseIntoDB(release);

				if(updated){
					logger.info("Hard Update performed correctly for "+release.getName());
					Release daoRelease = daoManager.getReleasePersistenceEntity().updateReleaseInfo(release);
					daoRelease.setListPackages(null); //FIX INDERCT LIST
					return daoRelease;
				}else{
					logger.warn("Sorry, an error occurred on update Release, try again later");
					throw new Exception("Sorry, an error occurred on perfoiming hard update for the Release: "+release.getName()+", try to delete it and create again");
				}

			}

			Release daoRelease = daoManager.getReleasePersistenceEntity().updateReleaseInfo(release);
//			Release daoRelease = daoManager.getReleasePersistenceEntity().update(release);
			daoRelease.setListPackages(null); //FIX INDERCT LIST
			logger.info("DaoRelease updated into DB, returning..");
			return daoRelease;

		} catch (DatabaseServiceException e) {
			logger.error("updateReleaseInfo error: ",e);
			throw new Exception("Sorry, an error occurred on update Release, try again later");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesService#deletePackage(org.gcube.portlets.admin.gcubereleases.shared.Package)
	 */
	@Override
	public boolean deletePackage(Package pck) throws Exception{

		DaoGcubeBuilderReportDBManager<Release> daoManager = getDBManager();

		try {
			if(daoManager==null)
				throw new Exception("DaoManger is null");

			if(pck==null)
				throw new Exception("Package is null");

			logger.info("Trying to delete: "+pck.toString());

			PackagePersistence pckPeristence = daoManager.getReleasePersistenceEntity().getPackagesPersistence();
//			int deleted = pckPeristence.deletePackageForID(pck.getID(), true);
			int deleted = pckPeristence.deletePackageForInternalId(pck.getInternalId(), true);

			logger.info("Deleted? "+(deleted>0));
			return deleted>0;
		} catch (DatabaseServiceException e) {
			logger.error("deletePackage error: ",e);
			throw new Exception("Sorry, an error occurred on delete Package, try again later");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesService#deleteRelease(org.gcube.portlets.admin.gcubereleases.shared.Release)
	 */
	@Override
	public boolean deleteRelease(Release release) throws Exception{
		DaoGcubeBuilderReportDBManager<Release> daoManager = getDBManager();
		//TODO USE TO DEBUG
//		DaoGcubeBuilderReportDBManager daoManager = new DaoGcubeBuilderReportDBManager<Release>();
//		daoManager.instanceReleaseEntity();

		try {
			if(daoManager==null)
				throw new Exception("DaoManger is null");

			if(release==null)
				throw new Exception("release is null");

			logger.info("Trying to delete release: "+release.getId() +" name: "+release.getName());

			int deleted = 0;
			if(release !=null){
				deleted = daoManager.getReleasePersistenceEntity().removeRelations(release);
//				System.out.println("Removed relation (packages)? "+deleted);
				logger.info("Removed relation (packages)? "+deleted);
				deleted = daoManager.getReleasePersistenceEntity().deleteItemByInternalId(release.getInternalId());
//				System.out.println("Removed release? "+(deleted>0));
				logger.info("Removed release? "+(deleted>0));
			}

			return deleted>0;

		} catch (DatabaseServiceException e) {
			logger.error("deleteRelease error: ",e);
			throw new Exception("Sorry, an error occurred on delete Release, try again later");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesService#deletePackages(java.util.List)
	 */
	@Override
	public List<Package> deletePackages(List<Package> listPcks) throws Exception{

		DaoGcubeBuilderReportDBManager<Release> daoManager = getDBManager();

		try {
			if(daoManager==null)
				throw new Exception("DaoManger is null");

			if(listPcks==null || listPcks.size()==0)
				throw new Exception("Package is null or empty");

			List<Package> errors = new ArrayList<Package>();

			for (Package pck : listPcks) {
				try{
					boolean deleted = deletePackage(pck);
					if(!deleted)
						errors.add(pck);
				}catch(Exception e){
					errors.add(pck);
				}
			}
			return errors;

		} catch (DatabaseServiceException e) {
			logger.error("deletePackages error: ",e);
			throw new Exception("Sorry, an error occurred on delete Packages, try again later");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.client.rpc.BuildReportService#filterPackagesForValue(java.lang.String)
	 */
	@Override
	public List<Package> filterPackagesForValue(String releaseID, String filter) throws Exception {

		DaoGcubeBuilderReportDBManager<Release> daoManager = getDBManager();

		try {
			if(daoManager==null)
				throw new Exception("DaoManger is null");

			Map<String, String> likeMapFilter = new HashMap<String, String>();
			likeMapFilter.put("artifactID", filter);

			List<Package> packages = daoManager.getReleasePersistenceEntity().getPackagesPersistence().getPackageOrderedLikeFilter(releaseID, "artifactID", likeMapFilter);

			//FIX Type 'org.eclipse.persistence.indirection.IndirectList'on Release
			for (Package package1 : packages) {
				package1.setRelease(null);
			}
			logger.trace("Returning "+packages.size() +" packages for releaseID: "+releaseID +" LIKE filter: "+filter);
			return packages;
		} catch (DatabaseServiceException e) {
			logger.error("filterPackagesForValue error: ",e);
			throw new Exception("Sorry, an error occurred on reconvering packages, try again later");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesService#incrementPackageAccounting(org.gcube.portlets.admin.gcubereleases.shared.Package, org.gcube.portlets.admin.gcubereleases.shared.AccoutingReference)
	 */
	@Override
	public void incrementPackageAccounting(Package pck, AccoutingReference reference){

		try {

			DaoGcubeBuilderReportDBManager<Release> daoManager = getDBManager(); //TO INSTANCE ASL

			if(daoManager==null)
				throw new Exception("DaoManger is null");

			if(reference==null)
				return;

			PackagePersistence persistence = daoManager.getReleasePersistenceEntity().getPackagesPersistence();
			AccountingPersistence accounting = persistence.getAccountingPersistence();
			accounting.incrementPackageAccounting(pck, reference);
		} catch (Exception e) {
			logger.error("incrementPackageAccounting error: ",e);
//			throw new Exception("Sorry, an error occurred on reconvering packages, try again later");
		}
	}

	/**
	 * Checks if is management mode.
	 *
	 * @return true, if is management mode
	 * @throws Exception the exception
	 */
	@Override
	public boolean isManagementMode() throws Exception{

		DaoGcubeBuilderReportDBManager<Release> daoManager = getDBManager(); //TO INSTANCE ASL

		logger.trace("Checking is Management mode..");
		try{

			ASLSession asl = ScopeUtil.getAslSession(this.getThreadLocalRequest().getSession());

			if(ScopeUtil.isWithinPortal()){

				String usernameASL = asl.getUsername();
				logger.info("Comparing Asl Username: "+usernameASL +" and TEST USER: "+ScopeUtil.TEST_USER);
				if(usernameASL.compareTo(ScopeUtil.TEST_USER)==0){
					logger.info("TEST USER IN SESSION, returnig Management mode FALSE");
					return false;
				}

				if(LiferayUserUtil.isReleaseManager(asl)){
					logger.info("Is ManagementMode returning TRUE");
					return true;
				}

				logger.info("Is ManagementMode returning FALSE");
				return false;
			}

			logger.info("Is Management mode returning TRUE");
			return true;
		}catch(Exception e){
			logger.warn("LiferayUserManager is not instantiable!!: "+e.getMessage());
			logger.info("is Management mode returning FALSE");
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesService#getAccountingReportForRelease(java.lang.String)
	 */
	@Override
	public AccountingReport getAccountingReportForRelease(String releaseInternalId) throws Exception{

		logger.trace("Get AccountingReportForRelease..");
		AccountingReport report = new AccountingReport();

		try{

			DaoGcubeBuilderReportDBManager<Release> daoManager = getDBManager(); //TO INSTANCE ASL

			ReleasePersistence releasePeristence = daoManager.getReleasePersistenceEntity();

			EntityManager em = null;

			if(releaseInternalId==null || releaseInternalId.isEmpty()){
				logger.warn("releaseInternalId is null, returning empty AccountingReport");
				return new AccountingReport();
			}
			Integer internalID = null;

			try{
				em = releasePeristence.createNewManager();

				try{
					internalID = Integer.parseInt(releaseInternalId);
				}catch (NumberFormatException e) {
					logger.warn("releaseInternalId is not Integer, returning empty AccountingReport");
					if(em!=null)
						em.close();
					return report;
				}

				Release daoRelease = em.find(Release.class, internalID);

				//RETRIEVE LIST PACKAGE IDS
				String queryString = "select p.internalId"
						+ " FROM "+Package.class.getSimpleName()+" p"
						+ " WHERE p."+Package.RELEASE +"= :release";

				Query query = em.createQuery(queryString);
				List<String> packageIds = query.setParameter("release", daoRelease).getResultList();

				em.close();
				em = releasePeristence.createNewManager();

				//RETRIEVE LIST ACCOUNTING IDS
				queryString = "select p."+Package.ACCOUNTING+".internalId"
						+ " FROM "+Package.class.getSimpleName()+" p"
						+ " WHERE p.internalId IN :packages";

				query = em.createQuery(queryString);
				List<Integer> listAccountingIds = query.setParameter("packages", packageIds).getResultList();

				em.close();
				em = releasePeristence.createNewManager();

				//RETRIEVE LIST ACCOUNTING IDS
				queryString = "select sum(p.downloadNmb) as totaldownloadNmb, sum(p.javadocNmb) as totaljavadocNmb,  sum(p.mavenRepoNmb) as totalmavenrepoNmb, sum(p.wikiNmb) as totalwikiNmb, sum(p.gitHubNmb) as totalgitHubNmb"
						+ " FROM "+AccountingPackage.class.getSimpleName()+" p"
						+ " WHERE p.internalId IN :accountings";

				query = em.createQuery(queryString);
				Object[] sums = (Object[]) query.setParameter("accountings", listAccountingIds).getSingleResult();

				em.close();
				em = releasePeristence.createNewManager();

				report.put(AccoutingReference.DOWNLOAD, (int) (long) sums[0]);
				report.put(AccoutingReference.JAVADOC, (int) (long) sums[1]);
				report.put(AccoutingReference.MAVEN_REPO, (int) (long) sums[2]);
				report.put(AccoutingReference.WIKI, (int) (long) sums[3]);
				if(sums[4]!=null)
					report.put(AccoutingReference.GITHUB, (int) (long) sums[4]);

			}catch (Exception e) {
				logger.error("getAccountingReportForRelease query error: ",e);
				throw new Exception(e);
			}finally{
				if(em!=null)
					em.close();
			}
		}catch(Exception e){
			logger.error("getAccountingReportForRelease error: ",e);
			throw new Exception("Sorry, an error occurred on reconvering accoutings, try again later");
		}

		return report;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	@Override
	public void destroy() {
		super.destroy();

		if(entityManagerFactory!=null)
			entityManagerFactory.close();
	}
}
