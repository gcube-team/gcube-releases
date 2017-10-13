package gr.cite.geoanalytics.client;

import java.util.HashSet;
import java.util.Set;

import org.apache.spark.api.java.function.Function2;


public class Reducer implements Function2<Set<Boolean>, Set<Boolean>, Set<Boolean>> {

	private static final long serialVersionUID = 1923332794337126038L;

	@Override
	public Set<Boolean> call(Set<Boolean> resultsL, Set<Boolean> resultsR) {
		Set<Boolean> merged = new HashSet<Boolean>();
		merged.addAll(resultsL);
		merged.addAll(resultsR);
		return (HashSet<Boolean>) merged;
	}

	
}
