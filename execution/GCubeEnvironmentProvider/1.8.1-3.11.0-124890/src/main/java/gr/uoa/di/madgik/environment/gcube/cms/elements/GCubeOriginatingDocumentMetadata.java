package gr.uoa.di.madgik.environment.gcube.cms.elements;

import java.io.InputStream;
import java.net.URI;

import gr.uoa.di.madgik.environment.cms.elements.metadata.DocumentMetadata;

public class GCubeOriginatingDocumentMetadata extends DocumentMetadata {

	@Override
	public InputStream ResolveContent() throws Exception {
		if(contentLocator == null) return null;
		return new URI(contentLocator).toURL().openStream();
	}
	
}
