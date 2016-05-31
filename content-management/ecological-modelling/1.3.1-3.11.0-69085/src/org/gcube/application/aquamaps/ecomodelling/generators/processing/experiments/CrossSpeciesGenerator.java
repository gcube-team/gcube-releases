package org.gcube.application.aquamaps.ecomodelling.generators.processing.experiments;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.utils.DatabaseFactory;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.hibernate.SessionFactory;

public class CrossSpeciesGenerator {

	String hspecTableName;
	String tmpTable;
	String username;
	String crossSpeciesTable;
	
	final double threshold = 0.5;
	
	final String query= "select csquarecode, count(%1$s.speciesid) as maxspeciescountinacell from %1$s"+
    " inner join %2$s ON %1$s.speciesid = %2$s.speciesid where probability > %3$s group by csquarecode order by maxspeciescountinacell DESC";
	
	final String createTempTableQuery = "create table %1$s ( speciesid character varying) WITH (OIDS=FALSE ); ALTER TABLE %1$s OWNER TO %2$s; CREATE INDEX %1$s_idx ON %1$s USING btree (speciesid)"; 
	final String dropTempTableQuery = "drop table %1$s";
	final String createCrossSpeciesQuery = "create table %1$s ( csquarecode character varying, maxspeciescountinacell integer ) WITH (OIDS=FALSE ); ALTER TABLE %1$s OWNER TO %2$s; CREATE INDEX %1$s_idx ON %1$s USING btree (csquarecode)";
	final String insertionStatement = "insert into %1$s values %2$s;";
	
