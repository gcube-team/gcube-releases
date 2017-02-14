package gr.uoa.di.madgik.searchlibrary.operatorlibrary.extjdbc;

import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Parses that is responsible to extract form the input to the service query string all the available information
 * need by the wrapper. It expects to find as first level elements in the xml query string the <code>query</code>,
 * the <code>driverName</code> and the <code>connectionString</code>
 * 
 * @author UoA
 */
public class QueryParser {
	/**
	 * The Logger the class uses            
	 */
	private static Logger logger = LoggerFactory.getLogger(QueryParser.class.getName());
	/**
	 * The query document
	 */
	Document query=null;
	
	String url=null;
	
	/**
	 * Creates a new {@link QueryParser}
	 * 
	 * @param queryString The query string to parse
	 * @throws Exception An unrecoverable fo the operation error occured
	 */
	public QueryParser(String queryString) throws Exception{
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			this.query=db.parse(new InputSource(new StringReader(queryString)));
		}catch(Exception e){
			logger.error("Could not parse query. Throwing Exception",e);
			throw new Exception("Could not parse query");
		}
	}
	
	public QueryParser(URI queryString) throws Exception{
		url = URLDecoder.decode(queryString.toASCIIString(), "UTF-8");
	}
	
	/**
	 * Retrieves the content of the query element
	 * 
	 * @return The retrieved query
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getQuery() throws Exception{
		if (query == null) {
			if (url.startsWith("jdbc:postgresql://"))
				return url.substring(url.lastIndexOf("/") + 1);
			else
				throw new Exception("Unknown driver");
		}
		
		Element docEle = query.getDocumentElement();
		NodeList nl = docEle.getElementsByTagName("query");
		if(nl == null || nl.getLength() != 1){
			logger.error("Null or more than one query elements. Throwing Exception");
			throw new Exception("Null or more than one query elements");
		}
		return ((Element)nl.item(0)).getFirstChild().getNodeValue();
	}

	/**
	 * Retrieves the content of the driverName element
	 * 
	 * @return The retrieved driverName
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getDriverName() throws Exception{
		if (query == null) {
			if (url.startsWith("jdbc:postgresql://"))
				return "org.postgresql.Driver";
			else
				throw new Exception("Unknown driver");
		}
			
		Element docEle = query.getDocumentElement();
		NodeList nl = docEle.getElementsByTagName("driverName");
		if(nl == null || nl.getLength() != 1){
			logger.error("Null or more than one driverName elements. Throwing Exception");
			throw new Exception("Null or more than one driverName elements");
		}
		return ((Element)nl.item(0)).getFirstChild().getNodeValue();
	}

	/**
	 * Retrieves the content of the connectionString element
	 * 
	 * @return The retrieved connectionString
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String getConnectionString() throws Exception{
		if (query == null) {
			if (url.startsWith("jdbc:postgresql://"))
				return url.substring(0, url.lastIndexOf("/"));
			else
				throw new Exception("Unknown driver");
		}

		Element docEle = query.getDocumentElement();
		NodeList nl = docEle.getElementsByTagName("connectionString");
		if(nl == null || nl.getLength() != 1){
			logger.error("Null or more than one connectionString elements. Throwing Exception");
			throw new Exception("Null or more than one connectionString elements");
		}
		return ((Element)nl.item(0)).getFirstChild().getNodeValue();
	}
}
