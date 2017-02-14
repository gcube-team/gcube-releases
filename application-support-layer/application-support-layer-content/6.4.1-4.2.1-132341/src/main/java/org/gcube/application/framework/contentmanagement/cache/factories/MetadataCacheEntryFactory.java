package org.gcube.application.framework.contentmanagement.cache.factories;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.gcube.application.framework.core.util.CacheEntryConstants;
import org.gcube.application.framework.core.util.QueryString;
//import org.gcube.common.core.informationsystem.ISException;
//import org.gcube.common.core.scope.GCUBEScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.constructs.blocking.CacheEntryFactory;


/**
 * @author Valia Tsagkalidou (NKUA)
 *
 */
public class MetadataCacheEntryFactory implements CacheEntryFactory {

	/**
	 * An atomic integer to get the CMS EPRs round-robin
	 */
	protected static AtomicInteger mcId = new AtomicInteger(0);

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(MetadataCacheEntryFactory.class);
	
	/**
	 * @param key a QueryString representing pairs of keys and values. Needed keys are "vre", "metadataColID", "oid"
	 * @return  the metadata object of the corresponding ID
	 */
	public Object createEntry(Object key) throws Exception {
		/*QueryString query = (QueryString) key;
		String scope = query.get(CacheEntryConstants.vre);
		String metaColId = query.get(CacheEntryConstants.metadataColID);
		String oid = query.get(CacheEntryConstants.oid);
		
		MetadataView mView = new MetadataView(GCUBEScope.getScope(scope));
		mView.setId(metaColId);
		List<MetadataView> similars = null;
		try {
			similars = mView.findSimilar();
		} catch (ISException e1) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e1);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e1);
		}
		
		if (similars != null && !similars.isEmpty()) {
			MetadataView mv = similars.get(0);
			try {
				ViewReader vReader = mv.reader();
				GCubeDocument metaPayloads = vReader.get(oid, metadata().with(BYTESTREAM));
				
				return new String(metaPayloads.bytestream());
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				logger.error("Exception:", e);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception:", e);
			}
		}*/
		return null;
//		QueryString query = (QueryString) key;
//		
//		try {
//			MetadataManagerPortType metaManager = null;
//			EndpointReference[] mcURIs = RIsManager.getInstance().getISCache(GCUBEScope.getScope(query.get(CacheEntryConstants.vre))).getEPRsFor("MetadataManagement", "MetadataManager", SrvType.FACTORY.name());
//			logger.info("Number of available mms factories: " + mcURIs.length);
//			for(int i=0; i < mcURIs.length; i++)
//			{            
//				try {
//	                MetadataManagerServiceAddressingLocator instanceLocator = new MetadataManagerServiceAddressingLocator();		
//	                MetadataManagerFactoryPortType mcFactory = null;
//	          		EndpointReferenceType endpoint = new EndpointReferenceType();
//	          		endpoint.setAddress(new Address(mcURIs[mcId.getAndIncrement()%mcURIs.length].getAddress().toString()));
//	          		MetadataManagerFactoryServiceAddressingLocator mcflocator = new MetadataManagerFactoryServiceAddressingLocator();
//	          		mcFactory = mcflocator.getMetadataManagerFactoryPortTypePort(endpoint);
//	          		mcFactory = ServiceContextManager.applySecurity(mcFactory, GCUBEScope.getScope(query.get(CacheEntryConstants.vre)), ApplicationCredentials.getInstance().getCredential(query.get(CacheEntryConstants.vre)));
//	                CreateManagerResponse createResponsefromcollection = mcFactory.createManagerFromCollection(query.get(CacheEntryConstants.metadataColID));
//	                EndpointReferenceType instanceEPR = createResponsefromcollection.getEndpointReference();
//	                metaManager = instanceLocator.getMetadataManagerPortTypePort(instanceEPR);
//	                break;
//				}
//				catch (Exception e) {
//					logger.error("",e);
//				}
//			}
//			
//			try {
//				metaManager = ServiceContextManager.applySecurity(metaManager, GCUBEScope.getScope(query.get(CacheEntryConstants.vre)), ApplicationCredentials.getInstance().getCredential(query.get(CacheEntryConstants.vre)));
//			} catch (Exception e) {
//				logger.error("",e);
//			}
//			logger.info("get metadata for oid: " + query.get(CacheEntryConstants.oid) + " and metadta col ID: " + query.get(CacheEntryConstants.metadataColID));
//			String[] oids = new String[1];
//			oids[0] = query.get(CacheEntryConstants.oid);
//			InformationObjectIDList oidsList = new InformationObjectIDList(oids);
//			GetElementsResponse elements = metaManager.getElements(oidsList);
//			for(int i=0; i < elements.getGetElementsItemResponse().length; i++)
//			{
//				GetElementItemResponse element = elements.getGetElementsItemResponse(i);
//				MetadataObjectList moList = element.getMetadataObjectList();
//				if (moList != null) {
//					for(int j=0; j < moList.getMetadataObject().length; j++)
//					{
//						String metadataXML = moList.getMetadataObject(0);
//						logger.debug("metadata:" + metadataXML);
//						if(metadataXML != null)
//							return metadataXML;
//					}
//				}
//			}
//		} catch (Exception e) {
//			logger.error("",e);
//		}
//		return null;
	}

}
