//package org.gcube.datatransformation.datatransformationlibrary.contenttypeevaluators.impl;
//
//import java.net.URI;
//
//import org.gcube.contentmanagement.contentmanager.stubs.model.protocol.URIs;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.DocumentReader;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.DocumentProjection;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.Projections;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeDocument;
//import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeEvaluator;
//import org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.handlers.CMSUtils;
//import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
//import org.gcube.datatransformation.datatransformationlibrary.security.DTSSManager;
//
///**
// * @author Dimitris Katris, NKUA
// *
// * <p>
// * Evaluates the {@link ContentType} of the {@link DataElement} with id <tt>dataElementID</tt>.
// * </p>
// */
//public class CMSContenTypeEvaluator implements ContentTypeEvaluator {
//
//	/**
//	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeEvaluator#evaluateContentTypeOfDataElement(java.lang.String)
//	 * @param dataElementID The id of the {@link DataElement}.
//	 * @return The {@link ContentType} of the {@link DataElement}.
//	 * @throws Exception If the evaluator did not manage to evaluate the {@link ContentType}.
//	 */
//	public ContentType evaluateContentTypeOfDataElement(String dataElementID) throws Exception {
//		//Get URI of object
//		URI objURI = new URI(dataElementID);
//		//Get object ID
//		String objectID = URIs.documentID(objURI);
//		//Get collection ID
//		String collectionID = URIs.collectionID(objURI);
//		DocumentReader cmReader = new DocumentReader(collectionID, DTSSManager.getScope(), DTSSManager.getSecurityManager());
//		DocumentProjection dp = Projections.document();
//		GCubeDocument document = cmReader.get(objectID, dp);
//		return CMSUtils.getContentTypeOfObject(document);
//	}
//	
//}
