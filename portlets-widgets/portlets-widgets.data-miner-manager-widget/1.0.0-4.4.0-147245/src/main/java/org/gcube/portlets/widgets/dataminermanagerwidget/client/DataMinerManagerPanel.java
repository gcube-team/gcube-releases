package org.gcube.portlets.widgets.dataminermanagerwidget.client;

import org.gcube.portlets.widgets.dataminermanagerwidget.client.common.EventBusProvider;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.computations.ComputationsPanel;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.dataspace.DataSpacePanel;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.DataMinerWorkAreaRequestEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.MenuSwitchEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.experiments.ExperimentPanel;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.resources.Resources;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.type.DataMinerWorkAreaRegionType;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.type.DataMinerWorkAreaRequestEventType;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.type.MenuType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;


/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class DataMinerManagerPanel extends ContentPanel {

	public static final Resources resources = GWT.create(Resources.class);
	

	private Header header;
	private HomePanel homePanel;
	private DataSpacePanel dataSpacePanel;
	private ExperimentPanel experimentPanel;
	private ComputationsPanel computationsPanel;

	private SimpleContainer previousPanel;
	private SimpleContainer centerPanel;

	public DataMinerManagerPanel() {
		Log.debug("DataMiner Manager Panel");
		resources.dataMinerCSS().ensureInjected();
		initPanel();
		create();

	}

	private void initPanel() {
		setHeaderVisible(false);
		setBodyBorder(false);
		
	}

	private void create() {
		homePanel = new HomePanel();
		dataSpacePanel = new DataSpacePanel();
		experimentPanel = new ExperimentPanel();
		computationsPanel = new ComputationsPanel();

		bind();

		// Layout
		BorderLayoutContainer mainPanelLayout = new BorderLayoutContainer();
		mainPanelLayout.setId("mainPanelLayout");
		mainPanelLayout.setBorders(false);
		mainPanelLayout.setResize(true);
		mainPanelLayout.getElement().getStyle().setBackgroundColor("#FFFFFF");

		// Center
		centerPanel = new SimpleContainer();
		MarginData mainData = new MarginData(new Margins(0));
		mainPanelLayout.setCenterWidget(centerPanel, mainData);

		// Menu
		header = new Header();

		BorderLayoutData menuData = new BorderLayoutData(40);
		menuData.setMargins(new Margins(5));
		menuData.setCollapsible(false);
		menuData.setSplit(false);

		mainPanelLayout.setNorthWidget(header, menuData);

		//
		header.setMenu(MenuType.EXPERIMENT);
		centerPanel.add(experimentPanel);
		previousPanel = experimentPanel;
		
		/*
		if (dataMinerManagerController.getOperatorId() != null
				&& !dataMinerManagerController.getOperatorId().isEmpty()) {
			header.setMenu(MenuType.EXPERIMENT);
			centerPanel.add(experimentPanel);
			previousPanel = experimentPanel;

		} else {
			centerPanel.add(homePanel);
			previousPanel = homePanel;
		
		}*/

		setWidget(mainPanelLayout);
		mainPanelLayout.forceLayout();

	}

	
	
	private void bind() {
		EventBusProvider.INSTANCE.addHandler(MenuSwitchEvent.TYPE,
				new MenuSwitchEvent.MenuSwitchEventHandler() {

					@Override
					public void onSelect(MenuSwitchEvent event) {
						Log.debug("Catch MenuSwitchEvent");
						menuSwitch(event);

					}
				});
	}

	/**
	 * 
	 * @param event
	 */
	private void menuSwitch(MenuSwitchEvent event) {
		if (event == null || event.getMenuType() == null) {
			return;
		}

		switch (event.getMenuType()) {
		case COMPUTATIONS:
			switchPanel(computationsPanel);
			fireDataMinerWorkAreareRequestUpdate(DataMinerWorkAreaRegionType.Computations);
			break;
		case DATA_SPACE:
			switchPanel(dataSpacePanel);
			fireDataMinerWorkAreareRequestUpdate(DataMinerWorkAreaRegionType.DataSets);
			break;
		case EXPERIMENT:
			switchPanel(experimentPanel);
			break;
		case HOME:
			switchPanel(homePanel);
			break;
		default:
			break;

		}

	}

	private void fireDataMinerWorkAreareRequestUpdate(
			DataMinerWorkAreaRegionType dataMinerWorkAreaRegionType) {
		DataMinerWorkAreaRequestEvent event = new DataMinerWorkAreaRequestEvent(
				DataMinerWorkAreaRequestEventType.UPDATE,
				dataMinerWorkAreaRegionType);
		EventBusProvider.INSTANCE.fireEvent(event);
	}

	/**
	 * 
	 * @param panel
	 */
	private void switchPanel(SimpleContainer panel) {
		centerPanel.remove(previousPanel);
		centerPanel.add(panel);
		centerPanel.forceLayout();
		previousPanel = panel;
	}

}
