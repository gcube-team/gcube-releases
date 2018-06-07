/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.dataspace;

import org.gcube.portlets.user.dataminermanager.client.DataMinerManager;

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
		outputDataSetsPanel = new OutputDataSetsPanel(); 
		inputDataSetsPanel = new InputDataSetsPanel();
		
		dataSpaceTabPanel=new TabPanel();
		dataSpaceTabPanel.setBorders(false);
		dataSpaceTabPanel.setBodyBorder(false);
		
		TabItemConfig outputDataItemConf = new TabItemConfig("Output Data Sets",
				false);
		outputDataItemConf.setIcon(DataMinerManager.resources
				.folderExplore());
	
		dataSpaceTabPanel.add(outputDataSetsPanel, outputDataItemConf);

		TabItemConfig inputDataSetsItemConf = new TabItemConfig(
				"Input Data Sets", false);
		inputDataSetsItemConf.setIcon(DataMinerManager.resources
				.folderExplore());
		dataSpaceTabPanel.add(inputDataSetsPanel, inputDataSetsItemConf);

		dataSpaceTabPanel.setActiveWidget(outputDataSetsPanel);
		
		add(dataSpaceTabPanel);
		
	}
	
	

}
