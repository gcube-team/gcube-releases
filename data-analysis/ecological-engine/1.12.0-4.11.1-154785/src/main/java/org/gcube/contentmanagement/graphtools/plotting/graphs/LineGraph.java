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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;

public class LineGraph extends GenericStandaloneGraph {

	private static final long serialVersionUID = 1L;

	public LineGraph(String title) {
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

		 // create the chart...
        JFreeChart chart = ChartFactory.createLineChart(
            this.getTitle(),       // chart title
            "",                    // domain axis label
            "",                   // range axis label
            (DefaultCategoryDataset)dataset,                   // data
            PlotOrientation.VERTICAL,  // orientation
            true,                      // include legend
            true,                      // tooltips
            false                      // urls
        );
        chart.setBackgroundPaint(Color.white);
        CategoryPlot plot = chart.getCategoryPlot();
//       plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setDomainCrosshairVisible(true);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.setRenderer(new LineAndShapeRenderer(true,true));
        
        //deprecated
        /*
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setShapesVisible(true);
        renderer.setDrawOutlines(true);
        renderer.setUseFillPaint(true);
        renderer.setFillPaint(Color.white);
*/
        
        

        
		return chart;
	}

	public static JFreeChart createStaticChart(Dataset dataset, String title) {

		 // create the chart...
       JFreeChart chart = ChartFactory.createLineChart(
           title,       // chart title
           "",                    // domain axis label
           "",                   // range axis label
           (DefaultCategoryDataset)dataset,                   // data
           PlotOrientation.VERTICAL,  // orientation
           true,                      // include legend
           true,                      // tooltips
           false                      // urls
       );
       chart.setBackgroundPaint(Color.white);
       CategoryPlot plot = chart.getCategoryPlot();
//      plot.setBackgroundPaint(Color.white);
       plot.setRangeGridlinePaint(Color.white);
       plot.setDomainCrosshairVisible(true);
       plot.setDomainGridlinesVisible(true);
       plot.setRangeCrosshairVisible(true);
       plot.setRenderer(new LineAndShapeRenderer(true,true));
       
       //deprecated
       /*
       LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
       renderer.setShapesVisible(true);
       renderer.setDrawOutlines(true);
       renderer.setUseFillPaint(true);
       renderer.setFillPaint(Color.white);
*/
       
       

       
		return chart;
	}
	
	@Override
	protected GenericStandaloneGraph getInstance(String title) {
		return new LineGraph(title);
	}

}
