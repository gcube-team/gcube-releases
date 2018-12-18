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
import java.util.List;

import org.gcube.contentmanagement.graphtools.abstracts.GenericStandaloneGraph;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphData;
import org.gcube.portlets.user.timeseries.charts.support.types.Point;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

public class NumericSeriesGraph extends GenericStandaloneGraph {

	private static final long serialVersionUID = 1L;

	public NumericSeriesGraph(String title) {
		super(title);
	}

	protected Dataset generateDataset() {
		XYSeries xyseries = new XYSeries("Series 1");
		xyseries.add(2D, 56.270000000000003D);
		xyseries.add(3D, 41.32D);
		xyseries.add(4D, 31.449999999999999D);
		xyseries.add(5D, 30.050000000000001D);
		xyseries.add(6D, 24.690000000000001D);
		xyseries.add(7D, 19.780000000000001D);
		xyseries.add(8D, 20.940000000000001D);
		xyseries.add(9D, 16.73D);
		xyseries.add(10D, 14.210000000000001D);
		xyseries.add(11D, 12.44D);
		XYSeriesCollection xyseriescollection = new XYSeriesCollection(xyseries);
		XYSeries xyseries1 = new XYSeries("Series 2");
		xyseries1.add(11D, 56.270000000000003D);
		xyseries1.add(10D, 41.32D);
		xyseries1.add(9D, 31.449999999999999D);
		xyseries1.add(8D, 30.050000000000001D);
		xyseries1.add(7D, 24.690000000000001D);
		xyseries1.add(6D, 19.780000000000001D);
		xyseries1.add(5D, 20.940000000000001D);
		xyseries1.add(4D, 16.73D);
		xyseries1.add(3D, 14.210000000000001D);
		xyseries1.add(2D, 12.44D);
		xyseriescollection.addSeries(xyseries1);

		return xyseriescollection;
	}

	protected JFreeChart createChart(Dataset dataset) {

		NumberAxis numberaxis = new NumberAxis("X");
		numberaxis.setAutoRangeIncludesZero(true);
		NumberAxis numberaxis1 = new NumberAxis("Y");
		numberaxis1.setAutoRangeIncludesZero(true);
		XYSplineRenderer xysplinerenderer = new XYSplineRenderer();
		XYPlot xyplot = new XYPlot((XYDataset) dataset, numberaxis, numberaxis1, xysplinerenderer);
		xyplot.setBackgroundPaint(Color.lightGray);
		xyplot.setDomainGridlinePaint(Color.white);
		xyplot.setRangeGridlinePaint(Color.white);
		xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));
		JFreeChart chart = new JFreeChart("Numeric Series", JFreeChart.DEFAULT_TITLE_FONT, xyplot, true);

		return chart;
	}

	public static JFreeChart createStaticChart(Dataset dataset) {

		NumberAxis numberaxis = new NumberAxis("X");
		numberaxis.setAutoRangeIncludesZero(true);
		NumberAxis numberaxis1 = new NumberAxis("Y");
		numberaxis1.setAutoRangeIncludesZero(true);
		XYSplineRenderer xysplinerenderer = new XYSplineRenderer();
		XYPlot xyplot = new XYPlot((XYDataset) dataset, numberaxis, numberaxis1, xysplinerenderer);
		xyplot.setBackgroundPaint(Color.lightGray);
		xyplot.setDomainGridlinePaint(Color.white);
		xyplot.setRangeGridlinePaint(Color.white);
		xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));
		JFreeChart chart = new JFreeChart("Numeric Series", JFreeChart.DEFAULT_TITLE_FONT, xyplot, true);

		return chart;
	}
	
	@Override
	protected Dataset convert2Dataset(GraphData st) {

		List<Point<? extends Number, ? extends Number>> pointslist = st.getData();

		// NOTE: after the graph generation graphs are inverted in x and y
		int numbOfRows = pointslist.size();
		XYSeriesCollection xyseriescollection = new XYSeriesCollection();

		if (numbOfRows > 0) {
			int numbOfCols = pointslist.get(0).getEntries().size();
			// calclulation will be made only for the first series

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
		return new NumericSeriesGraph(title);
	}

}
