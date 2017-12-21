package org.gcube.portlets.user.statisticalalgorithmsimporter.client.tools.input;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.InputRequestEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.NewSelectedRowsVariableEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.ProjectStatusEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.InputOutputVariables;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class InputVariablePanel extends ContentPanel {

	private EventBus eventBus;
	private InputVariableTabPanel inputVariableTabPanel;

	// private InputVariablePanelState state;

	public InputVariablePanel(EventBus eventBus) {
		super();
		Log.debug("InputVariablePanel");
		this.eventBus = eventBus;
		init();
		bindToEvents();

	}

	public InputVariablePanel(EventBus eventBus,
			AccordionLayoutAppearance appearance) {
		super(appearance);
		Log.debug("Open InputVariablePanel");
		this.eventBus = eventBus;
		init();
		bindToEvents();

	}

	private void init() {
		setId("InputVariablePanel");
		setHeaderVisible(true);
		setAnimCollapse(false);
		setResize(true);
		setBodyBorder(true);
		setBorders(true);
		setHeadingText("Input");
		forceLayoutOnResize = true;

	}

	private void bindToEvents() {

		eventBus.addHandler(ProjectStatusEvent.TYPE,
				new ProjectStatusEvent.ProjectStatusEventHandler() {

					@Override
					public void onProjectStatus(ProjectStatusEvent event) {
						Log.debug("Catch ProjectStatusEvent");
						manageProjectStatusEvents(event);

					}
				});

		eventBus.addHandler(InputRequestEvent.TYPE,
				new InputRequestEvent.InputRequestEventHandler() {

					@Override
					public void onInputRequest(InputRequestEvent event) {
						Log.debug("Catch InputRequestEvent");
						manageInputRequestEvents(event);

					}
				});

		eventBus.addHandler(
				NewSelectedRowsVariableEvent.TYPE,
				new NewSelectedRowsVariableEvent.NewSelectedRowsVariableEventHandler() {

					@Override
					public void onNewVariable(NewSelectedRowsVariableEvent event) {
						addNewSelectedRowsVariable(event
								.getSelectedRowsVariable());

					}

				});
		Log.debug("InputVariablePanel bind to Event do!");
	}

	private void addNewSelectedRowsVariable(
			InputOutputVariables selectedRowsVariable) {
		inputVariableTabPanel.addSelectedRowsVariable(selectedRowsVariable);

	}

	private void manageInputRequestEvents(InputRequestEvent event) {
		inputVariableTabPanel.requestInput();
	}

	private void manageProjectStatusEvents(ProjectStatusEvent event) {
		Log.debug("InputVariablePanel recieved event ProjectStatus: "
				+ event.toString());
		switch (event.getProjectStatusEventType()) {
		case SAVE:
		case SOFTWARE_PUBLISH:
		case SOFTWARE_REPACKAGE:
		case START:
		case ADD_RESOURCE:
		case DELETE_RESOURCE:
		case EXPLORER_REFRESH:	
			break;
		case OPEN:
			create(event.getProject());
			break;
		case UPDATE:
			updatePanel(event.getProject());
			break;
		case DELETE_MAIN_CODE:
			setMainCode(event.getProject());
			break;
		case MAIN_CODE_SET:
			setMainCode(event.getProject());
			break;
		case BINARY_CODE_SET:
			setBinaryCode(event.getProject());	
		default:
			break;
		}

	}

	private void create(Project project) {
		inputVariableTabPanel = new InputVariableTabPanel(eventBus, this);
		add(inputVariableTabPanel);
		startPanel(project);
		forceLayout();
	}

	/*
	 * private void closePanelOnly() { collapse(); disable(); state =
	 * InputVariablePanelState.CLOSED; }
	 */

	private void startPanel(Project project) {
		enable();
		expand();
		// state = InputVariablePanelState.OPENED;
		inputVariableTabPanel.startTabs(project);

	}

	private void updatePanel(Project project) {
		inputVariableTabPanel.updateTabs(project);
		Log.debug("ToolBoxPanel Updated");

	}

	private void setMainCode(Project project) {
		inputVariableTabPanel.setMainCode(project);
		Log.debug("ToolBoxPanel Updated");

	}
	
	private void setBinaryCode(Project project) {
		inputVariableTabPanel.setBinaryCode(project);
		Log.debug("ToolBoxPanel Updated");

	}

}
