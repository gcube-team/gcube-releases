package org.gcube.common.searchservice.searchlibrary.resultset.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.axis.utils.XMLUtils;
import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementWSEPR;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * Helper class used by {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} to create
 * and manipulate xml files / content
 * 
 * @author UoA
 */
public class RSXMLHelper {
	/**
	 * The Logger this class uses
	 */
	private static Logger log = Logger.getLogger(RSXMLHelper.class);
	
	/**
	 * Creates an XML representation of a {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * head part and persists it to storage
	 * 
	 * @param filename The name of the file that must be created to store the created head part
	 * @param next The name of the file that will hold the next header in the chain
	 * @param properties The properties the head should have
	 * @param dataFlow Whether or not the RS supports on demand production or results
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static void createHead(String filename,String next,String []properties,boolean dataFlow) throws Exception{
		Document dom;
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.newDocument();
			Element rootEle = dom.createElement(RSConstants.ResultSetTag);
			dom.appendChild(rootEle);
			Element headEle = dom.createElement(RSConstants.HeadTag);
			rootEle.appendChild(headEle);
			Element propsEle = dom.createElement(RSConstants.PropertiesTag);
			headEle.appendChild(propsEle);
			{
				Element propEle=dom.createElement(RSConstants.PropertyTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.isHead);
				Text propText = dom.createTextNode("yes");
				propEle.appendChild(propText);
				propsEle.appendChild(propEle);
			}
			{
				Element propEle=dom.createElement(RSConstants.PropertyTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.localFileName);
				Text propText = dom.createTextNode(filename);
				propEle.appendChild(propText);
				propsEle.appendChild(propEle);
			}
			
			{
				Element propEle=dom.createElement(RSConstants.PropertyTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.dataFlow);
				Text propText = dom.createTextNode(""+dataFlow);
				propEle.appendChild(propText);
				propsEle.appendChild(propEle);
			}
			
			Element partsEle = dom.createElement(RSConstants.PartsTag);
			headEle.appendChild(partsEle);
			{
				Element propEle=dom.createElement(RSConstants.PartTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.nextLink);
				Text propText = dom.createTextNode(next);
				propEle.appendChild(propText);
				partsEle.appendChild(propEle);
			}
			{
				Element propEle=dom.createElement(RSConstants.PartTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.previousLink);
				Text propText = dom.createTextNode("no");
				propEle.appendChild(propText);
				partsEle.appendChild(propEle);
			}

			Element custpropsEle = dom.createElement(RSConstants.CustomPropertiesTag);
			headEle.appendChild(custpropsEle);
			for(int i=0;i<properties.length;i+=1){
				try{
					DocumentBuilderFactory datadbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder datadb = datadbf.newDocumentBuilder();
					Document datadom = datadb.parse(new InputSource(new StringReader(properties[i])));
					Element imp=datadom.getDocumentElement();
					custpropsEle.appendChild(dom.importNode(imp,true));
				}catch(Exception e){
					log.error("could not create custom property element");
				}
			}
		} catch (Exception e){
			log.error("Caught exception while creating head document. Throwing new Exception",e);
			throw new Exception("Caught exception while creating head document");
		}
		try{
			synchronized(RSConstants.lockHead){
				RSXMLHelper.persistDocument(dom,filename);
				dom=null;
			}
		}catch(Exception e){
			log.error("Could not serialize head to file "+filename+". Throwing new Exception",e);
			throw new Exception("Could not serialize head to file "+filename);
		}
	}

	/**
	 * Creates an XML representation of a {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * head part and persists it to storage
	 * 
	 * @param filename The name of the file that must be created to store the created head part
	 * @param next The name of the file that will hold the next header in the chain
	 * @param properties The properties the head should have
	 * @param dataFlow Whether or not the RS supports on demand production or results
	 * @param access access leasing
	 * @param forward is forward only property enabled?
	 * @param date time leasing 
	 * @param pkey private key
	 * @param encryptedkey encrypted key
	 * @param allprops all properties in a string
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public static void createHead(String filename,String next,String []properties,boolean dataFlow,
			int access, boolean forward, Date date, String pkey, String encryptedkey, String allprops) throws Exception{
		Document dom;
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.newDocument();
			Element rootEle = dom.createElement(RSConstants.ResultSetTag);
			dom.appendChild(rootEle);
			Element headEle = dom.createElement(RSConstants.HeadTag);
			rootEle.appendChild(headEle);
			Element propsEle = dom.createElement(RSConstants.PropertiesTag);
			headEle.appendChild(propsEle);
			{
				Element propEle=dom.createElement(RSConstants.PropertyTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.isHead);
				Text propText = dom.createTextNode("yes");
				propEle.appendChild(propText);
				propsEle.appendChild(propEle);
			}
			{
				Element propEle=dom.createElement(RSConstants.PropertyTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.localFileName);
				Text propText = dom.createTextNode(filename);
				propEle.appendChild(propText);
				propsEle.appendChild(propEle);
			}
			
			{
				Element propEle=dom.createElement(RSConstants.PropertyTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.dataFlow);
				Text propText = dom.createTextNode(""+dataFlow);
				propEle.appendChild(propText);
				propsEle.appendChild(propEle);
			}

			{
				Element propEle=dom.createElement(RSConstants.PropertyTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.access);
				Text propText = dom.createTextNode(""+access);
				propEle.appendChild(propText);
				propsEle.appendChild(propEle);
			}
			{
				Element propEle=dom.createElement(RSConstants.PropertyTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.forward);
				Text propText = dom.createTextNode(""+forward);
				propEle.appendChild(propText);
				propsEle.appendChild(propEle);
			}
			{
				Element propEle=dom.createElement(RSConstants.PropertyTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.expireDate);
				Text propText = dom.createTextNode(""+date.getTime());
				propEle.appendChild(propText);
				propsEle.appendChild(propEle);
			}
			
			if (pkey!=null){
				log.info("Public Key: "+ pkey +" Encrited Key:"+ encryptedkey);	
				Element propEle=dom.createElement(RSConstants.PropertyTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.pKey);
				Text propText = dom.createTextNode(pkey);
				propEle.appendChild(propText);
				propsEle.appendChild(propEle);
				
				propEle=dom.createElement(RSConstants.PropertyTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.encKey);
				propText = dom.createTextNode(encryptedkey);
				propEle.appendChild(propText);
				propsEle.appendChild(propEle);
				
			}

			Element partsEle = dom.createElement(RSConstants.PartsTag);
			headEle.appendChild(partsEle);
			{
				Element propEle=dom.createElement(RSConstants.PartTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.nextLink);
				Text propText = dom.createTextNode(next);
				propEle.appendChild(propText);
				partsEle.appendChild(propEle);
			}
			{
				Element propEle=dom.createElement(RSConstants.PartTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.previousLink);
				Text propText = dom.createTextNode("no");
				propEle.appendChild(propText);
				partsEle.appendChild(propEle);
			}

			if (allprops == null){
				Element custpropsEle = dom.createElement(RSConstants.CustomPropertiesTag);
				headEle.appendChild(custpropsEle);
				for(int i=0;i<properties.length;i+=1){
					try{
						DocumentBuilderFactory datadbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder datadb = datadbf.newDocumentBuilder();
						Document datadom = datadb.parse(new InputSource(new StringReader(properties[i])));
						Element imp=datadom.getDocumentElement();
						custpropsEle.appendChild(dom.importNode(imp,true));
					}catch(Exception e){
						log.error("could not create custom property element");
					}
				}
			}else{
				Element imp=null;
				try{
					DocumentBuilderFactory datadbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder datadb = datadbf.newDocumentBuilder();
//					Document datadom = datadb.parse(new InputSource(new StringReader(RSXMLHelper.stripProperties(properties))));
					Document datadom = datadb.parse(new InputSource(new StringReader(allprops)));
					imp=datadom.getDocumentElement();
				}catch(Exception e) {
					log.error("Could not parse properties. Thorwing Exception",e);
					throw new Exception("Could not parse properties");
				}
				try{
					headEle.appendChild(dom.importNode(imp,true));
				}catch(Exception e){
					log.error("Could not add properties. Throwing Exception",e);
					throw new Exception("Could not add properties");
				}

			}
			
		} catch (Exception e){
			log.error("Caught exception while creating head document. Throwing new Exception",e);
			throw new Exception("Caught exception while creating head document");
		}
		try{
			synchronized(RSConstants.lockHead){
				RSXMLHelper.persistDocument(dom,filename);
				dom=null;
			}
		}catch(Exception e){
			log.error("Could not serialize head to file "+filename+". Throwing new Exception",e);
			throw new Exception("Could not serialize head to file "+filename);
		}
	}

	/**
	 * Creates an XML representation of a {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * head part and persists it to storage
	 * 
	 * @param filename The name of the file that must be created to store the created head part
	 * @param next The name of the file that will hold the next header in the chain
	 * @param properties The serialized properties the head should have
	 * @param dataFlow Whether or not the RS supports on demand production or results
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static void createHead(String filename,String next,String properties,boolean dataFlow) throws Exception{
		Document dom;
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.newDocument();
			Element rootEle = dom.createElement(RSConstants.ResultSetTag);
			dom.appendChild(rootEle);
			Element headEle = dom.createElement(RSConstants.HeadTag);
			rootEle.appendChild(headEle);
			Element propsEle = dom.createElement(RSConstants.PropertiesTag);
			headEle.appendChild(propsEle);
	
			{
				Element propEle=dom.createElement(RSConstants.PropertyTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.isHead);
				Text propText = dom.createTextNode("yes");
				propEle.appendChild(propText);
				propsEle.appendChild(propEle);
			}
			{
				Element propEle=dom.createElement(RSConstants.PropertyTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.localFileName);
				Text propText = dom.createTextNode(filename);
				propEle.appendChild(propText);
				propsEle.appendChild(propEle);
			}
			
			{
				Element propEle=dom.createElement(RSConstants.PropertyTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.dataFlow);
				Text propText = dom.createTextNode(""+dataFlow);
				propEle.appendChild(propText);
				propsEle.appendChild(propEle);
			}
			
			Element partsEle = dom.createElement(RSConstants.PartsTag);
			headEle.appendChild(partsEle);
			{
				Element propEle=dom.createElement(RSConstants.PartTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.nextLink);
				Text propText = dom.createTextNode(next);
				propEle.appendChild(propText);
				partsEle.appendChild(propEle);
			}
			{
				Element propEle=dom.createElement(RSConstants.PartTag);
				propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.previousLink);
				Text propText = dom.createTextNode("no");
				propEle.appendChild(propText);
				partsEle.appendChild(propEle);
			}
			Element imp=null;
			try{
				DocumentBuilderFactory datadbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder datadb = datadbf.newDocumentBuilder();
//				Document datadom = datadb.parse(new InputSource(new StringReader(RSXMLHelper.stripProperties(properties))));
				Document datadom = datadb.parse(new InputSource(new StringReader(properties)));
				imp=datadom.getDocumentElement();
			}catch(Exception e) {
				log.error("Could not parse properties. Thorwing Exception",e);
				throw new Exception("Could not parse properties");
			}
			
			try{
				headEle.appendChild(dom.importNode(imp,true));
			}catch(Exception e){
				log.error("Could not add properties. Throwing Exception",e);
				throw new Exception("Could not add properties");
			}
		} catch (Exception e){
			log.error("Caught exception while creating head document. Throwing new Exception",e);
			throw new Exception("Caught exception while creating head document");
		}
		try{
			synchronized(RSConstants.lockHead){
				RSXMLHelper.persistDocument(dom,filename);
				dom=null;
			}
		}catch(Exception e){
			log.error("Could not serialize head to file "+filename+". Throwing new Exception",e);
			throw new Exception("Could not serialize head to file "+filename);
		}
	}
	
	/**
	 * Overrides existing custom properties and updates them
	 * 
	 * @param headName the name of the head file
	 * @param properties the properties
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static void updateProperties(String headName,String []properties) throws Exception{
		Document dom=RSXMLHelper.getDocument(headName);
		NodeList head=dom.getElementsByTagName(RSConstants.HeadTag);
		Element headEle=(Element)head.item(0);
		NodeList custom=dom.getElementsByTagName(RSConstants.CustomPropertiesTag);
		if(custom!=null && custom.getLength()>0){
//			for(int i=0;i<custom.getLength();i+=1)  dom.removeChild(custom.item(0));
			Element c=(Element)custom.item(0);
			c.getParentNode().removeChild(c);
		}
		Element custpropsEle = dom.createElement(RSConstants.CustomPropertiesTag);
		headEle.appendChild(custpropsEle);
		for(int i=0;i<properties.length;i+=1){
			try{
				DocumentBuilderFactory datadbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder datadb = datadbf.newDocumentBuilder();
				Document datadom = datadb.parse(new InputSource(new StringReader(properties[i])));
				Element imp=datadom.getDocumentElement();
				custpropsEle.appendChild(dom.importNode(imp,true));
			}catch(Exception e){
				log.error("could not create custom property element");
			}
		}
		Vector<String> props=RSXMLHelper.getProperties(dom,PropertyElementWSEPR.propertyType);
		if(props!=null){
			for(String pro : props){
				try{
					DocumentBuilderFactory datadbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder datadb = datadbf.newDocumentBuilder();
					Document datadom = datadb.parse(new InputSource(new StringReader(pro)));
					Element imp=datadom.getDocumentElement();
					custpropsEle.appendChild(dom.importNode(imp,true));
				}catch(Exception e){
					log.error("could not create custom property element");
				}
			}
		}
		synchronized(RSConstants.lockHead){
			RSXMLHelper.persistDocument(dom,headName);
			dom=null;
		}
	}

	/**
	 * Overrides existing custom properties and updates them
	 * 
	 * @param headName the name of the head file
	 * @param serializedProps the serialized properties
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static void updateProperties(String headName,String serializedProps) throws Exception{
		Document dom=RSXMLHelper.getDocument(headName);
		NodeList head=dom.getElementsByTagName(RSConstants.HeadTag);
		Element headEle=(Element)head.item(0);
		NodeList custom=dom.getElementsByTagName(RSConstants.CustomPropertiesTag);
		if(custom!=null && custom.getLength()>0){
//			for(int i=0;i<custom.getLength();i+=1) dom.removeChild(custom.item(0));
			Element c=(Element)custom.item(0);
			c.getParentNode().removeChild(c);
		}
		Element imp=null;
		try{
			DocumentBuilderFactory datadbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder datadb = datadbf.newDocumentBuilder();
			Document datadom = datadb.parse(new InputSource(new StringReader(serializedProps)));
			imp=datadom.getDocumentElement();
		}catch(Exception e) {
			log.error("Could not parse properties. Thorwing Exception",e);
			throw new Exception("Could not parse properties");
		}
		try{
			headEle.appendChild(dom.importNode(imp,true));
		}catch(Exception e){
			log.error("Could not add properties. Throwing Exception",e);
			throw new Exception("Could not add properties");
		}
		custom=dom.getElementsByTagName(RSConstants.CustomPropertiesTag);
		Element custpropsEle=(Element)custom.item(0);
		Vector<String> props=RSXMLHelper.getProperties(dom,PropertyElementWSEPR.propertyType);
		if(props!=null){
			for(String pro : props){
				try{
					DocumentBuilderFactory datadbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder datadb = datadbf.newDocumentBuilder();
					Document datadom = datadb.parse(new InputSource(new StringReader(pro)));
					Element impp=datadom.getDocumentElement();
					custpropsEle.appendChild(dom.importNode(impp,true));
				}catch(Exception e){
					log.error("could not create custom property element");
				}
			}
		}
		synchronized(RSConstants.lockHead){
			RSXMLHelper.persistDocument(dom,headName);
			dom=null;
		}
	}
	
	/**
	 * Persists the provided DOM object in a file wholse filename is provided  
	 * 
	 * @param dom The DOM object to persist
	 * @param filename The filename to place the serialization in
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static void persistDocument(Document dom, String filename) throws Exception{
		FileOutputStream file=null;
		try
		{
			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);
			file=new FileOutputStream(new File(RSFileHelper.publicToTmp(filename)));
			XMLSerializer serializer = new XMLSerializer((file), format);
			serializer.serialize(dom);
			file.flush();
			file.close();
			file=null;
			File tmpFile=new File(RSFileHelper.publicToTmp(filename));
			tmpFile.renameTo(new File(filename));
		} catch(Exception e) {
			if(file!=null) file.close();
			log.error("Could not persist document to file "+filename+". Throwing new Exception",e);
		    throw new Exception("Could not persist document to file "+filename);
		}
	}
	
	/**
	 * Retrieves the DOM object parsing the content of the provided file
	 * 
	 * @param filename The file name to parse
	 * @return The created DOM object
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static Document getDocument(String filename) throws Exception{
		try{
			DocumentBuilderFactory datadbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder datadb = datadbf.newDocumentBuilder();
			Document dom=null;
			long startTime=Calendar.getInstance().getTimeInMillis();
			while(true){
				if(Calendar.getInstance().getTimeInMillis()-startTime>=RSConstants.sleepMax){
					log.error("Maximum waiting ammount of time reached. Throwing Exception");
					throw new Exception("Maximum waiting ammount of time reached");
				}
				try{
					dom=datadb.parse(new File(filename));
				}catch (Exception e){
					log.error("Could not parse file "+filename+". Trying again");
					dom=null;
				}
				if(dom!=null) break;
				try{
					synchronized(RSConstants.sleepOnIt){
						RSConstants.sleepOnIt.wait(RSConstants.sleepTime);
					}
				}catch(Exception ee){}
				try{
					dom=datadb.parse(new File(filename));
				}catch (Exception e){
					log.error("Could not parse file "+filename+". Trying again");
					dom=null;
				}
				if(dom!=null) break;
			}
			return dom;
		}catch(Exception e) {
			log.error("Could not parse document from file "+filename+" Throwing Exception",e);
			throw new Exception("Could not parse file "+filename);
		}
	}
	
//	public static void setNext(Document dom,String next) throws Exception{
//		try{
//			Element docEle = dom.getDocumentElement();
//			NodeList headnl = docEle.getElementsByTagName(RSConstants.HeadTag);
//			if(headnl == null || headnl.getLength() <= 0 || headnl.getLength() !=1){
//				log.error("Not single Head element found. Throwing Exception");
//				throw new Exception("Not single Head element found");
//			}
//			NodeList partsnl = ((Element)headnl.item(0)).getElementsByTagName(RSConstants.PartsTag);
//			if(partsnl == null || partsnl.getLength() <= 0 || partsnl.getLength() !=1){
//				log.error("Not single Parts element found. Throwing Exception");
//				throw new Exception("Not single Parts element found");
//			}
//			Element propEle=dom.createElement(RSConstants.PartTag);
//			propEle.setAttribute(RSConstants.PropertyAttributeNameTag,RSConstants.nextLink);
//			Text propText = dom.createTextNode(next);
//			propEle.appendChild(propText);
//			partsnl.item(0).appendChild(propEle);
//		}catch(Exception e){
//			log.error("Could not update next property. Throwing Exception".e);
//			throw new Exception("Could not update next property");
//		}
//	}
//	
	/**
	 * Adds the provided property to the respective placeholder in the provided DOM representation of the
	 * head part
	 * 
	 * @param dom The DOM object representing the head part
	 * @param prop The property to add
	 * @return The updated DOM object
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static Document addCustomProperty(Document dom, PropertyElementBase prop) throws Exception{
		try{
			Element docEle = dom.getDocumentElement();
			NodeList headnl = docEle.getElementsByTagName(RSConstants.HeadTag);
			if(headnl == null || headnl.getLength() <= 0 || headnl.getLength() !=1){
				log.error("Not single Head element found. Throwing Exception");
				throw new Exception("Not single Head element found");
			}
			NodeList propsnl = ((Element)headnl.item(0)).getElementsByTagName(RSConstants.CustomPropertiesTag);
			if(propsnl == null || propsnl.getLength() <= 0 || propsnl.getLength() !=1){
				log.error("Not single custom properties element found. Throwing Exception");
				throw new Exception("Not single custom properties element found");
			}
			DocumentBuilderFactory datadbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder datadb = datadbf.newDocumentBuilder();
			Document datadom = datadb.parse(new InputSource(new StringReader(prop.RS_toXML())));
			Element imp=datadom.getDocumentElement();
			propsnl.item(0).appendChild(dom.importNode(imp,true));
			return dom;
		}catch(Exception e){
			log.error("could not add custom property. Throwing Exception",e);
			throw new Exception("could not add custom property");
		}
	}

	/**
	 * Adds the provided property to the respective place holder in the provided DOM representation of the
	 * head part
	 * 
	 * @param dom The DOM object representing the head part
	 * @param newaccess The property to add
	 * @return The updated DOM object
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public static Document updateAccessCounter(Document dom, int newaccess) throws Exception{
		try{
			Element docEle = dom.getDocumentElement();
			NodeList headnl = docEle.getElementsByTagName(RSConstants.HeadTag);
			if(headnl == null || headnl.getLength() <= 0 || headnl.getLength() !=1){
				log.error("Not single Head element found. Throwing Exception");
				throw new Exception("Not single Head element found");
			}
			NodeList propsnl = ((Element)headnl.item(0)).getElementsByTagName(RSConstants.PropertyTag);
			if(propsnl == null || propsnl.getLength() <= 0){
				log.info("No access counter encountered in the RS header. Throwing Exception");
				throw new Exception("No access counter encountered in the RS header.");
			}
			for(int i = 0; i < propsnl.getLength(); i++){
				NamedNodeMap map = propsnl.item(i).getAttributes();
				Node n = map.getNamedItem("Name");
				if (n.getNodeValue().equalsIgnoreCase(RSConstants.access)){
					propsnl.item(i).setTextContent(""+newaccess);
				}
			}
			log.info("Updated access counter. ");
			return dom;
		}catch(Exception e){
			log.info("Could not updated access counter. Throwing Exception",e);
			throw new Exception("Could not updated access counter");
		}
	}

	/**
	 * Retrives the properties with the specified type from the DOM object representing a head part
	 * 
	 * @param dom The DOM object containing the Head content
	 * @param type The type of properties to retrieve
	 * @return The retrieved properties
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static Vector<String> getProperties(Document dom,String type) throws Exception{
		Vector<String> ret=new Vector<String>();
		try{
			Element docEle = dom.getDocumentElement();
			NodeList headnl = docEle.getElementsByTagName(RSConstants.HeadTag);
			if(headnl == null || headnl.getLength() <= 0 || headnl.getLength() !=1){
				log.error("Could not find single head element. Throwing Exception");
				throw new Exception("Could not find single head element");
			}
			NodeList propsnl = ((Element)headnl.item(0)).getElementsByTagName(RSConstants.CustomPropertiesTag);
			if(propsnl == null || propsnl.getLength() <= 0 || propsnl.getLength() !=1){
				log.error("Could not find single custom properties element. Throwing Exception");
				throw new Exception("Could not find single custom properties element");
			}
			NodeList typenl = ((Element)propsnl.item(0)).getElementsByTagName(type);
			if(typenl == null || typenl.getLength() < 0){
				return ret;
			}
			for(int i=0;i<typenl.getLength();i+=1){
				try{
					DocumentBuilderFactory datadbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder datadb = datadbf.newDocumentBuilder();
					Document newdom = datadb.newDocument();
					Element rootEle=(Element)newdom.importNode(typenl.item(i).cloneNode(true),true);
					newdom.appendChild(rootEle);
					OutputFormat format = new OutputFormat(newdom);
					format.setIndenting(false);
					format.setOmitDocumentType(true);
					format.setOmitXMLDeclaration(true);
					StringWriter writer = new StringWriter();
					XMLSerializer serializer = new XMLSerializer(writer, format);
					serializer.serialize(newdom);
					ret.add(writer.toString());
				}catch(Exception e){
					log.error("could not copy property. Continuing",e);
				}
			}
		}catch(Exception e){
			log.error("Could not retrieve properties. Throwing exception",e);
			throw new Exception("Could not retrieve properties");
		}
		return ret;
	}

	/**
	 * Retrieves the previous or nect link the provided document has
	 * 
	 * @param dom The document to retrieve the document from
	 * @param which {@link RSConstants#nextLink} or {@link RSConstants#previousLink}
	 * @return The value of the requested link
	 * @throws Exception The link value could not be retrieved
	 */
	public static String getLink(Document dom,String which) throws Exception{
		try{
			Element docEle = dom.getDocumentElement();
			NodeList headnl = docEle.getElementsByTagName(RSConstants.HeadTag);
			if(headnl == null || headnl.getLength() <= 0 || headnl.getLength() !=1){
				log.error("Could not find single head element. Throwing Exception");
				throw new Exception("Could not find single head element");
			}
			NodeList propsnl = ((Element)headnl.item(0)).getElementsByTagName(RSConstants.PartsTag);
			if(propsnl == null || propsnl.getLength() <= 0 || propsnl.getLength() !=1){
				log.error("Could not find single parts element. Throwing Exception");
				throw new Exception("Could not find single parts element");
			}
			NodeList propnl = ((Element)propsnl.item(0)).getElementsByTagName(RSConstants.PartTag);
			if(propnl != null && propnl.getLength() >= 0){
				for(int i = 0 ; i < propnl.getLength();i++) {
					if(((Element)propnl.item(i)).getAttribute(RSConstants.PropertyAttributeNameTag).equals(which)){
						return ((Element)propnl.item(i)).getFirstChild().getNodeValue();
					}
				}
			}
			log.error("Could not find part elements at all or with the attribute name "+which+". Throwing Exception");
			throw new Exception("Could not find part elements at all or with the attribute name "+which);
		}catch(Exception e){
			log.error("Could not retrieve link "+which+". Throwing exception",e);
			throw new Exception("Could not retrieve link");
		}
	}
	
