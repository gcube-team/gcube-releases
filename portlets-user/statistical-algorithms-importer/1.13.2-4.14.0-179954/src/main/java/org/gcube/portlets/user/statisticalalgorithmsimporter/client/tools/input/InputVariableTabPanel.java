package org.gcube.portlets.user.statisticalalgorithmsimporter.client.tools.input;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.InputReadyEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.InputOutputVariables;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.ProjectInfo;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.InputData;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;

//import org.gcube.portlets.user.td.taskswidget.client.TdTaskController;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class InputVariableTabPanel extends TabPanel {
	private InputVariableMessages msgs;
	private EventBus eventBus;
	private GlobalVariablesPanel globalVariablesPanel;
	private InputOutputVariablesPanel inputOutputVariablesPanel;
	private InterpreterInfoPanel interpreterInfoPanel;
	private ProjectInfoPanel projectInfoPanel;

	public InputVariableTabPanel(EventBus eventBus, InputVariablePanel toolBoxPanel) {
		super();
		Log.debug("Create InpuntVariableTabPanel");
		this.eventBus = eventBus;
		this.msgs = GWT.create(InputVariableMessages.class);
		init();

	}

	protected void init() {
		setId("InputVariableTabPanel");
		setBodyBorder(false);
		setBorders(false);
		setAnimScroll(true);
		setTabScroll(true);
		setCloseContextMenu(true);
	}

	public void startTabs(Project project) {
		Log.debug("Start InputVariable Tabs");
		addProjectInfoPanel(project);
		addInputOutputVariablesPanel(project);
		addInterpreterInfoPanel(project);
		addGlobalVariablesPanel(project);
		setActiveWidget(getWidget(0));

	}

	/*
	 * public void openEnviromentVariablesPanel() {
	 * Log.debug("Open EnviromentVariablesPanel Tab"); if
	 * (enviromentVariablesPanel == null) { addChangeColumnTypePanel(trId,
	 * columnName); } else { enviromentVariablesPanel.update(); }
	 * setActiveWidget(changeColumnTypePanel);
	 * 
	 * }
	 */

	// Close
	/*
	 * public void closePropertiesTabs() { Log.debug("Close Properties Tab"); if
	 * (trProperties != null) { remove(trProperties); trProperties = null; }
	 * 
	 * }
	 */

	//
	public void setMainCode(Project project) {
		try {
			projectInfoPanel.update(project);
			inputOutputVariablesPanel.update(project);
			interpreterInfoPanel.update(project);
			globalVariablesPanel.update(project);
			forceLayout();
		} catch (Throwable e) {
			Log.error("Error in InputVariableTabPanel: " + e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	public void setBinaryCode(Project project) {
		try {
			projectInfoPanel.update(project);
			inputOutputVariablesPanel.update(project);
			interpreterInfoPanel.update(project);
			globalVariablesPanel.update(project);
			forceLayout();
		} catch (Throwable e) {
			Log.error("Error in InputVariableTabPanel: " + e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	public void updateTabs(Project project) {
		try {
			projectInfoPanel.update(project);
			inputOutputVariablesPanel.update(project);
			interpreterInfoPanel.update(project);
			globalVariablesPanel.update(project);
			forceLayout();
		} catch (Throwable e) {
			Log.error("Error in InputVariableTabPanel: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	public void requestInput() {

		InputData inputData = new InputData();

		InputReadyEvent inputReadyEvent;

		try {
			if (projectInfoPanel != null) {
				ProjectInfo projectInfo = projectInfoPanel.getProjectInfo();
				inputData.setProjectInfo(projectInfo);
			}

			if (inputOutputVariablesPanel != null) {
				inputData.setListInputOutputVariables(inputOutputVariablesPanel.getInputOutputVariables());
			}

			if (interpreterInfoPanel != null) {
				inputData.setInterpreterInfo(interpreterInfoPanel.getInterpreterInfo());
			}

			if (globalVariablesPanel != null) {
				inputData.setListGlobalVariables(globalVariablesPanel.getGlobalVariables());
			}

			inputReadyEvent = new InputReadyEvent(inputData);

		} catch (Throwable e) {
			Log.error("Attention invalid info: " + e.getLocalizedMessage());
			inputReadyEvent = new InputReadyEvent(inputData, e.getLocalizedMessage());
		}

		Log.debug("Fired InputReadyEvent");
		eventBus.fireEvent(inputReadyEvent);

	}

	// Add
	private void addProjectInfoPanel(Project project) {
		TabItemConfig projectInfoPanelItemConf = new TabItemConfig(msgs.projectInfo(), false);

		projectInfoPanel = new ProjectInfoPanel(project, eventBus);
		projectInfoPanel.setHeaderVisible(false);
		add(projectInfoPanel, projectInfoPanelItemConf);

	}

	private void addInputOutputVariablesPanel(Project project) {
		TabItemConfig inputOutputPanelItemConf = new TabItemConfig(msgs.inputOutputVariables(), false);

		inputOutputVariablesPanel = new InputOutputVariablesPanel(project, eventBus);
		inputOutputVariablesPanel.setHeaderVisible(false);
		add(inputOutputVariablesPanel, inputOutputPanelItemConf);

	}

	private void addGlobalVariablesPanel(Project project) {
		TabItemConfig globalVariablePanelItemConf = new TabItemConfig(msgs.globalVariables(), false);

		globalVariablesPanel = new GlobalVariablesPanel(project, eventBus);
		globalVariablesPanel.setHeaderVisible(false);
		add(globalVariablesPanel, globalVariablePanelItemConf);
	}

	private void addInterpreterInfoPanel(Project project) {
		TabItemConfig interpreterInfoPanelItemConf = new TabItemConfig(msgs.interpreterInfo(), false);

		interpreterInfoPanel = new InterpreterInfoPanel(project, eventBus);
		interpreterInfoPanel.setHeaderVisible(false);
		add(interpreterInfoPanel, interpreterInfoPanelItemConf);

	}

	public void addSelectedRowsVariable(InputOutputVariables inputOutputVariable) {
		setActiveWidget(inputOutputVariablesPanel);
		inputOutputVariablesPanel.addNewInputOutputVariables(inputOutputVariable);

	}

}
