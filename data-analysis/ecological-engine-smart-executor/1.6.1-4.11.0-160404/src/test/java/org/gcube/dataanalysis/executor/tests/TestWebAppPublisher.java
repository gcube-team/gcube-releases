package org.gcube.dataanalysis.executor.tests;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;

public class TestWebAppPublisher {

	static String cfg = "./cfg/";

	public static void main(String[] args) throws Exception {
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");

		config.setAgent("WEB_APP_PUBLISHER");

		config.setGcubeScope("/gcube/devsec/devVRE");
		
		config.setParam("ServiceUserName","gianpaolo.coro");
		config.setParam("MainPage", "index.html");
		//config.setParam("ZipFile", "C:\\Users\\coro\\Desktop\\DATABASE e NOTE\\Experiments\\WEB_APP_PUBLISHER\\SitoUnirender2015.zip");
		//config.setParam("ZipFile", "C:\\Users\\coro\\Desktop\\DATABASE e NOTE\\Experiments\\WEB_APP_PUBLISHER\\gcube.zip");
//		config.setParam("ZipFile", "C:/Users/coro/Desktop/DATABASE e NOTE/Experiments/WEB_APP_PUBLISHER/simplesite.zip");
		config.setParam("ZipFile", "C:/Users/coro/Desktop/DATABASE e NOTE/Experiments/WEB_APP_PUBLISHER/verysimple.zip");
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		AnalysisLogger.getLogger().debug("Executing: " + config.getAgent());
		List<ComputationalAgent> trans = null;
		trans = TransducerersFactory.getTransducerers(config);
		trans.get(0).init();
		CustomRegressor.process(trans.get(0));
		StatisticalType st = trans.get(0).getOutput();
		AnalysisLogger.getLogger().debug("ST:" + st);
		trans = null;

	}

}
