//package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
//import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
//import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.utils.ResultSetDataElement;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceWSRFType;
//import org.gcube.common.searchservice.searchlibrary.rswriter.RSBLOBWriter;
//
///**
// * @author Dimitris Katris, NKUA
// *
// * <p>
// * This <tt>DataSink</tt> stores <tt>DataElements</tt> to a result set with blob elements.
// * </p>
// */
//public class RSBlobDataSink implements DataSink{
//
//	private static Logger log = LoggerFactory.getLogger(RSBlobDataSource.class);
//	
//	private RSBLOBWriter writer;
//	
//	/**
//	 * This constructor is used when DTS is instantiating a new RSBlobDataSink.
//	 * The output and outputParameters are not used in this DataSink.
//	 * 
//	 * @param output The output value of the <tt>DataSink</tt>.
//	 * @param outputParameters The output parameters of the <tt>DataSink</tt>.
//	 */
//	public RSBlobDataSink(String output, Parameter[] outputParameters){
//		try {
//			RSBLOBWriter writer = RSBLOBWriter.getRSBLOBWriter();
//			this.writer=writer;
//		} catch (Exception e) {
//			log.error("Could not create RSBlobWriter",e);
//		}
//	}
//	
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#getOutput()
//	 * @return The output of the transformation.
//	 */
//	public String getOutput(){
//		try {
//			return writer.getRSLocator(new RSResourceWSRFType()).getLocator();
//		} catch (Exception e) {
//			log.error("Could not get RS Locator",e);
//		}
//		return null;
//	}
//	
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#append(org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement)
//	 * @param element {@link DataElement} to be appended to this <tt>DataSink</tt>
//	 */
//	public void append(DataElement element) {
//		if(element==null || isClosed)return;
//		ResultSetDataElement rsElement;
//		try {
//			log.debug("Inserting element with id "+element.getId());
//			rsElement = new ResultSetDataElement(element);
//			writer.addResults(rsElement);
//		} catch (Exception e) {
//			log.error("Could not append object to result set",e);
//		}
//	}
//
//	private boolean isClosed=false;
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
//	 */
//	public void close() {
//		try {
//			isClosed=true;
//			ReportManager.closeReport();
//			writer.close();
//		} catch (Exception e) {
//			log.error("Could not close writer",e);
//		}
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#isClosed()
//	 * @return true if the <tt>DataHandler</tt> has been closed.
//	 */
//	public boolean isClosed() {
//		return isClosed;
//	}
//}
