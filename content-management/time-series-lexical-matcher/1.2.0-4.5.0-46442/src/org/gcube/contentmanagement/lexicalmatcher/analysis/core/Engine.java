package org.gcube.contentmanagement.lexicalmatcher.analysis.core;


import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.data.CategoryOrderedList;
import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.data.CategoryScores;
import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.data.SingleResult;
import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.treeStructure.chunks.ChunkSet;
import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.treeStructure.chunks.ReferenceChunk;
import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.treeStructure.chunks.ReferenceChunkSet;
import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.treeStructure.chunks.SetOfReferenceChunkSet;
import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.treeStructure.chunks.SingletonChunkSet;
import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.treeStructure.chunks.TimeSeriesChunk;
import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.treeStructure.chunks.TimeSeriesChunkSet;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.gcube.contentmanagement.lexicalmatcher.utils.MathFunctions;
import org.hibernate.SessionFactory;

public class Engine {

	private String ConfigurationFileNameLocal = "hibernate.cfg.xml";
	private SessionFactory referenceDBSession;

	public ArrayList<String> bestCategories;
	public ArrayList<Double> bestScores;
	public ArrayList<String> bestColumns;
	public HashMap<String, CategoryScores> scoresTable;
	public String columnFilter;
	private LexicalEngineConfiguration config;
	private TimeSeriesChunk singletonChunk;
	
	public ArrayList<SingleResult> getSingletonMatches(){
		return singletonChunk.getDetailedResults();
	}
	
	public String getSingletonElement(){
		return singletonChunk.getSingletonEntry();
	}
	
	public SessionFactory getDBSession() throws Exception {

		if (referenceDBSession == null) {
			referenceDBSession = DatabaseFactory.initDBConnection(ConfigurationFileNameLocal);
		}

		return referenceDBSession;
	}

	public SessionFactory getDBSession(LexicalEngineConfiguration externalConf) throws Exception {

		if (referenceDBSession == null) {
			referenceDBSession = DatabaseFactory.initDBConnection(ConfigurationFileNameLocal, externalConf);
		}

		return referenceDBSession;
	}

	public void resetEngine(LexicalEngineConfiguration Config,String ColumnFilter,String configPath){
		config = Config;
		scoresTable = new HashMap<String, CategoryScores>();
		bestCategories = new ArrayList<String>();
		bestColumns = new ArrayList<String>();
		bestScores = new ArrayList<Double>();
		columnFilter = ColumnFilter;
//		ConfigurationFileNameLocal = configPath+"/"+ConfigurationFileNameLocal;
	}
	
	public Engine(LexicalEngineConfiguration Config,String ColumnFilter,String configPath) {
		config = Config;
		scoresTable = new HashMap<String, CategoryScores>();
		bestCategories = new ArrayList<String>();
		bestColumns = new ArrayList<String>();
		bestScores = new ArrayList<Double>();
		columnFilter = ColumnFilter;
		ConfigurationFileNameLocal = configPath+"/"+ConfigurationFileNameLocal;
	}

	public void calcLike(CategoryOrderedList col, String unknownSeriesName, String unknownSeriesColumn) {
		scoresTable = col.getScoresTable();

		// take a time series set of chunks
		TimeSeriesChunkSet tsChunkSet = null;
		try {
			tsChunkSet = new TimeSeriesChunkSet(config.TimeSeriesChunksToTake, config.chunkSize, unknownSeriesName, unknownSeriesColumn,config, this);
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().error("Engine->calcLike->  ERROR could not retrieve time series chunks " + e.getLocalizedMessage());
		}
		// if we took the ts chunk set correctly perform calculation
		if (tsChunkSet != null) {

			// generate the set of reference chunks
			SetOfReferenceChunkSet setRefChunksSet = new SetOfReferenceChunkSet(col.getOrderedList(),config, this);

			TimeSeriesChunk tsChunk = tsChunkSet.nextChunk();
			// for all ts chunks
			while (tsChunk != null) {

				// take a set of chunks from a reference category
				ReferenceChunkSet refChunkSet = setRefChunksSet.getNextChunkSet();
				while (refChunkSet != null) {
					// take a chunk in the reference chunk set
					ReferenceChunk refChunk = refChunkSet.nextChunk();
					while (refChunk != null) {

						try {
							tsChunk.compareToReferenceChunk(scoresTable, refChunk);
						} catch (Exception e) {
							e.printStackTrace();
							AnalysisLogger.getLogger().error("Engine->calcLike->  ERROR could not compare time series chunk with reference chunk " + e.getLocalizedMessage());
						}
						// take another chunk in the reference chunk set
						refChunk = refChunkSet.nextChunk();
					}

					// check score
					UpdateScores(refChunkSet.getSeriesName(),false);

					// take another set of chunks from another reference category
					refChunkSet = setRefChunksSet.getNextChunkSet();
				}

				tsChunk = tsChunkSet.nextChunk();
			}

		}

	}

