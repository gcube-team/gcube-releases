package org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts;

import java.util.ArrayList;
import java.util.List;


public class MatcherInput {

	
	public List<SingleEntry> entries = new ArrayList<SingleEntry>();
	
	public MatcherInput(){
		
	}

	public void addEntry(int id, String originalName, String parsedScientificName,String parsedAuthorship, List<String> otherElements){
		entries.add(new SingleEntry(id, originalName, parsedScientificName, parsedAuthorship, otherElements));
	}
	
	public int getEntriesNumber(){
		return entries.size();
	}
	
	public SingleEntry getEntry(int index){
		return entries.get(index);
	}
	
}

