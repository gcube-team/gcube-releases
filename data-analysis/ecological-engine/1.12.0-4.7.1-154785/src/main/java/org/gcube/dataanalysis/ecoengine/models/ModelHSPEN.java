package org.gcube.dataanalysis.ecoengine.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.gcube.contentmanagement.graphtools.utils.HttpRequest;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.connectors.livemonitor.ResourceLoad;
import org.gcube.dataanalysis.ecoengine.connectors.livemonitor.Resources;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.DatabaseParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.Model;
import org.gcube.dataanalysis.ecoengine.models.cores.aquamaps.AquamapsEnvelopeAlgorithm;
import org.gcube.dataanalysis.ecoengine.models.cores.aquamaps.EnvelopeSet;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.hibernate.SessionFactory;

public class ModelHSPEN implements Model {

	private float version;
	// DB SESSION
	protected SessionFactory connection;
	// Queries
	private static final String alterQuery = "UPDATE %HSPEN% SET %1$s WHERE speciesid = '%2$s'";
	private static final String dropHspenTable = "DROP TABLE %HSPEN%; ";
	private static final String createHspenTable = "CREATE TABLE %HSPEN% ( speccode integer, speciesid character varying NOT NULL, lifestage character varying NOT NULL, faoareas character varying(100), faoareasref character varying, faocomplete smallint, nmostlat real, smostlat real, wmostlong real,emostlong real, lme character varying(180), depthyn smallint, depthmin integer, depthmax integer,  depthprefmin integer,  depthprefmax integer,  meandepth smallint,  depthref character varying,  pelagic smallint,  tempyn smallint,  tempmin real,  tempmax real,  tempprefmin real,  tempprefmax real,  tempref character varying,  salinityyn smallint,  salinitymin real,  salinitymax real,  salinityprefmin real,  salinityprefmax real,  salinityref character varying,  primprodyn smallint,  primprodmin real,  primprodmax real,  primprodprefmin real,  primprodprefmax real,  primprodprefref character varying,  iceconyn smallint,  iceconmin real,  iceconmax real,  iceconprefmin real,  iceconprefmax real,  iceconref character varying,  landdistyn smallint,  landdistmin real,  landdistmax real,  landdistprefmin real,  landdistprefmax real,  landdistref character varying,  remark character varying,  datecreated timestamp without time zone,  datemodified timestamp without time zone,  expert integer,  dateexpert timestamp without time zone,  envelope smallint,  mapdata smallint,  effort smallint,  layer character(1),  usepoints smallint,  rank smallint,  CONSTRAINT %HSPEN%_pkey PRIMARY KEY (speciesid, lifestage))WITH (  OIDS=FALSE); CREATE INDEX envelope_%HSPEN%_idx   ON %HSPEN%  USING btree  (envelope); CREATE INDEX mapdata_%HSPEN%_idx  ON %HSPEN%  USING btree  (mapdata); CREATE INDEX speciesid_%HSPEN%_idx  ON %HSPEN%  USING btree  (speciesid);";
	private static final String populateNewHspen = "insert into %HSPEN% (select * from %HSPEN_ORIGIN%);";
	private static final String speciesListQuery = "select distinct speciesid from %HSPEN%;";
	private static final String hspenListQuery = "select speciesid, layer, iceconmin , iceconmax , iceconprefmin , iceconprefmax , salinitymin , salinitymax , salinityprefmin , salinityprefmax , landdistmin , landdistmax , landdistprefmin , landdistprefmax , tempmin , tempmax , tempprefmin , tempprefmax ,  primprodmin ,  primprodmax ,  primprodprefmin ,  primprodprefmax  from %HSPEN%;";

