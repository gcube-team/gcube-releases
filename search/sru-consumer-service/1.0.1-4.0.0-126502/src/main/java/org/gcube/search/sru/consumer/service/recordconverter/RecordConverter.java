//package org.gcube.search.sru.consumer.service.recordconverter;
//
//import java.io.IOException;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//import javax.xml.xpath.XPath;
//import javax.xml.xpath.XPathConstants;
//import javax.xml.xpath.XPathExpression;
//import javax.xml.xpath.XPathExpressionException;
//import javax.xml.xpath.XPathFactory;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//
//import com.google.common.base.Joiner;
//import com.google.common.collect.ArrayListMultimap;
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Multimap;
//
//public class RecordConverter {
//
//	public List<String> supportedSchemas = Lists.newArrayList(
//					"info:srw/schema/1/dc-v1.1",
//					"info:srw/schema/1/dc-schema", "rss");
//	
//	static final Logger logger = LoggerFactory.getLogger(RecordConverter.class);
////	
////	public Map<String, Collection<String>> convertToRecord(Object obj, String schema)
////			throws JAXBException, XPathExpressionException, SAXException,
////			IOException, ParserConfigurationException {
////
////		String xml = JAXBConverter.getObjString(obj);
////		Schema cs = null;
////
////		if (schema.equalsIgnoreCase("info:srw/schema/1/dc-v1.1")
////				|| schema.equalsIgnoreCase("info:srw/schema/1/dc-schema")) {
////			cs = new SruDC();
////		} else if (schema.equalsIgnoreCase("rss")) {
////			cs = new RSS();
////		} 
////		return cs.parse(xml);
////	}
////	
////	public Map<String, Collection<String>> convertToRecord(Object obj,
////			final Map<String, String> schemaMapping) throws JAXBException,
////			XPathExpressionException, SAXException, IOException,
////			ParserConfigurationException {
////
////		String xml = XMLConverter.convertToXML(obj);
////
////		Schema cs = new Schema(){
////			@Override
////			Map<String, String> getMapping() {
////				return schemaMapping;
////			}
////		};
////		
////		return cs.parse(xml);
////	}
//	
//	
//	public static List<Map<String, String>> convertSQLResultSetToRecords(ResultSet rs) throws SQLException{
//		ResultSetMetaData rsmd = rs.getMetaData();
//	    int colCount = rsmd.getColumnCount();
//	    
//	    List<Map<String, String>> recs = Lists.newArrayList();
//	    
//	    while (rs.next()) {
//	    	Map<String, String> rec = Maps.newHashMap();
//	    	
//	    	for (int i = 1; i <= colCount; i++) {
//		        String columnName = rsmd.getColumnName(i);
//		        Object value = rs.getObject(i);
//		        
//		        if (value == null)
//		        	value = "";
//		        rec.put(columnName, value.toString());
//	    	}
//	    	recs.add(rec);
//	    }
//	    
//	    return recs;
//	}
//	
//	public static String convertRecordsToSRU(Long resultsCnt, List<Map<String, String>> rs) throws TransformerException, ParserConfigurationException {
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//	    DocumentBuilder builder = factory.newDocumentBuilder();
//	    Document doc = builder.newDocument();
//	    doc.setXmlStandalone(true);
//	    Element searchRetrieveResponse = doc.createElement("searchRetrieveResponse");
//	    searchRetrieveResponse.setAttribute("xmlns:zs", "http://www.loc.gov/zing/srw/");
//	    doc.appendChild(searchRetrieveResponse);
//	    
//	    
//	    Element el = doc.createElement("zs:version");
//	    el.appendChild(doc.createTextNode("1.1"));
//	    searchRetrieveResponse.appendChild(el);
//	    
//	   
//	    Element records = doc.createElement("zs:records");
//	    searchRetrieveResponse.appendChild(records);
//	    
////	    Element el = doc.createElement(DB_TITLE_PROP);
////	    el.appendChild(doc.createTextNode(DB_TITLE));
////	    channel.appendChild(el);
////	    
////	    el = doc.createElement(DB_LINK_PROP);
////	    el.appendChild(doc.createTextNode(DB_LINK));
////	    channel.appendChild(el);
////	    
////	    el = doc.createElement(DB_DESCRIPTION_PROP);
////	    el.appendChild(doc.createTextNode(DB_DESCRIPTION));
////	    channel.appendChild(el);
////	    
////	    el = doc.createElement(DB_PUBDATE_PROP);
////	    el.appendChild(doc.createTextNode(DB_PUBDATE));
////	    channel.appendChild(el);
////	    
////	    el = doc.createElement(DB_LASTBUILDDATE_PROP);
////	    el.appendChild(doc.createTextNode(DB_LASTBUILDDATE));
////	    channel.appendChild(el);
////	    
////	    el = doc.createElement(DB_TTL_PROP);
////	    el.appendChild(doc.createTextNode(DB_TTL));
////	    channel.appendChild(el);
//	    
//	    //Element 
//		
//	    el = doc.createElement("zs:numberOfRecords");
//	    el.appendChild(doc.createTextNode(String.valueOf(resultsCnt)));
//	    searchRetrieveResponse.appendChild(el);
//	    
//	    int recPosition = 1;
//	    
//	    for (Map<String, String> rec : rs) {
//	      Element record = doc.createElement("zs:record");
//	      records.appendChild(record);
//	      
//	      el = doc.createElement("zs:recordSchema");
//	      el.appendChild(doc.createTextNode("rss"));
//	      record.appendChild(el);
//	      
//	      el = doc.createElement("zs:recordPacking");
//	      el.appendChild(doc.createTextNode("xml"));
//	      record.appendChild(el);
//	      
//		  
//		  Element recordData = doc.createElement("zs:recordData");
//		  //el.setAttribute("xmlns:srw_rss", "rss");
//		  //el.appendChild(doc.createTextNode("xml"));
//		  record.appendChild(recordData);
//	      
//		  Element srw = doc.createElement("srw_rss:rss");
//		  srw.setAttribute("xmlns:srw_rss", "rss");
//		  srw.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
//		  srw.setAttribute("xmlns", "rss");
//		  
//		  
//		  //el.appendChild(doc.createTextNode("xml"));
//		  recordData.appendChild(srw);
//	      
//		  
//	      //channel.appendChild(row);
//		  
//		  for (Entry<String, String> col : rec.entrySet()){
//			  String columnName =  col.getKey();
//		      String value = col.getValue();
//		      Element node = doc.createElement(columnName);
//		      if (value == null)
//		        	node.appendChild(doc.createTextNode(""));
//		      else
//		        	node.appendChild(doc.createTextNode(value.toString()));
//		      srw.appendChild(node);
//		  }
//		  
//	      Element recordPosition = doc.createElement("zs:recordPosition");
//	      recordPosition.appendChild(doc.createTextNode(String.valueOf(recPosition)));
//		  //el.appendChild(doc.createTextNode("xml"));
//		  record.appendChild(recordPosition);
//		  recPosition++;
//	    }
//	    
//	    
//	    DOMSource domSource = new DOMSource(doc);
//	    TransformerFactory tf = TransformerFactory.newInstance();
//	    Transformer transformer = tf.newTransformer();
//	    //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
//	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//	    //transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//	    StringWriter sw = new StringWriter();
//	    StreamResult sr = new StreamResult(sw);
//	    transformer.transform(domSource, sr);
//
//		return sw.toString();
//	}
//	
//	
//	
//	
//	
//}
//
//
//
//
//abstract class Schema {
//	
//	static final Logger logger = LoggerFactory.getLogger(Schema.class);
//	
//	public Schema() {
//	}
//	
//	abstract Map<String, String> getMapping();
//
//	public Map<String, Collection<String>> parse(String xml) throws SAXException, IOException,
//			ParserConfigurationException, XPathExpressionException {
//		
//		Multimap<String, String> rec = ArrayListMultimap.create();
//
//		XPathFactory xPathfactory = XPathFactory.newInstance();
//		XPath xpath = xPathfactory.newXPath();
//
//		for (String field : this.getMapping().keySet()) {
//			String xpathVal = getMapping().get(field);
//			
//			XPathExpression expr = xpath.compile(xpathVal);
//			NodeList nodes = (NodeList)expr.evaluate(new InputSource(
//					new StringReader(xml)), XPathConstants.NODESET);
//			
//			List<String> vals = Lists.newArrayList();
//			for (int i = 0 ; i < nodes.getLength() ; i++){
//				vals.add(nodes.item(i).getTextContent().trim());
//			}
//			
//			String value = Joiner.on(", ").skipNulls().join(vals);
//			
//			logger.info("xpath for field : " + field + " is : " + xpathVal + " value : " + value);
//
//			rec.put(field, value);
//		}
//		
//		System.out.println("rec : " + rec);
//
//		return rec.asMap();
//	}
//}
//
//class RSS extends Schema {
//
//	private Map<String, String> mapping = ImmutableMap.<String, String>builder()
//			.put("id", "//*[local-name() = 'id']")
//			.put("title", "//*[local-name() = 'title']")
//			.put("author", "//*[local-name() = 'author']")
//			.build();
//	
//	@Override
//	Map<String, String> getMapping() {
//		return this.mapping;
//	}
//	
//}
//
//class SruDC extends Schema {
//	
//	private Map<String, String> mapping = ImmutableMap.<String, String>builder()
//			.put("source", "//*[local-name() = 'source']")
//			.put("publisher", "//*[local-name() = 'publisher']")
//			.put("title", "//*[local-name() = 'title']")
//			.put("subject", "//*[local-name() = 'subject']")
//			.put("contributor", "//*[local-name() = 'contributor']")
//			.put("rights", "//*[local-name() = 'rights']")
//			.put("creator", "//*[local-name() = 'creator']")
//			.put("coverage", "//*[local-name() = 'coverage']")
//			.put("type", "//*[local-name() = 'type']")
////			.put("title", "//*[local-name() = 'title']")
//			.put("format", "//*[local-name() = 'format']")
//			.put("identifier", "//*[local-name() = 'identifier']")
//			.put("relation", "//*[local-name() = 'relation']")
//			.put("language", "//*[local-name() = 'language']")
//			.put("description", "//*[local-name() = 'description']")
//			.put("date", "//*[local-name() = 'date']")
//			.build();
//
//	@Override
//	Map<String, String> getMapping() {
//		return this.mapping;
//
//	}
//}
