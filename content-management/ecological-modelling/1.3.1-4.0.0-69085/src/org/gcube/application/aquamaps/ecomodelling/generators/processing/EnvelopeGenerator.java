package org.gcube.application.aquamaps.ecomodelling.generators.processing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.gcube.application.aquamaps.ecomodelling.generators.abstracts.AbstractEnvelopeAlgorithm;
import org.gcube.application.aquamaps.ecomodelling.generators.aquamapsorg.AquamapsEnvelopeAlgorithm;
import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.EnvelopeModel;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.EnvelopeName;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.Hspen;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.RemoteHspecOutputObject;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.livemonitor.ResourceLoad;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.livemonitor.Resources;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Envelope;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.EnvelopeSet;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.OccurrencePointSets;
import org.gcube.application.aquamaps.ecomodelling.generators.utils.DatabaseFactory;
import org.gcube.contentmanagement.graphtools.utils.HttpRequest;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.hibernate.SessionFactory;

public class EnvelopeGenerator {
	
	
	private static final String alterQuery = "UPDATE %HSPEN% SET %1$s WHERE speciesid = '%2$s'";
	
	private static final String createHspenTable = "CREATE TABLE %HSPEN% ( speccode integer, speciesid character varying NOT NULL, lifestage character varying NOT NULL, faoareas character varying(100), faoareasref character varying, faocomplete smallint, nmostlat real, smostlat real, wmostlong real,emostlong real, lme character varying(180), depthyn smallint, depthmin integer, depthmax integer,  depthprefmin integer,  depthprefmax integer,  meandepth smallint,  depthref character varying,  pelagic smallint,  tempyn smallint,  tempmin real,  tempmax real,  tempprefmin real,  tempprefmax real,  tempref character varying,  salinityyn smallint,  salinitymin real,  salinitymax real,  salinityprefmin real,  salinityprefmax real,  salinityref character varying,  primprodyn smallint,  primprodmin real,  primprodmax real,  primprodprefmin real,  primprodprefmax real,  primprodprefref character varying,  iceconyn smallint,  iceconmin real,  iceconmax real,  iceconprefmin real,  iceconprefmax real,  iceconref character varying,  landdistyn smallint,  landdistmin real,  landdistmax real,  landdistprefmin real,  landdistprefmax real,  landdistref character varying,  remark character varying,  datecreated timestamp without time zone,  datemodified timestamp without time zone,  expert integer,  dateexpert timestamp without time zone,  envelope smallint,  mapdata smallint,  effort smallint,  layer character(1),  usepoints smallint,  rank smallint,  CONSTRAINT %HSPEN%_pkey PRIMARY KEY (speciesid, lifestage))WITH (  OIDS=FALSE);ALTER TABLE %HSPEN% OWNER TO %1$s; CREATE INDEX envelope_%HSPEN%_idx   ON %HSPEN%  USING btree  (envelope); CREATE INDEX mapdata_%HSPEN%_idx  ON %HSPEN%  USING btree  (mapdata); CREATE INDEX speciesid_%HSPEN%_idx  ON %HSPEN%  USING btree  (speciesid);";
	private static final String populateNewHspen = "insert into %HSPEN% (select * from %HSPEN_ORIGIN%);";
	private static final String speciesListQuery = "select distinct speciesid from %HSPEN%;";
	private static final String hspenListQuery = "select speciesid, layer, iceconmin , iceconmax , iceconprefmin , iceconprefmax , salinitymin , salinitymax , salinityprefmin , salinityprefmax , landdistmin , landdistmax , landdistprefmin , landdistprefmax , tempmin , tempmax , tempprefmin , tempprefmax ,  primprodmin ,  primprodmax ,  primprodprefmin ,  primprodprefmax  from %HSPEN%;";
	
	//database variables
	String defaultDatabaseFile = "DestinationDBHibernate.cfg.xml";
	String defaultLogFile = "ALog.properties";
	String default_hspec_suitable_table = "hspec_suitable_gp2";
	String default_hcaf_table = "hcaf_s";
	String default_hspen_table = "hspen_new";
	String default_origin_hspen_table = "hspen";
	String default_species_list = "selectedSpecies.txt";
	
