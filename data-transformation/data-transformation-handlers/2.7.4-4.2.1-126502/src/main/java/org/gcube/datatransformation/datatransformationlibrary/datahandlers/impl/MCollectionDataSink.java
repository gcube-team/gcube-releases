//package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URI;
//import java.util.Locale;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.gcube.contentmanagement.contentmanager.stubs.model.protocol.URIs;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.DocumentReader;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.DocumentWriter;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.DocumentProjection;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.Projections;
//import org.gcube.contentmanagement.gcubedocumentlibrary.views.MetadataView;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeDocument;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeMetadata;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.StrDataElement;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandlerDefinitions;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.utils.MetadataViewDescription;
//import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
//import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
//import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Status;
//import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Type;
//import org.gcube.datatransformation.datatransformationlibrary.security.DTSSManager;
//
///**
// * @author Dimitris Katris, NKUA
// *
// * <p>
// * This <tt>DataSink</tt> stores <tt>DataElements</tt> in a metadata collection.
// * </p>
// */
//@Deprecated
//public class MCollectionDataSink implements DataSink {
//
//	private static Logger log = LoggerFactory.getLogger(MCollectionDataSink.class);
//	
//	private DocumentWriter cmWriter = null;
//	private DocumentReader cmReader = null;
//	private String contentColID=null; 
//	private String outColName="DTSProducedMCollection";
//	private String outColDesc="DTS Produced Metadata Collection";
//	private String outColSchemaName=null;
//	private String outColSchemaURI=null;
//	private String outColLanguage=null;
//	private String outColIsIndexable="false";
//	private String outColIsUser="false";
//	private String outColIsEditable="false";
//	private MetadataView myView = null;
//	private Locale locale = null;
//	private boolean isClosed = false;
//	
//	/**
//	 * @param output The output value of the <tt>DataSink</tt>.
//	 * @param outputParameters The output parameters of the <tt>DataSink</tt>.
//	 * @throws Exception If the collection could not be created.
//	 */
//	public MCollectionDataSink(String output, Parameter[] outputParameters) throws Exception {
//		
//		//TODO: If output is metadata collection id store data in this collection...
//		
//		if(outputParameters==null || outputParameters.length==0){
//			log.error("Output Parameters not specified");
//			throw new Exception("Output Parameters not specified");
//		}
//
//		for(Parameter param: outputParameters){
//			log.debug("Parameter: "+param.toString());
//			if(param.getName().equals(MetadataViewDescription.CreationParameters.RELATEDBY_COLLID.toString())){
//				this.contentColID=param.getValue();
//			}else if(param.getName().equals(MetadataViewDescription.CreationParameters.COLLECTIONNAME.toString())){
//				this.outColName=param.getValue();
//			}else if(param.getName().equals(MetadataViewDescription.CreationParameters.DESCRIPTION.toString())){
//				this.outColDesc=param.getValue();
//			}else if(param.getName().equals(MetadataViewDescription.CreationParameters.INDEXABLE.toString())){
//				this.outColIsIndexable=param.getValue();
//			}else if(param.getName().equals(MetadataViewDescription.CreationParameters.USER.toString())){
//				this.outColIsUser=param.getValue();
//			}else if(param.getName().equals(MetadataViewDescription.CreationParameters.METADATANAME.toString())){
//				this.outColSchemaName=param.getValue();
//			}else if(param.getName().equals(MetadataViewDescription.CreationParameters.METADATALANG.toString())){
//				this.outColLanguage=param.getValue();
//			}else if(param.getName().equals(MetadataViewDescription.CreationParameters.METADATAURI.toString())){
//				this.outColSchemaURI=param.getValue();
//			}else if(param.getName().equals(MetadataViewDescription.CreationParameters.EDITABLE.toString())){
//				this.outColIsEditable=param.getValue();
//			}
//		}
//		
//		locale = getLocaleFromString(outColLanguage);
//		
//		if(contentColID==null || outColSchemaName==null || outColSchemaURI==null || outColLanguage==null || locale==null){
//			log.error("ContentColID, OutColSchemaName OutColSchemaURI or OutColLanguage is not specified");
//			throw new Exception("ContentColID, OutColSchemaName OutColSchemaURI or OutColLanguage is not specified");
//		}
//		
//		//Create the view in case someone needs it .....
//		myView = new MetadataView(DTSSManager.getScope());
////		myView.setCollectionId(collectionId);
//		myView.setCollectionId(contentColID);
//		myView.setName(outColName);
//		myView.setDescription(outColDesc);
//		myView.setProjection(locale, outColSchemaName, new URI(outColSchemaURI));
//		myView.setIndexable(true);
//		myView.setUserCollection(true);
//		myView.setEditable(true);
//		if (outColIsIndexable.equalsIgnoreCase("FALSE"))
//			myView.setIndexable(false);
//		else if (outColIsIndexable.equalsIgnoreCase("TRUE"))
//			myView.setIndexable(true);
//
//		if (outColIsUser.equalsIgnoreCase("FALSE"))
//			myView.setUserCollection(false);
//		else if (outColIsUser.equalsIgnoreCase("TRUE"))
//			myView.setUserCollection(true);
//		
//		if (outColIsEditable.equalsIgnoreCase("FALSE"))
//			myView.setEditable(false);
//		else if (outColIsEditable.equalsIgnoreCase("TRUE"))
//			myView.setEditable(true);
//
//		myView.publishAndBroadcast();
//		String newMetadataCollectionID;
//		newMetadataCollectionID = myView.id();
//
//		if (newMetadataCollectionID == null)
//			log.error("Metadata Collection ID not retrieved. Proceeding with the transformation");
//			
//		
//		cmReader = new DocumentReader(contentColID, DTSSManager.getScope(), DTSSManager.getSecurityManager());
//		cmWriter = new DocumentWriter(contentColID, DTSSManager.getScope(), DTSSManager.getSecurityManager());
//
//		log.info("Managed to create new metadata collection with id "+newMetadataCollectionID);
//	}
//	
//	private static String stringFromInputStream (InputStream in) throws IOException {
//	    StringBuffer out = new StringBuffer();
//	    byte[] b = new byte[4096];
//	    for (int n; (n = in.read(b)) != -1;) {
//	        out.append(new String(b, 0, n));
//	    }
//	    return out.toString();
//	}
//	
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#append(org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement)
//	 * @param dataElement {@link DataElement} to be appended to this <tt>DataSink</tt>
//	 */
//	public void append(DataElement dataElement){
//		if(isClosed)return;
//		try {
//
//			String payload; 
//			if(dataElement instanceof StrDataElement){
//				payload = ((StrDataElement)dataElement).getStringContent();
//			}else{
//				payload = stringFromInputStream(dataElement.getContent());
//			}
//
//			URI metadataObjURI = null;
//			String contentOID = null;
//			if(dataElement.getAttributeValue(DataHandlerDefinitions.ATTR_METADATA_OID)!=null){
//				//Get URI of metadata object
//				metadataObjURI = new URI(dataElement.getAttributeValue(DataHandlerDefinitions.ATTR_METADATA_OID));
//				//Get the document URI
//				URI gDocURI = URIs.parentURI(metadataObjURI);
//				//Get content object ID
//				contentOID = URIs.documentID(gDocURI);
//			}
//
//			
//			GCubeMetadata metaDocument = new GCubeMetadata();
//			metaDocument.setBytestream(payload.getBytes());
//			metaDocument.setLanguage(locale);
//			metaDocument.setSchemaName(outColSchemaName);
//			metaDocument.setSchemaURI(new URI(outColSchemaURI));
//			
//			DocumentProjection dp = Projections.document().with(Projections.NAME);
//			GCubeDocument doc =   cmReader.get(contentOID, dp);
//			doc.metadata().add(metaDocument);
//			cmWriter.update(doc);
//
//			/* Add every attribute as a ResultSet attribute */
////			try {
////				int i = 0;
////				Map<String,String> attributes = dataElement.getAllAttributes();
////				RecordAttribute[] rsAttrs = new RecordAttribute[attributes.size()];
////				for (String attrName : attributes.keySet()) {
////					rsAttrs[i] = new RecordAttribute(attrName, attributes.get(attrName));
////					i++;
////				}
////				rsElem.setRecordAttributes(rsAttrs);
////			} catch (Exception e) {
////				log.error("Failed to set attributes on the new ResultSet element.", e);
////				throw new Exception("Failed to set attributes on the new ResultSet element.", e);
////			}
//			
//		} catch (Exception e) {
//			log.error("Failed to append Element");
//			ReportManager.manageRecord(dataElement.getId(), "Object with id "+dataElement.getId()+", MOID "+dataElement.getAttributeValue(DataHandlerDefinitions.ATTR_METADATA_OID)+", ReferencedCOID "+dataElement.getAttributeValue(DataHandlerDefinitions.ATTR_CONTENT_OID)+" could not be appended to RS for MCollection"+e.getMessage(), Status.FAILED, Type.SINK);
//		}
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
//	 */
//	public void close() {
//		if(!isClosed){
//			isClosed=true;
//			try {
//				log.debug("Closing the Metadata Manager Sink...");
//			} catch (Exception e) {
//				log.error("Could not close RSXMLWriter ", e);
//			}
//			try {
//				log.debug("The result set must have been send to metadata manager successfully...");
//			} catch (Exception e) {
//				log.error("Could not create RS Locator", e);
//			}
//			ReportManager.closeReport();
//		}else{
//			log.error("Trying to close an already closed Metadata Manager data sink...");
//		}
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#getOutput()
//	 * @return The output of the transformation.
//	 */
//	public String getOutput() {
//		String newMetadataCollectionID;
//		newMetadataCollectionID = myView.id();
//
//		if (newMetadataCollectionID == null)
//			log.error("Metadata Collection ID not retrieved. Proceeding with the transformation");
//
//		return newMetadataCollectionID;
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#isClosed()
//	 * @return true if the <tt>DataHandler</tt> has been closed.
//	 */
//	public boolean isClosed() {
//		return isClosed;
//	}
//
//	
//	public Locale getLocaleFromString(String localeString)
//    {
//        if (localeString == null)
//        {
//            return null;
//        }
//        localeString = localeString.trim();
//        if (localeString.toLowerCase().equals("default"))
//        {
//            return Locale.getDefault();
//        }
//
//        // Extract language
//        int languageIndex = localeString.indexOf('_');
//        String language = null;
//        if (languageIndex == -1)
//        {
//            // No further "_" so is "{language}" only
//            return new Locale(localeString, "");
//        }
//        else
//        {
//            language = localeString.substring(0, languageIndex);
//        }
//
//        // Extract country
//        int countryIndex = localeString.indexOf('_', languageIndex + 1);
//        String country = null;
//        if (countryIndex == -1)
//        {
//            // No further "_" so is "{language}_{country}"
//            country = localeString.substring(languageIndex+1);
//            return new Locale(language, country);
//        }
//        else
//        {
//            // Assume all remaining is the variant so is "{language}_{country}_{variant}"
//            country = localeString.substring(languageIndex+1, countryIndex);
//            String variant = localeString.substring(countryIndex+1);
//            return new Locale(language, country, variant);
//        }
//    }
//}
