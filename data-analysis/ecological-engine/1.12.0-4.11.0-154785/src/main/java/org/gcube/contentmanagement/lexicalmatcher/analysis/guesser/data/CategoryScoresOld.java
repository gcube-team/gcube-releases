package org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.data;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

//score relative to a certain category and column

public class CategoryScoresOld {

	// column names vs percentage
	private HashMap<String, BigDecimal> columnsScore;

	private BigDecimal maximumElements;

	public CategoryScoresOld(BigInteger maxelements) {
		this.maximumElements = new BigDecimal(maxelements);
		columnsScore = new HashMap<String, BigDecimal>();
	}

	public void setMaximumElements(BigDecimal MaximumElements) {
		maximumElements = MaximumElements;
	}

	public void incrementScore(String columnName,float increment) {

		BigDecimal score = columnsScore.get(columnName);

		BigDecimal reciproc = BigDecimal.valueOf(increment);

		if (score == null) {
			// build up a new score : 1/TOTAL
			score = reciproc;
		} else {
			score = score.add(reciproc);
		}
		columnsScore.put(columnName, score);
		// AnalysisLogger.getLogger().debug("CategoryOrderedList->checkUnkEntriesOnEntireCategory-> SCORE "+score);
	}

	public double getScore(String columnName) {

		double score = 0;
		try {

			BigDecimal percentage = columnsScore.get(columnName);
			try {
				if (percentage == null)
					percentage = BigDecimal.ZERO;

				AnalysisLogger.getLogger().trace("getScore -> Score for "+columnName+": " + percentage + " vs " + maximumElements);
				percentage = percentage.divide(maximumElements, 2, BigDecimal.ROUND_DOWN);
			} catch (ArithmeticException e) {
				percentage = BigDecimal.ZERO;
				e.printStackTrace();
			}

			score = percentage.doubleValue();
		} catch (Exception e) {
		}
		return score;

	}

	// take the best performing column
	public String findBest() {

		String bestCol = null;
		BigDecimal bestscore = BigDecimal.valueOf(-1);

		for (String column : columnsScore.keySet()) {

			BigDecimal score = BigDecimal.ZERO;
			try {
				score = columnsScore.get(column);
			} catch (Exception e) {
				AnalysisLogger.getLogger().error("ERROR in getting SCORE " + e.getLocalizedMessage());
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

			BigDecimal score = BigDecimal.ZERO;

			try {
				score = columnsScore.get(column);
			} catch (Exception e) {
				AnalysisLogger.getLogger().error("ERROR in getting SCORE " + e.getLocalizedMessage());
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

}
