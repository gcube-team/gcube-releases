//package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;
//
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.gcube.contentmanagement.contentmanager.smsplugin.util.GCubeCollections;
//import org.gcube.contentmanagement.contentmanager.stubs.CollectionReference;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.DocumentWriter;
//import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
//import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Status;
//import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Type;
//import org.gcube.datatransformation.datatransformationlibrary.security.DTSSManager;
//import org.gcube.datatransformation.datatransformationlibrary.statistics.Metric;
//import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager;
//import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager.MetricType;
//import org.gcube.datatransformation.datatransformationlibrary.DTSCore;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataBridge;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandlerDefinitions;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.handlers.CMSUtils;
//import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
//
////TODO: Any output parameters should be stored as document properties...
////collectionName shall be one of the output parameters...
///**
// * @author Dimitris Katris, NKUA
// *
// * <p>
// * This <tt>DataSink</tt> stores <tt>DataElements</tt> in a new content collection.
// * </p>
// */
//public class CollectionDataSink extends Thread implements DataSink{
//
//	private DataBridge bridge = DTSCore.getHardDataBridge();
//
//	private static Logger log = LoggerFactory.getLogger(CollectionDataSink.class);
//
//	private static Metric cmsDataSinkMetric = StatisticsManager.createMetric("CMSDataSinkMetric", "Time to store object to CMS", MetricType.SINK);
//
//	private String collectionID=null;
//
//	//Default Creation Values...
//	private String collectionName = "DTSCreatedCollection";
//	private String collectionDesc = "DTSCreatedCollectionDescription";
//
//	boolean isUserCollection = false;
//
//	boolean isVirtual = false;
//
//	protected static final String PARAMETER_CollectionName="CollectionName";
//	protected static final String PARAMETER_CollectionDesc="CollectionDesc";
//	protected static final String PARAMETER_IsUserCollection="isUserCollection";
//	protected static final String PARAMETER_IsVirtual="isVirtual";
//
//	private String documentName = "DTSProducedDoc";
//
//	private DocumentWriter cmWriter;
//
//	/**
//	 * @param output The output value of the <tt>DataSink</tt>.
//	 * @param outputParameters The output parameters of the <tt>DataSink</tt>.
//	 * @throws Exception If the new collection could not be created.
//	 */
//	public CollectionDataSink(String output, Parameter[] outputParameters) throws Exception {
//
//		if(outputParameters!=null && outputParameters.length>0){
//			nextParam: for(Parameter param: outputParameters){
//				if(param==null || param.getName()==null || param.getValue()==null){
//					continue nextParam;
//				}
//				if(param.getName().equals(PARAMETER_CollectionName)){
//					if(param.getValue()!=null && param.getValue().trim().length()>0){
//						collectionName=param.getValue();
//					}
//				}else if(param.getName().equals(PARAMETER_CollectionDesc)){
//					if(param.getValue()!=null && param.getValue().trim().length()>0){
//						collectionDesc=param.getValue();
//					}
//				}else if(param.getName().equals(PARAMETER_IsUserCollection)){
//					isUserCollection = Boolean.parseBoolean(param.getValue());
//				}else if(param.getName().equals(PARAMETER_IsVirtual)){
//					isVirtual = Boolean.parseBoolean(param.getValue());
//					log.warn("Is Virtual is depricated. Ignoring it.");
//
//				}
//			}
//		}
//
//		if(output==null || output.trim().length()==0){
//
//			try {
//
//				//we want to propagate the request to others CM
//				boolean propagateRequest = true;
//				//we want the collection to be readable and writable
//				boolean readable = true;
//				boolean writable = true;
//
//				//finally we create the collection
//				List<CollectionReference> collectionReferences = GCubeCollections.createGCubeCollection(
//						propagateRequest, collectionName, collectionDesc,
//						isUserCollection, readable, writable, DTSSManager.getScope(),
//						DTSSManager.getSecurityManager());
//
//				log.debug("Created collections: "+collectionReferences.size());
//				//We are using the first collection... not sure if this is OK
//				String newcolid = collectionReferences.get(0).getCollectionID();
//				CollectionReference newcollectionReference = collectionReferences.get(0);
//				
//				for (CollectionReference collectionReference:collectionReferences){
//					log.debug("Collection id: "+collectionReference.getCollectionID());
//				}
//				if(newcolid!=null && newcolid.trim().length()>0){
//					log.info("Managed to create a new collection with id "+newcolid+" and name "+collectionName);
//				}else{
//					throw new Exception("Could not create new content collection");
//				}
//				this.collectionID=newcolid;
//				cmWriter = new DocumentWriter(newcollectionReference, DTSSManager.getScope());
//
//			} catch (Exception e) {
//				log.error("Could not create new content collection",e);
//				throw new Exception("Could not create new content collection");
//			}
//		}else {
//			//Using the collection set by the user...
//			this.collectionID=output;
//		}
//
//		this.start();
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#append(org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement)
//	 * @param object {@link DataElement} to be appended to this <tt>DataSink</tt>
//	 */
//	public void append(DataElement object) {
//		if(object!=null)
//			bridge.append(object);
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
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#getOutput()
//	 * @return The output of the transformation.
//	 */
//	public String getOutput() {
//		return this.collectionID;
//	}
//
//	/**
//	 * @see java.lang.Thread#run()
//	 */
//	public void run(){
//		try {
//			//			CMSPortType1PortType cms = CMSUtils.getCMSPortType(DTSSManager.getSecurityManager(), DTSSManager.getScope());
//			while(this.bridge.hasNext()){
//				DataElement object = bridge.next();
//				if(object!=null){
//					log.debug("Going to store to collection "+this.collectionID+" the object with id "+object.getId()+" and content type "+object.getContentType().toString());
//					try {
//						long startTime = System.currentTimeMillis();
////						String storedObjectID = CMSUtils.storeDataElementToCMS(cms, object, documentName, this.collectionID);
//						if (object.getAttributeValue(DataHandlerDefinitions.ATTR_DOCUMENT_NAME)==null ||
//								object.getAttributeValue(DataHandlerDefinitions.ATTR_DOCUMENT_NAME).length() == 0)
//							object.setAttribute(DataHandlerDefinitions.ATTR_DOCUMENT_NAME, documentName);
//						String storedObjectID = CMSUtils.storeDataElementToCM(cmWriter, object);
//						cmsDataSinkMetric.addMeasure(System.currentTimeMillis()-startTime);
//						if(storedObjectID==null){throw new Exception();}
//						ReportManager.manageRecord(object.getId(), "Data element with id "+object.getId()+" and content type "+object.getContentType().toString()+" was stored successfully to CMS with id "+storedObjectID, Status.SUCCESSFUL, Type.SINK);
//					} catch (Exception e) {
//						log.error("Did not manage to store data element with id "+object.getId()+" at CMS, continuing...");
//						ReportManager.manageRecord(object.getId(), "Did not manage to store data element with id "+object.getId()+" and content type "+object.getContentType().toString()+" to CMS", Status.FAILED, Type.SINK);					
//					}
//				}
//			}
//		} catch (Exception e) {
//			log.error("Could not store objects in CMS",e);
//		}
//		ReportManager.closeReport();
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#isClosed()
//	 * @return true if the <tt>DataHandler</tt> has been closed.
//	 */
//	public boolean isClosed() {
//		return bridge.isClosed();
//	}
//}
