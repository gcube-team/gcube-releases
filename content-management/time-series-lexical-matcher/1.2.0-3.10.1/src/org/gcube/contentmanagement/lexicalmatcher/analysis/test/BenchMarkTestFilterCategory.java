package org.gcube.contentmanagement.lexicalmatcher.analysis.test;

import java.util.ArrayList;

import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.data.SingleResult;
import org.gcube.contentmanagement.lexicalmatcher.analysis.run.CategoryGuesser;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;


public class BenchMarkTestFilterCategory {

	public static void main(String[] args) {

		try {
			
			String configPath = ".";
			CategoryGuesser guesser = new CategoryGuesser(configPath);
			//bench 1 
			AnalysisLogger.getLogger().warn("----------------------BENCH 1-------------------------");
			String seriesName = "ref_order";
			String column = "scientific_name";
			String correctFamily = "order";
			String correctColumn = "scientific_name";
			
			guesser.runGuesser(seriesName, column, null, correctFamily, correctColumn);
			ArrayList<SingleResult> results = guesser.getClassification(); 
			
			CategoryGuesser.showResults(results);
			
			AnalysisLogger.getLogger().warn("--------------------END BENCH 1-----------------------\n");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
