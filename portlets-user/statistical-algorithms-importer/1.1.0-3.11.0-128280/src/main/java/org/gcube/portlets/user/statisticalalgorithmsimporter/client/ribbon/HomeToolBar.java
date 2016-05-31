/**
 * 
 */
package org.gcube.portlets.user.statisticalalgorithmsimporter.client.ribbon;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.ProjectStatusEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.StatAlgoImporterRibbonEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.resource.StatAlgoImporterResources;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.type.ProjectStatusEventType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.type.StatAlgoImporterRibbonType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.ButtonArrowAlign;
import com.sencha.gxt.cell.core.client.ButtonCell.ButtonScale;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.ButtonGroup;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class HomeToolBar {
	private static final String GROUP_HEIGHT = "64px";
	private HomeToolBarMessages msgs;
	private EventBus eventBus;
	private ToolBar toolBar;
	
	//Home
	private TextButton btnCreateProject;
	private TextButton btnOpenProject;
	private TextButton btnSaveProject;
	
	//Resources
	private TextButton btnAddResource;
	
	
	//Software
	private TextButton btnCreateSoftware;
	private TextButton btnPublishSoftware;
	private TextButton btnRepackageSoftware;
	
	// Help
	private TextButton btnHelp;
	
	
	// private TextButton languageButton;
	// private TextButton logsButton;
	// private TextButton testButton;

	// Language Menu
	/*
	 * private MenuItem enItem; private MenuItem itItem; private MenuItem
	 * esItem;
	 */

	public HomeToolBar(EventBus eventBus) {
		this.eventBus = eventBus;
		msgs = GWT.create(HomeToolBarMessages.class);
		build();
	}

	public ToolBar getToolBar() {
		return toolBar;
	}

	protected void build() {
		toolBar = new ToolBar();
		toolBar.setSpacing(5);
		toolBar.setEnableOverflow(false);

		// Project
		ButtonGroup projectGroup = new ButtonGroup();
		projectGroup.setId("Project");
		projectGroup.setHeadingText(msgs.projectGroupHeadingText());
		projectGroup.setHeight(GROUP_HEIGHT);
		toolBar.add(projectGroup);

		FlexTable homeLayout = new FlexTable();
		projectGroup.add(homeLayout);
		
		btnCreateProject = new TextButton(msgs.btnCreateProject(),
				StatAlgoImporterResources.INSTANCE.projectCreate24());
		btnCreateProject.setId("btnCreateProject");
		btnCreateProject.setScale(ButtonScale.SMALL);
		btnCreateProject.setIconAlign(IconAlign.LEFT);
		btnCreateProject.setArrowAlign(ButtonArrowAlign.BOTTOM);
		btnCreateProject.setToolTip(msgs.btnCreateProjectToolTip());
		btnCreateProject.getElement().setMargins(new Margins(0, 4, 0, 0));
		btnCreateProject.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new StatAlgoImporterRibbonEvent(
						StatAlgoImporterRibbonType.PROJECT_CREATE));
			}
		});
	
		homeLayout.setWidget(0, 0, btnCreateProject);
		homeLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);
		
		btnOpenProject = new TextButton(msgs.btnOpenProject(),
				StatAlgoImporterResources.INSTANCE.projectOpen24());
		btnOpenProject.setId("btnOpenProject");
		btnOpenProject.setScale(ButtonScale.SMALL);
		btnOpenProject.setIconAlign(IconAlign.LEFT);
		btnOpenProject.setArrowAlign(ButtonArrowAlign.BOTTOM);
		btnOpenProject.setToolTip(msgs.btnOpenProjectToolTip());
		btnOpenProject.getElement().setMargins(new Margins(0, 4, 0, 0));
		btnOpenProject.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new StatAlgoImporterRibbonEvent(
						StatAlgoImporterRibbonType.PROJECT_OPEN));
			}
		});

		homeLayout.setWidget(0, 1, btnOpenProject);
		homeLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);
		
		
		btnSaveProject = new TextButton(msgs.btnSaveProject(),
				StatAlgoImporterResources.INSTANCE.projectSave24());
		btnSaveProject.setId("btnSaveProject");
		btnSaveProject.setScale(ButtonScale.SMALL);
		btnSaveProject.setIconAlign(IconAlign.LEFT);
		btnSaveProject.setArrowAlign(ButtonArrowAlign.BOTTOM);
		btnSaveProject.setToolTip(msgs.btnSaveProjectToolTip());
		btnSaveProject.disable();
		btnSaveProject.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new StatAlgoImporterRibbonEvent(
						StatAlgoImporterRibbonType.PROJECT_SAVE));
			}
		});

		homeLayout.setWidget(0, 2, btnSaveProject);
		homeLayout.getFlexCellFormatter().setRowSpan(0, 2, 2);
		
		
		// Resources
		ButtonGroup resourcesGroup = new ButtonGroup();
		resourcesGroup.setId("Resource");
		resourcesGroup.setHeadingText(msgs.resourceGroupHeadingText());
		resourcesGroup.setHeight(GROUP_HEIGHT);
		toolBar.add(resourcesGroup);

		FlexTable resourcesLayout = new FlexTable();	
		resourcesGroup.add(resourcesLayout);

		btnAddResource = new TextButton(msgs.btnAddResourceText(),
				StatAlgoImporterResources.INSTANCE.upload24());
		btnAddResource.setId("btnAddResource");
		btnAddResource.setScale(ButtonScale.SMALL);
		btnAddResource.setIconAlign(IconAlign.LEFT);
		btnAddResource.setArrowAlign(ButtonArrowAlign.BOTTOM);
		btnAddResource.setToolTip(msgs.btnAddResourceToolTip());
		btnAddResource.disable();
		btnAddResource.addSelectHandler(new SelectHandler() {
			
			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new StatAlgoImporterRibbonEvent(
						StatAlgoImporterRibbonType.RESOURCE_ADD));
			}
		});

		resourcesLayout.setWidget(0, 0, btnAddResource);
		resourcesLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);
		
		// Software
		ButtonGroup softwareGroup = new ButtonGroup();
		softwareGroup.setId("SoftwareGroup");
		softwareGroup.setHeadingText(msgs.softwareGroupHeadingText());
		softwareGroup.setHeight(GROUP_HEIGHT);
		toolBar.add(softwareGroup);

		FlexTable softwareLayout = new FlexTable();	
		softwareGroup.add(softwareLayout);
		
		btnCreateSoftware = new TextButton(msgs.btnCreateSoftwareText(),
				StatAlgoImporterResources.INSTANCE.algorithm24());
		btnCreateSoftware.setId("createSoftwareButton");
		btnCreateSoftware.setScale(ButtonScale.SMALL);
		btnCreateSoftware.setIconAlign(IconAlign.LEFT);
		btnCreateSoftware.setArrowAlign(ButtonArrowAlign.BOTTOM);
		btnCreateSoftware.setToolTip(msgs.btnCreateSoftwareToolTip());
		btnCreateSoftware.getElement().setMargins(new Margins(0, 4, 0, 0));
		btnCreateSoftware.disable();
		btnCreateSoftware.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new StatAlgoImporterRibbonEvent(
						StatAlgoImporterRibbonType.SOFTWARE_CREATE));
			}
		});

		softwareLayout.setWidget(0, 0, btnCreateSoftware);
		softwareLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);
		
		btnPublishSoftware = new TextButton(msgs.btnPublishSoftwareText(),
				StatAlgoImporterResources.INSTANCE.publish24());
		btnPublishSoftware.setId("publishSofwareButton");
		btnPublishSoftware.setScale(ButtonScale.SMALL);
		btnPublishSoftware.setIconAlign(IconAlign.LEFT);
		btnPublishSoftware.setArrowAlign(ButtonArrowAlign.BOTTOM);
		btnPublishSoftware.setToolTip(msgs.btnPublishSoftwareToolTip());
		btnPublishSoftware.getElement().setMargins(new Margins(0, 4, 0, 0));
		btnPublishSoftware.disable();
		btnPublishSoftware.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new StatAlgoImporterRibbonEvent(
						StatAlgoImporterRibbonType.SOFTWARE_PUBLISH));
			}
		});

		softwareLayout.setWidget(0, 1, btnPublishSoftware);
		softwareLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);
		
		
		btnRepackageSoftware = new TextButton(msgs.btnRepackageSoftwareText(),
				StatAlgoImporterResources.INSTANCE.zip24());
		btnRepackageSoftware.setId("publishSofwareButton");
		btnRepackageSoftware.setScale(ButtonScale.SMALL);
		btnRepackageSoftware.setIconAlign(IconAlign.LEFT);
		btnRepackageSoftware.setArrowAlign(ButtonArrowAlign.BOTTOM);
		btnRepackageSoftware.setToolTip(msgs.btnRepackageSoftwareToolTip());
		btnRepackageSoftware.disable();
		btnRepackageSoftware.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new StatAlgoImporterRibbonEvent(
						StatAlgoImporterRibbonType.SOFTWARE_REPACKAGE));
			}
		});

		softwareLayout.setWidget(0, 2, btnRepackageSoftware);
		softwareLayout.getFlexCellFormatter().setRowSpan(0, 2, 2);
		
		

		// Help
		ButtonGroup helpGroup = new ButtonGroup();
		helpGroup.setId("Help");
		helpGroup.setHeight(GROUP_HEIGHT);
		helpGroup.setHeadingText(msgs.helpGroupHeadingText());
		toolBar.add(helpGroup);

		FlexTable helpLayout = new FlexTable();
		helpGroup.add(helpLayout);

		/*
		 * languageButton = new TextButton(msgs.languageButton(),
		 * TabularDataResources.INSTANCE.language32()); languageButton.enable();
		 * languageButton.setScale(ButtonScale.LARGE);
		 * languageButton.setIconAlign(IconAlign.TOP);
		 * languageButton.setToolTip(msgs.languageButtonToolTip());
		 * languageButton.setArrowAlign(ButtonArrowAlign.RIGHT);
		 * languageButton.setMenu(createLanguageMenu());
		 * 
		 * helpLayout.setWidget(0, 0, languageButton);
		 * helpLayout.getFlexCellFormatter().setRowSpan(0, 0, 2);
		 */

		btnHelp = new TextButton(msgs.helpButton(),
				StatAlgoImporterResources.INSTANCE.help24());
		btnHelp.enable();
		btnHelp.setToolTip(msgs.helpButtonToolTip());
		btnHelp.setScale(ButtonScale.SMALL);
		btnHelp.setIconAlign(IconAlign.LEFT);
		btnHelp.setArrowAlign(ButtonArrowAlign.BOTTOM);
		btnHelp.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				eventBus.fireEvent(new StatAlgoImporterRibbonEvent(
						StatAlgoImporterRibbonType.HELP));
			}
		});

		helpLayout.setWidget(0, 1, btnHelp);
		helpLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);

		/*
		 * logsButton = new TextButton("Logs",
		 * TabularDataResources.INSTANCE.logs32()); logsButton.enable();
		 * logsButton.setToolTip("Show Logs");
		 * logsButton.setScale(ButtonScale.LARGE);
		 * logsButton.setIconAlign(IconAlign.TOP);
		 * logsButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		 * logsButton.addSelectHandler(new SelectHandler() {
		 * 
		 * public void onSelect(SelectEvent event) { eventBus.fireEvent(new
		 * RibbonEvent(RibbonType.LOGS)); } });
		 * 
		 * helpLayout.setWidget(0, 1, logsButton);
		 * helpLayout.getFlexCellFormatter().setRowSpan(0, 1, 2);
		 */

		/*
		 * testButton = new TextButton("Test",
		 * TabularDataResources.INSTANCE.test32()); testButton.disable();
		 * testButton.setToolTip("Test");
		 * testButton.setScale(ButtonScale.LARGE);
		 * testButton.setIconAlign(IconAlign.TOP);
		 * testButton.setArrowAlign(ButtonArrowAlign.BOTTOM);
		 * testButton.addSelectHandler(new SelectHandler() {
		 * 
		 * public void onSelect(SelectEvent event) { eventBus.fireEvent(new
		 * RibbonEvent(RibbonType.TEST)); } });
		 * 
		 * helpLayout.setWidget(0, 2, testButton);
		 * helpLayout.getFlexCellFormatter().setRowSpan(0, 2, 2);
		 */

		cleanCells(helpLayout.getElement());

		eventBus.addHandler(ProjectStatusEvent.TYPE,
				new ProjectStatusEvent.ProjectStatusEventHandler() {
					
					@Override
					public void onProjectStatus(ProjectStatusEvent event) {
						setUI(event);
						
					}
				});

	}

	private void cleanCells(Element elem) {
		NodeList<Element> tds = elem.<XElement> cast().select("td");
		for (int i = 0; i < tds.getLength(); i++) {
			Element td = tds.getItem(i);

			if (!td.hasChildNodes() && td.getClassName().equals("")) {
				td.removeFromParent();
			}
		}
	}

	/*
	 * private Menu createLanguageMenu() { Menu menuReplace = new Menu(); enItem
	 * = new MenuItem(msgs.english(), TabularDataResources.INSTANCE.flagGB());
	 * itItem = new MenuItem(msgs.italian(),
	 * TabularDataResources.INSTANCE.flagIT()); esItem = new
	 * MenuItem(msgs.spanish(), TabularDataResources.INSTANCE.flagES());
	 * 
	 * enItem .addSelectionHandler(new SelectionHandler<Item>() {
	 * 
	 * @Override public void onSelection(SelectionEvent<Item> event) {
	 * eventBus.fireEvent(new RibbonEvent( RibbonType.LANGUAGE_EN));
	 * 
	 * } });
	 * 
	 * itItem .addSelectionHandler(new SelectionHandler<Item>() {
	 * 
	 * @Override public void onSelection(SelectionEvent<Item> event) {
	 * eventBus.fireEvent(new RibbonEvent( RibbonType.LANGUAGE_IT));
	 * 
	 * } });
	 * 
	 * esItem .addSelectionHandler(new SelectionHandler<Item>() {
	 * 
	 * @Override public void onSelection(SelectionEvent<Item> event) {
	 * eventBus.fireEvent(new RibbonEvent( RibbonType.LANGUAGE_ES));
	 * 
	 * } });
	 * 
	 * menuReplace.add(enItem); menuReplace.add(esItem);
	 * menuReplace.add(itItem); return menuReplace; }
	 */

	public void setUI(ProjectStatusEvent event) {
		ProjectStatusEventType projectStatusEventType = event.getProjectStatusEventType();
		if(projectStatusEventType==null){
			return;
		}
		
		try {
			switch (projectStatusEventType) {
			case START:
				btnCreateProject.enable();
				btnOpenProject.enable();
				btnSaveProject.disable();
				btnAddResource.disable();
				btnCreateSoftware.disable();
				btnPublishSoftware.disable();
				btnRepackageSoftware.disable();
				btnHelp.enable();
				break;
			case OPEN:
			case UPDATE:
			case ADD_RESOURCE:
			case DELETE_RESOURCE:
			case SAVE:
			case MAIN_CODE_SET:	
				btnCreateProject.enable();
				btnOpenProject.enable();
				btnSaveProject.enable();
				btnAddResource.enable();
				btnCreateSoftware.enable();
				btnPublishSoftware.enable();
				btnRepackageSoftware.enable();
				btnHelp.enable();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			Log.error("setUI Error : " + e.getLocalizedMessage());
		}
	}
}
