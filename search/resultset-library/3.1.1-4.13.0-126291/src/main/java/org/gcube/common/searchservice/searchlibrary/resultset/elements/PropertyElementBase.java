package org.gcube.common.searchservice.searchlibrary.resultset.elements;

import org.apache.log4j.Logger;

/**
 * This class is the base class that must be extended by anyone wishing to implement a custom
 * Property element class. Every class extending this one must define an accessible default constructor
 * with an empty argument list
 * 
 * @author UoA
 */
public abstract class PropertyElementBase {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(PropertyElementBase.class);

	/**
	 * The type of the property
	 */
	String type=null;
	
	/**
	 * Retrieves the Type of the current property 
	 * 
	 * @return the type of the current property
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public final String getType() throws Exception{
		if(type==null || type.trim().length()==0){
			log.error("the specified type "+type+" is not valid. Throwing Exception");
			throw new Exception("the specified type "+type+" is not valid");
		}
		return type;
	}
	
	/**
	 * Sets the Type of the current property element 
	 * 
	 * @param type The type this property element has
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public final void setType(String type) throws Exception{
		if(type==null || type.trim().length()==0){
			log.error("the specified type "+type+" is not valid. Throwing Exception");
			throw new Exception("the specified type "+type+" is not valid");
		}
		this.type=type;
	}

	/**
	 * Retrieves the Type of the provided property serialization 
	 * 
	 * @param xml the serialixed property
	 * @return the type of the provided  property
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public static final String getType(String xml) throws Exception{
		int start1T=xml.indexOf("<");
		int end1T=xml.indexOf(">");
		if(start1T <0 || end1T<0){
			log.error("the retrieved xml "+xml+" is not valid. Throwing Exception");
			throw new Exception("the retrieved xml "+xml+" is not valid.");
		}
		return xml.substring(start1T+"<".length(),end1T);
	}
	
	/**
	 * Creates a serialization of the current property element that can be added to the {@link ResultSet}
	 * head part.
	 * 
	 * @return The valid serialization of the property element 
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public final String RS_toXML() throws Exception{
		if(getType()==null || getType().trim().length()==0){
			log.error("the specified type "+getType()+" is not valid. Throwing Exception");
			throw new Exception("the specified type "+getType()+" is not valid");
		}
		if(toXML()==null || toXML().trim().length()==0){
			log.error("the specified content "+toXML()+" is not valid. Throwing Exception");
			throw new Exception("the specified type "+toXML()+" is not valid");
		}
		return "<"+getType()+">"+toXML()+"</"+getType()+">";
	}

	/**
	 * Populates the current property element with provided property serialization 
	 * 
	 * @param xml The serialized property
	 * @throws Exception An unrecoverable for the operation error has occured 
	 */
	public final void RS_fromXML(String xml) throws Exception{
		int start1T=xml.indexOf("<");
		int end1T=xml.indexOf(">");
		int start2T=xml.lastIndexOf("<");
		int end2T=xml.lastIndexOf(">");
		if(start1T <0 || end1T<0 || start2T <0 || end2T<0){
			log.error("the retrieved xml "+xml+" is not valid. Throwing Exception");
			throw new Exception("the retrieved xml "+xml+" is not valid.");
		}
		setType(xml.substring(start1T+"<".length(),end1T));
		fromXML(xml.substring(end1T+">".length(),start2T));
	}
	
	/**
	 * Method to be implemented that will handle the population of the custom Property element. It is 
	 * called by the {@link PropertyElementBase#RS_fromXML(String)} after it has striped the xml
	 * serialization of the type property
	 * @see PropertyElementBase#toXML()
	 * 
	 * @param xml The striped xml serialization the custom property element must handle
	 * @throws Exception The extending element can specify the Exception that is thrown 
	 */
	public abstract void fromXML(String xml) throws Exception;
	
	/**
	 * Method to be implemented that will handle the serialization of the custom Property element. It is 
	 * called by the {@link PropertyElementBase#RS_toXML()} which then enriches the returned serialization
	 * to produce a valid property xml string
	 * @see PropertyElementBase#fromXML()
	 * 
	 * @return an xml serialization of the proeprty content
	 * @throws Exception The extending element can specify the Exception that is thrown 
	 */
	public abstract String toXML() throws Exception;
}
