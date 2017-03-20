/**
 *
 */
package org.gcube.portlets.admin.gcubereleases.client;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.portlets.admin.gcubereleases.client.event.DisplaySelectedReleaseEvent;
import org.gcube.portlets.admin.gcubereleases.client.event.DisplaySelectedReleaseEventHandler;
import org.gcube.portlets.admin.gcubereleases.client.event.ManagePackagesEvent;
import org.gcube.portlets.admin.gcubereleases.client.event.ManagePackagesEventHandler;
import org.gcube.portlets.admin.gcubereleases.client.event.ManageReleasesEvent;
import org.gcube.portlets.admin.gcubereleases.client.event.ManageReleasesEventHandler;
import org.gcube.portlets.admin.gcubereleases.client.event.NewInsertReleaseEventHandler;
import org.gcube.portlets.admin.gcubereleases.client.event.NewInsertReleasesEvent;
import org.gcube.portlets.admin.gcubereleases.client.event.PackageClickEvent;
import org.gcube.portlets.admin.gcubereleases.client.event.PackageClickEventHandler;
import org.gcube.portlets.admin.gcubereleases.client.event.ReloadReleasesEvent;
import org.gcube.portlets.admin.gcubereleases.client.event.ReloadReleasesEventHandler;
import org.gcube.portlets.admin.gcubereleases.client.event.ShowClickReportEvent;
import org.gcube.portlets.admin.gcubereleases.client.event.ShowClickReportEventHandler;
import org.gcube.portlets.admin.gcubereleases.client.manage.ClickStatistcsManager;
import org.gcube.portlets.admin.gcubereleases.client.manage.NewReleaseManager;
import org.gcube.portlets.admin.gcubereleases.client.manage.PackagesManager;
import org.gcube.portlets.admin.gcubereleases.client.manage.ReleasesManager;
import org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesServiceAsync;
import org.gcube.portlets.admin.gcubereleases.client.view.DividerElement;
import org.gcube.portlets.admin.gcubereleases.shared.AccountingReport;
import org.gcube.portlets.admin.gcubereleases.shared.Release;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * The Class GcubeReleasesAppController.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 26, 2015
 */
public class GcubeReleasesAppController {

	/**
	 *
	 */
//	public static final String NO_SUBSYSTEMS_FOUND = "No Subsystems found";

	public static final String NO_SUBSYSTEMS_FOUND = "	Release has been archived and is not accessible anymore.";

	/** The Constant DIV_BUILDREPORTMANAGER. */
	private static final String DIV_BUILDREPORTMANAGER = "buildreportmanager";

	/** The Constant HEADER_ID. */
	public static final String HEADER_ID = "release-title";

	/** The Constant JAVADOC_RESOLVER_SERVLET. */
	public static final String JAVADOC_RESOLVER_SERVLET = com.google.gwt.core.client.GWT.getModuleBaseURL() + "javadocResolver";

	/** The header pg mng. */
	private HeaderPageMng headerPgMng = new HeaderPageMng(this);

	/** The root panel. */
	private GcubeReleasesRootPanel rootPanel = new GcubeReleasesRootPanel();

	/** The body page mng. */
	private BodyPageMng bodyPageMng = new BodyPageMng(this);

	/** The release displayed. */
	private Release releaseDisplayed;

	/** The all releases. */
	private List<Release> allReleases;

	/** The subsystem i ds. */
	private Set<String> subsystemIDs;

	/** The Constant eventBus. */
	public final static HandlerManager eventBus = new HandlerManager(null);

	/**
	 * Instantiates a new gcube releases app controller.
	 */
	public GcubeReleasesAppController() {
		rootPanel.add(headerPgMng.getHeader());
		rootPanel.add(new DividerElement());
		rootPanel.add(bodyPageMng.getBody());
		RootPanel.get(DIV_BUILDREPORTMANAGER).add(rootPanel);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				loadReleases(true);
			}
		});

		eventBus.addHandler(NewInsertReleasesEvent.TYPE, new NewInsertReleaseEventHandler() {

			@Override
			public void onNewInsertRelease(NewInsertReleasesEvent manageReleasesEvent) {
				NewReleaseManager mng = new NewReleaseManager();
				mng.showDialog();
			}
		});

		eventBus.addHandler(ManageReleasesEvent.TYPE, new ManageReleasesEventHandler() {

			@Override
			public void onManageReleases(ManageReleasesEvent manageReleasesEvent) {
				ReleasesManager releasesManager = new ReleasesManager(releaseDisplayed);
				releasesManager.showDialog();
			}
		});

		eventBus.addHandler(ManagePackagesEvent.TYPE, new ManagePackagesEventHandler() {

			@Override
			public void onManagePackages(ManagePackagesEvent managePackagesEvent) {
				final PackagesManager pck = new PackagesManager(releaseDisplayed, subsystemIDs);
				pck.showDialog();
			}
		});

		eventBus.addHandler(ReloadReleasesEvent.TYPE, new ReloadReleasesEventHandler() {

			@Override
			public void onReleadReleases(ReloadReleasesEvent reloadReleasesEvent) {
				GWT.log("Fired reloadReleasesEvent");
				loadReleases(reloadReleasesEvent.isDisplayFirst());

			}
		});

		eventBus.addHandler(DisplaySelectedReleaseEvent.TYPE, new DisplaySelectedReleaseEventHandler() {

			@Override
			public void onSelectRelease(DisplaySelectedReleaseEvent loadSelecteReleaseEvent) {
				if(loadSelecteReleaseEvent.getRelease()!=null){
					GWT.log("Fired DisplaySelectedReleaseEvent");
					setReleaseDisplayed(loadSelecteReleaseEvent.getRelease());
					displayRelease(loadSelecteReleaseEvent.getRelease());
				}
			}
		});

		eventBus.addHandler(ShowClickReportEvent.TYPE, new ShowClickReportEventHandler() {

			@Override
			public void onShowClickReport(ShowClickReportEvent showClickReportEvent) {
				GcubeReleasesServiceAsync.Util.getInstance().getAccountingReportForRelease(""+releaseDisplayed.getInternalId(), new AsyncCallback<AccountingReport>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(AccountingReport result) {
						ClickStatistcsManager stats = new ClickStatistcsManager(releaseDisplayed, subsystemIDs);
						stats.showDialog();
					}
				});

			}
		});


		eventBus.addHandler(PackageClickEvent.TYPE, new PackageClickEventHandler() {

			@Override
			public void onClickEvent(PackageClickEvent packageClickEvent) {

				GWT.log(packageClickEvent.toString());
				packageClickEvent.getPck().setAccouting(null);
				packageClickEvent.getPck().setRelease(null);
				GcubeReleasesServiceAsync.Util.getInstance().incrementPackageAccounting(packageClickEvent.getPck(), packageClickEvent.getAccoutingReference(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						//silent

					}

					@Override
					public void onSuccess(Void result) {
						//silent
					}
				});
			}
		});

