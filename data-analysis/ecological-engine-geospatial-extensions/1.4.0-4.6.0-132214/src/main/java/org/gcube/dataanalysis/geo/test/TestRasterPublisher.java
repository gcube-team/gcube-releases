package org.gcube.dataanalysis.geo.test;

import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.geo.connectors.table.TableMatrixRepresentation;
import org.gcube.dataanalysis.geo.matrixmodel.ZExtractor;

public class TestRasterPublisher {

	static String cfg = "./cfg/";

	public static void main(String[] args) throws Exception {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		ScopeProvider.instance.set("/gcube/devsec");
		
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");

		config.setAgent("RASTER_DATA_PUBLISHER");
		
		config.setGcubeScope("/gcube/devsec");
		
		config.setParam("ServiceUserName","gianpaolo.coro");
		config.setParam("PublicationLevel","PRIVATE");
		config.setParam("DatasetTitle", "test raster dataset production");
		config.setParam("DatasetAbstract", "test raster dataset production abstract");
		config.setParam("InnerLayerName", "adux_pres_1");
		config.setParam("RasterFile", "C:/Users/coro/Downloads/adux_pres_portale_test.nc");
		config.setParam("Topics", "adux"+AlgorithmConfiguration.listSeparator+"gianpaolo");
		config.setParam("SpatialResolution", "-1");
		config.setParam("FileNameOnInfra", "adux_pres_portale_test_10.nc");
		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		AnalysisLogger.getLogger().debug("Executing: " + config.getAgent());
		List<ComputationalAgent> trans = null;
		trans = TransducerersFactory.getTransducerers(config);
		trans.get(0).init();
		Regressor.process(trans.get(0));
		StatisticalType st = trans.get(0).getOutput();
		AnalysisLogger.getLogger().debug("ST:" + st);
		trans = null;

	}

}
