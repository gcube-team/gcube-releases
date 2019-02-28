package org.gcube.portlets.admin.fhn_manager_portlet.shared.model;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;



public interface Storable{

	
	// GUI LOGIC
	
	public String getKey();
	
	public Object getObjectField(String fieldName);
	
	public ObjectType getType();
	
	public String getName();
	
}
