package org.gcube.portlets.admin.vredeployer.client.charts;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientFunctionalityDeployReport;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientFunctionalityReport;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientServiceReport;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.DeployStatus;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.YAxis;
import com.extjs.gxt.charts.client.model.charts.BarChart;
import com.extjs.gxt.charts.client.model.charts.BarChart.Bar;
import com.google.gwt.core.client.GWT;


public class FunctionalityChart {

	private Chart chart;

	public FunctionalityChart() {
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
	public ChartModel getChartModel(ClientFunctionalityDeployReport report) {
		ChartModel cm = new ChartModel("current status: " + report.getStatus(),	"font-size: 14px; font-family: Verdana; text-align: center;");  
		cm.setBackgroundColour("#FFFFFF");
		cm.setDecimalSeparatorComma(true);

		HashMap<ClientFunctionalityReport, List<ClientServiceReport>> funTable;
		if (report.getFunTable() != null) 		
			funTable = report.getFunTable();
		else
			funTable = new HashMap<ClientFunctionalityReport, List<ClientServiceReport>>();
		
		int n = report.getFunTable().size();

		XAxis xa = new XAxis();
		xa.setLabels(getAscisseValues(funTable));
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


		for (ClientFunctionalityReport func : funTable.keySet()) {	
			DeployStatus deployStatus = func.getStatus();
			//bchart.addValues(Random.nextInt(5000) + 10000);
			switch (deployStatus) {
			case FAIL:
				Bar fail = new Bar(30, "#D91717");
				fail.setTooltip("failure");
				bchart.addBars(fail);
				break;
			case FINISH:
				Bar finish = new Bar(100, "#63AC1C");
				finish.setTooltip("completed " + func.getName());
				bchart.addBars(finish);
				break;
			case PENDING:
				Bar pending = new Bar(10, "#FE8537");
				pending.setTooltip("pending");
				bchart.addBars(pending);
				break;
			case RUN:
				Bar running = new Bar(50, "#63AC1C");
				running.setTooltip("running " + func.getName());
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
				waiting.setTooltip("waiting " + func.getName());
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
	public static List<String> getAscisseValues(HashMap<ClientFunctionalityReport, List<ClientServiceReport>> funTable) {
		funTable.keySet();
		List<String> func = new LinkedList<String>();
		int i = 0;
		for (ClientFunctionalityReport fun : funTable.keySet()) {
			func.add("f"+i);
			i++;
		}
		
		return func;
	}

}
	
	
//
//		HashMap<ClientFunctionalityReport, List<ClientServiceReport>> funTable;
//		if (report.getFunTable() != null) 		
//			funTable = report.getFunTable();
//		else
//			funTable = new HashMap<ClientFunctionalityReport, List<ClientServiceReport>>();
//
//		int n = report.getFunTable().size();
//		GWT.log("SIZE TABLE" + n); 
//
//		ChartModel cm = new ChartModel("current status: " + report.getStatus(),	"font-size: 14px; font-family: Verdana; text-align: center;");  
//		cm.setBackgroundColour("#FFFFFF");
//
//		XAxis xa = new XAxis();  
//		xa.setRange(0, getMaxServicesNumber(funTable)*10 + 30); 
//		xa.getLabels().setColour("#009900");
//		xa.addLabels("");
//		xa.setColour("#009900");
//		xa.setSteps(10);
//		
//	//	xa.setRange(0, 100);  
//		cm.setXAxis(xa);  
//		xa.setGridColour("#eeffee");
//
//		YAxis ya = new YAxis();  
//		ya.setColour("#009900");
//		//ya.addLabels(getAscisseValues(funTable));  
//		ya.setGridColour("#eeffee");
//		ya.setOffset(true);  
//		cm.setYAxis(ya);  
//
//
//		HorizontalBarChart bchart = new HorizontalBarChart();  
//		/**
//		 * 
//		 */
//		for (MyBarData data: getBars(funTable)) {
//			bchart.addBars(new HorizontalBarChart.Bar(data.value, data.color));  
//		}
//		cm.addChartConfig(bchart);
//		return cm;
//	}
//
//	/**
//	 * 
//	 * @param table
//	 * @return
//	 */
//	private int getMaxServicesNumber(HashMap<ClientFunctionalityReport, List<ClientServiceReport>> table) {
//		//calculate the max of services for each functionality
//		int max = 0;
//		for (ClientFunctionalityReport func : table.keySet()) {			
//			 int toCompare = table.get(func).size();
//			 if (toCompare > max)
//				 max = toCompare;
//		}
//		return max;
//	}
//	
//	/**
//	 * 
//	 * @return
//	 */
//	private List<MyBarData> getBars(HashMap<ClientFunctionalityReport, List<ClientServiceReport>> table) {
//
//		List<MyBarData> toReturn = new LinkedList<MyBarData>();
//
//		//a bar for each functionality
//		for (ClientFunctionalityReport func : table.keySet()) {			
//			toReturn.add(new MyBarData( table.get(func).size()*10, getColor(func.getStatus()) ) );
//		}
//		return toReturn;
//	}
//
//	/**
//	 * 
//	 * @author massi
//	 *
//	 */
//	private class MyBarData {
//		public double value;
//		public String color;
//		/**
//		 * 
//		 * @param value
//		 * @param color
//		 */
//		public MyBarData(double value, String color) {
//			super();
//			this.value = value;
//			this.color = color;
//		}
//		
//	}
//	/**
//	 * 
//	 * @param status
//	 * @return
//	 */
//	private String getColor(DeployStatus status) {
//		switch (status) {
//		case FAIL:
//			return "#D91717";
//		case FINISH:
//			return "#63AC1C";
//		case PENDING:
//			return "#FE8537";
//		case RUN:
//			return "#63AC1C";
//		case SKIP:
//			return "#CCCCCC";
//		case WAIT:
//			return "#FE8537";
//		default:
//			return "#000000";
//		}
//	}
//
//	/**
//	 * 
//	 * @param n
//	 * @return
//	 */
//	public static List<String> getAscisseValues(HashMap<ClientFunctionalityReport, List<ClientServiceReport>> funTable) {
//		funTable.keySet();
//		List<String> machines = new LinkedList<String>();
//		int i = 0;
//		for (ClientFunctionalityReport fun : funTable.keySet()) {
//			machines.add(fun.getName());
//			i++;
//		}
//		GWT.log("size func" + machines.size());
//		return machines;
//	}