	String dynamicCreateTable;
	String dynamicPopulateNewHspen;
	String dynamicAlterQuery;
	String dynamicSelectValues;
	String dynamicSpeciesListQuery;
	String dynamicHspenInformationQuery;
	String currentHCAFTable;
	String currentOccurrenceTable;
	
	boolean useDB=true;
	boolean interruptProcessing=false;
	float status;
	int numbOfProcessedSpecies;
	private ExecutorService executorService;
	EnvelopeModel generatorAlgorithm = EnvelopeModel.AQUAMAPS;
	List<Object> selectedSpecies;
	AbstractEnvelopeAlgorithm envelopeGenerationAlgorithm;
	int countDifferences;
	
	//DB SESSION
	protected SessionFactory vreConnection;

	//PARAMETERS
	static int chunksize = 5000;
	int numberOfthreads = 1;
	
	boolean threadActivity[];
	
	public void stopProcess(){
		interruptProcessing = true;
	}
	
	public double getStatus(){
		return status;
	}
	//initializes DB session
	public void initDBSession(EngineConfiguration engineConf) throws Exception{
		if ((engineConf!=null) && (engineConf.getConfigPath()!=null))
			defaultDatabaseFile = engineConf.getConfigPath()+defaultDatabaseFile;
		if (useDB)
			vreConnection = DatabaseFactory.initDBConnection(defaultDatabaseFile,engineConf);
	}

	//waits for thread to be free
	private void wait4Thread(int index) {

		// wait until thread is free
		while (threadActivity[index]) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}
	
	//initializes threads activity status 
	public void initializeThreads(int numberOfThreadsToUse) {
		// initialize threads and their activity state
		executorService = Executors.newFixedThreadPool(numberOfThreadsToUse);
		
		threadActivity = new boolean[numberOfThreadsToUse];
		// initialize to false;
		for (int j = 0; j < threadActivity.length; j++) {
			threadActivity[j] = false;
		}
		
	}
	
	//initializes queries
	public void initQueriesStatements(EngineConfiguration engineConf) throws Exception{
		if ((engineConf!=null) && (engineConf.getConfigPath()!=null)){
			try{
			dynamicAlterQuery = alterQuery.replace("%HSPEN%", engineConf.getHspenTable());
			dynamicCreateTable = createHspenTable.replace("%HSPEN%", engineConf.getHspenTable());
			dynamicPopulateNewHspen = populateNewHspen.replace("%HSPEN_ORIGIN%", engineConf.getOriginHspenTable()).replace("%HSPEN%", engineConf.getHspenTable());
			dynamicSpeciesListQuery = speciesListQuery.replace("%HSPEN%", engineConf.getOriginHspenTable());
			dynamicHspenInformationQuery = hspenListQuery.replace("%HSPEN%", engineConf.getOriginHspenTable());
			currentHCAFTable = engineConf.getHcafTable();
			currentOccurrenceTable = engineConf.getOccurrenceCellsTable();
			}catch(Exception e){}
		}
	}
	
	//shutdown the connection
	public void shutdownConnection(){
		vreConnection.close();
	}
	
	//sets the speciesList from outside
	public void setSelectedSpecies(List<String> speciesList){
		selectedSpecies = new ArrayList<Object>();
		for (String species:speciesList){
			selectedSpecies.add(species);
		}
	}
	
	// populates the selectedSpecies variable by reading species from file
	private void populateSelectedSpecies(){
		if (selectedSpecies==null){
			BufferedReader br = null;
			try{
				br = new BufferedReader(new FileReader(default_species_list));
				selectedSpecies = new ArrayList<Object>();
				String line = br.readLine();
				while (line != null) {
					selectedSpecies.add(line.trim());
					line = br.readLine();
				}
				
			}catch(Exception e){
			AnalysisLogger.getLogger().trace("Distribution Generator - SELECTED SPECIES - FILE NOT FOUND - POPULATING FROM DB");
			populateSelectedSpeciesByDB();
			}
			finally{
				try{
					br.close();
				}catch(Exception e){}
			}
		}
	}
	
	
	//populates the selectedSpecies variable by getting species from db
	private void populateSelectedSpeciesByDB(){
		AnalysisLogger.getLogger().trace("Distribution Generator ->getting all species list from DB");
		if (useDB){
			List<Object> allspecies = DatabaseFactory.executeSQLQuery(dynamicSpeciesListQuery, vreConnection);
			selectedSpecies = allspecies;
		}
	}
	HashMap<String, List<Object>> allSpeciesHspen;
	private void populateHspen(){
		allSpeciesHspen = new HashMap<String, List<Object>>();
		List<Object> SpeciesInfo = DatabaseFactory.executeSQLQuery(dynamicHspenInformationQuery, vreConnection);
		int lenSpecies = SpeciesInfo.size();
		
		for (int i=0;i<lenSpecies;i++){
			Object[] speciesArray = (Object[])SpeciesInfo.get(i);
			String speciesid = (String)speciesArray[0];
			List<Object> singleSpeciesInfo = new ArrayList<Object>();
			singleSpeciesInfo.add(speciesArray);
			allSpeciesHspen.put((String)speciesid, singleSpeciesInfo);
		}
		
	}
	
