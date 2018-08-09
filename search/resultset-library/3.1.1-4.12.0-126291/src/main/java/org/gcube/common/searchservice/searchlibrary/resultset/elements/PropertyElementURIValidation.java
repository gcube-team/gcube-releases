package org.gcube.common.searchservice.searchlibrary.resultset.elements;

import java.net.URI;

/**
 * Property Element that is used to specify an xsd used to validate the records that are inserted in the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} 
 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase
 * 
 * @author uoA
 */
public class PropertyElementURIValidation extends PropertyElementBase{
	/**
	 * The Type of the Property this Property element produces
	 */
	public static String propertyType="URIValidation";
	/**
	 * URI to the xsd
	 */
	private URI xsdURI=null;
	
	/**
	 * Default contructor required by {@link PropertyElementBase}
	 */
	public PropertyElementURIValidation(){}
	
	/**
	 * Initializes a new {@link PropertyElementURIValidation}
	 * 
	 * @param xsdURI the xsd content
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public PropertyElementURIValidation(URI xsdURI) throws Exception{
		this.xsdURI=xsdURI;
		setType(PropertyElementURIValidation.propertyType);
	}
	
	/**
	 * retrieves the URI
	 * 
	 * @return the URI
	 */
	public URI getURIxsd(){
		return this.xsdURI;
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase#toXML()
	 * @return The serialized property payload
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String toXML() throws Exception{
		return this.xsdURI.toString();
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase#fromXML(java.lang.String)
	 * @param xml The serialized string to populate the insance from 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void fromXML(String xml) throws Exception{
		this.xsdURI=new URI(xml);
	}
}
