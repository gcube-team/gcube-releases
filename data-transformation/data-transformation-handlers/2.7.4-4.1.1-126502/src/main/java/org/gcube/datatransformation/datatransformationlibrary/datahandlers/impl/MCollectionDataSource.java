//package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.io.StringWriter;
//import java.io.Writer;
//import java.net.MalformedURLException;
//import java.net.URISyntaxException;
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.StrDataElement;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeDataSource;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandlerDefinitions;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
//import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
//import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
//import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
//import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Status;
//import org.gcube.datatransformation.datatransformationlibrary.security.DTSSManager;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementGeneric;
//import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementXBean;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceLocalType;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSXMLIterator;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSXMLReader;
//import org.gcube.common.searchservice.searchlibrary.rswriter.RSXMLWriter;
//import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Type;
//import org.gcube.contentmanagement.contentmanager.stubs.calls.iterators.RemoteIterator;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.ViewReader;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.MetadataProjection;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.Projections;
//import org.gcube.contentmanagement.gcubedocumentlibrary.views.MetadataView;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeDocument;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeMetadata;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.MetadataElements;
//import org.gcube.contentmanagement.storagelayer.storagemanagementservice.stubs.protocol.SMSURLConnection;
//
///**
// * @author Dimitris Katris, NKUA
// *
// * <p>
// * This <tt>DataSource</tt> fetches <tt>DataElements</tt> from a metadata collection.
// * </p>
// */
//public class MCollectionDataSource implements DataSource, ContentTypeDataSource {
//
//	private Boolean readFromRS = false;
//	private String contentCollectionID;
//	private String mCollectionID;
//	private RSXMLIterator rsiterator;
//	private RSXMLReader rsreader;
//
//	private static Logger log = LoggerFactory.getLogger(MCollectionDataSource.class);
//
//	private static String XMLMimeType = "application/xml";
//
//	private String schemaName;
//	private String schemaURI;
//	private String language;
//
//	private ViewReader cmReader;
//	
//	RemoteIterator<GCubeDocument> metadata = null;
//
//
//	/**
//	 * @param input The input value of the <tt>DataSource</tt>.
//	 * @param inputParameters The input parameters of the <tt>DataSource</tt>. <b>getElementsRS</b> is the only parameter identified. 
//	 * @throws Exception If the <tt>MCollectionDataSource</tt> could not be initialized.
//	 */
//	public MCollectionDataSource(String input, Parameter[] inputParameters) throws Exception {
//		this.mCollectionID=input;
//
//		MetadataView tView = new MetadataView(DTSSManager.getScope());
//		tView.setId(mCollectionID);
//		List<MetadataView> views = tView.findSimilar();
//
//		if (views.isEmpty()){
//			throw new Exception("Metadata view not found for ID: " + mCollectionID);
//		}
//
//		MetadataView view = views.get(0);
//		cmReader = view.reader();
//		MetadataProjection mp = Projections.metadata();
//		metadata = cmReader.get(mp);
//
//		contentCollectionID = view.collectionId();
//		log.debug("ContentCollectionID is found to be "+contentCollectionID);
//		schemaName=view.name();
//		schemaURI=view.schemaName().toString();
//		language=view.language();
//
//		String getElementsRS=null;
//		if(inputParameters != null && inputParameters.length>0){
//			for(Parameter param: inputParameters){
//				log.debug("Got Input parameter: " + param.toString());
//				if(param.getName()!=null && param.getName().equalsIgnoreCase("getElementsRS")){
//					if(param.getValue()!=null && param.getValue().trim().length()>0){
//						getElementsRS = param.getValue();
//					}else{
//						log.warn("Parameter getElementsRS found without having specified properly the value");
//					}
//				}
//			}
//		}
//
//		String RSEPR;
//		try {
//			if(getElementsRS!=null){
//				readFromRS = true;
//				RSEPR = getElementsRS(getElementsRS, mCollectionID, cmReader);
//				log.debug("Some SPECIFIC collection contents are in RS EPR: \n" + RSEPR);
//				rsreader = RSXMLReader.getRSXMLReader(new RSLocator(RSEPR)).makeLocalPatiently(new RSResourceLocalType(), 1200000);
//				rsiterator = rsreader.getRSIterator();
//			}else{
//				readFromRS = false;
//				log.debug("ALL collection contents\n");
//			}
//		} catch (Exception e) {
//			log.error("Failed to retrieve the metadata objects of metadata collection: " + mCollectionID, e);
//			throw new Exception("Failed to retrieve the metadata objects of metadata collection: " + mCollectionID, e);
//		}
//
//	}
//
//	private String getElementsRS(String getElementsRS, final String collectionID, final ViewReader metadataReader) throws Exception {
//		try {
//			final RSXMLReader rsWithIDsIreader = RSXMLReader.getRSXMLReader(new RSLocator(getElementsRS)).makeLocalPatiently(new RSResourceLocalType(), 1200000);
//			final RSXMLIterator rsWithIDsIterator = rsWithIDsIreader.getRSIterator();
//			final RSXMLWriter rsWithPayloadWriter = RSXMLWriter.getRSXMLWriter();
//			new Thread(){
//				@Override
//				public void run() {
//					String metadataObjectID;
//					int failures=0;
//					nextResult: while(rsWithIDsIterator.hasNext()){
//						try {
//							ResultElementGeneric rselement = (ResultElementGeneric)rsWithIDsIterator.next(ResultElementGeneric.class);
//							metadataObjectID = rselement.getRecordAttributes(ResultElementGeneric.RECORD_ID_NAME)[0].getAttrValue();
//							log.debug("Managed to get metadata object from result set with id: "+metadataObjectID);
//
//							MetadataProjection mp = Projections.metadata();
//							GCubeDocument doc = metadataReader.get(metadataObjectID, mp);
//							log.trace("Got metadata object: "+doc.id());
//							rsWithPayloadWriter.addResults(new ResultElementXBean(metadataObjectID, collectionID, doc));
//
//						} catch (Exception e) {
//							log.error("Could not manage result set record", e);
//							failures++;
//							if(failures==10){
//								log.debug("10 failures occured abording reading result set");
//								break nextResult;
//							}
//						}
//					}
//					try {
//						log.info("Got all metadata objects from result set with ids, closing result set with the payload");
//						rsWithPayloadWriter.close();
//					} catch (Exception e) {
//						log.error("Did not manage to close result set", e);
//					}
//				}
//			}.start();
//			return rsWithPayloadWriter.getRSLocator(new RSResourceLocalType()).getLocator();
//		} catch (Exception e) {
//			log.error("Could not read getElementsRS or create writer", e);
//			throw new Exception("Could not read getElementsRS or create writer", e);
//		}
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#hasNext()
//	 * @return true if the <tt>DataSource</tt> has more elements.
//	 */
//	public boolean hasNext() {
//		if (readFromRS)
//			return rsiterator.hasNext();
//		else{
//			log.trace("More documents to process? "+metadata.hasNext());
//			return metadata.hasNext();
//		}
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#next()
//	 * @return the next element of the <tt>DataSource</tt>.
//	 */
//	public DataElement next() {
//		GCubeDocument doc = null;
//		GCubeMetadata metaelement = null;
//		String OID = "None";
//		try {
//			if (readFromRS){
//				ResultElementXBean rselement = (ResultElementXBean)rsiterator.next(ResultElementXBean.class);
//				doc = (GCubeDocument)rselement.getBean();
//			}else{
//				log.trace("Reading next metadata document!");
//				
//				doc = metadata.next();
//
//				MetadataElements elements = doc.metadata();
//				if (elements.size() == 0){
//					log.error("No metadata documents are found for metadata object "+doc.id());					
//					throw new Exception("No metadata documents are found for metadata object "+doc.id());
//				}
//				if (elements.size() > 1){
//					log.warn("More than one metadata elements for object "+doc.id());
//				}
//				metaelement = elements.iterator().next();
//				log.trace("Metadata:");
//				log.trace(" id: " + metaelement.id());
//				log.trace(" mimeType: " + metaelement.mimeType());
//				log.trace(" length: " + metaelement.length());
//				log.trace(" Metadata info:");
//				log.trace("  name: " + metaelement.schemaName());
//				log.trace("  schema: " + metaelement.schemaURI());
//				log.trace("  language: " + metaelement.language());
//
//			}
//
//			StrDataElement element = StrDataElement.getSourceDataElement();
//			element.setId(metaelement.id());
//
//			ContentType contentType = new ContentType();
//			contentType.setMimeType(XMLMimeType);
//
//			contentType.addContentTypeParameters(new Parameter("schema", schemaName), new Parameter("language", language), new Parameter("schemaURI", schemaURI));
//
//			element.setContentType(contentType);
//
//			element.setAttribute("schema", schemaName);
//			element.setAttribute("language", language);
//			element.setAttribute("schemaURI", schemaURI);
//			
//			//OID = metaDocument.getParentId();
//			OID = doc.uri().toString();
//			String MCID = mCollectionID;
//			String MOID = metaelement.uri().toString();
////			String MCID = contentCollectionID;
//			String body = getContent(metaelement, DTSSManager.getScope());
//			//String body = streamToString(metaDocument.getContent());
//
//			log.debug("Got attribute from payload "+DataHandlerDefinitions.ATTR_CONTENT_OID+" - "+ OID);
//			log.debug("Got attribute from payload "+DataHandlerDefinitions.ATTR_METADATACOL_ID+" - "+ MCID);
//			log.debug("Got attribute from payload "+DataHandlerDefinitions.ATTR_METADATA_OID+" - "+ MOID);
//			log.debug("Got attribute from view "+DataHandlerDefinitions.ATTR_DOCUMENT_LANGUAGE+" - "+ language);
//
//			element.setAttribute(DataHandlerDefinitions.ATTR_CONTENT_OID, OID);
//			element.setAttribute(DataHandlerDefinitions.ATTR_METADATACOL_ID, MCID);
//			element.setAttribute(DataHandlerDefinitions.ATTR_METADATA_OID, MOID);
//			element.setAttribute(DataHandlerDefinitions.ATTR_DOCUMENT_LANGUAGE, language);
//			element.setAttribute("ContentCollectionID", contentCollectionID); 
//			log.debug("Set attribute ContentCollectionID "+contentCollectionID);
//			                      
//			element.setContent(body);
//
//			//TODO: Check if custome? properties play any role here
//			/*
//			RecordAttribute[] attrs = rselement.getRecordAttributes();
//			if(attrs!=null && attrs.length>0){
//				for (RecordAttribute attr : attrs) {
//					log.debug("Got record attribute from MM: "+attr.getAttrName()+" - "+ attr.getAttrValue());
//					element.setAttribute(attr.getAttrName(), attr.getAttrValue());
//				}
//			}else{
//				log.trace("Result set record does not contain any attributes");
//			}
//			 */
//
//			ReportManager.manageRecord(OID, "Object with id "+OID+", MOID "+element.getAttributeValue(DataHandlerDefinitions.ATTR_METADATA_OID)+", ReferencedCOID "+element.getAttributeValue(DataHandlerDefinitions.ATTR_CONTENT_OID)+" was successfully fetched by RS from MCollection", Status.SUCCESSFUL, Type.SOURCE);
//			return element;
//		} catch (Exception e) {
//			log.error("Did not manage to get data element from RS", e);
//			ReportManager.manageRecord(OID, "Object with id "+OID+" could not be fetched by RS from MCollection", Status.FAILED, Type.SOURCE);
//			return null;
//		}
//
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
//	 */
//	public void close() {
//		try {
//			if(!isClosed){
//				isClosed=true;
//				try {
//					rsreader.clear();
//				} catch (Exception e) {}
//			}
//		} catch (Exception e) {
//			log.error("Did not manage to clear rs reader", e);
//		}
//	}
//
//	private boolean isClosed=false;
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#isClosed()
//	 * @return true if the <tt>DataHandler</tt> has been closed.
//	 */
//	public boolean isClosed() {
//		return isClosed;
//	}
//
//	public String streamToString(InputStream in) throws IOException {
//		StringBuffer out = new StringBuffer();
//		byte[] b = new byte[4096];
//		for (int n; (n = in.read(b)) != -1;) {
//			out.append(new String(b, 0, n));
//		}
//		return out.toString();
//	}
//
//	protected static String getContent(GCubeMetadata metadata, GCUBEScope scope) throws Exception 
//	{ 
//		
//		InputStream instream = getContnetStream(metadata, scope);
//		if (instream == null)
//			throw new Exception("Metadata document "+metadata.id()+" with no content.");
//
//		Writer writer = new StringWriter();
//
//        char[] buffer = new char[1024];
//        try {
//            Reader reader = new BufferedReader(
//                    new InputStreamReader(instream, "UTF-8"));
//            int n;
//            while ((n = reader.read(buffer)) != -1) {
//                writer.write(buffer, 0, n);
//            }
//        } finally {
//            instream.close();
//        }
//        return writer.toString();
//	}
//
//	private static InputStream getContnetStream(GCubeMetadata document, GCUBEScope scope) throws MalformedURLException, IOException {
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
//	public ContentType nextContentType(){
//		GCubeDocument doc = null;
//		try {
//			if (readFromRS){
//				rsiterator.next(ResultElementXBean.class);
//			}else{
//				log.trace("Reading next metadata document!");
//
//				doc = metadata.next();
//
//				MetadataElements elements = doc.metadata();
//				if (elements.size() == 0){
//					log.error("No metadata documents are found for metadata object "+doc.id());					
//					throw new Exception("No metadata documents are found for metadata object "+doc.id());
//				}
//				if (elements.size() > 1){
//					log.warn("More than one metadata elements for object "+doc.id());
//				}
//			}
//
//			ContentType ct = new ContentType();
//			ct.setMimeType(XMLMimeType);
//
//			return ct;
//		} catch (Exception e) {
//			log.error("Did not manage to get data element content type from RS", e);
//			return null;
//		}
//	}
//}
