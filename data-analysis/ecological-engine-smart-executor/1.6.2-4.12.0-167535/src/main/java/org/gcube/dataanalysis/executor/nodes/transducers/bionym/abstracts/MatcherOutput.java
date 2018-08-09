package org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts;

import java.util.ArrayList;
import java.util.List;


public class MatcherOutput {
	
	public List<SingleEntry> entries = new ArrayList<SingleEntry>();
	
	public MatcherOutput(){
	}
	
	public void addEntry(int id, String originalName, String parsedScientificName,String parsedAuthorship, double matchingScore, String targetDataSource, String targetID, String targetScientificName, String targetAuthor, List<String> otherElements){
		entries.add(new SingleEntry(id, originalName, parsedScientificName,parsedAuthorship, matchingScore, targetDataSource, targetID, targetScientificName, targetAuthor, otherElements));
	}

	public int getEntriesNumber(){
		return entries.size();
	}
	
	public SingleEntry getEntry(int index){
		return entries.get(index);
	}
	
	public boolean contains(String inputName, String scientificName, String author,String targetID){
		for (SingleEntry entry: entries){
				if (entry.originalName.equals(inputName)
					&&
					(entry.targetScientificName.equals(scientificName))
					&& 
					(entry.targetAuthor.equals(author))
					&&		
					(entry.targetID.equals(targetID)))
					return true;
		}
		return false;
	}
	
}
