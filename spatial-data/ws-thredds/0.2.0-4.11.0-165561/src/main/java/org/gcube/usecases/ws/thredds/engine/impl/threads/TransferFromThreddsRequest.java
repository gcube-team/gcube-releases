package org.gcube.usecases.ws.thredds.engine.impl.threads;

import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.usecases.ws.thredds.engine.impl.Process;

import lombok.Data;

@Data
public class TransferFromThreddsRequest extends SynchronizationRequest {

	private WorkspaceItem targetItem; // can be null
	
	private String remoteFilename;
	
	public TransferFromThreddsRequest(Process process, WorkspaceItem targetItem, WorkspaceFolder containingFolder,
			String remoteFilename) {
		super(process,containingFolder);
		this.targetItem = targetItem;
		this.remoteFilename = remoteFilename;
	}
	
	
	
	
}
