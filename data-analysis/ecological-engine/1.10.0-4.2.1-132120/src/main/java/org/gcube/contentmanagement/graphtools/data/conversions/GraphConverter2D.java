package org.gcube.contentmanagement.graphtools.data.conversions;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.graphtools.abstracts.SamplesTable;
import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphData;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;
import org.gcube.portlets.user.timeseries.charts.support.types.Point;
import org.gcube.portlets.user.timeseries.charts.support.types.ValueEntry;

/*
 * Performs Operations on Lists of Points
 * Helps in transforming a SamplesTable to a GraphData
 */
public class GraphConverter2D {

	private static Point<? extends Number, ? extends Number> searchPoint(Point<? extends Number, ? extends Number> x, List<Point<? extends Number, ? extends Number>> samples) {
		Point<? extends Number, ? extends Number> novelpoint = null;
		for (Point<? extends Number, ? extends Number> point : samples) {
			// if (point.getValue().equals(x.getValue())) {
			if (point.getLabel().equals(x.getLabel())) {
				novelpoint = point;
				break;
			}
		}

		return novelpoint;
	}

	// optimizes the dimensions of the sample table, ordering the x and y entries

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Point<? extends Number, ? extends Number>> reduceDimension(List<Point<? extends Number, ? extends Number>> samples) {
		ArrayList<Point<? extends Number, ? extends Number>> novelSamples = new ArrayList<Point<? extends Number, ? extends Number>>();
		int novelCounter = 0;
		try {
			for (Point<? extends Number, ? extends Number> pointsample : samples) {
				// search the current point in the new built list
				Point<? extends Number, ? extends Number> novelPoint = searchPoint(pointsample, novelSamples);

				int index = 0;
				ValueEntry pointValue = null;
				// if it is not the first insertion then find the optimal index for the y value of the current point
				// that is: find the column to insert the value
				if (novelCounter > 0) {
					// find column index
					pointValue = pointsample.getEntries().get(0);
					List<?> referencevalues = novelSamples.get(0).getEntries();
					int i = 0;
					index = referencevalues.size();
					// get the best column
					for (Object val : referencevalues) {
						if (((ValueEntry) val).getLabel().equals(pointValue.getLabel())) {
							index = i;
							break;
						}
						i++;
					}
				}
				// if the point has not been inserted yet (there isn't another point with the same label previously inserted)
				if (novelPoint == null) {
					// generate a new y
					ValueEntry ve = new ValueEntry(pointsample.getEntries().get(0).getLabel(), pointsample.getEntries().get(0).getValue());
					// generate a new (x,y)
					novelPoint = new Point(pointsample.getLabel(), pointsample.getValue());

					// the number of columns to fill with 0s corresponds to all the columns
					int numofcolumns = index;
					if (novelCounter > 0)
						numofcolumns = novelSamples.get(0).getEntries().size();

					// fill all the columns with 0s
					for (int j = 0; j < numofcolumns; j++) {
						novelPoint.getEntries().add(j, new ValueEntry(novelSamples.get(0).getEntries().get(j).getLabel(), Double.valueOf(0)));
					}

					// add the y at the right column according to the calculated index
					if (index >= novelPoint.getEntries().size())
						novelPoint.getEntries().add(index, ve);
					else
						novelPoint.getEntries().set(index, ve);

					// add the new point in the list
					novelSamples.add(novelPoint);

					novelCounter++;
				} else {

					// if we found a previous element update it
					if (index >= novelPoint.getEntries().size())
						// if the index is higher than the y size, add the column at the end
						novelPoint.getEntries().add(index, pointValue);
					else
						// otherwise substitute the current index value
						novelPoint.getEntries().set(index, pointValue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return novelSamples;

	}

	// converts a bidimensional sampleTable to a list of bidimensional Points
	@SuppressWarnings("unchecked")
	public static List<Point<? extends Number, ? extends Number>> convert(SamplesTable sampleTable) {
		ArrayList<Point<? extends Number, ? extends Number>> pointsList = new ArrayList<Point<? extends Number, ? extends Number>>();
		try {
			// every point has a label and a list of associated y points
			int rows = sampleTable.getNumOfDataRows();
			for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
				// take the label: it is separated in two parts separated by ';'
				String label = sampleTable.getClassification(rowIndex);
				int commaIndex = label.indexOf(";");
				String xlabel = label;
				String ylabel = "";
				if (commaIndex > 0) {
					xlabel = label.substring(0, commaIndex);
					ylabel = label.substring(commaIndex + 1);
				}
				double x = sampleTable.getValue(rowIndex, 0);
				double y = sampleTable.getValue(rowIndex, 1);
				ValueEntry<Double> ve = new ValueEntry<Double>(ylabel, y);

				Point<Double, Double> p = new Point<Double, Double>(xlabel, x, ve);
				pointsList.add(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pointsList;
	}

	@SuppressWarnings("rawtypes")
	public static List<Point<? extends Number, ? extends Number>> deleteHeaders(List<Point<? extends Number, ? extends Number>> samples) {

		int size = samples.size();

		for (int i = 0; i < size; i++) {
			Point p = samples.get(i);
			if (p.getLabel().equals("header")) {
				samples.remove(i);
				size--;
				i--;
			}
		}

		return samples;
	}

	// performs a complete transformation
	public static List<Point<? extends Number, ? extends Number>> transformTable(SamplesTable sampleTable) {

		List<Point<? extends Number, ? extends Number>> singlegraph = convert(sampleTable);
		singlegraph = reduceDimension(singlegraph);
		singlegraph = deleteHeaders(singlegraph);

		return singlegraph;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Point<? extends Number, ? extends Number>> reorder(List<Point<? extends Number, ? extends Number>> samples) {

		List<Point<? extends Number, ? extends Number>> orderedsamples = new ArrayList<Point<? extends Number, ? extends Number>>();
		// check and reorder points
		for (Point p : samples) {
			int index = 0;

			for (Point ordP : orderedsamples) {
				if (ordP.getValue().doubleValue() > p.getValue().doubleValue()) {
					break;
				}

				index++;
			}

			orderedsamples.add(index, p);
		}
		// re-enumerate x dimension
		int i = 0;
		for (Point ordP : orderedsamples) {
			try {
				ordP.setValue(Double.valueOf(i));
			} catch (Exception e) {
			}
			i++;
		}
		return orderedsamples;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Point<? extends Number, ? extends Number>> filterXRange(List<Point<? extends Number, ? extends Number>> samples, String minX, String maxX) {

		List<Point<? extends Number, ? extends Number>> filteredsamples = new ArrayList<Point<? extends Number, ? extends Number>>();
		boolean copy = false;
		for (Point p : samples) {
			if (p.getLabel().equals(minX)) {
				copy = true;
			}
			if (copy) {
				filteredsamples.add(p);
			}
			if (p.getLabel().equals(maxX)) {
				break;
			}
		}
		return filteredsamples;

	}

	public static final String SPIKE = "STATIONARY";

	public static void anotateStationaryPoints(GraphGroups gg) {

		for (String key : gg.getGraphs().keySet()) {
			GraphData graph = gg.getGraphs().get(key);

			// for each series
			int trends = graph.getData().size();
			int yvalues = graph.getData().get(0).getEntries().size();
			// System.out.println("yvalues "+yvalues);
			// System.out.println("trends "+trends);
			for (int i = 0; i < trends; i++) {
				double[] points = MathFunctions.points2Double(graph.getData(), i, yvalues);
				double[] derivative = MathFunctions.derivative(points);
				boolean[] spikes = MathFunctions.findSpikes(derivative, threshold);
				for (int k = 0; k < yvalues; k++) {
					if (spikes[k]) {
						String label = graph.getData().get(i).getEntries().get(k).getLabel();
						String newLabel = label + ";" + SPIKE;
						graph.getData().get(i).getEntries().get(k).setLabel(newLabel);
					}
				}
			}
		}
		// return gg;
	}

	private static double threshold = 0.001;

	public static void anotateStationaryPoints(GraphGroups gg, List<String> lables) {

		for (String key : gg.getGraphs().keySet()) {
			GraphData graph = gg.getGraphs().get(key);

			// for each series
			int trends = graph.getData().size();
			int yvalues = graph.getData().get(0).getEntries().size();
			int spikeslablessize = lables.size();
			// System.out.println("yvalues "+yvalues);
			// System.out.println("trends "+trends);
			for (int i = 0; i < trends; i++) {
				double[] points = MathFunctions.points2Double(graph.getData(), i, yvalues);
				double[] derivative = MathFunctions.derivative(points);
				boolean[] spikes = MathFunctions.findSpikes(derivative, threshold);
				int spikecounter = 0;
				for (int k = 0; k < yvalues; k++) {
					if (spikes[k]) {
						String label = graph.getData().get(i).getEntries().get(k).getLabel();
						String spikelable = SPIKE;
						if (spikecounter < spikeslablessize)
							spikelable = lables.get(spikecounter);

						String newLabel = label + ";" + spikelable;
						graph.getData().get(i).getEntries().get(k).setLabel(newLabel);
						spikecounter++;
					}
				}
			}
		}
		// return gg;
	}

	public static void anotatePoints(GraphGroups gg, List<Integer> pointsIndexes, List<String> lables) {

		for (String key : gg.getGraphs().keySet()) {
			GraphData graph = gg.getGraphs().get(key);

			// for each series
			int trends = graph.getData().size();
			for (int i = 0; i < trends; i++) {
				int progressive = 0;
				for (Integer index : pointsIndexes) {
					String label = graph.getData().get(i).getEntries().get(index.intValue()).getLabel();
					String addinglabel = lables.get(progressive);
					String newLabel = label;
					if (addinglabel != null)
						newLabel += ";" + addinglabel;
					graph.getData().get(i).getEntries().get(index.intValue()).setLabel(newLabel);
					progressive++;
				}
			}
		}
		// return gg;
	}

	// works a single trend in the graph
	public static List<Point> getStationaryPoints(GraphData graph) throws Exception {
		List<Point> st = new ArrayList<Point>();
		Point p = graph.getData().get(0);
		st.add(new Point(p.getLabel(), p.getValue()));
		int yvalues = graph.getData().get(0).getEntries().size();
		double[] points = MathFunctions.points2Double(graph.getData(), 0, yvalues);
		double[] derivative = MathFunctions.derivative(points);
		boolean[] spikes = MathFunctions.findSpikes(derivative, threshold);

		for (int k = 0; k < yvalues; k++) {
			if (spikes[k]) {
				String label = graph.getData().get(0).getEntries().get(k).getLabel();
				Double val = points[k];
				ValueEntry v = new ValueEntry(label, val);
				st.get(0).addEntry(v);
			}
		}

		return st;
	}

	// works a single trend in the graph
	public static List<String> getLablesFromPoints(Point points) throws Exception {
		List<String> lables = new ArrayList<String>();

		for (Object v : points.getEntries()) {

			lables.add(((ValueEntry) v).getLabel());
		}

		return lables;
	}

	// works the first trend in the graph: takes a list of points according to a list of indexes
	public static List<String> getLabelsfromIndexes(List<Point<? extends Number, ? extends Number>> points, List<Integer> indexes) throws Exception {
		List<String> lables = new ArrayList<String>();
		int size = indexes.size();

		for (Integer index : indexes) {
			Object v = points.get(0).getEntries().get(index);
			lables.add(((ValueEntry) v).getLabel());
		}

		return lables;
	}

	// works the first trend in the graph: takes a list of points according to a list of indexes
	public static void sampleAnotationBySameFollower(List<Point<? extends Number, ? extends Number>> samples) throws Exception {

		String previousLabel = null;
		// check and reorder points
		for (Point p : samples) {

			for (Object v : p.getEntries()) {

				String label = ((ValueEntry) v).getLabel();

				int indexcomma = label.indexOf(";");
				if (indexcomma >= 0) {

					String labelcountry = label.substring(indexcomma + 1);
					// AnalysisLogger.getLogger().debug("sampleAnotationBySameFollower-> comparing "+labelcountry+" vs "+previousLabel+" ORIGINAL "+label);
					if ((previousLabel != null) && (labelcountry.equals(previousLabel))) {
						label = label.substring(0, indexcomma);
						// AnalysisLogger.getLogger().debug("sampleAnotationBySameFollower-> ELIMINATING LABEL!!!");
						((ValueEntry) v).setLabel(label);
					} else
						previousLabel = labelcountry;
				}
			}

		}
	}

	// works the first trend in the graph: takes a list of points according to a list of indexes
	public static void sampleAnotationByRange(List<Point<? extends Number, ? extends Number>> samples, int range) throws Exception {

		if (range > 0) {
			// check and reorder points
			for (Point p : samples) {
				int partialCounter = 0;
				for (Object v : p.getEntries()) {

					String label = ((ValueEntry) v).getLabel();

					int indexcomma = label.indexOf(";");
					if (indexcomma >= 0) {
						//if not enough time has passed delete the label otherwise reset counter
						if (partialCounter <= range) {
								String labelcountry = label.substring(indexcomma + 1);
//								AnalysisLogger.getLogger().debug("sampleAnotationByRange-> partial counter "+partialCounter+ " label "+ label);
								label = label.substring(0, indexcomma);
//								AnalysisLogger.getLogger().debug("sampleAnotationByRange-> ELIMINATING LABEL!!!");
								((ValueEntry) v).setLabel(label);
						}
						else{
							partialCounter = 0;
						}
					}
					
					partialCounter++;
					
				}

			}
		}
	}
}
