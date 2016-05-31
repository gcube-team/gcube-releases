package gr.uoa.di.madgik.workflow.adaptor.search.utils;

import java.util.LinkedHashMap;

public class LuceneGcqlQueryContainer extends GcqlQueryContainer{
	private QuerySnippetTermsPair luceneQuery;

	public LuceneGcqlQueryContainer(QuerySnippetTermsPair luceneQuery, LinkedHashMap<String, String> projectedFields) {
		super(projectedFields);
		this.luceneQuery = luceneQuery;		
	}
	
	public void setLuceneQuery(QuerySnippetTermsPair luceneQuery) {
		this.luceneQuery = luceneQuery;
	}

	public QuerySnippetTermsPair getLuceneQuery() {
		return luceneQuery;
	}
	
}
