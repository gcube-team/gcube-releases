package org.gcube.common.searchservice.searchlibrary.resultset.elements;

import java.io.StringReader;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * This class acts as a generic placeholder for the records that can be inserted and extracted from the 
 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} payload parts
 * 
 * @author UoA
 */
public class ResultElementGeneric extends ResultElementBase{
	/**
	 * The Logger this class uses 
	 */
	private static Logger log = Logger.getLogger(ResultElementGeneric.class);

	/**
	 * The name of the id attribute holding the identifier value
	 */
	public static final String RECORD_ID_NAME="DocID";
	/**
	 * The name of the collection attribute holding the collection value
	 */
	public static final String RECORD_COLLECTION_NAME="CollID";
	/**
	 * The name of the rank attribute holding the record ranking
	 */
	public static final String RECORD_RANK_NAME="RankID";
	
	/**
	 * The payload of the record
	 */
	private String payload;
	
	/**
	 * Default contructor nessecary for the framework
	 */
	public ResultElementGeneric(){}
	
	/**
	 * Creates a new {@link ResultElementGeneric}
	 * 
	 * @param id The value of the id. This cannot be null or an empty string
	 * @param collection The collection this id belongs to. This cannot be null or an empty string
	 * @param rank The rank this id got. This cannot be null or an empty string
	 * @param payload The payload of the record
	 * @throws Exception The {@link ResultElementGeneric} could not be created
	 */
	public ResultElementGeneric(String id,String collection, String rank,String payload) throws Exception{
		Vector<RecordAttribute> at=new Vector<RecordAttribute>();
		at.add(new RecordAttribute(ResultElementGeneric.RECORD_ID_NAME,id));
		at.add(new RecordAttribute(ResultElementGeneric.RECORD_COLLECTION_NAME,collection));
		at.add(new RecordAttribute(ResultElementGeneric.RECORD_RANK_NAME,rank));
		setRecordAttributes(at.toArray(new RecordAttribute[0]));
		if(payload==null || payload.trim().length()==0) this.payload=null;
		else this.payload=payload;
	}
	
	/**
	 * Creates a new {@link ResultElementGeneric}. The rank attribute is set to 1.0
	 * 
	 * @param id The value of the id. This cannot be null or an empty string
	 * @param collection The collection this id belongs to. This cannot be null or an empty string
	 * @param payload The payload of the record
	 * @throws Exception The {@link ResultElementGeneric} could not be created
	 */
	public ResultElementGeneric(String id,String collection,String payload) throws Exception{
		Vector<RecordAttribute> at=new Vector<RecordAttribute>();
		at.add(new RecordAttribute(ResultElementGeneric.RECORD_ID_NAME,id));
		at.add(new RecordAttribute(ResultElementGeneric.RECORD_COLLECTION_NAME,collection));
		at.add(new RecordAttribute(ResultElementGeneric.RECORD_RANK_NAME,"1.0"));
		setRecordAttributes(at.toArray(new RecordAttribute[0]));
		if(payload==null || payload.trim().length()==0) this.payload=null;
		else this.payload=payload;
	}

	/**
	 * Sets the payload of the record
	 * 
	 * @param payload The payload to set
	 */
	private void setPayload(String payload){
		this.payload=payload;
	}

