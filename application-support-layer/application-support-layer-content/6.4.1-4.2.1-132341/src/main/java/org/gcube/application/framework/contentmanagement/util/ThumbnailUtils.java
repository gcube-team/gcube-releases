package org.gcube.application.framework.contentmanagement.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Random;

//import javax.xml.rpc.ServiceException;

//import org.apache.axis.message.addressing.Address;
//import org.apache.axis.message.addressing.EndpointReference;
//import org.apache.axis.message.addressing.EndpointReferenceType;
//import org.apache.axis.types.URI.MalformedURIException;
import org.gcube.application.framework.contentmanagement.cache.factories.NewContentInfoCacheEntryFactory;
import org.gcube.application.framework.contentmanagement.content.impl.DigitalObject;
import org.gcube.application.framework.core.util.ServiceType;
import org.gcube.application.framework.core.util.ServiceUtils;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.gcube.resources.discovery.icclient.ICFactory.*;

public class ThumbnailUtils {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(ThumbnailUtils.class);
	
	public final static int chunkSize = 1024*1024;
	public static String getContent(String oid, String scope) throws Exception {
		/*URI contentURI = new URI(oid);
		String collectionId = URIs.collectionID(contentURI);
		String contentId = URIs.documentID(contentURI);
		DocumentReader reader = null;
		GCubeDocument doc = null;
		try {
			reader = new DocumentReader(collectionId, GCUBEScope.getScope(scope));
			doc = reader.get(contentId, document().with(opt(BYTESTREAM)));
		} catch (UnknownDocumentException e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		} catch (DiscoveryException e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		} catch (GCUBEException e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		}
		if (doc.bytestream() != null) {
			byte[] bytes = doc.bytestream();
			return bytes;
		}
		return null; */
		
		return DigitalObject.getContent(oid, scope);
		
//		CMSPortType1PortType cms = getCMS(scope);
//		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
//		int tries=0;
//		try {
//			log.info("I will try to retrieve the object with id "+oid+".");
//			//Configuring the parameters to get the doc...
//			GetDocumentParameters getDocumentParams = new GetDocumentParameters();
//			getDocumentParams.setDocumentID(oid);
//			getDocumentParams.setTargetFileLocation(BasicInfoObjectDescription.RAW_CONTENT_IN_MESSAGE);
//			
//			for(int count=0;;count++){
//				Hint[] hints = new Hint[2];
//				hints[0] = new Hint();
//				hints[0].setName(BasicStorageHints.HINT_NAME_READING_START_OFFSET);
//				hints[0].setValue(String.valueOf(count*chunkSize));
//				hints[1] = new Hint();
//				hints[1].setName(BasicStorageHints.HINT_NAME_LIMIT_CONTENT_LENGTH_READ);
//				hints[1].setValue(String.valueOf(chunkSize));
//				getDocumentParams.setHints(hints);
//				try {
//	//				Invoking CMS to get the document description here...				
//					DocumentDescription document = cms.getDocument(getDocumentParams);
//	
//					byte[] buffer = document.getRawContent();
//					if(buffer == null)
//						break;
//					outstream.write(buffer);
//					outstream.flush();
//					if(buffer.length < chunkSize)
//						break;
//				} catch(RemoteException e) {
//					if(tries<10){
//						log.error("Caught a remote exception trying again...",e);
//						tries++;
//						count--;
//					}else{
//						log.error("Abording downloading.");
//						throw new Exception("Abording downloading tries = 10");
//					}
//				} catch(Exception e1) {
//					log.error("Undefined exception in getDataElementFromCMS()",e1);
//					throw new Exception("Undefined exception in getDataElementFromCMS()");
//				}
//				log.trace("I have retrieve the "+count+" chunk for object "+oid);
//			}
//			log.debug("The size of the outstream of object "+oid+" is "+outstream.size());
//			outstream.close();
//		} catch (IOException e) {
//			log.error("IOException in getDataElementFromCMS()",e);
//			throw new Exception("IOException in getDataElementFromCMS()");
//		} 
//		if(outstream==null || outstream.size()<1){
//			throw new Exception("Did not manage to download the content of object "+oid+" from cms");
//		}
//		log.debug("Downloading the OID "+oid+" completed successfully.");
//		return outstream.toByteArray();
	}
	
