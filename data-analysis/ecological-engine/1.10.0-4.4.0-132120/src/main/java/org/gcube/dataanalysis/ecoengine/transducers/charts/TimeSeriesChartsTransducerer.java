package org.gcube.dataanalysis.ecoengine.transducers.charts;

import java.awt.Image;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.contentmanagement.graphtools.data.conversions.ImageTools;
import org.gcube.contentmanagement.graphtools.plotting.graphs.TimeSeriesGraph;
import org.gcube.contentmanagement.graphtools.plotting.graphs.TransectLineGraph;
import org.gcube.contentmanagement.graphtools.utils.DateGuesser;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.utils.Operations;
import org.gcube.dataanalysis.ecoengine.utils.TimeAnalyzer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;

public class TimeSeriesChartsTransducerer extends QuantitiesAttributesChartsTransducerer {

	@Override
	protected void setInputParameters() {
		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.GENERIC);
		InputTable tinput = new InputTable(templates, inputTableParameter, "The input table");
		inputs.add(tinput);
		ColumnTypesList p1 = new ColumnTypesList(inputTableParameter, attributesParameter, "The dimensions to consider in the charts", true);
		ColumnTypesList p2 = new ColumnTypesList(inputTableParameter, quantitiesParameter, "The numeric quantities to visualize ", false);
		inputs.add(p1);
		inputs.add(p2);
		ColumnType p3 = new ColumnType(inputTableParameter, timeParameter, "The column containing time information", "year", false);
		inputs.add(p3);
		DatabaseType.addDefaultDBPars(inputs);
	}

	@Override
	public String getDescription() {
		return "An algorithm producing time series charts of attributes vs. quantities. Charts are displayed per quantity column and superposing quantities are summed.";
	}

	protected String timepattern = "";
	protected SimpleDateFormat formatter = null;
	protected Date sampleTime = null;
	protected int maxvisualizable = 100;

	public Date getTime(String timel) {
		try {
			if (formatter == null) {
				TimeAnalyzer analyzer = new TimeAnalyzer();
				sampleTime = analyzer.string2Date(timel);
				timepattern = analyzer.getPattern();
				AnalysisLogger.getLogger().debug("TimeSeriesChart: Time pattern: " + timepattern);
				formatter = new SimpleDateFormat(timepattern);
			}

			return formatter.parse(timel);
		} catch (ParseException e) {
			AnalysisLogger.getLogger().debug("Error parsing date " + timel + " using pattern " + timepattern);
			return null;
		}
	}

	public String getChartPattern(Date time) {

		if (timepattern.equals("s") || DateGuesser.isJavaDateOrigin(time)) {
			return "HH:mm:ss:SS";
		} else {
			if (timepattern.length() == 4)
				return "yyyy";
			else
				return "MM-dd-yy";
		}
	}

	@Override
	public LinkedHashMap<String, Object> createCharts(String[] dimensions, String quantity, String time, List<Object> rows, boolean displaychart) {

		if (dimensions == null)
			dimensions = new String[0];

		LinkedHashMap<String, Object> charts = new LinkedHashMap<String, Object>();

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		TimeSeries series = null;
		if (dimensions.length > 0)
			series = new TimeSeries("Trend of " + quantity + " for " + Arrays.toString(dimensions).replace("[", "").replace("]", ""));
		else
			series = new TimeSeries("Trend of " + quantity);
		dataset.addSeries(series);

		DefaultCategoryDataset[] linedatasets = new DefaultCategoryDataset[dimensions.length];

		int nrows = rows.size();
		int[] indicesToTake = null;
		if (nrows > maxvisualizable)
			indicesToTake = Operations.uniformIntegerSampling(0, nrows - 1, maxvisualizable);
		else
			indicesToTake = Operations.uniformIntegerSampling(0, nrows - 1, nrows);

		AnalysisLogger.getLogger().debug("TimeSeriesChartsTransducerer: uniform sampling - taking " + indicesToTake.length + " over " + nrows);

		for (int i = 0; i < indicesToTake.length; i++) {
			Object row = rows.get(indicesToTake[i]);
			Object[] array = (Object[]) row;
			// AnalysisLogger.getLogger().debug("TimeSeriesChartsTransducerer: "+Arrays.toString(array));
			Double q = null;
			Date timeD = null;
			String timel = "" + array[array.length - 1];
			try {
				q = Double.parseDouble("" + array[array.length - 2]);
				timeD = getTime(timel);
			} catch (Exception e) {
				AnalysisLogger.getLogger().debug("TimeSeriesChartsTransducerer: warning skipping value " + q + "," + timel);
			}

			if (q != null && timeD != null) {
				FixedMillisecond ms = new FixedMillisecond(timeD);
				TimeSeriesDataItem item = series.getDataItem(ms);
				if (item != null) {
					double prevquant = (Double) item.getValue();
					q = prevquant + q;
					AnalysisLogger.getLogger().debug("TimeSeriesChartsTransducerer: a previous quantity was found for time " + timel + " : " + prevquant + " setting to " + (prevquant + q));
					item.setValue(q);
				} else
					series.add(ms, q);

				for (int dimIdx = 0; dimIdx < dimensions.length; dimIdx++) {
					String dimensionValue = "";
					if (array[dimIdx] != null)
						dimensionValue = "" + array[dimIdx];
					if (dimensionValue.length() > 0) {
						DefaultCategoryDataset lineds = linedatasets[dimIdx];
						if (lineds == null) {
							lineds = new DefaultCategoryDataset();
							linedatasets[dimIdx] = lineds;
						}
						lineds.addValue(q, "singlets", timel + ";[" + dimensionValue + "]");
					}
				}
			}
		}
		if (sampleTime != null) {
			Image chartTS = ImageTools.toImage(TimeSeriesGraph.createStaticChart(dataset, getChartPattern(sampleTime), "Variation of " + quantity).createBufferedImage(1200, 960));

			if (displaychart) {
				TimeSeriesGraph tsg = new TimeSeriesGraph("Variation of " + quantity);
				tsg.timeseriesformat = getChartPattern(sampleTime);
				tsg.render(dataset);
				for (int i = 0; i < dimensions.length; i++) {
					TransectLineGraph tlg = new TransectLineGraph("Variation of " + dimensions[i]);
					tlg.render(TransectLineGraph.orderByTime(linedatasets[i], timepattern));
				}
			}

			charts.put("Time trend of " + quantity, chartTS);

			for (int i = 0; i < dimensions.length; i++) {
				Image linechartTS = ImageTools.toImage(TransectLineGraph.createStaticChart(TransectLineGraph.orderByTime(linedatasets[i], timepattern)).createBufferedImage(1200, 960));
				charts.put("Annotated chart of " + quantity + " for " + dimensions[i], linechartTS);
			}
		}
		return charts;
	}

}
