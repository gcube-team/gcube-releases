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

import org.gcube.contentmanagement.graphtools.abstracts.GenericStandaloneGraph;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.ui.RectangleInsets;

public class HistogramGraph extends GenericStandaloneGraph {

	private static final long serialVersionUID = 1L;

	public HistogramGraph(String title) {
		super(title);
	}

	protected Dataset generateDataset() {
		String s = "S1";
		String s1 = "S2";
		String s2 = "S3";
		String s3 = "Category 1";
		String s4 = "Category 2";
		String s5 = "Category 3";
		String s6 = "Category 4";
		String s7 = "Category 5";
		String s8 = "Category 6";
		String s9 = "Category 7";
		String s10 = "Category 8";
		DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
		defaultcategorydataset.addValue(1.0D, s, s3);
		defaultcategorydataset.addValue(4D, s, s4);
		defaultcategorydataset.addValue(3D, s, s5);
		defaultcategorydataset.addValue(5D, s, s6);
		defaultcategorydataset.addValue(5D, s, s7);
		defaultcategorydataset.addValue(7D, s, s8);
		defaultcategorydataset.addValue(7D, s, s9);
		defaultcategorydataset.addValue(8D, s, s10);
		defaultcategorydataset.addValue(5D, s1, s3);
		defaultcategorydataset.addValue(7D, s1, s4);
		defaultcategorydataset.addValue(6D, s1, s5);
		defaultcategorydataset.addValue(8D, s1, s6);
		defaultcategorydataset.addValue(4D, s1, s7);
		defaultcategorydataset.addValue(4D, s1, s8);
		defaultcategorydataset.addValue(2D, s1, s9);
		defaultcategorydataset.addValue(1.0D, s1, s10);
		defaultcategorydataset.addValue(4D, s2, s3);
		defaultcategorydataset.addValue(3D, s2, s4);
		defaultcategorydataset.addValue(2D, s2, s5);
		defaultcategorydataset.addValue(3D, s2, s6);
		defaultcategorydataset.addValue(6D, s2, s7);
		defaultcategorydataset.addValue(3D, s2, s8);
		defaultcategorydataset.addValue(4D, s2, s9);
		defaultcategorydataset.addValue(3D, s2, s10);
		return defaultcategorydataset;

	}

	protected JFreeChart createChart(Dataset dataset) {

		JFreeChart chart = ChartFactory.createBarChart("Histogram Chart", "", "", (DefaultCategoryDataset) dataset, PlotOrientation.VERTICAL, true, true, false);
		chart.setBackgroundPaint(Color.white);
		CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
		categoryplot.setBackgroundPaint(new Color(238, 238, 255));
		categoryplot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);

		CategoryAxis categoryaxis = categoryplot.getDomainAxis();
		categoryaxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
		
		LegendTitle legendtitle = new LegendTitle(categoryplot.getRenderer(0));
		legendtitle.setMargin(new RectangleInsets(2D, 2D, 2D, 2D));
		
		
		return chart;
	}

	public static JFreeChart createStaticChart(Dataset dataset) {

		JFreeChart chart = ChartFactory.createBarChart("Histogram Chart", "", "", (DefaultCategoryDataset) dataset, PlotOrientation.VERTICAL, true, true, false);
		chart.setBackgroundPaint(Color.white);
		CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
		categoryplot.setBackgroundPaint(new Color(238, 238, 255));
		categoryplot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);

		CategoryAxis categoryaxis = categoryplot.getDomainAxis();
		categoryaxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
		
		LegendTitle legendtitle = new LegendTitle(categoryplot.getRenderer(0));
		legendtitle.setMargin(new RectangleInsets(2D, 2D, 2D, 2D));
		
		
		return chart;
	}
	
	@Override
	protected GenericStandaloneGraph getInstance(String title) {
		return new HistogramGraph(title);
	}

}