	/**
	 * Returns whether or not the RS is populated with results on demand
	 * 
	 * @param dom The document to retrieve the document from
	 * @return <code>true</code> if the results are proiduced on demand,<code>false</code> otherwise 
	 * @throws Exception The link value could not be retrieved
	 */
	public static boolean getDataFlow(Document dom) throws Exception{
		try{
			Element docEle = dom.getDocumentElement();
			NodeList headnl = docEle.getElementsByTagName(RSConstants.HeadTag);
			if(headnl == null || headnl.getLength() <= 0 || headnl.getLength() !=1){
				log.error("Could not find single head element. Throwing Exception");
				throw new Exception("Could not find single head element");
			}
			NodeList propsnl = ((Element)headnl.item(0)).getElementsByTagName(RSConstants.PropertiesTag);
			if(propsnl == null || propsnl.getLength() <= 0 || propsnl.getLength() !=1){
				log.error("Could not find single parts element. Throwing Exception");
				throw new Exception("Could not find single parts element");
			}
			NodeList propnl = ((Element)propsnl.item(0)).getElementsByTagName(RSConstants.PropertyTag);
			if(propnl != null && propnl.getLength() >= 0){
				for(int i = 0 ; i < propnl.getLength();i++) {
					if(((Element)propnl.item(i)).getAttribute(RSConstants.PropertyAttributeNameTag).equals(RSConstants.dataFlow)){
						try{
							return Boolean.parseBoolean(((Element)propnl.item(i)).getFirstChild().getNodeValue());
						}catch(Exception e){
							log.error("invalid data flow property value. Throwing Exception.",e);
							throw new Exception("invalid data flow property value");
						}
					}
				}
			}
			return false;
		}catch(Exception e){
			log.error("Could not retrieve link data flow property. Throwing exception",e);
			throw new Exception("Could not retrieve link data flow property");
		}
	}
	
