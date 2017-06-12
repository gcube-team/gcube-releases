/**
 *
 */
package org.gcube.portlets.admin.gcubereleases.client;

import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.portlets.admin.gcubereleases.client.event.FilterPackageEvent;
import org.gcube.portlets.admin.gcubereleases.client.event.FilterPackageEventHandler;
import org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesServiceAsync;
import org.gcube.portlets.admin.gcubereleases.client.view.BodyPage;
import org.gcube.portlets.admin.gcubereleases.shared.Package;
import org.gcube.portlets.admin.gcubereleases.shared.Release;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Class BodyPageMng.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class BodyPageMng {

	/** The map subsystems. */
	LinkedHashMap<String, List<org.gcube.portlets.admin.gcubereleases.shared.Package>> mapSubsystems = new LinkedHashMap<String, List<org.gcube.portlets.admin.gcubereleases.shared.Package>>();

	// LinkedHashMap<String,
	// List<org.gcube.portlets.admin.buildreportmng.shared.Package>>
	// mapSubsyView = new LinkedHashMap<String,
	// List<org.gcube.portlets.admin.buildreportmng.shared.Package>>();

	/** The body page. */
	private BodyPage bodyPage = new BodyPage();

	/** The release. */
	private Release release;

	/** The root panel. */
	private GcubeReleasesAppController rootPanel;

	/**
	 * Instantiates a new body page mng.
	 *
	 * @param buildReportRootPanel the build report root panel
	 */
	public BodyPageMng(GcubeReleasesAppController buildReportRootPanel) {
		handleEvents();
		this.rootPanel = buildReportRootPanel;
	}

	/**
	 * Map subsystem reset.
	 */
	private void mapSubsystemReset() {
		mapSubsystems.clear();
	}

	/**
	 * Handle events.
	 */
	private void handleEvents() {

		GcubeReleasesAppController.eventBus.addHandler(FilterPackageEvent.TYPE,
				new FilterPackageEventHandler() {

					@Override
					public void onFilterPackage(
							FilterPackageEvent accountingHistoryEvent) {

						bodyPage.reset();
						mapSubsystemReset();

						final String filter = accountingHistoryEvent.getValue();
						if (filter != null && !filter.isEmpty()) {
							bodyPage.setLoading(true, "Loading Packages...");
							GcubeReleasesServiceAsync.Util.getInstance()
									.filterPackagesForValue(release.getId(),
											filter,
											new AsyncCallback<List<Package>>() {

												@Override
												public void onFailure(
														Throwable caught) {
													GWT.log("Error on loadPackagesForSubsystem",
															caught);
													bodyPage.setLoading(false,
															null);
												}

												@Override
												public void onSuccess(
														List<Package> results) {

													if (results != null
															&& results.size() > 0)
														bodyPage.addPackageView(results);
													else
														bodyPage.showError("No package available when searching for \""+ filter + "\"");

													bodyPage.setLoading(false,null);
													rootPanel.enableFilterBySubsystem(false);
												}
											});
						} else if (filter.isEmpty()) {
							// rootPanel.headerReset();
							// rootPanel.loadReleaseByID(release.getId());
							rootPanel.enableFilterBySubsystem(true);
							rootPanel.loadSubsystemsForReleaseID(release
									.getId());
						}
					}
				});

	}

	/**
	 * Adds the substystem.
	 *
	 * @param substystem the substystem
	 * @param packages the packages
	 */
	private void addSubstystem(String substystem,
			List<org.gcube.portlets.admin.gcubereleases.shared.Package> packages) {
		mapSubsystems.put(substystem, packages);
		bodyPage.addSubstystemView(substystem, packages);
	}

	/**
	 * Gets the body.
	 *
	 * @return the body
	 */
	public BodyPage getBody() {
		return bodyPage;
	}

	/**
	 * Load packages for subsystem.
	 *
	 * @param subsystemID the subsystem id
	 */
	private void loadPackagesForSubsystem(final String subsystemID) {

		GcubeReleasesServiceAsync.Util.getInstance().loadPackagesForSubsystem(
				release.getId(), subsystemID,
				new AsyncCallback<List<Package>>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Error on loadPackagesForSubsystem", caught);
						bodyPage.setLoading(false, null);
					}

					@Override
					public void onSuccess(List<Package> results) {
						GWT.log("Loaded " + results.size() + " to "
								+ subsystemID);

						if (results != null) {
							addSubstystem(subsystemID, results);
						}

						bodyPage.setLoading(false, null);
					}
				});
	}

	/**
	 * Sets the release.
	 *
	 * @param result the new release
	 */
	private void setRelease(Release result) {
		this.release = result;
	}

	/**
	 * Update body view.
	 *
	 * @param releaseDisplayed the release displayed
	 * @param result the result
	 */
	public void updateBodyView(Release releaseDisplayed,
			LinkedHashMap<String, Long> result) {
		setRelease(releaseDisplayed);
		mapSubsystemReset();

		if (result == null) {
			GWT.log("updateBodyView error: packages null");
			return;
		}

		bodyPage.setLoading(true, "Loading Packages...");

		for (String subSystemID : result.keySet()) {
			loadPackagesForSubsystem(subSystemID);
		}

	}

	/**
	 * Body reset.
	 */
	public void bodyReset() {
		bodyPage.reset();
	}

	/**
	 * Show error.
	 *
	 * @param txt the txt
	 */
	public void showError(String txt) {
		bodyPage.showError(txt);
	}

	/**
	 * Show info.
	 *
	 * @param txt the txt
	 */
	public void showInfo(String txt) {
		bodyPage.showMessage(txt);
	}

	/**
	 * Sets the loading.
	 *
	 * @param b the b
	 * @param label the label
	 */
	public void setLoading(boolean b, String label) {
		bodyPage.setLoading(b, label);

	}

}
