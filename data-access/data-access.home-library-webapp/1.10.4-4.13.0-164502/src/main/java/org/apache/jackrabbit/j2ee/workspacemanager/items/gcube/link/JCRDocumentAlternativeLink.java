package org.apache.jackrabbit.j2ee.workspacemanager.items.gcube.link;

import org.gcube.common.homelibary.model.items.gcube.DocumentAlternativeLink;


public class JCRDocumentAlternativeLink implements DocumentAlternativeLink {
	
	private final String parentURI;
	private final String uri;
	private final String name;
	private final String mimeType;

	public JCRDocumentAlternativeLink(String parentURI, String uri,
			String name, String mimeType) {
		super();
		this.parentURI = parentURI;
		this.uri = uri;
		this.name = name;
		this.mimeType = mimeType;
	}

	@Override
	public String getParentURI() {
		return parentURI;
	}

	@Override
	public String getURI() {
		return uri;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}


}