	/**
	 * Returns whether or not the RS is populated with results access
	 * 
	 * @param dom The document to retrieve the document from
	 * @return <code>int</code> access window 
	 * @throws Exception The link value could not be retrieved
	 */
	public static int getAccess(Document dom) throws Exception{
		try{
			Element docEle = dom.getDocumentElement();
			NodeList headnl = docEle.getElementsByTagName(RSConstants.HeadTag);
			if(headnl == null || headnl.getLength() <= 0 || headnl.getLength() !=1){
				log.error("Could not find single head element. Throwing Exception");
				throw new Exception("Could not find single head element");
			}
			NodeList propsnl = ((Element)headnl.item(0)).getElementsByTagName(RSConstants.PropertiesTag);
			if(propsnl == null || propsnl.getLength() <= 0 || propsnl.getLength() !=1){
				log.error("Could not find single parts element. Throwing Exception");
				throw new Exception("Could not find single parts element");
			}
			NodeList propnl = ((Element)propsnl.item(0)).getElementsByTagName(RSConstants.PropertyTag);
			if(propnl != null && propnl.getLength() >= 0){
				for(int i = 0 ; i < propnl.getLength();i++) {
					if(((Element)propnl.item(i)).getAttribute(RSConstants.PropertyAttributeNameTag).equals(RSConstants.access)){
						try{
							return Integer.parseInt(((Element)propnl.item(i)).getFirstChild().getNodeValue());
						}catch(Exception e){
							log.error("invalid access property value. Throwing Exception.",e);
							throw new Exception("invalid access property value");
						}
					}
				}
			}
			return -1;
		}catch(Exception e){
			log.error("Could not retrieve link access property. Throwing exception",e);
			throw new Exception("Could not retrieve link access property");
		}
	}
	
