package org.gcube.common.searchservice.searchlibrary.resultset.elements;

import java.io.InputStream;
import java.util.Vector;

/**
 * This class acts as a generic placeholder for the records that can be inserted and extracted from the 
 * {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} payload parts
 * 
 * @author UoA
 */
public class ResultElementBLOBGeneric extends ResultElementBLOBBase{
	/**
	 * The name of the id attribute holding the identifier value
	 */
	public static final String RECORD_ID_NAME="DocID";
	/**
	 * The name of the collection attribute holding the collection value
	 */
	public static final String RECORD_COLLECTION_NAME="CollID";
	
	/**
	 * The payload of the record
	 */
	private String payload;
	/**
	 * The content of the blob
	 */
	private InputStream content;
	
	/**
	 * Default contructor nessecary for the framework
	 */
	public ResultElementBLOBGeneric(){}
	
	/**
	 * Creates a new {@link ResultElementBLOBGeneric}
	 * 
	 * @param id The value of the id. This cannot be null or an empty string
	 * @param collection The collection this id belongs to. This cannot be null or an empty string
	 * @param payload The payload of the record
	 * @param content the content of the blob
	 * @throws Exception The {@link ResultElementBLOBGeneric} could not be created
	 */
	public ResultElementBLOBGeneric(String id,String collection, String payload,InputStream content) throws Exception{
		Vector<RecordAttribute> at=new Vector<RecordAttribute>();
		at.add(new RecordAttribute(ResultElementBLOBGeneric.RECORD_ID_NAME,id));
		at.add(new RecordAttribute(ResultElementBLOBGeneric.RECORD_COLLECTION_NAME,collection));
		setRecordAttributes(at.toArray(new RecordAttribute[0]));
		if(payload==null || payload.trim().length()==0) this.payload=null;
		else this.payload=payload;
		this.content =content;
	}
	
	/**
	 * Sets the payload of the record
	 * 
	 * @param payload The payload to set
	 */
	private void setPayload(String payload){
		this.payload=payload;
	}

	/**
	 * Retrieves the payload
	 * 
	 * @return The payload
	 */
	public String getPayload(){
		return this.payload;
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBLOBBase#close()
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void close()throws Exception{
		try{
			this.content.close();
		}catch(Exception e){
			throw new Exception("Could not close the underlying stream",e);
		}
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBLOBBase#setContentOfBLOB(java.io.InputStream)
	 * 
	 * @param content sets the content of the blob
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void setContentOfBLOB(InputStream content) throws Exception{
		this.content=content;
	}

	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBLOBBase#getContentOfBLOB()
	 *
	 * @return the content of the blob
	 * @throws Exception An uinrecoverablee for the operation error occured
	 */
	public InputStream getContentOfBLOB() throws Exception{
		return this.content;
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBase#toXML()
	 * @return The serialized record payload
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public String toXML() throws Exception{
		return payload;
	}
	
	/**
	 * @see org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBase#fromXML(java.lang.String)
	 * @param xml The serialized string to populate the insance from 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void fromXML(String xml) throws Exception{
		try{
			setPayload(xml);
		}catch(Exception e){
			throw new Exception("provided xml string is not valid ResultSelement serialization",e);
		}
	}
}
