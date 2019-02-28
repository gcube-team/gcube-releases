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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.contentmanagement.graphtools.abstracts.GenericStandaloneGraph;
import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.CategoryTick;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.text.TextBlock;
import org.jfree.text.TextBlockAnchor;
import org.jfree.text.TextLine;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

public class TransectLineGraph extends GenericStandaloneGraph {

	private static final long serialVersionUID = 1L;

	public TransectLineGraph(String title) {
		super(title);
	}

	protected Dataset generateDataset() {
		// row keys...
		String series1 = "First";
		String series2 = "Second";
		String series3 = "Third";

		// column keys...
		String type1 = "Type 1";
		String type2 = "Type 2";
		String type3 = "Type 3";
		String type4 = "Type 4";
		String type5 = "Type 5";
		String type6 = "Type 6";
		String type7 = "Type 7";
		String type8 = "Type 8";

		// create the dataset...
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		dataset.addValue(1.0, series1, type1);
		dataset.addValue(4.0, series1, type2);
		dataset.addValue(3.0, series1, type3);
		dataset.addValue(5.0, series1, type4);
		dataset.addValue(5.0, series1, type5);
		dataset.addValue(7.0, series1, type6);
		dataset.addValue(7.0, series1, type7);
		dataset.addValue(8.0, series1, type8);

		dataset.addValue(5.0, series2, type1);
		dataset.addValue(7.0, series2, type2);
		dataset.addValue(6.0, series2, type3);
		dataset.addValue(8.0, series2, type4);
		dataset.addValue(4.0, series2, type5);
		dataset.addValue(4.0, series2, type6);
		dataset.addValue(2.0, series2, type7);
		dataset.addValue(1.0, series2, type8);

		dataset.addValue(4.0, series3, type1);
		dataset.addValue(3.0, series3, type2);
		dataset.addValue(2.0, series3, type3);
		dataset.addValue(3.0, series3, type4);
		dataset.addValue(6.0, series3, type5);
		dataset.addValue(3.0, series3, type6);
		dataset.addValue(4.0, series3, type7);
		dataset.addValue(3.0, series3, type8);
		return dataset;
	}

	protected JFreeChart createChart(Dataset dataset) {

		DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();
		DefaultCategoryDataset dataset2 = new DefaultCategoryDataset();
		DefaultCategoryDataset dataset0 = (DefaultCategoryDataset) dataset;
		ArrayList<Integer> relevantindexes = new ArrayList<Integer>();

		for (Object row : dataset0.getRowKeys()) {
			int spikescounter = 0;
			int pointscounter = 0;
			int allcounter = 0;
			int mincolumns = 0;
			int maxcolumns = dataset0.getColumnCount() - 1;
			int medcolumns = (maxcolumns) / 2;
			for (Object column : dataset0.getColumnKeys()) {
				// System.out.println("row "+row+" column "+column );
				double value = dataset0.getValue((String) row, (String) column).doubleValue();
				String xlab = (String) column;
				String annotation = "";

				String x1lab = xlab;
				int commaindex = xlab.indexOf(";");
				if (commaindex > 0) {
					annotation = xlab.substring(commaindex + 1);
					x1lab = xlab.substring(0, commaindex);
					dataset2.addValue(value, (String) row, "" + (allcounter + 1) + ": " + annotation);
					spikescounter++;
					relevantindexes.add(allcounter);
				}

				else {
					if ((allcounter == mincolumns) || (allcounter == maxcolumns) || (allcounter == medcolumns))
						relevantindexes.add(allcounter);

					dataset2.addValue(value, (String) row, "" + (allcounter + 1) + "");
					pointscounter++;
				}
				allcounter++;
				dataset1.addValue(value, (String) row, x1lab);
			}
		}

		// create the chart...

		JFreeChart chart = ChartFactory.createLineChart(" ", // chart title
				"", // domain axis label
				"", // range axis label
				(DefaultCategoryDataset) dataset1, // data
				PlotOrientation.VERTICAL, // orientation
				false, // include legend
				true, // tooltips
				false // urls
				);

		chart.setTitle(new TextTitle(" ", new Font("sansserif", Font.BOLD, 60)));

		chart.setBackgroundPaint(Color.white);

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		plot.setDomainCrosshairVisible(true);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeCrosshairVisible(true);
		// plot.setRenderer(new LineAndShapeRenderer(true,true));
		plot.setRenderer(new LineAndShapeRenderer(true, false));
		plot.setAxisOffset(new RectangleInsets(1D, 1D, 1D, 1D));

		plot.setDomainAxis(0, new CustomXAxis("", dataset1, relevantindexes));
		CategoryAxis categoryaxis1 = plot.getDomainAxis(0);
		categoryaxis1.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		plot.mapDatasetToDomainAxis(0, 0);

		plot.setDataset(1, (DefaultCategoryDataset) dataset2);
		plot.setDomainAxis(1, new CustomXAxis("", dataset2, relevantindexes));
		CategoryAxis categoryaxis2 = plot.getDomainAxis(1);
		categoryaxis2.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		plot.mapDatasetToDomainAxis(1, 1);
		plot.setDomainAxisLocation(1, AxisLocation.TOP_OR_LEFT);

		// categoryaxis2.setLabelInsets(new RectangleInsets(100, 100, 100, 100));

		// categoryaxis2.setLowerMargin(0.05D);
		// categoryaxis2.setUpperMargin(1D);

		// plot.mapDatasetToRangeAxis(1, 1);
		// deprecated
		/*
		 * LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer(); renderer.setShapesVisible(true); renderer.setDrawOutlines(true); renderer.setUseFillPaint(true); renderer.setFillPaint(Color.white);
		 */

		// rangeAxis.setStandardTickUnits(ValueAxis);
		// rangeAxis.setAutoRangeIncludesZero(false);
		// rangeAxis.setUpperMargin(0.12);

		chart.setPadding(new RectangleInsets(30, 30, 90, 90));

		big = true;
		chart.getPlot().setBackgroundPaint(Color.white);

		return chart;
	}

