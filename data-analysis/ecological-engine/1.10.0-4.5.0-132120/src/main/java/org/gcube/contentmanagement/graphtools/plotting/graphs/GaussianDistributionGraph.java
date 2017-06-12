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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.graphtools.abstracts.GenericStandaloneGraph;
import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphData;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;
import org.gcube.portlets.user.timeseries.charts.support.types.Point;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.function.NormalDistributionFunction2D;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYSeriesCollection;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GaussianDistributionGraph extends GenericStandaloneGraph {

	private static final long serialVersionUID = 1L;

	public GaussianDistributionGraph(String title) {
		super(title);
	}

	protected Dataset generateDataset() {
		 XYSeriesCollection xyseriescollection = new XYSeriesCollection();
	        NormalDistributionFunction2D normaldistributionfunction2d = new NormalDistributionFunction2D(0.0D, 1.0D);
	        org.jfree.data.xy.XYSeries xyseries = DatasetUtilities.sampleFunction2DToSeries(normaldistributionfunction2d, -5.0999999999999996D, 5.0999999999999996D, 121, "N1");
	        xyseriescollection.addSeries(xyseries);
	        NormalDistributionFunction2D normaldistributionfunction2d1 = new NormalDistributionFunction2D(0.0D, Math.sqrt(0.20000000000000001D));
	        org.jfree.data.xy.XYSeries xyseries1 = DatasetUtilities.sampleFunction2DToSeries(normaldistributionfunction2d1, -5.0999999999999996D, 5.0999999999999996D, 121, "N2");
	        xyseriescollection.addSeries(xyseries1);
	        NormalDistributionFunction2D normaldistributionfunction2d2 = new NormalDistributionFunction2D(0.0D, Math.sqrt(5D));
	        org.jfree.data.xy.XYSeries xyseries2 = DatasetUtilities.sampleFunction2DToSeries(normaldistributionfunction2d2, -5.0999999999999996D, 5.0999999999999996D, 121, "N3");
	        xyseriescollection.addSeries(xyseries2);
	        NormalDistributionFunction2D normaldistributionfunction2d3 = new NormalDistributionFunction2D(-2D, Math.sqrt(0.5D));
	        org.jfree.data.xy.XYSeries xyseries3 = DatasetUtilities.sampleFunction2DToSeries(normaldistributionfunction2d3, -5.0999999999999996D, 5.0999999999999996D, 121, "N4");
	        xyseriescollection.addSeries(xyseries3);
	        return xyseriescollection;
	}

	protected JFreeChart createChart(Dataset dataset) {

		String label = "mean:"+mean+" std dev:"+variance;
		if (label.length()>30)
			label = label.substring(0,30)+"...";
		
		JFreeChart jfreechart = ChartFactory.createXYLineChart("", label, "", (XYSeriesCollection)dataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        xyplot.setDomainZeroBaselineVisible(true);
        xyplot.setRangeZeroBaselineVisible(true);
        xyplot.setDomainPannable(true);
        xyplot.setRangePannable(true);
        ValueAxis valueaxis = xyplot.getDomainAxis();
        valueaxis.setLowerMargin(0.0D);
        valueaxis.setUpperMargin(0.0D);
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
        xylineandshaperenderer.setDrawSeriesLineAsPath(true);
        xylineandshaperenderer.setSeriesStroke(0, new BasicStroke(1.5F));
        
        return jfreechart;
	}

	public static JFreeChart createStaticChart(Dataset dataset, double mean, double stddev) {

		String label = "mean:"+mean+" std dev:"+stddev;
		if (label.length()>30)
			label = label.substring(0,30)+"...";
		
		JFreeChart jfreechart = ChartFactory.createXYLineChart("", label, "", (XYSeriesCollection)dataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        xyplot.setDomainZeroBaselineVisible(true);
        xyplot.setRangeZeroBaselineVisible(true);
        xyplot.setDomainPannable(true);
        xyplot.setRangePannable(true);
        ValueAxis valueaxis = xyplot.getDomainAxis();
        valueaxis.setLowerMargin(0.0D);
        valueaxis.setUpperMargin(0.0D);
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
        xylineandshaperenderer.setDrawSeriesLineAsPath(true);
        xylineandshaperenderer.setSeriesStroke(0, new BasicStroke(1.5F));
        
        return jfreechart;
	}
	
	@Override
	protected GenericStandaloneGraph getInstance(String title) {
		return new GaussianDistributionGraph(title);
	}
	
	
	public double mean;
	public double variance;
	@Override
	protected Dataset convert2Dataset(GraphData st) {

		List<Point<? extends Number, ? extends Number>> pointslist = st.getData();
		
		// NOTE: after the graph generation graphs are inverted in x and y
		int numbOfSeries = pointslist.size();
		XYSeriesCollection xyseriescollection = new XYSeriesCollection();

		if (numbOfSeries > 0) {
			int numbOfPoints = pointslist.get(0).getEntries().size();
			//for each series
			for (int s = 0; s < numbOfSeries; s++) {
				//get label
				String serieslabel = pointslist.get(s).getLabel();
				double maxRange = st.getMaxY().doubleValue();
				double minRange = st.getMinY().doubleValue();
				
				//get doubles vector for performing mean and variance calculation 
				double [] points = MathFunctions.points2Double(pointslist,s,numbOfPoints);
				mean = MathFunctions.mean(points);
				variance = com.rapidminer.tools.math.MathFunctions.variance(points, Double.NEGATIVE_INFINITY);
				
				mean = Math.round(mean);
				variance = Math.round(variance);
				
				if (variance==0)
					variance = 0.1;
				
				AnalysisLogger.getLogger().debug("mean "+mean+" variance "+variance);
				//build up normal distribution and add to the series
				NormalDistributionFunction2D normaldistributionfunction2d = new NormalDistributionFunction2D(mean, variance);
				//make the representation a bit longer
				maxRange = maxRange*2;
				org.jfree.data.xy.XYSeries xyseries = DatasetUtilities.sampleFunction2DToSeries(normaldistributionfunction2d, minRange, maxRange, 121, serieslabel);
				
				
				
				xyseriescollection.addSeries(xyseries);
			}
		}
		return xyseriescollection;
	}
	
	public static Map<String,List<NormalDistributionFunction2D>> graphs2Normals(GraphGroups gg){
		
		Map<String,List<NormalDistributionFunction2D>> normalsMap = new HashMap<String, List<NormalDistributionFunction2D>>();
		for (String key:gg.getGraphs().keySet())
		{
			
			GraphData st = gg.getGraphs().get(key);
			List<Point<? extends Number, ? extends Number>> pointslist = st.getData();
			List<NormalDistributionFunction2D> normalsList = new ArrayList<NormalDistributionFunction2D>();
			
			// NOTE: after the graph generation graphs are inverted in x and y
			int numbOfSeries = pointslist.size();
			
			if (numbOfSeries > 0) {
				int numbOfPoints = pointslist.get(0).getEntries().size();
				//for each series
				for (int s = 0; s < numbOfSeries; s++) {
					//get doubles vector for performing mean and variance calculation 
					double [] points = MathFunctions.points2Double(pointslist,s,numbOfPoints);
					double mean = MathFunctions.mean(points);
					double variance = com.rapidminer.tools.math.MathFunctions.variance(points, Double.NEGATIVE_INFINITY);
					
					if (variance==0)
						variance = 0.1;
					
					AnalysisLogger.getLogger().debug("mean "+mean+" variance "+variance);
					//build up normal distribution and add to the series
					NormalDistributionFunction2D normaldistributionfunction2d = new NormalDistributionFunction2D(mean, variance);
					normalsList.add(normaldistributionfunction2d);
				}
			}
			
			normalsMap.put(key, normalsList);
		}
		
		return normalsMap;
	}
	 
	
}
