 package org.gcube.common.searchservice.searchlibrary.resultset.elements;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Vector;

/**
 * This class acts as a generic placeholder for the records that can be inserted and extracted from the 
 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} payload parts treating the 
 * payload as a java bean object.
 * 
 * @author UoA
 */
public class ResultElementBean extends ResultElementGeneric{

	/**
	 * The name of the searialized bean
	 */
	public static String RECORD_BEAN_NAME="Bean";
	
	/**
	 * The bean to serialize
	 */
	private Object bean=null;
	
	/**
	 * Default contructor nessecary for the framework
	 */
	public ResultElementBean(){}
	
	/**
	 * Constructs a new instance of this class
	 * 
	 * @param id The id of the record
	 * @param collection Teh collection this record belongs to
	 * @param bean The bean to serialize
	 * @throws Exception An unrecoverable for the operaton error occured
	 */
	public ResultElementBean(String id,String collection,Object bean) throws Exception{
		Vector<RecordAttribute> at=new Vector<RecordAttribute>();
		at.add(new RecordAttribute(ResultElementGeneric.RECORD_ID_NAME,id));
		at.add(new RecordAttribute(ResultElementGeneric.RECORD_COLLECTION_NAME,collection));
		at.add(new RecordAttribute(ResultElementBean.RECORD_BEAN_NAME,bean.getClass().getName()));
		at.add(new RecordAttribute(ResultElementGeneric.RECORD_RANK_NAME,"1.0"));
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
			return this.getRecordAttributes(ResultElementBean.RECORD_BEAN_NAME)[0].getAttrValue();
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
		ByteArrayOutputStream os=new ByteArrayOutputStream();
		XMLEncoder encoder=new XMLEncoder(os);
		encoder.writeObject(this.bean);
		encoder.close();
		return os.toString().substring(os.toString().indexOf("<java version"));
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBase#fromXML(java.lang.String)
	 * @param xml The serialized string to populate the insance from 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void fromXML(String xml) throws Exception{
		try{
			InputStream is = new ByteArrayInputStream(xml.getBytes());
			XMLDecoder decoder = new XMLDecoder(is);
			setBean(decoder.readObject());
		}catch(Exception e){
			throw new Exception("provided xml string is not valid ResultSelement serialization",e);
		}
	}
}
