/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.experimentArea;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.statisticalmanager.client.Constants;
import org.gcube.portlets.user.statisticalmanager.client.StatisticalManager;
import org.gcube.portlets.user.statisticalmanager.client.bean.ComputationStatus;
import org.gcube.portlets.user.statisticalmanager.client.bean.ComputationStatus.Status;
import org.gcube.portlets.user.statisticalmanager.client.bean.Operator;
import org.gcube.portlets.user.statisticalmanager.client.widgets.ComputationOutputPanel;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author ceras
 *
 */
public class ComputationStatusPanel extends LayoutContainer {

	private ProgressBar progressBar = new ProgressBar();
	private String computationId;
	private Operator operator;
	private boolean terminated=false;
    Logger logger = Logger.getLogger("NameOfYourLogger");
	private ComputationTimer timer = new ComputationTimer();
	
	/**
	 * 
	 */
	public ComputationStatusPanel(Operator operator) {
		super();
		this.setStyleAttribute("margin", "20px");
		this.setStyleAttribute("padding", "10px");
		this.setStyleAttribute("border", "1px solid #000050");
		this.operator = operator;
	}

	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.ContentPanel#onRender(com.google.gwt.user.client.Element, int)
	 */
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		Html title = new Html("<div class='computationTitle'>Computation of <b>" + operator.getName() + "</b></div>");
		Html date = new Html("<div class='computationDate'>" + new Date().toString() + "</div>");
		this.add(title);
		this.add(date);
		this.layout();
	}
	
	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.Container#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		progressBar.repaint();
	}
	
	/**
	 * @param computationId the computationId to set
	 */
	public void computationStarted(String computationId) {
		this.computationId = computationId;
		this.add(new Html("Created, the id is "+computationId));
		
		progressBar.setStyleAttribute("margin", "20px");
		progressBar.updateProgress(0, "Starting...");		
		this.add(progressBar);
		this.layout();
		timer.scheduleRepeating(Constants.TIME_UPDATE_COMPUTATION_STATUS_PANEL);
	}
	

	/**
	 * @param computationId 
	 * @param operator 
	 * 
	 */
	protected void computationTerminated(String computationId, Operator operator, ComputationStatus computationStatus) {
		if (terminated == false) {
			terminated = true;

			if (computationStatus.isComplete()) {
				Info.display("Terminated", "The computation "+computationId+" of "+operator.getName()+" is terminated correctly.");
				progressBar.updateProgress(1, "Computation Complete");
				progressBar.addStyleName("progressBar-complete");

				showOutput();

			} else if (computationStatus.isFailed()) {
//				Info.display("Failed", "The computation of "+operator.getName()+" has failed.");
//				MessageBox.alert("Failed", "The computation "+computationId+" of "+operator.getName()+" has failed.</br>"
//						+"Message: "+computationStatus.getMessage(), null);
				MessageBox.alert("Failed", "The computation "+computationId+" of "+operator.getName()+" has failed.</br>"
				+"Message: "+computationStatus.getErrResource().getDescription(), null);
				progressBar.updateProgress(1, "Computation Fail");
				progressBar.addStyleName("progressBar-failed");
			}
			
		}
		this.layout();
	}
	
	
	/**
	 * 
	 */
	private void showOutput() {
		this.add(new Html(" The computation "+operator.getName()+" finished."));
		ComputationOutputPanel computationOutputPanel = new ComputationOutputPanel(computationId);
		this.add(computationOutputPanel);
		
	}

	/**
	 * @param computationStatus
	 */
	protected void updateStatus(ComputationStatus computationStatus) {
	    logger.log(Level.SEVERE, "Conputation Status Panel ::Update Status ");
		if (computationStatus.getStatus() == Status.PENDING)
			progressBar.updateText("Pending...");
		else {
			double percentage = computationStatus.getPercentage();
			progressBar.updateProgress(percentage/100,
					"Running, " + NumberFormat.getFormat("0.00").format(percentage) + "% Complete");
		}
	}

	
	private class ComputationTimer extends Timer {

		/* (non-Javadoc)
		 * @see com.google.gwt.user.client.Timer#run()
		 */
		@Override
		public void run() {
		    logger.log(Level.SEVERE, "Timer run .....");
//			final ComputationStatusPanel cp = ComputationStatusPanel.this;
			StatisticalManager.getService().getComputationStatus(computationId, new AsyncCallback<ComputationStatus>() {

				@Override
				public void onFailure(Throwable caught) {
					progressBar.updateProgress(1, "Failed to get the status");
					progressBar.addStyleName("progressBar-failed");
//					MessageBox.alert("Error", ""+caught.getMessage()+"\nCause: "+caught.getCause()+"\nStackTrace: "+caught.getStackTrace().toString(), null);
//					computationTerminated(computationId, operator, TerminationMode.FAILED2);
//					ComputationTimer.this.cancel();
				}

				@Override
				public void onSuccess(ComputationStatus computationStatus) {
					if (computationStatus.isTerminated()) {
						computationTerminated(computationId, operator, computationStatus);
						ComputationTimer.this.cancel();
					} else
						updateStatus(computationStatus);
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
		this.removeAll();
		
	}
	
	/*
	protected void createProgressBar(Operator operator, String id) {
		progressBar = new ProgressBar();
		progressBar.setStyleAttribute("margin", "20px");
		progressBar.updateProgress(0, "Starting...");		
		this.add(progressBar);
		this.layout();
		timer = new ComputationTimer(operator, id);
		timer.scheduleRepeating(Constants.TIME_UPDATE_RANGE);
	}
	*/
}
