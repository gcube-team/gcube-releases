package org.gcube.dataanalysis.executor.nodes.transducers.bionym;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.ActorNode;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.Transformations;
import org.gcube.dataanalysis.executor.generators.D4ScienceDistributedProcessing;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.MatcherOutput;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.SingleEntry;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.matchers.FuzzyMatcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.matchers.GsayMatcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.matchers.LevensteinMatcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.matchers.MixedLexicalMatcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.matchers.SoundexMatcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.matchers.TrigramMatcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.workflows.BiOnymWF;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.interfaces.Matcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.utils.YasmeenGlobalParameters;
import org.gcube.dataanalysis.executor.scripts.OSCommand;
import org.hibernate.SessionFactory;

public class BionymFlexibleWorkflowTransducer extends ActorNode {
	//SM parameters
	protected AlgorithmConfiguration currentconfig;
	protected SessionFactory dbconnection;
	boolean haspostprocessed = false;
	float status;
	public int prevbroadcastTimePeriod;
	public int prevmaxNumberOfStages;
	public int prevmaxMessages;
	String destinationTable;
	String destinationTableLabel;
	
	//Table
	public static String headers = "SOURCE_DATA,TARGET_DATA_SCIENTIFIC_NAME,TARGET_DATA_AUTHORITY,MATCHING_SCORE,TARGET_DATA_SOURCE,TARGET_DATA_ID";
	private static String createOutputTable = "CREATE TABLE %1$s (SOURCE_DATA character varying(255),  TARGET_DATA_SCIENTIFIC_NAME character varying(255), TARGET_DATA_AUTHORITY character varying(255), MATCHING_SCORE real,TARGET_DATA_SOURCE character varying, TARGET_DATA_ID character varying)";
	public int rawnamescount = 0;

	//Algorithm
	public static String destinationTableParam = "OutputTable";
	public static String destinationTableLableParam = "OutputTableLabel";
	public static String originTableParam = "RawTaxaNamesTable";
	public static String rawnamesColumnParam = "RawNamesColumn";
	public static String matcherParamPrefix = "Matcher";
	public static String thresholdParamPrefix = "Threshold";
	public static String maxresultsParamPrefix = "MaxResults";
	public static int maxMatchersInterface = 5;		
	public static int maxMatchersReal = 20;
	
	public static Matcher getEnum2Matcher(YasmeenGlobalParameters.BuiltinMatchers matcher, String sandboxFolder, double threshold, int maxResults, HashMap<String,String> parameters){
		
		switch(matcher){
		case GSAy:
			return new GsayMatcher(sandboxFolder, threshold, maxResults,parameters);
		case FUZZYMATCH:
			return new FuzzyMatcher(sandboxFolder, threshold, maxResults,parameters);
		case LEVENSHTEIN:
			return new LevensteinMatcher(sandboxFolder, threshold, maxResults,parameters);
		case TRIGRAM:
			return new TrigramMatcher(sandboxFolder, threshold, maxResults,parameters);
		case SOUNDEX:
			return new SoundexMatcher(sandboxFolder, threshold, maxResults,parameters);
		case LEV_SDX_TRIG:
			return new MixedLexicalMatcher(sandboxFolder, threshold, maxResults,parameters);
		case NONE:
			return null;
		default:
			return null;
		}
		
	}
	
	public List<Matcher> buildMatcherList(AlgorithmConfiguration config, String sandboxFolder, HashMap<String,String> globalparameters){
		//rebuild the matchers
		List<Matcher> matchers = new ArrayList<Matcher>();
		for (int i=1;i<=maxMatchersReal;i++){
			String matchername = config.getParam(matcherParamPrefix+"_"+i);
			String threshold = config.getParam(thresholdParamPrefix+"_"+i);
			String maxResultS = config.getParam(maxresultsParamPrefix+"_"+i);
			if (matchername!=null){
				AnalysisLogger.getLogger().debug("Matcher "+i+" name: "+matchername);
				AnalysisLogger.getLogger().debug("Matcher "+i+" threshold: "+threshold);
				AnalysisLogger.getLogger().debug("Matcher "+i+" max results: "+maxResultS);
				double thr = 0.2;
				if (threshold!=null)
					thr = Double.parseDouble(threshold);
				int maxResults = 10;
				if (maxResultS!=null)
					maxResults = Integer.parseInt(maxResultS);
		
				Matcher m = getEnum2Matcher(YasmeenGlobalParameters.BuiltinMatchers.valueOf(matchername),sandboxFolder,thr,maxResults, globalparameters);
				if (m!=null)
					matchers.add(m);
			}
		}
		return matchers;
	}
	
