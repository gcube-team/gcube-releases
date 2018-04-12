package org.gcube.portlets.admin.vredeployer.client.charts;

import java.util.List;

import org.gcube.portlets.admin.vredeployer.shared.deployreport.DeployStatus;

import com.extjs.gxt.charts.client.model.ChartModel;


public interface SimpleChartModel {

	  public ChartModel getChartModel(DeployStatus cloudStatus, List<DeployStatus> singleMachines);
}
