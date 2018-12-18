package org.gcube.usecases.ws.thredds.model;

import org.gcube.usecases.ws.thredds.engine.impl.ProcessDescriptor;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SyncFolderDescriptor {

	@NonNull
	private String folderId;
	@NonNull
	private String folderPath;
	@NonNull
	private SynchFolderConfiguration configuration;
	@NonNull
	private boolean isLocked=false;
	
	
	private ProcessDescriptor localProcessDescriptor=null; 
	
	
}
