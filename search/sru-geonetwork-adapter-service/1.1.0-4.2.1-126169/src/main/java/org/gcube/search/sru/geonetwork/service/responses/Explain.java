package org.gcube.search.sru.geonetwork.service.responses;

import org.gcube.search.sru.geonetwork.service.exceptions.NotSupportedException;

public class Explain {

	private final String xmlnsSru = "http://www.loc.gov/zing/srw/";
	private final String xmlnsZr = "http://explain.z3950.org/dtd/2.0/";
	private final String dcContextIdentifier = "info:srw/cql-context-set/1/dc-v1.1";
	private final String dcSchemaIdentifier = "info:srw/schema/1/dc-v1.1";
	private final String recordSchema = xmlnsZr;
	
	private Float version;
	private String recordPacking;
	private String host;
	private String port;
	private String basePath;
	private String sruName;
	private int numOfRecords;
	
	public Explain(Float version, String recordPacking, String host, String port,String basePath, String sruName, int numOfRecords) throws NotSupportedException{
		if(version==null)
			version=Float.parseFloat("1.1");
		if(version>=1.2)
			throw new NotSupportedException("Only version 1.1 is supported.");
		this.version = version;
		if(recordPacking==null)
			recordPacking = "xml";
		if(!"xml".equals(recordPacking))
			throw new NotSupportedException("Record packing other than xml is not supported.");
		this.recordPacking = recordPacking;
		this.host = host;
		this.port = port;
		this.basePath = basePath;
		this.sruName = sruName;
		this.numOfRecords = numOfRecords;
	}
	
	
	public String getExplainResponse(){
		
		String xml = "<sru:explainResponse xmlns:sru=\""+xmlnsSru+"\">\n" + 
				"				 <sru:version>"+version+"</sru:version>\n" + 
				"				 <sru:record>\n" + 
				"				   <sru:recordPacking>"+recordPacking+"</sru:recordPacking>\n" + 
				"				   <sru:recordSchema>"+recordSchema+"</sru:recordSchema>\n" + 
				"				   <sru:recordData>\n" + 
				"				   <zr:explain xmlns:zr=\""+xmlnsZr+"\">\n" + 
				"				     <zr:serverInfo protocol=\"SRU\" version=\""+version+"\" transport=\"http\" method=\"GET POST SOAP\">\n" + 
				"				        <zr:host>"+host+"</zr:host>\n" + 
				"				        <zr:port>"+port+"</zr:port>\n" + 
				"				        <zr:database>"+basePath+"</zr:database>\n" + 
				"				     </zr:serverInfo>\n" + 
				"				     <zr:databaseInfo>\n" + 
				"				       <title lang=\"en\" primary=\"true\">"+sruName+"</title>\n" + 
				"				     </zr:databaseInfo>\n" + 
				"				     <zr:indexInfo>\n" + 
				"				       <zr:set name=\"dc\" identifier=\""+dcContextIdentifier+"\"/>\n" + 
				"				        <zr:index>\n" + 
				"				          <zr:map><zr:name set=\"dc\">title</zr:name></zr:map>\n" + 
				"				        </zr:index>\n" + 
				"				     </zr:indexInfo>\n" + 
				"				     <zr:schemaInfo>\n" + 
				"				        <zr:schema name=\"dc\" identifier=\""+dcSchemaIdentifier+"\">\n" + 
				"				          <zr:title>Simple Dublin Core</zr:title>\n" + 
				"				        </zr:schema>\n" + 
				"				     </zr:schemaInfo>\n" + 
				"				     <zr:configInfo>\n" + 
				"				         <zr:default type=\"numberOfRecords\">"+numOfRecords+"</zr:default>\n" + 
				"				         <zr:setting type=\"maximumRecords\">"+numOfRecords+"</zr:setting>\n" + 
				"				         <zr:supports type=\"proximity\"/>\n" + 
				"				     </zr:configInfo>\n" + 
				"				    </zr:explain>\n" + 
				"				   </sru:recordData>\n" + 
				"				 </sru:record>\n" + 
				"				</sru:explainResponse>";
		
		return xml;
	}
	
//	Float version, String recordPacking, String host, String port,String basePath, String sruName, String numOfRecords)
	public static void main (String args[]) throws NumberFormatException, NotSupportedException{
		
		Explain e = new Explain(Float.parseFloat("1.1"), "xml", "dionysus.di.uoa.gr", "8080", "aaa/aa", "srrrr", 1000);
		
		System.out.println(e.getExplainResponse());
	}
	
	
	
	
	
	
	
	
	
	
}
