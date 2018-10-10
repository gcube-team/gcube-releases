package org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.treeStructure.graph;

import java.math.BigInteger;

import org.jgrapht.graph.DefaultWeightedEdge;

public class RelationEdge extends DefaultWeightedEdge{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private String relationName;
	private long indexFrom;
	private long indexTo;
	private BigInteger weight;

	private String categoryFrom;
	private String categoryTo;
	
	
	public BigInteger getWeigth(){
		return weight;
	}
	
	public void setWeigth(BigInteger Weight){
		weight = Weight;
	}
	
	public long getTo(){
		return indexTo;
	}
	public long getFrom(){
		return indexFrom;
	}
	public String getName(){
		return relationName;
	}
	public void setName(String name){
		relationName = name;
	}
	
	public RelationEdge(String name,long from,long to){
		relationName = name;
		indexFrom = from;
		indexTo = to;
	}
	@Override
	public String toString(){
		return "["+relationName+": from "+indexFrom+" to " +indexTo+" nameFrom "+categoryFrom+" nameTo "+categoryTo+"]";
	}

	public void setCategoryFrom(String categoryFrom) {
		this.categoryFrom = categoryFrom;
	}

	public String getCategoryFrom() {
		return categoryFrom;
	}

	public void setCategoryTo(String categoryTo) {
		this.categoryTo = categoryTo;
	}

	public String getCategoryTo() {
		return categoryTo;
	}
		
}
