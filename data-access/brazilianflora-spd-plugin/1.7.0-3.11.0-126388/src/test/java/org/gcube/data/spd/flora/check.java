//package org.gcube.data.spd.flora;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.net.URI;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.xml.stream.XMLEventReader;
//import javax.xml.stream.XMLInputFactory;
//import javax.xml.stream.XMLStreamException;
//import javax.xml.stream.events.XMLEvent;
//
//import org.gcube.common.core.utils.logging.GCUBELog;
//
//
//public class check {
//	static GCUBELog logger = new GCUBELog(check.class);
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		try {
//			fill();
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//
//	/**
//	 * Parse every path looking for synonyms
//	 * @throws IOException 
//	 * @throws MalformedURLException 
//	 */
//	protected static void fill() throws SQLException, MalformedURLException, IOException {		
//
//
//		ResultSet res = null;
//		Database database = null;  
//
//		try {
//			database = new Database();  			
//			database.connect();
//
//			logger.trace("select id, id_parent from " + FloraPlugin.tableName + " where path is null and status='Accepted name'" );
//			res = database.get("select id, id_parent from " + FloraPlugin.tableName + " where path is null and status='Accepted name'" );
//
//			while(res.next()) {
//				String id = res.getString(1);
//				String id_parent = res.getString(2);
//
//				disco("http://checklist.florabrasil.net/service/FULLRECORD/VERSION/2012/FORMAT/xml/LANG/en/ID/" + id, id, id_parent);		   	
//
//			}			
//		} catch (SQLException sqlExcept) {
//
//			logger.error("SQL Exception", sqlExcept);
//		}finally{
//			if (res != null)
//				res.close();
//
//			database.shutDown();
//		}
//
//	}
//
//	/**
//	 * Parse every path and populate db
//	 */
//	public static void disco(String pathUrl, String id, String id_parent) throws SQLException, MalformedURLException, IOException {
//
//
//		if (Utils.urlExists(pathUrl)){
//			//			String status = "";
//
//			//			logger.trace(pathUrl);
//			InputStream is = null;
//			XMLInputFactory ifactory;
//			XMLEventReader eventReader = null;
//			try {	
//				//				logger.trace("open 3");
//				is =URI.create(pathUrl).toURL().openStream();
//				ifactory = XMLInputFactory.newInstance();
//				eventReader = ifactory.createXMLEventReader(is, "utf-8");
//				Map<String, String> values = new HashMap<String, String>();
//				while (eventReader.hasNext()){
//					XMLEvent event = eventReader.nextEvent();
//
//					if (Utils.checkStartElement(event, "plain_name")){	
//						//						logger.trace(readCharacters(eventReader));
//						values.put("plain_name", Utils.readCharacters(eventReader));		
//						continue;
//					}else if (Utils.checkStartElement(event, "author")){
//						//						logger.trace(readCharacters(eventReader));
//						values.put("author", Utils.readCharacters(eventReader));			
//						continue;
//					}else if (Utils.checkStartElement(event, "rank")){
//						//						logger.trace(readCharacters(eventReader));
//						values.put("rank", Utils.readCharacters(eventReader));		
//						continue;
//					}else if (Utils.checkStartElement(event, "status")){	
//						//						logger.trace(readCharacters(eventReader));
//						values.put("status", Utils.readCharacters(eventReader));		
//						continue;
//					}else if (Utils.checkStartElement(event, "qualifier")){
//						//						logger.trace(readCharacters(eventReader));
//						values.put("qualifier", Utils.readCharacters(eventReader));		
//						continue;
//
//
//					} else if (Utils.checkEndElement(event, "record")){		
//						//												logger.trace(path);
//						ArrayList<String> terms = new ArrayList<String>();
//						ResultSet res = null;
//						Database database = null;  
//						try {
//							database = new Database();  			
//							database.connect();
//							//			logger.trace("select count(*) from "+ FloraPlugin.tableName + " where id = '" + id + "'");
//							res = database.get("select count(*) from "+ FloraPlugin.tableName + " where id = '" + id + "'");
//
//							if(res.next()) {						
//
//								String stmt = "";
//								terms.add(id);
//								terms.add(values.get("rank"));
//								terms.add(values.get("plain_name"));
//								terms.add(id_parent);
//								terms.add(values.get("author"));
//								terms.add(values.get("status"));
//								terms.add(values.get("qualifier"));
//
//								if  (res.getInt(1) == 0) {
//									//Set values
//									stmt = ("insert into "+ FloraPlugin.tableName + " (id, rank, scientific_name, id_parent, citation, status, qualifier) values (?, ?, ?, ?, ?, ?, ?)");
//								}
//								else{	
//									//										logger.trace("update "+ FloraPlugin.tableName + " set id = ?, rank = ?, scientific_name = ?, id_parent = ?, citation = ?, status = ? where id = '" + id + "'");
//									stmt = ("update "+ FloraPlugin.tableName + " set id = ?, rank = ?, scientific_name = ?, id_parent = ?, citation = ?, status = ?, qualifier = ? where id = '" + id + "'");
//								}
//
//								if (!database.preStatement(stmt, terms)){
//									logger.trace("error");
//								}
//								terms.clear();
//
//							}
//
//						}
//						catch (SQLException e) {
//							logger.error("SQL Exception",e);
//						} 
//
//						continue;
//
//					} else if (Utils.checkEndElement(event, "result")){		
//						//						logger.trace("break");
//						break;
//					}
//				}
//			} catch (Exception e) {
//				logger.error("ExceptionPrintStackTrace",e);
//			}finally{				
//				try {
//					//					logger.trace("close 3");
//					if (eventReader != null)
//						eventReader.close();
//					if (is != null)
//						is.close();
//				} catch (XMLStreamException e) {
//					logger.error("XMLStreamException",e);
//				}
//			}
//		}
//	}
//
//
//
//}
