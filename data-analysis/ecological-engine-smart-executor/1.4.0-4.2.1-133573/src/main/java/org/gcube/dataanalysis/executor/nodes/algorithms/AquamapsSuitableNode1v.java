package org.gcube.dataanalysis.executor.nodes.algorithms;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.SpatialProbabilityDistributionNode;
import org.gcube.dataanalysis.ecoengine.spatialdistributions.AquamapsAlgorithmCore;
import org.gcube.dataanalysis.ecoengine.spatialdistributions.AquamapsSuitable;
import org.gcube.dataanalysis.ecoengine.user.GeneratorT;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.Operations;
import org.hibernate.SessionFactory;

public class AquamapsSuitableNode1v extends SpatialProbabilityDistributionNode {

	// variables
	protected String countSpeciesQuery, countCellsQuery, createTableStatement, destinationTable, destinationTableLabel, type;
	protected AquamapsAlgorithmCore core;
	protected AlgorithmConfiguration currentconfig;
	protected AquamapsSuitableFunctions operations;
	protected SessionFactory dbHibConnection;

	public AquamapsSuitableNode1v() {

	}

	// Processing
	// an initialization phase in which the input are initialized and then dumped to files
	@Override
	public void setup(AlgorithmConfiguration config) throws Exception {
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
		System.out.println("Creating table " + distributionTable + " " + createTable);
		if (createTable) {
			try {
				DatabaseUtils.dropTableStatement(distributionTable);
			} catch (Exception e) {
			}
			DatabaseUtils.createBigTable(createTable, distributionTable, config.getDatabaseDriver(), config.getDatabaseUserName(), config.getDatabasePassword(), config.getDatabaseURL(), createTableStatement, dbHibConnection);
		}
		//create minmaxlat table if necessary
		createMaxMinHspen(config);
		// close connection
		// DatabaseUtils.closeDBConnection(dbHibConnection);
		System.out.println("Preparation phase finished in " + (System.currentTimeMillis() - t00) + " ms");

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
				System.out.println("Aquamaps Algorithm Node Init ->the min max latitudes table yet exists "+hspenMinMaxLat);
			}
			else{
				//otherwise create it by calling the creator
				System.out.println("Aquamaps Algorithm Node Init ->the min max latitudes table does not exist! - generating");
				hspenMinMaxLat = AquamapsSuitable.generateMaxMinHspec(supposedminmaxlattable, hspenTable,config.getParam("OccurrencePointsTable"), dbHibConnection);
				System.out.println("Aquamaps Algorithm Node Init ->min max latitudes table created in "+hspenMinMaxLat);
			}
		}
		config.setParam("PreprocessedTable",hspenMinMaxLat);	
	}
	
	// initializations
	public void initSingleNode(AlgorithmConfiguration config) {
		initCore(config);
		// take all species records
		System.out.println("Aquamaps Algorithm Init ->Taking all species records");
		operations.numberOfSpecies = Integer.parseInt("" + (DatabaseFactory.executeSQLQuery(countSpeciesQuery, dbHibConnection)).get(0));
		// take all csquares records
		System.out.println("Aquamaps Algorithm Init ->Count all csquares");
		operations.numberOfCells = Integer.parseInt("" + DatabaseFactory.executeSQLQuery(countCellsQuery, dbHibConnection).get(0));
		// take all csquares records
		/*
		 * System.out.println("Aquamaps Algorithm ->Taking csquares"); String CellsQuery = String.format(AquamapsSuitableFunctions.csquareCodeQuery, currentconfig.getParam("CsquarecodesTable"), "" + operations.numberOfCells, "0"); operations.environmentVectors = DatabaseFactory.executeSQLQuery(CellsQuery, dbHibConnection);
		 */
		System.out.println("Aquamaps Algorithm Init ->init finished");
	}

	private void initCore(AlgorithmConfiguration config) {
		currentconfig = config;
		System.out.println("Aquamaps Algorithm-> Initializing DB connection");
		dbHibConnection = DatabaseUtils.initDBSession(config);
		System.out.println("Aquamaps Algorithm-> Initializing DB connection OK");

		countSpeciesQuery = String.format(AquamapsSuitableFunctions.countAllSpeciesQuery, config.getParam("EnvelopeTable"));
		countCellsQuery = String.format(AquamapsSuitableFunctions.countCsquareCodeQuery, config.getParam("CsquarecodesTable"));

		createTableStatement = String.format(AquamapsSuitableFunctions.createTableStatement, config.getParam("DistributionTable"));
		if (config.getTableSpace() != null) {
			createTableStatement = createTableStatement.replace("#TABLESPACE#", "TABLESPACE " + config.getTableSpace());
		} else
			createTableStatement = createTableStatement.replace("#TABLESPACE#", "");

		destinationTable = config.getParam("DistributionTable");
		destinationTableLabel = config.getParam("DistributionTableLabel");
		System.out.println("Aquamaps Algorithm-> Initializing Core Algorithm");
		core = new AquamapsAlgorithmCore();
		operations = new AquamapsSuitableFunctions(core, type, config);
		System.out.println("Aquamaps Algorithm-> Core Initializiation OK ");
	}

	// end initialization

	// the core execution of a node
	@Override
	public int executeNode(int cellOrdinal, int chunksize, int speciesOrdinal, int speciesChunkSize, boolean duplicate, String pathToFiles, String nodeConfigurationFileObject, String logfile) {
		long t00 = System.currentTimeMillis();
		try {
			System.out.println("Aquamaps Suitable: " + cellOrdinal + " Node Started");
			// rebuild all files
			core = new AquamapsAlgorithmCore();
			operations = new AquamapsSuitableFunctions(new AquamapsAlgorithmCore(), type, null);
			System.out.println("Aquamaps Suitable: " + cellOrdinal + " Rebuilding objects");
			long t0 = System.currentTimeMillis();
			operations.rebuildConfig(new File(pathToFiles, nodeConfigurationFileObject).getAbsolutePath());
			// operations.rebuildAll(cellOrdinal, chunksize, speciesOrdinal, speciesChunkSize, pathToFiles);
			operations.currentconfig.setConfigPath(pathToFiles);
			currentconfig = operations.currentconfig;
			System.out.println("Aquamaps Suitable: " + speciesOrdinal + " Objects Rebuilt in " + (System.currentTimeMillis() - t0) + " ms");
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
				System.out.println("Aquamaps Suitable: calculating elements from " + cellOrdinal + " to " + maxCells + " for species " + j);
				Object speciesV = operations.speciesVectors.get(j);
				System.out.println("Aquamaps Suitable: " + cellOrdinal + " calculating probabilities");
				singleStepPreprocess(speciesV, duplicate);
				for (int i = 0; i < maxCells; i++) {
					Object area = operations.environmentVectors.get(i);
					operations.calcProb(speciesV, area);
				}

				System.out.println("Aquamaps Suitable: " + cellOrdinal + " probabilities calculated!");
				System.out.println("Aquamaps Suitable: " + cellOrdinal + " writing on DB");
				singleStepPostprocess(speciesV);
				System.out.println("Aquamaps Suitable: " + cellOrdinal + " write on DB OK");
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
			System.out.println("Aquamaps Suitable: " + cellOrdinal + " Database Closed!");
			
		}
		System.out.println("Aquamaps Suitable: " + cellOrdinal + " Node Ended in " + ((float) (System.currentTimeMillis() - t00) / 1000f) + " s");
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
		ALG_PROPS[] p = { ALG_PROPS.PARALLEL_SPECIES_VS_CSQUARE_FROM_DATABASE};
		return p;
	}

	public String getName() {
		return "AQUAMAPS_SUITABLE";
	}

	public String getDescription() {
		return "Algorithm by Aquamaps on a single node";
	}

	public void postProcess(boolean manageDuplicates, boolean manageFault) {
		if (databasecheckScheduler != null)
			databasecheckScheduler.cancel();
		if (manageFault) {
			try {
				DatabaseFactory.executeSQLUpdate("drop table " + currentconfig.getParam("DistributionTable"), dbHibConnection);
			} catch (Exception e) {

			}
		}
	}

	public void postProcess4(boolean manageDuplicates) {
		if (databasecheckScheduler != null)
			databasecheckScheduler.cancel();

		if (manageDuplicates) {
			try {

				long t0 = System.currentTimeMillis();
				System.out.println("Deleting duplicates ... this can require very much time");
				String distributionTable = currentconfig.getParam("DistributionTable");
				String countQuery = String.format("select count(*) from %1$s ", distributionTable);

				System.out.println("Taking number of elements ... ");
				long t00 = System.currentTimeMillis();
				/*
				 * List<Object> explain = DatabaseFactory.executeSQLQuery(String.format(AquamapsSuitableFunctions.countAll, currentconfig.getParam("DistributionTable")), dbHibConnection); String explained = "" + explain.get(0); explained = explained.substring(explained.lastIndexOf("rows=")); explained = explained.substring(explained.indexOf('=') + 1, explained.indexOf(' ')); int nElements = (int )(Integer.parseInt(explained)*1.5);
				 */
				int nElements = Integer.parseInt("" + (DatabaseFactory.executeSQLQuery(countQuery, dbHibConnection)).get(0));
				System.out.println("Calculated " + nElements + " elements in " + (System.currentTimeMillis() - t00));
				int maxRounds = 100;
				int[] chunkSizes = Operations.takeChunks(nElements, maxRounds);
				// String selectChunk = "select speciesid,csquarecode, '' || ctid as ctidstring from %1$s limit %2$s offset %3$s";
				String selectChunk = "select speciesid,csquarecode, CAST( ctid AS text ) from %1$s limit %2$s offset %3$s";
				int offset = 0;
				HashMap<String, String> mapSpecies = new HashMap<String, String>();
				List<String> duplicates = new ArrayList<String>();
				t00 = System.currentTimeMillis();
				for (int i = 0; i < maxRounds; i++) {
					// for (int i = 0; i < 5; i++) {
					String query = String.format(selectChunk, distributionTable, "" + chunkSizes[i], "" + offset);
					offset += chunkSizes[i];
					System.out.println("Selecting " + chunkSizes[i] + " from " + offset + " : " + query);
					long t01 = System.currentTimeMillis();
					List<Object> objs = DatabaseFactory.executeSQLQuery(query, dbHibConnection);
					System.out.println("Selected in " + (System.currentTimeMillis() - t01));
					for (Object rowO : objs) {
						Object[] row = (Object[]) rowO;
						String species = "" + row[0];
						String csquareCode = "" + row[1];
						String ctid = "" + row[2];
						String key = species + ":" + csquareCode;
						if (mapSpecies.containsKey(key)) {
							duplicates.add(ctid);
						} else
							mapSpecies.put(key, ctid);
					}
					System.out.println("Status " + ((float) i / (float) maxRounds) * 100f);
				}
				System.out.println("Finished map calculation in " + (System.currentTimeMillis() - t00));

				t00 = System.currentTimeMillis();
				StringBuffer toDelete = new StringBuffer();
				toDelete.append("delete * from %1$s where ctid in (");
				int size = duplicates.size();
				if (size > 0) {
					System.out.println("Building Deleting Statement ...");
					for (int j = 0; j < size; j++) {
						toDelete.append(duplicates.get(j));
						if (j != size - 1) {
							toDelete.append(",");
						}
					}

					toDelete.append(")");
					System.out.println("Finished Building Deleting Statement in " + (System.currentTimeMillis() - t00));

					System.out.println("Deleting ...");
					t00 = System.currentTimeMillis();
					DatabaseFactory.executeSQLUpdate(toDelete.toString(), dbHibConnection);
					System.out.println("... Deleted in " + (System.currentTimeMillis() - t00));
				} else
					System.out.println("No Duplications to delete");

				System.out.println("Deleted All duplicates in " + (System.currentTimeMillis() - t0));
			} catch (Exception e) {
				System.out.println("An Error Occurred");
				e.printStackTrace();
			}

		}

	}

	public void postProcess1(boolean manageDuplicates) {
		if (databasecheckScheduler != null)
			databasecheckScheduler.cancel();

		if (manageDuplicates) {
			long t0 = System.currentTimeMillis();
			System.out.println("Deleting duplicates ... this can require very much time");
			String distributionTable = currentconfig.getParam("DistributionTable");
			// String deletecommand = String.format("delete from %1$s where exists ( select  * from %1$s i where i.speciesid = %1$s.speciesid and i.csquarecode = %1$s.csquarecode and i.ctid < %1$s.ctid)", currentconfig.getParam("DistributionTable"));
			String deletecommand = String.format("select * into testtable from %1$s where exists (select  * from %1$s i where i.speciesid = %1$s.speciesid and i.csquarecode = %1$s.csquarecode and i.ctid < %1$s.ctid)", currentconfig.getParam("DistributionTable"));
			// DELETE FROM %1$s WHERE ctid NOT IN(SELECT MAX(s.ctid) FROM %1$s s GROUP BY (s.speciesid,s.csquarecode))",

			/*
			 * String countQuery = String.format("select count(*) from %1$s ",distributionTable); int nElements = Integer.parseInt(""+(DatabaseFactory.executeSQLQuery(countQuery, dbHibConnection)).get(0)); int maxRounds = 100;
			 * 
			 * int[] chunkSizes = Operations.takeChunks(nElements, 100); String selectChunk = "select speciesid,csquarecode,ctid from %1$s limit %2$s offset %3$s"; int offset = 0; HashMap<String,String> mapSpecies = new HashMap<String, String>(); List<String> duplicates = new ArrayList<String>(); for (int i=0;i<maxRounds;i++) { String query = String.format(selectChunk, distributionTable,""+chunkSizes[i],""+offset); offset += chunkSizes[i]; List<Object> objs = DatabaseFactory.executeSQLQuery(query, dbHibConnection); for (Object rowO:objs) { Object[] row = (Object[]) rowO; String species = ""+row[0]; String csquareCode = ""+row[1]; String ctid = ""+row[3]; String key = species+":"+csquareCode; if (mapSpecies.containsKey(key)){ duplicates.add(ctid); } else mapSpecies.put(key, ctid); } }
			 * 
			 * StringBuffer toDelete = new StringBuffer(); toDelete.append("delete * from %1$s where ctid in ("); int size = duplicates.size(); for (int j=0;j<size;j++){ toDelete.append(duplicates.get(j)); if (j!=size-1){ toDelete.append(","); } }
			 * 
			 * toDelete.append(")");
			 */

			try {
				// DatabaseFactory.executeSQLUpdate(deletecommand, dbHibConnection);
				DatabaseFactory.executeSQLQuery(deletecommand, dbHibConnection);
				System.out.println("Deleted duplicates in " + (System.currentTimeMillis() - t0));
			} catch (Exception e) {
				System.out.println("An Error Occurred");
				e.printStackTrace();
			}

		}

	}

	public void postProcess2(boolean manageDuplicates) {
		if (databasecheckScheduler != null)
			databasecheckScheduler.cancel();

		if (manageDuplicates) {
			long t0 = System.currentTimeMillis();
			System.out.println("Deleting duplicates ... this can require very much time");
			String deletecommand = String.format("select * from %1$s where exists (select  * from %1$s i where i.speciesid = %1$s.speciesid and i.csquarecode = %1$s.csquarecode and i.ctid < %1$s.ctid)", currentconfig.getParam("DistributionTable"));

			try {
				DatabaseFactory.executeSQLQuery(deletecommand, dbHibConnection);
				System.out.println("Deleted duplicates in " + (System.currentTimeMillis() - t0));
			} catch (Exception e) {
				System.out.println("An Error Occurred");
				e.printStackTrace();
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
		AquamapsSuitableNode1v node = new AquamapsSuitableNode1v();
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
					toWrite.add("'" + speciesID + "','" + singleCsquare + "','" + MathFunctions.roundDecimal(prob, 2) + "'" + additionalInformation);
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
	public int getNumberOfSpecies() {
		return operations.numberOfSpecies;
	}

	@Override
	public int getNumberOfGeoInfo() {
		return operations.numberOfCells;
	}

	private int lastProcessed;
	Timer databasecheckScheduler;
	private static int refreshTime = 10000;

	@Override
	public int getNumberOfProcessedSpecies() {
		return lastProcessed;
	}

	private class DatabaseController extends TimerTask {

		@Override
		public void run() {
			try {
				List<Object> explain = DatabaseFactory.executeSQLQuery(String.format(AquamapsSuitableFunctions.countAll, currentconfig.getParam("DistributionTable")), dbHibConnection);
				String explained = "" + explain.get(0);
				explained = explained.substring(explained.lastIndexOf("rows="));
				explained = explained.substring(explained.indexOf('=') + 1, explained.indexOf(' '));
				AnalysisLogger.getLogger().debug("ESTIMATED ROWS: " + explained);
				lastProcessed = Integer.parseInt(explained);
			} catch (Exception e) {
				try {
					e.printStackTrace();
					/*
					 * List<Object> all = DatabaseFactory.executeSQLQuery(String.format(AquamapsSuitableFunctions.countAll, currentconfig.getParam("DistributionTable")), dbHibConnection); int numberOfElements = Integer.parseInt("" + all.get(0)); lastProcessed = numberOfElements;
					 */
				} catch (Exception e2) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void stop() {
		try{
		databasecheckScheduler.cancel();
		}catch(Exception e){}
		DatabaseUtils.closeDBConnection(dbHibConnection);
	}

}