//		rootPanel.addListener(Events.Scroll, new Listener<ComponentEvent>() {
//
//			@Override
//			public void handleEvent(ComponentEvent be) {
//
//				int scrollPosition = INSTANCE.getVScrollPosition() + ConstantsTdTasks.MAINHEIGHT;
//				int end = lcTasks.getHeight();
////				int scrollPositionOffset = lcTasks.getHeight()-60;
//				int difference = end-scrollPosition;
////				System.out.println("scrollPosition: "+ scrollPosition + " lc.heigth: "+lcTasks.getHeight() +" difference"+difference);
//
//				if(end-scrollPosition <=2 && lcSeeMore.isVisible()){
////					Info.display("Info","End is raggiunto");
//					seeMoreIsFired();
//				}
//			}
//
//		});
	}

	/**
	 * Load releases.
	 *
	 * @param displayFirst the display first
	 */
	private void loadReleases(final boolean displayFirst){

		GcubeReleasesServiceAsync.Util.getInstance().getReleases(true, new AsyncCallback<List<Release>>() {

			@Override
			public void onSuccess(List<Release> result) {
				if(result!=null && result.size()>0){
					allReleases = result;

					if(displayFirst){
						setReleaseDisplayed(result.get(0));
						displayRelease(result.get(0));
					}else
						updateOtherReleases();

				}else
					bodyPageMng.showError("Sorry, an error occurred when reading DB releases!! Try again later");
			}

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error on getReleases");
				Window.alert(caught.getMessage());
			}
		});
	}


	/**
	 * Update other releases.
	 */
	private void updateOtherReleases() {
		headerPgMng.setOtherReleases(allReleases, releaseDisplayed);
	}

	/**
	 * Sets the release displayed.
	 *
	 * @param release the new release displayed
	 */
	private void setReleaseDisplayed(Release release){
		releaseDisplayed = release;
	}

	/**
	 * Display release.
	 *
	 * @param release the release
	 */
	public void displayRelease(final Release release){

		headerReset();
		bodyReset();
		headerPgMng.showLoading(true);
		GcubeReleasesServiceAsync.Util.getInstance().getReleaseByID(release.getId(), new AsyncCallback<Release>() {

			@Override
			public void onFailure(Throwable caught) {
//				setReleaseDisplayed(null);
				headerPgMng.showLoading(false);
				GWT.log("Error on getReleaseByID", caught);
			}

			@Override
			public void onSuccess(Release result) {

				headerPgMng.showLoading(false);
				if(result!=null){
					setReleaseDisplayed(result);
					updateOtherReleases();
					headerPgMng.updateHeaderTitleAndInfo(result);
					loadSubsystemsForReleaseID(release.getId());
				}
			}
		});
	}

	/**
	 * Load subsystems for release id.
	 *
	 * @param releaseID the release id
	 */
	public void loadSubsystemsForReleaseID(String releaseID){

		bodyPageMng.getBody().setLoading(true, "Loading Subsystems..");

		GcubeReleasesServiceAsync.Util.getInstance().getSubsystemsForReleaseID(releaseID, new AsyncCallback<Map<String, Long>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error on getSubsystemsForReleaseID", caught);

			}

			@Override
			public void onSuccess(Map<String, Long> result) {

				if(result==null){
					Window.alert("An error occurred when loading subsystems, try again later!");
					return;
				}

				subsystemIDs = result.keySet();

				bodyPageMng.setLoading(false, null);
				if(result!=null && result.size()>0){
					headerPgMng.headerUpdateNavigation("Subsystems ["+result.size()+"]", (LinkedHashMap<String, Long>) result);
					bodyPageMng.updateBodyView(releaseDisplayed, (LinkedHashMap<String, Long>) result);
				}else{
					bodyPageMng.showInfo(NO_SUBSYSTEMS_FOUND);
				}
			}
		});
	}

	/**
	 * Header reset.
	 */
	public void headerReset() {
		headerPgMng.headerReset();
	}

	/**
	 * Body reset.
	 */
	public void bodyReset() {
		bodyPageMng.bodyReset();
	}

	/**
	 * Enable filter by subsystem.
	 *
	 * @param b the b
	 */
	public void enableFilterBySubsystem(boolean b) {
		headerPgMng.enableFilterBySubsystem(b);
		headerPgMng.showReleaseNotes(b);
	}


}
