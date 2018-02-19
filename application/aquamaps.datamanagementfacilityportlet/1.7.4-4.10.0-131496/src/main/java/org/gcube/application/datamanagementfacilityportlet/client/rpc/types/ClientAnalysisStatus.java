package org.gcube.application.datamanagementfacilityportlet.client.rpc.types;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum ClientAnalysisStatus implements IsSerializable{
	Pending,Simulating,Generating,Publishing,Completed,Error
}
