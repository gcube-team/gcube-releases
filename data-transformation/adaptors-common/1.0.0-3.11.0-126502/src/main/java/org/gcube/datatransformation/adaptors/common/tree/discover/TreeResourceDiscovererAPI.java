package org.gcube.datatransformation.adaptors.common.tree.discover;

import java.util.Set;

import org.gcube.datatransformation.adaptors.common.xmlobjects.TreeResource;



public interface TreeResourceDiscovererAPI <T extends TreeResource> {

	Set<String> discoverTreeServiceRunningInstances(String scope);
	
}
