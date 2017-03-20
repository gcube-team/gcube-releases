package org.gcube.common.searchservice.searchlibrary.resultset.elements;

/**
 * Property Element that is used to add WSRF {@link org.apache.axis.message.addressing.EndpointReferenceType}
 * serializations to the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} head
 * part
 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase
 * 
 * @author uoA
 */
public class PropertyElementWSEPR extends PropertyElementBase{
	/**
	 * The Type of the Property this Property element produces
	 */
	public static String propertyType="WS-EPR";
	/**
	 * The End Point Reference
	 */
	private String epr=null;
	
	/**
	 *Default contructor required by {@link PropertyElementBase} 
	 */
	public PropertyElementWSEPR(){}
	
	/**
	 * Initializes a new {@link PropertyElementWSEPR}
	 * 
	 * @param epr The {@link org.apache.axis.message.addressing.EndpointReferenceType} serialization
	 * @throws Exception An unrecoverable for the operation error occured 
	 */
	public PropertyElementWSEPR(String epr) throws Exception{
		this.epr=epr;
		setType(PropertyElementWSEPR.propertyType);
	}
	
	/** 
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase#toXML()
	 * @return The serialized property payload
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String toXML() throws Exception{
		return this.epr;
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase#fromXML(java.lang.String)
	 * @param xml The serialized string to populate the insance from 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void fromXML(String xml) throws Exception{
		this.epr=xml;
	}
}
