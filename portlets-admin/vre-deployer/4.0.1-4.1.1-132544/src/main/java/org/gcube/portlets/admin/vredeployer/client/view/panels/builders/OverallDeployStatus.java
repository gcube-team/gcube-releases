package org.gcube.portlets.admin.vredeployer.client.view.panels.builders;

import org.gcube.portlets.admin.vredeployer.client.util.DelayedOperation;
import org.gcube.portlets.admin.vredeployer.client.view.panels.ReportPanel;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientDeployReport;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.DeployStatus;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

public class OverallDeployStatus {
	/**
	 * 
	 */
	private ContentPanel cp;
	boolean barAdded = false;
	ProgressBar bar;
	/**
	 * 
	 */
	public OverallDeployStatus() {
		cp = new ContentPanel(new FitLayout());
		cp.setLayout(new CenterLayout()); 		
	}

	private int getStatusDependingOnOtherSteps(ClientDeployReport report) {
		int cloudStep = 0;
		if (report.getCloudReport().getStatus() != null) {
			if (report.getCloudReport().getStatus() == DeployStatus.FINISH || report.getCloudReport().getStatus() == DeployStatus.SKIP)
				cloudStep = 25;
		}
		int resourceManagerStep = 0;
		if (report.getResourceManagerReport().getStatus() != null) {
			if (report.getResourceManagerReport().getStatus() == DeployStatus.FINISH)
				resourceManagerStep = 25;
		}
		int funcStep = 0;
		if (report.getFunctionalityReport().getStatus() != null) {
			if (report.getFunctionalityReport().getStatus() == DeployStatus.FINISH)
				funcStep = 25;
		}
		int genresStep = 0;
		if (report.getResourcesReport().getStatus() != null) {
			if (report.getResourcesReport().getStatus() == DeployStatus.FINISH)
				genresStep = 25;
		}
		return cloudStep + resourceManagerStep + funcStep + genresStep;
	}
	/**
	 * 
	 * @param funcReport
	 * @param resourcesReport
	 */
	public void updateReport(ClientDeployReport report) {
		
		cp.unmask();
		DeployStatus overallStatus = report.getGlobalsStatus();
		GWT.log("overallStatus: " + overallStatus);
		int i = getStatusDependingOnOtherSteps(report);
		String label = "% Complete";
	
		if (! barAdded) {
			createReport();
			barAdded = true;
			bar.updateProgress(0, " not started yet");  
			cp.add(bar);   
			bar.setWidth("90%");
		
			cp.layout();
		}
		else {
			if (report.getGlobalsStatus() != DeployStatus.FINISH || report.getGlobalsStatus() != DeployStatus.FAIL) {
				cp.mask("Retrieving data", "loading-indicator");	
				DelayedOperation delay =	new DelayedOperation() {
					@Override
					public void doJob() { 
						cp.unmask();	
					} 
				};
				delay.start(1500);
			}			
			bar.setWidth("90%");
			bar.updateProgress((i/100), (int )i +label);  
			cp.layout();
		}
		if (report.getGlobalsStatus() == DeployStatus.FAIL || report.getGlobalsStatus() == DeployStatus.FINISH) {
			Window.alert("VRE Deploying report completed: " + report.getGlobalsStatus() );
			bar.setWidth("90%");
			i = 100;
			bar.updateProgress((i/100), (int )i +label);  
			cp.layout();
		}
	}

	/**
	 * 
	 */
	public void createReport() {
		cp.removeAll();
		bar = new ProgressBar();
			
	}

	/**
	 * 
	 * @return
	 */
	public ContentPanel getOverallDeployStatusPanel() {
		cp.setHeaderVisible(true);
		cp.setHeading("Overall deploying status");
		cp.setHeight(250);
		cp.setBorders(true);
		cp.mask("Retrieving data", "loading-indicator");	

		return cp;
	}
}
