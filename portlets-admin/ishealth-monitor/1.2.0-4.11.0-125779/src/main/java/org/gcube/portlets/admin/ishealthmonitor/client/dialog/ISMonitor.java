/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: ISMonitor.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.ishealthmonitor.client.dialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.adapters.highcharts.codegen.sections.options.OptionPath;
import org.adapters.highcharts.codegen.types.SeriesType;
import org.adapters.highcharts.codegen.types.SeriesType.SeriesDataEntry;
import org.adapters.highcharts.codegen.utils.ClientConsole;
import org.adapters.highcharts.gxt.widgets.HighChart;
import org.gcube.portlets.admin.ishealthmonitor.client.async.ISMonitorService;
import org.gcube.portlets.admin.ishealthmonitor.client.async.ISMonitorServiceAsync;
import org.gcube.resourcemanagement.support.client.utils.LocalStatus;
import org.gcube.resourcemanagement.support.shared.util.PerformanceMonitor;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.axis.Label;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.charts.CylinderBarChart;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;



/**
 * Widgets used to monitor the health of IS.
 * @author Daniele Strollo (ISTI-CNR)
 */
public class ISMonitor extends Dialog {

	private final ISMonitorServiceAsync monitorService = GWT.create(ISMonitorService.class);

	private ArrayList<PingResult> results = new ArrayList<PingResult>();
	private HighChart chart;
	private int shiftGap = 10;
	List<Map<String, Object>> series = new Vector<Map<String,Object>>();
	private ArrayList<String> scopes = new ArrayList<String>();
	
	private int pingCounter = 0;

	protected final void closeDialog() {
		this.hide();
	}

	private void initScopes() {
		this.scopes.clear();
		ArrayList<String> theScopes = LocalStatus.getInstance().getAvailableScopes();
//		ArrayList<String> theScopes = new ArrayList<String>();
//		theScopes.add("/gcube");
//		theScopes.add("/gcube/devsec");
//		theScopes.add("/gcube/devNext");

		for (final String scope : theScopes) {
			if (!ISMonitor.isVRE(scope)) {
				this.scopes.add(scope);
			}
		}
	}
	
	public ISMonitor() {
		this.initScopes();
		this.setWidth(600);
		this.setHeight(500);
		this.setLayout(new FitLayout());
		
		
		this.getButtonBar().removeAll();
		this.getButtonBar().add(new FillToolItem());
		this.getButtonBar().add(new Button("Check again") {
			@Override
			protected void onClick(final ComponentEvent ce) {
				super.onClick(ce);
				refresh();
			}
		});
		this.getButtonBar().add(new Button("Close") {
			@Override
			protected void onClick(final ComponentEvent ce) {
				super.onClick(ce);
				closeDialog();
			}
		});
		this.setHideOnButtonClick(true);
		chart = new HighChart(null, "");
		this.add(chart);
		initUI();
	}

	
	private void initUI () {
		try {
			this.chart.setOption(new OptionPath("/credits/enabled"), false);

			this.chart.setOption(new OptionPath("/title/text"), "Calculating IS Health, please wait ..");
		
			this.chart.setOption(new OptionPath("/legend/enabled"), true);

			this.chart.setOption(new OptionPath("/xAxis/title/text"), "Infrastructure Scopes");
			this.chart.setOption(new OptionPath("/xAxis/labels/enabled"), false);		
			this.chart.setOption(new OptionPath("/plotOptions/series/marker/enabled"), false);

			this.chart.setOption(new OptionPath("/yAxis/minPadding"), 0.2);
			this.chart.setOption(new OptionPath("/yAxis/maxPadding"), 0.2);
			this.chart.setOption(new OptionPath("/yAxis/title/text"), "Delay in seconds");
			this.chart.setOption(new OptionPath("/yAxis/title/margin"), 40);
			
			this.chart.setOption(new OptionPath("/yAxis/title/style/visibility"), "visible");		
			this.chart.setOption(new OptionPath("/chart/zoomType"), "x");	
			this.chart.setOption(new OptionPath("/exporting/enabled"), false);

			for (final String scope : scopes) {
				SeriesType series = new SeriesType(scope);
				series.setType("column");
				this.chart.addSeries(series);
			}
			
			
		} catch (Exception e) {
		}
	}

	private native void setChartTitle(final String chartId, final String newTitle) /*-{
		var chart = $wnd.getChartById(chartId);
		
		chart.setTitle({ text: ''+newTitle});
	}-*/;
	
	/*
	 * Used internally on render phase.
	 */
	private native synchronized void insertPoint(
			final String chartId, final double x, final double y, final double shiftGap, final int scopeIndex) /*-{
		var chart = $wnd.getChartById(chartId);
		if (chart == null) {
		alert('cannot retrieve chart ' + chartId);
	  		return;
		}
		var series = chart.series[scopeIndex];
	    shift = series.data.length > shiftGap;
	
	   chart.series[scopeIndex].addPoint([x, y], true, false);	
	   chart.xAxis[0].setExtremes(-40, 125);   
	}-*/;

	private String formatScope(final String scope) {
		StringBuilder retval = new StringBuilder();
		String[] parts = scope.split("/");
		for (String part : parts) {
			if (part.contains(".")) {
				retval.append("/" + part.substring(0, part.indexOf(".")));
			} else {
				retval.append("/" + part);
			}
		}
		return retval.toString();
	}

	// The VRE will be excluded form the chart
	public static boolean isVRE(final String scope) {
		if (scope != null && scope.trim().length() > 0) {
			return scope.split("/").length > 3;
		}
		return false;
	}

	public final void refresh() {
		setChartTitle(this.chart.getChartJSId(), "Calculating IS Health please wait");
		final PerformanceMonitor monitor = PerformanceMonitor.getClock(ISMonitor.class);
		for (final String scope : this.scopes) {
			monitor.start();
			monitorService.getResourceTypeTree(scope, new AsyncCallback<HashMap<String, ArrayList<String>>>() {
				public void onSuccess(final HashMap<String, ArrayList<String>> result) {
					float score = monitor.stop(true);
					addValue(new PingResult(scope, score));
				}
				public void onFailure(final Throwable caught) {
					GWT.log("FAILURE");
				}
			});
		}
	}

	public static void pingIS() {
		final ISMonitor pingDialog = new ISMonitor();
		pingDialog.show();
		pingDialog.refresh();
	}
	
	private void addValue(PingResult result) {
		final String chartId = this.chart.getChartJSId();
		int scopeIndex = scopes.indexOf(result.getScope());
		insertPoint(chartId, scopeIndex*10+10, result.getResult(), shiftGap, scopeIndex);
		pingCounter++;
		if (pingCounter % scopes.size() == 0) {
			setChartTitle(this.chart.getChartJSId(), "IS Health Report Complete");
		}
	}
}



@SuppressWarnings("serial")
class PingResult implements Serializable {
	private String scope = null;
	private float result = 100;
	public PingResult() {
	}

	public PingResult(final String scope, final float result) {
		this.scope = scope;
		this.result = result;
	}

	public float getResult() {
		return this.result;
	}

	public String getScope() {
		return this.scope;
	}
}
