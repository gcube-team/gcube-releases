package org.gcube.search.sru.search.adapter.service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gcube.search.sru.search.adapter.service.helpers.RecordConverter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


public class SruSearchAdapterExplain {
	String version;
	String recordSchema;
	String recordPacking;
	
	String serverHost;
	Integer serverPort;
	String databaseName;
	
	String databaseTitle;
	String databaseDescription;
	
	
	String schemaID;
	String schemaName;
	
	Map<String, List<String>> indexInfo;
	Map<String, String> indexSets;
	int numberOfRecords;
	String retrieveSchema;
	
	
	public String getExplainXML() {
		
		String indexInfoXML = "";
		
		for (Entry<String, String> set : this.indexSets.entrySet()) {
			indexInfoXML += "					<set identifier=\"" + set.getKey() + "\" name=\"" + set.getValue() + "\" />\n";  
		}
		
		int id = 1;
		for (Entry<String, List<String>> index : this.indexInfo.entrySet()) {
			String indexSet = index.getKey();
			
			for (String map : index.getValue()) {
				indexInfoXML +=
				"					<index id=\"" + id + "\">\n" + 
				"						<title>" + map + "</title>\n" + 
				"						<map>\n" + 
				"							<name set=\"" + indexSet + "\">" + map+ "</name>\n" + 
				"						</map>\n" + 
				"					</index>\n"; 
				
				id++;
			}
		}
		
		String xml =
				"<?xml version=\"1.0\"?>" +
				"<zs:explainResponse xmlns:zs=\"http://www.loc.gov/zing/srw/\">\n" + 
				"	<zs:version>" + this.version +"</zs:version>\n" + 
				"	<zs:record>\n" + 
				"		<zs:recordSchema>" + this.recordSchema + "</zs:recordSchema>\n" + 
				"		<zs:recordPacking>" + this.recordPacking + "</zs:recordPacking>\n" + 
				"		<zs:recordData>\n" + 
				"			<explain xmlns=\"http://explain.z3950.org/dtd/2.0/\">\n" + 
				"				<serverInfo>\n" + 
				"					<host>" + this.serverHost + "</host>\n" + 
				"					<port>" + this.serverPort + "</port>\n" + 
				"					<database>" + this.databaseName +"</database>\n" + 
				"				</serverInfo>\n" + 
				"				<databaseInfo>\n" + 
				"					<title>" + this.databaseTitle + "</title>\n" + 
				"					<description lang=\"en\" primary=\"true\">\n" + 
				"						" + this.databaseDescription + "\n" + 
				"					</description>\n" + 
				"				</databaseInfo>\n" + 
				"				<indexInfo>\n" + 
				indexInfoXML +
				"				</indexInfo>\n" + 
				"\n" + 
				"				<schemaInfo>\n" + 
				"					<schema identifier=\"" + this.schemaID +"\" sort=\"false\" name=\"" + this.schemaName + "\">\n" + 
				"					  <title>" + this.schemaName + "</title>\n" + 
				"					</schema>\n" + 
				"				</schemaInfo>\n" + 
				"               <configInfo>\n" +
				"                   <default type=\"numberOfRecords\">" + this.numberOfRecords +"</default>\n" +
                //"                   <default type=\"numberOfRecords\">10</default>" +
                "                  <default type=\"retrieveSchema\">" + this.retrieveSchema + "</default>" +
                "                  </configInfo>" +
				"			</explain>\n" + 
				"		</zs:recordData>\n" + 
				"	</zs:record>\n" + 
				"</zs:explainResponse>";
		
		return xml;
	}
	
	
	public static SruSearchAdapterExplain createExplain(Map<String, String> collections, Map<String, List<String>> fields, String hostname, Integer port, Boolean includeNonDC, int defaultNumberOfRecords){
		
		
		Map<String, String> indexSets = Maps.newHashMap();
		indexSets.put("info:srw/cql-context-set/1/cql-v1.1", "cql");
		indexSets.put("info:srw/cql-context-set/1/dc-v1.1", "oai_dc");
//		for (String table : collections.keySet()){
//			indexSets.put(table, "info:srw/cql-context-set/1/db-v1.1");
//		}
		
		
//		Map<String, List<String>> collectionsAndFields = Maps.newHashMap();
//
//		for (Map.Entry<String, List<String>> entry : fields.entrySet()) {
//			collectionsAndFields.put(entry.getKey(), Lists.newArrayList(entry.getValue()));
//		}
		
		
		Map<String, List<String>> collectionsAndFields = Maps.newHashMap();
		Set<String> allFields = Sets.newHashSet();
		for (Map.Entry<String, List<String>> entry : fields.entrySet()) {
			allFields.addAll(entry.getValue());
		}
		
		if (includeNonDC == false)
			allFields.retainAll(RecordConverter.DC_FIELDS);
		
		
		collectionsAndFields.put("oai_dc", Lists.newArrayList(allFields));
		collectionsAndFields.put("cql", Lists.newArrayList("allIndexes"));
		
		
		SruSearchAdapterExplain explain = new SruSearchAdapterExplain();
		explain.indexSets = indexSets;
		explain.indexInfo = collectionsAndFields;
		
		explain.schemaID  = "info:srw/schema/1/dc-v1.1";
		explain.recordSchema  = "http://explain.z3950.org/dtd/2.0/";
		
		
		explain.databaseTitle = "gCube Search";
		
		explain.serverHost = hostname;
		explain.serverPort = port;
		
		explain.version = "1.1";
		
		explain.schemaName  = "oai_dc";
		explain.recordPacking  = "xml";
		explain.numberOfRecords = defaultNumberOfRecords;
		explain.retrieveSchema = "info:srw/schema/1/dc-v1.1";
		
		return explain;
	}
	
	public static void main(String[] args) {
		SruSearchAdapterExplain explain = new SruSearchAdapterExplain();
		
		explain.version = "1.1";
		explain.recordSchema = "http://explain.z3950.org/dtd/2.0/";
		explain.recordPacking = "xml";
		
		explain.serverHost = "jazzman.di.uoa.gr";
		explain.serverPort = 3306;
		explain.databaseName = "test";
		
		explain.databaseTitle = "Test";
		explain.databaseDescription = "Test mysql database";
		
		explain.indexSets = new HashMap<String, String>();
		explain.indexSets.put("cql", "info:srw/cql-context-set/1/cql-v1.1");
		explain.indexSets.put("books", "info:srw/cql-context-set/1/db-v1.1");
		
		explain.indexInfo = new HashMap<String, List<String>>();
		explain.indexInfo.put("books",Lists.newArrayList("author", "title"));
		
		
		explain.schemaID = "http://www.loc.gov/mods";
		explain.schemaName = "rss";
		
		System.out.println(explain.getExplainXML());
		
		
		
	}
	
}
