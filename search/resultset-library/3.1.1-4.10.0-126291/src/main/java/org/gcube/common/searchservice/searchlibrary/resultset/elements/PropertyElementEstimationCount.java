package org.gcube.common.searchservice.searchlibrary.resultset.elements;

/**
 * Property Element that is used to set an estimation of the records in the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase
 * 
 * @author uoA
 */
public class PropertyElementEstimationCount extends PropertyElementBase{
	/**
	 * The Type of the Property this Property element produces
	 */
	public static String propertyType="EstimationCount";
	/**
	 * Specifying that there is no SSID provided
	 */
	public static int unspecified=Integer.MIN_VALUE;
	/**
	 * The min specified
	 */
	private int min=PropertyElementEstimationCount.unspecified;
	/**
	 * The max specified
	 */
	private int max=PropertyElementEstimationCount.unspecified;
	/**
	 * The estimation specified
	 */
	private int estimation=PropertyElementEstimationCount.unspecified;
	
	/**
	 * Default contructor required by {@link PropertyElementBase}
	 */
	public PropertyElementEstimationCount(){}
	
	
	/**
	 * Initializes a new {@link PropertyElementEstimationCount}
	 * 
	 * @param min The min estimation
	 * @param max The max estimation
	 * @param estimation The estimation
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public PropertyElementEstimationCount(int min, int max, int estimation) throws Exception{
		this.min=min;
		this.max=max;
		this.estimation=estimation;
		setType(PropertyElementEstimationCount.propertyType);
	}
	
	/**
	 * Retrieves the min estimation
	 * 
	 * @return the min estimation
	 */
	public int getMin(){
		return this.min;
	}
	
	/**
	 * Retrieves the max estimation
	 * 
	 * @return the max estimation
	 */
	public int getMax(){
		return this.max;
	}
	
	/**
	 * Retrieves the estimation
	 * 
	 * @return the estimation
	 */
	public int getEstimation(){
		return this.estimation;
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase#toXML()
	 * @return The serialized property payload
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String toXML() throws Exception{
		return "<min>"+this.min+"</min><max>"+this.max+"</max><estimation>"+this.estimation+"</estimation>";
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase#fromXML(java.lang.String)
	 * @param xml The serialized string to populate the insance from 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void fromXML(String xml) throws Exception{
		int minStart=xml.indexOf("<min>");
		int minStop=xml.indexOf("</min>");
		int maxStart=xml.indexOf("<max>");
		int maxStop=xml.indexOf("</max>");
		int estimationStart=xml.indexOf("<estimation>");
		int estimationStop=xml.indexOf("</estimation>");
		if(minStart<0 || minStop<0 || maxStart<0 || maxStop<0 || estimationStart<0 || estimationStop<0 || minStart>=minStop || maxStart>=maxStop || estimationStart>=estimationStop) throw new Exception("invalid serialization");
		String min=xml.substring(minStart+"<min>".length(),minStop);
		String max=xml.substring(maxStart+"<max>".length(),maxStop);
		String estimation=xml.substring(estimationStart+"<estimation>".length(),estimationStop);
		if(estimation==null || estimation.trim().length()<=0 || min== null || min.trim().length()<=0 || max==null || max.trim().length()<=0) throw new Exception("invalid serialization");
		this.min=Integer.parseInt(min);
		this.max=Integer.parseInt(max);
		this.estimation=Integer.parseInt(estimation);
	}
}
