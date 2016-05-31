package org.gcube.dataanalysis.geo.test.projections;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.connectors.asc.AscDataExplorer;
import org.gcube.dataanalysis.geo.connectors.asc.AscRasterWriter;
import org.gcube.dataanalysis.geo.connectors.geotiff.GeoTiff;
import org.gcube.dataanalysis.geo.connectors.netcdf.NetCDF;
import org.gcube.dataanalysis.geo.connectors.table.Table;
import org.gcube.dataanalysis.geo.connectors.table.TableMatrixRepresentation;
import org.gcube.dataanalysis.geo.connectors.wcs.WCS;
import org.gcube.dataanalysis.geo.connectors.wfs.WFS;
import org.gcube.dataanalysis.geo.utils.MapUtils;
import org.gcube.dataanalysis.geo.utils.VectorOperations;

public class TestXYExtractionConnectors {

	static String[] urlToTest3 = {
	// "http://geoserver3.d4science.research-infrastructures.eu/geoserver"
	// "http://geoserver2.d4science.research-infrastructures.eu/geoserver"
	"http://www.fao.org/figis/geoserver/species/ows" };

	static String[] layernamesTest3 = {
	// "lsoleasolea20121217184934494cet"
	// "lcarcharodoncarcharias20121217173706733cet"
	// "lxiphiasgladius20130410182141778cest"
	// "SPECIES_DIST_BIB"

	"SPECIES_DIST_SWO" };

	static String[] urlToTest1 = { "http://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/global-reanalysis-phys-001-004-b-ref-fr-mjm95-gridv_OCEANS_CLIMATOLOGY_METEOROLOGY_ATMOSPHERE_1366211498692.nc", };

	static String[] layernamesTest1 = { "vomecrty" };

	static String[] urlToTest2 = { "http://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/WOA2005TemperatureAnnual_CLIMATOLOGY_METEOROLOGY_ATMOSPHERE_.nc" };

	static String[] layernamesTest2 = { "t00an1" };

	static String[] urlToTest5 = { "./maxent3719990c-7998-4859-9dca-4b0a792f9d2f/layer1.asc" };

	static String[] layernamesTest5 = { "layer1" };

	static String[] urlToTest6 = { "table" };

	static String[] layernamesTest6 = { "table" };

	static String[] urlToTest = { "tableeez" };

	static String[] layernamesTest = { "tableeez" };
	
	static String[] urlToTest_ = { "https://dl.dropboxusercontent.com/u/12809149/layer1.asc", "http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/ph.asc", "http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/calcite.asc", 
		"https://dl.dropboxusercontent.com/u/12809149/wind1.tif", 
		"http://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/WOA2005TemperatureAnnual_CLIMATOLOGY_METEOROLOGY_ATMOSPHERE_.nc", "http://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/salinity_annual_1deg_ENVIRONMENT_OCEANS_.nc", "http://thredds.d4science.org/thredds/fileServer/public/netcdf/global-reanalysis-phys-001-004-b-ref-fr-mjm95-icemod_ENVIRONMENT_OCEANS_CLIMATOLOGY_METEOROLOGY_ATMOSPHERE_1366211441189.nc", "http://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/CERSAT-GLO-CLIM_WIND_L4-OBS_FULL_TIME_SERIE_CLIMATOLOGY_METEOROLOGY_ATMOSPHERE_1366217956317.nc", "http://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/phosphate_seasonal_5deg_ENVIRONMENT_BIOTA_.nc", "http://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/global-analysis-bio-001-008-_a_BIOTA_ENVIRONMENT_1366217546908.nc", "http://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/dissolved_oxygen_annual_1deg_ENVIRONMENT_BIOTA_.nc", "http://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/global-reanalysis-phys-001-004-b-ref-fr-mjm95-gridv_OCEANS_CLIMATOLOGY_METEOROLOGY_ATMOSPHERE_1366211498692.nc", "http://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/nitrate_seasonal_5deg_ENVIRONMENT_BIOTA_.nc", "http://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/global-analysis-bio-001-008-a_BIOTA_ENVIRONMENT_1366217608283.nc", "http://thredds.research-infrastructures.eu/thredds/fileServer/public/netcdf/cloudmean.asc", 
		"http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver/wcs/wcs?service=wcs&version=1.0.0&request=GetCoverage&coverage=aquamaps:WorldClimBio2&CRS=EPSG:4326&bbox=-180,0,180,90&width=1&height=1&format=geotiff&RESPONSE_CRS=EPSG:4326", 
		"http://geoserver2.d4science.research-infrastructures.eu/geoserver"

	};

