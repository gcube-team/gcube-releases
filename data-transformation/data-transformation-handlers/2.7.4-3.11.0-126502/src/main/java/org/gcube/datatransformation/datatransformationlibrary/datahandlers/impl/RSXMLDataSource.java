//package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;
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
//import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Type;
//import org.gcube.common.searchservice.searchlibrary.resultset.elements.RecordAttribute;
//import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementGeneric;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceLocalType;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSXMLIterator;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSXMLReader;
//
///**
// * @author Dimitris Katris, NKUA
// *
// * <p>
// * This <tt>DataSource</tt> fetches <tt>DataElements</tt> from a metadata collection.
// * </p>
// */
//public class RSXMLDataSource implements DataSource, ContentTypeDataSource {
//
//	private RSXMLIterator rsiterator;
//	private RSXMLReader rsreader;
//	
//	private static Logger log = LoggerFactory.getLogger(RSXMLDataSource.class);
//	
//	private static String XMLMimeType = "text/xml";
//	
//	/**
//	 * @param input The input value of the <tt>DataSource</tt>.
//	 * @param inputParameters The input parameters of the <tt>DataSource</tt>. <b>getElementsRS</b> is the only parameter identified. 
//	 * @throws Exception If the <tt>MCollectionDataSource</tt> could not be initialized.
//	 */
//	public RSXMLDataSource(String RSEPR, Parameter[] inputParameters) throws Exception {
//		
//		log.info("Initialization of RS with RSEPR: "+ RSEPR);
//		try {
//			rsreader = RSXMLReader.getRSXMLReader(new RSLocator(RSEPR)).makeLocalPatiently(new RSResourceLocalType(), 1200000);
//			rsiterator = rsreader.getRSIterator();
//		} catch (Exception e) {
//			log.error("Could not get RSXMLIterator ", e);
//			throw new Exception("Could not get RSXMLIterator ", e);
//		}
//	}
//	
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#hasNext()
//	 * @return true if the <tt>DataSource</tt> has more elements.
//	 */
//	public boolean hasNext() {
//		return rsiterator.hasNext();
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#next()
//	 * @return the next element of the <tt>DataSource</tt>.
//	 */
//	public DataElement next() {
//		String objectID=null;
//		try {
//			ResultElementGeneric rselement = (ResultElementGeneric)rsiterator.next(ResultElementGeneric.class);
//			StrDataElement element = StrDataElement.getSourceDataElement();
//			objectID = rselement.getRecordAttributes(ResultElementGeneric.RECORD_ID_NAME)[0].getAttrValue();
//			String collID = rselement.getRecordAttributes(ResultElementGeneric.RECORD_COLLECTION_NAME)[0].getAttrValue();
//			
//			element.setId(objectID);
//			
//			ContentType contentType = new ContentType();
//			contentType.setMimeType(XMLMimeType);
//			
//			element.setContentType(contentType);
//			
//			RecordAttribute[] attrs = rselement.getRecordAttributes();
//			if(attrs!=null && attrs.length>0){
//				for (RecordAttribute attr : attrs) {
//					log.debug("Got record attribute from RSXML: "+attr.getAttrName()+" - "+ attr.getAttrValue());
//					element.setAttribute(attr.getAttrName(), attr.getAttrValue());
//				}
//			}else{
//				log.trace("Result set record does not contain any attributes");
//			}
//			
//			element.setAttribute(DataHandlerDefinitions.ATTR_CONTENT_OID, objectID);
//			element.setAttribute(DataHandlerDefinitions.ATTR_METADATACOL_ID, collID);
//
//			element.setContent(rselement.getPayload());
//			
//			ReportManager.manageRecord(objectID, "Object with id "+objectID+", ReferencedCOID "+element.getAttributeValue(DataHandlerDefinitions.ATTR_CONTENT_OID)+" was successfully fetched by RS from MCollection", Status.SUCCESSFUL, Type.SOURCE);
//			return element;
//		} catch (Exception e) {
//			log.error("Did not manage to get data element from RS", e);
//			ReportManager.manageRecord(objectID, "Object with id "+objectID+" could not be fetched by RS from RSXML", Status.FAILED, Type.SOURCE);
//			return null;
//		}
//	}
//	
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
//	 */
//	public void close() {
//		try {
//			if(!isClosed){
//				isClosed=true;
//				rsreader.clear();
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
//	/**
//	 * @return the content type which will always be text/xml
//	 */
//	public ContentType nextContentType() {
//		rsiterator.next(ResultElementGeneric.class);
//
//		ContentType ct = new ContentType();
//		ct.setMimeType(XMLMimeType);
//		
//		return ct;
//	}
//}
