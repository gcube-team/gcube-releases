//package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.handlers;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.net.URISyntaxException;
//import java.util.ArrayList;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.DocumentWriter;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeAlternative;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeDocument;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeElement;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeElementProperty;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubePart;
//import org.gcube.contentmanagement.storagelayer.storagemanagementservice.stubs.protocol.SMSURLConnection;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.CompoundDataElementImpl;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.DataElementImpl;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandlerDefinitions;
//import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
//import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
//import org.gcube.datatransformation.datatransformationlibrary.tmpfilemanagement.TempFileManager;
//
///**
// * @author Dimitris Katris, NKUA
// *
// * <p>
// * Utility classes for contacting cms.
// * </p>
// */
//public class CMSUtils {
//
//	private static Logger log = LoggerFactory.getLogger(CMSUtils.class);
//
//	protected final static int chunkSize = 2048*1024;
//
//	protected static final String contenttypeParamsPropertyType = "dts:contenttypeparameters";
//	protected static final String customParamsPropertyType = "dts:customparameters";
//
//	public static ContentType getContentTypeOfObject(GCubeElement gdoc) throws Exception{
//
//		ContentType contentFormat = new ContentType();
//
//		//Getting the mimetype
//		if(gdoc.mimeType() == null){
//			throw new Exception("Mimetype of the object "+gdoc.id()+" is not set");
//		}
//		contentFormat.setMimeType(gdoc.mimeType());
//
//		//TODO: What is the key in the map?
//		Map<String, GCubeElementProperty> props = gdoc.properties();
//
//		ArrayList<Parameter> contentTypeParameters = new ArrayList<Parameter>();
//		for(GCubeElementProperty prop: props.values()){
//			log.debug("Property - Name: "+prop.key()+" - Value: "+prop.value()+" - Type: "+prop.type());
//			if(prop.type().equals(contenttypeParamsPropertyType)){
//				Parameter formatParameter = new Parameter();
//				formatParameter.setName(prop.key());
//				formatParameter.setValue(prop.value());
//				contentTypeParameters.add(formatParameter);
//			}
//		}
//
//		//Setting the format parameters...
//		contentFormat.setContentTypeParameters(contentTypeParameters);
//		return contentFormat;
//	}
//
//	private static String tmpSubDir;
//	static {
//		try {
//			tmpSubDir = TempFileManager.genarateTempSubDir();
//		} catch (Exception e) {
//		}
//	}
//
///*	public static DataElement getDataElementFromCM(GCubeDocument gdoc) throws Exception{
//		ContentType contentType = getContentTypeOfObject(gdoc);		
//		DataElementImpl dataElement = DataElementImpl.getSourceDataElement();
//		dataElement.setContent(getContnetStream(gdoc));
//		dataElement.setId(gdoc.getId());
//		dataElement.setContentType(contentType);
//		dataElement.setAttribute(DataHandlerDefinitions.ATTR_CONTENT_OID, gdoc.getURI().toString());
//		return dataElement;
//	}
//*/
//	public static DataElement getDataElementFromCM(GCubeElement gdoc, GCUBEScope scope) throws Exception{
//		log.debug("Getting the OID "+gdoc.id()+" completed successfully.");
//		ContentType contentType = getContentTypeOfObject(gdoc);		
//		DataElementImpl dataElement = DataElementImpl.getSourceDataElement();
//		dataElement.setContent(getContnetStream(gdoc, scope));
//		dataElement.setId(gdoc.id());
//		dataElement.setContentType(contentType);
//		dataElement.setAttribute(DataHandlerDefinitions.ATTR_CONTENT_OID, gdoc.uri().toString());
//		dataElement.setAttribute(DataHandlerDefinitions.ATTR_DOCUMENT_NAME, gdoc.name());
//		return dataElement;
//	}
//
//	public static DataElement getDataElementFromAlternativeCM(GCubeAlternative gdoc, GCUBEScope scope) throws Exception{
//		log.debug("Getting the OID "+gdoc.id()+" completed successfully.");
//		ContentType contentType = getContentTypeOfObject(gdoc);		
//		DataElementImpl dataElement = DataElementImpl.getSourceDataElement();
//		dataElement.setContent(getContnetStream(gdoc, scope));
//		dataElement.setId(gdoc.id());
//		dataElement.setContentType(contentType);
//		dataElement.setAttribute(DataHandlerDefinitions.ATTR_CONTENT_OID, gdoc.uri().toString());
//		dataElement.setAttribute(DataHandlerDefinitions.ATTR_DOCUMENT_NAME, gdoc.name());
//		return dataElement;
//	}
//
//	public static String getObjectsProperty(GCubeDocument gdoc, String PropertyName){
//		Map<String, GCubeElementProperty> props = gdoc.properties();
//		return props.get(PropertyName).value();
//	}
//
//	public static DataElement getDataElementWithAlternativeRepresentationsFromCM(
//			GCubeDocument gdoc, GCUBEScope scope) throws Exception {
//
//		if(gdoc.alternatives().size() > 0){
//			CompoundDataElementImpl compoundDataElement = new CompoundDataElementImpl();
//			compoundDataElement.setId(gdoc.id());
//			ContentType contentType = new ContentType();
//			contentType.setMimeType("multipart/alternative");
//			compoundDataElement.setContentType(contentType);
//			compoundDataElement.addPart(getDataElementFromCM(gdoc, scope));
//			for(GCubeAlternative alternativeRepresentation: gdoc.alternatives()){
//				compoundDataElement.addPart(getDataElementFromAlternativeCM(alternativeRepresentation, scope));
//			}
//			return compoundDataElement;
//		}else{
//			return getDataElementFromCM(gdoc, scope);
//		}
//	}
//
//	public static DataElement getCompoundDataElementFromCM(
//			GCubeDocument gdoc, GCUBEScope scope) throws Exception {
//
//		if(gdoc.parts().size() > 1){
//			CompoundDataElementImpl compoundDataElement = new CompoundDataElementImpl();
//			compoundDataElement.setId(gdoc.id());
//			ContentType contentType = new ContentType();
//			contentType.setMimeType("multipart/mixed");//For alternative representations multipart/alternative
//			compoundDataElement.setContentType(contentType);
//			compoundDataElement.addPart(getDataElementFromCM(gdoc, scope));
//			for(GCubePart part: gdoc.parts()){
//				log.debug("Part found with id: "+part.id());
//				compoundDataElement.addPart(getDataElementFromCM(part, scope));
//			}
//			return compoundDataElement;
//		}else{
//			return getDataElementFromCM(gdoc, scope);
//		}
//	}
//
//	public static String storeDataElementToCM(DocumentWriter cmWriter,
//			DataElement object) throws Exception {
//		GCubeDocument gdoc = new GCubeDocument();
//		gdoc.setName(object.getAttributeValue(DataHandlerDefinitions.ATTR_DOCUMENT_NAME));
//		gdoc.setBytestream(object.getContent());
//		gdoc.setMimeType(object.getContentType().getMimeType());
//
//		if(object.getContentType().getContentTypeParameters()!=null && object.getContentType().getContentTypeParameters().size()>0){
//			for(Parameter fparam: object.getContentType().getContentTypeParameters()){
//				log.debug("Setting the property Name: \""+fparam.getName()+"\", Value: \""+fparam.getValue()+"\" to the object "+object.getId());
//				GCubeElementProperty prop = new GCubeElementProperty(fparam.getName(), contenttypeParamsPropertyType, fparam.getValue());
//				gdoc.properties().put(fparam.getName(), prop);
//			}
//		}
//
//		return cmWriter.add(gdoc);
//	}
//
//	public static void storeDataElementToCMAsAlternativeRepresentation(
//			DocumentWriter cmWriter, GCubeDocument gdoc, DataElement dataElement, String representationRole) throws Exception {
//
//		GCubeAlternative alternative = new GCubeAlternative();
//		alternative.setMimeType(dataElement.getContentType().getMimeType());
//		alternative.setBytestream(dataElement.getContent());
//		alternative.setName(dataElement.getAttributeValue(DataHandlerDefinitions.ATTR_DOCUMENT_NAME));
//		alternative.setType(representationRole);
//		//we add the alternative to the document
//		gdoc.trackChanges();
//		gdoc.alternatives().add(alternative);
//
//		//we ask for an update
//		cmWriter.update(gdoc);
//	}
//
//	private static InputStream getContnetStream(GCubeElement document, GCUBEScope scope) throws MalformedURLException, IOException {
//		if (document.bytestreamURI() != null){
//			if (document.bytestreamURI().getScheme().equals("sms")){
//				try {
//					return SMSURLConnection.openConnection(document.bytestreamURI(), scope.toString()).getInputStream();
//				} catch (URISyntaxException e) {
//					log.error("Cannot get stream for metadata, "+document.id(),e);
//					return null;
//				}
//			}else{
//				return document.bytestreamURI().toURL().openStream();
//			}
//		}else if (document.bytestream() != null){
//			byte[] content = document.bytestream();
//			ByteArrayInputStream ins = new ByteArrayInputStream(content);
//			return ins;
//		}else
//			return null;
//	}
//
//}