	public EnvelopeGenerator (EngineConfiguration engine) throws Exception 
	{
		if (engine!=null){
			useDB = engine.useDB();
			//init DB session
			initDBSession(engine);
			//init Queries Statements
			initQueriesStatements(engine);

			//init logger
			if (engine.getConfigPath()!=null){
				defaultLogFile = engine.getConfigPath()+defaultLogFile; 
				default_species_list = engine.getConfigPath()+default_species_list;
			}
			AnalysisLogger.setLogger(defaultLogFile);
			//if necessary set up Destination Table
			if (engine.getHspenTable()!=null)
				default_hspen_table = engine.getHspenTable();

			if (engine.getHcafTable()!=null)
				default_hcaf_table = engine.getHcafTable(); 
			if (engine.getOriginHspenTable()!=null)
				default_origin_hspen_table = engine.getOriginHspenTable();
			
			//setup of the number of threads
			if (engine.getNumberOfThreads()!=null)
				numberOfthreads = engine.getNumberOfThreads();
			
			//create remote table
			if (engine.createTable()){
				try{
					if (useDB){
						//create and populate the novel table
						AnalysisLogger.getLogger().trace("Distribution Generator->creating new table "+String.format(dynamicCreateTable, engine.getDatabaseUserName()));
						DatabaseFactory.executeSQLUpdate(String.format(dynamicCreateTable, engine.getDatabaseUserName()), vreConnection);
						AnalysisLogger.getLogger().trace("Distribution Generator->populating new table "+dynamicPopulateNewHspen);
						DatabaseFactory.executeSQLUpdate(dynamicPopulateNewHspen, vreConnection);
					}
				}catch(Exception e){
					AnalysisLogger.getLogger().trace("Distribution Generator->could not create table");
					}
			}
			
			if (engine.getGenerator()!= null){
				generatorAlgorithm = engine.getEnvelopeGenerator();
			}
		}
		
		if (generatorAlgorithm == EnvelopeModel.AQUAMAPS)
			envelopeGenerationAlgorithm = new AquamapsEnvelopeAlgorithm();
		
		//initialize the processing flag
		interruptProcessing = false;
	}
	
