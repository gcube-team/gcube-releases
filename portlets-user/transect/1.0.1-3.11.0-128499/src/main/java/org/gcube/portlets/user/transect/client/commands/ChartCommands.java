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
 * Filename: ChartCommands.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.user.transect.client.commands;

import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.gcube.portlets.user.timeseries.charts.support.types.GraphData;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;
import org.gcube.portlets.user.timeseries.charts.support.types.Point;
import org.gcube.portlets.user.timeseries.charts.support.types.ValueEntry;
import org.gcube.portlets.user.transect.client.remote.ProxyRegistry;
import org.gxt.adapters.highcharts.codegen.sections.options.OptionPath;
import org.gxt.adapters.highcharts.codegen.sections.options.types.ChartType;
import org.gxt.adapters.highcharts.codegen.sections.options.types.RawStringType;
import org.gxt.adapters.highcharts.codegen.sections.options.types.ZoomType;
import org.gxt.adapters.highcharts.codegen.types.SeriesType;
import org.gxt.adapters.highcharts.codegen.utils.ClientConsole;
import org.gxt.adapters.highcharts.widgets.HighChart;
import org.gxt.adapters.highcharts.widgets.ext.ChartFrame;
import org.gxt.adapters.highcharts.widgets.ext.plugins.impl.PlgDecreaseInterval;
import org.gxt.adapters.highcharts.widgets.ext.plugins.impl.PlgIncreaseInterval;
import org.gxt.adapters.highcharts.widgets.ext.plugins.impl.PlgInvertAxis;
import org.gxt.adapters.highcharts.widgets.ext.plugins.impl.PlgRefreshChart;
import org.gxt.adapters.highcharts.widgets.ext.plugins.impl.PlgRotateDataLabels;
import org.gxt.adapters.highcharts.widgets.ext.plugins.impl.PlgSetChartType;
import org.gxt.adapters.highcharts.widgets.ext.plugins.impl.PlgSetZoomType;
import org.gxt.adapters.highcharts.widgets.ext.plugins.impl.PlgSharedTooltip;
import org.gxt.adapters.highcharts.widgets.ext.plugins.impl.PlgShowHideMarker;
import org.gxt.adapters.highcharts.widgets.ext.plugins.impl.PlgStacking;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Class ChartCommands.
 *
 * @author Daniele Strollo (ISTI-CNR)
 * @author updated by Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 */
public class ChartCommands {

	/**
	 * Interally used to initialize the charts.
	 *
	 * @param hc the hc
	 * @param type the type
	 * @return the high chart
	 */
	private static synchronized HighChart initChart(final HighChart hc, final ChartType type) {
		try {
			hc.setOption(new OptionPath("/chart/type"), type);
			hc.setOption(new OptionPath("/chart/zoomType"), ZoomType.ZOOM_X);
			hc.setOption(new OptionPath("/credits/enabled"), false);
			hc.setOption(new OptionPath("/xAxis/allowDecimals"), false);
			hc.setOption(new OptionPath("/yAxis/min"), 0);
			hc.setOption(new OptionPath("/legend/enabled"), false);
			
			hc.setOption(new OptionPath("/plotOptions/" + type.toString() + "/marker/enabled"), false);

			final String OPT_PREFIX = "/plotOptions/series/dataLabels/";
			hc.setOption(new OptionPath(OPT_PREFIX + "enabled"), true);
			hc.setOption(new OptionPath(OPT_PREFIX + "formatter"), new RawStringType("function() { return this.point.name; }"));
			hc.setOption(new OptionPath("/tooltip/formatter"), new RawStringType("function() { return '<b>' + this.x + ':</b> ' + '<b>' + this.y + '</b>'; }"));
		} catch (Exception e) {
			UICommands.log(e);
		}
		return hc;
	}

	/**
	 * Builds the charts.
	 *
	 * @param groups the groups
	 * @return the list
	 */
	private static List<HighChart> buildCharts(final GraphGroups groups) {
		List<HighChart> retval = new Vector<HighChart>();
		// each data will correspond to a different tab item in the chart
		for (Entry<String, GraphData> data : groups.getGraphs().entrySet()) {
			HighChart chart = new HighChart();

			// sets the title
			chart.setTitle(data.getKey());

			// chart.followWindowResize(true);
			// chart.setResizeDelay(2000);
			final List<Point<? extends Number, ? extends Number>> points = data.getValue().getData();

			// Labels on X-axis
			List<String> xLabels = new Vector<String>();
			for (ValueEntry<? extends Number> yPoint : points.get(0).getEntries()) {
				// set label
				String yLabel = yPoint.getLabel();
				xLabels.add(yLabel.contains(Configuration.TRANS_DELIMITER) ? yLabel.substring(0, yLabel.indexOf(Configuration.TRANS_DELIMITER)) : yLabel);
			}
			try {
				chart.setOption(new OptionPath("/xAxis/categories"), xLabels);
			} catch (Exception e) {
				UICommands.log(e);
			}

			// here each point is a curve and its Y values are the points
			for (Point<? extends Number, ? extends Number> xPoint : points) {
				// name of the curve
				SeriesType series = new SeriesType(xPoint.getLabel());
				for (ValueEntry<? extends Number> yPoint : xPoint.getEntries()) {
					String yLabel = yPoint.getLabel();
					// set Y-axis values
					series.addEntry(new SeriesType.SeriesDataEntry(
							yLabel.contains(Configuration.TRANS_DELIMITER) ? yLabel.substring(yLabel.indexOf(Configuration.TRANS_DELIMITER) + 1, yLabel.length()) : null,
									yPoint.getValue()));
				}
				chart.addSeries(series);
			}

			retval.add(chart);
		}
		return retval;
	}