	public static JFreeChart createStaticChart(Dataset dataset) {

		DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();
		DefaultCategoryDataset dataset2 = new DefaultCategoryDataset();
		DefaultCategoryDataset dataset0 = (DefaultCategoryDataset) dataset;
		ArrayList<Integer> relevantindexes = new ArrayList<Integer>();

		for (Object row : dataset0.getRowKeys()) {
			int spikescounter = 0;
			int pointscounter = 0;
			int allcounter = 0;
			int mincolumns = 0;
			int maxcolumns = dataset0.getColumnCount() - 1;
			int medcolumns = (maxcolumns) / 2;
			for (Object column : dataset0.getColumnKeys()) {
				// System.out.println("row "+row+" column "+column );
				double value = dataset0.getValue((String) row, (String) column).doubleValue();
				String xlab = (String) column;
				String annotation = "";

				String x1lab = xlab;
				int commaindex = xlab.indexOf(";");
				if (commaindex > 0) {
					annotation = xlab.substring(commaindex + 1);
					x1lab = xlab.substring(0, commaindex);
					dataset2.addValue(value, (String) row, "" + (allcounter + 1) + ": " + annotation);
					spikescounter++;
					relevantindexes.add(allcounter);
				}

				else {
					if ((allcounter == mincolumns) || (allcounter == maxcolumns) || (allcounter == medcolumns))
						relevantindexes.add(allcounter);

					dataset2.addValue(value, (String) row, "" + (allcounter + 1) + "");
					pointscounter++;
				}
				allcounter++;
				dataset1.addValue(value, (String) row, x1lab);
			}
		}

		// create the chart...

		JFreeChart chart = ChartFactory.createLineChart(" ", // chart title
				"", // domain axis label
				"", // range axis label
				(DefaultCategoryDataset) dataset1, // data
				PlotOrientation.VERTICAL, // orientation
				false, // include legend
				true, // tooltips
				false // urls
				);

		chart.setTitle(new TextTitle(" ", new Font("sansserif", Font.BOLD, 60)));

		chart.setBackgroundPaint(Color.white);

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		plot.setDomainCrosshairVisible(true);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeCrosshairVisible(true);
		plot.setRenderer(new LineAndShapeRenderer(true, false));
		plot.setAxisOffset(new RectangleInsets(1D, 1D, 1D, 1D));

		plot.setDomainAxis(0, new CustomXAxis("", dataset1, relevantindexes));
		CategoryAxis categoryaxis1 = plot.getDomainAxis(0);
		categoryaxis1.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		plot.mapDatasetToDomainAxis(0, 0);

		plot.setDataset(1, (DefaultCategoryDataset) dataset2);
		plot.setDomainAxis(1, new CustomXAxis("", dataset2, relevantindexes));
		CategoryAxis categoryaxis2 = plot.getDomainAxis(1);
		categoryaxis2.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		plot.mapDatasetToDomainAxis(1, 1);
		plot.setDomainAxisLocation(1, AxisLocation.TOP_OR_LEFT);

		chart.setPadding(new RectangleInsets(30, 30, 90, 90));

		chart.getPlot().setBackgroundPaint(Color.white);

		return chart;
	}

