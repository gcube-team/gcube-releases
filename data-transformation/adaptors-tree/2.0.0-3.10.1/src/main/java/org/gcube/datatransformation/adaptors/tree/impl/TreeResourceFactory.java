package org.gcube.datatransformation.adaptors.tree.impl;

import org.gcube.datatransformation.adaptors.common.xmlobjects.TreeResource;
import org.gcube.rest.commons.resourceawareservice.resources.ResourceFactory;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;

public class TreeResourceFactory extends ResourceFactory<TreeResource> {

	@Override
	public TreeResource createResource(String resourceID, String resourceAsXML) throws StatefulResourceException {
		TreeResource treeResource = new TreeResource();
		return treeResource;
	}

	@Override
	public String getScope() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
}
