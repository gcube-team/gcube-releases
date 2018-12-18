package org.apache.jackrabbit.j2ee.workspacemanager.items.gcube.link;

import org.gcube.common.homelibary.model.items.gcube.DocumentPartLink;

public class JCRDocumentPartLink implements DocumentPartLink {

	private final String parentOid;
	private final String oid;
	private final String name;
	private final String mimeType;
	
	
	
	public JCRDocumentPartLink(String parentOid, String oid, String name,
			String mimeType) {
		super();
		this.parentOid = parentOid;
		this.oid = oid;
		this.name = name;
		this.mimeType = mimeType;
	}

	@Override
	public String getParentURI() {
		return parentOid;
	}

	@Override
	public String getURI() {
		return oid;
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
