//package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;
//
//import java.net.URI;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.gcube.contentmanagement.contentmanager.stubs.model.protocol.URIs;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.DocumentWriter;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeDocument;
//import org.gcube.datatransformation.datatransformationlibrary.DTSCore;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataBridge;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandlerDefinitions;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.handlers.CMSUtils;
//import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
//import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
//import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Status;
//import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Type;
//import org.gcube.datatransformation.datatransformationlibrary.security.DTSSManager;
//
////TODO: If output parameters have names as rank - isWeakRepresentation... then they should be used otherwise default or null if not neccessary...
////Also any other parameters should be stored as document properties...
///**
// * @author Dimitris Katris, NKUA
// *
// * <p>
// * This <tt>DataSink</tt> stores <tt>DataElements</tt> as alternative representations.
// * </p>
// */
//public class AlternativeRepresentationDataSink extends Thread implements DataSink {
//
//	private static Logger log = LoggerFactory.getLogger(AlternativeRepresentationDataSink.class);
//
//	protected static final String PARAMETER_RepresentationRole="RepresentationRole";
//
//	private String documentName="dtsproducedaltrepr";
//
//	private String representationRole;
//
//	private DataBridge bridge = DTSCore.getHardDataBridge();
//
//	/**
//	 * This constructor is used when DTS is instantiating a new AlternativeRepresentationDataSink.
//	 * 
//	 * @param output The output value of the <tt>DataSink</tt>.
//	 * @param outputParameters The output parameters of the <tt>DataSink</tt>.
//	 */
//	public AlternativeRepresentationDataSink(String output, Parameter[] outputParameters){
//		if(outputParameters!=null && outputParameters.length>0){
//			nextParam: for(Parameter param: outputParameters){
//				if(param==null || param.getName()==null || param.getValue()==null){
//					continue nextParam;
//				}
//				if(param.getName().equals(PARAMETER_RepresentationRole)){
//					if(param.getValue()!=null && param.getValue().trim().length()>0){
//						representationRole=param.getValue();
//					}
//				}
//			}
//		}
//		this.start();
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#append(org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement)
//	 * @param element {@link DataElement} to be appended to this <tt>DataSink</tt>
//	 */
//	public void append(DataElement element) {
//		if(element!=null)
//			bridge.append(element);
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
//	public String getOutput() {return "nothing";}
//
//	/**
//	 * @see java.lang.Thread#run()
//	 */
//	public void run(){
//		try {
//			while(this.bridge.hasNext()){
//				DataElement dataElement = bridge.next();
//				if(dataElement!=null){
//					log.debug("Going to store alternative representation of the object with id "+dataElement.getId()+" and content type "+dataElement.getContentType().toString());
//					try {
//						String docURL = dataElement.getAttributeValue(DataHandlerDefinitions.ATTR_CONTENT_OID);
//						log.debug("Document has URL: " + docURL);
//						String collectionID = URIs.collectionID(new URI(docURL));
//						String documentID = URIs.documentID(new URI(docURL));
//						DocumentWriter cmWriter = new DocumentWriter(collectionID, DTSSManager.getScope(),DTSSManager.getSecurityManager());
//
//						GCubeDocument gdoc = new GCubeDocument(documentID);
//
//						if (dataElement.getAttributeValue(DataHandlerDefinitions.ATTR_DOCUMENT_NAME)==null ||
//								dataElement.getAttributeValue(DataHandlerDefinitions.ATTR_DOCUMENT_NAME).length() == 0)
//							dataElement.setAttribute(DataHandlerDefinitions.ATTR_DOCUMENT_NAME, documentName);
//
//
//						CMSUtils.storeDataElementToCMAsAlternativeRepresentation(cmWriter, gdoc, dataElement, representationRole);
//						ReportManager.manageRecord(dataElement.getId(), "Alternative representation of object with id "+dataElement.getId()+" and content type "+dataElement.getContentType().toString()+" was stored successfully", Status.SUCCESSFUL, Type.SINK);
//					} catch (Exception e) {
//						log.error("Did not manage to store data element with id "+dataElement.getId()+" at CMS, continuing...");
//						log.debug("Did not manage to store data element with id "+dataElement.getId()+" because: ",e);
//						ReportManager.manageRecord(dataElement.getId(), "Did not manage to store Alternative representation of object with id "+dataElement.getId()+" and content type "+dataElement.getContentType().toString()+" to CMS", Status.FAILED, Type.SINK);
//					}
//				}
//			}
//		} catch (Exception e) {
//			log.error("Could not store alternative representations objects in CMS",e);
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