	/*	public static String getMimeType(String oid, String scope) throws Exception {
		URI uri = new URI(oid);
		String collectionId = URIs.collectionID(uri);
		String documentId = URIs.documentID(uri);
		
		try {
			QueryString query = new QueryString();
			query.addParameter(CacheEntryConstants.vre, scope);
			query.addParameter(CacheEntryConstants.oid, oid);
			query.addParameter("cid", collectionId);
			GCubeDocument docDescription = (GCubeDocument) CachesManager.getInstance().getEhcache(org.gcube.application.framework.contentmanagement.util.CacheEntryConstants.newContentCache, new NewContentInfoCacheEntryFactory()).get(query).getObjectValue();
			return docDescription.mimeType();
		}
		catch (Exception e) {
			logger.info("An error occured while trying to retrieve document's mime type from cache");
			return "unknown/unknown";
		}
		return "XML_URL";
	}*/
	
//	public static CMSPortType1PortType getCMS(String vre) throws MalformedURIException,
//	ServiceException {
//		EndpointReference[] cmsURIs = null;
//		try {
//			
//				cmsURIs = RIsManager.getInstance().getISCache(GCUBEScope.getScope(vre)).getEPRsFor("ContentManagement","ContentManagementService",SrvType.SIMPLE.name());
//		} catch (Exception e2) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e2);
//		}
//		boolean excep = true;
//		String cmsAddress;
//		Random random = new Random();
//		cmsAddress = cmsURIs[random.nextInt(cmsURIs.length)].getAddress().toString();
//		CMSPortType1PortType cms;
//		EndpointReferenceType endpoint = new EndpointReferenceType();
//		endpoint.setAddress(new Address(cmsAddress));
//		CMSPortType1ServiceAddressingLocator cmslocator = new CMSPortType1ServiceAddressingLocator();
//		cms = cmslocator.getCMSPortType1PortTypePort(endpoint);
//		try {
//			cms = ServiceContextManager.applySecurity(cms, GCUBEScope.getScope(vre), ApplicationCredentials.getInstance().getCredential(vre));
//		} catch (MalformedScopeExpressionException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		return cms; 
//	}
		
	
//	public static DataTransformationServicePortType getDTSPortType(String scope) throws Exception {
//		String dtsAddress = null;
//		try {
//			dtsAddress = ServiceUtils.getEprAddressOfService("DataTransformation", "DataTransformationService", ServiceType.SIMPLE.name(), scope);
////			dtsURIs = RIsManager.getInstance().getISCache(GCUBEScope.getScope(scope)).getEPRsFor("DataTransformation", "DataTransformationService", SrvType.SIMPLE.name());
//		} catch (Exception e) {
//			logger.error("Exception:", e);
//		}
//		
//		DataTransformationServicePortType dts = null;
//		EndpointReferenceType endpoint = new EndpointReferenceType();
//		endpoint.setAddress(new Address(dtsAddress));
//		DataTransformationServiceAddressingLocator dtsLocator = new DataTransformationServiceAddressingLocator();
//		dts = dtsLocator.getDataTransformationServicePortTypePort(endpoint);
////		try {
////			dts = ServiceContextManager.applySecurity(dts, scope, ApplicationCredentials.getInstance().getCredential(scope));
////		} catch (MalformedScopeExpressionException e) {
////			logger.error("Exception:", e);
////		} catch (Exception e) {
////			logger.error("Exception:", e);
////		}
//		return dts;
//		
//	}
	
	

//	public static DataTransformationServicePortType getDTSPortTypeOLD(String scope) throws Exception {
//		List<GCUBERunningInstance> result = null;
//		
//		EndpointReference[] dtsURIs = null;
//		try {
//			dtsURIs = RIsManager.getInstance().getISCache(GCUBEScope.getScope(scope)).getEPRsFor("DataTransformation", "DataTransformationService", SrvType.SIMPLE.name());
//		} catch (Exception e) {
//			logger.error("Exception:", e);
//		}
//		
//		String dtsAddress;
//		Random random = new Random();
//		
//		
//		dtsAddress = dtsURIs[random.nextInt(dtsURIs.length)].getAddress().toString();
//		
//		DataTransformationServicePortType dts;
//		EndpointReferenceType endpoint = new EndpointReferenceType();
//		endpoint.setAddress(new Address(dtsAddress));
//		DataTransformationServiceAddressingLocator dtsLocator = new DataTransformationServiceAddressingLocator();
//		dts = dtsLocator.getDataTransformationServicePortTypePort(endpoint);
//		try {
//			dts = ServiceContextManager.applySecurity(dts, GCUBEScope.getScope(scope), ApplicationCredentials.getInstance().getCredential(scope));
//		} catch (MalformedScopeExpressionException e) {
//			logger.error("Exception:", e);
//		} catch (Exception e) {
//			logger.error("Exception:", e);
//		}
//		return dts;
//	}
	
	
	/*public static GCubeDocument getDocumentDescription(String oid, String scope) throws Exception {
		QueryString newQuery = new QueryString();
		newQuery.addParameter(CacheEntryConstants.vre, scope);
		newQuery.addParameter(CacheEntryConstants.oid, oid);
		GCubeDocument docDescription = (GCubeDocument) CachesManager.getInstance().getEhcache(org.gcube.application.framework.contentmanagement.util.CacheEntryConstants.contentCache, new NewContentInfoCacheEntryFactory()).get(newQuery).getValue();
		return docDescription;
	}*/
}
