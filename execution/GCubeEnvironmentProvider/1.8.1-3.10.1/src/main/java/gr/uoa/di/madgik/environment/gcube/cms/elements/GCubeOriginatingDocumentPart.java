package gr.uoa.di.madgik.environment.gcube.cms.elements;

import java.io.InputStream;
import java.net.URI;

import gr.uoa.di.madgik.environment.cms.elements.part.DocumentPart;

public class GCubeOriginatingDocumentPart extends DocumentPart {

	@Override
	public InputStream ResolveContent() throws Exception {
		if(contentLocator == null) return null;
		return new URI(contentLocator).toURL().openStream();
	}
	
}