	@Override
	protected GenericStandaloneGraph getInstance(String title) {

		return new TransectLineGraph(title);
	}

	
	public static Date calcAnnotationTime(String annotation, String timepattern){
		String time = annotation.substring(0,annotation.indexOf(";"));
		Date timeD = null;
		try {
			timeD = new SimpleDateFormat(timepattern).parse(time);
		} catch (ParseException e) {
		}
		return timeD;
	}
	public static DefaultCategoryDataset orderByTime(DefaultCategoryDataset annotatedTimeChart, String timepattern){
		
		DefaultCategoryDataset orderedChart = new DefaultCategoryDataset();
		if (annotatedTimeChart==null)
			return orderedChart;
		
		List<Double> values = new ArrayList<Double>();
		List<String> annotations= new ArrayList<String>();
		List<Date> dates = new ArrayList<Date>();
		
		int ncols = annotatedTimeChart.getColumnCount();
		//suppose there is only one time series here
		String rowkey = annotatedTimeChart.getRowKeys().get(0).toString();
		
		for (int i=0;i<ncols ;i++){
			String annotation = annotatedTimeChart.getColumnKey(i).toString();
			double value = (Double)annotatedTimeChart.getValue(rowkey, annotation);
			Date timeD = calcAnnotationTime(annotation, timepattern);
			if (timeD!=null){
				int ncolsOrdered =  dates.size();
				int bestidx =ncolsOrdered;
				for (int j=0;j<ncolsOrdered;j++){
					Date timeo = dates.get(j);
					if (timeo.after(timeD)){
						bestidx=j;
						break;
					}
				}
				values.add(bestidx, value);
				annotations.add(bestidx, annotation);
				dates.add(bestidx, timeD);
			}
		}
		int nvals = values.size(); 
		for (int i=0;i<nvals;i++){
			orderedChart.addValue(values.get(i), rowkey, annotations.get(i));
			
		}
		
		return orderedChart;
	}
	
	static class CustomXAxis extends CategoryAxis {

		DefaultCategoryDataset dataset;
		List<Integer> samplingindexes;

		public java.util.List refreshTicks(Graphics2D graphics2d, AxisState axisstate, Rectangle2D rectangle2d, RectangleEdge rectangleedge) {
			ArrayList arraylist = new ArrayList();
			int size = dataset.getColumnCount();

			for (int i = 0; i < size; i++) {
				TextBlock tb = new TextBlock();

				if (MathFunctions.isIn(samplingindexes, i)) {
					String xlab = (String) dataset.getColumnKeys().get(i);
					// xlab = xlab.substring(xlab.indexOf(":")+1);

					tb.addLine(new TextLine(xlab, new Font("sansserif", Font.BOLD, 8)));
				} else {
					tb.addLine(new TextLine(""));
				}
				arraylist.add(new CategoryTick("p" + i, tb, TextBlockAnchor.CENTER_RIGHT, TextAnchor.CENTER_RIGHT, 0));

			}

			return arraylist;

		}

		public CustomXAxis(String s, DefaultCategoryDataset d, List<Integer> indexes) {
			super(s);
			dataset = d;
			samplingindexes = indexes;
		}
	}
}