	/**
	 * Retrives the serialized version of the CustomPropertied that are contained in the Head part DOM object
	 * 
	 * @param dom The DOM object containing the head part
	 * @return The serialized properties
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static String retrieveCustomProperties(Document dom) throws Exception{
		try{
			Element docEle = dom.getDocumentElement();
			NodeList headnl = docEle.getElementsByTagName(RSConstants.HeadTag);
			if(headnl == null || headnl.getLength() <= 0 || headnl.getLength() !=1){
				log.error("Could not find single head element. Throwing Exception");
				throw new Exception("Could not find single head element");
			}
			NodeList propsnl = ((Element)headnl.item(0)).getElementsByTagName(RSConstants.CustomPropertiesTag);
			if(propsnl == null || propsnl.getLength() <= 0 || propsnl.getLength() !=1){
				log.error("Could not find single custom properties element. Throwing Exception");
				throw new Exception("Could not find single custom properties element");
			}
			try{
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document newdom = db.newDocument();
				Element rootEle=(Element)newdom.importNode(propsnl.item(0).cloneNode(true),true);
				newdom.appendChild(rootEle);
				OutputFormat format = new OutputFormat(newdom);
				format.setIndenting(false);
				format.setOmitDocumentType(true);
				format.setOmitXMLDeclaration(true);
				StringWriter writer = new StringWriter();
				XMLSerializer serializer = new XMLSerializer(writer, format);
				serializer.serialize(newdom);
				return writer.toString();
			}catch(Exception e){
				log.error("Could not copy property.Throwing exception",e);
				throw new Exception("Could not copy property");
			}
		}catch(Exception e){
			log.error("Could not retrieve custom properties. Throwing exception",e);
			throw new Exception("Could not retrieve custom properties");
		}
	}

	/**
	 * Evaluates the provided xPath and returns a serialization of the results 
	 * 
	 * @param dom The document to use
	 * @param xPath The xPath to be evaluated
	 * @return the retrieved serialization
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static String executeQueryOnDocument(Document dom,String xPath) throws Exception{
		Document newdom=null;
		Element rootEle=null;
		try{
			DocumentBuilderFactory datadbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder datadb = datadbf.newDocumentBuilder();
			newdom = datadb.newDocument();
			rootEle = newdom.createElement(RSConstants.RecordsTag);
			newdom.appendChild(rootEle);
		}catch(Exception e) {
			log.error("Could not create document. Throwing Exception",e);
			throw new Exception("Could not create document");
		}
		try{
			NodeList res=XPathAPI.selectNodeList(dom,xPath);
			for(int i=0;i<res.getLength();i+=1){
				rootEle.appendChild(newdom.importNode(((Element)res.item(i)),true));				
			}
		}catch(Exception e){
			log.error("Could not execute query.Throwing Exception",e);
			throw new Exception("Could not execute query");
		}
		try
		{
			OutputFormat format = new OutputFormat(newdom);
			format.setIndenting(false);
			format.setOmitDocumentType(true);
			format.setOmitXMLDeclaration(true);
			StringWriter writer = new StringWriter();
			XMLSerializer serializer = new XMLSerializer(writer, format);
			serializer.serialize(newdom);
			return writer.toString();
		} catch(Exception e) {
			log.error("Could not serialize results.Throwing Exception",e);
			throw new Exception("Could not serialize results");
		}
	}
	
	/**
	 * Perofmors the provided xPath expression on the serialized xml string provided.
	 * 
	 * @param result The xml serialization to evaluate the expression against
	 * @param xPath The expression to be avaluated
	 * @return <code>true</code> if the evaluation returned result, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static boolean executeQueryOnResults(String result,String xPath) throws Exception{
		try{
			DocumentBuilderFactory datadbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder datadb = datadbf.newDocumentBuilder();
			Document dom = datadb.parse(new InputSource(new StringReader(result)));
			NodeList res=XPathAPI.selectNodeList(dom.getDocumentElement(),xPath);
			if(res.getLength()>0) return true;
			return false;
		}catch(Exception e){
			log.error("Could not execute query on records.Throwing Exception",e);
			throw new Exception("Could not execute query on records");
		}
	}
	
	/**
	 * evaluates the provided xPath expression against the content of the provided file and returns 
	 * the indexes of the results that the evaluation returned results
	 * 
	 * @param filename The file whose results must be iterated over
	 * @param xPath The xpath expression
	 * @return The indexes of the records that produced output during evaluation
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static Vector<Integer> executeQueryOnResultsFile(String filename,String xPath) throws Exception{
		try{
			Vector<Integer> ret=new Vector<Integer>();
			DocumentBuilderFactory datadbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder datadb = datadbf.newDocumentBuilder();
			Document dom = datadb.parse(new File(filename));
			Element root=dom.getDocumentElement();
			NodeList nl=null;
			try{
				nl=root.getElementsByTagName(RSConstants.RecordTag);
				if(nl==null){
					return ret;
				}
			}catch(Exception e){
				return ret; 
			}
			for(int i=0;i<nl.getLength();i+=1){
				//If the record satisfies the xPath, we place its position in the part, to the returned vector of integers
				XPathFactory factory = XPathFactory.newInstance(XPathFactory.DEFAULT_OBJECT_MODEL_URI);
				XPath xpath = factory.newXPath();
				String res = xpath.evaluate(xPath, new InputSource(new StringReader(XMLUtils.ElementToString((Element)nl.item(i)))));
				
				if(res != null && !res.equalsIgnoreCase("")) ret.add(new Integer(i));
			}
			return ret;
		}catch(Exception e){
			log.error("Could not execute query on records.Throwing Exception",e);
			throw new Exception("Could not execute query on records");
		}
	}
	
	/**
	 * Performs a xslt transformation to the input file and dumpt the out put to a new file 
	 * 
	 * @param xslt The XSLT transformation to apply 
	 * @param source The source file to transform
	 * @param target The target file to place the output
	 * @throws Exception An unrecoverable for the operation error has occured
	 */
	public static void transform(String xslt,String source,String target) throws Exception{
		File xmlFile = new File(source);
		Source xmlSource = new StreamSource(xmlFile);
		Source xsltSource = new StreamSource(new StringReader(xslt));
		TransformerFactory transFact =TransformerFactory.newInstance();
		try{
			Transformer trans = transFact.newTransformer(xsltSource);
			File output=new File(target);
			output.createNewFile();
			Writer write=new FileWriter(target);
			Result result = new StreamResult(write);
			trans.transform(xmlSource, result);
		}catch(Exception e){
			log.error("Could not perform transformation. Throwing Exception",e);
			throw new Exception("Could not perform transformation");
		}
	}

