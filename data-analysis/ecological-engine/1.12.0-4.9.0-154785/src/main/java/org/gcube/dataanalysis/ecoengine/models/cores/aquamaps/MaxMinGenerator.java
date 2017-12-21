package org.gcube.dataanalysis.ecoengine.models.cores.aquamaps;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.hibernate.SessionFactory;

public class MaxMinGenerator {

	String selectionQuery = "SELECT DISTINCT Max(hcaf_s.CenterLat) AS maxCLat, Min(hcaf_s.CenterLat) AS minCLat,speciesid FROM #occurrencecells# INNER JOIN hcaf_s ON #occurrencecells#.CsquareCode = hcaf_s.CsquareCode WHERE (((hcaf_s.oceanarea > 0))) AND #occurrencecells#.SpeciesID in (%1$s) AND #occurrencecells#.GoodCell <> 0 group by speciesid; ";
	
	String insertionQuery = "insert into maxminlat_%4$s values ('%1$s','%2$s','%3$s'); ";
	String creationQuery = "CREATE TABLE %1$s(  speciesid character varying,  maxclat real,  minclat real) WITH (  OIDS=FALSE)";
	SessionFactory vreConnection;
	List<Object> selectedSpecies;
	// static String SpeciesListQuery = "select distinct speciesid from hspen;";
	String SpeciesListQuery = "select distinct s.speciesid from %1$s s;";
	String CheckListQuery = "select speciesid from maxminlat where speciesid = '%1$s';";
	String vreDatabaseConfig = "DestinationDBHibernate.cfg.xml";

	private boolean checkElement(String speciesid) {

		List<Object> maxMin = DatabaseFactory.executeSQLQuery(String.format(CheckListQuery, speciesid), vreConnection);

		if ((maxMin != null) && (maxMin.size() > 0))
			return true;
		else
			return false;
	}

	public void checkAllElements() {
		int i = 1;
		for (Object species : selectedSpecies) {
			// System.out.println("CHECKING: "+i+" SPECIES: "+species);
			boolean check = checkElement((String) species);
			if (!check) {
				System.err.println("MISSING: " + species);
			}
			i++;
		}
	}

	int counter = 0;
	StringBuffer buffer = new StringBuffer();
	private double status = 0;

