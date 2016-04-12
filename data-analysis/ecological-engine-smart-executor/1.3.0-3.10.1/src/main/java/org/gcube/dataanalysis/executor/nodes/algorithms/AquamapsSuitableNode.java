package org.gcube.dataanalysis.executor.nodes.algorithms;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.ActorNode;
import org.gcube.dataanalysis.ecoengine.spatialdistributions.AquamapsAlgorithmCore;
import org.gcube.dataanalysis.ecoengine.spatialdistributions.AquamapsSuitable;
import org.gcube.dataanalysis.ecoengine.user.GeneratorT;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.hibernate.SessionFactory;

public class AquamapsSuitableNode extends ActorNode {

	// variables
	protected String countSpeciesQuery, countCellsQuery, createTableStatement, destinationTable, destinationTableLabel, type;
	protected AquamapsAlgorithmCore core;
	protected AlgorithmConfiguration currentconfig;
	protected AquamapsSuitableFunctions operations;
	protected SessionFactory dbHibConnection;

	public AquamapsSuitableNode() {

	}

	// Processing
	// an initialization phase in which the input are initialized and then dumped to files
	@Override
	public void setup(AlgorithmConfiguration config) throws Exception {
		AnalysisLogger.getLogger().debug("Setup of the node algorithm for AquaMaps");
		// init algorithm
		long t00 = System.currentTimeMillis();
		initSingleNode(config);
		long t0 = System.currentTimeMillis();
		String distributionTable = config.getParam("DistributionTable");
		// System.out.println("Dumping information .. in " + config.getPersistencePath() + config.getModel() + "/");
		// operations.dumpAll(config.getPersistencePath() + config.getModel() + "/");
		// System.out.println(".. Dump Done in " + (System.currentTimeMillis() - t0) + " ms");
		// create table
		boolean createTable = config.getParam("CreateTable") != null ? Boolean.parseBoolean(config.getParam("CreateTable")) : false;
		AnalysisLogger.getLogger().debug("Creating table " + distributionTable + " " + createTable);
		if (config.getDatabaseDriver()==null)
			config.setDatabaseDriver("org.postgresql.Driver");
		if (createTable) {
			DatabaseUtils.createBigTable(createTable, distributionTable, config.getDatabaseDriver(), config.getDatabaseUserName(), config.getDatabasePassword(), config.getDatabaseURL(), createTableStatement, dbHibConnection);
		}
		//create minmaxlat table if necessary
		AnalysisLogger.getLogger().debug("Creating min max hspen");
		createMaxMinHspen(config);
		// close connection
		// DatabaseUtils.closeDBConnection(dbHibConnection);
		AnalysisLogger.getLogger().debug("Preparation phase finished in " + (System.currentTimeMillis() - t00) + " ms");

		/*
		 * databasecheckScheduler = new Timer(); databasecheckScheduler.schedule(new DatabaseController(), 0, refreshTime);
		 */
	}

	
	private void createMaxMinHspen(AlgorithmConfiguration config){
		
		String hspenMinMaxLat=""; 
		if ((config.getParam("PreprocessedTable")!=null)&&(config.getParam("PreprocessedTable").length()>0))
			hspenMinMaxLat = config.getParam("PreprocessedTable");
		
		//if not preprocessed then generate a preprocessed table
		if ((hspenMinMaxLat==null)||(hspenMinMaxLat.length()==0)){
			//take the name of the hspen table
			String hspenTable = config.getParam("EnvelopeTable");
			//check if the table exists
			String supposedminmaxlattable = "maxminlat_"+hspenTable;
			List<Object> select = null;
			try{
				select = DatabaseFactory.executeSQLQuery("select * from "+supposedminmaxlattable+" limit 1",dbHibConnection);
			}catch(Exception ee){}
			//if it exists then set the table name
			if (select!=null){
				hspenMinMaxLat = supposedminmaxlattable;
				AnalysisLogger.getLogger().debug("Aquamaps Algorithm Node Init ->the min max latitudes table yet exists "+hspenMinMaxLat);
			}
			else{
				//otherwise create it by calling the creator
				AnalysisLogger.getLogger().debug("Aquamaps Algorithm Node Init ->the min max latitudes table does not exist! - generating");
				hspenMinMaxLat = AquamapsSuitable.generateMaxMinHspec(supposedminmaxlattable, hspenTable,config.getParam("OccurrencePointsTable"), dbHibConnection);
				AnalysisLogger.getLogger().debug("Aquamaps Algorithm Node Init ->min max latitudes table created in "+hspenMinMaxLat);
			}
		}
		config.setParam("PreprocessedTable",hspenMinMaxLat);	
	}
	