	boolean threadActivity[];
	
	private void wait4Thread(int index){
		
		
			// wait until thread is free
			while (threadActivity[index]) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
			}
			
		
	}
	
	private void startNewTCalc(TimeSeriesChunk tsChunk, ReferenceChunkSet refChunkSet,int index){
		
		threadActivity[index] = true;
		ThreadCalculator tc = new ThreadCalculator(tsChunk, refChunkSet,index);
		Thread t = new Thread(tc);
		t.start();
//		AnalysisLogger.getLogger().info("ThreadCalculator<-go "+index);
	}
		

	public void calcLikeThread(CategoryOrderedList col, String unknownSeriesName, String unknownSeriesColumn,String singletonString) {
		scoresTable = col.getScoresTable();

		// take a time series set of chunks
		ChunkSet tsChunkSet = null;
		int[] currentThreads = MathFunctions.generateSequence(config.numberOfThreadsToUse);
		int currentThread = 0;
		threadActivity = new boolean [currentThreads.length];
		//initialize to false;
		for (int j=0;j<threadActivity.length;j++){
			threadActivity[j] = false;
		}
		
		
		try {
			
			if (singletonString==null)
				tsChunkSet = new TimeSeriesChunkSet(config.TimeSeriesChunksToTake, config.chunkSize, unknownSeriesName, unknownSeriesColumn,config, this);
			else{
				
				tsChunkSet = new SingletonChunkSet(singletonString,config, this);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().error("Engine->calcLike->  ERROR could not retrieve time series chunks " + e.getLocalizedMessage());
		}
		// if we took the ts chunk set correctly perform calculation
		if (tsChunkSet != null) {

			// generate the set of reference chunks
			SetOfReferenceChunkSet setRefChunksSet = new SetOfReferenceChunkSet(col.getOrderedList(),config, this);

			TimeSeriesChunk tsChunk = (TimeSeriesChunk)tsChunkSet.nextChunk();
			
			AnalysisLogger.getLogger().debug("tsChunk is null "+(tsChunk != null));
			// for all ts chunks
			while (tsChunk != null) {

				// take a set of chunks from a reference category
				ReferenceChunkSet refChunkSet = setRefChunksSet.getNextChunkSet();
				while (refChunkSet != null) {
					wait4Thread(currentThreads[currentThread]);
					startNewTCalc(tsChunk, refChunkSet,currentThreads[currentThread]);
					
//					makeComparisonsTSChunk2RefChunks(tsChunk, refChunkSet);

					// take another set of chunks from another reference category
					refChunkSet = setRefChunksSet.getNextChunkSet();

					currentThread++;
					if (currentThread >= currentThreads.length)
						currentThread = 0;
				}
				
				
				//if the chunk is a singleton, don't process other and record the result 
				if (tsChunk.isSingleton()){
					singletonChunk = tsChunk;
					
					break;
				}	
				
				tsChunk = (TimeSeriesChunk)tsChunkSet.nextChunk();
			}
			
			//wait for last threads to finish
			for (int i : currentThreads) {
				// free previous calculation
				wait4Thread(i);
			}
			
		}

	}

	private void makeComparisonsTSChunk2RefChunks(TimeSeriesChunk tsChunk, ReferenceChunkSet refChunkSet) {

		// take a chunk in the reference chunk set
		ReferenceChunk refChunk = refChunkSet.nextChunk();
		while (refChunk != null) {

			try {
				tsChunk.compareToReferenceChunk(scoresTable, refChunk,columnFilter);
			} catch (Exception e) {
				e.printStackTrace();
				AnalysisLogger.getLogger().error("Engine->calcLike->  ERROR could not compare time series chunk with reference chunk " + e.getLocalizedMessage());
			}
			
			//if the TimeSeries chunk states the processing must be interrupted, don't perform other comparisons
			if (tsChunk.mustInterruptProcess())
				break;
			
			// take another chunk in the reference chunk set
			refChunk = refChunkSet.nextChunk();
			
		}
		// check score
		UpdateScores(refChunkSet.getSeriesName(),tsChunk.isSingleton());
	}

	private void UpdateScores(String categoryName, boolean singletonMatch) {

		CategoryScores categoryScore = scoresTable.get(categoryName);
		ArrayList<String> bestCols = categoryScore.findBestList();
		String bestColumn = null;
		double score = 0;
		if (bestCols.size() > 0) {
			bestColumn = bestCols.get(0);
			score = categoryScore.getScore(bestColumn,singletonMatch);
		}

		AnalysisLogger.getLogger().trace("Engine->UpdateScores->  \tBEST SUITABLE COLUMN IS: " + bestColumn);
		AnalysisLogger.getLogger().trace("Engine->UpdateScores->  \tBEST SCORE IS: " + score);

		// order this column
		if (score > config.categoryDiscardThreshold) {

			int index = 0;
			// insert at the right point in the classification
			for (Double dscore : bestScores) {
				if (dscore.doubleValue() < score) {

					break;
				}
				index++;
			}
			bestCategories.add(index, categoryName);
			bestScores.add(index, score);
			bestColumns.add(index, bestColumn);
			checkAndAddColumns(categoryScore, bestCols, categoryName,singletonMatch);
		}

	}

	private void checkAndAddColumns(CategoryScores scores, ArrayList<String> bestCols, String categoryName,boolean singletonMatch) {

		int size = bestCols.size();
		double bestScore = scores.getScore(bestCols.get(0),singletonMatch);

		for (int i = 1; i < size; i++) {
			// take the i-th column
			String column = bestCols.get(i);
			if (column != null) {
				// check the score
				double score = scores.getScore(column,singletonMatch);

				// if the score is near the best, add the column
				if ((score > 0) && (score >= (bestScore - 0.5 * bestScore))) {
					
					int index = 0;
					// insert at the right point in the classification
					for (Double dscore : bestScores) {
						if (dscore.doubleValue() < score) {

							break;
						}
						index++;
					}
					
					// AnalysisLogger.getLogger().info("chechAndAddColumns -> column to add "+column+" category "+categoryName+" with value "+score+" previous "+(bestScore - 0.5 * bestScore));
					bestColumns.add(index,column);
					bestScores.add(index,score);
					bestCategories.add(index,categoryName);
					// AnalysisLogger.getLogger().info("chechAndAddColumns -> "+bestCategories);
				}
			}
		}

	}

	private class ThreadCalculator implements Runnable {
		TimeSeriesChunk tsChunk;
		ReferenceChunkSet refChunksSet;
		int index;

		public ThreadCalculator(TimeSeriesChunk tsChunk, ReferenceChunkSet refChunksSet,int index) {
			this.tsChunk = tsChunk;
			this.refChunksSet = refChunksSet;
			this.index = index;
		}

		public void run() {
//			AnalysisLogger.getLogger().info("ThreadCalculator->started "+index);
			makeComparisonsTSChunk2RefChunks(tsChunk, refChunksSet);
			threadActivity[index]=false;
//			AnalysisLogger.getLogger().info("ThreadCalculator>-finished "+index);
		}

	}

}