	String defaultDatabaseFile = "DestinationDBHibernate.cfg.xml";
	String defaultLogFile = "ALog.properties";
	String default_species_list = "selectedSpecies.txt";
	private SessionFactory vreConnection;
	private ArrayList<Object> selectedSpecies;
	private List<Object> crossSpecies;
	
	
	//buildup the temporary table for storing the list of species to analyze
	private void createSpeciesTempTable(){
		AnalysisLogger.getLogger().trace("creating cross species temporary table");
		tmpTable = "tmp_speciesList_"+UUID.randomUUID();
		tmpTable = tmpTable.replace("-", "");
		
		String createTempQuery = String.format(createTempTableQuery,tmpTable,username);
		try {
			DatabaseFactory.executeSQLUpdate(createTempQuery, vreConnection);
			AnalysisLogger.getLogger().trace("creating cross species temporary table - OK!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//destroy the temp table
	private void cleanTargetTable(){
		String dropTargetQuery = String.format(dropTempTableQuery,crossSpeciesTable);
		AnalysisLogger.getLogger().trace("destroying crossSpeciesTable table");
		try {
			DatabaseFactory.executeSQLUpdate(dropTargetQuery , vreConnection);
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().trace("could not destroy crossSpeciesTable table .. continuing");
		}
		
	}
	
	//destroy the temp table
	private void destroySpeciesTempTable(){
		String dropTempQuery = String.format(dropTempTableQuery,tmpTable);
		AnalysisLogger.getLogger().trace("destroying temporary table");
		try {
			DatabaseFactory.executeSQLUpdate(dropTempQuery, vreConnection);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	//populate the temp table putting the species list
	private void populateTempTable(){
			
		
			AnalysisLogger.getLogger().trace("populating temporary table");
			try {
				StringBuffer buffer = new StringBuffer();
				int size = selectedSpecies.size();
				int i=0;
				for (Object species:selectedSpecies){
					String species$ = (String)species;
					//Create statement and write all the species data
					buffer.append("("+"'"+species+"'"+")");
					if (i<size-1)
						buffer.append(",");
					i++;
				}
				
				AnalysisLogger.getLogger().trace("species buffer fullfilled");
				
				AnalysisLogger.getLogger().trace("Writing table of species names");
				
				String insertQuery = String.format(insertionStatement,tmpTable,buffer.toString());
				DatabaseFactory.executeSQLUpdate(insertQuery, vreConnection);
				AnalysisLogger.getLogger().trace("populating temporary table - OK");	
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			
	}
	
	
	//destroy the temp table
	private void populateCrossSpeciesBuffer(){
		String crossspeciesQuery = String.format(query,hspecTableName,tmpTable,threshold);
		AnalysisLogger.getLogger().trace("populating cross-species table: "+crossspeciesQuery);
		long t0 = System.currentTimeMillis();
		try {
			crossSpecies = DatabaseFactory.executeSQLQuery(crossspeciesQuery, vreConnection);
			long t1 = System.currentTimeMillis();
			AnalysisLogger.getLogger().trace("populating cross-species table - OK - elapsed: "+(t1-t0)+" ms" );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//save the cross-species table
	private void saveCrossSpeciesTable(){
		
		String crossSpeciesQuery = String.format(createCrossSpeciesQuery,crossSpeciesTable,username);
		AnalysisLogger.getLogger().trace("saving cross-species object");
		try {
			DatabaseFactory.executeSQLUpdate(crossSpeciesQuery, vreConnection);
			StringBuffer buffer = new StringBuffer();
			int size = crossSpecies.size();
			int i=0;
			for (Object speciesCount:crossSpecies){
				Object[] speciesCountRow = (Object[])speciesCount;
				String species = ""+speciesCountRow[0];
				String count = ""+speciesCountRow[1];
				//Create statement and write all the species data
				buffer.append("("+"'"+species+"'"+","+"'"+count+"'"+")");
				if (i<size-1)
					buffer.append(",");
				i++;
			}
			
			AnalysisLogger.getLogger().trace("object buffer fullfilled");
			
			AnalysisLogger.getLogger().trace("Writing table of species");
			
			String insertQuery = String.format(insertionStatement,crossSpeciesTable,buffer.toString());
			DatabaseFactory.executeSQLUpdate(insertQuery, vreConnection);
			AnalysisLogger.getLogger().trace("saving cross-species object - OK");	
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//initializes DB session
	private void initDBSession(EngineConfiguration engineConf) throws Exception{
		
		if ((engineConf!=null) && (engineConf.getConfigPath()!=null)){
			AnalysisLogger.getLogger().trace("Initializing Database Connection");
			defaultDatabaseFile = engineConf.getConfigPath()+defaultDatabaseFile;
			vreConnection = DatabaseFactory.initDBConnection(defaultDatabaseFile,engineConf);
		}
	}
	
	//shutdown db connection
	public void shutdownConnection(){
		AnalysisLogger.getLogger().trace("Shuttingdown Database Connection");
		vreConnection.close();
	}
	
	//populate selected species
	private void populateSelectedSpecies(){
		BufferedReader br = null;
		if (selectedSpecies==null){
			AnalysisLogger.getLogger().trace("fulfilling species list");
			try{
				br = new BufferedReader(new FileReader(default_species_list));
				selectedSpecies = new ArrayList<Object>();
				String line = br.readLine();
				while (line != null) {
					selectedSpecies.add(line.trim());
					line = br.readLine();
				}
			}catch(Exception e){
				AnalysisLogger.getLogger().trace("SELECTED SPECIES - FILE NOT FOUND");
			}
			finally{
				try{
					br.close();
				}catch(Exception e){}
			}
			AnalysisLogger.getLogger().trace("fulfilling species list - OK!");
		}
	}
	
	public void generateCrossSpeciesTable(EngineConfiguration engine,String crossSpeciesTable) throws Exception{
		
		if (engine!=null){
			defaultLogFile = engine.getConfigPath()+defaultLogFile; 
			default_species_list = engine.getConfigPath()+default_species_list;
			AnalysisLogger.setLogger(defaultLogFile);
			username = engine.getDatabaseUserName();
			this.crossSpeciesTable = crossSpeciesTable;
			hspecTableName = engine.getDistributionTable();
			//init DB session
			initDBSession(engine);
			//destroy destination table 
			cleanTargetTable();
			//populate species 
			populateSelectedSpecies();
			//create cross species table
			createSpeciesTempTable();
			//populate temporary table
			populateTempTable();
			//fullfil species object 
			populateCrossSpeciesBuffer();
			//save all cross-species objects 
			saveCrossSpeciesTable();
			//FINAL PHASE - CLOSE ALL
			//destroy temporary table
			destroySpeciesTempTable();
			//shutdown
			shutdownConnection();
		}
	}
	
	
}
