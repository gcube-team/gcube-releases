package org.gcube.dataanalysis.executor.nodes.transducers.bionym;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

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
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.executor.generators.D4ScienceDistributedProcessing;
import org.gcube.dataanalysis.executor.job.management.QueueJobManager;
import org.gcube.dataanalysis.executor.scripts.OSCommand;
import org.hibernate.SessionFactory;

import com.thoughtworks.xstream.XStream;

public class BionymWorkflow extends ActorNode {
	protected AlgorithmConfiguration currentconfig;
	protected SessionFactory dbconnection;
	private static String createOutputTable = "CREATE TABLE %1$s (inputname character varying(255),   suggestion character varying(255), score real)";
	int rawnamescount = 0;
	public int prevbroadcastTimePeriod;
	public int prevmaxNumberOfStages;
	public int prevmaxMessages;
	
	String destinationTable;
	public static String destinationTableParam = "OutputTable";
	public static String destinationTableLable = "OutputTableLabel";
	String originTable;
	public static String originTableParam = "RawTaxaNamesTable";
	String rawnamesColumn;
	public static String rawnamesColumnParam = "RawNamesColumn";
	String parser;
	public static String parserParam = "Parser";
	String reference;
	public static String referenceParam = "ReferenceDataset";
	String soundexweight;
	public static String soundexweightParam = "SoundexVSEditDist";
	String preprocessor;
	public static String doPreprocessParam = "Preprocess";
	float status;
	public static String maxMatchesParam= "MaxMatches";
	int maxMatches=10;
	public static String pruningThresholdParam= "PruningThreshold";
	float pruningThreshold=0.4f;
	
