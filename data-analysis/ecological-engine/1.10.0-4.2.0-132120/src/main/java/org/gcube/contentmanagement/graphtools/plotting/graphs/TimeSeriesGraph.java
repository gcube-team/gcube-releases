/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------------
 * PieGraph.java
 * ------------------
 * (C) Copyright 2003-2007, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   ;
 *
 * Changes
 * -------
 * 09-Mar-2005 : Version 1, copied from the demo collection that ships with
 *               the JFreeChart Developer Guide (DG);
 *
 */

package org.gcube.contentmanagement.graphtools.plotting.graphs;

import java.awt.Color;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.gcube.contentmanagement.graphtools.abstracts.GenericStandaloneGraph;
import org.gcube.contentmanagement.graphtools.utils.DateGuesser;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphData;
import org.gcube.portlets.user.timeseries.charts.support.types.Point;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

public class TimeSeriesGraph extends GenericStandaloneGraph {

	private static final long serialVersionUID = 1L;
	private static final String formatYear= "yyyy";
	private static final String formatMonth= "MM-yyyy";
	private static final String formatDay= "MM-dd-yyyy";
	public String timeseriesformat;
	
	
	public TimeSeriesGraph(String title) {
		super(title);
	}

	protected Dataset generateDataset() {
		   	TimeSeriesCollection dataset = new TimeSeriesCollection();
	        
	        final TimeSeries s1 = new TimeSeries("Series 1");
	        s1.add(new Minute(0, 0, 7, 12, 2003), 1.2);
	        s1.add(new Minute(30, 12, 7, 12, 2003), 3.0);
	        s1.add(new Minute(15, 14, 7, 12, 2003), 8.0);
	        
	        final TimeSeries s2 = new TimeSeries("Series 2");
	        s2.add(new Minute(0, 3, 7, 12, 2003), 0.0);
	        s2.add(new Minute(30, 9, 7, 12, 2003), 0.0);
	        s2.add(new Minute(15, 10, 7, 12, 2003), 0.0);
	        
	        dataset.addSeries(s1);
	        dataset.addSeries(s2);

	        return dataset;
	}

	protected JFreeChart createChart(Dataset dataset) {

		
		 JFreeChart chart = ChartFactory.createTimeSeriesChart(
		            "Time Series",  // title
		            "",             // x-axis label
		            "",   // y-axis label
		            (XYDataset)dataset,            // data
		            true,               // create legend?
		            true,               // generate tooltips?
		            false               // generate URLs?
		        );

		        chart.setBackgroundPaint(Color.white);

		        XYPlot plot = (XYPlot) chart.getPlot();
		        plot.setBackgroundPaint(Color.lightGray);
		        plot.setDomainGridlinePaint(Color.white);
		        plot.setRangeGridlinePaint(Color.white);
		        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		        plot.setDomainCrosshairVisible(true);
		        plot.setRangeCrosshairVisible(true);

		        XYItemRenderer r = plot.getRenderer();
		        if (r instanceof XYLineAndShapeRenderer) {
		            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
		            renderer.setBaseShapesVisible(true);
		            renderer.setBaseShapesFilled(true);
		            renderer.setDrawSeriesLineAsPath(true);
		        }

		        DateAxis axis = (DateAxis) plot.getDomainAxis();
		        
	    axis.setDateFormatOverride(new SimpleDateFormat(timeseriesformat));
		
		return chart;
	}

	public static JFreeChart createStaticChart(Dataset dataset, String timeSeriesStringFormat) {
		return createStaticChart(dataset, timeSeriesStringFormat, "Time Series");
	}
	
	public static JFreeChart createStaticChart(Dataset dataset, String timeSeriesStringFormat, String chartName) {

		
		 JFreeChart chart = ChartFactory.createTimeSeriesChart(
		            chartName,  // title
		            "",             // x-axis label
		            "",   // y-axis label
		            (XYDataset)dataset,            // data
		            true,               // create legend?
		            true,               // generate tooltips?
		            false               // generate URLs?
		        );

		        chart.setBackgroundPaint(Color.white);

		        XYPlot plot = (XYPlot) chart.getPlot();
		        plot.setBackgroundPaint(Color.lightGray);
		        plot.setDomainGridlinePaint(Color.white);
		        plot.setRangeGridlinePaint(Color.white);
		        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		        plot.setDomainCrosshairVisible(true);
		        plot.setRangeCrosshairVisible(true);

		        XYItemRenderer r = plot.getRenderer();
		        if (r instanceof XYLineAndShapeRenderer) {
		            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
		            renderer.setBaseShapesVisible(true);
		            renderer.setBaseShapesFilled(true);
		            renderer.setDrawSeriesLineAsPath(true);
		        }

		        DateAxis axis = (DateAxis) plot.getDomainAxis();
		        
	    axis.setDateFormatOverride(new SimpleDateFormat(timeSeriesStringFormat));
		
		return chart;
	}
	
	@Override
	protected Dataset convert2Dataset(GraphData st) {

		List<Point<? extends Number, ? extends Number>> pointslist = st.getData();

		// NOTE: after the graph generation graphs are inverted in x and y
		int numbOfRows = pointslist.size();
		TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();

		if (numbOfRows > 0) {
			int numbOfCols = pointslist.get(0).getEntries().size();
			// calclulation will be made only for the first series

			for (int x = 0; x < numbOfRows; x++) {

				String serieslabel = pointslist.get(x).getLabel();
				TimeSeries xyseries = new TimeSeries(serieslabel);

				for (int y = 0; y < numbOfCols; y++) {
					String xlabel = pointslist.get(x).getEntries().get(y).getLabel();
					double value = pointslist.get(x).getEntries().get(y).getValue().doubleValue();
					Calendar cal = DateGuesser.convertDate(xlabel);
					String granularity = DateGuesser.granularity(xlabel);
					
					if (granularity.equals(DateGuesser.YEAR)) timeseriesformat = formatYear;
					else if (granularity.equals(DateGuesser.MONTH)) timeseriesformat = formatMonth;
					else if (granularity.equals(DateGuesser.DAY)) timeseriesformat = formatDay;
					
					AnalysisLogger.getLogger().debug("TimeSeriesGraph-> granularity "+granularity+" format "+timeseriesformat);
					
					xyseries.add(new Day(new Date(cal.getTimeInMillis())),value);
					
				}

				timeseriescollection.addSeries(xyseries);
			}
		}
		return timeseriescollection;
	}

	@Override
	protected GenericStandaloneGraph getInstance(String title) {
		TimeSeriesGraph tsg  = new TimeSeriesGraph(title);
		tsg.timeseriesformat = timeseriesformat;
		return tsg;
	}

}
