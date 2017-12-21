package org.gcube.dataanalysis.executor.nodes.transducers.bionym.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.BionymBiodiv;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.BionymFlexibleWorkflowTransducer;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.utils.YasmeenGlobalParameters;

import com.thoughtworks.xstream.XStream;

public class TestBiOnymBiodiv {

	
	public static void main(String[] args) throws Exception {
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		String sandbox = "./PARALLEL_PROCESSING";
		String configfile = "testconfig.cfg";
		config.setPersistencePath(sandbox);
		/*
		config.setParam("DatabaseUserName", "gcube");
		config.setParam("DatabasePassword", "d4science2");
		config.setParam("DatabaseURL", "jdbc:postgresql://146.48.87.169/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
*/
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		
		config.setParam(BionymFlexibleWorkflowTransducer.destinationTableParam, "taxamatchoutputlocal");
		config.setParam(BionymFlexibleWorkflowTransducer.destinationTableLableParam, "taxamatchoutputlabel");
//		config.setParam(BionymTransducer.originTableParam, "taxamatchinput1000");
//		config.setParam(BionymTransducer.rawnamesColumnParam, "rawstrings");
		//4
//		config.setParam(BionymFlexibleWorkflowTransducer.originTableParam, "generic_id1ecb405c_980f_47a4_926a_3043d065fc7d");
//		config.setParam(BionymFlexibleWorkflowTransducer.rawnamesColumnParam, "field0");
		//2
		config.setParam(BionymFlexibleWorkflowTransducer.originTableParam, "generic_id471e6d50_d243_4112_bc07_e22152438e5c");
		config.setParam(BionymFlexibleWorkflowTransducer.rawnamesColumnParam, "field0");
				//FABIO DS:
//		config.setParam(BionymTransducer.originTableParam, "generic_ide43477df_d9e6_4191_8a81_e94a0a2d16f8");
//		config.setParam(BionymTransducer.rawnamesColumnParam, "field0");
		
		config.setParam(YasmeenGlobalParameters.parserNameParam,YasmeenGlobalParameters.BuiltinParsers.SIMPLE.name());
		config.setParam(YasmeenGlobalParameters.taxaAuthorityFileParam,YasmeenGlobalParameters.BuiltinDataSources.OBIS.name());
		config.setParam(YasmeenGlobalParameters.activatePreParsingProcessing,"true");
		config.setParam(YasmeenGlobalParameters.useStemmedGenusAndSpecies,"false");
		config.setParam(YasmeenGlobalParameters.overallMaxResults,"10");
		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		BufferedWriter oos = new BufferedWriter(new FileWriter(new File(sandbox, configfile)));
		oos.write(new XStream().toXML(config));
		oos.close();

		new BionymBiodiv().setup(config);

		new BionymBiodiv().executeNode(0, 1, 0, 2, false, sandbox, configfile, "test.log");
	}

}
