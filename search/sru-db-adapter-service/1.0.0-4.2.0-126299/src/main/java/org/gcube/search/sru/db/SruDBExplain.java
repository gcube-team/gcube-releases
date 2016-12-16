package org.gcube.search.sru.db;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;


public class SruDBExplain {
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
	
	Map<String, ArrayList<String>> indexInfo;
	Map<String, String> indexSets;
	
	
	public String getExplainXML() {
		
		String indexInfoXML = "";
		
		for (Entry<String, String> set : this.indexSets.entrySet()) {
			indexInfoXML += "					<set identifier=\"" + set.getKey() + "\" name=\"" + set.getValue() + "\" />\n";  
		}
		
		int id = 1;
		for (Entry<String, ArrayList<String>> index : this.indexInfo.entrySet()) {
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
				"			</explain>\n" + 
				"		</zs:recordData>\n" + 
				"	</zs:record>\n" + 
				"</zs:explainResponse>";
		
		return xml;
	}
	
	public static void main(String[] args) {
		SruDBExplain explain = new SruDBExplain();
		
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
		
		explain.indexInfo = new HashMap<String, ArrayList<String>>();
		explain.indexInfo.put("books",Lists.newArrayList("author", "title"));
		
		
		explain.schemaID = "http://www.loc.gov/mods";
		explain.schemaName = "rss";
		
		System.out.println(explain.getExplainXML());
		
		
		
	}
	
}
