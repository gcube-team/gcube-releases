package org.gcube.datatransformation.adaptors.common.oaipmh.discover;

import java.util.Set;

import org.gcube.datatransformation.adaptors.common.xmlobjects.OAIPMHConfig;


public interface OAIPMHConfigDiscovererAPI <T extends OAIPMHConfig> {

	public Set<String> discoverOAIPMHServiceRunningInstances(String scope);
	
}