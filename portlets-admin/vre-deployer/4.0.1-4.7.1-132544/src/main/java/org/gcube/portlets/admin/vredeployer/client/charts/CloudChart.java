package org.gcube.portlets.admin.vredeployer.client.charts;

import java.util.List;

import org.gcube.portlets.admin.vredeployer.shared.deployreport.DeployStatus;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.YAxis;
import com.extjs.gxt.charts.client.model.charts.BarChart;
import com.extjs.gxt.charts.client.model.charts.FilledBarChart;
import com.extjs.gxt.charts.client.model.charts.BarChart.Bar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Random;

public class CloudChart implements SimpleChartModel {

	private Chart chart;

	public CloudChart() {
		super();
		String url = GWT.getModuleBaseURL() + "../";
		url += "gxt/chart/open-flash-chart.swf";
		chart = new Chart(url);
		chart.setBorders(true);
	}

	/**
	 * 
	 * @return
	 */
	public Chart getChart() {
		return chart;
	}

	/**
	 * 
	 */
	public ChartModel getChartModel(DeployStatus cloudStatus, List<DeployStatus> singleMachines) {
		ChartModel cm = new ChartModel("current status: " + cloudStatus,	"font-size: 14px; font-family: Verdana; text-align: center;");  
		cm.setBackgroundColour("#FFFFFF");
		cm.setDecimalSeparatorComma(true);

		int n = singleMachines.size();

		XAxis xa = new XAxis();
		xa.setLabels(getAscisseValues(singleMachines));
		xa.getLabels().setColour("#009900");
		xa.setGridColour("#eeffee");
		xa.setColour("#009900");
		cm.setXAxis(xa);


		YAxis ya = new YAxis();
		ya.setRange(0, 100);
		ya.setSteps(10);
		ya.setGridColour("#eeffee");
		ya.setColour("#009900");
		ya.setOffset(true);  
		cm.setYAxisLabelStyle(10, "#009900");
		cm.setYAxis(ya);


		BarChart bchart = new BarChart();


		for (DeployStatus deployStatus : singleMachines) {
			//bchart.addValues(Random.nextInt(5000) + 10000);
			switch (deployStatus) {
			case FAIL:
				Bar fail = new Bar(30, "#D91717");
				fail.setTooltip("failure");
				bchart.addBars(fail);
				break;
			case FINISH:
				Bar finish = new Bar(100, "#63AC1C");
				finish.setTooltip("completed");
				bchart.addBars(finish);
				break;
			case PENDING:
				Bar pending = new Bar(10, "#FE8537");
				pending.setTooltip("pending");
				bchart.addBars(pending);
				break;
			case RUN:
				Bar running = new Bar(50, "#63AC1C");
				running.setTooltip("running");
				bchart.addBars(running);
		;
				break;
			case SKIP:
				Bar skip = new Bar(10, "#CCCCCC");
				skip.setTooltip("skip");
				bchart.addBars(skip);

				break;
			case WAIT:
				Bar waiting = new Bar(25, "#FE8537");
				waiting.setTooltip("waiting");
				bchart.addBars(waiting);
				//bchart.setTooltip("waiting");
				break;
			default:
				break;
			}
			//bchart.setTooltip("#val#%");
		}

		cm.addChartConfig(bchart);
		return cm;
	}


	/**
	 * 
	 * @param n
	 * @return
	 */
	public static String[] getAscisseValues(List<DeployStatus> singleMachines) {
		int n = singleMachines.size();
		String[] machines = new String[n];
		for (int c = 0; c < n; c++)
			machines[c] = "node" + (c+1);
		return machines;
	}


}
