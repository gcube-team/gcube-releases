package org.gcube.dataanalysis.executor.nodes.transducers.bionym.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.BionymFlexibleWorkflowTransducer;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.utils.YasmeenGlobalParameters;

import com.thoughtworks.xstream.XStream;

public class TestBiOnymGenericWF {

	public static void main1(String[] args) throws Exception {
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		String sandbox = "./PARALLEL_PROCESSING";
		String configfile = "testconfig.cfg";
		config.setPersistencePath(sandbox);
		/*
		 * config.setParam("DatabaseUserName", "gcube"); config.setParam("DatabasePassword", "d4science2"); config.setParam("DatabaseURL", "jdbc:postgresql://146.48.87.169/testdb"); config.setParam("DatabaseDriver", "org.postgresql.Driver");
		 */
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");

		config.setParam(BionymFlexibleWorkflowTransducer.destinationTableParam, "taxamatchoutputlocal");
		config.setParam(BionymFlexibleWorkflowTransducer.destinationTableLableParam, "taxamatchoutputlabel");
		// 1000
		// config.setParam(BionymTransducer.originTableParam, "taxamatchinput1000");
		// config.setParam(BionymTransducer.rawnamesColumnParam, "rawstrings");
		config.setParam(BionymFlexibleWorkflowTransducer.originTableParam, "taxamatchinput");
		config.setParam(BionymFlexibleWorkflowTransducer.rawnamesColumnParam, "rawstrings");
		// 4
		// config.setParam(BionymFlexibleWorkflowTransducer.originTableParam, "generic_id1ecb405c_980f_47a4_926a_3043d065fc7d");
		// config.setParam(BionymFlexibleWorkflowTransducer.rawnamesColumnParam, "field0");
		// FABIO DS:
		// config.setParam(BionymTransducer.originTableParam, "generic_ide43477df_d9e6_4191_8a81_e94a0a2d16f8");
		// config.setParam(BionymTransducer.rawnamesColumnParam, "field0");

		config.setParam(YasmeenGlobalParameters.parserNameParam, YasmeenGlobalParameters.BuiltinParsers.SIMPLE.name());
		config.setParam(YasmeenGlobalParameters.taxaAuthorityFileParam, YasmeenGlobalParameters.BuiltinDataSources.FISHBASE.name());
		config.setParam(YasmeenGlobalParameters.activatePreParsingProcessing, "true");
		config.setParam(YasmeenGlobalParameters.useStemmedGenusAndSpecies, "false");
		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix + "_" + 1, YasmeenGlobalParameters.BuiltinMatchers.SOUNDEX.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix + "_" + 1, "0.2");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix + "_" + 1, "1");
		/*
		 * config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix+"_"+2,YasmeenGlobalParameters.BuiltinMatchers.LEVENSHTEIN.name()); config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix+"_"+2,"0.2"); config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix+"_"+2,"2");
		 */
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		BufferedWriter oos = new BufferedWriter(new FileWriter(new File(sandbox, configfile)));
		oos.write(new XStream().toXML(config));
		oos.close();

		new BionymFlexibleWorkflowTransducer().setup(config);

		new BionymFlexibleWorkflowTransducer().executeNode(0, 1, 0, 96, false, sandbox, configfile, "test.log");
	}

	public static void main2(String[] args) throws Exception {
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		String sandbox = "./PARALLEL_PROCESSING";
		String configfile = "testconfig.cfg";
		config.setPersistencePath(sandbox);
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");

		config.setParam(BionymFlexibleWorkflowTransducer.originTableParam, "bionymfaked2red2");
		config.setParam(BionymFlexibleWorkflowTransducer.rawnamesColumnParam, "sname");
		config.setParam(BionymFlexibleWorkflowTransducer.destinationTableParam, "bionymfaked2test2");
		config.setParam(BionymFlexibleWorkflowTransducer.destinationTableLableParam, "bionymfaked2test2");

		config.setParam(YasmeenGlobalParameters.parserNameParam, YasmeenGlobalParameters.BuiltinParsers.SIMPLE.name());
		config.setParam(YasmeenGlobalParameters.taxaAuthorityFileParam, YasmeenGlobalParameters.BuiltinDataSources.WORMS_PISCES.name());
		config.setParam(YasmeenGlobalParameters.activatePreParsingProcessing, "true");
		config.setParam(YasmeenGlobalParameters.useStemmedGenusAndSpecies, "false");
		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix + "_" + 1, YasmeenGlobalParameters.BuiltinMatchers.LEVENSHTEIN.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix + "_" + 1, "0.4");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix + "_" + 1, "10");
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		BufferedWriter oos = new BufferedWriter(new FileWriter(new File(sandbox, configfile)));
		oos.write(new XStream().toXML(config));
		oos.close();

		new BionymFlexibleWorkflowTransducer().setup(config);

		new BionymFlexibleWorkflowTransducer().executeNode(0, 1, 0, 63, false, sandbox, configfile, "test.log");

	}

	public static void main(String[] args) throws Exception {
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		String sandbox = "./PARALLEL_PROCESSING";
		String configfile = "testconfig.cfg";
		config.setPersistencePath(sandbox);
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");

		config.setParam(YasmeenGlobalParameters.parserNameParam, YasmeenGlobalParameters.BuiltinParsers.NONE.name());
		config.setParam(YasmeenGlobalParameters.taxaAuthorityFileParam, YasmeenGlobalParameters.BuiltinDataSources.WORMS_PISCES.name());
		config.setParam(YasmeenGlobalParameters.activatePreParsingProcessing, "true");
		config.setParam(YasmeenGlobalParameters.useStemmedGenusAndSpecies, "false");

		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix + "_" + 1, YasmeenGlobalParameters.BuiltinMatchers.GSAy.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix + "_" + 1, "0.6");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix + "_" + 1, "10");

		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix + "_" + 2, YasmeenGlobalParameters.BuiltinMatchers.FUZZYMATCH.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix + "_" + 2, "0.6");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix + "_" + 2, "10");

		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix + "_" + 3, YasmeenGlobalParameters.BuiltinMatchers.LEVENSHTEIN.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix + "_" + 3, "0.4");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix + "_" + 3, "10");

		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix + "_" + 4, YasmeenGlobalParameters.BuiltinMatchers.TRIGRAM.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix + "_" + 4, "0.4");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix + "_" + 4, "10");

		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		config.setAgent("BIONYM");
		config.setPersistencePath("./");
		// config.setGcubeScope( "/gcube");
		config.setGcubeScope("/d4science.research-infrastructures.eu");
		config.setParam("ServiceUserName", "gianpaolo.coro");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		// String directory = "C:\\Users\\coro\\Desktop\\DATABASE e NOTE\\Experiments\\BiOnym TaxaMatch\\BenchmarkTablesPrepared\\";
		String directory = "C:\\Users\\coro\\Desktop\\DATABASE e NOTE\\Experiments\\BiOnym TaxaMatch\\BenchmarkTablesPreparedReduced\\";
		File[] files = new File(directory).listFiles();
		int counter = 1;
		for (File file : files) {
			if (file.getName().endsWith(".prepr.csv")) {

				// if (file.getName().startsWith("real9.csv")){
				if (counter >= 5) {

					String tablename = "bionym" + file.getName().replace(".", "");
					System.out.println("Processing table " + tablename + " number:" + counter);
					String outputtablename = "bionymoutsimple" + file.getName().replace(".", "");
					config.setParam(BionymFlexibleWorkflowTransducer.originTableParam, tablename);
					config.setParam(BionymFlexibleWorkflowTransducer.rawnamesColumnParam, "sname");
					config.setParam(BionymFlexibleWorkflowTransducer.destinationTableParam, outputtablename);
					config.setParam(BionymFlexibleWorkflowTransducer.destinationTableLableParam, outputtablename + "label");

					BufferedWriter oos = new BufferedWriter(new FileWriter(new File(sandbox, configfile)));
					oos.write(new XStream().toXML(config));
					oos.close();

					new BionymFlexibleWorkflowTransducer().setup(config);

					new BionymFlexibleWorkflowTransducer().executeNode(0, 1, 0, 1024, false, sandbox, configfile, "test.log");

					System.out.println("STOP FOR A WHILE " + counter + " of " + files.length);
					Thread.sleep(1000);
					break;
				}
				counter++;
			}

		}
	}

}
