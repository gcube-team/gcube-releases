package org.gcube.portlets.admin.gcubereleases.client.rpc;

import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.gcubereleases.shared.AccountingReport;
import org.gcube.portlets.admin.gcubereleases.shared.AccoutingReference;
import org.gcube.portlets.admin.gcubereleases.shared.Package;
import org.gcube.portlets.admin.gcubereleases.shared.Release;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The Interface GcubeReleasesService.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
@RemoteServiceRelativePath("gcubeReleases")
public interface GcubeReleasesService extends RemoteService {

	/**
	 * Gets the releases.
	 *
	 * @param onlyOnline the only online
	 * @return the releases
	 * @throws Exception the exception
	 */
	List<Release> getReleases(boolean onlyOnline) throws Exception;

	/**
	 * Gets the packages for release id.
	 *
	 * @param release the release
	 * @return the packages for release id
	 * @throws Exception the exception
	 */
	List<Package> getPackagesForReleaseID(Release release) throws Exception;

	/**
	 * Gets the release by id.
	 *
	 * @param releaseID the release id
	 * @return the release by id
	 * @throws Exception the exception
	 */
	Release getReleaseByID(String releaseID) throws Exception;

	/**
	 * Gets the subsystems for release id.
	 *
	 * @param releaseID the release id
	 * @return the subsystems for release id
	 * @throws Exception the exception
	 */
	Map<String, Long> getSubsystemsForReleaseID(String releaseID) throws Exception;

	/**
	 * Load packages for subsystem.
	 *
	 * @param id the id
	 * @param subsystemID the subsystem id
	 * @return the list
	 * @throws Exception the exception
	 */
	List<Package> loadPackagesForSubsystem(String id, String subsystemID) throws Exception;

	/**
	 * Filter packages for value.
	 *
	 * @param releaseID the release id
	 * @param filter the filter
	 * @return the list
	 * @throws Exception the exception
	 */
	List<Package> filterPackagesForValue(String releaseID, String filter) throws Exception;

	/**
	 * Insert new release.
	 *
	 * @param release the release
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	boolean insertNewRelease(Release release) throws Exception;

	/**
	 * Delete package.
	 *
	 * @param pck the pck
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	boolean deletePackage(Package pck) throws Exception;

	/**
	 * Delete packages.
	 *
	 * @param listPcks the list pcks
	 * @return the list
	 * @throws Exception the exception
	 */
	List<Package> deletePackages(List<Package> listPcks) throws Exception;

	/**
	 * Update release info.
	 *
	 * @param release the release
	 * @return the release
	 * @throws Exception the exception
	 */
	Release updateReleaseInfo(Release release) throws Exception;

	/**
	 * Delete release.
	 *
	 * @param release the release
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	boolean deleteRelease(Release release) throws Exception;

	/**
	 * Checks if is management mode.
	 *
	 * @return true, if is management mode
	 * @throws Exception 
	 */
	boolean isManagementMode() throws Exception;

	/**
	 * @param pck
	 * @param reference
	 */
	void incrementPackageAccounting(Package pck, AccoutingReference reference);


	/**
	 * @param releaseInternalId
	 * @return
	 * @throws Exception
	 */
	AccountingReport getAccountingReportForRelease(String releaseInternalId)
			throws Exception;

}
