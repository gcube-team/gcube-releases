package org.gcube.dataanalysis.geo.algorithms;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.transducers.charts.TimeSeriesChartsTransducerer;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.geo.charts.GeoMapChart;
import org.gcube.dataanalysis.geo.charts.GeoTemporalPoint;

public class TimeGeoChartProducer extends TimeSeriesChartsTransducerer {

	protected static String longitudeParameter = "Longitude";
	protected static String latitudeParameter = "Latitude";

	@Override
	protected void setInputParameters() {
		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.GENERIC);
		InputTable tinput = new InputTable(templates, inputTableParameter, "The input table");
		inputs.add(tinput);

		ColumnType p1 = new ColumnType(inputTableParameter, longitudeParameter, "The column containing longitude decimal values", "long", false);
		ColumnType p2 = new ColumnType(inputTableParameter, latitudeParameter, "The column containing latitude decimal values", "lat", false);
		ColumnTypesList q = new ColumnTypesList(inputTableParameter, quantitiesParameter, "The numeric quantities to visualize ", false);
		ColumnType t = new ColumnType(inputTableParameter, timeParameter, "The column containing time information", "year", false);

		inputs.add(p1);
		inputs.add(p2);
		inputs.add(q);
		inputs.add(t);

		DatabaseType.addDefaultDBPars(inputs);
	}

	@Override
	public String getDescription() {
		return "An algorithm producing an animated gif displaying quantities as colors in time. The color indicates the sum of the values recorded in a country.";
	}

	public String InfoRetrievalQuery(String table, String[] dimensions, String quantity, String time) {
		return "select distinct " + Arrays.toString(dimensions).replace("[", "").replace("]", "") + " , " + quantity + "," + time + " from " + table;
	}

	public String[] getDimensions() {
		String[] dimensions = { IOHelper.getInputParameter(config, longitudeParameter), IOHelper.getInputParameter(config, latitudeParameter) };
		return dimensions;
	}

	@Override
	public LinkedHashMap<String, Object> createCharts(String[] dimensions, String quantity, String time, List<Object> rows, boolean displaychart) {
		if (dimensions == null)
			dimensions = new String[0];

		List<GeoTemporalPoint> xyvalues = new ArrayList<GeoTemporalPoint>();
		long t0 = System.currentTimeMillis();
		AnalysisLogger.getLogger().debug("TimeGeoChartProducer: building Geo dataset");
		for (Object row : rows) {
			Object[] array = (Object[]) row;
			Double lat = null;
			Double longitude = null;
			Double q = null;
			String timel = null;
			try {
				longitude = Double.parseDouble("" + array[0]);
				lat = Double.parseDouble("" + array[1]);
				q = Double.parseDouble("" + array[2]);
				if (array[3] != null)
					timel = "" + array[3];
			} catch (Exception e) {
			}
			if (lat != null && longitude != null && q != null && timel != null) {
				xyvalues.add(new GeoTemporalPoint(longitude, lat, q, timel));

			} else
				AnalysisLogger.getLogger().debug("TimeGeoChartProducer: skipping these values " + longitude + "," + lat + "," + q + "," + timel);
		}
		AnalysisLogger.getLogger().debug("TimeGeoChartProducer: producing charts");

		LinkedHashMap<String, Object> charts = new LinkedHashMap<String, Object>();

		try {
			String baseImageName = new File(config.getPersistencePath(), "" + UUID.randomUUID()).getAbsolutePath();
			String timeImage = baseImageName + "_time.gif";
			String cumulativeImage = baseImageName + "_cutime.gif";
			String faotimeImage = baseImageName + "_faotime.gif";
			String eeztimeImage = baseImageName + "_eeztime.gif";
			String pointsImage = baseImageName + "points.gif";
			if (xyvalues.size() > 0) {
				AnalysisLogger.getLogger().debug("TimeGeoChartProducer: producing Points Chart in time " + pointsImage);
				GeoMapChart.createPointsImageInTime(config.getConfigPath(), xyvalues, pointsImage, config.getPersistencePath());
				charts.put("A GIF file displaying the points recorded in the time frames of the dataset", new File(pointsImage));

				AnalysisLogger.getLogger().debug("TimeGeoChartProducer: producing World Chart in time " + timeImage);
				GeoMapChart.createWorldWeightedImageInTime(config.getConfigPath(), xyvalues, timeImage, config.getPersistencePath(), false);
				charts.put("A GIF file displaying the temporal trend of quantities in each time instant over emerged lands, divided per country", new File(timeImage));

				AnalysisLogger.getLogger().debug("TimeGeoChartProducer: producing cumulative World Chart in time " + cumulativeImage);
				GeoMapChart.createWorldWeightedImageInTime(config.getConfigPath(), xyvalues, cumulativeImage, config.getPersistencePath(), true);
				charts.put("A GIF file displaying the temporal trend of cumulative quantities over emerged lands, divided per country", new File(cumulativeImage));

				AnalysisLogger.getLogger().debug("TimeGeoChartProducer: producing EEZ World Chart in time " + eeztimeImage);
				GeoMapChart.createEEZWeightedImageInTime(config.getConfigPath(), xyvalues, eeztimeImage, config.getPersistencePath(), true);
				charts.put("A GIF file displaying the temporal trend of cumulative quantities over Exclusive Economic Zones", new File(eeztimeImage));

				AnalysisLogger.getLogger().debug("TimeGeoChartProducer: producing FAO Areas World Chart in time " + faotimeImage);
				GeoMapChart.createFAOAreasWeightedImageInTime(config.getConfigPath(), xyvalues, faotimeImage, config.getPersistencePath(), true);
				charts.put("A GIF file displaying the temporal trend of cumulative quantities over FAO Areas", new File(faotimeImage));
			}
			else
				AnalysisLogger.getLogger().debug("TimeGeoChartProducer: no point was found");
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("TimeGeoChartProducer: error in producing GeoCharts " + e.getMessage());
			AnalysisLogger.getLogger().debug(e);
		}

		AnalysisLogger.getLogger().debug("TimeGeoChartProducer: procedure finished in ms " + (System.currentTimeMillis() - t0));
		return charts;
	}

}
