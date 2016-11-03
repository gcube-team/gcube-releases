//package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataBridge;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.utils.ResultSetDataElement;
//import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceLocalType;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSBLOBIterator;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSBLOBReader;
//import org.gcube.common.searchservice.searchlibrary.rswriter.RSBLOBWriter;
//
///**
// * @author Dimitris Katris, NKUA
// *
// * <p>
// * <tt>DataBridge</tt> which appends data elements to a local result set.
// * </p>
// */
//public class RSBlobDataBridge implements DataBridge{
//	
//	private static Logger log = LoggerFactory.getLogger(RSBlobDataSource.class);
//	
//	private RSBLOBIterator rsiterator;
//	private RSBLOBWriter writer;
//	private RSLocator rslocator;
//	private boolean isClosed=false;
//	
//	/**
//	 * Instantiates the <tt>DataBridge</tt>.
//	 */
//	public RSBlobDataBridge(){
//		try {
//			RSBLOBWriter writer = RSBLOBWriter.getRSBLOBWriter();
//			this.writer=writer;
//			RSLocator locator = writer.getRSLocator(new RSResourceLocalType());
//			this.rslocator=locator;
//			RSBLOBReader reader = RSBLOBReader.getRSBLOBReader(locator);
//			RSBLOBIterator iterator = reader.getRSIterator();
//			this.rsiterator=iterator;
//		} catch (Exception e) {
//			log.error("Could not create local RS writer - reader",e);
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
//		ResultSetDataElement rsElement = (ResultSetDataElement)rsiterator.next(ResultSetDataElement.class);
//		if(rsElement==null){
//			return null;
//		}
//		return rsElement.getDataElement();
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#nextContentType()
//	 * @return the next element's <tt>ContentType</tt> from the <tt>DataSource</tt>.
//	 */
//	public ContentType nextContentType() {
//		ResultSetDataElement rsElement = (ResultSetDataElement)rsiterator.next(ResultSetDataElement.class);
//		if(rsElement==null){
//			return null;
//		}
//		return rsElement.getDataElement().getContentType();
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#append(org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement)
//	 * @param element {@link DataElement} to be appended to this <tt>DataSink</tt>
//	 */
//	public void append(DataElement element) {
//		if(element==null)return;
//		ResultSetDataElement rsElement;
//		try {
//			rsElement = new ResultSetDataElement(element);
//			writer.addResults(rsElement);
//		} catch (Exception e) {
//			log.error("Could not append object.",e);
//		}
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
//	 */
//	public void close() {
//		try {
//			if(!isClosed && writer!=null){
//				isClosed=true;
//				writer.close();
//			}
//		} catch (Exception e) {
//			log.error("Could not close rsblobwriter",e);
//		}
//	}
//
//	/* Generally the getOutput method won't be used in bridges... */
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#getOutput()
//	 * @return null
//	 */
//	public String getOutput() {
//		if(this.rslocator==null)return null;
//		return this.rslocator.getLocator();
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
//}
