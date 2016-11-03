package org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.data;

import java.math.BigInteger;

public class RelationEdge {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private String relationName;
	private String indexFrom;
	private String indexTo;
	private BigInteger weight;

	private String categoryFrom;
	private String categoryTo;
	
	
	public BigInteger getWeigth(){
		return weight;
	}
	
	public void setWeigth(BigInteger Weight){
		weight = Weight;
	}
	
	public String getTo(){
		return indexTo;
	}
	public String getFrom(){
		return indexFrom;
	}
	public String getName(){
		return relationName;
	}
	public void setName(String name){
		relationName = name;
	}
	
	public RelationEdge(String name,String from,String to){
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
