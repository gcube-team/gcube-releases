package org.gcube.usecases.ws.thredds.engine.impl.threads;

import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.usecases.ws.thredds.engine.impl.Process;

import lombok.Data;

@Data
public class DeleteRemoteRequest extends SynchronizationRequest {

	private String toRemoveName;
	
	public DeleteRemoteRequest(Process process,WorkspaceFolder location,String name) {
		super(process,location);
		this.toRemoveName=name;
	}

}
