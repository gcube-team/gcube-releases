//package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;
//
//import java.net.URI;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.gcube.contentmanagement.contentmanager.stubs.model.protocol.URIs;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.DocumentReader;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.DocumentProjection;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.Projections;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeDocument;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeDataSource;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.handlers.CMSUtils;
//import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
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
// * Reads a compound object from cms.
// * </p>
// */
//public class CompCObjectDataSource implements DataSource, ContentTypeDataSource {
//
//	private String objectID;
//	
//	private DocumentReader cmReader;
//
//	/**
//	 * @param input The input value of the <tt>DataSource</tt>.
//	 * @param inputParameters The input parameters of the <tt>DataSource</tt>.
//	 */
//	public CompCObjectDataSource(String input, Parameter[] inputParameters) {
//		String objectURI = input;
//		try {
//			String collectionID = URIs.collectionID(new URI(objectURI));
//			cmReader = new DocumentReader(collectionID, DTSSManager.getScope(),
//					DTSSManager.getSecurityManager());
//			this.objectID = URIs.documentID(new URI(objectURI));;
//		} catch (Exception e) {
//			log.error("Could not initialize data source handler",e);
//		}
//	}
//	
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#hasNext()
//	 * @return true if the <tt>DataSource</tt> has more elements.
//	 */
//	public boolean hasNext() {
//		return !isClosed;
//	}
//	
//	private static Logger log = LoggerFactory.getLogger(CObjectDataSource.class);
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#next()
//	 * @return the next element of the <tt>DataSource</tt>.
//	 */
//	public DataElement next() {
//		if (isClosed)
//			return null;
//		try {
//			close();
//			DocumentProjection dp = Projections.document();
//			GCubeDocument gdoc = cmReader.get(objectID, dp);
//			DataElement object = CMSUtils.getCompoundDataElementFromCM(gdoc, DTSSManager.getScope());
//			ReportManager.manageRecord(objectID, "Object with id "+objectID+" was downloaded successfully by CMS", Status.SUCCESSFUL, Type.SOURCE);
//			return object;
//		} catch (Exception e) {
//			log.error("Could not manage to fetch the object "+objectID,e);
//			close();
//			ReportManager.manageRecord(objectID, "Object with id "+objectID+" could not be fetched by CMS", Status.FAILED, Type.SOURCE);
//		}
//		return null;
//	}
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
//	 */
//	public void close() {
//		isClosed=true;
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
//	public ContentType nextContentType() {
//		if (isClosed)
//			return null;
//		try {
//			close();
//			DocumentProjection dp = Projections.document().with(Projections.MIME_TYPE);
//			GCubeDocument gdoc = cmReader.get(objectID, dp);
//			
//			ReportManager.manageRecord(objectID, "Object's content type with id "+objectID+" was retrived successfully", Status.SUCCESSFUL, Type.SOURCE);
//	
//			return CMSUtils.getContentTypeOfObject(gdoc);
//		} catch (Exception e) {
//			log.error("Could not manage to fetch the object "+objectID,e);
//			close();
//			ReportManager.manageRecord(objectID, "Object with id "+objectID+" could not be fetched by CMS", Status.FAILED, Type.SOURCE);
//		}
//		return null;
//	}
//}