	@Override
	public ALG_PROPS[] getProperties() {
		ALG_PROPS[] p = { ALG_PROPS.PHENOMENON_VS_PARALLEL_PHENOMENON };
		return p;
	}

	@Override
	public String getName() {
		return "BIONYM";
	}

	@Override
	public String getDescription() {
		return "An algorithm implementing BiOnym, a flexible workflow approach to taxon name matching. The workflow allows to activate several taxa names matching algorithms and to get the list of possible transcriptions for a list of input raw species names with possible authorship indication.";
	}

	
	
	@Override
	public List<StatisticalType> getInputParameters() {
		List<TableTemplates> templateLWRInput = new ArrayList<TableTemplates>();
		templateLWRInput.add(TableTemplates.GENERIC);
		
		InputTable p1 = new InputTable(templateLWRInput, originTableParam, "Input table containing raw taxa names that you want to match", "byonym");
		ColumnType p2 = new ColumnType(originTableParam, rawnamesColumnParam, "The column containing the raw taxa names with or without authoship information", "rawnames", false);
		ServiceType p3 = new ServiceType(ServiceParameters.RANDOMSTRING, destinationTableParam, "name of the table that will contain the matches", "bion_");
		PrimitiveType p4 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, destinationTableLableParam, "Name of the table which will contain the matches", "bionout");
		
		PrimitiveType p5 = new PrimitiveType(Enum.class.getName(), YasmeenGlobalParameters.BuiltinDataSources.values(), PrimitiveTypes.ENUMERATED, YasmeenGlobalParameters.taxaAuthorityFileParam, "The reference dataset to use", "" +YasmeenGlobalParameters.BuiltinDataSources.FISHBASE);
		PrimitiveType p6 = new PrimitiveType(Enum.class.getName(), YasmeenGlobalParameters.BuiltinParsers.values(), PrimitiveTypes.ENUMERATED, YasmeenGlobalParameters.parserNameParam, "The Species - Authority parser", "" + YasmeenGlobalParameters.BuiltinParsers.SIMPLE);
		PrimitiveType p7 = new PrimitiveType(Boolean.class.getName(), null, PrimitiveTypes.BOOLEAN, YasmeenGlobalParameters.activatePreParsingProcessing,"Use preparsing rules to correct common errors","true");
		PrimitiveType p8 = new PrimitiveType(Boolean.class.getName(), null, PrimitiveTypes.BOOLEAN, YasmeenGlobalParameters.useStemmedGenusAndSpecies,"Process using Genus and Species names without declension","false");
		PrimitiveType p9 = new PrimitiveType(Enum.class.getName(), YasmeenGlobalParameters.Performance.values(), PrimitiveTypes.ENUMERATED, YasmeenGlobalParameters.performanceParam, "A trade-off between recognition speed and accuracy. Max speed corresponds to search for strings with the same length only.", "" + YasmeenGlobalParameters.Performance.MAX_ACCURACY);
		
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		parameters.add(p1);
		parameters.add(p3);
		parameters.add(p2);
		parameters.add(p4);
		parameters.add(p5);
		parameters.add(p6);
		parameters.add(p7);
		parameters.add(p8);
		parameters.add(p9);
		
