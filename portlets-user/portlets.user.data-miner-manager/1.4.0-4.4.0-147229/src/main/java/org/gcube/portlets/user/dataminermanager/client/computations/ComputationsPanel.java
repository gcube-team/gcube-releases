/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.computations;

import java.util.ArrayList;

import org.gcube.portlets.user.dataminermanager.client.DataMinerManager;
import org.gcube.portlets.user.dataminermanager.client.common.EventBusProvider;
import org.gcube.portlets.user.dataminermanager.client.events.ComputationDataEvent;
import org.gcube.portlets.user.dataminermanager.client.events.DataMinerWorkAreaEvent;
import org.gcube.portlets.user.dataminermanager.client.events.RefreshDataMinerWorkAreaEvent;
import org.gcube.portlets.user.dataminermanager.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ComputationsPanel extends SimpleContainer {
	private ArrayList<ComputationsViewerPanel> computationsViewers;

	private ComputationsExecutedPanel computationsExecutedPanel;
	private TabPanel computationsTabPanel;

	public ComputationsPanel() {
		super();
		Log.debug("ComputationsPanel");
		init();
		create();
		bind();
	}

	private void init() {

	}

	private void bind() {
		EventBusProvider.INSTANCE.addHandler(ComputationDataEvent.getType(),
				new ComputationDataEvent.ComputationDataEventHandler() {

					@Override
					public void onComputationData(ComputationDataEvent event) {
						Log.debug("Catch ComputationDataEvent: " + event);
						addComputationsViewerPanel(event);

					}

				});

		EventBusProvider.INSTANCE.addHandler(DataMinerWorkAreaEvent.TYPE,
				new DataMinerWorkAreaEvent.DataMinerWorkAreaEventHandler() {

					@Override
					public void onChange(DataMinerWorkAreaEvent event) {
						manageDataMinerWorkAreaEvents(event);

					}

				});

		EventBusProvider.INSTANCE
				.addHandler(
						RefreshDataMinerWorkAreaEvent.TYPE,
						new RefreshDataMinerWorkAreaEvent.RefreshDataMinerWorkAreaEventHandler() {

							@Override
							public void onRefresh(
									RefreshDataMinerWorkAreaEvent event) {
								Log.debug("Catch RefreshDataMinerWorkAreaEvent: "
										+ event);
								manageRefreshDataMinerWorkAreaEvents(event);
							}

						});

	}

	private void manageDataMinerWorkAreaEvents(DataMinerWorkAreaEvent event) {
		Log.debug("ComputationsPanel recieved DataMinerWorkAreaEvent: " + event);
		if (event == null) {
			Log.error("DataMinerWorkAreaEvent null");
			return;
		}
		switch (event.getDataMinerWorkAreaRegionType()) {
		case Computations:
			closeAllComputationsViewer();
			break;
		case DataSets:
			break;
		default:
			break;

		}

	}

	private void manageRefreshDataMinerWorkAreaEvents(
			RefreshDataMinerWorkAreaEvent event) {
		Log.debug("ComputationsPanel recieved RefreshDataMinerWorkAreaEvent: "
				+ event);
		if (event == null) {
			Log.error("RefreshDataMinerWorkAreaEvent null");
			return;
		}
		switch (event.getDataMinerWorkAreaElementType()) {
		case Computations:
			closeAllComputationsViewer();
			break;
		case InputDataSets:
			break;
		case OutputDataSets:
			break;
		default:
			break;
		}

	}

	private void create() {
		computationsViewers = new ArrayList<>();
		computationsExecutedPanel = new ComputationsExecutedPanel();

		computationsTabPanel = new TabPanel();
		computationsTabPanel.setTabScroll(true);
		computationsTabPanel.setBorders(false);
		computationsTabPanel.setBodyBorder(false);

		TabItemConfig computationsExecutedItemConf = new TabItemConfig(
				"List of Computations", false);

		computationsExecutedItemConf.setIcon(DataMinerManager.resources
				.folderExplore());

		computationsTabPanel.add(computationsExecutedPanel,
				computationsExecutedItemConf);

		computationsTabPanel.setActiveWidget(computationsExecutedPanel);

		add(computationsTabPanel, new MarginData(0));

	}

	private void addComputationsViewerPanel(ComputationDataEvent event) {
		if (event == null || event.getComputationData() == null) {
			Log.error("Invalid ComputationDataEvent: " + event);
			UtilsGXT3.alert("Error", "Invalid ComputationDataEvent: " + event);
			return;
		}

		if (event.getComputationData().getComputationId() == null) {
			Log.error("Invalid ComputationId: "
					+ event.getComputationData().getComputationId());
			UtilsGXT3.alert("Error", "Invalid ComputationId: "
					+ event.getComputationData().getComputationId());

			return;
		}

		if (event.getComputationData().getComputationId().getId() == null
				|| event.getComputationData().getComputationId().getId()
						.isEmpty()) {
			Log.error("Invalid Computation id: "
					+ event.getComputationData().getComputationId().getId());
			UtilsGXT3.alert("Error", "Invalid Computation id: "
					+ event.getComputationData().getComputationId().getId());
			return;
		}

		
		
		ComputationsViewerPanel computationsViewerPanel = new ComputationsViewerPanel(
				event.getComputationData());
		computationsViewers.add(computationsViewerPanel);
		TabItemConfig computationsViewerItemConf = new TabItemConfig(event
				.getComputationData().getComputationId().getId(), true);
		computationsViewerItemConf.setIcon(DataMinerManager.resources
				.folderExplore());
		computationsTabPanel.add(computationsViewerPanel,
				computationsViewerItemConf);

		computationsTabPanel.setActiveWidget(computationsViewerPanel);

		computationsTabPanel.forceLayout();
		forceLayout();
	}

	private void closeAllComputationsViewer() {
		for (ComputationsViewerPanel view : computationsViewers) {
			if (computationsTabPanel.getWidgetIndex(view) != -1) {
				computationsTabPanel.remove(view);
			}
		}
		computationsViewers.clear();
		computationsTabPanel.forceLayout();
		forceLayout();
	}

}
