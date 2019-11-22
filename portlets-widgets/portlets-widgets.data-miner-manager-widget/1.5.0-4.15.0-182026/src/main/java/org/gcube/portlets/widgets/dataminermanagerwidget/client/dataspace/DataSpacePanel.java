/**
 * 
 */
package org.gcube.portlets.widgets.dataminermanagerwidget.client.dataspace;

import org.gcube.portlets.widgets.dataminermanagerwidget.client.DataMinerManagerPanel;

import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class DataSpacePanel extends SimpleContainer {

	private TabPanel dataSpaceTabPanel;
	private OutputDataSetsPanel outputDataSetsPanel;
	private InputDataSetsPanel inputDataSetsPanel;

	public DataSpacePanel() {
		super();
		init();
		create();
	}

	private void init() {
	}

	private void create() {
		inputDataSetsPanel = new InputDataSetsPanel();
		outputDataSetsPanel = new OutputDataSetsPanel();

		dataSpaceTabPanel = new TabPanel();
		dataSpaceTabPanel.setBorders(false);
		dataSpaceTabPanel.setBodyBorder(false);

		TabItemConfig outputDataItemConf = new TabItemConfig("Output Data Sets", false);
		outputDataItemConf.setIcon(DataMinerManagerPanel.resources.folderExplore());
		dataSpaceTabPanel.add(outputDataSetsPanel, outputDataItemConf);

		TabItemConfig inputDataSetsItemConf = new TabItemConfig("Input Data Sets", false);
		inputDataSetsItemConf.setIcon(DataMinerManagerPanel.resources.folderExplore());
		dataSpaceTabPanel.add(inputDataSetsPanel, inputDataSetsItemConf);

		dataSpaceTabPanel.setActiveWidget(outputDataSetsPanel);
		add(dataSpaceTabPanel);
		forceLayout();

	}

}
