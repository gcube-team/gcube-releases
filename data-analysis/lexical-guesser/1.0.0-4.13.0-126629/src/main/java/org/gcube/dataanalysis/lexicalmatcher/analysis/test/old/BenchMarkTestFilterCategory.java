package org.gcube.dataanalysis.lexicalmatcher.analysis.test.old;

import java.util.ArrayList;

import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data.SingleResult;
import org.gcube.dataanalysis.lexicalmatcher.analysis.run.CategoryGuesser;
import org.gcube.dataanalysis.lexicalmatcher.utils.LexicalLogger;


public class BenchMarkTestFilterCategory {

	public static void main(String[] args) {

		try {
			
			String configPath = ".";
			CategoryGuesser guesser = new CategoryGuesser();
			//bench 1 
			LexicalLogger.getLogger().warn("----------------------BENCH 1-------------------------");
			String seriesName = "ref_order";
			String column = "scientific_name";
			String correctFamily = "order";
			String correctColumn = "scientific_name";
			
			guesser.runGuesser(seriesName, column, null, correctFamily, correctColumn);
			ArrayList<SingleResult> results = guesser.getClassification(); 
			
			CategoryGuesser.showResults(results);
			
			LexicalLogger.getLogger().warn("--------------------END BENCH 1-----------------------\n");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
