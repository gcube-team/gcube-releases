package org.gcube.usecases.ws.thredds.engine.impl;

import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProcessDescriptor implements Cloneable{

	private String folderId;
	private String folderPath;
	private long launchTime;
	private String processId;
	
	private SynchFolderConfiguration synchConfiguration;
	
	@Override
	protected Object clone() throws CloneNotSupportedException {	
		return super.clone();
	}
}