	// initializations
	public void initSingleNode(AlgorithmConfiguration config) {
		initCore(config);
		// take all species records
		AnalysisLogger.getLogger().debug("Aquamaps Algorithm Init ->Taking all species records");
		operations.numberOfSpecies = Integer.parseInt("" + (DatabaseFactory.executeSQLQuery(countSpeciesQuery, dbHibConnection)).get(0));
		// take all csquares records
		AnalysisLogger.getLogger().debug("Aquamaps Algorithm Init ->Count all csquares");
		operations.numberOfCells = Integer.parseInt("" + DatabaseFactory.executeSQLQuery(countCellsQuery, dbHibConnection).get(0));
		// take all csquares records
		/*
		 * System.out.println("Aquamaps Algorithm ->Taking csquares"); String CellsQuery = String.format(AquamapsSuitableFunctions.csquareCodeQuery, currentconfig.getParam("CsquarecodesTable"), "" + operations.numberOfCells, "0"); operations.environmentVectors = DatabaseFactory.executeSQLQuery(CellsQuery, dbHibConnection);
		 */
		AnalysisLogger.getLogger().debug("Aquamaps Algorithm Init ->init finished");
	}

	private void initCore(AlgorithmConfiguration config) {
		currentconfig = config;
		AnalysisLogger.getLogger().debug("Aquamaps Algorithm-> Initializing DB connection");
		dbHibConnection = DatabaseUtils.initDBSession(config);
		AnalysisLogger.getLogger().debug("Aquamaps Algorithm-> Initializing DB connection OK");

		countSpeciesQuery = String.format(AquamapsSuitableFunctions.countAllSpeciesQuery, config.getParam("EnvelopeTable"));
		countCellsQuery = String.format(AquamapsSuitableFunctions.countCsquareCodeQuery, config.getParam("CsquarecodesTable"));

		createTableStatement = String.format(AquamapsSuitableFunctions.createTableStatement, config.getParam("DistributionTable"));
		if (config.getTableSpace() != null) {
			createTableStatement = createTableStatement.replace("#TABLESPACE#", "TABLESPACE " + config.getTableSpace());
		} else
			createTableStatement = createTableStatement.replace("#TABLESPACE#", "");

		destinationTable = config.getParam("DistributionTable");
		destinationTableLabel = config.getParam("DistributionTableLabel");
		AnalysisLogger.getLogger().debug("Aquamaps Algorithm-> Initializing Core Algorithm");
		core = new AquamapsAlgorithmCore();
		operations = new AquamapsSuitableFunctions(core, type, config);
		AnalysisLogger.getLogger().debug("Aquamaps Algorithm-> Core Initializiation OK ");
	}

	// end initialization