	/**
	 * Performs a xslt transformation to the input file and dumpt the out put to a new file 
	 * 
	 * @param xslt The XSLT transformation to apply 
	 * @param source The source file to transform
	 * @param target The target file to place the output
	 * @throws Exception An unrecoverable for the operation error has occured
	 */
	public static void transform(Templates xslt,String source,String target) throws Exception{
		File xmlFile = new File(source);
		Source xmlSource = new StreamSource(xmlFile);
		try{
			Transformer trans = xslt.newTransformer();
			File output=new File(target);
			output.createNewFile();
			Writer write=new FileWriter(target);
			Result result = new StreamResult(write);
			trans.transform(xmlSource, result);
		}catch(Exception e){
			log.error("Could not perform transformation. Throwing Exception",e);
			throw new Exception("Could not perform transformation");
		}
	}
	
	/**
	 * Returns whether or not the RS is populated with results forward
	 * 
	 * @param dom The document to retrieve the document from
	 * @return <code>boolean</code> boolean window 
	 * @throws Exception The value could not be retrieved
	 */
	public static boolean getForward(Document dom) throws Exception{
		try{
			Element docEle = dom.getDocumentElement();
			NodeList headnl = docEle.getElementsByTagName(RSConstants.HeadTag);
			if(headnl == null || headnl.getLength() <= 0 || headnl.getLength() !=1){
				log.error("Could not find single head element. Throwing Exception");
				throw new Exception("Could not find single head element");
			}
			NodeList propsnl = ((Element)headnl.item(0)).getElementsByTagName(RSConstants.PropertiesTag);
			if(propsnl == null || propsnl.getLength() <= 0 || propsnl.getLength() !=1){
				log.error("Could not find single parts element. Throwing Exception");
				throw new Exception("Could not find single parts element");
			}
			NodeList propnl = ((Element)propsnl.item(0)).getElementsByTagName(RSConstants.PropertyTag);
			if(propnl != null && propnl.getLength() >= 0){
				for(int i = 0 ; i < propnl.getLength();i++) {
					if(((Element)propnl.item(i)).getAttribute(RSConstants.PropertyAttributeNameTag).equals(RSConstants.forward)){
						try{
							return Boolean.parseBoolean(((Element)propnl.item(i)).getFirstChild().getNodeValue());
						}catch(Exception e){
							log.error("invalid forward property value. Throwing Exception.",e);
							throw new Exception("invalid forward property value");
						}
					}
				}
			}
			return false;
		}catch(Exception e){
			log.error("Could not retrieve forward property. Throwing exception",e);
			throw new Exception("Could not retrieve forward property");
		}
	}
	
