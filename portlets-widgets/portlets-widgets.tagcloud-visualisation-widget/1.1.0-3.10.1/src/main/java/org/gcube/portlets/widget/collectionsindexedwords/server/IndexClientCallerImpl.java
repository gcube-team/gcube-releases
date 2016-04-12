package org.gcube.portlets.widget.collectionsindexedwords.server;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.search.library.exception.InitialBridgingNotCompleteException;
import org.gcube.application.framework.search.library.exception.InternalErrorException;
import org.gcube.application.framework.search.library.impl.SearchHelper;
import org.gcube.application.framework.search.library.model.CollectionInfo;
import org.gcube.application.framework.search.library.model.Query;
import org.gcube.application.framework.search.library.model.QueryGroup;
import org.gcube.portlets.widget.collectionsindexedwords.client.exceptions.DataException;
import org.gcube.portlets.widget.collectionsindexedwords.client.exceptions.OnlyOpensearchException;
import org.gcube.portlets.widget.collectionsindexedwords.client.rpc.IndexClientCaller;
import org.gcube.portlets.widget.collectionsindexedwords.shared.IndexData;
import org.gcube.rest.index.client.IndexClient;
import org.gcube.rest.index.client.exceptions.IndexException;
import org.gcube.rest.index.common.entities.ClusterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class IndexClientCallerImpl extends RemoteServiceServlet  implements IndexClientCaller {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(IndexClientCallerImpl.class);
	
	@Override
	public void init() throws ServletException {
		super.init();
	}
	
	
	public String getClusterValues(Integer queryID) throws Exception{
		
		ASLSession session = getASLSession();
		SearchHelper sh = new SearchHelper(session);
		QueryGroup queries = sh.getQuery(queryID);
		logger.debug("Found "+queries.getQueries().size()+" queries for ID: "+queryID);
		Query q = queries.getQuery(0);
		String queryStr = q.getQueryString();
		String parentScope = session.getParentScope();
		String searchTerm = q.getSearchTerm();
		logger.debug("queryStr: "+queryStr);
		logger.debug("parentScope: "+parentScope);
		logger.debug("searchTerm: "+searchTerm);
		
//		//for testing, comment out all lines above and use the three ones below: 
//		String parentScope = "/gcube/devNext";
//		String queryStr = "((631585d0-7120-4b01-a14e-c5a9407a6dbc = fish) and (gDocCollectionID == 55b15c13-0ef7-4d7d-90f3-4e993c664b92)) project 553b9afb-f671-4c58-a38e-94d88c45387b f1432f0f-c45e-41d8-af73-b80646197f41";
//		String searchTerm = "fish";
		
		IndexClient.Builder indexClientBuilder = new IndexClient.Builder();
		logger.debug("Creating index client for scope: "+parentScope);
		IndexClient cl = indexClientBuilder.scope(parentScope).build();
		
		
		List<ClusterResponse> clustering = cl.clustering(queryStr, searchTerm, null, 10, "ObjectID", Lists.newArrayList("title"), Lists.newArrayList("description"), Lists.newArrayList("gDocCollectionLang"), "lingo", 1000);
		
		//construct the json
		StringBuffer sb = new StringBuffer();
		String prefix = "";
		sb.append("{\"name\": \"Index Clusters\",\"children\": [");
		for (ClusterResponse cr : clustering){
			sb.append(prefix);
			sb.append("{\"name\": \""+cr.getClusterName()+" ("+cr.getScore()+")"+"\",\"children\": [");
			if(!cr.getDocs().isEmpty())
				sb.append("{\"name\": \""+limiter(cr.getDocs().get(0))+"\",\"size\": "+"1"+" }");
			for(int j=1;j<cr.getDocs().size();j++)
				sb.append(",{\"name\": \""+limiter(cr.getDocs().get(j))+"\",\"size\": "+"1"+" }");
			sb.append("]}");
			prefix = ",";
		}
		sb.append("]}");
		return sb.toString();
		
	}
	
	private static String limiter(String input){
		//limit characters
		final int upToLength = 30;
		if(input.length()>upToLength)
			input = input.substring(0, upToLength)+"...";
		//replace " with '
		return input.replace("\"", "'");
	}
	
	public IndexData getValues(Integer queryID, Integer maxStats) throws  OnlyOpensearchException, DataException {

		try{
			ASLSession session = getASLSession();
			
			SearchHelper sh = new SearchHelper(session);
			QueryGroup queries = sh.getQuery(queryID);
			logger.debug("Found "+queries.getQueries().size()+" queries for ID: "+queryID);
			Query q = queries.getQuery(0);
			
			String queryStr = q.getQueryString();
	//		logger.debug("Query string: "+queryStr);
			
			/////DO EITHER THIS://////
			HashMap<CollectionInfo, ArrayList<CollectionInfo>> availableCollections = sh.getAvailableCollections();
			Iterator<Entry<CollectionInfo, ArrayList<CollectionInfo>>> iterator = availableCollections.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<CollectionInfo, ArrayList<CollectionInfo>> entry = iterator.next();
				for (CollectionInfo collection : entry.getValue()) {
					if (("opensearch").equalsIgnoreCase(collection.getCollectionType())) {
						logger.debug("Filtered opensearch collection with id: "+collection.getId()+" from index visualisation query");
						queryStr = filterCollection(queryStr, collection.getId());
					}
				}
			}
			//by now, the opensearch collections are thrown out of the query. if no collection is still there, throw exception
			if(!queryStr.contains("gDocCollectionID")) 
				throw new OnlyOpensearchException();
	
			///////OR THIS://////
			/*List<ClusterResponse> clustering = cl.clustering(queryString, "genus", null, 10, "ObjectID", Lists.newArrayList("title"), Lists.newArrayList("description"), Lists.newArrayList("gDocCollectionLang"), "lingo", 1000);
	//
	//		for (ClusterResponse cr : clustering){
	//			System.out.println(cr.getClusterName());
	//			System.out.println(cr.getDocs().size());
	//		}
			try {
				queryStr = q.createIndexVisQuery(session, q.getSearchQueryTerms());
			} catch (InitialBridgingNotCompleteException e) {
				throw new Exception("Indexing of the particular collections is not complete, please try again later!",e);
			} catch (InternalErrorException e) {
				throw new Exception("General internal error exception.",e);
			} catch (QuerySyntaxException e) {
				throw new Exception("Syntax of the query for that queryID was not correct",e);
			}*/
			
			
//			String scope = "/gcube/devNext"; 
			String parentScope = session.getParentScope();
			
			IndexClient.Builder indexClientBuilder = new IndexClient.Builder();
			logger.debug("Creating index client for scope: "+parentScope);
			IndexClient cl = indexClientBuilder.scope(parentScope).build();
			
			logger.debug("Querying indexing services for frequent terms...");
			Map<String, Integer> terms = cl.frequentTerms(queryStr, null, maxStats, true);
	//		System.out.println("got results!");
	//		System.out.println(terms);
			
			ArrayList<String> words = new ArrayList<String>();
			ArrayList<Integer> values = new ArrayList<Integer>();
			
			for (String key : terms.keySet()){
				words.add(key);
				values.add(terms.get(key));
			}
			IndexData idxData = new IndexData();
			idxData.setWords(words);
			idxData.setValues(values);
			return idxData;			
		}catch(InternalErrorException | InitialBridgingNotCompleteException | IndexException ie ){
			logger.debug("Exception: " + ie);
			throw new DataException();
		}
		
		
	}

	
	private ASLSession getASLSession(){
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String username = (String) this.getThreadLocalRequest().getSession().getAttribute("username");
		ASLSession session =  SessionManager.getInstance().getASLSession(sessionID, username);
		if(session==null)
			logger.debug("No session could be retrieved from the ASL for user: "+username+" with id: "+sessionID);
		return session;
	}


	
	private String filterCollection(String original, String collectionID){
		String escap = "";
		for(int i=0;i<original.length();i++){
			String ch = String.valueOf(original.charAt(i));
			if(ch.equals("("))
				escap+="_BracStart_";
			else if(ch.equals(")"))
				escap+="_BracEnd_";
			else
				escap+=ch;
		}
		String cleaned = escap.replaceAll("_BracStart_gDocCollectionID.*?"+collectionID+"_BracEnd_", "");
		cleaned = cleaned.replaceAll("_BracStart_\\s*?and\\s*?_BracStart_", "_BracStart_");
		cleaned = cleaned.replaceAll("_BracEnd_\\s*?and\\s*?_BracEnd_", "_BracEnd_");
		cleaned = cleaned.replaceAll("_BracStart_\\s*?or\\s*?_BracStart_", "_BracStart_");
		cleaned = cleaned.replaceAll("_BracEnd_\\s*?or\\s*?_BracEnd_", "_BracEnd_");
		cleaned = cleaned.replaceAll("_BracStart_","(");
		cleaned = cleaned.replaceAll("_BracEnd_",")");
		return cleaned;
	}
	
}
