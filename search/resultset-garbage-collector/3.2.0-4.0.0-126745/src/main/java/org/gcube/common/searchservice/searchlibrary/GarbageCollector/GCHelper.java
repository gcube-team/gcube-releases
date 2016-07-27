package org.gcube.common.searchservice.searchlibrary.GarbageCollector;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.HeaderRef;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementGC;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementLifeSpanGC;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementWSEPR;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSFileHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * Helper class providing methods to navigate through a {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * chain and for retrieving properties that are interesting to the {@link org.gcube.common.searchservice.searchlibrary.GarbageCollector.GarbageCollect}
 * 
 * @author UoA
 */
public class GCHelper {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(GCHelper.class);
	
	/**
	 * Goes to the last part of the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} whose
	 * member is the part that is stored in the provided filename and populates the properties 
	 * 
	 * @param filename The filename that stores the part whose end must be reached
	 * @param props The properties of the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} that must be populated
	 * @return The filename of the last part
	 */
	public static String goToLast(String filename,GCProperties props){
		try{
			while(true){
				String next=null;
				if(!isXML(filename)){
					HeaderRef head=RSFileHelper.populateHeader(filename);
					next=head.getNext();
				}
				else{
					DocumentBuilderFactory datadbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder datadb = datadbf.newDocumentBuilder();
					Document dom=datadb.parse(new File(filename));
					NodeList nextnl = dom.getElementsByTagName(RSConstants.PartTag);
					for(int i = 0 ; i < nextnl.getLength();i++) {
						if(((Element)nextnl.item(i)).getAttribute(RSConstants.PropertyAttributeNameTag).equals(RSConstants.nextLink)){
							next=((Element)nextnl.item(i)).getFirstChild().getNodeValue();
							break;
						}
					}
				}
				if(next.equalsIgnoreCase("no")){
					return filename;
				}
				filename=next;
				props.addToChain(filename);
				File exam=new File(filename);
				props.setLastAccessed(exam.lastModified());
			}
		}catch(Exception e){
			log.error("Could not go to Last. Returning current "+filename);
//			log.error(e.toString());
			return filename;
		}
	}
	
	/**
	 * Goes to the head part of the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} whose
	 * member is the part that is stored in the provided filename and populates the properties 
	 * 
	 * @param filename The filename that stores the part whose head must be reached
	 * @param props The properties of the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} that must be populated
	 * @return The filename of the head part
	 */
	public static String goToHead(String filename,GCProperties props){
		try{
			while(true){
				String prev=null;
				if(!isXML(filename)){
					HeaderRef head=RSFileHelper.populateHeader(filename);
					prev=head.getPrev();
				}
				else{
					DocumentBuilderFactory datadbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder datadb = datadbf.newDocumentBuilder();
					Document dom=datadb.parse(new File(filename));
					NodeList nextnl = dom.getElementsByTagName(RSConstants.PartTag);
					for(int i = 0 ; i < nextnl.getLength();i++) {
						if(((Element)nextnl.item(i)).getAttribute(RSConstants.PropertyAttributeNameTag).equals(RSConstants.previousLink)){
							prev=((Element)nextnl.item(i)).getFirstChild().getNodeValue();
							break;
						}
					}
				}
				if(prev.equalsIgnoreCase("no")){
					return filename;
				}
				filename=prev;
				props.addToChain(filename);
				File exam=new File(filename);
				props.setLastAccessed(exam.lastModified());
			}
		}catch(Exception e){
			log.error("Could not go to Head. Returning current "+filename);
//			log.error(e.toString());
			return filename;
		}
	}
	
	/**
	 * Populates the properties of the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} whose
	 * head part is stored in the provided filename 
	 * 
	 * @param filename The filename storing the head part of the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * @param props The properties of the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 */
	public static void headPoperties(String filename,GCProperties props){
		try{
			File exam=new File(filename);
			props.setLastAccessed(exam.lastModified());
			DocumentBuilderFactory datadbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder datadb = datadbf.newDocumentBuilder();
			Document dom=datadb.parse(exam);
			try{
				NodeList res=XPathAPI.selectNodeList(dom,"/"+RSConstants.ResultSetTag+"/"+RSConstants.HeadTag+"/"+RSConstants.CustomPropertiesTag+"/"+PropertyElementGC.propertyType);
				if(res!=null){
					for(int i=0;i<res.getLength();i+=1){
						DocumentBuilderFactory bf = DocumentBuilderFactory.newInstance();
						DocumentBuilder db = bf.newDocumentBuilder();
						Document newdom = db.newDocument();
						Element rootEle=(Element)newdom.importNode(res.item(i).cloneNode(true),true);
						newdom.appendChild(rootEle);
						OutputFormat format = new OutputFormat(newdom);
						format.setIndenting(false);
						format.setOmitDocumentType(true);
						format.setOmitXMLDeclaration(true);
						StringWriter writer = new StringWriter();
						XMLSerializer serializer = new XMLSerializer(writer, format);
						serializer.serialize(newdom);
						PropertyElementGC tmp=new PropertyElementGC();
						tmp.RS_fromXML(writer.toString());
						props.addSSID(tmp.toXML());
					}
				}
			}catch(Exception e){
				log.debug("Could not find "+PropertyElementGC.propertyType+" property...Continuing",e);
			}
			try{
				NodeList res=XPathAPI.selectNodeList(dom,"/"+RSConstants.ResultSetTag+"/"+RSConstants.HeadTag+"/"+RSConstants.CustomPropertiesTag+"/"+PropertyElementLifeSpanGC.propertyType);
				if(res!=null){
					for(int i=0;i<res.getLength();i+=1){
						DocumentBuilderFactory bf = DocumentBuilderFactory.newInstance();
						DocumentBuilder db = bf.newDocumentBuilder();
						Document newdom = db.newDocument();
						Element rootEle=(Element)newdom.importNode(res.item(i).cloneNode(true),true);
						newdom.appendChild(rootEle);
						OutputFormat format = new OutputFormat(newdom);
						format.setIndenting(false);
						format.setOmitDocumentType(true);
						format.setOmitXMLDeclaration(true);
						StringWriter writer = new StringWriter();
						XMLSerializer serializer = new XMLSerializer(writer, format);
						serializer.serialize(newdom);
						PropertyElementLifeSpanGC tmp=new PropertyElementLifeSpanGC();
						tmp.RS_fromXML(writer.toString());
						props.addLifeSpan(tmp.toXML());
					}
				}
			}catch(Exception e){
				log.debug("Could not find "+PropertyElementLifeSpanGC.propertyType+" property...Continuing",e);
			}
			try{
				NodeList res=XPathAPI.selectNodeList(dom,"/"+RSConstants.ResultSetTag+"/"+RSConstants.HeadTag+"/"+RSConstants.CustomPropertiesTag+"/"+PropertyElementWSEPR.propertyType);
				if(res!=null){
					for(int i=0;i<res.getLength();i+=1){
						DocumentBuilderFactory bf = DocumentBuilderFactory.newInstance();
						DocumentBuilder db = bf.newDocumentBuilder();
						Document newdom = db.newDocument();
						Element rootEle=(Element)newdom.importNode(res.item(i).cloneNode(true),true);
						newdom.appendChild(rootEle);
						OutputFormat format = new OutputFormat(newdom);
						format.setIndenting(false);
						format.setOmitDocumentType(true);
						format.setOmitXMLDeclaration(true);
						StringWriter writer = new StringWriter();
						XMLSerializer serializer = new XMLSerializer(writer, format);
						serializer.serialize(newdom);
						PropertyElementWSEPR tmp=new PropertyElementWSEPR();
						tmp.RS_fromXML(writer.toString());
						props.addWSEPR(tmp.toXML());
					}
				}
			}catch(Exception e){
				log.debug("Could not find "+PropertyElementWSEPR.propertyType+" property...Continuing",e);
			}
		}catch(Exception e){
			log.error("Could not parse for head properties file "+filename,e);
		}
	}
	
	/**
	 * Populates the properties of the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} whose
	 * last part is stored in the provided filename 
	 * 
	 * @param filename The filename storing the last part of the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 * @param props The properties of the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
	 */
	public static void tailPoperties(String filename,GCProperties props){
		try{
			File exam=new File(filename);
			props.setLastAuthored(exam.lastModified());
			String next=null;
			if(isXML(filename)){
				DocumentBuilderFactory datadbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder datadb = datadbf.newDocumentBuilder();
				Document dom=datadb.parse(new File(filename));
				NodeList nextnl = dom.getElementsByTagName(RSConstants.PartTag);
				for(int i = 0 ; i < nextnl.getLength();i++) {
					if(((Element)nextnl.item(i)).getAttribute(RSConstants.PropertyAttributeNameTag).equals(RSConstants.nextLink)){
						next=((Element)nextnl.item(i)).getFirstChild().getNodeValue();
						break;
					}
				}
			}
			else{
				HeaderRef head=RSFileHelper.populateHeader(filename);
				next=head.getNext();
			}

			if(next.equalsIgnoreCase("no")){
				props.setComplete(true);
			}
			else{
				props.setComplete(true);
			}
		}catch(FileNotFoundException x){
			log.error("File "+filename+" not found. Probably an RS is being written.");			
		}catch(Exception e){
			log.error("Could not parse for tail properties file "+filename);
			log.trace("Exception: ",e);
		}
	}
	
	/**
	 * Checks whetehr the provifed header file is in xml format or not
	 * 
	 * @param filename The name of the file
	 * @return <code>true</code> if the file is in xml format, <code>false</code> otherwise
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	private static boolean isXML(String filename) throws Exception{
		FileReader fr=new FileReader(new File(filename));
		BufferedReader br=new BufferedReader(fr);
		String line=br.readLine();
		if(line.trim().length()>0){
			br.close();
			fr.close();
			if(line.trim().startsWith("<")) return true;
			else return false;
		}
		while(line!=null){
			line=br.readLine();
			if(line.trim().length()>0){
				br.close();
				fr.close();
				if(line.trim().startsWith("<")) return true;
				else return false;
			}
		}
		br.close();
		fr.close();
		return false;
	}
	
	/**
	 * Checks if a file has already been checked for deletion either it sel or as part of a travered chain
	 * and if it hasn't makes it us checked
	 * 
	 * @param filename The file to check
	 * @param algorithm The encription algorithm used to create a hash of the filename name
	 * @param checked The set of checked files
	 * @return <code>true</code> if the file has been checked, <code>false</code> otherwise
	 */
	public static boolean alreadyChecked(String filename,MessageDigest algorithm, Set<String> checked){
		if(filename.endsWith(RSConstants.cextention)){
			return false;
		}
		algorithm.reset();
		algorithm.update(filename.getBytes());
		String key=new String(algorithm.digest());
		if(checked.contains(key)){
			return true;
		}
		checked.add(key);
		return false;
	}
}
