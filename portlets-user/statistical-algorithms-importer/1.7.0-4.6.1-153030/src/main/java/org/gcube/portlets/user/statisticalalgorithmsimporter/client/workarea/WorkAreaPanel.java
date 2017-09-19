package org.gcube.portlets.user.statisticalalgorithmsimporter.client.workarea;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.ProjectStatusEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.WorkAreaEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.maindata.MainDataPanel;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.tools.ToolsPanel;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.type.ProjectStatusEventType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportBashEdit;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportBlackBox;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class WorkAreaPanel extends SimpleContainer {

	private EventBus eventBus;

	public WorkAreaPanel(EventBus eventBus) {
		super();
		this.eventBus = eventBus;
		init();
		create();
		bindToEvents();
	}

	private void init() {
		forceLayoutOnResize = true;
		setBorders(false);
	}

	private void create() {

	}

	private void bindToEvents() {
		eventBus.addHandler(WorkAreaEvent.TYPE, new WorkAreaEvent.WorkAreaEventHandler() {

			@Override
			public void onWorkArea(WorkAreaEvent event) {
				Log.debug("Work Area Panel Catch WorkAreaEvent: " + event);
				doProjectStatusCommand(event);

			}
		});
		Log.debug("Work Area Panel bind to event do!");

	}

	private void doProjectStatusCommand(WorkAreaEvent event) {
		if (event.getWorkAreaEventType() == null) {
			return;
		}
		switch (event.getWorkAreaEventType()) {
		case WORK_AREA_SETUP:
			setupWorkAreaPanel(event.getProject());
			break;
		default:
			break;

		}

	}

	private void setupWorkAreaPanel(Project project) {
		if (project != null) {
			if (project.getProjectConfig() == null || project.getProjectConfig().getProjectSupport() == null) {
				Log.debug("Work Area Panel Set R Area! ");
				createRArea(project);
			} else {
				if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBlackBox) {
					Log.debug("Work Area Panel Set BlackBox Area! ");

					createBlackBoxArea(project);
				} else {
					if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBashEdit) {
						Log.debug("Work Area Panel Set BashEdit Area! ");

						createBashArea(project);
					} else {
						Log.debug("Work Area Panel Set R Area! ");
						createRArea(project);
					}
				}
			}
			forceLayout();
			fireProjectStatusOpenEvent(project);
		} else {
			Log.debug("Work Area Panel: project is null! ");

		}

	}

	private void createBlackBoxArea(Project project) {
		ToolsPanel toolsPanel = new ToolsPanel(eventBus);
		clear();
		add(toolsPanel);
	}

	private void createRArea(Project project) {
		// Main Panel
		BorderLayoutContainer mainPanelLayout = new BorderLayoutContainer();
		// mainPanelLayout.setId("mainPanelLayout");
		mainPanelLayout.setBorders(false);
		mainPanelLayout.setResize(true);

		// Main
		final MainDataPanel mainDataPanel = new MainDataPanel(eventBus);
		MarginData mainData = new MarginData(new Margins(2));
		mainPanelLayout.setCenterWidget(mainDataPanel, mainData);

		// Right
		ToolsPanel toolsPanel = new ToolsPanel(eventBus);
		BorderLayoutData eastData = new BorderLayoutData(500);
		eastData.setCollapsible(true);
		eastData.setSplit(false);
		eastData.setFloatable(false);
		eastData.setCollapseMini(true);
		eastData.setMargins(new Margins(0, 5, 0, 5));
		eastData.setCollapseHidden(false);

		mainPanelLayout.setEastWidget(toolsPanel, eastData);
		toolsPanel.enable();
		toolsPanel.collapse();

		clear();
		add(mainPanelLayout);

	}
	
	private void createBashArea(Project project) {
		// Main Panel
		BorderLayoutContainer mainPanelLayout = new BorderLayoutContainer();
		// mainPanelLayout.setId("mainPanelLayout");
		mainPanelLayout.setBorders(false);
		mainPanelLayout.setResize(true);

		// Main
		final MainDataPanel mainDataPanel = new MainDataPanel(eventBus);
		MarginData mainData = new MarginData(new Margins(2));
		mainPanelLayout.setCenterWidget(mainDataPanel, mainData);

		// Right
		ToolsPanel toolsPanel = new ToolsPanel(eventBus);
		BorderLayoutData eastData = new BorderLayoutData(500);
		eastData.setCollapsible(true);
		eastData.setSplit(false);
		eastData.setFloatable(false);
		eastData.setCollapseMini(true);
		eastData.setMargins(new Margins(0, 5, 0, 5));
		eastData.setCollapseHidden(false);

		mainPanelLayout.setEastWidget(toolsPanel, eastData);
		toolsPanel.enable();
		toolsPanel.collapse();

		clear();
		add(mainPanelLayout);

	}
	

	private void fireProjectStatusOpenEvent(final Project project) {
		try {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				
				@Override
				public void execute() {
					ProjectStatusEvent projectStatusEvent = new ProjectStatusEvent(ProjectStatusEventType.OPEN, project);
					Log.debug("Work Area Panel ProjectStatusEvent fire! " + projectStatusEvent);
					eventBus.fireEvent(projectStatusEvent);
					
				}
			});
			
			
		} catch (Throwable e) {
			Log.error(e.getLocalizedMessage(), e);
		}

	}

}
