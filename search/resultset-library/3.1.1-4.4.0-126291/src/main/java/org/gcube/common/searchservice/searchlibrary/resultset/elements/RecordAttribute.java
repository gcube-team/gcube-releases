package org.gcube.common.searchservice.searchlibrary.resultset.elements;

import org.apache.log4j.Logger;

/**
 * Placeholder for attribute key values that can be placed in each result serialization
 * 
 * @author UoA
 */
public class RecordAttribute {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RecordAttribute.class);
	
	/**
	 * The name of the Attribute
	 */
	private String attrName=null;
	/**
	 * The value of the Attribute
	 */
	private String attrValue=null;
	
	/**
	 * Creates a new {@link RecordAttribute}
	 * 
	 * @param attrName The attribute name. This cannot be null or zero lengthed 
	 * @param attrValue The attribute value. This cannot be null or zero lengthed
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RecordAttribute(String attrName,String attrValue) throws Exception{
		if(attrName==null || attrValue==null || attrName.trim().length()==0 || attrValue.trim().length()==0){
			log.error("Atttribute name or value is null or whitespace. Throwing Exception");
			throw new Exception("Atttribute name or value is null or whitespace");
		}
		this.attrName=attrName;
		this.attrValue=attrValue;
	}
	
	/**
	 * Retrieves the attribute name
	 * 
	 * @return The attribute name
	 */
	public String getAttrName() {
		return attrName;
	}

	/**
	 * Sets the atribute name
	 * 
	 * @param attrName The attribute name. This cannot be null or zero lengthed
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void setAttrName(String attrName) throws Exception{
		if(attrName==null || attrName.trim().length()==0){
			log.error("Atttribute name is null or whitespace. Throwing Exception");
			throw new Exception("Atttribute name is null or whitespace");
		}
		this.attrName = attrName;
	}

	/**
	 * Retrieves the attribute value
	 * 
	 * @return The attribute value
	 */
	public String getAttrValue() {
		return attrValue;
	}

	/**
	 * Sets the atribute value
	 * 
	 * @param attrValue The attribute value. This cannot be null or zero lengthed
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void setAttrValue(String attrValue) throws Exception{
		if(attrValue==null || attrValue.trim().length()==0){
			log.error("Atttribute value is null or whitespace. Throwing Exception");
			throw new Exception("Atttribute value is null or whitespace");
		}
		this.attrValue = attrValue;
	}
}
