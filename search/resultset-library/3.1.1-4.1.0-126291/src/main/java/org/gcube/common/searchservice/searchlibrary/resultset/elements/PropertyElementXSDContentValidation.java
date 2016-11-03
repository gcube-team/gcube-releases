package org.gcube.common.searchservice.searchlibrary.resultset.elements;

/**
 * Property Element that is used to specify an xsd used to validate the records that are inserted in the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} 
 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase
 * 
 * @author uoA
 */
public class PropertyElementXSDContentValidation extends PropertyElementBase{
	/**
	 * The Type of the Property this Property element produces
	 */
	public static String propertyType="XSDContentValidation";
	/**
	 * content of the xsd
	 */
	private String xsdContent=null;
	
	/**
	 * Default contructor required by {@link PropertyElementBase}
	 */
	public PropertyElementXSDContentValidation(){}
	
	/**
	 * Initializes a new {@link PropertyElementXSDContentValidation}
	 * 
	 * @param xsdContent the xsd content
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public PropertyElementXSDContentValidation(String xsdContent) throws Exception{
		this.xsdContent=xsdContent;
		setType(PropertyElementXSDContentValidation.propertyType);
	}
	
	/**
	 * retieves the xsd content
	 * 
	 * @return te xsd content
	 */
	public String getContentXSD(){
		return this.xsdContent;
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase#toXML()
	 * @return The serialized property payload
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String toXML() throws Exception{
		return this.xsdContent;
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase#fromXML(java.lang.String)
	 * @param xml The serialized string to populate the insance from 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void fromXML(String xml) throws Exception{
		this.xsdContent=xml;
	}
}
