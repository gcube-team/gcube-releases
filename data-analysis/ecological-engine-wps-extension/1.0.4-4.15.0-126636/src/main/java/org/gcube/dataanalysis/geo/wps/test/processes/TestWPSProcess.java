package org.gcube.dataanalysis.geo.wps.test.processes;

import java.util.HashMap;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.geo.wps.interfaces.WPSProcess;

public class TestWPSProcess {

	static String[] algorithms = { "com.terradue.wps_hadoop.processes.examples.async.Async", "com.terradue.wps_hadoop.processes.ird.kernel_density.KernelDensity", "com.terradue.wps_hadoop.processes.terradue.envi_enrich.EnviEnrich", "com.terradue.wps_hadoop.processes.fao.intersection.Intersection", "com.terradue.wps_hadoop.processes.fao.spread.Spread" };

	static String wps = "http://wps01.i-marine.d4science.org:80/wps/WebProcessingService";

	static AlgorithmConfiguration[] configs = { testAsynch(), testKernelDensity(), testEnvironmentalEnrichment(), testIntersection(), testSpread() };

	public static void main(String[] args) throws Exception {

		System.out.println("TEST 1");

		for (int i = 0; i < algorithms.length; i++) {
			AnalysisLogger.getLogger().debug("Executing:" + algorithms[i]);
			
			ComputationalAgent trans = new WPSProcess(wps, algorithms[i]);

			trans.setConfiguration(configs[i]);
			trans.init();
			Regressor.process(trans);
			StatisticalType st = trans.getOutput();
			AnalysisLogger.getLogger().debug("ST:" + st);
			PrimitiveType p = (PrimitiveType)st;
			HashMap map = (HashMap) p.getContent();
			for (Object v:map.values())
				System.out.println("Out: "+((PrimitiveType)v).getContent());
			trans = null;
		}
	}

	private static AlgorithmConfiguration testAsynch() {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setParam("secondsDelay", "1");
		return config;
	}

	private static AlgorithmConfiguration testKernelDensity() {
		// com.terradue.wps_hadoop.processes.ird.kernel_density.KernelDensity
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setParam("species", "Carcharodon carcharias");
		return config;
	}

	private static AlgorithmConfiguration testEnvironmentalEnrichment() {

		// com.terradue.wps_hadoop.processes.terradue.envi_enrich.EnviEnrich

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setParam("species", "Carcharodon carcharias");
		config.setParam("envVars", "salinity" + AlgorithmConfiguration.listSeparator + "sst");

		return config;
	}

	private static AlgorithmConfiguration testIntersection() {

		// com.terradue.wps_hadoop.processes.ird.kernel_density.KernelDensity
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setParam("masterWfsUrl", "http://www.fao.org/figis/geoserver/species/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=species:SPECIES_DIST_OCC");
		config.setParam("slaveWfsUrls", "http://mdst-macroes.ird.fr:8080/constellation/WS/wfs/longhurst?service=WFS&version=1.1.0&request=GetFeature&typeName=Longhurst_world_v4_2010:Longhurst_world_v4_2010");
		config.setParam("outputFormat", "GML");

		return config;
	}

	private static AlgorithmConfiguration testSpread() {
		// dataInputs=geoColumn=field0;quantityColumn=field4;sourceAreaLayerName=FAO_AREAS;targetAreaLayerName=EEZ_HIGHSEAS;dataUrls=https://dl.dropboxusercontent.com/u/24368142/timeseries_100.json;&ResponseDocument=result

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setParam("geoColumn", "field0");
		config.setParam("quantityColumn", "field4");
		config.setParam("sourceAreaLayerName", "FAO_AREAS");
		config.setParam("targetAreaLayerName", "EEZ_HIGHSEAS");
		config.setParam("dataUrls", "https://dl.dropboxusercontent.com/u/24368142/timeseries_100.json");

		return config;
	}

	private static AlgorithmConfiguration testTuna1() {
		// dataInputs=geoColumn=field0;quantityColumn=field4;sourceAreaLayerName=FAO_AREAS;targetAreaLayerName=EEZ_HIGHSEAS;dataUrls=https://dl.dropboxusercontent.com/u/24368142/timeseries_100.json;&ResponseDocument=result
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setParam("species", "Thunnus albacares");
		config.setParam("wfsUrl", "http://www.fao.org/figis/geoserver/species/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=species:SPECIES_DIST_OCC");
		return config;
	}

	private static AlgorithmConfiguration testTuna2() {
		// dataInputs=geoColumn=field0;quantityColumn=field4;sourceAreaLayerName=FAO_AREAS;targetAreaLayerName=EEZ_HIGHSEAS;dataUrls=https://dl.dropboxusercontent.com/u/24368142/timeseries_100.json;&ResponseDocument=result
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setParam("species", "Thunnus albacares");
		config.setParam("wfsUrl", "http://www.fao.org/figis/geoserver/species/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=species:SPECIES_DIST_OCC");
		return config;
	}

}
