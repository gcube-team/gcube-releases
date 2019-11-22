package org.apache.jackrabbit.j2ee.workspacemanager.items.gcube;

import org.gcube.common.homelibary.model.items.gcube.DocumentMetadata;


public class JCRDocumentMetadata implements DocumentMetadata {

	
	private final String schemaName;
	private final String xml;
	
	public JCRDocumentMetadata(String schemaName, String xml) { 
		
		this.schemaName = schemaName;
		this.xml = xml;
	}

	@Override
	public String getSchemaName() {
		return schemaName;
	}

	@Override
	public String getXML() throws Exception {
		return xml;
	}

}
