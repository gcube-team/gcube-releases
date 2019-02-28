package org.gcube.usecases.ws.thredds.engine.impl.threads;

import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.usecases.ws.thredds.engine.impl.Process;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

public class TransferToThreddsRequest extends SynchronizationRequest {

	private WorkspaceItem toTransfer;

	public TransferToThreddsRequest(Process process,WorkspaceFolder location, WorkspaceItem toTransfer) {
		super(process,location);
		this.toTransfer = toTransfer;
	}

}