	/**
	 * Creates the high chart.
	 *
	 * @param vp the vp
	 */
	public static final void createHighChart(final Viewport vp) {
		try {
			// TODO here gets parameters
			String x1 = com.google.gwt.user.client.Window.Location.getParameter("x1");
			String y1 = com.google.gwt.user.client.Window.Location.getParameter("y1");
			String x2 = com.google.gwt.user.client.Window.Location.getParameter("x2");
			String y2 = com.google.gwt.user.client.Window.Location.getParameter("y2");
			String SRID = com.google.gwt.user.client.Window.Location.getParameter("SRID");
			String maxelements = com.google.gwt.user.client.Window.Location.getParameter("maxelements");
			String minumumGap = com.google.gwt.user.client.Window.Location.getParameter("minumumGap");
			String biodiversityfield = com.google.gwt.user.client.Window.Location.getParameter("biodiversityfield");
			String tablename = com.google.gwt.user.client.Window.Location.getParameter("tablename");
			String scope = com.google.gwt.user.client.Window.Location.getParameter("scope");
			
//			if(scope==null || scope.isEmpty()){
//				UICommands.showAlert("Error, no scope found!!", "Please set \"scope\" parameter into query string");
//				return;
//			}
			
			UICommands.mask("Loading transect data", vp);
			ProxyRegistry.getProxyInstance().getChartData(scope,
					x1,
					y1,
					x2,
					y2,
					SRID,
					maxelements,
					minumumGap,
					(biodiversityfield != null) ? "\"" + biodiversityfield + "\"" : null,
					(tablename != null) ? "\"" + tablename + "\"" : null,
					new AsyncCallback<GraphGroups>() {

						public void onSuccess(GraphGroups result) {
							if (result == null || result.getGraphs() == null || result.getGraphs().size() == 0) {
								UICommands.unmask(vp);
								UICommands.showAlert("Error during transect builder", "No data received");
							}
							for (HighChart hc : buildCharts(result)) {
								hc = initChart(hc, new ChartType("spline"));
								ChartFrame cframe = new ChartFrame(hc);

								// Customize the toolbar
								try {
									cframe.clearToolbar();
									// Supported types
									cframe.addPlugin("Change Type", new PlgSetChartType(new ChartType("line")));
									cframe.addPlugin("Change Type", new PlgSetChartType(new ChartType("area")));
									cframe.addPlugin("Change Type", new PlgSetChartType(new ChartType("spline")));
									cframe.addPlugin("Change Type", new PlgSetChartType(new ChartType("areaspline")));
									cframe.addPlugin("Change Type", new PlgSetChartType(new ChartType("column")));

									cframe.addPlugin("View", new PlgRefreshChart());
									cframe.addPlugin("View", new PlgShowHideMarker(true));

									cframe.addPlugin("Options", new PlgInvertAxis());
									cframe.addPlugin("Options", new PlgSharedTooltip());
									cframe.addPlugin("Options", new PlgRotateDataLabels(false));
									cframe.addPlugin("Options", new PlgStacking());

									cframe.addPlugin("Intervals", new PlgIncreaseInterval(5));
									cframe.addPlugin("Intervals", new PlgDecreaseInterval(5));

									cframe.addPlugin("Zoom", new PlgSetZoomType(ZoomType.ZOOM_X));
									cframe.addPlugin("Zoom", new PlgSetZoomType(ZoomType.ZOOM_Y));
									cframe.addPlugin("Zoom", new PlgSetZoomType(ZoomType.ZOOM_XY));
									cframe.addPlugin("Zoom", new PlgSetZoomType(ZoomType.ZOOM_NONE));
									UICommands.mask("Loading transect data", vp);
								} catch (Exception e) {
									ClientConsole.err("Building custom toolbar", e);
									UICommands.showAlert("Error during transect builder", e.getMessage());
								}
								
								UICommands.unmask(vp);
								ContentPanel container = new ContentPanel();
								container.setLayout(new FitLayout());
								container.add(cframe);
								vp.add(container);
								vp.layout(true);
							}
						}

						public void onFailure(Throwable caught) {
							UICommands.unmask(vp);
							UICommands.showAlert("Error during transect builder", caught.getMessage());
							UICommands.log("During getGraphForTS", caught);
						}
					});
		} catch (Exception e) {
			UICommands.log("During createHighChart", e);
		}
	}
}
