 package org.gcube.common.searchservice.searchlibrary.resultset.elements;

import java.util.Vector;

import com.thoughtworks.xstream.XStream;

/**
 * This class acts as a generic placeholder for the records that can be inserted and extracted from the 
 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} payload parts treating the 
 * payload as a java bean object.
 * 
 * @author UoA
 */
public class ResultElementXBean extends ResultElementGeneric{

	/**
	 * The name of the searialized bean
	 */
	public static String RECORD_BEAN_NAME="Bean";
	
	private static XStream xstream = new XStream();
	/**
	 * The bean to serialize
	 */
	private Object bean=null;
	
	/**
	 * Default contructor nessecary for the framework
	 */
	public ResultElementXBean(){}
	
	/**
	 * Constructs a new instance of this class
	 * 
	 * @param id The id of the record
	 * @param collection Teh collection this record belongs to
	 * @param bean The bean to serialize
	 * @throws Exception An unrecoverable for the operaton error occured
	 */
	public ResultElementXBean(String id,String collection,Object bean) throws Exception{
		Vector<RecordAttribute> at=new Vector<RecordAttribute>();
		at.add(new RecordAttribute(ResultElementGeneric.RECORD_ID_NAME,id));
		at.add(new RecordAttribute(ResultElementBean.RECORD_BEAN_NAME,bean.getClass().getName()));
		setRecordAttributes(at.toArray(new RecordAttribute[0]));
		this.bean=bean;
	}
	
	/**
	 * Sets the bean to serialize
	 * 
	 * @param bean The bean
	 */
	public void setBean(Object bean){
		this.bean=bean;
	}

	/**
	 * Retrieves the payload
	 * 
	 * @return The payload
	 */
	public Object getBean(){
		return this.bean;
	}
	
	/**
	 * Retrieves the name of the bean class
	 * 
	 * @return The class name
	 * @throws Exception An unrecoverable for the operaton error occured
	 */
	public String getBeanClassName() throws Exception{
		try{
			return this.getRecordAttributes(ResultElementXBean.RECORD_BEAN_NAME)[0].getAttrValue();
		}
		catch(Exception e){
			throw new Exception("No bean name found.");
		}
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBase#toXML()
	 * @return The serialized record payload
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String toXML() throws Exception{
		return xstream.toXML(bean);
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBase#fromXML(java.lang.String)
	 * @param xml The serialized string to populate the insance from 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void fromXML(String xml) throws Exception{
		setBean(xstream.fromXML(xml));
	}
}
