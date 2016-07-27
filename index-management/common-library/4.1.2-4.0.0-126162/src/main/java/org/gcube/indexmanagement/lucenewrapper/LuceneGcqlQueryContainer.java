package org.gcube.indexmanagement.lucenewrapper;

import java.util.LinkedHashMap;

import org.gcube.indexmanagement.gcqlwrapper.GcqlQueryContainer;

public class LuceneGcqlQueryContainer extends GcqlQueryContainer{
	private QuerySnippetTermsPair luceneQuery;
	private boolean distinct = false;
	
	public LuceneGcqlQueryContainer(QuerySnippetTermsPair luceneQuery, LinkedHashMap<String, String> projectedFields,  boolean distinct) {
		super(projectedFields);
		this.luceneQuery = luceneQuery;		
		this.distinct = distinct;
	}
	
	public void setLuceneQuery(QuerySnippetTermsPair luceneQuery) {
		this.luceneQuery = luceneQuery;
	}

	public QuerySnippetTermsPair getLuceneQuery() {
		return luceneQuery;
	}
	
	public boolean getDistinct() {
		return distinct ;
	}
	
}
