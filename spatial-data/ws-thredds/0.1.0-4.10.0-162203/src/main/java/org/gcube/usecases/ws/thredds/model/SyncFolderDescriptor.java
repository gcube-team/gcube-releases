package org.gcube.usecases.ws.thredds.model;

import org.gcube.usecases.ws.thredds.engine.impl.ProcessDescriptor;

public class SyncFolderDescriptor {

	private String folderId;
	private String folderPath;
	private boolean isLocked=false;
	private ProcessDescriptor localProcessDescriptor; 
	
}
