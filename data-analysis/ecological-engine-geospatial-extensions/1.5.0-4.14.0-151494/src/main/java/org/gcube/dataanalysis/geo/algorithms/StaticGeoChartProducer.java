package org.gcube.dataanalysis.geo.algorithms;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.transducers.charts.AbstractChartsProducer;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.geo.charts.GeoMapChart;
import org.gcube.dataanalysis.geo.charts.GeoTemporalPoint;

public class StaticGeoChartProducer extends AbstractChartsProducer {

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
		ColumnTypesList q = new ColumnTypesList(inputTableParameter, quantitiesParameter, "The numeric quantities to visualize ", true);

		inputs.add(p1);
		inputs.add(p2);
		inputs.add(q);

		DatabaseType.addDefaultDBPars(inputs);
	}

	@Override
	public String getDescription() {
		return "An algorithm producing a charts that displays quantities as colors of countries. The color indicates the sum of the values recorded in a country.";
	}

	public String InfoRetrievalQuery(String table, String[] dimensions, String quantity, String time) {
		if (quantity.length() == 0)
			return "select distinct " + Arrays.toString(dimensions).replace("[", "").replace("]", "") + " from " + table;
		else
			return "select distinct " + Arrays.toString(dimensions).replace("[", "").replace("]", "") + " , " + quantity + " as quanta232a from " + table;
	}

	public String[] getDimensions() {
		String[] dimensions = { IOHelper.getInputParameter(config, longitudeParameter), IOHelper.getInputParameter(config, latitudeParameter) };
		return dimensions;
	}

	@Override
	public LinkedHashMap<String , Object> createCharts(String[] dimensions, String quantity, String time, List<Object> rows, boolean displaychart) {
		if (dimensions == null)
			dimensions = new String[0];

		List<GeoTemporalPoint> xyvalues = new ArrayList<GeoTemporalPoint>();
		long t0 = System.currentTimeMillis();
		AnalysisLogger.getLogger().debug("StaticGeoChartProducer: building Geo dataset");
		for (Object row : rows) {
			Object[] array = (Object[]) row;
			Double lat = null;
			Double longitude = null;
			Double q = null;
			try {
				longitude = Double.parseDouble("" + array[0]);
				lat = Double.parseDouble("" + array[1]);
				if (quantity.length() >0)
					q = Double.parseDouble("" + array[2]);
				else
					q = 0d;
			} catch (Exception e) {
			}
			if (lat != null && longitude != null && q != null)
				xyvalues.add(new GeoTemporalPoint(longitude, lat, q));
			else 
				AnalysisLogger.getLogger().debug("StaticGeoChartProducer: skipping these values "+longitude+","+lat+","+q);
		}
		AnalysisLogger.getLogger().debug("StaticGeoChartProducer: producing charts");
		LinkedHashMap<String , Object> charts = new LinkedHashMap<String, Object>();
		
		try {
			String baseImageName = new File(config.getPersistencePath(), "" + UUID.randomUUID()).getAbsolutePath();
			String pointsImage = baseImageName + "_points.jpg";
			if (xyvalues.size()>0){
				GeoMapChart.createWorldImageWithPoints(config.getConfigPath(),xyvalues, pointsImage);
				charts.put("Distribution of latitudes and longitudes points",ImageIO.read(new File(pointsImage)));
			
			
			if (quantity.length()>0) {
				AnalysisLogger.getLogger().debug("StaticGeoChartProducer: quantity is present, producing all the static geocharts");
				String eezImage = baseImageName + "_eez.jpg";
				GeoMapChart.createEEZWeightedImage(config.getConfigPath(),xyvalues, eezImage, config.getPersistencePath());
				try{charts.put("Distribution of summed quantities over Exclusive Economic Zone delimitations",ImageIO.read(new File(eezImage)));}catch(Exception e){AnalysisLogger.getLogger().debug("StaticGeoChartProducer: WARNING could not produce EEZ chart");}
				String faoImage = baseImageName + "_FAO.jpg";
				GeoMapChart.createFAOAreasWeightedImage(config.getConfigPath(),xyvalues, faoImage, config.getPersistencePath());
				try{charts.put("Distribution of summed quantities over FAO Major Area delimitations",ImageIO.read(new File(faoImage)));}catch(Exception e){AnalysisLogger.getLogger().debug("StaticGeoChartProducer: WARNING could not produce FAO chart");}
				String worldImage = baseImageName + "_world.jpg";
				GeoMapChart.createWorldWeightedImage(config.getConfigPath(),xyvalues, worldImage, config.getPersistencePath(),null,null);
				try{charts.put("Distribution of summed quantities over emerged lands, divided per country",ImageIO.read(new File(worldImage)));}catch(Exception e){AnalysisLogger.getLogger().debug("StaticGeoChartProducer: WARNING could not country chart");}
			}
			else{
				AnalysisLogger.getLogger().debug("StaticGeoChartProducer: quantity is absent, producing only one geochart");
			}
		}
			else
				AnalysisLogger.getLogger().debug("StaticGeoChartProducer: no viable point was found");
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("StaticGeoChartProducer: error in producing GeoCharts " + e.getMessage());
		}
		
		AnalysisLogger.getLogger().debug("StaticGeoChartProducer: procedure finished in ms " + (System.currentTimeMillis() - t0));
		return charts;
	}

}
