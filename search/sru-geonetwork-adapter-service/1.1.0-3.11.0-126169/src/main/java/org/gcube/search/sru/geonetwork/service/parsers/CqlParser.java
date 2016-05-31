package org.gcube.search.sru.geonetwork.service.parsers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchRequest.Param;
import it.geosolutions.geonetwork.util.GNSearchResponse;

import org.apache.commons.lang.StringUtils;
import org.gcube.search.sru.geonetwork.service.exceptions.CqlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import search.library.util.cql.query.tree.GCQLAndNode;
import search.library.util.cql.query.tree.GCQLNode;
import search.library.util.cql.query.tree.GCQLNotNode;
import search.library.util.cql.query.tree.GCQLOrNode;
import search.library.util.cql.query.tree.GCQLProjectNode;
import search.library.util.cql.query.tree.GCQLQueryTreeManager;
import search.library.util.cql.query.tree.GCQLTermNode;
import search.library.util.cql.query.tree.ModifierSet;


public class CqlParser {
	
	private static final Logger logger = LoggerFactory.getLogger(CqlParser.class);
	
	
	public GNSearchRequest getRequestByCqlQuery(String cqlQuery) throws CqlException {
		
		if(StringUtils.containsIgnoreCase(cqlQuery, " like ")){
			throw new CqlException("CQL parser does not support 'like'. You can use wildcards within ordinary search terms.");
		}
		
		if(cqlQuery.startsWith("(")||cqlQuery.endsWith(")"))  //this means that the query is formed by the sru-consumer-service
			return getRequestByCqlQueryAlternative(cqlQuery);
		//if not above, the query is a normal cql query i.e. submitted directly to this service through its http interface.
		try{
			GNSearchRequest searchRequest = new GNSearchRequest();
			// set not searching at other bridged geonetworks 
		    searchRequest.addConfig(GNSearchRequest.Config.remote, "off");
			
			if(!cqlQuery.contains("=")){
				searchRequest.addParam(GNSearchRequest.Param.any, cqlQuery);
				return searchRequest;
			}
			
			logger.debug("Parsing cql and trying to transform into geonetwork query (non-cql)");
			
			//if you can't understand the following code, please do not touch it -- from here...
			StringBuilder cqlQueryReplica = new StringBuilder();
			//custom preperation before splitting by whitespaces
			boolean inQuotes = false;
			for(int i=0;i<cqlQuery.length();i++){
				if((cqlQuery.charAt(i)=='\'')||(cqlQuery.charAt(i)=='\"')){
					inQuotes = !inQuotes;
					continue;
				}
				if((cqlQuery.charAt(i)==' ')&&(inQuotes)){
					cqlQueryReplica.append("_-_");
					continue;
				}
				cqlQueryReplica.append(cqlQuery.charAt(i));
			}
			//parse cql parameters
			String [] params = cqlQueryReplica.toString().split(" ");
			//revert again to whitespaces
			for(int i=0;i<params.length;i++)
				params[i] = params[i].replace("_-_", " ");
			//...up to here
	
			HashMap<String,String> ors = new HashMap<String,String>();
			HashMap<String,List<String>> ands = new HashMap<String,List<String>>();
			
			if(params.length==1){
				if(params[0].split("=")[0].equalsIgnoreCase("title"))
					searchRequest.addParam(GNSearchRequest.Param.title, params[0].split("=")[1]);
				else
					searchRequest.addParam(GNSearchRequest.Param.any, params[0].split("=")[1]);
			}
			
			for(int i=1;i<params.length-1;i++){
				if(params[i].contains("="))
					continue;
				String prevKey = params[i-1].split("=")[0].toLowerCase();
				String prevVal = params[i-1].split("=")[1];
				String afterKey = params[i+1].split("=")[0].toLowerCase();
				String afterVal = params[i+1].split("=")[1];
				
				if("and".equalsIgnoreCase(params[i])){		
					if(i>2){
						if(ands.get(afterKey)!=null){
							List<String> tmp = new ArrayList<String>();
							tmp.addAll(ands.get(afterKey));
							tmp.add(afterVal);
							ands.put(afterKey, tmp);
						}
						else
							ands.put(afterKey, Arrays.asList(afterVal));
					}
					else{	
						if(ands.get(prevKey)!=null){
							List<String> tmp = new ArrayList<String>();
							tmp.addAll(ands.get(prevKey));
							tmp.add(prevVal);
							ands.put(prevKey, tmp);
						}
						else
							ands.put(prevKey, Arrays.asList(prevVal));
						if(ands.get(afterKey)!=null){
							List<String> tmp = new ArrayList<String>();
							tmp.addAll(ands.get(afterKey));
							tmp.add(afterVal);
							ands.put(afterKey, tmp);
						}
						else
							ands.put(afterKey, Arrays.asList(afterVal));
					}
				}
				else if("or".equalsIgnoreCase(params[i])){
					if(i>2){
						if(ors.get(afterKey)!=null)
							ors.put(afterKey, ors.get(afterKey)+ "|" +afterVal);
						else
							ors.put(afterKey, afterVal);
					}
					else{
						if(ors.get(prevKey)!=null)
							ors.put(prevKey, ors.get(prevKey)+ "|" +prevVal);
						else
							ors.put(prevKey, prevVal);
						if(ors.get(afterKey)!=null)
							ors.put(afterKey, ors.get(afterKey)+ "|" +afterVal);
						else
							ors.put(afterKey, afterVal);
					}
				}
			}
			
			//add all "OR":
			for(String key : ors.keySet()){
				if(key.equalsIgnoreCase("title"))
					searchRequest.addParam(GNSearchRequest.Param.title, ors.get(key));
				else
					searchRequest.addParam(key, ors.get(key));
			}
			//add all "AND":
			for(String key : ands.keySet()){
				for(String val : ands.get(key)){
					if(key.equalsIgnoreCase("title"))
						searchRequest.addParam(GNSearchRequest.Param.title, val);
					else
						searchRequest.addParam(key, val);
				}
			}
	
		    return searchRequest;
		}catch(Exception e){ //in case of any exception, try an alternative parsing...
			return getRequestByCqlQueryAlternative(cqlQuery);
	    }
	}
	
