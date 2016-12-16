//package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.utils;
//
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Vector;
//
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.DataElementImpl;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
//import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
//import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
//import org.gcube.common.searchservice.searchlibrary.resultset.elements.RecordAttribute;
//import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBLOBBase;
//
///**
// * @author Dimitris Katris, NKUA
// *
// * <p>
// * Encapsulates a {@link DataElement} into a {@link ResultElementBLOBBase}.
// * </p>
// */
//public class ResultSetDataElement extends ResultElementBLOBBase{
//	/**
//	 * The name of the id attribute holding the identifier value
//	 */
//	private static final String OID="oid";
//	
//	private static final String MIMETYPE="mimetype";
//	
//	private static final String FORMATPREFIX="fmt_";
//	
//	private static final String ATTRIBUTEPREFIX="att_";
//	
//	/**
//	 * The payload of the record
//	 */
//	private String payload;
//	/**
//	 * The content of the blob
//	 */
//	private InputStream content;
//	
//	/**
//	 * Default constructor necessary for the framework
//	 */
//	public ResultSetDataElement(){}
//	
//	/**
//	 * Creates a new {@link ResultElementBLOBGeneric}
//	 * 
//	 * @param dataElement The data element.
//	 * @throws Exception The {@link ResultElementBLOBGeneric} could not be created
//	 */
//	public ResultSetDataElement(DataElement dataElement) throws Exception{
//		Vector<RecordAttribute> at=new Vector<RecordAttribute>();
//		at.add(new RecordAttribute(ResultSetDataElement.OID,dataElement.getId()));
//		at.add(new RecordAttribute(ResultSetDataElement.MIMETYPE,dataElement.getContentType().getMimeType()));
//		if(dataElement.getContentType().getContentTypeParameters()!=null){
//			for(Parameter param: dataElement.getContentType().getContentTypeParameters()){
//				at.add(new RecordAttribute(ResultSetDataElement.FORMATPREFIX+param.getName(), param.getValue()));
//			}
//		}
//		for (String attrName : dataElement.getAllAttributes().keySet()){
//			at.add(new RecordAttribute(ResultSetDataElement.ATTRIBUTEPREFIX+attrName, dataElement.getAttributeValue(attrName)));
//		}
//		
//		setRecordAttributes(at.toArray(new RecordAttribute[at.size()]));
//		this.payload=null;
//		this.content = dataElement.getContent();
//	}
//	
//	/**
//	 * Sets the payload of the record
//	 * 
//	 * @param payload The payload to set
//	 */
//	private void setPayload(String payload){
//		this.payload=payload;
//	}
//
//	/**
//	 * Retrieves the payload
//	 * 
//	 * @return The payload
//	 */
//	public String getPayload(){
//		return this.payload;
//	}
//	
//	/**
//	 * @return The id of the data element.
//	 */
//	public String getOID(){
//		return getRecordAttributes(ResultSetDataElement.OID)[0].getAttrValue(); 
//	}
//	
//	/**
//	 * @return The content type of the data element.
//	 */
//	public ContentType getContentType(){
//		ContentType format = new ContentType();
//		format.setMimeType(getRecordAttributes(ResultSetDataElement.MIMETYPE)[0].getAttrValue());
//		ArrayList<Parameter> params = new ArrayList<Parameter>();
//		RecordAttribute[] attrs = getRecordAttributes();
//		for(RecordAttribute attr: attrs){
//			if(attr.getAttrName().startsWith(ResultSetDataElement.FORMATPREFIX)){
//				Parameter param = new Parameter();
//				param.setName(attr.getAttrName().substring(ResultSetDataElement.FORMATPREFIX.length()));
//				param.setValue(attr.getAttrValue());
//				params.add(param);
//			}
//		}
//		format.setContentTypeParameters(params);
//		return format; 
//	}
//	
//	/**
//	 * @return The data element.
//	 */
//	public DataElement getDataElement(){
//		DataElementImpl element = DataElementImpl.getSourceDataElement();
//		element.setId(getOID());
//		element.setContentType(getContentType());
//		RecordAttribute[] attrs = getRecordAttributes();
//		for(RecordAttribute attr: attrs){
//			if(attr.getAttrName().startsWith(ResultSetDataElement.ATTRIBUTEPREFIX)){
//				element.setAttribute(attr.getAttrName().substring(ResultSetDataElement.ATTRIBUTEPREFIX.length()), attr.getAttrValue());
//			}
//		}
//		element.setContent(this.content);
//		return element;
//	}
//	
//	/**
//	 * @see org.gcube.searchservice.searchlibrary.resultset.elements.ResultElementBLOBBase#close()
//	 * @throws Exception An unrecoverable for the operation error occurred
//	 */
//	public void close()throws Exception{
//		try{
//			this.content.close();
//		}catch(Exception e){
//			throw new Exception("Could not close the underlying stream",e);
//		}
//	}
//	
//	/**
//	 * @see org.gcube.searchservice.searchlibrary.resultset.elements.ResultElementBLOBBase#setContentOfBLOB(java.io.InputStream)
//	 * 
//	 * @param content sets the content of the blob
//	 * @throws Exception An unrecoverable for the operation error occurred
//	 */
//	public void setContentOfBLOB(InputStream content) throws Exception{
//		this.content=content;
//	}
//
//	/**
//	 * @see org.gcube.searchservice.searchlibrary.resultset.elements.ResultElementBLOBBase#getContentOfBLOB()
//	 *
//	 * @return the content of the blob
//	 * @throws Exception An unrecoverable for the operation error occurred
//	 */
//	public InputStream getContentOfBLOB() throws Exception{
//		return this.content;
//	}
//	
//	/**
//	 * @see org.gcube.searchservice.searchlibrary.resultset.elements.ResultElementBase#toXML()
//	 * @return The serialized record payload
//	 * @throws Exception An unrecoverable for the operation error occurred
//	 */
//	public String toXML() throws Exception{
//		return payload;
//	}
//	
//	/**
//	 * @see org.gcube.searchservice.searchlibrary.resultset.elements.ResultElementBase#fromXML(java.lang.String)
//	 * @param xml The serialized string to populate the instance from 
//	 * @throws Exception An unrecoverable for the operation error occurred
//	 */
//	public void fromXML(String xml) throws Exception{
//		try{
//			setPayload(xml);
//		}catch(Exception e){
//			throw new Exception("provided xml string is not valid ResultSelement serialization",e);
//		}
//	}
//}
