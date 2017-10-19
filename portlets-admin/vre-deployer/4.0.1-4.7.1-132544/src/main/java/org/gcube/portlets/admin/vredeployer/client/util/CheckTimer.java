
package org.gcube.portlets.admin.vredeployer.client.util;

import org.gcube.portlets.admin.vredeployer.client.VredeployerService;
import org.gcube.portlets.admin.vredeployer.client.VredeployerServiceAsync;
import org.gcube.portlets.admin.vredeployer.client.view.panels.builders.CloudDeployStatus;
import org.gcube.portlets.admin.vredeployer.client.view.panels.builders.OverallDeployStatus;
import org.gcube.portlets.admin.vredeployer.client.view.panels.builders.ResourcesDeployStatus;
import org.gcube.portlets.admin.vredeployer.client.view.panels.builders.ServicesDeployStatus;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientDeployReport;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.DeployStatus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author desperados
 *
 */
public class CheckTimer {
	/**
	 * delay in msec
	 */
	public static int DELAY = 8000; // 5 sec
	
	CloudDeployStatus cloudPanel;
	ServicesDeployStatus resourceManagerPanel;
	ResourcesDeployStatus resourceAndFuncPanel;
	OverallDeployStatus overallPanel;
	
	
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final VredeployerServiceAsync deployerService = GWT.create(VredeployerService.class);
	/**
	 * Constructor
	 * @param VREName VRE Name
	 */
	public CheckTimer(
			CloudDeployStatus cloudPanel, 
			ServicesDeployStatus resourceManagerPanel, 
			ResourcesDeployStatus resourceAndFuncPanel, 
			OverallDeployStatus overPanel) {
		
		this.cloudPanel = cloudPanel;
		this.resourceManagerPanel = resourceManagerPanel;
		this.resourceAndFuncPanel = resourceAndFuncPanel;
		this.overallPanel = overPanel;
	}
	
	private Timer t = new Timer(){
		public void run(){			
			deployerService.checkCreateVRE(timeCallback);
		}
		
	};
	
	/**
	 * 
	 */
	AsyncCallback<ClientDeployReport> timeCallback = new AsyncCallback<ClientDeployReport>(){

		public void onSuccess(ClientDeployReport report) {
			
			GWT.log("timeCallback");
			//**** Updating Panels ****//
			cloudPanel.updateReport(report.getCloudReport());
			resourceManagerPanel.updateReport(report.getResourceManagerReport());
			resourceAndFuncPanel.updateReport(report.getFunctionalityReport(), report.getResourcesReport());
			overallPanel.updateReport(report);
			if (report.getGlobalsStatus() == DeployStatus.FAIL || (report.getGlobalsStatus() == DeployStatus.FINISH) )
				cancelScheduling() ;
			
		}
		
		public void onFailure(Throwable caught) {
			GWT.log("timeCallback fails");
		}
		
	};
	
	
	/**
	 * @param delayMillis delay
	 */
	public void scheduleTimer(int seconds){
		t.scheduleRepeating(seconds*1000);
	}

	/**
	 * Cancel scheduling
	 */
	public void cancelScheduling() {
		t.cancel();
	}
	
}
