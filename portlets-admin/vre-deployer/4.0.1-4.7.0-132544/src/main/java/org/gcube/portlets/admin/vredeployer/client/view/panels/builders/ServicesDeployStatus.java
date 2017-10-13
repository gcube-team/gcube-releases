package org.gcube.portlets.admin.vredeployer.client.view.panels.builders;

import java.util.List;

import org.gcube.portlets.admin.vredeployer.client.charts.CloudChart;
import org.gcube.portlets.admin.vredeployer.client.util.DelayedOperation;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientResourceManagerDeployingReport;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.DeployStatus;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Window;

public class ServicesDeployStatus {
	private ContentPanel cp;

	boolean barAdded = false;
	ProgressBar bar;

	public ServicesDeployStatus() {
		cp = new ContentPanel(new FitLayout());
		cp.setLayout(new CenterLayout());  
	}

	public void updateReport(ClientResourceManagerDeployingReport report) {
		cp.unmask();
		DeployStatus managerStatus = report.getStatus();
		int i = 0;
		String label = "% Complete";
		switch (managerStatus) {
		case FAIL:
			label = "% Failure, see textual report";
			i = 0;
			break;
		case RUN:
			i = 50;
			break;
		case WAIT:
			label = "% not started yet";
			i = 0;
			break;		
		case FINISH:
			label =  "% Complete";
			i = 100;
			break;

		default:
			break;
		}

		if (! barAdded) {
			createReport();
			barAdded = true;
			bar.updateProgress(0, " not started yet");  
			cp.add(bar);   
			bar.setWidth("90%");
			cp.layout();
		}
		else {
			if (report.getStatus() != DeployStatus.FINISH) {
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
		if (report.getStatus() == DeployStatus.FAIL || report.getStatus() == DeployStatus.FINISH) {			
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
	public ContentPanel getServicesDeployStatusPanel() {
		cp.setHeaderVisible(true);
		cp.setHeading("Services dynamic deploying");
		cp.setHeight(250);
		cp.setBorders(true);
		cp.mask("Retrieving data", "loading-indicator");	

		return cp;
	}
}
