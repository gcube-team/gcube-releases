package org.gcube.application.framework.contentmanagement.util;

import static java.util.Locale.*;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import org.gcube.application.framework.contentmanagement.exceptions.ViewPublishingException;
import org.gcube.application.framework.core.session.ASLSession;
//import org.gcube.common.core.faults.GCUBEException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewManagementUtils {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(ViewManagementUtils.class);
	
	/*public static String createMetadataView(String collectionId, String name, String description, String schemaName, Locale schemaLanguage, String schemaURI, ASLSession session) throws ViewPublishingException {
		MetadataView view = new MetadataView(session.getScope());
		view.setCollectionId(collectionId);
		view.setName(name);
		view.setDescription(description);
		try {
			view.setProjection(schemaLanguage, schemaName, new URI(schemaURI));
			
			// publish the view
			try {
				view.publish();
				return view.id();
			} catch (GCUBEException e) {
				throw new ViewPublishingException(e);
			} catch (IllegalStateException e) {
				throw new ViewPublishingException(e);
			} catch (Exception e) {
				throw new ViewPublishingException(e);
			}
		} catch (URISyntaxException e) {
			throw new ViewPublishingException(e);
		}
	}*/
	
	
	/*public static void addGCubeMetadataForDocument(String documentId, String collectionId, String schemaName, Locale schemaLanguage, String schemaURI, InputStream payload, ASLSession session) {
		GCubeMetadata meta = new GCubeMetadata();
		try {
			meta.setSchemaName(schemaName);
			meta.setLanguage(schemaLanguage);
			meta.setSchemaURI(new URI(schemaURI));
			meta.setBytestream(payload);
			
			DocumentWriter cmWriter = new DocumentWriter(collectionId, session.getScope());
			
			DocumentReader cmReader = new DocumentReader(collectionId, session.getScope());
			DocumentProjection dp = Projections.document().with(Projections.NAME);
			
			GCubeDocument document = cmReader.get(documentId, dp);
			document.metadata().add(meta);
			cmWriter.update(document);
		} catch (Exception e) {
			logger.error("Exception:", e);
		}
	}*/

}
