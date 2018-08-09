package org.gcube.contentmanagement.graphtools.data;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.graphtools.abstracts.SamplesTable;
import org.gcube.portlets.user.timeseries.charts.support.types.Point;
import org.gcube.portlets.user.timeseries.charts.support.types.ValueEntry;

/*
 * Represents a graphicable SamplesTable to be converted into a GraphData 
 * Performs transformation from GraphData to SamplesTable
 * GraphConverter2D : SamplesTable -> GraphData
 * GraphSamplesTable: GraphData -> SamplesTable
 */

/*
 * 
 * Structure of a transposed graph coming from common structure library
 * 
 * Point 1 (Series1, DefaultValue1) -> (y1,xLabel1) (y2,xLabel2) (y3,xLabel3) ..
 * Point 2 (Series2, DefaultValue2) -> (y1,xLabel1) (y2,xLabel2) (y3,xLabel3) ..
 * ...
 * 
 * Structure of a not-transposed graph coming from database
 * 
 * Point 1 (xLabel1,EnumeratedValue1) -> (y1,Series1) (y2,Series2) (y3,Series3) ..
 * Point 2 (xLabel2,EnumeratedValue2) -> (y1,Series1) (y2,Series2) (y3,Series3) ..
 * ...
 */
public class GraphSamplesTable extends SamplesTable {

	List<Point<? extends Number, ? extends Number>> singlegraph;

	public GraphSamplesTable(List<Point<? extends Number, ? extends Number>> graph) {
		super();
		singlegraph = graph;
	}

	public GraphSamplesTable() {
		super();
		singlegraph = new ArrayList<Point<? extends Number, ? extends Number>>();
	}

	// builds up a graph from a set of values and lables
	// used for building up graphs from simple data
	public GraphSamplesTable(String seriesName, List<String> xLables, List<Double> yValues,boolean invert) {
		super();
		singlegraph = new ArrayList<Point<? extends Number, ? extends Number>>();
		int size = xLables.size();
		try {
			
		if (invert){
			
			Point p = new Point<Number, Number>(seriesName, Double.valueOf(0));
			for (int i = 0; i < size; i++) {
				ValueEntry v = new ValueEntry(xLables.get(i), yValues.get(i));
				p.addEntry(v);
			}
			singlegraph.add(p);
		
		}
		else{
			for (int i = 0; i < size; i++) {
				Point p = new Point<Number, Number>(xLables.get(i),new Double(i));
				ValueEntry v = new ValueEntry("series1", yValues.get(i));
				p.addEntry(v);
				singlegraph.add(p);
			}
		}
		
		} catch (Exception e) {

		}
	}

	@Override
	public int getNumOfAttributes() {
		if (singlegraph.size() > 0)
			return singlegraph.get(0).getEntries().size() + 1;
		else
			return 0;

	}

	@Override
	public int getNumOfDataRows() {
		return singlegraph.size();
	}

	@Override
	public double getValue(int d, int a) {
		Double doub;
		if (a == 0)
			doub = (Double) singlegraph.get(d).getValue();
		else
			doub = (Double) singlegraph.get(d).getEntries().get(a - 1).getValue();

		return doub.doubleValue();
	}

	@Override
	public String getClassification(int d) {

		StringBuffer sb = new StringBuffer();
		int numbOfColumns = getNumOfAttributes();
		sb.append(singlegraph.get(d).getLabel() + ";");
		for (int i = 0; i < numbOfColumns - 1; i++) {
			sb.append(singlegraph.get(d).getEntries().get(i).getLabel());
			if (i < numbOfColumns - 2)
				sb.append(";");
		}
		return sb.toString();
	}

	@Override
	public void addLabel(int i, String label) {
		singlegraph.get(i).setLabel(label);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addSample(int i, int j, double value) {
		try {

			if (j == 0) {
				Point p = singlegraph.get(i);
				p.setValue(Double.valueOf(value));
			} else {
				Point<Double, Double> p = (Point<Double, Double>) singlegraph.get(i);
				p.getEntries().get(j - 1).setValue(new Double(value));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void addSampleRow(String label, double... values) {
		try {

			Double x = values[0];
			Double y1 = Double.valueOf(0);
			if (values.length > 1)
				y1 = values[1];

			String[] lables = label.split(";");
			int labsize = lables.length;
			String labelx = lables[0];
			String labely1 = lables[1];

			ValueEntry<Double> ve = new ValueEntry<Double>(labely1, y1);
			Point<Double, Double> p = new Point<Double, Double>(labelx, x, ve);

			for (int j = 2; j < labsize; j++) {

				Double y = new Double(0);
				if (values.length > j)
					y = values[j];

				p.getEntries().add(new ValueEntry<Double>(lables[j], y));
			}

			singlegraph.add(p);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Point<? extends Number, ? extends Number>> getGraph() {
		return singlegraph;
	}

}