	static String[] layernamesTest_ = { "layer1", "ph", "calcite", "wind", "t00an1", "s_sd", "iicevelu", "wind_speed", "p_mn", "CHL", "o_mn", "vomecrty", "n_mn", "PHYC", "cloud", "aquamaps:WorldClimBio2", "lxiphiasgladius20130410182141778cest" };

	public static void main(String[] args) throws Exception {
		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "gcube");
		config.setParam("DatabasePassword", "d4science2");
		config.setParam("DatabaseURL", "jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		config.setGcubeScope("/gcube/devsec/devVRE");

		FileWriter fw = new FileWriter(new File("mapsconnectors.txt"));
		for (int t = 0; t < urlToTest.length; t++) {

			String layerURL = urlToTest[t];
			String layerName = layernamesTest[t];
			AnalysisLogger.getLogger().debug("Processing Layer: " + layerURL);
			List<Double> values = null;
			double res = 0.5d;
			List<Tuple<Double>> tuples = VectorOperations.generateCoordinateTripletsInBoundingBox(-180, 180, -90, 90, 0, res, res);

			if (layerURL.endsWith(".nc")) {
				NetCDF geotiff = new NetCDF(layerURL, layerName);
				values = geotiff.getFeaturesInTimeInstantAndArea(layerURL, layerName, 0, tuples, -180, 180, -90, 90);
			} else if (layerURL.endsWith(".asc")) {
				AscDataExplorer asc = new AscDataExplorer(layerURL);
				values = asc.retrieveDataFromAsc(tuples, 0);
			} else if (layerURL.endsWith("tif")) {
				GeoTiff geotiff = new GeoTiff(config);
				values = geotiff.getFeaturesInTimeInstantAndArea(layerURL, layerName, 0, tuples, -180, 180, -90, 90);
			} else if (layerURL.contains("wcs")) {
				WCS wcs = new WCS(config, layerURL);
				values = wcs.getFeaturesInTimeInstantAndArea(layerURL, layerName, 0, tuples, -180, 180, -90, 90);
			} else if (layerURL.contains("geoserver")) {
				WFS wfs = new WFS();
				values = wfs.getFeaturesInTimeInstantAndArea(layerURL, layerName, 0, tuples, -180, 180, -90, 90);
			} else if (layerURL.equals("table")) {
				config.setParam(TableMatrixRepresentation.tableNameParameter, "testextraction4");
				config.setParam(TableMatrixRepresentation.xDimensionColumnParameter, "approx_x");
				config.setParam(TableMatrixRepresentation.yDimensionColumnParameter, "approx_y");
				config.setParam(TableMatrixRepresentation.timeDimensionColumnParameter, "time");
				config.setParam(TableMatrixRepresentation.valueDimensionColumnParameter, "f_cat");
				config.setParam(TableMatrixRepresentation.filterParameter, " ");

				Table connector = new Table(config, res);
				values = connector.getFeaturesInTimeInstantAndArea(null, null, 0, tuples, -180, 180, -90, 90);
			} else if (layerURL.contains("tableeez")) {
				config.setParam("DatabaseUserName", "postgres");
				config.setParam("DatabasePassword", "d4science2");
				config.setParam("DatabaseURL", "jdbc:postgresql://geoserver-dev.d4science-ii.research-infrastructures.eu/aquamapsdb");
				config.setParam(TableMatrixRepresentation.tableNameParameter, "\"WorldEEZv72012HR\"");
				config.setParam(TableMatrixRepresentation.xDimensionColumnParameter, "longitude");
				config.setParam(TableMatrixRepresentation.yDimensionColumnParameter, "latitude");
				config.setParam(TableMatrixRepresentation.timeDimensionColumnParameter, "");
				config.setParam(TableMatrixRepresentation.valueDimensionColumnParameter, "eez_id");
				config.setParam(TableMatrixRepresentation.filterParameter, " ");

				Table connector = new Table(config, res);
				values = connector.getFeaturesInTimeInstantAndArea(null, null, 0, tuples, -180, 180, -90, 90);
			}
			double[][] matrix = VectorOperations.vectorToMatix(values, -180, 180, -90, 90, res, res);

			// System.out.println(MapUtils.globalASCIIMap(values,step,step));
			System.out.println(MapUtils.globalASCIIMap(matrix));
			fw.write(MapUtils.globalASCIIMap(matrix));

			AscRasterWriter writer = new AscRasterWriter();
			writer.writeRasterInvertYAxis("testraster.asc", matrix, -180, -90, res, "-9999");

		}

		fw.close();

	}

}
