/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.client.algorithms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.trendylyzer_portlet.client.Constants;
import org.gcube.portlets.user.trendylyzer_portlet.client.TrendyLyzer_portlet;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.ComputationStatus.Status;
import org.gcube.portlets.user.trendylyzer_portlet.client.widgets.ComputationOutputPanel;
import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.Parameter;


import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ComputationStatusPanel extends LayoutContainer {

	private ProgressBar progressBar = new ProgressBar();
	private String computationId;
	private Algorithm operator;
	private boolean terminated=false;
	Logger log = Logger.getLogger("");

	private ComputationTimer timer = new ComputationTimer();
	
	/**
	 * 
	 */
	public ComputationStatusPanel(Algorithm operator) {
		super();
		this.setStyleAttribute("margin", "20px");
		this.setStyleAttribute("padding", "10px");
		this.setStyleAttribute("border", "1px solid #000050");
		this.operator = operator;
		this.operator.setAlgorithmParameters(operator.getAlgorithmParameters());
//		List<Parameter>parameters= new ArrayList<Parameter>();
//		parameters=operator.getAlgorithmParameters();
//		log.log(Level.SEVERE, "inside computationStatusPanel constructor");
//		for(Parameter p: parameters)
//		{
//			//this.operator.setAlgorithmParameters(operator.getAlgorithmParameters());
//			String param=p.getName()+":&nbsp"+p.getValue()+"<BR>";
//			
//			log.log(Level.SEVERE, param);
//		}
	}

	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.ContentPanel#onRender(com.google.gwt.user.client.Element, int)
	 */
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		
//		List<Parameter>parameters= new ArrayList<Parameter>();
//		parameters=operator.getAlgorithmParameters();
//		
//		for(Parameter p: parameters)
//		{
//			
//			String param="<div>"+p.getName().toString()+p.getValue().toString()+"</div>";
//			Html par= new Html(param);
//			this.add(par);
//			log.log(Level.SEVERE, param);
//		}
		Html title = new Html("<div class='computationTitle'>Computation of <b>" +operator.getName() + "</b></div>");
//		Html date = new Html("<div class='computationDate'>" + new Date().toString() + "</div>");
		this.add(title);
//		this.add(date);
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
		//this.add(new Html("Created, the id is "+computationId));
		
		progressBar.setStyleAttribute("margin", "20px");
		progressBar.updateProgress(0, "Starting...");		
		this.add(progressBar);
		this.layout();
		timer.scheduleRepeating(Constants.TIME_UPDATE_COMPUTATION_STATUS_PANEL);
	}
	

	/**
	 * @param computationId 
	 * @param operator 
	 * @throws InterruptedException 
	 * 
	 */
	protected void computationTerminated(String computationId, Algorithm operator, ComputationStatus computationStatus) {
		if (terminated == false) {
			terminated = true;
			log.log(Level.SEVERE,"Inside computationTerminated");

			if (computationStatus.isComplete()) {
				//Info.display("Terminated", "The computation "+computationId+" of "+operator.getName()+" is terminated correctly.");
				progressBar.updateProgress(1, "Computation Complete");
				progressBar.addStyleName("progressBar-complete");

				showOutput();
				Timer t = new Timer() {
					  public void run() {
						  progressBar.setVisible(false);
					  }
					};

					// delay running for 5 seconds
					t.schedule(2000); 
				
				

			} else if (computationStatus.isFailed()) {
//				Info.display("Failed", "The computation of "+operator.getName()+" has failed.");
				MessageBox.alert("Failed", "The computation "+computationId+" of "+operator.getName()+" has failed.</br>"
						+"Message: "+computationStatus.getMessage(), null);
				progressBar.updateProgress(1, "Computation Failed");
				progressBar.addStyleName("progressBar-failed");
			}
			
		}
		this.layout();
	}
	
	
	/**
	 * 
	 */
	private void showOutput() {
		String title="&nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp;Result of "+operator.getName()+ "<BR>";
		Html h= getHtmlTitle(title);
		//h.setStyleName("jobViewer-output-outputType");
		this.add(h);
//		List<Parameter>parameters= new ArrayList<Parameter>();
//		parameters=operator.getAlgorithmParameters();
//		
//		for(Parameter p: parameters)
//		{
//			
//			String param="<div><b>"+p.getName().toString()+p.getValue().toString()+"<b></div>";
//			log.log(Level.SEVERE, param);
//			Html ht= new Html(param);
//			//h.setStyleName("jobViewer-output-outputType");
//			this.add(ht);
//			//log.log(Level.SEVERE, title);
//		}
		
		
		ComputationOutputPanel computationOutputPanel = new ComputationOutputPanel(computationId);
		this.add(computationOutputPanel);
		
	}
	private Html getHtmlTitle(String title) {
		Html html = new Html(title);
		html.setStyleName("jobViewer-output-outputType");
		return html;
	}
	/**
	 * @param computationStatus
	 */
	protected void updateStatus(ComputationStatus computationStatus) {
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
//			final ComputationStatusPanel cp = ComputationStatusPanel.this;
			TrendyLyzer_portlet.getService().getComputationStatus(computationId, new AsyncCallback<ComputationStatus>() {

				public void onFailure(Throwable caught) {
					progressBar.updateProgress(1, "Failed to get the status");
					progressBar.addStyleName("progressBar-failed");
//					MessageBox.alert("Error", ""+caught.getMessage()+"\nCause: "+caught.getCause()+"\nStackTrace: "+caught.getStackTrace().toString(), null);
//					computationTerminated(computationId, operator, TerminationMode.FAILED2);
//					ComputationTimer.this.cancel();
				}

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
	

}