	/**
	 * Retrieves the payload
	 * 
	 * @return The payload
	 */
	public String getPayload(){
		return this.payload;
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBase#toXML()
	 * @return The serialized record payload
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String toXML() throws Exception{
		return payload;
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBase#fromXML(java.lang.String)
	 * @param xml The serialized string to populate the insance from 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void fromXML(String xml) throws Exception{
		try{
			setPayload(xml);
		}catch(Exception e){
			throw new Exception("provided xml string is not valid ResultSelement serialization",e);
		}
	}
	
	/**
	 * This method receives a {@link ResultElementGeneric} and an expression in the form of
	 *<code>element/element/element</code> and traverses the payload expecting to find at every
	 * level it moves a child element with the name as provided, in the respective level, in the
	 * <code>which</code> parameter. It then retrieves and returns the content of the first child element
	 * with the name of the last element in the <code>which</code> argument. If the which argument 
	 * is one of {@link ResultElementGeneric#RECORD_COLLECTION_NAME}, {@link ResultElementGeneric#RECORD_ID_NAME},
	 * {@link ResultElementGeneric#RECORD_RANK_NAME}, the first value of the attribute whith the respective type is retrieved
	 * If the expression beggins with <code>//</code> the first element in the entire record document that is
	 * named as the provided expression will be returned.
	 * 
	 * @param element The element to scan through
	 * @param which a path to the element whose contents must be retrieved
	 * @return The extracted value
	 */
	public static String extractValue(ResultElementGeneric element,String which){
		try{
			if(which.compareTo(ResultElementGeneric.RECORD_ID_NAME)==0){
				return element.getRecordAttributes(ResultElementGeneric.RECORD_ID_NAME)[0].getAttrValue();
			}
			else if(which.compareTo(ResultElementGeneric.RECORD_COLLECTION_NAME)==0){
				return element.getRecordAttributes(ResultElementGeneric.RECORD_COLLECTION_NAME)[0].getAttrValue();
			}
			else if(which.compareTo(ResultElementGeneric.RECORD_RANK_NAME)==0){
				return element.getRecordAttributes(ResultElementGeneric.RECORD_RANK_NAME)[0].getAttrValue();
			}
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			if(which.startsWith("//")){
				try{
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document document = builder.parse(new InputSource(new StringReader(element.RS_toXML())));
					Element docElement=document.getDocumentElement();
					NodeList nl = XPathAPI.selectNodeList(docElement,which);
//					NodeList nl = docElement.getElementsByTagName(which.substring("//".length()));
					if (nl.getLength()==0){
						return "";
					}
					return ((Element)nl.item(0)).getFirstChild().getNodeValue();
				} catch (Exception e) {
					log.error("Could not perform extraction. Throwing Excpetion",e);
					throw new Exception("Could not perform extraction");
				}
			}
			else if(which.indexOf("/")<0){
				try{
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document document = builder.parse(new InputSource(new StringReader(element.RS_toXML())));
					Element docElement=document.getDocumentElement();
					//NodeList nl = docElement.getElementsByTagName(which);
					NodeList nl = docElement.getElementsByTagNameNS("*",which);
					if (nl.getLength()==0){
						return "";
					}
					return ((Element)nl.item(0)).getFirstChild().getNodeValue();
				} catch (Exception e) {
					log.error("Could not perform extraction. Throwing Excpetion",e);
					throw new Exception("Could not perform extraction");
				}
			}
			else{
				try {
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document document = builder.parse(new InputSource(new StringReader(element.getPayload())));
					String []keyParts=which.split("/");
					int i=0;
					Element docElement=document.getDocumentElement();
					while(true){
						NodeList nl = docElement.getElementsByTagName(keyParts[i]);
						if (nl.getLength()==0){
							return "";
						}
						i+=1;
						if(i==keyParts.length){
							return nl.item(0).getFirstChild().getNodeValue();
						}
						else docElement=(Element)nl.item(0);
					}
				} catch (Exception e) {
					log.error("Could not perform extraction. Throwing Excpetion",e);
					throw new Exception("Could not perform extraction");
				}
			}
		}catch(Exception e){
			log.error("Could not perform extraction. Returning null",e);
			return null;
		}
	}

	/** Merges the two provided {@link ResultElementGeneric} into a new one which has as the id the id of element1,
	 * and as payload the concatenation of the two elements payload 
	 * 
	 * @param element1 One of the {@link ResultElementGeneric} that must be merged 
	 * @param element2 One of the {@link ResultElementGeneric} that must be merged
	 * @return The newly created {@link ResultElementGeneric}
	 * @throws Exception The merge operation could not be performed
	 */
	public static ResultElementGeneric merge(ResultElementGeneric element1,ResultElementGeneric element2) throws Exception{
		ResultElementGeneric element=new ResultElementGeneric();
		RecordAttribute []attrs=new RecordAttribute[element1.getRecordAttributes().length];
		for(int i=0;i<element1.getRecordAttributes().length;i+=1) attrs[i]=new RecordAttribute(element1.getRecordAttributes()[i].getAttrName(),element1.getRecordAttributes()[i].getAttrValue());
		element.setRecordAttributes(attrs);
		if(element1.getPayload()==null && element2.getPayload()==null) element.setPayload(null);
		else if(element1.getPayload()==null && element2.getPayload()!=null) element.setPayload(new String(element2.getPayload()));
		else if(element1.getPayload()!=null && element2.getPayload()==null) element.setPayload(new String(element1.getPayload()));
		else element.setPayload(new String(element1.getPayload()+element2.getPayload()));
		return element;
	}
}