	/**
	 * Set forward only access to this RS
	 * @param dom the dom of the header
	 * @param forward the flag to set for forward
	 * @return The header in a Document
	 * @throws Exception Something went wrong
	 */
	public static Document setForward(Document dom, boolean forward) throws Exception{
		try{
			Element docEle = dom.getDocumentElement();
			NodeList headnl = docEle.getElementsByTagName(RSConstants.HeadTag);
			if(headnl == null || headnl.getLength() <= 0 || headnl.getLength() !=1){
				log.error("Not single Head element found. Throwing Exception");
				throw new Exception("Not single Head element found");
			}
			NodeList propsnl = ((Element)headnl.item(0)).getElementsByTagName(RSConstants.PropertyTag);
			if(propsnl == null || propsnl.getLength() <= 0){
				log.info("No access counter encountered in the RS header. Throwing Exception");
				throw new Exception("No access counter encountered in the RS header.");
			}
			for(int i = 0; i < propsnl.getLength(); i++){
				NamedNodeMap map = propsnl.item(i).getAttributes();
				Node n = map.getNamedItem("Name");
				if (n.getNodeValue().equalsIgnoreCase(RSConstants.forward)){
					Boolean b = forward;
					propsnl.item(i).setTextContent(b.toString());
				}
			}
			log.info("Updated forward. ");
			return dom;
		}catch(Exception e){
			log.info("Could not updated access counter. Throwing Exception",e);
			throw new Exception("Could not updated access counter");
		}
	}