	// the core execution of a node
	@Override
	public int executeNode(int cellOrdinal, int chunksize, int speciesOrdinal, int speciesChunkSize, boolean duplicate, String pathToFiles, String nodeConfigurationFileObject, String logfile) {
		long t00 = System.currentTimeMillis();
		try {
			System.out.println("Aquamaps Algorithm: " + cellOrdinal + " Node Started");
			// rebuild all files
			core = new AquamapsAlgorithmCore();
			operations = new AquamapsSuitableFunctions(new AquamapsAlgorithmCore(), type, null);
			System.out.println("Aquamaps Algorithm: " + cellOrdinal + " Rebuilding objects");
			long t0 = System.currentTimeMillis();
			operations.rebuildConfig(new File(pathToFiles, nodeConfigurationFileObject).getAbsolutePath());
			// operations.rebuildAll(cellOrdinal, chunksize, speciesOrdinal, speciesChunkSize, pathToFiles);
			operations.currentconfig.setConfigPath(pathToFiles);
			currentconfig = operations.currentconfig;
			System.out.println("Aquamaps Algorithm: " + speciesOrdinal + " Objects Rebuilt in " + (System.currentTimeMillis() - t0) + " ms");
			String SpeciesQuery = String.format(AquamapsSuitableFunctions.selectAllSpeciesQuery, currentconfig.getParam("EnvelopeTable"), "" + speciesChunkSize, "" + speciesOrdinal);
			String CellsQuery = String.format(AquamapsSuitableFunctions.csquareCodeQuery, currentconfig.getParam("CsquarecodesTable"), "" + chunksize, "" + cellOrdinal);
			System.out.println("Aquamaps Algorithm-> Initializing DB connection with parameters: " + currentconfig.getConfigPath() + AlgorithmConfiguration.defaultConnectionFile + " " + currentconfig.getParam("DatabaseDriver") + " " + currentconfig.getParam("DatabaseUserName") + " " + currentconfig.getParam("DatabasePassword") + " " + currentconfig.getParam("DatabaseURL"));
			System.out.println("Aquamaps Algorithm-> File Exists: " + new File(currentconfig.getConfigPath() + AlgorithmConfiguration.defaultConnectionFile).exists());
			System.out.println("Aquamaps Algorithm-> File IS READABLE: " + new File(currentconfig.getConfigPath() + AlgorithmConfiguration.defaultConnectionFile).canRead());
			dbHibConnection = DatabaseUtils.initDBSession(currentconfig);

			System.out.println("Aquamaps Algorithm-> Building Species Observations Max-Min Latitutes");
			operations.allSpeciesObservations = new HashMap<String, List<Object>>();
			String hspenMinMaxLat = currentconfig.getParam("PreprocessedTable");
			System.out.println("Aquamaps Algorithm-> Preprocessed Table is " + hspenMinMaxLat);
			System.out.println("Aquamaps Algorithm-> Query: " + String.format(AquamapsSuitableFunctions.selectAllSpeciesObservationQuery, hspenMinMaxLat));
			System.out.println("Aquamaps Algorithm-> DB CONNECTION " + dbHibConnection);
			List<Object> SpeciesObservations = DatabaseFactory.executeSQLQuery(String.format(AquamapsSuitableFunctions.selectAllSpeciesObservationQuery, hspenMinMaxLat), dbHibConnection);
			System.out.println("Aquamaps Algorithm-> Queried");
			if (SpeciesObservations == null)
				SpeciesObservations = new ArrayList<Object>();
			System.out.println("Aquamaps Algorithm-> Obtained Observations " + SpeciesObservations.size());

			int lenObservations = SpeciesObservations.size();
			for (int i = 0; i < lenObservations; i++) {
				Object[] maxminArray = (Object[]) SpeciesObservations.get(i);
				String speciesid = (String) maxminArray[0];
				List<Object> maxminInfo = new ArrayList<Object>();
				maxminInfo.add(maxminArray);
				operations.allSpeciesObservations.put((String) speciesid, maxminInfo);
			}

			// take all species records
			System.out.println("Aquamaps Algorithm ->Taking species records");
			operations.speciesVectors = DatabaseFactory.executeSQLQuery(SpeciesQuery, dbHibConnection);
			// take all csquares records
			System.out.println("Aquamaps Algorithm ->Taking csquares");
			if (operations.environmentVectors == null)
				operations.environmentVectors = DatabaseFactory.executeSQLQuery(CellsQuery, dbHibConnection);

			int maxSpecies = operations.speciesVectors.size();
			int maxCells = operations.environmentVectors.size();

			System.out.println("Aquamaps Algorithm ->Processing " + maxCells + " cells VS " + maxSpecies + " species");

			System.out.println("Aquamaps Algorithm ->Focus On Table: " + currentconfig.getParam("DistributionTable"));
			for (int j = 0; j < maxSpecies; j++) {
				System.out.println("Aquamaps Algorithm: calculating elements from " + cellOrdinal + " to " + maxCells + " for species " + j);
				Object speciesV = operations.speciesVectors.get(j);
				System.out.println("Aquamaps Algorithm: " + cellOrdinal + " calculating probabilities");
				singleStepPreprocess(speciesV, duplicate);
				for (int i = 0; i < maxCells; i++) {
					Object area = operations.environmentVectors.get(i);
					operations.calcProb(speciesV, area);
				}

				System.out.println("Aquamaps Algorithm: " + cellOrdinal + " probabilities calculated!");
				System.out.println("Aquamaps Algorithm: " + cellOrdinal + " writing on DB");
				singleStepPostprocess(speciesV);
				System.out.println("Aquamaps Algorithm: " + cellOrdinal + " write on DB OK");
			}

			

			try {
				FileWriter fw = new FileWriter(new File(logfile));
				fw.write("OK");
				fw.close();
			} catch (Exception e) {

			}
		} catch (Exception e) {
			System.err.println("ERROR " + e);
			e.printStackTrace();
		}
		finally{
			
			DatabaseUtils.closeDBConnection(dbHibConnection);
			System.out.println("Aquamaps Algorithm: " + cellOrdinal + " Database Closed!");
			
		}
		System.out.println("Aquamaps Algorithm: " + cellOrdinal + " Node Ended in " + ((float) (System.currentTimeMillis() - t00) / 1000f) + " s");
		return 0;
	}

