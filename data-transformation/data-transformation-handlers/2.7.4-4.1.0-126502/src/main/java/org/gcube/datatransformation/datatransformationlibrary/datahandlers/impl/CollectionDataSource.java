//package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.net.URI;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementGeneric;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceLocalType;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSXMLIterator;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSXMLReader;
//import org.gcube.contentmanagement.contentmanager.stubs.calls.iterators.RemoteIterator;
//import org.gcube.contentmanagement.contentmanager.stubs.model.protocol.URIs;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.DocumentReader;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.DocumentProjection;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.Projections;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeDocument;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeDataSource;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataBridge;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.DataElementImpl;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.handlers.CMSUtils;
//import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
//import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
//import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
//import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Status;
//import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Type;
//import org.gcube.datatransformation.datatransformationlibrary.security.DTSSManager;
//import org.gcube.datatransformation.datatransformationlibrary.statistics.Metric;
//import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager;
//import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager.MetricType;
//import org.gcube.datatransformation.datatransformationlibrary.DTSCore;
//
///**
// * @author Dimitris Katris, NKUA
// *
// * <p>
// * This <tt>DataSource</tt> fetches <tt>DataElements</tt> from a content collection.
// * </p>
// */
//public class CollectionDataSource extends Thread implements DataSource, ContentTypeDataSource {
//
//	private String contentCollectionID;
//	private DocumentReader cmReader;
//	private boolean handleParts = false;
//	private boolean handleAlternativeRepresentations = false;
//	
//	private DataBridge bridge = DTSCore.getDataBridge();
//	private static Logger log = LoggerFactory.getLogger(CollectionDataSource.class);
//	
//	private static Metric cmsDataSourceMetric = StatisticsManager.createMetric("CMSDataSourceMetric", "Time to retrieve object from CMS", MetricType.SOURCE);
//	
//	private boolean useOIDs = false;
//	private boolean getContentTypesOnly = false;
//	private String getElementsRS = null;
//	private RSXMLIterator rsiterator;
//	private RSXMLReader rsreader;
//	
//	/**
//	 * @param input The input value of the <tt>DataSource</tt>.
//	 * @param inputParameters The input parameters of the <tt>DataSource</tt>.
//	 * @throws Exception If data source could not be initialized
//	 */
//	public CollectionDataSource(String input, Parameter[] inputParameters) throws Exception {
//		log.debug("Going to fetch objects from collection with id: "+input);
//		this.contentCollectionID=input;
//		
//		if(inputParameters!=null){
//			for(Parameter param: inputParameters){
//				if(param!=null && param.getName()!=null && param.getValue()!=null){
//					if(param.getName().equalsIgnoreCase("handleParts")){
//						try {
//							handleParts = Boolean.parseBoolean(param.getValue());
//						} catch (Exception e) { }
//					}else if(param.getName().equalsIgnoreCase("handleAlternativeRepresentations")){
//						try {
//							handleAlternativeRepresentations = Boolean.parseBoolean(param.getValue());
//						} catch (Exception e) { }
//					}else if(param.getName().equalsIgnoreCase("useOIDs")){
//						try {
//							useOIDs = Boolean.parseBoolean(param.getValue());
//						} catch (Exception e) { }
//					}else if(param.getName().equalsIgnoreCase("getElementsRS")){
//						if(param.getValue()!=null && param.getValue().trim().length()>0){
//							getElementsRS = param.getValue();
//						}else{
//							log.warn("Parameter getElementsRS found without having specified properly the value");
//						}
//					}else if(param.getName().equalsIgnoreCase("getContentTypesOnly")){
//						try {
//							getContentTypesOnly = Boolean.parseBoolean(param.getValue());
//						} catch (Exception e) { }
//					}
//
//				}
//			}
//		}
//		if(handleParts && handleAlternativeRepresentations){
//			log.error("Cannot handle both parts and alternative representations (currently)");
//			throw new Exception("Cannot handle both parts and alternative representations (currently)");
//		}
//		
//		if(getElementsRS!=null){
//			log.info("Going to get SPECIFIC elements from collection from result set:\n"+getElementsRS);
//			try {
//				rsreader = RSXMLReader.getRSXMLReader(new RSLocator(getElementsRS)).makeLocalPatiently(new RSResourceLocalType(), 1200000);
//				rsiterator = rsreader.getRSIterator();
//			} catch (Exception e) {
//				log.error("Could not get RSXMLIterator ", e);
//				throw new Exception("Could not get RSXMLIterator ", e);
//			}
//		}else{
//			log.info("Going to get ALL elements from content collection");
//		}
//		
//		cmReader = new DocumentReader(contentCollectionID, DTSSManager.getScope(), DTSSManager.getSecurityManager());
//		
//		this.start();
//	}
//	
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#hasNext()
//	 * @return true if the <tt>DataSource</tt> has more elements.
//	 */
//	public boolean hasNext() {
//		return bridge.hasNext();
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#next()
//	 * @return the next element of the <tt>DataSource</tt>.
//	 */
//	public DataElement next() {
//		return bridge.next();
//	}
//	
//	/**
//	 * @see java.lang.Thread#run()
//	 */
//	public void run(){
//		try {
//			if(getContentTypesOnly){
//				DocumentProjection dp = Projections.document().with(Projections.MIME_TYPE);
//				RemoteIterator<GCubeDocument> documentIterator = cmReader.get(dp);
//				while(documentIterator.hasNext()){
//					GCubeDocument document = documentIterator.next();
//
//					DataElementImpl de = DataElementImpl.getSourceDataElement();
//					ContentType ct =  new ContentType();
//					ct.setMimeType(document.mimeType());
//
//					de.setContentType(ct);
//
//					bridge.append(de);
//				}
//			}else {
//				if(getElementsRS == null){
//					
//					File tempIDsStorage = File.createTempFile("DTS", ".tmp");
//					log.info("File storing gDoc IDs: " + tempIDsStorage.getName());
//				    BufferedWriter out = new BufferedWriter(new FileWriter(tempIDsStorage));
//					DocumentProjection dp = Projections.document().with(Projections.NAME);
//					RemoteIterator<GCubeDocument> documentIterator = cmReader.get(dp);
//					int i = 0;
//					while(documentIterator.hasNext()){
//						i++;
//						if ((i % 100) == 0)
//							log.info("Pre-fetched IDs for " + i +" docs.");
//						GCubeDocument document = documentIterator.next();
//					    out.write(document.id()+"\n");
//					}
//				    out.close();
//					log.info("Done prefetching IDs and staff...");
//				    
//				    String id;
//				    BufferedReader in = new BufferedReader(new FileReader(tempIDsStorage));
//					dp = Projections.document();
//				    while (( id = in.readLine()) != null){
//				    	if (id.isEmpty())
//				    		break;
//				    	GCubeDocument document = cmReader.get(id, dp);
//						manageObject(document);
//			        }
//				    in.close();
//				    
//					log.info("Removing temp file.");
//				    tempIDsStorage.delete();
//		
//				}else{
//					String objectID;
//					while(rsiterator.hasNext()){
//						try {
//							ResultElementGeneric rselement = (ResultElementGeneric)rsiterator.next(ResultElementGeneric.class);
//							objectID = rselement.getRecordAttributes(ResultElementGeneric.RECORD_ID_NAME)[0].getAttrValue();
//							log.trace("Managed to get object from result set with id: "+objectID);
//							if (useOIDs == false){
//								//Get URI of metadata object
//								URI objURI = new URI(objectID);
//								//Get content object ID
//								objectID = URIs.documentID(objURI);
//		
//							}
//							DocumentProjection dp = Projections.document();
//							GCubeDocument document = cmReader.get(objectID, dp);
//							manageObject(document);
//						} catch (Exception e) {
//							log.error("Did not manage to read result set element", e);
//						}
//					}
//				}
//			}	
//		} catch (Exception e) {
//			log.error("Did not manage to fetch content from cms", e);
//		} finally {
//			bridge.close();
//		}
//	}
//	
//	private void manageObject(GCubeDocument document){
//		try {
//			long startTime = System.currentTimeMillis();
//			DataElement object;
//			if(handleParts){
//				object = CMSUtils.getCompoundDataElementFromCM(document, DTSSManager.getScope());
//			}else if (handleAlternativeRepresentations){
//				object = CMSUtils.getDataElementWithAlternativeRepresentationsFromCM(document, DTSSManager.getScope());
//			}else{
//				object = CMSUtils.getDataElementFromCM(document, DTSSManager.getScope());
//			}
//			cmsDataSourceMetric.addMeasure(System.currentTimeMillis()-startTime);
//			if(object==null){throw new Exception();}
//			bridge.append(object);
//			ReportManager.manageRecord(document.id(), "Object with id "+document.id()+" was downloaded successfully by CMS", Status.SUCCESSFUL, Type.SOURCE);
//		} catch (Exception e) {
//			log.error("Could not manage to fetch the object "+document,e);
//			ReportManager.manageRecord(document.id(), "Object with id "+document+" could not be fetched by CMS", Status.FAILED, Type.SOURCE);
//		}
//	}
//	
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
//	 */
//	public void close() {
//		bridge.close();
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#isClosed()
//	 * @return true if the <tt>DataHandler</tt> has been closed.
//	 */
//	public boolean isClosed() {
//		return bridge.isClosed();
//	}
//	
//	public ContentType nextContentType() {
//		return bridge.next().getContentType();
//	}
//}
