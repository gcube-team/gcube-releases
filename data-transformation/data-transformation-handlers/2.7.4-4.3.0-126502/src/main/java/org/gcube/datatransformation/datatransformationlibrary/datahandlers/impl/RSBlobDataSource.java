//package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeDataSource;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.utils.ResultSetDataElement;
//import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
//import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceLocalType;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSBLOBIterator;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSBLOBReader;
//
///**
// * @author Dimitris Katris, NKUA
// *
// * <p>
// * This <tt>DataSource</tt> fetches <tt>DataElements</tt> from a result set with blob elements.
// * </p>
// */
//public class RSBlobDataSource implements DataSource, ContentTypeDataSource {
//
//	private static Logger log = LoggerFactory.getLogger(RSBlobDataSource.class);
//	
//	private RSBLOBIterator rsiterator=null;
//	private RSBLOBReader rsreader=null;
//	
//	/**
//	 * The input must be a locator for a RS with elements 
//	 * @param input The input value of the <tt>DataSource</tt>.
//	 * @param inputParameters The input parameters of the <tt>DataSource</tt>.
//	 */
//	public RSBlobDataSource(String input, Parameter[] inputParameters){
//		RSBLOBIterator iterator;
//		try {//TODO: Check the makeLocal()...
//			rsreader = RSBLOBReader.getRSBLOBReader(new RSLocator(input)).makeLocal(new RSResourceLocalType());
//			iterator = rsreader.getRSIterator();
//			this.rsiterator=iterator;
//		} catch (Exception e) {
//			log.error("Could not create blob iterator",e);
//		}
//	}
//	
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#hasNext()
//	 * @return true if the <tt>DataSource</tt> has more elements.
//	 */
//	public boolean hasNext() {
//		if(rsiterator==null)
//			return false;
//		return rsiterator.hasNext();
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#next()
//	 * @return the next element of the <tt>DataSource</tt>.
//	 */
//	public DataElement next() {
//		if(rsiterator==null)
//			return null;
//		ResultSetDataElement rsElement = (ResultSetDataElement)rsiterator.next(ResultSetDataElement.class);
//		if(rsElement==null){
//			log.warn("Got null object...");
//			return null;
//		}
//		log.debug("Got object with id "+rsElement.getOID());
//		return rsElement.getDataElement();
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
//	public ContentType nextContentType() {
//		DataElement de = next();
//		
//		return de == null? null : de.getContentType();
//	}
//}