	static String headers = "inputname,suggestion,score";
	boolean haspostprocessed = false;
	
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
		PrimitiveType p4 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, destinationTableLable, "Name of the table which will contain the matches", "bionout");
		PrimitiveType p5 = new PrimitiveType(Enum.class.getName(), CometMatcherManager.Reference.values(), PrimitiveTypes.ENUMERATED, referenceParam, "The reference dataset to use", "" + CometMatcherManager.Reference.FISHBASE);
		PrimitiveType p6 = new PrimitiveType(Enum.class.getName(), EVBPreprocessing.Preprocessors.values(), PrimitiveTypes.ENUMERATED, doPreprocessParam, "Set a preprocessing approach for the raw strings", "" + EVBPreprocessing.Preprocessors.EXPERT_RULES);
		PrimitiveType p7 = new PrimitiveType(Enum.class.getName(), CometMatcherManager.Weights.values(), PrimitiveTypes.ENUMERATED, soundexweightParam, "Set the use of soundex vs edit distance approaches to string matching", "" + CometMatcherManager.Weights.EDIT_DISTANCE);
		PrimitiveType p8 = new PrimitiveType(Enum.class.getName(), CometMatcherManager.Parsers.values(), PrimitiveTypes.ENUMERATED, parserParam, "Set the genus-species-author parser to use", "" + CometMatcherManager.Parsers.SIMPLE);
		PrimitiveType p9 = new PrimitiveType(Float.class.getName(), null, PrimitiveTypes.NUMBER, pruningThresholdParam,"Pruning threshold for the output scores (from 0 to 1)","0.4");
		PrimitiveType p10 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, maxMatchesParam,"Maximum number of matches to report per raw string","10");
		
		
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
		parameters.add(p10);
		
		DatabaseType.addDefaultDBPars(parameters);

		return parameters;

	}

	@Override
	public StatisticalType getOutput() {
		List<TableTemplates> template = new ArrayList<TableTemplates>();
		template.add(TableTemplates.GENERIC);
		OutputTable p = new OutputTable(template, destinationTableLable, destinationTable, "Output  table");
		return p;
	}

	@Override
	public void initSingleNode(AlgorithmConfiguration config) {
		

	}

	@Override
	public float getInternalStatus() {

		return status;
	}

	List<Tuple<String>> matchedTuples = new ArrayList<Tuple<String>>();
	List<Tuple<String>> unmatchedTuples = new ArrayList<Tuple<String>>();
	
	public void filterMatchedTuples(List<Tuple<String>> tuples){
		unmatchedTuples = null; 
		unmatchedTuples = new ArrayList<Tuple<String>>();
		
		for (Tuple<String> tuple:tuples){
			double score = 0;
			if (tuple.getElements().size()>2){
				try{score = Double.parseDouble(tuple.getElements().get(2));}catch(Exception e){
					e.printStackTrace();
				}
			}
			if (score==1)
				matchedTuples.add(tuple);
			else
				unmatchedTuples.add(tuple);
		}

	}
	
	public StringBuffer executeBionymWorkflow(String rawspeciesname, String sandboxfolder, String preprocessor, boolean[] enablematchers, float soundexweightF, int maxResults, float pruningThreshold) throws Exception {
		StringBuffer sb = new StringBuffer();
		List<String> rawnames = new ArrayList<String>();
		rawnames.add(rawspeciesname);
		
		List<Tuple<String>> preprocessednames = new ArrayList<Tuple<String>>();
		
		// preprocessing
		switch (EVBPreprocessing.Preprocessors.valueOf(preprocessor)) {
		case EXPERT_RULES:
			EVBPreprocessing preprocess = new EVBPreprocessing();
			preprocessednames = preprocess.preprocess(parser, sandboxfolder, rawnames);
			break;
		default:
			preprocessednames = EVBPreprocessing.populateTuples(rawnames);
			break;
		}

		filterMatchedTuples(preprocessednames);
		
		for (int i = 0; i < enablematchers.length; i++) {
			// standard WF
			if (i == 0) {
				CometMatcherManager comet = new CometMatcherManager();
				List<Tuple<String>> cometoutput = comet.match(parser, reference, sandboxfolder, unmatchedTuples, soundexweightF,maxResults);
				filterMatchedTuples(cometoutput);
			}
		}

		//add all non exact matches 
		matchedTuples.addAll(unmatchedTuples);
		int msize = matchedTuples.size();
		
		if (msize>0) {
			
			for (int i = 0; i < msize; i++) {
				Tuple<String> t = matchedTuples.get(i);
				String scoreS = t.getElements().get(2);
				Float score  =  (scoreS !=null)? Float.parseFloat(scoreS):0;
				
				if (score>=pruningThreshold){
					String spname = t.getElements().get(0);
					String authorname = t.getElements().get(1);
					if (authorname.length()>0)
						spname +=" ("+authorname+")";
					
					sb.append("('" + rawspeciesname + "','" + spname + "','" + scoreS + "')");
					if (i < msize - 1)
						sb.append(",");
				}
			}
		}
		String sbstring = sb.toString().trim();
		int ssize = sbstring.length(); 
		
		if (sbstring.endsWith(",")){
			System.out.println("Deleting final comma..");
			sb= new StringBuffer(sbstring.substring(0,ssize-1));
		}
		return sb;
	}

	@Override
	public int executeNode(int leftStartIndex, int numberOfLeftElementsToProcess, int rightStartIndex, int numberOfRightElementsToProcess,boolean duplicate, String sandboxFolder, String nodeConfigurationFileObject, String logfileNameToProduce) {
		try {
			status = 0;
			long t0 = System.currentTimeMillis();
			// rebuild variables
			System.out.println("Restoring configuration");
			AlgorithmConfiguration config = Transformations.restoreConfig(new File(sandboxFolder, nodeConfigurationFileObject).getAbsolutePath());
			config.setConfigPath(sandboxFolder);
			dbconnection = DatabaseUtils.initDBSession(config);
			destinationTable = config.getParam(destinationTableParam);
			originTable = config.getParam(originTableParam);
			rawnamesColumn = config.getParam(rawnamesColumnParam);
			parser = config.getParam(parserParam);
			reference = config.getParam(referenceParam);
			soundexweight = config.getParam(soundexweightParam);
			preprocessor = config.getParam(doPreprocessParam);
			String maxMatchesS = config.getParam(maxMatchesParam);
			maxMatches= (maxMatchesS==null)?10:Integer.parseInt(maxMatchesS);
			String pruningThrS = config.getParam(pruningThresholdParam);
			pruningThreshold = (pruningThrS==null)?0.4f:Float.parseFloat(pruningThrS);
			
			System.out.println("Destination Table: " + destinationTable);
			System.out.println("Origin Table: " + originTable);
			System.out.println("Column of names: " + rawnamesColumn);
			System.out.println("Parser to use: " + parser);
			System.out.println("Reference Dataset: " + reference);
			System.out.println("Soundex Preference: " + soundexweight);
			System.out.println("Preprocessor:" + preprocessor);
			System.out.println("Pruning threshold:" + pruningThreshold);
			System.out.println("Number of Matches:" + maxMatches);
						
			float soundexweightF = 0.5f;
			switch (CometMatcherManager.Weights.valueOf(soundexweight)) {
			case SOUNDEX:
				soundexweightF = 1f;
				break;
			case EDIT_DISTANCE:
				soundexweightF = 0f;
				break;
			case MIXED:
				soundexweightF = 0.5f;
				break;
			default:
				soundexweightF = 0.5f;
				break;
			}
			
			// retrieve the list of names to process
			System.out.println("Retrieving names to process");
			List<Object> rawnames = DatabaseFactory.executeSQLQuery(DatabaseUtils.getDinstictElements(originTable, rawnamesColumn, ""), dbconnection);
			System.out.println("Retrieved a total of "+rawnames.size()+" species");
			
			int end = rightStartIndex + numberOfRightElementsToProcess;
			
			System.out.println("Processing from "+rightStartIndex+" to "+end);
			List<String> rawnamesFiltered = new ArrayList<String>();
			for (int i = rightStartIndex; i < end; i++) {
				rawnamesFiltered.add("" + rawnames.get(i));
			}
			// prepare the environment
			try {
//				OSCommand.ExecuteGetLine("chmod +x * | whoami", null);
				OSCommand.ExecuteGetLine("chmod +x *", null);
			} catch (Exception e) {
				System.out.println("WARNING: could not change the permissions");
			}
			int rawscounter = 0;
			System.out.println("Processing raw names");
			for (String rawname : rawnamesFiltered) {
				rawname  = rawname.replace("'", "").replace("\"", "");
				System.out.println("Processing species: "+rawname);
				StringBuffer sb = executeBionymWorkflow(rawname, sandboxFolder, preprocessor, new boolean[] { true }, soundexweightF,maxMatches,pruningThreshold);
				System.out.println("Processed species: "+rawname);
				if (sb.length() > 0) {
					rawscounter++;
					System.out.println("Inserting results onto the table "+destinationTable);
					String insertQuery = DatabaseUtils.insertFromBuffer(destinationTable, headers, sb);
					System.out.println("Insert Query: " + insertQuery);
					System.out.println("Inserting values for " + rawname);
					DatabaseFactory.executeSQLUpdate(insertQuery, dbconnection);
					System.out.println("Successfully Inserted values for " + rawname);
				}
//				Thread.sleep(10000);
			}

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
		originTable = config.getParam(originTableParam);
		rawnamesColumn = config.getParam(rawnamesColumnParam);
		
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
		D4ScienceDistributedProcessing.maxMessagesAllowedPerJob=1;
		prevbroadcastTimePeriod = QueueJobManager.broadcastTimePeriod;
		QueueJobManager.broadcastTimePeriod=4*3600000;
		prevmaxNumberOfStages = QueueJobManager.maxNumberOfStages;
		QueueJobManager.maxNumberOfStages=10000;
		
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
		QueueJobManager.broadcastTimePeriod=prevbroadcastTimePeriod;
		QueueJobManager.maxNumberOfStages=prevmaxNumberOfStages;
		D4ScienceDistributedProcessing.maxMessagesAllowedPerJob=prevmaxMessages;
		haspostprocessed = true;
	}

	
	public static void mainTEST(String[] args) throws Exception {
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
		
		config.setParam(BionymWorkflow.destinationTableParam, "taxamatchoutputlocal");
		config.setParam(BionymWorkflow.destinationTableLable, "taxamatchoutputlabel");
		config.setParam(BionymWorkflow.originTableParam, "taxamatchinput");
		config.setParam(BionymWorkflow.rawnamesColumnParam, "rawstrings");
		config.setParam(BionymWorkflow.parserParam, CometMatcherManager.Parsers.SIMPLE.name());
		config.setParam(BionymWorkflow.referenceParam, CometMatcherManager.Reference.ASFIS.name());
		config.setParam(BionymWorkflow.soundexweightParam, CometMatcherManager.Weights.EDIT_DISTANCE.name());
		config.setParam(BionymWorkflow.doPreprocessParam , EVBPreprocessing.Preprocessors.EXPERT_RULES.name());
		config.setParam(BionymWorkflow.maxMatchesParam , "10");
		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		BufferedWriter oos = new BufferedWriter(new FileWriter(new File(sandbox, configfile)));
		oos.write(new XStream().toXML(config));
		oos.close();

		new BionymWorkflow().setup(config);

//		new BionymWorkflow().executeNode(0, 1, 0, 915, false, sandbox, configfile, "test.log");
		new BionymWorkflow().executeNode(0, 1, 0, 1, false, sandbox, configfile, "test.log");
	}
	
}
