package org.gcube.portlets.admin.vredeployer.client.view.panels.builders;

import java.util.List;

import org.gcube.portlets.admin.vredeployer.client.charts.FunctionalityChart;
import org.gcube.portlets.admin.vredeployer.client.util.DelayedOperation;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientCloudReport;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientFunctionalityDeployReport;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientResourcesDeployReport;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.DeployStatus;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class ResourcesDeployStatus {
	private ContentPanel cp;
	boolean chartAdded = false;
	FunctionalityChart funcChart;
	Chart myChart;
	/**
	 * 
	 */
	public ResourcesDeployStatus() {
		cp = new ContentPanel(new FitLayout());
	}
	/**
	 * 
	 * @param funcReport
	 * @param resourcesReport
	 */
	public void updateReport(final ClientFunctionalityDeployReport funcReport, ClientResourcesDeployReport resourcesReport) {
		cp.unmask();
		if (! chartAdded) {
			createReport();
			chartAdded = true;
			myChart = funcChart.getChart();		
			myChart.setChartModel(funcChart.getChartModel(funcReport));
		    cp.add(myChart);    
			cp.layout();
		}
		else {
			
			if (funcReport.getStatus() != DeployStatus.FINISH) {
				cp.mask("Retrieving data", "loading-indicator");	
				DelayedOperation delay =	new DelayedOperation() {
					@Override
					public void doJob() { 
						cp.unmask();		
						myChart.setChartModel(funcChart.getChartModel(funcReport));
						myChart.refresh();
					} 
				};
				delay.start(1500);
			}
			else {
				cp.unmask();		
				myChart.setChartModel(funcChart.getChartModel(funcReport));
				myChart.refresh();
			}
		}
	}
	
	/**
	 * 
	 */
	public void createReport() {
		cp.removeAll();
		funcChart = new FunctionalityChart();
	}
	/**
	 * 
	 * @param report
	 */
	public void updateReport(ClientFunctionalityDeployReport report) {
		cp.unmask();
		DeployStatus status = report.getStatus();

		if (! chartAdded) {
			createReport();
			chartAdded = true;
			myChart = funcChart.getChart();
			myChart.setChartModel(funcChart.getChartModel(report));
		    cp.add(myChart);
  			cp.layout();
		}
		else {
			myChart.setChartModel(funcChart.getChartModel(report));
			myChart.refresh();
		}
	}
	/**
	 * 
	 * @return
	 */
	public ContentPanel getResourcesDeployStatusPanel() {
		cp.setHeaderVisible(true);
		cp.setHeading("Functionality deploying");
		cp.setHeight(250);
		cp.setBorders(true);
		cp.mask("Retrieving data", "loading-indicator");	
		
		return cp;
	}
}
