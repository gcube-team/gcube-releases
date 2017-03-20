package org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts;

import java.util.List;

public class SingleEntry {
	public int id;
	public String originalName;
	public String parsedScientificName;
	public String parsedAuthorship;
	public double matchingScore;
	public String targetDataSource;
	public String targetID;
	public String targetScientificName;
	public String targetAuthor;
	public List<String> otherElements;
	
	public SingleEntry(int id, String originalName, String parsedScientificName,String parsedAuthorship, double matchingScore, String targetDataSource, String targetID, String targetScientificName, String targetAuthor, List<String> otherElements){
		this.id=id;
		this.originalName=originalName;
		this.parsedScientificName=parsedScientificName;
		this.parsedAuthorship=parsedAuthorship;
		this.otherElements=otherElements;
		this.matchingScore=matchingScore;
		this.targetDataSource=targetDataSource;
		this.targetID=targetID;
		this.targetScientificName=targetScientificName;
		this.targetAuthor=targetAuthor;
	}
	
	public SingleEntry(int id, String originalName, String parsedScientificName,String parsedAuthorship, List<String> otherElements){
		this(id,originalName,parsedScientificName,parsedAuthorship,0,"","","","",otherElements);
	}
	
	@Override
	public String toString(){
		return "\""+id+"\";\""+originalName+"\";\""+targetID+"\";\""+targetScientificName+"\";\""+targetAuthor+"\";\""+matchingScore+"\"";
	} 
}