	/**
	 * Returns whether or not the RS is populated with results forward
	 * 
	 * @param dom The document to retrieve the document from
	 * @return <code>boolean</code> boolean window 
	 * @throws Exception The value could not be retrieved
	 */
	public static Date getExpireDate(Document dom) throws Exception{
		try{
			Element docEle = dom.getDocumentElement();
			NodeList headnl = docEle.getElementsByTagName(RSConstants.HeadTag);
			if(headnl == null || headnl.getLength() <= 0 || headnl.getLength() !=1){
				log.error("Could not find single head element. Throwing Exception");
				throw new Exception("Could not find single head element");
			}
			NodeList propsnl = ((Element)headnl.item(0)).getElementsByTagName(RSConstants.PropertiesTag);
			if(propsnl == null || propsnl.getLength() <= 0 || propsnl.getLength() !=1){
				log.error("Could not find single parts element. Throwing Exception");
				throw new Exception("Could not find single parts element");
			}
			NodeList propnl = ((Element)propsnl.item(0)).getElementsByTagName(RSConstants.PropertyTag);
			if(propnl != null && propnl.getLength() >= 0){
				for(int i = 0 ; i < propnl.getLength();i++) {
					if(((Element)propnl.item(i)).getAttribute(RSConstants.PropertyAttributeNameTag).equals(RSConstants.expireDate)){
						try{
							return new Date(Long.parseLong(((Element)propnl.item(i)).getFirstChild().getNodeValue()));
						}catch(Exception e){
							log.error("invalid expire date property value. Throwing Exception.",e);
							throw new Exception("invalid expire date property value");
						}
					}
				}
			}
			return new Date(0);
		}catch(Exception e){
			log.error("Could not retrieve expire date property. Throwing exception",e);
			throw new Exception("Could not retrieve expire date property");
		}
	}
	