	/**
	 * 
	 * @param cqlQuery the cql query as it is formed by the sru-consumer-service (weird stuff) :P
	 * @return
	 */
	private GNSearchRequest getRequestByCqlQueryAlternative(String cqlQuery){
//		cqlQuery = "((((((((((((((((identifier = tuna) or (title = tuna))) or (date = tuna))) or (description = tuna))) or (publisher = tuna))) or (rights = tuna))) or (source = tuna))) or (coverage = tuna))) or (subject = tuna))";
		Map<String,String> fieldVal = new HashMap<String,String>();
		Pattern regexp = Pattern.compile("[(][a-zA-Z_]*\\s[=]\\s[a-zA-Z_]*[)]");
		Matcher matcher = regexp.matcher(cqlQuery);
		while(matcher.find()){
			String pairs = matcher.group(0); //gets something like "(title = fish)"
			pairs = pairs.substring(1, pairs.length()-1); //remove the parentheses (  )
			fieldVal.put(pairs.split(" = ")[0], pairs.split(" = ")[1]);			
		}
		//by now, the hashmap contains the pairs of the sru-consumer-service cql query
		GNSearchRequest searchRequest = new GNSearchRequest();
		// set not searching at other bridged geonetworks 
	    searchRequest.addConfig(GNSearchRequest.Config.remote, "off");
		
	    String searchValue = fieldVal.entrySet().iterator().next().getValue();
		
		if(fieldVal.keySet().size()>1){ //if more than 1, then search in all fields
			searchRequest.addParam(GNSearchRequest.Param.any, searchValue);
			return searchRequest;
		}
		else{
			if(fieldVal.get("title")!=null)
				searchRequest.addParam(GNSearchRequest.Param.title, searchValue);
			else
				searchRequest.addParam(GNSearchRequest.Param.any, searchValue);
			return searchRequest;
		}
		
	}
	
	
	
	public void writeToFile(String pFilename, StringBuffer pData) throws IOException {  
        BufferedWriter out = new BufferedWriter(new FileWriter(pFilename));  
        out.write(pData.toString());  
        out.flush();  
        out.close();  
    }  
    public StringBuffer readFromFile(String pFilename) throws IOException {  
        BufferedReader in = new BufferedReader(new FileReader(pFilename));  
        StringBuffer data = new StringBuffer();  
        int c = 0;  
        while ((c = in.read()) != -1) {  
            data.append((char)c);  
        }  
        in.close();  
        return data;  
    }  
	
    
//	List<String> projections = new ArrayList<String>();
//	Set<String> tables = new HashSet<String>();
//	List<String> columns = new ArrayList<String>();
//	
//	String sqlQuery;
//	
//	String whereClause;
//	
//	Integer limit;
//	Integer offset = 0;
	

	
//	String defaultTable;
    
   
	

}
