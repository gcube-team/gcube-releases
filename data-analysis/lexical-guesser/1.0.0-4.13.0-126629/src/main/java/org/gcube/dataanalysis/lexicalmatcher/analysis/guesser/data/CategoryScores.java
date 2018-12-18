package org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.dataanalysis.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.dataanalysis.lexicalmatcher.utils.LexicalLogger;
import org.gcube.dataanalysis.lexicalmatcher.utils.MathFunctions;

//score relative to a certain category and column

public class CategoryScores {

	// column names vs percentage
	private HashMap<String, Float> columnsScore;

	private int matchedElements;
	private BigInteger maxElements;
	private BigInteger categoryElements;
	private LexicalEngineConfiguration config;
	
	public CategoryScores(BigInteger catElements, LexicalEngineConfiguration Config) {
		columnsScore = new HashMap<String, Float>();
		matchedElements = 0;
		setCategoryElements(catElements);
		config = Config;
		maxElements = calculateMaxElements(catElements);
	}

	public double calculateCoverage(){
		
		double bd = new BigDecimal(matchedElements).divide(new BigDecimal(maxElements), 2, BigDecimal.ROUND_FLOOR).doubleValue();
		
		//lower poor categories
		if (maxElements.compareTo(BigInteger.valueOf(config.chunkSize))<=0)
			bd = bd *0.8;
		
		//To-DO take into observation!!!
		//higher very big set coverage
		if (categoryElements.compareTo(BigInteger.valueOf(10000))>0)
			bd = Math.max(0.01, bd);
		
		return bd;
	}
	
	private BigInteger calculateMaxElements(BigInteger catElements){
		BigInteger maxElements = BigInteger.ZERO;
		
		int maxNumberOfChunks = config.ReferenceChunksToTake;
		int chunkSize = config.chunkSize;
		int numberofcycles=0;
		
		if (maxNumberOfChunks<0)
			return catElements;
		try{
			BigDecimal intcycles;
			BigDecimal oddcycles;
			BigDecimal catElementsDecimal = new BigDecimal(catElements);
			BigDecimal[] arraydecimal = catElementsDecimal.divideAndRemainder(new BigDecimal(BigInteger.valueOf(chunkSize)));
			intcycles = arraydecimal[0];
			oddcycles = arraydecimal[1];
			numberofcycles = intcycles.intValue();
			if ((numberofcycles==0)&&(oddcycles.intValue() > 0)) {
				numberofcycles = numberofcycles + 1;
				maxElements = oddcycles.toBigInteger();
			}
			else{
				if (numberofcycles>maxNumberOfChunks)
					numberofcycles = maxNumberOfChunks;
				
				maxElements = BigInteger.valueOf(chunkSize).multiply(BigInteger.valueOf(numberofcycles));
			}
		
		}catch(Exception e){}
		
		return maxElements;
	}
	
	
	public String showScores(){
		return columnsScore.toString()+":"+calculateCoverage(); //+" - "+matchedElements+" vs "+maxElements;
	}
	
	public void incrementScore(String columnName,float increment,boolean doIncrementMathes) {

		Float score = columnsScore.get(columnName);
		
		if (score==null)
			score =new Float(0);
		
		score = MathFunctions.incrementPerc(score, increment, matchedElements);
		
		if (doIncrementMathes)
			matchedElements ++;
		
		columnsScore.put(columnName, score);
	}


	public float getScore(String columnName,boolean simpleMatch) {

		if (simpleMatch){
			return getSimpleScore(columnName);
		}
		else
			return getScore(columnName);
	}

	
	public float getScore(String columnName) {

		Float score = null;
		try {
//			score = columnsScore.get(columnName)*(float)calculateCoverage();
			score = columnsScore.get(columnName);
			if (score!=null){
				return score*(float)calculateCoverage();
			}
		} catch (Exception e) {
		}
		return score;

	}

	public float getSimpleScore(String columnName) {

		Float score = null;
		try {
//			score = columnsScore.get(columnName)*(float)calculateCoverage();
			score = columnsScore.get(columnName);
			if (score!=null){
				return score;
			}
		} catch (Exception e) {
		}
		return score;

	}
	
	// take the best performing column
	public String findBest() {

		String bestCol = null;
		Float bestscore = Float.valueOf(-1);

		for (String column : columnsScore.keySet()) {

			Float score = new Float(0);
			try {
				score = columnsScore.get(column);
			} catch (Exception e) {
				LexicalLogger.getLogger().error("ERROR in getting SCORE " + e.getLocalizedMessage());
			}
			if (bestscore.compareTo(score) < 0) {
				bestscore = score;
				bestCol = column;
			}
		}

		return bestCol;
	}

	// take the best performing columns
	public ArrayList<String> findBestList() {

		ArrayList<String> bestCols = new ArrayList<String>();

		for (String column : columnsScore.keySet()) {

			Float score = new Float(0);

			try {
				score = columnsScore.get(column);
			} catch (Exception e) {
				LexicalLogger.getLogger().error("ERROR in getting SCORE " + e.getLocalizedMessage());
			}

			// find best place where to put column
			int size = bestCols.size();
			int index = size;
			for (int i = 0; i < size; i++) {
				if (columnsScore.get(bestCols.get(i)).compareTo(score) <= 0) {
					index = i;
					break;
				}
			}
			bestCols.add(index, column);

		}

		return bestCols;
	}

	public void setCategoryElements(BigInteger categoryElements) {
		this.categoryElements = categoryElements;
	}

	public BigInteger getCategoryElements() {
		return categoryElements;
	}

}
