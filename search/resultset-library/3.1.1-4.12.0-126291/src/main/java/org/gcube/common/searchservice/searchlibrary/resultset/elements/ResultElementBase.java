package org.gcube.common.searchservice.searchlibrary.resultset.elements;

import java.util.Vector;
import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants;

/**
 * This class is the base class that must be extended by anyone wishing to implement a custom
 * Record element class. Every class extending this one must define an accessible default constructor
 * with an empty argument list
 * 
 * @author UoA
 */
public abstract class ResultElementBase {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(ResultElementBase.class);
	
	/**
	 * The Attributes the element has
	 */
	private RecordAttribute []attrs=null;
	
	/**
	 * This method must return a valid xml serialization of the result record payload.
	 * 
	 * @return The xml serialization
	 * @throws Exception The extending element can specify the Exception that is thrown 
	 */
	public abstract String toXML() throws Exception;
	
	/**
	 * This method must be able to reconstruct the result elelement from a xml string as returned
	 * by {@link ResultElementBase#toXML()}  
	 * 
	 * @param xml The xml serialization
	 * @throws Exception The elelement creation could not be performed
	 */
	public abstract void fromXML(String xml) throws Exception;

	/**
	 * Retrieves the Attributs of the recors
	 * 
	 * @return The attributes
	 */
	public RecordAttribute[] getRecordAttributes(){
		return attrs;
	}
	
	/**
	 * Retrieveds the record Attributes with the given attribute name
	 * 
	 * @param attrName The name of the attribute 
	 * @return The attributes
	 */
	public RecordAttribute[] getRecordAttributes(String attrName){
		if(this.attrs==null) return new RecordAttribute[0];
		Vector<RecordAttribute> at=new Vector<RecordAttribute>();
		for(int i=0;i<attrs.length;i+=1){
			if(attrs[i].getAttrName().compareTo(attrName)==0) at.add(attrs[i]);
		}
		return at.toArray(new RecordAttribute[0]);
	}
	
	/**
	 * Sets the record Attributes
	 * 
	 * @param attrs The Attributes
	 */
	public void setRecordAttributes(RecordAttribute[] attrs){
		this.attrs=attrs;
	}
	
	/**
	 * This operation is used to retrieve the searialization of the provided 
	 * record element checking if it is a valid serialization. It fills the serialization
	 * with the available attributes
	 * 
	 * @return The record serialization if it is a valid one 
	 * @throws Exception The serialization is not a valid record serialization 
	 */
	public final String RS_toXML() throws Exception{
		String xml=toXML();
		StringBuilder validXML=new StringBuilder("<"+RSConstants.RecordTag);
		if(attrs!=null && attrs.length>0){
			for(int i=0;i<attrs.length;i+=1){
				validXML.append(" "+attrs[i].getAttrName()+"=\""+attrs[i].getAttrValue()+"\"");
			}
		}
		if(xml==null) validXML.append("/>");
		else{
			validXML.append(">");
			validXML.append(xml);
			validXML.append("</"+RSConstants.RecordTag+">");
		}
		String recXML=validXML.toString();
		if(!valid(recXML)){
			log.error("record is not valid. Throwing Exception");
			throw new Exception("record is not valid");
		}
		return recXML;
	}
	
	/**
	 * This operation is used to unmarshal a result element from a serialization string only if
	 * this is a valid result serialization as returned by {@link ResultElementBase#RS_toXML()}. 
	 * It also parses the availalbe atributes
	 * 
	 * @param xml The xml string to be unmarshaled
	 * @throws Exception The unmarshaling could not be performed
	 */
	public final void RS_fromXML(String xml) throws Exception{
		if(!valid(xml)){
			log.error("record is not valid. Throwing Exception");
			throw new Exception("record is not valid");
		}
		String payload=null;
		String attrsS=null;
		Vector<RecordAttribute> attrs=new Vector<RecordAttribute>();
		if(xml.endsWith("/>")){
			payload=null;
			attrsS=xml.substring(("<".length()+RSConstants.RecordTag.length()),(xml.length()-"/>".length()));
		}
		else{
			payload=xml.substring((xml.indexOf(">")+">".length()),xml.lastIndexOf("</"+RSConstants.RecordTag+">"));
			if ( ("<".length()+RSConstants.RecordTag.length()) <= (xml.indexOf(">")-">".length())){
				attrsS=xml.substring(("<".length()+RSConstants.RecordTag.length()),(xml.indexOf(">")-">".length()));
			}else{
				attrsS="";
			}
		}
		String []attrsSA=attrsS.split("\"");
		try{
			for(int i=0;i<attrsSA.length-1;i+=2){
				try{
					attrs.add(new RecordAttribute(attrsSA[i].trim().substring(0,attrsSA[i].trim().lastIndexOf("=")),attrsSA[i+1]));
				}catch(Exception e){}
			}
		}catch(Exception e){}
		setRecordAttributes(attrs.toArray(new RecordAttribute[0]));
		fromXML(payload);
	}
	
	/**
	 * This operations checkes if the provided xml string is a valid serialization of a record
	 * 
	 * @param xml The serialization that must be checked
	 * @return <code>true</code> if it is a valid serialization, <code>false</code> otherwise
	 */
	public static final boolean isValid(String xml){
		return valid(xml);
	}
	
	/**
	 * Checks if the serialized record is valid
	 * 
	 * @param xml The serialized record to validate
	 * @return <code>true</code> if the recrd is valid, <code>false</code> otherwise
	 */
	private static boolean valid(String xml){
		try{
			if(xml.trim().startsWith("<"+RSConstants.RecordTag) && (xml.trim().endsWith("</"+RSConstants.RecordTag+">") || xml.trim().endsWith("/>"))) return true;
			return false;
		}catch(Exception e){
			return false;
		}
	}
}