	//ENVELOPES GENERATION
	public void reGenerateEnvelopes() throws Exception {
		
		//INITIALIZATION
		AnalysisLogger.getLogger().trace("Distribution Generator->populating species");
		populateSelectedSpecies();
		populateHspen();
		
		AnalysisLogger.getLogger().trace("Distribution Generator->ENVELOPES GENERATION STARTED");
		long tstart = System.currentTimeMillis();
		//initialize threads
		initializeThreads(numberOfthreads);
		//END INITIALIZATION
		
		try{
		//thread selection index
		int currentThread = 0;
		//global chunks counter
		int globalcounter = 0;
		//count differences in hspen original and new hspen 
		countDifferences = 0;
		//take time
		long computationT0 = System.currentTimeMillis();
		int numberOfSpecies = selectedSpecies.size();
		
		//ENVELOPES CALCULATION
		//cycle throw the species to generate
		//one thread calculation for each species
		for (Object species : selectedSpecies) {
		
			//get speciesID
			String speciesid = (String) species;
			if (speciesid.length()>0){
			//calculation on multiple threads
			AnalysisLogger.getLogger().trace("Distribution Generator->ANALIZING SPECIES: " + speciesid);
			//wait for thread to be free
			wait4Thread(currentThread);
			//start species information calculation on the thread
			startNewTCalc(currentThread, speciesid);
			//increment thread selection index
			currentThread++;
			//reset current thread index
			if (currentThread >= numberOfthreads)
				currentThread = 0;
			//report probability
			float s = (float)((int)(((float)globalcounter*100f/(numberOfSpecies))*100f))/100f;
			status = (s==100)?99:s;
			AnalysisLogger.getLogger().trace("STATUS->"+status+"%");
			
			//increment global counter index
			globalcounter++;
			AnalysisLogger.getLogger().warn("Number of Found Differences: "+countDifferences);
			}
			
			if (interruptProcessing)
				break;
		}
			//END OF CALCULATION CORE
			
			//wait for last threads to finish
			for (int i=0;i<numberOfthreads;i++) {
				// free previous calculation
				wait4Thread(i);
			}
		
			long computationT1 = System.currentTimeMillis();
			AnalysisLogger.getLogger().warn("All Envelopes Computation Finished in "+(computationT1-computationT0)+" ms");
			AnalysisLogger.getLogger().warn("Number of Overall Found Differences: "+countDifferences);
		}catch(Exception e){
			AnalysisLogger.getLogger().trace("Computation traminate prematurely: ",e);
		}
		
		//shutdown threads
		executorService.shutdown();
		//shutdown connection
		shutdownConnection();
		//set completeness
		status = 100.0f;
	}
	//END OF PROBABILITIES GENERATION PROCEDURE
	
	
	
	
	//calculations based on external objects
	//
	public Hspen calcEnvelopes(Hspen hspen, EngineConfiguration config){
	
		//take initial time
		long t0 = System.currentTimeMillis();
		try{
		//convert hspen to object array
		Object [] singleHspen = envelopeGenerationAlgorithm.hspen2ObjectArray(hspen);
		
		//call all envelopes calculations		
		EnvelopeSet envSet = envelopeGenerationAlgorithm.calculateEnvelopes(hspen.getSpeciesID(), vreConnection, config.getOccurrenceCellsTable(), config.getHcafTable(), singleHspen);
		List<Envelope> envelopes = envSet.getEnvelopes();
		for (Envelope e:envelopes){
			if (e.getName().equals(EnvelopeName.TEMPERATURE)){
				hspen.setTemperature(e);
			}
			else if (e.getName().equals(EnvelopeName.SALINITY)){
				hspen.setSalinity(e);
			}
			else if (e.getName().equals(EnvelopeName.LAND_DISTANCE)){
				hspen.setLandDistance(e);
			}
			else if (e.getName().equals(EnvelopeName.ICE_CONCENTRATION)){
				hspen.setIceConcentration(e);
			}
			else if (e.getName().equals(EnvelopeName.PRIMARY_PRODUCTION)){
				hspen.setPrimaryProduction(e);
			}
		}
		
		}catch(Exception e){
			AnalysisLogger.getLogger().trace("Computation traminated prematurely: ",e);
		}
	
		//take the result of the calculation
		long t1 = System.currentTimeMillis();
		AnalysisLogger.getLogger().trace("Computation for species "+hspen.getSpeciesID()+" finished in "+(t1-t0)+" ms");
		
		//shutdown connection
		shutdownConnection();
		
		return hspen;
	}
	
	//calculation when database is invoked from outside
	public Hspen calcEnvelopes(Hspen hspen, OccurrencePointSets occPointLists){
		
		//take initial time
		long t0 = System.currentTimeMillis();
		try{
		//convert hspen to object array
		Object [] singleHspen = envelopeGenerationAlgorithm.hspen2ObjectArray(hspen);
		AnalysisLogger.getLogger().trace("calcEnvelopes->Species ID : "+hspen.getSpeciesID());
		
		//call all envelopes calculations		
		EnvelopeSet envSet = envelopeGenerationAlgorithm.calculateEnvelopes(hspen.getSpeciesID(), singleHspen, occPointLists);
		List<Envelope> envelopes = envSet.getEnvelopes();
		
		for (Envelope e:envelopes){
			if (e.getName().equals(EnvelopeName.TEMPERATURE)){
				hspen.setTemperature(e);
			}
			else if (e.getName().equals(EnvelopeName.SALINITY)){
				hspen.setSalinity(e);
			}
			else if (e.getName().equals(EnvelopeName.LAND_DISTANCE)){
				hspen.setLandDistance(e);
			}
			else if (e.getName().equals(EnvelopeName.ICE_CONCENTRATION)){
				hspen.setIceConcentration(e);
			}
			else if (e.getName().equals(EnvelopeName.PRIMARY_PRODUCTION)){
				hspen.setPrimaryProduction(e);
			}
		}
		}catch(Exception e){
			AnalysisLogger.getLogger().trace("Computation traminated prematurely: ",e);
		}
		//take the result of the calculation
		long t1 = System.currentTimeMillis();
		AnalysisLogger.getLogger().trace("Computation for species "+hspen.getSpeciesID()+" finished in "+(t1-t0)+" ms");
		return hspen;
	}
	
