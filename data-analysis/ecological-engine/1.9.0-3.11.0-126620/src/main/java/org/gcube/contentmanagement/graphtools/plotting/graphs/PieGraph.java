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

import java.util.List;

import org.gcube.contentmanagement.graphtools.abstracts.GenericStandaloneGraph;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphData;
import org.gcube.portlets.user.timeseries.charts.support.types.Point;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.TableOrder;

public class PieGraph extends GenericStandaloneGraph {

	private static final long serialVersionUID = 1L;

	public PieGraph(String title) {
		super(title);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	protected Dataset generateDataset() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("One", Double.valueOf(43.2));
		dataset.setValue("Two", Double.valueOf(10.0));
		dataset.setValue("Three", Double.valueOf(27.5));
		dataset.setValue("Four", Double.valueOf(17.5));
		dataset.setValue("Five", Double.valueOf(11.0));
		dataset.setValue("Six", Double.valueOf(19.4));

		return dataset;
	}
/*
	protected JFreeChart createChart(Dataset dataset) {

		PiePlot plot = new PiePlot((DefaultPieDataset) dataset);
		JFreeChart chart = new JFreeChart(plot);

		return chart;
	}
*/
	protected JFreeChart createChart(Dataset dataset) {
	 JFreeChart chart = ChartFactory.createMultiplePieChart(
	            "Multiple Pie Chart",  // chart title
	            (DefaultCategoryDataset)dataset,               // dataset
	            TableOrder.BY_ROW,
	            true,                  // include legend
	            true,
	            false
	        );
	 return chart;
	}
	
	public static JFreeChart createStaticChart(Dataset dataset) {
		 JFreeChart chart = ChartFactory.createMultiplePieChart(
		            "Multiple Pie Chart",  // chart title
		            (DefaultCategoryDataset)dataset,               // dataset
		            TableOrder.BY_ROW,
		            true,                  // include legend
		            true,
		            false
		        );
		 return chart;
		}
	
	protected Dataset convert2DatasetOld(GraphData st) {

		DefaultPieDataset dataset = new DefaultPieDataset();
		List<Point<? extends Number, ? extends Number>> pointslist = st.getData();

		// NOTE: after the graph generation graphs are inverted in x and y
		int numbOfRows = pointslist.size();
		if (numbOfRows > 0) {
			int numbOfCols = pointslist.get(0).getEntries().size();
			// calclulation will be made only for the first series
			int x = 0;

			String xlabel = pointslist.get(x).getLabel();

			//calculate maximum
			double max = 0;
			for (int y = 0; y < numbOfCols; y++) {
				double value = pointslist.get(x).getEntries().get(y).getValue().doubleValue();
				if (value>max){
					max = value;
				}
			}
			
			
			for (int y = 0; y < numbOfCols; y++) {
				double value = pointslist.get(x).getEntries().get(y).getValue().doubleValue();
				value = (value/max)*100;
				String ylabel = pointslist.get(x).getEntries().get(y).getLabel();
				AnalysisLogger.getLogger().info(xlabel + ":" + ylabel +"->"  + value);
				dataset.setValue(xlabel + ":" + ylabel, value);
			}

		}
		return dataset;
	}

	@Override
	protected GenericStandaloneGraph getInstance(String title) {
		return new PieGraph(title);
	}

	
	
}
