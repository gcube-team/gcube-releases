/**
 * 
 */
package org.gcube.portlets.widgets.dataminermanagerwidget.client.experiments;

import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.process.ComputationStatus;
import org.gcube.data.analysis.dataminermanagercl.shared.process.ComputationStatus.Status;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.common.EventBusProvider;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.custom.progress.GreenProgressBar;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.custom.progress.OrangeProgressBar;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.custom.progress.RedProgressBar;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.CancelComputationExecutionRequestEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.rpc.DataMinerPortletServiceAsync;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.util.UtilsGXT3;
import org.gcube.portlets.widgets.dataminermanagerwidget.shared.Constants;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ProgressBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ComputationStatusPanel extends SimpleContainer {

	private ProgressBar progressBar;
	private ComputationId computationId;
	private String operatorName;
	private boolean terminated = false;
	private ComputationTimer timer = new ComputationTimer();
	private TextButton cancelComputationBtn;
	private VerticalLayoutContainer vert;

	/**
	 * 
	 * @param operatorName operator name
	 */
	public ComputationStatusPanel(String operatorName) {
		super();
		this.operatorName = operatorName;
		init();
		create();
	}

	private void init() {
		setStylePrimaryName("computationStatusPanel");
	}

	private void create() {
		vert = new VerticalLayoutContainer();
		HtmlLayoutContainer title = new HtmlLayoutContainer(
				"<div class='computationStatusTitle'><p>Computation of <b>"
						+ operatorName + "</b></p></div>");
		vert.add(title, new VerticalLayoutData(-1, -1, new Margins(0)));
		add(vert);
		forceLayout();
	}

	/**
	 * @param computationId
	 *            the computationId to set
	 */
	public void computationStarted(ComputationId computationId) {
		this.computationId = computationId;
		/*
		 * HtmlLayoutContainer date = new HtmlLayoutContainer(
		 * "<div class='computationStatusDate'><p>" + new Date().toString() +
		 * "</p></div>");
		 */
		// vert.add(date, new VerticalLayoutData(-1, -1, new Margins(0)));

		vert.add(new HtmlLayoutContainer("<p>Created, the id is "
				+ computationId.getId() + " [<a href='"
				+ computationId.getUrlId() + "' >link</a>]</p>"));

		TextButton equivalentRequestBtn = new TextButton();
		equivalentRequestBtn.setText("Show");
		equivalentRequestBtn.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				showEquivalentRequestDialog();

			}

		});

		FieldLabel equivalentRequestLabel = new FieldLabel(
				equivalentRequestBtn, "Equivalent Get Request");
		equivalentRequestLabel.setLabelWidth(140);
		vert.add(equivalentRequestLabel, new VerticalLayoutData(-1, -1,
				new Margins(0)));

		progressBar = new ProgressBar();
		progressBar.updateProgress(0, "Starting...");
		vert.add(progressBar, new VerticalLayoutData(1, -1, new Margins(20)));

		cancelComputationBtn = new TextButton("Cancel");

		cancelComputationBtn.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				cancelComputationBtn.disable();
				cancelComputation();

			}
		});

		cancelComputationBtn.getElement().getStyle()
				.setMarginBottom(36, Unit.PX);

		vert.add(cancelComputationBtn, new VerticalLayoutData(-1, -1,
				new Margins(0)));
		forceLayout();
		timer.scheduleRepeating(Constants.TIME_UPDATE_COMPUTATION_STATUS_PANEL);
	}

	private void showEquivalentRequestDialog() {
		EquivalentRequestDialog equivalentRequestDialog = new EquivalentRequestDialog(
				computationId);
		equivalentRequestDialog.show();
	}

	private void cancelComputation() {
		CancelComputationExecutionRequestEvent event = new CancelComputationExecutionRequestEvent(
				computationId);
		EventBusProvider.INSTANCE.fireEvent(event);
	}

	/**
	 * @param computationId
	 * @param operator
	 * 
	 */
	private void computationTerminated(ComputationId computationId,
			ComputationStatus computationStatus) {
		Log.debug("Computation Terminated");
		Log.debug("Computation Status:" + computationStatus);
		if (terminated == false) {
			terminated = true;
			cancelComputationBtn.setVisible(false);
			if (computationStatus.isComplete()) {
				Log.debug("Computation is Complete");
				Info.display("Terminated",
						"The computation " + computationId.getId() + " of "
								+ computationId.getOperatorName()
								+ " is terminated correctly.");
				int index = vert.getWidgetIndex(progressBar);
				vert.remove(index);
				// TODO
				progressBar = new GreenProgressBar();
				progressBar.updateProgress(1, "Computation Complete");
				vert.insert(progressBar, index, new VerticalLayoutData(1, -1,
						new Margins(20)));
				showComputationCompletedOutput();
			} else if (computationStatus.isFailed()) {
				Log.debug("Computation is Failed");
				String errorMessage;
				if (computationStatus.getError() == null) {
					errorMessage = new String("Computation Failed!");
				} else {
					errorMessage = computationStatus.getError()
							.getLocalizedMessage();
				}
				Info.display("Failed",
						"The computation " + computationId.getId() + " of "
								+ computationId.getOperatorName()
								+ " is failed.");
				UtilsGXT3.alert("Failed",
						"The computation " + computationId.getId() + " of "
								+ computationId.getOperatorName()
								+ " has failed.</br>" + errorMessage);
				int index = vert.getWidgetIndex(progressBar);
				vert.remove(index);
				// TODO
				progressBar = new RedProgressBar();
				progressBar.updateProgress(1, "Computation Failed");
				progressBar.getElement().getStyle()
						.setMarginBottom(36, Unit.PX);
				vert.insert(progressBar, index, new VerticalLayoutData(1, -1,
						new Margins(20)));
			} else if (computationStatus.isCancelled()) {
				Log.debug("Computation Cancelled");
				String errorMessage;
				errorMessage = new String("Computation Cancelled!");
				Info.display("Info", "The computation " + computationId.getId()
						+ " of " + computationId.getOperatorName()
						+ " has been cancelled.");
				UtilsGXT3.info("Info",
						"The computation " + computationId.getId() + " of "
								+ computationId.getOperatorName()
								+ " has been cancelled.</br>" + errorMessage);
				int index = vert.getWidgetIndex(progressBar);
				vert.remove(index);
				// TODO
				progressBar = new OrangeProgressBar();
				progressBar.updateProgress(1, "Computation Cancelled");
				progressBar.getElement().getStyle()
						.setMarginBottom(36, Unit.PX);
				vert.insert(progressBar, index, new VerticalLayoutData(1, -1,
						new Margins(20)));
			}

		}

		forceLayout();
	}

	/**
	 * 
	 */
	private void showComputationCompletedOutput() {
		HtmlLayoutContainer computationEndMessage = new HtmlLayoutContainer(
				"<p>The computation <b>" + computationId.getOperatorName()
						+ "</b> finished.</p>");
		vert.add(computationEndMessage, new VerticalLayoutData(-1, -1,
				new Margins(0)));
		ComputationOutputPanel computationOutputPanel = new ComputationOutputPanel(
				computationId);
		computationOutputPanel.getElement().getStyle()
				.setMarginBottom(36, Unit.PX);
		vert.add(computationOutputPanel, new VerticalLayoutData(1, -1,
				new Margins(0)));

	}

	/**
	 * @param computationStatus
	 */
	private void updateStatus(ComputationStatus computationStatus) {
		Log.debug("Conputation Status Panel ::Update Status "
				+ computationStatus);
		if (computationStatus.getStatus().compareTo(Status.ACCEPTED) == 0)
			progressBar.updateText("Accepted...");
		else {
			double percentage = computationStatus.getPercentage();
			if (percentage == 0) {
				progressBar.updateText("Running, 0% Complete");
			} else {
				progressBar.updateProgress(percentage / 100, "Running, "
						+ percentage + "% Complete");
			}
		}
		forceLayout();
	}

	private class ComputationTimer extends Timer {

		@Override
		public void run() {
			Log.debug("Timer run .....");
			DataMinerPortletServiceAsync.INSTANCE.getComputationStatus(
					computationId, new AsyncCallback<ComputationStatus>() {

						@Override
						public void onFailure(Throwable caught) {
							int index = vert.getWidgetIndex(progressBar);
							vert.remove(index);
							// TODO
							progressBar = new RedProgressBar();
							progressBar.updateProgress(1,
									"Failed to get the status");
							progressBar.getElement().getStyle()
									.setMarginBottom(36, Unit.PX);
							vert.insert(progressBar, index,
									new VerticalLayoutData(1, -1, new Margins(
											20)));

						}

						@Override
						public void onSuccess(
								ComputationStatus computationStatus) {
							Log.debug("ComputationStatus: "+computationStatus);
							if (computationStatus != null) {
								if (computationStatus.isTerminated()) {
									ComputationTimer.this.cancel();
									computationTerminated(computationId,
											computationStatus);
								} else
									updateStatus(computationStatus);
							}
						}
					});
		}

	}

	/**
	 * 
	 */
	public void stopTimer() {
		try {
			timer.cancel();
		} catch (Exception e) {
		}
		vert.clear();

	}
}