	// constants
	String defaultDatabaseFile = "DestinationDBHibernate.cfg.xml";
	String defaultLogFile = "ALog.properties";
	private String dynamicAlterQuery;
	private String dynamicDropTable;
	private String dynamicCreateTable;
	private String dynamicPopulateNewHspen;
	private String dynamicSpeciesListQuery;
	private String dynamicHspenInformationQuery;
	private String currentHCAFTable;
	private String currentOccurrenceTable;
	private int numberOfthreads;
	private ExecutorService executorService;
	private boolean threadActivity[];
	private int countDifferences;
	private boolean interruptProcessing;
	private float status;
	private int numbOfProcessedSpecies;
	HashMap<String, List<Object>> allSpeciesHspen;
	private int lastProcessedRecordsNumber;
	private long lastTime;
	AlgorithmConfiguration outconfig;
	private String outputTable;
	private String outputTableLabel;

	@Override
	public float getVersion() {
		return version;
	}

	@Override
	public String getName() {
		return "HSPEN";
	}
	
	
	@Override
	public void init(AlgorithmConfiguration setup, Model previousModel) {
		outconfig = setup;
		defaultDatabaseFile = setup.getConfigPath() + defaultDatabaseFile;
	
		AnalysisLogger.setLogger(setup.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		try {
			String defaultDatabaseFile = setup.getConfigPath() + AlgorithmConfiguration.defaultConnectionFile;
			
			setup.setDatabaseDriver(setup.getParam("DatabaseDriver"));
			setup.setDatabaseUserName(setup.getParam("DatabaseUserName"));
			setup.setDatabasePassword(setup.getParam("DatabasePassword"));
			setup.setDatabaseURL(setup.getParam("DatabaseURL"));
			
			connection = DatabaseFactory.initDBConnection(defaultDatabaseFile, setup);
		} catch (Exception e) {
			AnalysisLogger.getLogger().error("error intitializing",e);
		}
		
		outputTable = outconfig.getParam("OuputEnvelopeTable");
		outputTableLabel = outconfig.getParam("OuputEnvelopeTableLabel");
		// initialize queries
		dynamicAlterQuery = alterQuery.replace("%HSPEN%", outconfig.getParam("OuputEnvelopeTable"));
		dynamicDropTable = dropHspenTable.replace("%HSPEN%", outconfig.getParam("OuputEnvelopeTable"));
		dynamicCreateTable = createHspenTable.replace("%HSPEN%", outconfig.getParam("OuputEnvelopeTable"));
		dynamicPopulateNewHspen = populateNewHspen.replace("%HSPEN_ORIGIN%", outconfig.getParam("EnvelopeTable")).replace("%HSPEN%", outconfig.getParam("OuputEnvelopeTable"));
		dynamicSpeciesListQuery = speciesListQuery.replace("%HSPEN%", outconfig.getParam("EnvelopeTable"));
		dynamicHspenInformationQuery = hspenListQuery.replace("%HSPEN%", outconfig.getParam("EnvelopeTable"));
		currentHCAFTable = outconfig.getParam("CsquarecodesTable");
		currentOccurrenceTable = outconfig.getParam("OccurrenceCellsTable");

		// Threads
		numberOfthreads = outconfig.getNumberOfResources();

		// interrupt process
		interruptProcessing = false;
		status = 0;
	}

	// populates the selectedSpecies variable by getting species from db
	private List<Object> populateSpecies() {
		AnalysisLogger.getLogger().trace("Distribution Generator ->getting all species list from DB");
		List<Object> allspecies = DatabaseFactory.executeSQLQuery(dynamicSpeciesListQuery, connection);
		return allspecies;
	}

	private HashMap<String, List<Object>> populateHspen() {
		HashMap<String, List<Object>> allSpeciesHspen = new HashMap<String, List<Object>>();
		List<Object> SpeciesInfo = DatabaseFactory.executeSQLQuery(dynamicHspenInformationQuery, connection);
		int lenSpecies = SpeciesInfo.size();

		for (int i = 0; i < lenSpecies; i++) {
			Object[] speciesArray = (Object[]) SpeciesInfo.get(i);
			String speciesid = (String) speciesArray[0];
			List<Object> singleSpeciesInfo = new ArrayList<Object>();
			singleSpeciesInfo.add(speciesArray);
			allSpeciesHspen.put((String) speciesid, singleSpeciesInfo);
		}

		return allSpeciesHspen;
	}

	// initializes threads activity status
	public void initializeThreads(int numberOfThreadsToUse) {
		// initialize threads and their activity state
		executorService = Executors.newFixedThreadPool(numberOfThreadsToUse);

		threadActivity = new boolean[numberOfThreadsToUse];
		// initialize to false;
		for (int j = 0; j < threadActivity.length; j++) {
			threadActivity[j] = false;
		}

	}

	// waits for thread to be free
	private void wait4Thread(int index) {

		// wait until thread is free
		while (threadActivity[index]) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}

	// shutdown the connection
	public void shutdownConnection() {
		connection.close();
	}

	private void generateTable(Object Input) throws Exception {
		AlgorithmConfiguration config = (AlgorithmConfiguration) Input;
		// create and populate the novel table
		if (config.getParam("CreateTable").equalsIgnoreCase("true")){
			AnalysisLogger.getLogger().trace("Distribution Generator->recreating new table " + dynamicCreateTable);
			try{
				DatabaseFactory.executeSQLUpdate(String.format(dynamicDropTable, config.getDatabaseUserName()), connection);	
			}catch(Exception e){
				AnalysisLogger.getLogger().trace("Impossible to drop table - maybe not existing");
			}
			try{
				DatabaseFactory.executeSQLUpdate(String.format(dynamicCreateTable, config.getDatabaseUserName()), connection);
			}catch(Exception e){
				AnalysisLogger.getLogger().trace("Impossible to create table - maybe yet existing");
			}
			
		}
		AnalysisLogger.getLogger().trace("Distribution Generator->populating new table " + dynamicPopulateNewHspen);
		DatabaseFactory.executeSQLUpdate(dynamicPopulateNewHspen, connection);
	}

	@Override
	public void train(AlgorithmConfiguration Input, Model previousModel) {
		long tstart = System.currentTimeMillis();
		// INITIALIZATION
		try {
			AnalysisLogger.getLogger().trace("ModelHSPENr->populating species");
			List<Object> allspecies = populateSpecies();
			allSpeciesHspen = populateHspen();

			AnalysisLogger.getLogger().trace("ModelHSPENr->ENVELOPES GENERATION STARTED");

			// initialize threads
			initializeThreads(numberOfthreads);
			// END INITIALIZATION
			// generate the hspen table
			generateTable(Input);
			// thread selection index
			int currentThread = 0;
			// global chunks counter
			int globalcounter = 0;
			// count differences in hspen original and new hspen
			countDifferences = 0;
			// take time
			long computationT0 = System.currentTimeMillis();
			int numberOfSpecies = allspecies.size();

			// ENVELOPES CALCULATION
			// cycle throw the species to generate
			// one thread calculation for each species
			for (Object species : allspecies) {
				// get speciesID
				String speciesid = (String) species;
				if (speciesid.length() > 0) {
					// calculation on multiple threads
					AnalysisLogger.getLogger().trace("ModelHSPENr->ANALIZING SPECIES: " + speciesid);
					// wait for thread to be free
					wait4Thread(currentThread);
					// start species information calculation on the thread
					startNewTCalc(currentThread, speciesid);
					// increment thread selection index
					currentThread++;
					// reset current thread index
					if (currentThread >= numberOfthreads)
						currentThread = 0;
					// report probability
					float s = (float) ((int) (((float) globalcounter * 100f / (numberOfSpecies)) * 100f)) / 100f;
					status = (s == 100) ? 99 : s;
					AnalysisLogger.getLogger().trace("STATUS->" + status + "%");

					// increment global counter index
					globalcounter++;
					AnalysisLogger.getLogger().warn("Number of Found Differences: " + countDifferences);
				}

				if (interruptProcessing)
					break;
			}

			// END OF CALCULATION CORE

			// wait for last threads to finish
			for (int i = 0; i < numberOfthreads; i++) {
				// free previous calculation
				wait4Thread(i);
			}

			long computationT1 = System.currentTimeMillis();
			AnalysisLogger.getLogger().warn("All Envelopes Computation Finished in " + (computationT1 - computationT0) + " ms");
			AnalysisLogger.getLogger().warn("Number of Overall Found Differences: " + countDifferences);
		} catch (Exception e) {
			AnalysisLogger.getLogger().trace("Computation traminate prematurely: ", e);
		} finally {
			// shutdown threads
			executorService.shutdown();
			// shutdown connection
			shutdownConnection();
			// set completeness
			status = 100.0f;
			long tstop = System.currentTimeMillis();
			AnalysisLogger.getLogger().warn("All Envelopes Computation Finished in " + (tstop - tstart) + " ms");
		}
	}

	// THREAD SECTION
	// definition of the Thread
	// calculates values for one species
	private class ThreadCalculator implements Callable<Integer> {
		int index;
		String species;

		public ThreadCalculator(int index, String species) {
			this.species = species;
			this.index = index;
		}

		public Integer call() {

			try {
				calcEnvelopes(species);
			} catch (Exception e) {
				AnalysisLogger.getLogger().trace("" + e);
				e.printStackTrace();
			}
			threadActivity[index] = false;
			return 0;
		}
	}

	// end Definition of the Thread
	// activation
	private void startNewTCalc(int index, String species) {
		threadActivity[index] = true;
		ThreadCalculator tc = new ThreadCalculator(index, species);
		executorService.submit(tc);
	}

	// END OF THREAD SECTION

	// calculation for standalone mode
	public void calcEnvelopes(String species) {
		// take initial time
		long t0 = System.currentTimeMillis();
		try {
			// take information for the selected Species
			List<Object> singleHspen = allSpeciesHspen.get(species);
			// call all envelopes calculations
			EnvelopeSet envSet = AquamapsEnvelopeAlgorithm.calculateEnvelopes(species, connection, currentOccurrenceTable, currentHCAFTable, (Object[]) singleHspen.get(0));
			String instruction = envSet.getEnvelopeString();
			// take the result of the calculation
			long t1 = System.currentTimeMillis();
			AnalysisLogger.getLogger().trace("Computation for species " + species + " finished in " + (t1 - t0) + " ms");

			if (instruction.length() > 0) {
				countDifferences++;
				// write results on the DB
				String query = String.format(dynamicAlterQuery, instruction, species);
				try {
					AnalysisLogger.getLogger().trace("Envelope Generated - executing query: " + query);
					DatabaseFactory.executeSQLUpdate(query, connection);
				} catch (Exception e) {
					AnalysisLogger.getLogger().trace("could not execute update");
					e.printStackTrace();
					// System.exit(0);
				}
			}

		} catch (Exception ex) {
			AnalysisLogger.getLogger().trace("Computation traminated prematurely: ", ex);
		}
		numbOfProcessedSpecies++;
		// take ending time
	}


	@Override
	public StatisticalType getOutput() {
		List<TableTemplates> templateHspen = new ArrayList<TableTemplates>();
		templateHspen.add(TableTemplates.HSPEN);
		OutputTable p = new OutputTable(templateHspen,outputTableLabel,outputTable,"Output hspen table");
		return p;
	}
	
	
	@Override
	public void setVersion(float version) {
		this.version = version;
	}

	@Override
	public void postprocess(AlgorithmConfiguration Input, Model previousModel) {

	}

	@Override
	public String getResourceLoad() {
		String returnString = "";
		try {
			long tk = System.currentTimeMillis();
			// double activity = Double.valueOf(processedRecordsCounter)*1000.00/Double.valueOf(tk-tstart);
			double activity = Double.valueOf(numbOfProcessedSpecies - lastProcessedRecordsNumber) * 1000.00 / Double.valueOf(tk - lastTime);
			lastTime = tk;
			lastProcessedRecordsNumber = numbOfProcessedSpecies;

			ResourceLoad rs = new ResourceLoad(tk, activity);
			returnString = rs.toString();
		} catch (Exception e) {
			e.printStackTrace();
			long tk = System.currentTimeMillis();
			returnString = new ResourceLoad(tk, 0).toString();
		}

		return returnString;
	}

	@Override
	//this methods gets information about the threads or the machines which are running the computation
		public String getResources(){
			Resources res = new Resources();
			try{
				for (int i=0;i<numberOfthreads;i++){
					try{
						double value = (threadActivity[i])? 100.00:0.00; 
						res.addResource("Thread_"+(i+1),value);
					}catch(Exception e1){}
					}
			}catch(Exception e){
				e.printStackTrace();
			}
			if ((res!=null) && (res.list!=null))
				return HttpRequest.toJSon(res.list).replace("resId", "resID");
			else 
				return "";
		}

	@Override
	public void stop() {
		interruptProcessing = true;
	}

	@Override
	public float getStatus() {
		return status;
	}

	
	@Override
	public ALG_PROPS[] getProperties() {
		ALG_PROPS[] props = {ALG_PROPS.SPECIES_ENVELOPES};
		return props;
	}

	@Override
	public String getDescription() {
		return "The AquMaps HSPEN algorithm. A modeling algorithm that generates a table containing species envelops (HSPEN), i.e. models capturing species tolerance with respect to environmental parameters, to be used by the AquaMaps approach.";
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		List<TableTemplates> templatesOccurrences = new ArrayList<TableTemplates>();
		templatesOccurrences.add(TableTemplates.OCCURRENCE_AQUAMAPS);
		List<TableTemplates> templateHspen = new ArrayList<TableTemplates>();
		templateHspen.add(TableTemplates.HSPEN);
		List<TableTemplates> templateHcaf = new ArrayList<TableTemplates>();
		templateHcaf.add(TableTemplates.HCAF);
		
		InputTable p1 = new InputTable(templateHspen,"EnvelopeTable","The previous hspen table for regeneration","hspen");
		InputTable p2 = new InputTable(templateHcaf,"CsquarecodesTable","HCaf Table","hcaf_d");
		InputTable p3 = new InputTable(templatesOccurrences,"OccurrenceCellsTable","Ocurrence Cells Table","occurrencecells");
		PrimitiveType p4 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.CONSTANT, "CreateTable","Create New Table for each computation","true");
		PrimitiveType p5 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, "OuputEnvelopeTableLabel","Table name for the new hspen","hspen_1");
		ServiceType p11 = new ServiceType(ServiceParameters.RANDOMSTRING, "OuputEnvelopeTable","Table name for the new hspen","hspen_");
		DatabaseType p6 = new DatabaseType(DatabaseParameters.DATABASEUSERNAME, "DatabaseUserName", "db user name");
		DatabaseType p7 = new DatabaseType(DatabaseParameters.DATABASEPASSWORD, "DatabasePassword", "db password");
		DatabaseType p8 = new DatabaseType(DatabaseParameters.DATABASEDRIVER, "DatabaseDriver", "db driver");
		DatabaseType p9 = new DatabaseType(DatabaseParameters.DATABASEURL, "DatabaseURL", "db url");
		DatabaseType p10 = new DatabaseType(DatabaseParameters.DATABASEDIALECT, "DatabaseDialect", "db dialect");
		
		parameters.add(p1);
		parameters.add(p2);
		parameters.add(p3);
		parameters.add(p4);
		parameters.add(p5);
		parameters.add(p6);
		parameters.add(p7);
		parameters.add(p8);
		parameters.add(p9);
		parameters.add(p10);
		parameters.add(p11);
		
		return parameters;
	}

}
