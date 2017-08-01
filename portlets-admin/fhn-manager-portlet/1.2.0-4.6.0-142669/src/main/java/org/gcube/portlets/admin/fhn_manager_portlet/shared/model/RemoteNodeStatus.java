package org.gcube.portlets.admin.fhn_manager_portlet.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum RemoteNodeStatus implements IsSerializable{

	
	active, 	// from start	| can stop, suspend
	inactive, 	// from create , stop | can start
	standby,		// from suspend | can start
	
	waiting,
	suspended
}
