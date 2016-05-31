package org.gcube.datatransformation.adaptors.common.db.discover;

import java.util.Set;

import org.gcube.datatransformation.adaptors.common.db.xmlobjects.DBProps;



public interface DBPropsDiscovererAPI <T extends DBProps> {


	Set<String> discoverDBServiceRunningInstances(String scope);

	
	
}
