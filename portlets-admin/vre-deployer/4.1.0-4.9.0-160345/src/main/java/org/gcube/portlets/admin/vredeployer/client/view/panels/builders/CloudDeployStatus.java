package org.gcube.portlets.admin.vredeployer.client.view.panels.builders;

import java.util.List;

import org.gcube.portlets.admin.vredeployer.client.charts.CloudChart;
import org.gcube.portlets.admin.vredeployer.client.util.DelayedOperation;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientCloudReport;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.DeployStatus;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * 
 * @author massi
 *
 */
public class CloudDeployStatus {

	private ContentPanel cp;
	boolean chartAdded = false;
	CloudChart cloudChart;
	Chart myChart;

	public CloudDeployStatus() {
		cp = new ContentPanel(new FitLayout());
	}

	/**
	 * 
	 */
	public void createReport() {
		cp.removeAll();
		cloudChart = new CloudChart();

	}

	public void updateReport(ClientCloudReport report) {
		cp.unmask();
		final DeployStatus cloudStatus = report.getStatus();
		if (cloudStatus == DeployStatus.SKIP) {
			cp.setHeading("Cloud machines setup was skipped");
			cp.add(new Html("Cloud deploy was not selected for this VRE"));
			cp.layout();
		}
		else {
			final List<DeployStatus> singleMachines = report.getItemsStatuses();
			if (! chartAdded) {
				createReport();
				chartAdded = true;
				myChart = cloudChart.getChart();
				myChart.setChartModel(cloudChart.getChartModel(cloudStatus, singleMachines));
				cp.add(myChart);    

				cp.layout();
			}
			else {
				if (report.getStatus() != DeployStatus.FINISH) {
					cp.mask("Retrieving data", "loading-indicator");	
					DelayedOperation delay =	new DelayedOperation() {
						@Override
						public void doJob() { 
							cp.unmask();		
							myChart.setChartModel(cloudChart.getChartModel(cloudStatus, singleMachines));
							myChart.refresh();
						} 
					};
					delay.start(1500);
				}
				else {
					cp.unmask();		
					myChart.setChartModel(cloudChart.getChartModel(cloudStatus, singleMachines));
					myChart.refresh();
				}

			}
		}
	}
	/**
	 * 
	 * @return
	 */
	public ContentPanel getCloudDeployStatusPanel() {
		cp.setHeaderVisible(true);
		cp.setHeading("Cloud machines setup");
		cp.setHeight(250);
		cp.setBorders(true);
		cp.mask("Retrieving data", "loading-indicator");	

		return cp;
	}
}
