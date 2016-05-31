package org.gcube.datatransformation.adaptors.db;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.TransformerException;

import org.gcube.datatransformation.adaptors.common.ElementGenerator;
import org.gcube.datatransformation.adaptors.common.db.exceptions.DBConnectionException;
import org.gcube.datatransformation.adaptors.common.db.tools.DBConstants;
import org.gcube.datatransformation.adaptors.common.db.tools.SourcePropsTools;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.DBProps;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.DBSource;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.Edge;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.Table;
import org.gcube.datatransformation.adaptors.db.toolbox.GenericTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DBDataStax {

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(DBDataStax.class);
	
	private Connection conn;
	private DBProps dbProps;
	private DBSource dbSource;
	private String rootTableName;
	private String scope;
	
	public DBDataStax(DBSource dbSource, DBProps dbProps, String scope){
		this.dbSource = dbSource;
		this.scope = scope;
		try {
			this.dbProps = dbProps;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		initiateRootTable();
	}
	
	
	public DBProps getSourceProps(){
		return dbProps;
	}
	
	public String getRootTableName(){
		return rootTableName;
	}
	
	private void initiateRootTable(){
		//1. find the root table
		for(Table table : dbProps.getTables()){
			boolean root = true;
			for(Edge edge : dbProps.getEdges()){
				if(table.getName().equalsIgnoreCase(edge.getChild()))
					root = false;
			}
			if(root){
				rootTableName = table.getName();
				break;
			}
		}
		logger.debug("root table is : "+rootTableName);
	}
	
	public StreamingOutput writeSourceData(final String hostnameport) throws  XMLStreamException, IOException, DBConnectionException{

		//initiate a connection to the database and retrieve the tuples of the root table 
		conn = connectToDatabaseOrDieTrying();
		
		final XMLOutputFactory factory = XMLOutputFactory.newInstance();
		
		
		StreamingOutput outputStream = new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				
				try{
					
					XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(new BufferedWriter(new OutputStreamWriter(output)));
					
					xmlWriter.writeStartDocument();
					
					xmlWriter.writeStartElement("collection");
					
					xmlWriter.writeStartElement("name");
					xmlWriter.writeCharacters(dbSource.getSourceName()+" "+"Database");
					xmlWriter.writeEndElement();
					
					xmlWriter.writeStartElement("provenance");
					xmlWriter.writeCharacters(dbSource.getDBType()+" Database");
					xmlWriter.writeEndElement();
					
					xmlWriter.writeStartElement("timestamp");
					xmlWriter.writeCharacters(GenericTools.getCurrentTimestamp());
					xmlWriter.writeEndElement();
					
					xmlWriter.writeStartElement("records");
					
					try {
						formSubElement(xmlWriter, rootTableName, new HashMap<String, String>(), hostnameport);
					} catch (Exception e) {	e.printStackTrace();}
					
					xmlWriter.writeEndElement(); //records element
					xmlWriter.writeEndElement(); //collection element
					
					xmlWriter.writeEndDocument();
	
					xmlWriter.flush();

					xmlWriter.close();

					try {
						conn.close();
					} catch (SQLException e) {
						throw new DBConnectionException("Error closing the connection with the database", e);
					}
					
				}
				catch(Exception e){
					
				}		
				
			}
        };
		
		return outputStream;
	}
	
	/**
	 * Recursive function to provide the tree structure values
	 */
	public Element formSubElement(XMLStreamWriter writer, String tableName, HashMap<String, String> keysVals, String hostnameport) throws SQLException, XMLStreamException, TransformerException{
		Document doc = ElementGenerator.getDocument();
		ResultSet rs = getFilteredTableResultSet(tableName, conn, keysVals);
		ResultSetMetaData fields = rs.getMetaData();
		
		Element elementList = doc.createElement(tableName+"_list");
		
		ArrayList<Edge> edges = SourcePropsTools.getEdges(dbProps, tableName);
		
		if(keysVals.size()==0){ //means it's the head table
			while(rs.next()){
				writer.writeStartElement("record");
				writer.writeStartElement("id");
				//find the primary keys of the head table to create the id
				String[] rootKeys = new String[1];
				for(Edge edge : dbProps.getEdges()){
					if(edge.getParent().equals(tableName))
						rootKeys = edge.getPKeys().split(",");
				}
				//e.g: http://dionysus.di.uoa.gr:8081/aslHarvestersHttpDB/HarvestDatabase?sourcename=CountriesDB&recordid=GRC
				writer.writeCharacters("http://"+hostnameport+"/aslHarvestersHttpDB/HarvestDatabase");
				writer.writeCharacters("?sourcename="+dbSource.getSourceName());
//				writer.writeCharacters("&sourceprops="+dbProps.getPropsName());
				
				String idStr = "";
				for(String key : rootKeys)
					idStr += rs.getString(key)+",";
				idStr = idStr.substring(0, idStr.length()-1); //remove trailing comma
				writer.writeCharacters("&recordid="+idStr);
				writer.writeEndElement();
				writer.writeStartElement("fields");
				writer.writeStartElement("field");
				writer.writeStartElement("name");
				writer.writeCharacters(tableName);
				writer.writeEndElement();
				writer.writeStartElement("mimetype");
				writer.writeCharacters("text/xml");
				writer.writeEndElement();
				writer.writeStartElement("payload");
				
				
				Element subTable = doc.createElement(tableName);
				
				for(int i=1;i<=fields.getColumnCount();i++){
					Element el = doc.createElement(fields.getColumnLabel(i));
					String val = rs.getString(i);
					if(val==null)
						val="";
					el.appendChild(doc.createTextNode(val));
					subTable.appendChild(el);
				}
				for(Edge edge : edges){
					HashMap<String, String> kv = new HashMap<String,String>();
					String[] pkeys = edge.getPKeys().split(",");
					String[] ckeys = edge.getCKeys().split(",");
					for(int i=0;i<ckeys.length;i++)
						kv.put(ckeys[i], rs.getString(pkeys[i]));
					Element e = formSubElement(writer, edge.getChild(), kv , hostnameport);
					if(e.hasChildNodes())
						subTable.appendChild(e);
				}
				
				writer.writeCData(ElementGenerator.domToXML(subTable));
				
				writer.writeEndElement();// payload element
				writer.writeEndElement(); //field element
				writer.writeEndElement(); //fields element
				writer.writeEndElement(); //record element
				writer.flush();
			}
			
		}
		else{
			while(rs.next()){
				Element subTable = doc.createElement(tableName);
				
				for(int i=1;i<=fields.getColumnCount();i++){
					Element el = doc.createElement(fields.getColumnLabel(i));
					String val = rs.getString(i);
					if(val==null)
						val="";
					el.appendChild(doc.createTextNode(val));
					subTable.appendChild(el);
				}
				for(Edge edge : edges){
					HashMap<String, String> kv = new HashMap<String,String>();
					String[] pkeys = edge.getPKeys().split(",");
					String[] ckeys = edge.getCKeys().split(",");
					for(int i=0;i<ckeys.length;i++)
						kv.put(ckeys[i], rs.getString(pkeys[i]));
					Element e = formSubElement(writer, edge.getChild(), kv, hostnameport);
					if(e.hasChildNodes())
						subTable.appendChild(e);
				}
				elementList.appendChild(subTable);
			}
			
		}
		return elementList;
	}
	
	
	
	private ResultSet getDefaultTableResultSet(String table, Connection conn) throws SQLException {
		Statement st = conn.createStatement();
		return st.executeQuery(SourcePropsTools.getSqlOfTable(dbProps, table));

	}
	
	/**
	 * Key and value (within keysValues) will be appended on the end of the default sql query. (e.g. "where key=value")
	 */
	private ResultSet getFilteredTableResultSet(String table, Connection conn, HashMap<String,String> keysValues) throws SQLException {
		Statement st = conn.createStatement();
		String sql = SourcePropsTools.getSqlOfTable(dbProps, table);
		if(keysValues.isEmpty())
			return st.executeQuery(sql);
		if(sql.toLowerCase().contains(" where ")){ //means that it already has a 'where' filter, so we append our filter with an 'and' 
			for(String key : keysValues.keySet())
				sql += " and "+key+"='"+keysValues.get(key)+"'";
		}
		else{
			sql += " where ";
			for(String key : keysValues.keySet())
				sql += key+"='"+keysValues.get(key)+"' and ";
		}
		if(sql.endsWith(" and "))//remove trailing " and "
			sql = sql.substring(0, sql.length()-5); //5 characters
		
		return st.executeQuery(sql);

	}
	
	
	private Connection connectToDatabaseOrDieTrying() throws DBConnectionException {
		Connection conn = null;
		try {
			Class.forName(DBConstants.getDriverForName(dbSource.getDBType()));
			String url = "jdbc:"+dbSource.getDBType()+":"+dbSource.getConnectionString();
			Properties props = new Properties();
			props.setProperty("user",dbSource.getUserName());
			props.setProperty("password",dbSource.getPassword());
			props.setProperty("tcpKeepAlive","true");
			conn = DriverManager.getConnection(url, props);
		} catch (ClassNotFoundException e) {
			throw new DBConnectionException("Could not find the jdbc connection class", e);
		} catch (SQLException e) {
			throw new DBConnectionException("Error connecting to the database through the jdbc class ", e);
		}
		return conn;
	}
	
}