	public double getstatus() {
		return status;
	}

	
	private void insertQuery(List<String> species, String hspenTable) {

		StringBuffer sb = new StringBuffer();
		int size = species.size();
		int i = 0;
		for (String sp: species){
			sb.append("'"+sp+"'");
			i++;
			if (i<size)
				sb.append(",");
		}
		
		String query = String.format(selectionQuery, sb.toString());
		
		List<Object> maxMin = DatabaseFactory.executeSQLQuery(query, vreConnection);
		int sizemm = maxMin.size();
		StringBuffer buffer = new StringBuffer();
		
		for (int j=0;j<sizemm;j++){
		Object[] maxMinRow = (Object[]) maxMin.get(0);
		String insert = String.format(insertionQuery,  maxMinRow[2], maxMinRow[0], maxMinRow[1], hspenTable);

		buffer.append(insert);
		
		}
		try {
				AnalysisLogger.getLogger().debug("inserting...");
				// DatabaseFactory.executeSQLUpdate(insert, vreConnection);
				DatabaseFactory.executeSQLUpdate(buffer.toString(), vreConnection);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}


	
	private void executeQuery(String species, String hspenTable) {

		String query = String.format(selectionQuery, species);
		List<Object> maxMin = DatabaseFactory.executeSQLQuery(query, vreConnection);
		Object[] maxMinRow = (Object[]) maxMin.get(0);
		String insert = String.format(insertionQuery, species, maxMinRow[0], maxMinRow[1], hspenTable);

		buffer.append(insert);
		if (counter % 100 == 0) {

			try {
				AnalysisLogger.getLogger().debug("inserting...");
				// DatabaseFactory.executeSQLUpdate(insert, vreConnection);
				DatabaseFactory.executeSQLUpdate(buffer.toString(), vreConnection);
			} catch (Exception e) {
				e.printStackTrace();
			}
			buffer = new StringBuffer();
		}
		counter++;
	}

	public void getAllSpecies(String hspentable) {
		// populates the selectedSpecies variable by getting species from db
		String query = String.format(SpeciesListQuery, hspentable);
		AnalysisLogger.getLogger().warn("Distribution Generator ->getting all species list from DB");
		AnalysisLogger.getLogger().warn("Distribution Generator ->" + query);
		List<Object> allspecies = DatabaseFactory.executeSQLQuery(query, vreConnection);
		selectedSpecies = allspecies;
		// AnalysisLogger.getLogger().warn("Distribution Generator -> SIZE: " + selectedSpecies.size());
	}

	protected void deleteDestinationTable(String table) {
		String deleteQuery = "drop table " + table + ";";
		// clean the corresponding table on destination db
		try {
			DatabaseFactory.executeSQLUpdate(deleteQuery, vreConnection);
			AnalysisLogger.getLogger().debug("destination table dropped");
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug("destination table NOT dropped");
		}

	}

	protected void buildDestinationTable(String destinationTable) {

		String createQuery = String.format(creationQuery, destinationTable);
		AnalysisLogger.getLogger().debug("Creating new table or destination schema: " + destinationTable);
		try {
			DatabaseFactory.executeSQLUpdate(String.format(createQuery, destinationTable), vreConnection);
			AnalysisLogger.getLogger().debug("Table created");
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug("Table NOT created");
		}
	}

	
	public void populatemaxminlat2(String hspenTable) {

		int i = 1;
		try {
			long t0 = System.currentTimeMillis();
			getAllSpecies(hspenTable);
			deleteDestinationTable("maxminlat_" + hspenTable);
			buildDestinationTable("maxminlat_" + hspenTable);
			int size = selectedSpecies.size();
			AnalysisLogger.getLogger().warn("Distribution Generator -> SIZE: " + size);
			for (Object species : selectedSpecies) {
				String speciesid = (String) species;
				executeQuery(speciesid, hspenTable);
				i++;
				
				status = MathFunctions.roundDecimal(((double) i *100/ (double) size), 2);
//				status = (double) i / (double) size;
				if (i%10==0)
					AnalysisLogger.getLogger().debug("status " + status);
			}
			long t1 = System.currentTimeMillis();
			AnalysisLogger.getLogger().debug("elapsed Time: " + (t1 - t0));

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			vreConnection.close();
		}
	}
	
	
	public String populatemaxminlat(String destinationTable, String hspenTable, String occurrenceCellsTable) {

		int i = 1;
		
		try {
			selectionQuery = selectionQuery.replace("#occurrencecells#",occurrenceCellsTable);
			long t0 = System.currentTimeMillis();
			getAllSpecies(hspenTable);
			deleteDestinationTable(destinationTable);
			buildDestinationTable(destinationTable);
			int size = selectedSpecies.size();
			AnalysisLogger.getLogger().warn("Distribution Generator -> SIZE: " + size);
			List<String> specieslist = new ArrayList<String>();
			for (Object species : selectedSpecies) {
				String speciesid = (String) species;
//				executeQuery(speciesid, hspenTable);
				i++;
				specieslist.add(speciesid);
				
				status = MathFunctions.roundDecimal(((double) i *100/ (double) size), 2);
				if (status >= 100)
					status = 99.00;
				
//				status = (double) i / (double) size;
				if (i%100==0){
					AnalysisLogger.getLogger().debug("status " + status+" species processed "+i);
					insertQuery(specieslist, hspenTable);
					specieslist = null;
					specieslist = new ArrayList<String>();
					
					//long t1 = System.currentTimeMillis();
					//AnalysisLogger.getLogger().debug("elapsed Time: " + (t1 - t0));
				}
				
			}
			
			if (specieslist.size()>0){
				AnalysisLogger.getLogger().debug("final status " + status+" species processed "+i);
				insertQuery(specieslist, hspenTable);
			}
			
			long t1 = System.currentTimeMillis();
			AnalysisLogger.getLogger().debug("overall elapsed Time: " + (t1 - t0));
			
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			status = 100;
		}
		
		return destinationTable;
	}

	
	public MaxMinGenerator(SessionFactory connection) {
		vreConnection = connection;
	}
	

	

}