	//calculation for standalone mode
	public void calcEnvelopes(String species){
		//take initial time
		long t0 = System.currentTimeMillis();
		try{
		//take information for the selected Species
		List<Object> singleHspen = allSpeciesHspen.get(species);
		//call all envelopes calculations
		EnvelopeSet envSet = envelopeGenerationAlgorithm.calculateEnvelopes(species, vreConnection, currentOccurrenceTable, currentHCAFTable, (Object[])singleHspen.get(0));
		String instruction = envSet.getEnvelopeString();
		//take the result of the calculation
		long t1 = System.currentTimeMillis();
		AnalysisLogger.getLogger().trace("Computation for species "+species+" finished in "+(t1-t0)+" ms");

		if (instruction.length()>0){
			countDifferences++;
			//write results on the DB
			String query = String.format(dynamicAlterQuery,instruction,species);
			try{
				AnalysisLogger.getLogger().trace("Envelope Generated - executing query: "+query);
				DatabaseFactory.executeSQLUpdate(query,vreConnection);
			}catch(Exception e){
				AnalysisLogger.getLogger().trace("could not execute update");
				e.printStackTrace();
//				System.exit(0);
			}
		}
		
		}catch(Exception ex){
			AnalysisLogger.getLogger().trace("Computation traminated prematurely: ",ex);
		}
		numbOfProcessedSpecies++;
		//take ending time
	}
	
	//THREAD SECTION
	//definition of the Thread
	//calculates values for one species
	private class ThreadCalculator implements Callable<Integer> {
		int index;
		String species;
		
		public ThreadCalculator(int index, String species) {
			this.species = species;
			this.index = index;
		}

		public Integer call(){
			
			try{
					calcEnvelopes(species);
			}catch(Exception e){AnalysisLogger.getLogger().trace(""+e);e.printStackTrace();}
			threadActivity[index]=false;
			return 0;
		}
	}
	
	//end Definition of the Thread
	//activation
	private void startNewTCalc(int index, String species){
		threadActivity[index] = true;
		ThreadCalculator tc = new ThreadCalculator(index, species);
		executorService.submit(tc);
	}
	//END OF THREAD SECTION
	
	//LOAD CALCULATION
	
	//get the Species Load, that is the number of processed species 
	public String getSpeciesLoad(){
		String returnString = "";
		try{
			long tk = System.currentTimeMillis();
			double activity = numbOfProcessedSpecies;
			ResourceLoad rs = new ResourceLoad(tk,activity);
			returnString = rs.toString();
		}catch(Exception e){
			e.printStackTrace();
			long tk = System.currentTimeMillis();
			returnString = new ResourceLoad(tk,0).toString();
		}
		return returnString;
	}
	
	private int lastProcessedRecordsNumber;
	private long lastTime;
	
	//get the Resource Load, that is the number of hspec records for second
	public String getResourceLoad(){
		String returnString = "";
		try{
			long tk = System.currentTimeMillis();
//			double activity = Double.valueOf(processedRecordsCounter)*1000.00/Double.valueOf(tk-tstart);
			double activity = Double.valueOf(numbOfProcessedSpecies-lastProcessedRecordsNumber)*1000.00/Double.valueOf(tk-lastTime);
			lastTime = tk;
			lastProcessedRecordsNumber = numbOfProcessedSpecies;
			
			ResourceLoad rs = new ResourceLoad(tk,activity);
			returnString = rs.toString();
		}catch(Exception e){
			e.printStackTrace();
			long tk = System.currentTimeMillis();
			returnString = new ResourceLoad(tk,0).toString();
		}
		
		return returnString;
	}
	
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
	
}