	/**
	 * Returns if the RS is populated with results forward
	 * 
	 * @param dom The document to retrieve the document from
	 * @param date the expire date 
	 * @return the header in a document 
	 * @throws Exception The value could not be retrieved
	 */
	public static Document setExpireDate(Document dom, Date date) throws Exception{
		try{
			Element docEle = dom.getDocumentElement();
			NodeList headnl = docEle.getElementsByTagName(RSConstants.HeadTag);
			if(headnl == null || headnl.getLength() <= 0 || headnl.getLength() !=1){
				log.error("Not single Head element found. Throwing Exception");
				throw new Exception("Not single Head element found");
			}
			NodeList propsnl = ((Element)headnl.item(0)).getElementsByTagName(RSConstants.PropertyTag);
			if(propsnl == null || propsnl.getLength() <= 0){
				log.info("No access counter encountered in the RS header. Throwing Exception");
				throw new Exception("No access counter encountered in the RS header.");
			}
			for(int i = 0; i < propsnl.getLength(); i++){
				NamedNodeMap map = propsnl.item(i).getAttributes();
				Node n = map.getNamedItem("Name");
				if (n.getNodeValue().equalsIgnoreCase(RSConstants.expireDate)){
					propsnl.item(i).setTextContent(""+date.getTime());
				}
			}
			log.info("Updated expire date. ");
			return dom;
		}catch(Exception e){
			log.info("Could not updated access counter. Throwing Exception",e);
			throw new Exception("Could not updated access counter");
		}
	}

	/**
	 * Get the encryption key
	 * @param dom The document to retrieve the document from
	 * @return the encrypted key
	 * @throws Exception An error occurred
	 */
	public static String getEncKey(Document dom) throws Exception{
		try{
			Element docEle = dom.getDocumentElement();
			NodeList headnl = docEle.getElementsByTagName(RSConstants.HeadTag);
			if(headnl == null || headnl.getLength() <= 0 || headnl.getLength() !=1){
				log.error("Could not find single head element. Throwing Exception");
				throw new Exception("Could not find single head element");
			}
			NodeList propsnl = ((Element)headnl.item(0)).getElementsByTagName(RSConstants.PropertiesTag);
			if(propsnl == null || propsnl.getLength() <= 0 || propsnl.getLength() !=1){
				log.error("Could not find single parts element. Throwing Exception");
				throw new Exception("Could not find single parts element");
			}
			NodeList propnl = ((Element)propsnl.item(0)).getElementsByTagName(RSConstants.PropertyTag);
			if(propnl != null && propnl.getLength() >= 0){
				for(int i = 0 ; i < propnl.getLength();i++) {
					if(((Element)propnl.item(i)).getAttribute(RSConstants.PropertyAttributeNameTag).equals(RSConstants.encKey)){
						try{
							return ((Element)propnl.item(i)).getFirstChild().getNodeValue();
						}catch(Exception e){
							log.error("invalid encripted encription key property value. Throwing Exception.",e);
							throw new Exception("invalid encripted encription key property value");
						}
					}
				}
			}
			return null;
		}catch(Exception e){
			log.error("Could not retrieve encripted encription key property. Throwing exception",e);
			throw new Exception("Could not retrieve encripted encription key property");
		}
	}

	/**
	 * Get the public key
	 * @param dom The document to retrieve the document from
	 * @return the  key
	 * @throws Exception An error occurred
	 */
	public static String getPublicKey(Document dom) throws Exception{
		try{
			Element docEle = dom.getDocumentElement();
			NodeList headnl = docEle.getElementsByTagName(RSConstants.HeadTag);
			if(headnl == null || headnl.getLength() <= 0 || headnl.getLength() !=1){
				log.error("Could not find single head element. Throwing Exception");
				throw new Exception("Could not find single head element");
			}
			NodeList propsnl = ((Element)headnl.item(0)).getElementsByTagName(RSConstants.PropertiesTag);
			if(propsnl == null || propsnl.getLength() <= 0 || propsnl.getLength() !=1){
				log.error("Could not find single parts element. Throwing Exception");
				throw new Exception("Could not find single parts element");
			}
			NodeList propnl = ((Element)propsnl.item(0)).getElementsByTagName(RSConstants.PropertyTag);
			if(propnl != null && propnl.getLength() >= 0){
				for(int i = 0 ; i < propnl.getLength();i++) {
//					log.trace("Element: "+ ((Element)propnl.item(i)).getAttribute(RSConstants.PropertyAttributeNameTag));
					if(((Element)propnl.item(i)).getAttribute(RSConstants.PropertyAttributeNameTag).equals(RSConstants.pKey)){
						try{
							return ((Element)propnl.item(i)).getFirstChild().getNodeValue();
						}catch(Exception e){
							log.error("invalid encripted encription key property value. Throwing Exception.",e);
							throw new Exception("invalid encripted encription key property value");
						}
					}
				}
			}
			return null;
		}catch(Exception e){
			log.error("Could not retrieve encripted encription key property. Throwing exception",e);
			throw new Exception("Could not retrieve encripted encription key property");
		}
	}
}
