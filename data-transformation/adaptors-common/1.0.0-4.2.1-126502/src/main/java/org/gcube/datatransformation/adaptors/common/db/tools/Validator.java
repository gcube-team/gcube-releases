package org.gcube.datatransformation.adaptors.common.db.tools;
//package org.gcube.application.framework.harvesting.common.db.tools;
//
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//
//import org.gcube.application.framework.harvesting.common.dbXMLObjects.DBSource;
//import org.gcube.application.framework.harvesting.common.dbXMLObjects.Edge;
//import org.gcube.application.framework.harvesting.common.dbXMLObjects.Table;
//import org.w3c.dom.Document;
//import org.w3c.dom.NodeList;
//import org.xml.sax.SAXException;
//
//public class Validator {
//
//	/**
//	 * Simple validator for the table connections
//	 * 
//	 * @param sourceProps the parsed properties from the xml source
//	 * @return empty string if the properties are valid, otherwise a text representation of the problem 
//	 */
//	public static String validateSourceProps(DBSource sourceProps){
//		//TODO: Create a validator for the DBSource
////		ArrayList<Table> tables = sourceProps.getTables();
////		ArrayList<Edge> edges = sourceProps.getEdges();
//		return "";
//	}
//	
//	final static String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><DBSources><DBSource><host>localhost</host><port>5432</port><dbtype>postgresql</dbtype><dbname>World</dbname><dbusername>postgres</dbusername><dbpassword>di_database</dbpassword><sourceid>nikolas.laskaris</sourceid><table><name>country</name><sql>select * from country</sql><!-- <sql>select * from country where code='GRC' or code='CIV' or code='USA' or code='GBR'</sql> --></table><table><name>city</name><sql>select * from city</sql></table><table><name>countrylanguage</name><sql>select * from countrylanguage</sql></table><table><name>zipcodes</name><sql>select * from zipcodes</sql></table><edge><parent>country</parent><pkeys>code</pkeys> <!-- if more than one, seperate them with comma --><child>city</child><ckeys>countrycode</ckeys> <!-- if more than one, seperate them with comma --></edge><edge><parent>country</parent><pkeys>code</pkeys> <!-- if more than one, seperate them with comma --><child>countrylanguage</child><ckeys>countrycode</ckeys> <!-- if more than one, seperate them with comma --></edge><edge><parent>city</parent><pkeys>id</pkeys> <!-- if more than one, seperate them with comma --><child>zipcodes</child><ckeys>cityid</ckeys> <!-- if more than one, seperate them with comma --></edge></DBSource><DBSource><host>localhost</host><port>5432</port><dbtype>postgresql</dbtype><dbname>FrenchTowns</dbname><dbusername>postgres</dbusername><dbpassword>di_database</dbpassword><sourceid>alex.antoniadis</sourceid><table><name>regions</name><sql>select * from regions</sql></table><table><name>departments</name><sql>select * from departments</sql></table><table><name>towns</name><sql>select * from towns</sql></table><edge><parent>regions</parent><pkeys>code</pkeys> <!-- if more than one, seperate them with comma --><child>departments</child><ckeys>region</ckeys> <!-- if more than one, seperate them with comma --></edge></DBSource><DBSource><host>localhost</host><port>5432</port><dbusername>postgres</dbusername><dbpassword>di_database</dbpassword><dbtype>postgresql</dbtype><dbname>sakila</dbname><sourceid>john.gerbesiotis</sourceid><table><name>customer</name><sql>select * from customer</sql></table><table><name>store</name><sql>select * from store</sql></table><table><name>address</name><sql>select * from address</sql></table><table><name>rental</name><sql>select * from rental</sql></table><table><name>city</name><sql>select * from city</sql></table><table><name>country</name><sql>select * from country</sql></table><table><name>payment</name><sql>select * from payment</sql></table><edge><parent>customer</parent><pkeys>store_id</pkeys> <!-- if more than one, seperate them with comma --><child>store</child><ckeys>store_id</ckeys> <!-- if more than one, seperate them with comma --></edge><edge><parent>customer</parent><pkeys>address_id</pkeys> <!-- if more than one, seperate them with comma --><child>address</child><ckeys>address_id</ckeys> <!-- if more than one, seperate them with comma --></edge><edge><parent>customer</parent><pkeys>customer_id</pkeys> <!-- if more than one, seperate them with comma --><child>rental</child><ckeys>customer_id</ckeys> <!-- if more than one, seperate them with comma --></edge><edge><parent>address</parent><pkeys>city_id</pkeys> <!-- if more than one, seperate them with comma --><child>city</child><ckeys>city_id</ckeys> <!-- if more than one, seperate them with comma --></edge><edge><parent>city</parent><pkeys>country_id</pkeys> <!-- if more than one, seperate them with comma --><child>country</child><ckeys>country_id</ckeys> <!-- if more than one, seperate them with comma --></edge><edge><parent>rental</parent><pkeys>rental_id</pkeys> <!-- if more than one, seperate them with comma --><child>payment</child><ckeys>rental_id</ckeys> <!-- if more than one, seperate them with comma --></edge></DBSource></DBSources>";
//	
//	/**
//	 * 
//	 * @param xml the xml text which is to be validated against non-allowed field names
//	 * @return
//	 */
//	public static String validateXMLFields(String xml) throws Exception{
//		ArrayList<String> dbSourceNodeNames = new ArrayList<String>();
//		dbSourceNodeNames.add("host");
//		dbSourceNodeNames.add("port");
//		dbSourceNodeNames.add("dbtype");
//		dbSourceNodeNames.add("dbname");
//		dbSourceNodeNames.add("dbusername");
//		dbSourceNodeNames.add("dbpassword");
//		dbSourceNodeNames.add("sourceid");
//		dbSourceNodeNames.add("table");
//		dbSourceNodeNames.add("edge");
//		ArrayList<String> tableNodeNames = new ArrayList<String>();
//		tableNodeNames.add("name");
//		tableNodeNames.add("sql");
//		ArrayList<String> edgeNodeNames = new ArrayList<String>();
//		edgeNodeNames.add("parent");
//		edgeNodeNames.add("pkeys");
//		edgeNodeNames.add("child");
//		edgeNodeNames.add("ckeys");
//		
//		
//		InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
//		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//		Document doc;
//		try {
//			doc = dBuilder.parse(stream);
//		} catch (SAXException e) {
//			return "XML is scrambled. Check if all tags do close";
//		} catch (IOException e) {
//			return "XML is probably not UTF-8 encoded";
//		}
//		doc.getDocumentElement().normalize();
//		
//		NodeList dbSources = doc.getChildNodes();
//		if(dbSources.getLength()>1)
//			return "root element should be just one and named \"DBSources\"";
//		if(!dbSources.item(0).getNodeName().equals("DBSources"))
//			return "The root element should be named \"DBSources\"";
//		
//		NodeList dbSource = doc.getDocumentElement().getChildNodes();  //doc.getElementsByTagName("DBSource");
//		for(int i=0;i<dbSource.getLength();i++){
//			if(!dbSource.item(i).getNodeName().equals("DBSource"))
//				return "DBSources contains at least one child which is other than \"DBSource\"";
//			NodeList dbSourceChildren = dbSource.item(i).getChildNodes();
//			for(int j=0;j<dbSourceChildren.getLength();j++){
////				if(dbSourceChildren.item(j)
//			}
//		}
//		
//		//if reached at this point, then everything is ok
//		return "";
//	}
//	
//	
//}