		PrimitiveType pgsay = new PrimitiveType(Enum.class.getName(), YasmeenGlobalParameters.BuiltinMatchers.values(), PrimitiveTypes.ENUMERATED, matcherParamPrefix+"_"+1, "Choose a Matcher", YasmeenGlobalParameters.BuiltinMatchers.GSAy.name());
		PrimitiveType pthrgsay = new PrimitiveType(Double.class.getName(), null, PrimitiveTypes.NUMBER, thresholdParamPrefix+"_"+1 ,"Threshold","0.6",true);
		PrimitiveType pmrgsay = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, maxresultsParamPrefix+"_"+1,"The maximum number of matching candidates per each raw input species","10");

		PrimitiveType ptaxa = new PrimitiveType(Enum.class.getName(), YasmeenGlobalParameters.BuiltinMatchers.values(), PrimitiveTypes.ENUMERATED, matcherParamPrefix+"_"+2, "Choose a Matcher", YasmeenGlobalParameters.BuiltinMatchers.FUZZYMATCH.name());
		PrimitiveType pthrtaxa = new PrimitiveType(Double.class.getName(), null, PrimitiveTypes.NUMBER, thresholdParamPrefix+"_"+2 ,"Threshold","0.6",true);
		PrimitiveType pmrtaxa = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, maxresultsParamPrefix+"_"+2,"The maximum number of matching candidates per each raw input species","10");
		
		PrimitiveType plev = new PrimitiveType(Enum.class.getName(), YasmeenGlobalParameters.BuiltinMatchers.values(), PrimitiveTypes.ENUMERATED, matcherParamPrefix+"_"+3, "Choose a Matcher", YasmeenGlobalParameters.BuiltinMatchers.LEVENSHTEIN.name());
		PrimitiveType pthrlev = new PrimitiveType(Double.class.getName(), null, PrimitiveTypes.NUMBER, thresholdParamPrefix+"_"+3 ,"Threshold","0.4",true);
		PrimitiveType pmrlev = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, maxresultsParamPrefix+"_"+3,"The maximum number of matching candidates per each raw input species","5");
		
		PrimitiveType ptrig = new PrimitiveType(Enum.class.getName(), YasmeenGlobalParameters.BuiltinMatchers.values(), PrimitiveTypes.ENUMERATED, matcherParamPrefix+"_"+4, "Choose a Matcher", YasmeenGlobalParameters.BuiltinMatchers.TRIGRAM.name());
		PrimitiveType pthrtrig = new PrimitiveType(Double.class.getName(), null, PrimitiveTypes.NUMBER, thresholdParamPrefix+"_"+4 ,"Threshold","0.4");
		PrimitiveType pmrtrig = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, maxresultsParamPrefix+"_"+4,"The maximum number of matching candidates per each raw input species","5");
		
		parameters.add(pgsay);
		parameters.add(pthrgsay);
		parameters.add(pmrgsay);
		
		parameters.add(ptaxa);
		parameters.add(pthrtaxa);
		parameters.add(pmrtaxa);
		
		parameters.add(plev);
		parameters.add(pthrlev);
		parameters.add(pmrlev);
		
		parameters.add(ptrig);
		parameters.add(pthrtrig);
		parameters.add(pmrtrig);
		
		for (int i=5;i<=maxMatchersInterface;i++){
			PrimitiveType p = new PrimitiveType(Enum.class.getName(), YasmeenGlobalParameters.BuiltinMatchers.values(), PrimitiveTypes.ENUMERATED, matcherParamPrefix+"_"+i, "Choose a Matcher (Optional)", YasmeenGlobalParameters.BuiltinMatchers.NONE.name(),true);
			PrimitiveType pn = new PrimitiveType(Double.class.getName(), null, PrimitiveTypes.NUMBER, thresholdParamPrefix+"_"+i ,"Threshold (def. 0.2)","0.2",true);
			PrimitiveType pr = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, maxresultsParamPrefix+"_"+i,"The maximum number of matching candidates per each raw input species","0");
			parameters.add(p);
			parameters.add(pn);
			parameters.add(pr);
		}
				
		DatabaseType.addDefaultDBPars(parameters);

		return parameters;

	}

	@Override
	public StatisticalType getOutput() {
		List<TableTemplates> template = new ArrayList<TableTemplates>();
		template.add(TableTemplates.GENERIC);
		OutputTable p = new OutputTable(template, destinationTableLabel, destinationTable, "Output  table");
		return p;
	}

	@Override
	public void initSingleNode(AlgorithmConfiguration config) {
	}

	@Override
	public float getInternalStatus() {
		return status;
	}

	@Override
	public int executeNode(int leftStartIndex, int numberOfLeftElementsToProcess, int rightStartIndex, int numberOfRightElementsToProcess,boolean duplicate, String sandboxFolder, String nodeConfigurationFileObject, String logfileNameToProduce) {
		String uuid = (""+UUID.randomUUID()).replace("-", "");
		String inputParserFile = "inputParser"+uuid+".txt";
		String outputParserFile = "outputParser"+uuid+".txt";
		try {
			status = 0;
			long t0 = System.currentTimeMillis();
			// rebuild variables
			System.out.println("Restoring configuration");
			AlgorithmConfiguration config = Transformations.restoreConfig(new File(sandboxFolder, nodeConfigurationFileObject).getAbsolutePath());
			config.setConfigPath(sandboxFolder);
			dbconnection = DatabaseUtils.initDBSession(config);
			
			String destinationTable = config.getParam(destinationTableParam);
			String originTable = config.getParam(originTableParam);
			String rawnamesColumn = config.getParam(rawnamesColumnParam);
			
			String parser = config.getParam(YasmeenGlobalParameters.parserNameParam);
			String accuracyvsspeed = config.getParam(YasmeenGlobalParameters.performanceParam);
			String reference = config.getParam(YasmeenGlobalParameters.taxaAuthorityFileParam);
			String doPreprocess =config.getParam(YasmeenGlobalParameters.activatePreParsingProcessing);
			String usestemming= config.getParam(YasmeenGlobalParameters.useStemmedGenusAndSpecies);
			String overallMaxResults= config.getParam(YasmeenGlobalParameters.overallMaxResults);
			
			
			System.out.println("Destination Table: " + destinationTable);
			System.out.println("Origin Table: " + originTable);
			System.out.println("Column of names: " + rawnamesColumn);
			System.out.println("Parser to use: " + parser);
			System.out.println("Accuracy vs Speed: " + accuracyvsspeed);
			System.out.println("Reference Dataset: " + reference);
			System.out.println("Do Preprocessing: " + doPreprocess);
			System.out.println("Use Stemming:" + usestemming);
			System.out.println("Overall MaxResults:" + overallMaxResults);
			
			//prepare the WF
			HashMap<String,String> globalparameters = new HashMap<String, String>();
			
			globalparameters.put(YasmeenGlobalParameters.parserInputFileParam, inputParserFile);
			globalparameters.put(YasmeenGlobalParameters.parserOutputFileParam, outputParserFile);
			globalparameters.put(YasmeenGlobalParameters.activatePreParsingProcessing, doPreprocess);
			globalparameters.put(YasmeenGlobalParameters.parserNameParam, parser);
			globalparameters.put(YasmeenGlobalParameters.performanceParam, accuracyvsspeed);
			globalparameters.put(YasmeenGlobalParameters.taxaAuthorityFileParam, reference);
			globalparameters.put(YasmeenGlobalParameters.useStemmedGenusAndSpecies, usestemming);
			System.out.println("Configuration Restored! - Time: "+(System.currentTimeMillis()-t0));
			
			// retrieve the list of names to process
			long t1 = System.currentTimeMillis();
			System.out.println("Retrieving names to process");
			String query = DatabaseUtils.getDinstictElements(originTable, rawnamesColumn, "")+" offset "+rightStartIndex+" limit "+numberOfRightElementsToProcess;
			List<Object> rawnames = DatabaseFactory.executeSQLQuery(query, dbconnection);
			System.out.println("Retrieved a total of "+rawnames.size()+" species");
			//modification due to the limit and offset on the query
			rightStartIndex = 0;
			
			int end = rightStartIndex + numberOfRightElementsToProcess;
			
			System.out.println("Processing from "+rightStartIndex+" to "+end);
			List<String> rawnamesFiltered = new ArrayList<String>();
			for (int i = rightStartIndex; i < end; i++) {
				String raw = "" + rawnames.get(i);
//				System.out.println("Taking species:"+raw);
				rawnamesFiltered.add(raw.replaceAll("^'", "").replaceAll("'$", ""));
			}
			int rawscounter = rawnamesFiltered.size();
			System.out.println("Retrieve from DB - Time: "+(System.currentTimeMillis()-t1));
			
			long t2 = System.currentTimeMillis();
			System.out.println("Processing " + rawscounter + " species..");
			// prepare the environment
			try {
				OSCommand.ExecuteGetLine("chmod +x *", null);
			} catch (Exception e) {
				System.out.println("WARNING: could not change the permissions");
			}
			
			int overallMR = 10;
			if (overallMaxResults!=null)
				overallMR=Integer.parseInt(overallMaxResults);
			
			BiOnymWF bionym = new BiOnymWF(sandboxFolder, overallMR, globalparameters);
			
			//rebuild the matchers
			List<Matcher> matchers = buildMatcherList(config, sandboxFolder, globalparameters);
			if (matchers!=null)
				bionym.resetMatchers(matchers);
			 System.out.println("WF Initialization - Time: "+(System.currentTimeMillis()-t2));
			 
			MatcherOutput output = bionym.executeChainedWorkflow(rawnamesFiltered);
			System.out.println("Workflow Executed");
			long t3 = System.currentTimeMillis();
			int nEntries = output.getEntriesNumber();
			List<String[]> toWrite = new ArrayList<String[]>(); 
			for (int i=0;i<nEntries;i++){
				SingleEntry se = output.getEntry(i);
				//"SOURCE_DATA,TARGET_DATA_SCIENTIFIC_NAME,TARGET_DATA_AUTHORITY,MATCHING_SCORE,TARGET_DATA_SOURCE,TARGET_DATA_ID";
				String[] srow = new String[6];
				srow[0] = se.originalName;
				srow[1] = se.targetScientificName;
				srow[2] = se.targetAuthor;
				srow[3] = ""+se.matchingScore;
				srow[4] = reference;
				srow[5] = se.targetID;
				toWrite.add(srow);
			}
			//write on DB
			DatabaseUtils.insertChunksIntoTable(destinationTable, headers, toWrite, 5000, dbconnection);
			System.out.println("Write on DB - Time: "+(System.currentTimeMillis()-t3));
			
			System.out.println("The procedure finished successfully. Processed " + rawscounter + " species.");
			System.out.println("Elapsed Time " + (System.currentTimeMillis() - t0) + " ms");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("warning: error in node execution " + e.getLocalizedMessage());
			System.err.println("Error in node execution " + e.getLocalizedMessage());
			return -1;
		} finally {
			if (dbconnection != null)
				try {
					dbconnection.close();
				} catch (Exception e) {
				}
			
			try {
				new File(sandboxFolder,inputParserFile).delete();
				new File(sandboxFolder,outputParserFile).delete();
			} catch (Exception e) {
			}
			
			status = 1f;
		}

		return 0;
	}

	@Override
	public void setup(AlgorithmConfiguration config) throws Exception {
		haspostprocessed = false;
		AnalysisLogger.getLogger().info("Initializing DB Connection");
		dbconnection = DatabaseUtils.initDBSession(config);
		destinationTable = config.getParam(destinationTableParam);
		destinationTableLabel = config.getParam(destinationTableLableParam);
		String originTable = config.getParam(originTableParam);
		String rawnamesColumn = config.getParam(rawnamesColumnParam);
		List<Object> rawnames = DatabaseFactory.executeSQLQuery(DatabaseUtils.getDinstictElements(originTable, rawnamesColumn, ""), dbconnection);
		
		rawnamescount = rawnames.size();
//		rawnamescount =1;
		
		AnalysisLogger.getLogger().info("Creating Destination Table " + destinationTable);
		try {
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(destinationTable), dbconnection);
		} catch (Exception e) {
			AnalysisLogger.getLogger().info("Table " + destinationTable + " did not exist");
		}

		DatabaseFactory.executeSQLUpdate(String.format(createOutputTable, destinationTable), dbconnection);

		prevmaxMessages=D4ScienceDistributedProcessing.maxMessagesAllowedPerJob;
		D4ScienceDistributedProcessing.maxMessagesAllowedPerJob=50;
		
		AnalysisLogger.getLogger().info("Destination Table Created! Addressing " + rawnamescount + " names");
	}

	@Override
	public int getNumberOfRightElements() {
		return rawnamescount;
	}

	@Override
	public int getNumberOfLeftElements() {
		return 1;
	}

	@Override
	public void stop() {
		// if has not postprocessed, then abort the computations by removing the database table
		if (!haspostprocessed) {
			try {
				AnalysisLogger.getLogger().info("The procedure did NOT correctly postprocessed ....Removing Table " + destinationTable + " because of computation stop!");
				DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(destinationTable), dbconnection);
			} catch (Exception e) {
				AnalysisLogger.getLogger().info("Table " + destinationTable + " did not exist");
			}
		} else
			AnalysisLogger.getLogger().info("The procedure has correctly postprocessed: shutting down the connection!");
		if (dbconnection != null)
			try {
				dbconnection.close();
			} catch (Exception e) {
			}
	}

	@Override
	public void postProcess(boolean manageDuplicates, boolean manageFault) {
		D4ScienceDistributedProcessing.maxMessagesAllowedPerJob=prevmaxMessages;
		haspostprocessed = true;
	}

	
	
	
}