	// Auxiliary Functions
	// to overwrite in case of native generation in order to filer on the probabilities types
	@Override
	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = new AquamapsSuitable().getInputParameters();
		return parameters;
	}
	
	public Queue<String> filterProbabilitySet(Queue<String> probabiltyRows) {
		return probabiltyRows;
	}

	public float getInternalStatus() {
		return 100;
	}

	public ALG_PROPS[] getProperties() {
		ALG_PROPS[] p = { ALG_PROPS.PHENOMENON_VS_PARALLEL_PHENOMENON};
		return p;
	}

	public String getName() {
		return "AQUAMAPS_SUITABLE";
	}

	public String getDescription() {
		return "Algorithm by Aquamaps on a single node";
	}

	public void postProcess(boolean manageDuplicates, boolean manageFault) {
		if (manageFault) {
			try {
				DatabaseFactory.executeSQLUpdate("drop table " + currentconfig.getParam("DistributionTable"), dbHibConnection);
			} catch (Exception e) {

			}
		}
	}

	public static void main2(String[] args) throws Exception {
		String configPath = "./cfg/";
		String csquareTable = "hcaf_d";
		String preprocessedTable = "maxminlat_hspen";
		String envelopeTable = "hspen";
		int numberOfResources = 1;
		String userName = "gianpaolo.coro";
		String generatorName = "AQUAMAPS_SUITABLE";
		String scope = "/gcube";
		String finalDistributionTable = "hspec_suitable_executor_all1";
		// String finalDistributionTable = "hspec2012_04_11_18_41_01_575";
		AlgorithmConfiguration config = GeneratorT.getGenerationConfig(numberOfResources, generatorName, envelopeTable, preprocessedTable, "", userName, csquareTable, finalDistributionTable, configPath);
		config.setPersistencePath("./");
		config.setGcubeScope(scope);
		config.setParam("ServiceUserName", "gianpaolo.coro");
		config.setParam("CreateTable", "false");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdated");
		config.setParam("DatabaseDriver", "org.hibernate.dialect.PostgreSQLDialect");
		config.setTableSpace("tbsp_1");
		AquamapsSuitableNode node = new AquamapsSuitableNode();
		node.setup(config);
		node.postProcess(true,false);

	}

	// pre process: add default min and max information
	public void singleStepPreprocess(Object species, boolean duplicate) throws Exception {

		List<Object> speciesObservations = operations.allSpeciesObservations.get(AquamapsSuitableFunctions.getMainInfoID(species));

		if (((speciesObservations == null) || speciesObservations.size() == 0)) {
			Object[] defaultmaxmin = { "90", "-90" };
			speciesObservations = new ArrayList<Object>();
			speciesObservations.add(defaultmaxmin);
		}

		String speciesID = AquamapsSuitableFunctions.getMainInfoID(species);
		// if this is a duplicate message, cancel all associate information to this species
		if (duplicate) {
			System.out.println("Deleting Duplicates for species " + speciesID);
			long t0 = System.currentTimeMillis();
			DatabaseFactory.executeSQLUpdate(String.format(AquamapsSuitableFunctions.deleteDuplicates, currentconfig.getParam("DistributionTable"), speciesID), dbHibConnection);
			System.out.println("Duplicates deleted in " + (System.currentTimeMillis() - t0));
		}

		operations.getBoundingBoxInformation((Object[]) species, (Object[]) speciesObservations.get(0));
	}

	// writes the distribution model on the DB: input species vector + list of areas vectors to report
	public void singleStepPostprocess(Object species) {
		// write info on DB
		List<String> toWrite = new ArrayList<String>();
		String speciesID = AquamapsSuitableFunctions.getMainInfoID(species);
		Map<String, Float> csquaresMap = operations.completeDistribution.get(speciesID);
		if (csquaresMap != null) {
			// write only processed areas
			for (String singleCsquare : csquaresMap.keySet()) {

				String additionalInformation = operations.getAdditionalInformation(species, operations.processedAreas.get(singleCsquare));
				if (additionalInformation == null)
					additionalInformation = "";
				else if (additionalInformation.length() > 0)
					additionalInformation = "," + additionalInformation.trim();

				float prob = 0f;
				try {
					prob = csquaresMap.get(singleCsquare);
				} catch (Exception e) {
					System.out.println("Aquamaps Algorithm Single Step PostProcess ->Error in getting probability value at " + speciesID + " , " + singleCsquare);
				}
				if (prob > 0)
					toWrite.add("'" + speciesID + "','" + singleCsquare + "','" + MathFunctions.roundDecimal(prob, 3) + "'" + additionalInformation);
			}

			AquamapsSuitableFunctions.writeOnDB(toWrite, currentconfig.getParam("DistributionTable"), dbHibConnection);
		}

	}

	@Override
	public StatisticalType getOutput() {
		List<TableTemplates> templateHspec = new ArrayList<TableTemplates>();
		templateHspec.add(TableTemplates.HSPEC);
		OutputTable p = new OutputTable(templateHspec,destinationTableLabel,destinationTable,"Output hspec table");
		return p;
	}
	

	@Override
	public int getNumberOfRightElements() {
		return operations.numberOfSpecies;
	}

	@Override
	public int getNumberOfLeftElements() {
		return operations.numberOfCells;
	}


	@Override
	public void stop() {
		DatabaseUtils.closeDBConnection(dbHibConnection);
	}

}
