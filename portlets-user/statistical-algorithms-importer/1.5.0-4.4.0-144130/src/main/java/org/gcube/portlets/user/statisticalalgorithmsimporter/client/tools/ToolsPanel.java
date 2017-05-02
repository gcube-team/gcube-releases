package org.gcube.portlets.user.statisticalalgorithmsimporter.client.tools;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.ProjectStatusEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.tools.explorer.ExplorerProjectPanel;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.tools.input.InputVariablePanel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ToolsPanel extends ContentPanel {

	private EventBus eventBus;
	private InputVariablePanel inputVariablePanel;
	private ExplorerProjectPanel explorerProjectPanel;
	private boolean first = true;

	public ToolsPanel(EventBus eventBus) {
		super();
		this.eventBus = eventBus;
		init();
		create();
		bindToEvents();
	}

	protected void init() {
		forceLayoutOnResize = true;
		setHeaderVisible(false);
		setResize(true);
		setBodyBorder(false);
		setBorders(false);
		setHideCollapseTool(true);

	}

	private void bindToEvents() {
		eventBus.addHandler(ProjectStatusEvent.TYPE,
				new ProjectStatusEvent.ProjectStatusEventHandler() {

					@Override
					public void onProjectStatus(ProjectStatusEvent event) {
						Log.debug("Catch ProjectStatusEvent");
						doProjectStatusCommand(event);

					}
				});
	}

	private void doProjectStatusCommand(ProjectStatusEvent event) {
		if (first) {
			expand();
		} else {

		}
	}

	private void create() {

		inputVariablePanel = new InputVariablePanel(eventBus);
		explorerProjectPanel = new ExplorerProjectPanel(eventBus);

		/*
		 * AccordionLayoutAppearance appearance = GWT
		 * .<AccordionLayoutAppearance> create(AccordionLayoutAppearance.class);
		 * inputVariablePanel = new InputVariablePanel(eventBus, appearance);
		 * explorerProjectPanel = new ExplorerProjectPanel(eventBus,
		 * appearance);
		 * 
		 * AccordionLayoutContainer accordion = new AccordionLayoutContainer();
		 * accordion.setExpandMode(ExpandMode.SINGLE_FILL);
		 * accordion.add(inputVariablePanel);
		 * accordion.add(explorerProjectPanel);
		 * accordion.setActiveWidget(inputVariablePanel);
		 * 
		 * add(accordion, new MarginData(new Margins(0)));
		 */

		/*
		 * VerticalLayoutContainer v = new VerticalLayoutContainer();
		 * v.add(inputVariablePanel, new VerticalLayoutData(1, 0.5, new
		 * Margins(0))); v.add(explorerProjectPanel, new VerticalLayoutData(1,
		 * 0.5, new Margins( 0)));
		 * 
		 * add(v, new MarginData(new Margins(0)));
		 * 
		 * forceLayout();
		 */

		MarginData centerData = new MarginData(0);

		BorderLayoutData southData = new BorderLayoutData(0.5);
		southData.setMargins(new Margins(0));
		southData.setCollapsible(true);
		southData.setSplit(true);

		BorderLayoutContainer borderLayoutContainer = new BorderLayoutContainer();
		borderLayoutContainer.setCenterWidget(inputVariablePanel, centerData);
		borderLayoutContainer.setSouthWidget(explorerProjectPanel, southData);

		add(borderLayoutContainer, new MarginData(new Margins(0)));

	}

}
