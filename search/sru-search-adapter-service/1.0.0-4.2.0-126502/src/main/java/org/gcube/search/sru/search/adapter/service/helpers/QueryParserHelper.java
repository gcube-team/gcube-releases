package org.gcube.search.sru.search.adapter.service.helpers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class QueryParserHelper {
	
	
	public String getQueryPart(String query){
		List<String> queryParts = Splitter.on(" project ").omitEmptyStrings().splitToList(query);
		
		String queryPart = null;
		if (queryParts.size() > 2){
			queryPart = Joiner.on(" ").join(queryParts.subList(0, queryParts.size() - 2));
		} else {
			queryPart = queryParts.get(0);
		}
		
		return queryPart;
	}
	
	public List<String> getProjectPart(String query, List<String> presentablesFields){
		if (!query.contains(" project ")){
			return Lists.newArrayList(presentablesFields);
		}
		
		
		String projectPart = Iterables.getLast(Splitter.on(" project ").omitEmptyStrings().splitToList(query));
		
		List<String> projections = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings().splitToList(projectPart);
		if (projections.size() == 1 && projections.get(0).equals("*")){
			return Lists.newArrayList(presentablesFields);
		}
		
		return projections;
	}
	
	
	public String replaceFields(String query, Map<String, String> fields){
		String newQuery = query;
		
		for (Entry<String, String> entry : fields.entrySet()){
			String fieldName = entry.getKey();
			String fieldID = entry.getValue();
			
			if (fieldName.equalsIgnoreCase("gDocCollectionID") || 
				fieldName.equalsIgnoreCase("gDocCollectionLang") ||
				fieldName.equalsIgnoreCase("ObjectID"))
				continue;
			
			newQuery = newQuery.replace(fieldName, fieldID);
		}
		
		newQuery = newQuery.replace("cql.", "");
		newQuery = newQuery.replace("oai_dc.", "");
		newQuery = newQuery.replace("dc.", "");
		
		return newQuery;
	}
	
	public static void main(String[] args) {
		QueryParserHelper qh = new QueryParserHelper();
		final String query = "select 1 2 from table project 1 2 3 4 ";
		
		Map<String, String> fieldsMapping = Maps.newHashMap();
		fieldsMapping.put("1", "name");
		fieldsMapping.put("2", "age");
		
		
		System.out.println("query part     : " + qh.getQueryPart(query));
		System.out.println("project part   : " + qh.getProjectPart(query, Lists.newArrayList(fieldsMapping.keySet())));
		
		System.out.println("replaced query : " + qh.replaceFields(query, fieldsMapping));
		
		
		
	}
	
	
}
