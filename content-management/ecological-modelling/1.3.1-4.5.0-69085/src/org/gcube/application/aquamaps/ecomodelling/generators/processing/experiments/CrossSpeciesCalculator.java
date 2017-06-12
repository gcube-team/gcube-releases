package org.gcube.application.aquamaps.ecomodelling.generators.processing.experiments;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.utils.DatabaseFactory;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.hibernate.SessionFactory;

public class CrossSpeciesCalculator {

	HashMap<String, Integer> crossSpecies;
	HashSet<String> speciesSet;
	
	private static String speciesSelectionQuery = "select speciesid from %1$s";
	private static String selectionQuery = "select csquarecode, speciesid from %1$s limit %2$s offset %3$s";
	private int chunkSize = 152000;
	protected SessionFactory vreConnection;
	private  String defaultDatabaseFile = "DestinationDBHibernate.cfg.xml";
	private String defaultLogFile = "ALog.properties";
	
	private void populateSelectedSpecies(String hspenTable) {
		speciesSet = new HashSet<String>();
		String speciesSelectionQueryInst = String.format(speciesSelectionQuery, hspenTable);
		List<Object> species = DatabaseFactory.executeSQLQuery(speciesSelectionQueryInst, vreConnection);
		for (Object spec : species){
			speciesSet.add((String) spec);
		}
	}
	
	public void initDBSession(EngineConfiguration engineConf) throws Exception{
		if ((engineConf!=null) && (engineConf.getConfigPath()!=null)){
			
			defaultDatabaseFile = engineConf.getConfigPath()+defaultDatabaseFile;
			defaultLogFile = engineConf.getConfigPath()+defaultLogFile;
			AnalysisLogger.setLogger(defaultLogFile);
			vreConnection = DatabaseFactory.initDBConnection(defaultDatabaseFile,engineConf);
		}
	}
	
	public CrossSpeciesCalculator(EngineConfiguration engineConf) throws Exception{
		
		initDBSession(engineConf);
	}
	
	public void generateCrossSpeciesMap(String hspenTable, String hspecTable) throws Exception {

		AnalysisLogger.getLogger().trace("CrossSpeciesMap->populating species");
		crossSpecies = new HashMap<String, Integer>();
		 populateSelectedSpecies(hspenTable);
		int currentIndex = 0;
		
		String selectionQueryInst = String.format(selectionQuery, hspecTable, chunkSize, currentIndex);
		long t0 = System.currentTimeMillis();
		AnalysisLogger.getLogger().trace("CrossSpeciesMap->taking chunks of species: "+selectionQueryInst);
		List<Object> csquaresSpecies = DatabaseFactory.executeSQLQuery(selectionQueryInst, vreConnection);
		//for all the chunks
		int i=0;
		while (csquaresSpecies.size() > 0) {
		
			//for all the rows
			for (Object row:csquaresSpecies){
				Object[] rowArr  = (Object[]) row;
				String csquare = (String)rowArr[0];
				String species = (String)rowArr[1];
				if (speciesSet.contains(species)){
					Integer nspec = crossSpecies.get(csquare);
					if (nspec!=null)
						nspec++;
					else 
						crossSpecies.put(csquare,1);
				}
			}
			
			long t1 = System.currentTimeMillis();
			
			AnalysisLogger.getLogger().trace("CrossSpeciesMap->Processed chunk "+i+" in "+(t1-t0)+" ms");
			currentIndex = currentIndex + chunkSize;
			selectionQueryInst = String.format(selectionQuery, hspecTable, chunkSize, currentIndex);
			t0 = System.currentTimeMillis();
			AnalysisLogger.getLogger().trace("CrossSpeciesMap->taking chunks of species: "+selectionQueryInst);
			csquaresSpecies = DatabaseFactory.executeSQLQuery(selectionQueryInst, vreConnection);
			
			i++;
		}

		// shutdown connection
		vreConnection.close();
	}

}
