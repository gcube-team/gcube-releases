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
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ScatterGraphNumeric extends GenericStandaloneGraph {

	private static final long serialVersionUID = 1L;

	public ScatterGraphNumeric(String title) {
		super(title);
	}

	protected Dataset generateDataset() {
        return null;
	}

	protected JFreeChart createChart(Dataset dataset) {

        JFreeChart jfreechart = ChartFactory.createScatterPlot("", "", "", (XYDataset)dataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        xyplot.setNoDataMessage("NO DATA");
        xyplot.setDomainPannable(true);
        xyplot.setRangePannable(true);
        xyplot.setDomainZeroBaselineVisible(true);
        xyplot.setRangeZeroBaselineVisible(true);
        xyplot.setDomainGridlineStroke(new BasicStroke(0.0F));
        xyplot.setDomainMinorGridlineStroke(new BasicStroke(0.0F));
        xyplot.setDomainGridlinePaint(Color.blue);
        xyplot.setRangeGridlineStroke(new BasicStroke(0.0F));
        xyplot.setRangeMinorGridlineStroke(new BasicStroke(0.0F));
        xyplot.setRangeGridlinePaint(Color.blue);
        xyplot.setDomainMinorGridlinesVisible(true);
        xyplot.setRangeMinorGridlinesVisible(true);
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
        xylineandshaperenderer.setSeriesOutlinePaint(0, Color.black);
        xylineandshaperenderer.setUseOutlinePaint(true);
        NumberAxis numberaxis = (NumberAxis)xyplot.getDomainAxis();
        numberaxis.setAutoRangeIncludesZero(false);
        numberaxis.setTickMarkInsideLength(2.0F);
        numberaxis.setTickMarkOutsideLength(2.0F);
        numberaxis.setMinorTickCount(2);
        numberaxis.setMinorTickMarksVisible(true);
        NumberAxis numberaxis1 = (NumberAxis)xyplot.getRangeAxis();
        numberaxis1.setTickMarkInsideLength(2.0F);
        numberaxis1.setTickMarkOutsideLength(2.0F);
        numberaxis1.setMinorTickCount(2);
        numberaxis1.setMinorTickMarksVisible(true);
        return jfreechart;
	}
	
	public static JFreeChart createStaticChart(Dataset dataset) {

        JFreeChart jfreechart = ChartFactory.createScatterPlot("", "", "", (XYDataset)dataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        xyplot.setNoDataMessage("NO DATA");
        xyplot.setDomainPannable(true);
        xyplot.setRangePannable(true);
        xyplot.setDomainZeroBaselineVisible(true);
        xyplot.setRangeZeroBaselineVisible(true);
        xyplot.setDomainGridlineStroke(new BasicStroke(0.0F));
        xyplot.setDomainMinorGridlineStroke(new BasicStroke(0.0F));
        xyplot.setDomainGridlinePaint(Color.blue);
        xyplot.setRangeGridlineStroke(new BasicStroke(0.0F));
        xyplot.setRangeMinorGridlineStroke(new BasicStroke(0.0F));
        xyplot.setRangeGridlinePaint(Color.blue);
        xyplot.setDomainMinorGridlinesVisible(true);
        xyplot.setRangeMinorGridlinesVisible(true);
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
        xylineandshaperenderer.setSeriesOutlinePaint(0, Color.black);
        xylineandshaperenderer.setUseOutlinePaint(true);
        NumberAxis numberaxis = (NumberAxis)xyplot.getDomainAxis();
        numberaxis.setAutoRangeIncludesZero(false);
        numberaxis.setTickMarkInsideLength(2.0F);
        numberaxis.setTickMarkOutsideLength(2.0F);
        numberaxis.setMinorTickCount(2);
        numberaxis.setMinorTickMarksVisible(true);
        NumberAxis numberaxis1 = (NumberAxis)xyplot.getRangeAxis();
        numberaxis1.setTickMarkInsideLength(2.0F);
        numberaxis1.setTickMarkOutsideLength(2.0F);
        numberaxis1.setMinorTickCount(2);
        numberaxis1.setMinorTickMarksVisible(true);
        return jfreechart;
	}
	
	@Override
	protected Dataset convert2Dataset(GraphData st) {

		List<Point<? extends Number, ? extends Number>> pointslist = st.getData();

		// NOTE: after the graph generation graphs are inverted in x and y
		int numbOfRows = pointslist.size();
		XYSeriesCollection xyseriescollection = new XYSeriesCollection();

		if (numbOfRows > 0) {
			int numbOfCols = pointslist.get(0).getEntries().size();
			// calculation will be made only for the first series

			for (int x = 0; x < numbOfRows; x++) {

				String serieslabel = pointslist.get(x).getLabel();
				XYSeries xyseries = new XYSeries(serieslabel);

				for (int y = 0; y < numbOfCols; y++) {
//					String xlabel = pointslist.get(x).getEntries().get(y).getLabel();
					double value = pointslist.get(x).getEntries().get(y).getValue().doubleValue();
					xyseries.add(y + 1, value);
				}

				xyseriescollection.addSeries(xyseries);
			}
		}
		return xyseriescollection;
	}

	@Override
	protected GenericStandaloneGraph getInstance(String title) {
		return new ScatterGraphNumeric(title);
	}

}
