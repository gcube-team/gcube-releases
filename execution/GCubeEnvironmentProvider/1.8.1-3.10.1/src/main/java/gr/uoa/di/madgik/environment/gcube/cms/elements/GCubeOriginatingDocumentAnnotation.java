package gr.uoa.di.madgik.environment.gcube.cms.elements;

import java.io.InputStream;
import java.net.URI;

import gr.uoa.di.madgik.environment.cms.elements.annotation.DocumentAnnotation;

public class GCubeOriginatingDocumentAnnotation extends DocumentAnnotation {

	@Override
	public InputStream ResolveContent() throws Exception {
		if(contentLocator == null) return null;
		return new URI(contentLocator).toURL().openStream();
	}
}
