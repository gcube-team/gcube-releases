package org.gcube.common.searchservice.searchlibrary.resultset.elements;

/**
 * Property Element that is used to set a specific the form of a text {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase
 * 
 * @author uoA
 */
public class PropertyElementWellFormed  extends PropertyElementBase{
	/**
	 * The Type of the Property this Property element produces
	 */
	public static String propertyType="WellFormed";
	/**
	 * The RS is well formed
	 */
	public static String YES="YES";
	/**
	 * The RS is not well formed
	 */
	public static String NO="NO";
	/**
	 * Whether or not the RS is well formed
	 */
	private String wellformed=null;
	
	/**
	 * Default contructor required by {@link PropertyElementBase}
	 */
	public PropertyElementWellFormed(){}
	
	
	/**
	 * Initializes a new {@link PropertyElementWellFormed}
	 * 
	 * @param wellformed Whether or not this {@link ResultSet} is wellformed
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public PropertyElementWellFormed(String wellformed) throws Exception{
		this.wellformed=wellformed;
		setType(PropertyElementGC.propertyType);
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase#toXML()
	 * @return The serialized property payload
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String toXML() throws Exception{
		return this.wellformed;
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase#fromXML(java.lang.String)
	 * @param xml The serialized string to populate the insance from 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void fromXML(String xml) throws Exception{
		this.wellformed=xml;
	}
}
