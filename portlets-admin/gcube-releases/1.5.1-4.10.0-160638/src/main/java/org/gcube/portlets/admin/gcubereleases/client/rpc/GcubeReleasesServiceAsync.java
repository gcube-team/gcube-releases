package org.gcube.portlets.admin.gcubereleases.client.rpc;

import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.gcubereleases.shared.AccountingReport;
import org.gcube.portlets.admin.gcubereleases.shared.AccoutingReference;
import org.gcube.portlets.admin.gcubereleases.shared.Package;
import org.gcube.portlets.admin.gcubereleases.shared.Release;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Interface GcubeReleasesServiceAsync.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public interface GcubeReleasesServiceAsync
{

	/**
	 * Gets the releases.
	 *
	 * @param onlyOnline the only online
	 * @param callback the callback
	 * @return the releases
	 */
	void getReleases(boolean onlyOnline, AsyncCallback<List<Release>> callback);

	/**
	 * Gets the packages for release id.
	 *
	 * @param release the release
	 * @param callback the callback
	 * @return the packages for release id
	 */
	void getPackagesForReleaseID(Release release, AsyncCallback<List<Package>> callback);

    /**
     * The Class Util.
     *
     * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
     * Feb 19, 2015
     */
    public static final class Util
    {
        private static GcubeReleasesServiceAsync instance;

        /**
         * Gets the single instance of Util.
         *
         * @return single instance of Util
         */
        public static final GcubeReleasesServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (GcubeReleasesServiceAsync) GWT.create( GcubeReleasesService.class );
            }
            return instance;
        }

        /**
         * Instantiates a new util.
         */
        private Util()
        {
            // Utility class should not be instantiated
        }
    }

	/**
	 * Gets the release by id.
	 *
	 * @param releaseID the release id
	 * @param callback the callback
	 * @return the release by id
	 */
	void getReleaseByID(String releaseID, AsyncCallback<Release> callback);

	/**
	 * Gets the subsystems for release id.
	 *
	 * @param releaseID the release id
	 * @param asyncCallback the async callback
	 * @return the subsystems for release id
	 */
	void getSubsystemsForReleaseID(String releaseID,
			AsyncCallback<Map<String, Long>> asyncCallback);

	/**
	 * Load packages for subsystem.
	 *
	 * @param id the id
	 * @param subsystemID the subsystem id
	 * @param asyncCallback the async callback
	 */
	void loadPackagesForSubsystem(String id, String subsystemID, AsyncCallback<List<Package>> asyncCallback);

	/**
	 * Filter packages for value.
	 *
	 * @param releaseID the release id
	 * @param filter the filter
	 * @param asyncCallback the async callback
	 */
	void filterPackagesForValue(String releaseID, String filter,
			AsyncCallback<List<Package>> asyncCallback);

	/**
	 * Insert new release.
	 *
	 * @param release the release
	 * @param callback the callback
	 */
	void insertNewRelease(Release release, AsyncCallback<Boolean> callback);

	/**
	 * Delete package.
	 *
	 * @param pck the pck
	 * @param callback the callback
	 */
	void deletePackage(Package pck, AsyncCallback<Boolean> callback);

	/**
	 * Delete packages.
	 *
	 * @param listPcks the list pcks
	 * @param callback the callback
	 */
	void deletePackages(List<Package> listPcks,
			AsyncCallback<List<Package>> callback);

	/**
	 * Update release info.
	 *
	 * @param release the release
	 * @param hardUpdate
	 * @param callback the callback
	 */
	void updateReleaseInfo(Release release, Boolean hardUpdate, AsyncCallback<Release> callback);

	/**
	 * Delete release.
	 *
	 * @param release the release
	 * @param callback the callback
	 */
	void deleteRelease(Release release, AsyncCallback<Boolean> callback);

	/**
	 * Checks if is management mode.
	 *
	 * @param callback the callback
	 */
	void isManagementMode(AsyncCallback<Boolean> callback);

	void incrementPackageAccounting(Package pck, AccoutingReference reference,
			AsyncCallback<Void> callback);

	void getAccountingReportForRelease(String releaseInternalId,
			AsyncCallback<AccountingReport> callback);
}
