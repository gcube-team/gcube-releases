package org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.treeStructure.chunks;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.dataanalysis.lexicalmatcher.analysis.core.DataTypeRecognizer;
import org.gcube.dataanalysis.lexicalmatcher.analysis.core.Engine;
import org.gcube.dataanalysis.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data.CategoryScores;
import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data.DBObjectTranslator;
import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data.Entry;
import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data.SingleResult;
import org.gcube.dataanalysis.lexicalmatcher.utils.DistanceCalculator;
import org.gcube.dataanalysis.lexicalmatcher.utils.LexicalLogger;
import org.hibernate.SessionFactory;

public class TimeSeriesChunk extends Chunk{

	
	private ArrayList<String> columnEntries;
	private String columnType;
	private LexicalEngineConfiguration config;
	private boolean mustInterrupt;
	private ArrayList<SingleResult> detailedResults; 
	private String singletonElement;
	private boolean isSingleton;
	
	public String getColumnType(){
		return columnType;
	}
	
	public String getSingletonEntry(){
		return singletonElement;
	}
	
	public ArrayList<SingleResult> getDetailedResults(){
		return detailedResults;
	}
	public boolean isSingleton(){
		return isSingleton;
	}
	
	public TimeSeriesChunk(String timeSeriesName, String timeSeriesColumn, String ColumnType, BigInteger start, int ChunkSize, LexicalEngineConfiguration Config, Engine engine) throws Exception{
		super(engine);
		DBObjectTranslator dbo = new DBObjectTranslator();
		SessionFactory sess = engine.getDBSession();
		columnEntries = dbo.retrieveTimeSeriesEntries(sess, timeSeriesName, timeSeriesColumn, start, ChunkSize);
		if (ColumnType==null){
			columnType = DataTypeRecognizer.guessType(columnEntries);
			LexicalLogger.getLogger().trace("TimeSeriesChunk-> GUESSED TYPE " + columnType + " FOR COLUMN "+timeSeriesColumn);
		}
		mustInterrupt = false;
		config = Config;
		isSingleton = false;
	}
	
	public TimeSeriesChunk(String singletonString, String ColumnType, BigInteger start, int ChunkSize, LexicalEngineConfiguration Config, Engine engine) throws Exception{
		super(engine);
		columnEntries = new ArrayList<String>();
		columnEntries.add(singletonString);
		if (ColumnType==null){
			columnType = DataTypeRecognizer.guessType(columnEntries);
			LexicalLogger.getLogger().trace("TimeSeriesChunk-> GUESSED TYPE " + columnType + " FOR SINGLETON "+singletonString);
		}
		mustInterrupt = false;
		config = Config;
		isSingleton = true;
		singletonElement = singletonString;
		detailedResults = new ArrayList<SingleResult>();
	}
	
	
	
	public boolean mustInterruptProcess (){
		return this.mustInterrupt;
	}
	public void compareToReferenceChunk(HashMap<String, CategoryScores> scoresTable, ReferenceChunk catChunk) throws Exception {
		compareToReferenceChunk(scoresTable, catChunk,null);
	}
	
	// checks an entry set against a reference set
	// columnEntries: column elements from unknown column
	// cat: category analyzed for candidating to recognized
	// referenceEntries: some elements belonging to cat, to be compared to columnEntries
	public void compareToReferenceChunk(HashMap<String, CategoryScores> scoresTable, ReferenceChunk catChunk,String ColumnFilter) throws Exception {

		
		//in the case of a singleton Chunk interrupt computation in case of exact match

		// get category Score for further processing
		CategoryScores categoryScores = scoresTable.get(catChunk.getCategoryName());
		//extract Entries from DB
		ArrayList<Entry> categoryEntries = catChunk.getReferenceEntries();

		for (String timeSeriesElement : columnEntries) {
			// for each reference entry
			for (Entry referenceEntry : categoryEntries) {

				// take all attributes of a reference entry for confrontation to columns
				HashMap<String, String> attributes = referenceEntry.getAttributes();
				HashMap<String, String> types = referenceEntry.getTypes();
				boolean anotherReference= true;
				
				// for each attribute of an entry
				for (String referenceColumn : attributes.keySet()) {

					// perform calculation only if the column type is the same
					if (types.get(referenceColumn).equals(columnType)&&((ColumnFilter==null)||(ColumnFilter.equalsIgnoreCase(referenceColumn)))) {
//						 AnalysisLogger.getLogger().debug("CategoryOrderedList->checkAllEntriesOnEntireCategory-> REFERENCE COLUMN "+referenceColumn+" HAS TYPE "+types.get(referenceColumn));
						// take the attribute value of the entry
						String attribute = attributes.get(referenceColumn);
						// calculate the distance between the unknown entry and the attribute
						DistanceCalculator d = new DistanceCalculator();
						double percentage = d.CD(config.useSimpleDistance, timeSeriesElement, attribute, isSingleton, isSingleton) * 100f;
//						 AnalysisLogger.getLogger().debug("CategoryOrderedList->checkUnkEntriesOnEntireCategory-> Percentage between " +timeSeriesElement + " and " + attribute + " is: "+percentage );
						// if they are similar
						if (percentage > config.entryAcceptanceThreshold) {
//							if (catChunk.getCategoryName().equals("COUNTRY_OLD"))
							LexicalLogger.getLogger().trace("TimeSeriesChunk->compareToCategoryChunk-> \t\tPercentage between " + timeSeriesElement + " vs. " + attribute + " is: " + percentage+" in "+catChunk.getCategoryName()+":"+referenceColumn);
							
							categoryScores.incrementScore(referenceColumn, (float)percentage,anotherReference);
							
							//if we are in a singleton we have to get the details
							if (isSingleton){
								//for singleton match, fulfil details
								int index =0;
								for (SingleResult sr :detailedResults){
									
									Double scoredetail = sr.getScore();
									
									if (scoredetail<percentage){
										break;
									}
									index ++;
								}
								detailedResults.add(index, new SingleResult(attribute, null, percentage,null,"0"));
							}
							else{
								LexicalLogger.getLogger().trace("TimeSeriesChunk->compareToCategoryChunk-> "+categoryScores.showScores());
							}
							//if exact match is reached, exit
							if ((percentage==100)&&(isSingleton))
							{
								detailedResults = new ArrayList<SingleResult>();
								detailedResults.add(new SingleResult(attribute, null, percentage,null,"0"));
								mustInterrupt = true;
								break;
							}
						}
					}

				}// end for on columns
				
				if (mustInterrupt)
					break;
				
			}// end for on entries
		}
	}
	
	
	
	
	
}
