package org.gcube.soa3.connector;

import java.util.List;

public interface RolesLoader 
{
	public List<String> loadRoles (String user, String organization);
	
	public void setSoa3Endpoint(String soa3Endpoint);
}
