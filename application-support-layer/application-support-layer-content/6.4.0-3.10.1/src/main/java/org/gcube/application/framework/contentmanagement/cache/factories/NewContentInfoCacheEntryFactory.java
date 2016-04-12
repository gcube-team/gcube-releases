package org.gcube.application.framework.contentmanagement.cache.factories;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

import org.gcube.application.framework.contentmanagement.util.ElementTypeConstants;
import org.gcube.application.framework.core.util.CacheEntryConstants;
import org.gcube.application.framework.core.util.QueryString;
//import org.gcube.common.core.faults.GCUBEException;
//import org.gcube.common.core.scope.GCUBEScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewContentInfoCacheEntryFactory implements CacheEntryFactory {

	/**
	 * An atomic integer to get the CMS EPRs round-robin
	 */
	protected static AtomicInteger cmsId = new AtomicInteger(0);

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(NewContentInfoCacheEntryFactory.class);

	/**
	 * @param key a QueryString representing pairs of keys and values: needed keys are "vre" and "oid"
	 * @return  a DocumentDescription as it is returned from the CMS. The content is not transfered for storage and efficiency reasons
	 */
	public Object createEntry(Object key) throws Exception {
		/*logger.info("New content info!!");
		logger.info("*************************************************************************************************************");
		QueryString query = (QueryString) key;
		GCubeDocument doc = null;
		try {
		if(query.containsKey(CacheEntryConstants.vre) && query.containsKey(CacheEntryConstants.oid) && query.containsKey("cid") || query.containsKey("uri")) {
			logger.info("ALL GOOD");
			//get also the element type
			String elementType = query.get("elementType");
			
			// we instantiate the CMReader
			String collectionId = query.get("cid");
			String scope = query.get(CacheEntryConstants.vre);
			String oid = query.get(CacheEntryConstants.oid);
			DocumentReader reader = null;
			try {
				reader = new DocumentReader(collectionId, GCUBEScope.getScope(scope));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception:", e);
			}
			
			logger.info("Trying to get description for: " + elementType);
			
			if (elementType == null || elementType.equals("") || elementType.equals(ElementTypeConstants.mainDoc)) {
				
				try {
					doc = reader.get(oid, document().allexcept(BYTESTREAM));
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
				
			} else if (elementType.equals(ElementTypeConstants.alternativeRep)) {
				AlternativeProjection alP = alternative();
				String uri = query.get("uri");
				URI Uri = new URI(uri);
				logger.info("Getting description for alternative: " + uri);
				GCubeAlternative alternative = (GCubeAlternative) reader.resolve(Uri, alP);
				if (alternative == null) 
					logger.info("Result alt: null");
				else
					logger.info("Result alt not null");
				return alternative;
			} else if (elementType.equals(ElementTypeConstants.annotation)) {
				String uri = query.get("uri");
				AnnotationProjection ap = annotation();
				URI Uri = new URI(uri);
				GCubeAnnotation annotation = (GCubeAnnotation) reader.resolve(Uri, ap);
				return annotation;
			} else if (elementType.equals(ElementTypeConstants.metadata)) {
				String uri = query.get("uri");
				MetadataProjection mp = metadata();
				URI Uri = new URI(uri);
				GCubeMetadata metadata = (GCubeMetadata) reader.resolve(Uri, mp);
				return metadata;
			} else if (elementType.equals(ElementTypeConstants.part)) {
				String uri = query.get("uri");
				PartProjection pp = part();
				URI Uri = new URI(uri);
				GCubePart part = (GCubePart) reader.resolve(Uri, pp);	
				return part;
			}
		} else {
			logger.info("There is something wrong in parameters!");
		}
		} catch (Exception e) {
			logger.error("Exception:", e);
		}
		return doc;*/
		return null;
	}
}
