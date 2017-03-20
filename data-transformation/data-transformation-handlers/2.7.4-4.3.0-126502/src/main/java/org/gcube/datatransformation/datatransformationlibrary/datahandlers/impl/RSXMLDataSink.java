//package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.StrDataElement;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandlerDefinitions;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
//import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
//import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
//import org.gcube.common.searchservice.searchlibrary.resultset.elements.RecordAttribute;
//import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementGeneric;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceWSRFType;
//import org.gcube.common.searchservice.searchlibrary.rswriter.RSXMLWriter;
//
///**
// * @author Dimitris Katris, NKUA
// *
// * <p>
// * This <tt>DataSink</tt> stores <tt>DataElements</tt> to a result set with xml elements.
// * </p>
// */
//public class RSXMLDataSink implements DataSink {
//	
//	private static Logger log = LoggerFactory.getLogger(RSXMLDataSink.class);
//	
//	private RSXMLWriter writer;
//	
//	/**
//	 * This constructor is used when DTS is instantiating a new AlternativeRepresentationDataSink.
//	 * 
//	 * @param output The output value of the <tt>DataSink</tt>.
//	 * @param outputParameters The output parameters of the <tt>DataSink</tt>.
//	 * @throws Exception If the result set could not be created.
//	 */
//	public RSXMLDataSink(String output, Parameter[] outputParameters) throws Exception {
//		//TODO: OutputParameter that denotes if the payload of the result elements should be wrapped with MM envelope...
//		writer = RSXMLWriter.getRSXMLWriter();
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
//	public void append(DataElement dataElement) {
//		/* Wrap the element's payload in a new ResultElementGeneric */
//		ResultElementGeneric rsElem = null;
//		try {
//			String payload;
//			if(dataElement instanceof StrDataElement){
//				payload = ((StrDataElement)dataElement).getStringContent();
//			}else{
//				payload = stringFromInputStream(dataElement.getContent());
//			}
//			
//			rsElem = new ResultElementGeneric("foo", "bar", payload);
//		} catch (Exception e) {
//			log.error("Failed to create ResultSet element", e);return;
////			throw new Exception("Failed to create ResultSet element.", e);
//		}
//
//		/* Setting as docID the content id. It should be for forward indexes */
//		dataElement.setAttribute(ResultElementGeneric.RECORD_ID_NAME, dataElement.getAttributeValue(DataHandlerDefinitions.ATTR_CONTENT_OID));
//		/* Setting as collID the content id. It should be for forward indexes */
//		dataElement.setAttribute(ResultElementGeneric.RECORD_COLLECTION_NAME, dataElement.getAttributeValue(DataHandlerDefinitions.ATTR_METADATACOL_ID));
//		/* Setting as RankID the content id. It should be for forward indexes */
//		dataElement.setAttribute(ResultElementGeneric.RECORD_RANK_NAME, "1.0");
//
//		/* Add every attribute as a ResultSet attribute */
//		try {
//			int i = 0;
//			Map<String,String> attributes = dataElement.getAllAttributes();
//			RecordAttribute[] rsAttrs = new RecordAttribute[attributes.size()];
//			for (String attrName : attributes.keySet()) {
//				log.debug("Setting record attribute: "+attrName+" - "+attributes.get(attrName));
//				rsAttrs[i] = new RecordAttribute(attrName, attributes.get(attrName));
//				i++;
//			}
//			rsElem.setRecordAttributes(rsAttrs);
//		} catch (Exception e) {
//			log.error("Failed to set attributes on the new ResultSet element.", e);return;
////			throw new Exception("Failed to set attributes on the new ResultSet element.", e);
//		}
//		
//		/* Finally write the new element */
//		try {
//			writer.addResults(rsElem);
//			log.debug("Wrote record #" + dataElement.getId());
//		} catch (Exception e) {
//			log.error("Failed to add the new element to the output ResultSet", e);
////			throw new Exception("Failed to add the new element to the output ResultSet", e);
//		}
//		
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
//	 */
//	public void close() {
//		try {
//			isClosed=true;
//			// TODO there is a double close of the ResultSet
//			// one from the executor and one from the program
//			// The correct behavour is to have only the program close the RS
//			writer.close();
//			ReportManager.closeReport();
//		} catch (Exception e) {
//			log.error("Could not close RSXMLWriter ", e);
//		}
//		
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#getOutput()
//	 * @return The output of the transformation.
//	 */
//	public String getOutput() {
//		try {
//			return writer.getRSLocator(new RSResourceWSRFType()).getLocator();
//		} catch (Exception e) {
//			log.error("Did not manage to create the RS Locator", e);
//			return null;
//		}
//	}
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
//}
