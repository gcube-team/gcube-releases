/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.manage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.gcube.portlets.admin.gcubereleases.client.GcubeReleasesAppController;
import org.gcube.portlets.admin.gcubereleases.client.dialog.DialogConfirm;
import org.gcube.portlets.admin.gcubereleases.client.dialog.DialogResult;
import org.gcube.portlets.admin.gcubereleases.client.event.DisplaySelectedReleaseEvent;
import org.gcube.portlets.admin.gcubereleases.client.manage.packages.PackageTableMng;
import org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesServiceAsync;
import org.gcube.portlets.admin.gcubereleases.client.view.LoaderIcon;
import org.gcube.portlets.admin.gcubereleases.shared.Package;
import org.gcube.portlets.admin.gcubereleases.shared.Release;

import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavList;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.LabelType;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * The Class PackagesManager.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class PackagesManager implements HandlerPackageDeletable{

	private BaseViewTemplate template;
	
	private DialogResult dialog = new DialogResult(null, "Delete Packages");

	private FlowPanel centerPanel = new FlowPanel();

	private LoaderIcon loader = new LoaderIcon();

	private String subText = " ";
	
	private Button deleteButton = new Button();

	private Release release;

	private Set<String> subsystemsIDs;
	
	private HashMap<String, Package> deletePackage = new HashMap<String, Package>();
	
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
	public PackagesManager(Release releaseDisplayed, Set<String> subsystemIDs) {
		
		this.release = releaseDisplayed;
		this.subsystemsIDs = subsystemIDs;

		template = new BaseViewTemplate();
		
		deleteButton.setType(ButtonType.PRIMARY);
		deleteButton.setText("Delete Packages");
		deleteButton.getElement().getStyle().setMarginTop(5.0, Unit.PX);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				
			}
		});

		Heading ph = new Heading(4);
		ph.setText("Delete Packages: "+release.getName());
		ph.setSubtext(subText);
		initAlertResult();
		showResult(false, "");
		
		accordionGroupNavigation.setHeading("Subsystems");
		accordionGroupNavigation.add(navList);
		accordionGroupNavigation.getElement().getStyle().setMarginBottom(10.0, Unit.PX);
		
		scrollPanel = new ScrollPanel();
		scrollPanel.setWidth("100%");
		scrollPanel.setHeight("500px");
	
		initCentralPanel();

		headerPanel.add(ph);
		headerPanel.add(accordionGroupNavigation);
		headerPanel.add(alertResult);

		deleteButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {

				if(deletePackage.size()>0){
					String msg ="";
					for (Package pck : deletePackage.values()) {
						msg+= "<br/><i> * " +pck.getArtifactID()+"</i>";
					}
					

					final DialogConfirm confirm = new DialogConfirm(null, "Delete confirm?");
					
					confirm.addToCenterPanel(new HTML("Do you want delete?" +msg));
					
					confirm.getYesButton().addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							sendDeletePackages();
							confirm.hide();
						}
					});
					
//					confirm.show();
					confirm.center();
				}
				
//				Window.alert("Do you want cancel "+tables.getSelectedPackages().toString());
				
			}
		});
		
		template.addToTop(headerPanel);
		template.addToMiddle(scrollPanel);
		template.addToBottom(deleteButton);
		
		dialog.addToCenterPanel(template);
		dialog.setWidth("900px");
		
		retrievePackagesForSubsystems();
		
		dialog.center();
	}
	
	/**
	 * Retrieve packages for subsystems.
	 */
	private void retrievePackagesForSubsystems(){
		deletePackage.clear();
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
	 * Send delete packages.
	 */
	private void sendDeletePackages(){
		
		showResult(true, "Deleting packages...");
		
		GcubeReleasesServiceAsync.Util.getInstance().deletePackages(new ArrayList<Package>(deletePackage.values()),new AsyncCallback<List<Package>>() {

			@Override
			public void onFailure(Throwable caught) {
//				Window.alert(caught.getMessage());
				showResult(true, "Sorry an error occurred on deleting package/s! Try again later");
				GcubeReleasesAppController.eventBus.fireEvent(new DisplaySelectedReleaseEvent(release));
			}

			@Override
			public void onSuccess(List<Package> result) {
				
				String msg = "";
				int correct = deletePackage.size()-result.size();
				msg = correct+" ";
				msg+=correct>1?"packages":"package";
				msg+=" deleted correctly!";
				msg+="<br/>";
				
				if(result.size()>0){
					msg+="An error occurred on deleting: ";
					for (Package package1 : result) {
						msg+="<br/>"+package1.getArtifactID();
					}	
				}
				
				showResult(true, msg);
				initCentralPanel();
				navList.clear();
				retrievePackagesForSubsystems();
				
				GcubeReleasesAppController.eventBus.fireEvent(new DisplaySelectedReleaseEvent(release));
				
			}
		});
				
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
		PackageTableMng tablesForSubsystem = new PackageTableMng(false, (HandlerPackageDeletable) this);
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

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.client.manage.HandlerDeletableInterface#delete(org.gcube.portlets.admin.buildreportmng.shared.Package)
	 */
	@Override
	public void delete(Package pck) {
		deletePackage.put(pck.getInternalId()+"", pck);
		
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.client.manage.HandlerDeletableInterface#undelete(org.gcube.portlets.admin.buildreportmng.shared.Package)
	 */
	@Override
	public void undelete(Package pck) {
		deletePackage.remove(pck.getInternalId()+"");
	}
	
	
}
