package org.gcube.common.searchservice.searchlibrary.resultset.elements;

/**
 * Property Element that is used to set a specific lifespan to the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase
 * 
 * @author uoA
 */
public class PropertyElementLifeSpanGC extends PropertyElementBase{
	/**
	 * The Type of the Property this Property element produces
	 */
	public static String propertyType="LifeSpan";
	/**
	 * The life span of the marked RS
	 */
	private long lifeSpan=0;
	
	/**
	 * Default contructor required by {@link PropertyElementBase}
	 */
	public PropertyElementLifeSpanGC(){}
	
	/**
	 * Initializes a new {@link PropertyElementLifeSpanGC}
	 * 
	 * @param lifeSpan The lifespan of this {@link ResultSet} in milliseconds 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public PropertyElementLifeSpanGC(long lifeSpan) throws Exception{
		this.lifeSpan=lifeSpan;
		setType(PropertyElementLifeSpanGC.propertyType);
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase#toXML()
	 * @return The serialized property payload
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String toXML() throws Exception{
		return Long.toString(this.lifeSpan);
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase#fromXML(java.lang.String)
	 * @param xml The serialized string to populate the insance from 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void fromXML(String xml) throws Exception{
		this.lifeSpan=Long.parseLong(xml);
	}
}
