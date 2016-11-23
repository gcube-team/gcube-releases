package org.gcube.search.datafusion.datatypes;

import java.io.Serializable;

/**
 * 
 * @author Alex Antoniadis
 * 
 */
public class Pair implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Float score;
	private Integer luceneDocID;

	public Pair(Float score, Integer luceneDocID) {
		this.score = score;
		this.luceneDocID = luceneDocID;
	}

	@Override
	public String toString() {
		return "Pair [score=" + getScore() + ", luceneDocID=" + getLuceneDocID() + "]";
	}

	public Float getScore() {
		return score;
	}

	public Integer getLuceneDocID() {
		return luceneDocID;
	}

}
