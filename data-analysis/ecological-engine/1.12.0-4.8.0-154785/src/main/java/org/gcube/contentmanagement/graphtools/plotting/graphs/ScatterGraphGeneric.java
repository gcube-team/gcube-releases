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

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.List;

import org.gcube.contentmanagement.graphtools.abstracts.GenericStandaloneGraph;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphData;
import org.gcube.portlets.user.timeseries.charts.support.types.Point;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ScatterGraphGeneric extends GenericStandaloneGraph {

	private static final long serialVersionUID = 1L;

	public ScatterGraphGeneric(String title) {
		super(title);
	}

	protected Dataset generateDataset() {
        return null;
	}

	protected JFreeChart createChart(Dataset dataset) {

		 // create the chart...
       JFreeChart chart = ChartFactory.createLineChart(
           "",       // chart title
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
       /*
       plot.setDomainGridlineStroke(new BasicStroke(0.0F));
       plot.setDomainGridlinePaint(Color.blue);
       plot.setRangeGridlineStroke(new BasicStroke(0.0F));
       plot.setRangeMinorGridlineStroke(new BasicStroke(0.0F));
       plot.setRangeGridlinePaint(Color.blue);
       plot.setRangeMinorGridlinesVisible(true);
       plot.setNoDataMessage("NO DATA");
       plot.setRangePannable(true);
       plot.setRangeZeroBaselineVisible(true);
       */
       plot.setBackgroundPaint(Color.white);
       plot.setRangeGridlinePaint(Color.white);
       plot.setRangeGridlinePaint(Color.white);
       plot.setDomainCrosshairVisible(true);
       plot.setDomainGridlinesVisible(false);
       plot.setRangeCrosshairVisible(true);
       plot.setRenderer(new LineAndShapeRenderer(false,true));
       
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

	public static JFreeChart createStaticChart(Dataset dataset) {

		 // create the chart...
      JFreeChart chart = ChartFactory.createLineChart(
          "",       // chart title
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
     
      plot.setBackgroundPaint(Color.white);
      plot.setRangeGridlinePaint(Color.white);
      plot.setRangeGridlinePaint(Color.white);
      plot.setDomainCrosshairVisible(true);
      plot.setDomainGridlinesVisible(false);
      plot.setRangeCrosshairVisible(true);
      plot.setRenderer(new LineAndShapeRenderer(false,true));
      
		return chart;
	}
	
	@Override
	protected GenericStandaloneGraph getInstance(String title) {
		return new ScatterGraphGeneric(title);
	}

}
