package org.gcube.common.searchservice.searchlibrary.resultset.elements;

/**
 * Property Element that is used to set a specific type to the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase
 * 
 * @author UoA
 */
public class PropertyElementType  extends PropertyElementBase{
	/**
	 * The Type of the Property this Property element produces
	 */
	public static String propertyType="Type";
	/**
	 * The RS is of this type
	 */
	public static String XML="XML";
	/**
	 * The RS is of this type
	 */
	public static String TEXT="TEXT";
	/**
	 * The RS is of this type
	 */
	public static String BLOB="BLOB";
	/**
	 * The specified type
	 */
	private String type=null;
	
	/**
	 * Default contructor required by {@link PropertyElementBase}
	 */
	public PropertyElementType(){}
	
	
	/**
	 * Initializes a new {@link PropertyElementType}
	 * 
	 * @param type The tyoe of this {@link ResultSet}
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public PropertyElementType(String type) throws Exception{
		this.type=type;
		setType(PropertyElementGC.propertyType);
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase#toXML()
	 * @return The serialized property payload
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String toXML() throws Exception{
		return this.type;
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase#fromXML(java.lang.String)
	 * @param xml The serialized string to populate the insance from 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void fromXML(String xml) throws Exception{
		this.type=xml;
	}
}
