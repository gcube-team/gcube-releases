package org.gcube.portlets.user.dataminermanager.client.experiments;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.dataminermanager.client.DataMinerManager;
import org.gcube.portlets.user.dataminermanager.client.common.EventBusProvider;
import org.gcube.portlets.user.dataminermanager.client.events.ResubmitComputationExecutionEvent;
import org.gcube.portlets.user.dataminermanager.client.events.StartComputationExecutionEvent;
import org.gcube.portlets.user.dataminermanager.client.events.StartComputationExecutionRequestEvent;
import org.gcube.portlets.user.dataminermanager.shared.process.Operator;

import com.allen_sauer.gwt.log.client.Log;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ComputationExecutionPanel extends FramedPanel {

	private List<ComputationStatusPanel> computationStatusPanels = new ArrayList<ComputationStatusPanel>();

	private static final String DELETE_ALL_BUTTON_TOOLTIP = "Remove Computations Log";
	private TextButton removeAllButton;

	private VerticalLayoutContainer v;

	private ToolBar toolBar;

	private VerticalLayoutContainer topV;

	/**
	 * 
	 */
	public ComputationExecutionPanel() {
		super();
		Log.debug("Computation Execution Panel");
		init();
		create();
		bind();
	}

	private void init() {
		setHeaderVisible(false);
		addStyleName("computationExcecutionPanel");
		setBodyStyle("backgroundColor:white;");
	}

	private void create() {
		topV = new VerticalLayoutContainer();
		setToolBar();
		topV.add(toolBar, new VerticalLayoutData(1, -1, new Margins(0)));
		SimpleContainer computations = new SimpleContainer();
		v = new VerticalLayoutContainer();
		v.setScrollMode(ScrollMode.AUTO);
		computations.add(v);
		topV.add(computations, new VerticalLayoutData(1, 1, new Margins(0)));
		add(topV);
	}

	private void bind() {
		EventBusProvider.INSTANCE
				.addHandler(
						ResubmitComputationExecutionEvent.getType(),
						new ResubmitComputationExecutionEvent.ResubmitComputationExecutionEventHandler() {
							@Override
							public void onResubmit(
									ResubmitComputationExecutionEvent event) {
								Log.debug("ResubmitComputationExecutionEvent: "
										+ event);
								resubmitComputation(event);
							}
						});

		EventBusProvider.INSTANCE
				.addHandler(
						StartComputationExecutionEvent.getType(),
						new StartComputationExecutionEvent.StartComputationExecutionEventHandler() {

							@Override
							public void onStart(
									StartComputationExecutionEvent event) {
								Log.debug("Catch StartComputationExecutionEvent: "
										+ event);
								startComputation(event);

							}

						});

		

	}

	public void startNewComputation(final Operator operator) {
		Log.debug("Computation Panel: start new computation ");
		ComputationStatusPanel statusPanel = new ComputationStatusPanel(
				operator.getName());
		computationStatusPanels.add(statusPanel);
		int index = computationStatusPanels.indexOf(statusPanel);
		statusPanel.setItemId("ComputationStatusPanel" + String.valueOf(index));

		Log.debug("Added status bar");
		v.insert(statusPanel, 0, new VerticalLayoutData(1, -1, new Margins(20)));
		removeAllButton.setEnabled(true);
		forceLayout();

		StartComputationExecutionRequestEvent event = new StartComputationExecutionRequestEvent(
				operator, index);
		EventBusProvider.INSTANCE.fireEvent(event);

	}

	private void startComputation(StartComputationExecutionEvent event) {
		try {
			ComputationStatusPanel statusPanel = null;
			try {
				statusPanel = computationStatusPanels.get(event
						.getComputationStatusPanelIndex());
			} catch (IndexOutOfBoundsException e) {
				Log.debug("No ComputationStatusPanel retrieved!");
				Log.debug(e.getLocalizedMessage());
			}

			if (statusPanel == null) {

			} else {
				statusPanel.computationStarted(event.getComputationId());
				forceLayout();
			}

		} catch (Throwable e) {
			Log.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void resubmitComputation(ResubmitComputationExecutionEvent event) {
		final ComputationStatusPanel statusPanel = new ComputationStatusPanel(
				event.getComputationId().getOperatorName());
		computationStatusPanels.add(statusPanel);
		if (v.getWidgetCount() == 0) {
			v.add(statusPanel, new VerticalLayoutData(1, -1, new Margins(20)));
		} else {
			v.insert(statusPanel, 0, new VerticalLayoutData(1, -1, new Margins(
					20)));
		}
		removeAllButton.setEnabled(true);

		statusPanel.computationStarted(event.getComputationId());

		forceLayout();

	}

	

	/**
	 * 
	 */
	private void emptyPanel() {
		// stop timers
		for (ComputationStatusPanel statusPanel : computationStatusPanels)
			statusPanel.stopTimer();

		removeAllButton.setEnabled(false);
		v.clear();
		computationStatusPanels.clear();
		forceLayout();
	}

	private void setToolBar() {
		toolBar = new ToolBar();

		removeAllButton = new TextButton("Remove All");

		removeAllButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				emptyPanel();

			}
		});

		removeAllButton.setIcon(DataMinerManager.resources.removeAll());

		removeAllButton.setToolTip(DELETE_ALL_BUTTON_TOOLTIP);
		// removeAllButton.setScale(ButtonScale.MEDIUM);
		removeAllButton.setEnabled(false);

		toolBar.add(new LabelToolItem("Tools:"));
		toolBar.add(removeAllButton);

	}
}
