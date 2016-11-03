/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.manage;

import java.util.List;
import java.util.Set;

import org.gcube.portlets.admin.gcubereleases.client.dialog.DialogResult;
import org.gcube.portlets.admin.gcubereleases.client.manage.statistics.StatisticsTableManager;
import org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesServiceAsync;
import org.gcube.portlets.admin.gcubereleases.client.view.LoaderIcon;
import org.gcube.portlets.admin.gcubereleases.shared.AccountingReport;
import org.gcube.portlets.admin.gcubereleases.shared.AccoutingReference;
import org.gcube.portlets.admin.gcubereleases.shared.Package;
import org.gcube.portlets.admin.gcubereleases.shared.Release;

import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavList;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.LabelType;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * The Class PackagesManager.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class ClickStatistcsManager {

	private BaseViewTemplate template;
	
	private DialogResult dialog = new DialogResult(null, "Click Statistics");

	private FlowPanel centerPanel = new FlowPanel();

	private LoaderIcon loader = new LoaderIcon();

	private String subText = " ";

	private Release release;

	private Set<String> subsystemsIDs;
	
	private FlowPanel headerPanel = new FlowPanel();
	
	private Alert alertResult = new Alert();

	private ScrollPanel scrollPanel;
	
	private NavList navList = new NavList();
	
	private AccordionGroup accordionGroupNavigation = new AccordionGroup();

	/**
	 * Instantiates a new packages manager.
	 *
	 * @param releaseDisplayed the release displayed
	 * @param subsystemIDs the subsystem i ds
	 */
	public ClickStatistcsManager(final Release releaseDisplayed, Set<String> subsystemIDs) {
		
		this.release = releaseDisplayed;
		this.subsystemsIDs = subsystemIDs;

		template = new BaseViewTemplate();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				
				GcubeReleasesServiceAsync.Util.getInstance().getAccountingReportForRelease(""+releaseDisplayed.getInternalId(), new AsyncCallback<AccountingReport>() {

					@Override
					public void onFailure(Throwable caught) {

					}

					@Override
					public void onSuccess(AccountingReport result) {				
						String html = "<div>";
						html+= "<span style=\"margin-left:20px;\">#Download: "+result.get(AccoutingReference.DOWNLOAD) +"</span>";
						html+= "<span style=\"margin-left:20px;\">#Javodoc: "+result.get(AccoutingReference.JAVADOC) +"</span>";
						html+= "<span style=\"margin-left:20px;\">#Wiki: "+result.get(AccoutingReference.WIKI) +"</span>";
						html+= "<span style=\"margin-left:20px;\">#Maven Repo: "+result.get(AccoutingReference.MAVEN_REPO) +"</span>";
						html+= "<div/>";
						showResult(true, html);
					}
				});
				
				scrollPanel.getElement().getStyle().setMarginTop(5.0, Unit.PX);
				scrollPanel.getElement().getStyle().setMarginBottom(5.0, Unit.PX);
			}
		});

		Heading ph = new Heading(4);
		ph.setText("Click Statistics: "+release.getName());
		ph.setSubtext(subText);
		initAlertResult();
		showResult(true, "Loading...");
		
		accordionGroupNavigation.setHeading("Subsystems");
		accordionGroupNavigation.add(navList);
		accordionGroupNavigation.getElement().getStyle().setMarginBottom(10.0, Unit.PX);
		
		scrollPanel = new ScrollPanel();
		scrollPanel.setWidth("100%");
		scrollPanel.setHeight("500px");

		initCentralPanel();

		headerPanel.add(ph);
		headerPanel.add(alertResult);
		headerPanel.add(accordionGroupNavigation);

		template.addToTop(headerPanel);
		template.addToMiddle(scrollPanel);
		
		dialog.addToCenterPanel(template);
		dialog.setWidth("900px");

		retrieveStatisticsForSubsystems();
		
		dialog.center();
	}
	
	/**
	 * Retrieve packages for subsystems.
	 */
	private void retrieveStatisticsForSubsystems(){
		showLoader(true);
		for (String subsystemID : subsystemsIDs) {
			loadPackagesForSubsystem(subsystemID);
		}
	}
	
	/**
	 * Inits the central panel.
	 */
	private void initCentralPanel(){
		centerPanel.clear();
		scrollPanel.clear();
		
//		scrollPanel.add(centerPanel);
		
		loader.setText("Loading packages...");
		showLoader(false);
		
		centerPanel.add(loader);
		scrollPanel.add(centerPanel);
	}
	
	/**
	 * Show result.
	 *
	 * @param show the show
	 * @param html the html
	 */
	private void showResult(boolean show, String html){
		alertResult.setVisible(show);
		alertResult.setHTML(html);
	}
	
	/**
	 * Inits the alert result.
	 */
	private void initAlertResult(){
		alertResult.setClose(false);
		alertResult.setType(AlertType.INFO);
	}
	
	/**
	 * Adds the substystem.
	 *
	 * @param substystem the substystem
	 * @param packages the packages
	 */
	private void addSubstystem(String substystem , List<org.gcube.portlets.admin.gcubereleases.shared.Package> packages){
	
		FlowPanel panelSubsystem = new FlowPanel();
		Label labelSubsytem = new Label(substystem);
		labelSubsytem.addStyleName("label-gcube-secondary");
		labelSubsytem.setType(LabelType.INFO);
		labelSubsytem.getElement().setId(release.getId()+substystem);
		panelSubsystem.add(labelSubsytem);
		StatisticsTableManager tablesForSubsystem = new StatisticsTableManager(false);
		tablesForSubsystem.addPackages(packages);
		panelSubsystem.add(tablesForSubsystem.getCellTables());
		centerPanel.add(panelSubsystem);
	}

	
	/**
	 * Load packages for subsystem.
	 *
	 * @param subsystemID the subsystem id
	 */
	private void loadPackagesForSubsystem(final String subsystemID){

		GcubeReleasesServiceAsync.Util.getInstance().loadPackagesForSubsystem(release.getId(), subsystemID, new AsyncCallback<List<Package>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error on loadPackagesForSubsystem",caught);
				showLoader(false);
			}

			@Override
			public void onSuccess(List<Package> results) {
				GWT.log("Loaded "+results.size() +" to "+subsystemID);
				
				if(results!=null){
					addNavigation(release.getId()+subsystemID, subsystemID);
					addSubstystem(subsystemID, results);
				}
				
				showLoader(false);
			}
		});
	}
	
	/**
	 * Adds the navigation.
	 *
	 * @param href the href
	 * @param linkName the link name
	 */
	private void addNavigation(String href, String linkName) {
		final NavLink navLink = new NavLink(linkName);
		navLink.setHref("#" + href);
		// dropDownRelease.add(navLink);
		

		navLink.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// Window.alert("clicked "+navigation);
			}
		});

		navList.add(navLink);
	}
	
	/**
	 * Show dialog.
	 */
	public void showDialog() {
		dialog.show();
	}

	/**
	 * Show loader.
	 *
	 * @param bool the bool
	 */
	private void showLoader(boolean bool) {
		loader.setVisible(bool);
	}
}
