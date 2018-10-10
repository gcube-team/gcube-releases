package org.gcube.common.searchservice.searchlibrary.resultset.elements;

/**
 * Property Element that is used to set a specific ssid to the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase
 * 
 * @author uoA
 */
public class PropertyElementGC extends PropertyElementBase{
	/**
	 * The Type of the Property this Property element produces
	 */
	public static String propertyType="GC";
	/**
	 * Specifying that there is no SSID provided
	 */
	public static String unspecified="UNSPECIFIED_SSID";
	/**
	 * The SSID specified
	 */
	private String ssid=null;
	
	/**
	 * Default contructor required by {@link PropertyElementBase}
	 */
	public PropertyElementGC(){}
	
	
	/**
	 * Initializes a new {@link PropertyElementLifeSpanGC}
	 * 
	 * @param ssid The ssid of this {@link ResultSet}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public PropertyElementGC(String ssid) throws Exception{
		this.ssid=ssid;
		setType(PropertyElementGC.propertyType);
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase#toXML()
	 * @return The serialized property payload
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String toXML() throws Exception{
		return this.ssid;
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase#fromXML(java.lang.String)
	 * @param xml The serialized string to populate the insance from 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void fromXML(String xml) throws Exception{
		this.ssid=xml;
	}
}
