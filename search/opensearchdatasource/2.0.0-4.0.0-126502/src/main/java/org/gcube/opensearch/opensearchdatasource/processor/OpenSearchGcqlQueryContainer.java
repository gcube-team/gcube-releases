package org.gcube.opensearch.opensearchdatasource.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OpenSearchGcqlQueryContainer extends GcqlQueryContainer{
	public HashMap<String, HashMap<String, ArrayList<OpenSearchGcqlCollectionQuery>>> queries = new HashMap<String, HashMap<String,ArrayList<OpenSearchGcqlCollectionQuery>>>();

	public QueriesContainer getArrayOfQueries() {
		
		ArrayList<String> colIDs =  new ArrayList<String>();
		ArrayList<String> langs = new ArrayList<String>();
		ArrayList<OpenSearchGcqlCollectionQuery> colQueries = new ArrayList<OpenSearchGcqlCollectionQuery>();
		
		//for all the collection IDs
		for(Map.Entry<String, HashMap<String, ArrayList<OpenSearchGcqlCollectionQuery>>> outerCurrent : queries.entrySet()) {
			String colID = outerCurrent.getKey();
			HashMap<String, ArrayList<OpenSearchGcqlCollectionQuery>> outerMap = outerCurrent.getValue();
			if(outerMap == null)
				continue;
			//for all the languages
			for(Map.Entry<String, ArrayList<OpenSearchGcqlCollectionQuery>> innerCurrent : outerMap.entrySet()) {
				String lang = innerCurrent.getKey();
				ArrayList<OpenSearchGcqlCollectionQuery> list = innerCurrent.getValue();
				if(list == null)
					continue;
				
				//for all the queries
				for(OpenSearchGcqlCollectionQuery query : list) {
					colIDs.add(colID);
					langs.add(lang);
					colQueries.add(query);
				}
			}
		}
		
		return new QueriesContainer(colIDs, langs, colQueries);
	}
	
	public static class QueriesContainer {
		private ArrayList<String> colIDs;
		private ArrayList<String> langs;
		private ArrayList<OpenSearchGcqlCollectionQuery> queries;
		
		public QueriesContainer(ArrayList<String> colIDs, ArrayList<String> langs, 
				ArrayList<OpenSearchGcqlCollectionQuery> queries) {
			this.colIDs = colIDs;
			this.langs = langs;
			this.queries = queries;
		}
		
		public ArrayList<String> getColIDs() {
			return colIDs;
		}
		
		public ArrayList<String> getLangs() {
			return langs;
		}
		
		public ArrayList<OpenSearchGcqlCollectionQuery> getQueries() {
			return queries;
		}
	}
	public OpenSearchGcqlQueryContainer(Map<String, String> projectedFields) {
		super(projectedFields);
	}
	
}
