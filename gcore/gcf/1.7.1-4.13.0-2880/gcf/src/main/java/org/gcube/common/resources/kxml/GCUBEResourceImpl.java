package org.gcube.common.resources.kxml;

import java.io.InputStream;

import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

	/**
	 * Describes the parsing behaviour for all implementations of {@link org.gcube.common.core.resources.GCUBEResource GCUBEResources}.  
	 * @author Fabio Simeoni (University of Strathclyde)
	 *
	 */
	public interface GCUBEResourceImpl {
		public void load(KXmlParser parser) throws Exception;
		public void store(KXmlSerializer serializer) throws Exception;
		public InputStream getSchemaResource() throws Exception;
	}
