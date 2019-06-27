package org.gcube.usecases.ws.thredds.engine.impl.threads;

import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.usecases.ws.thredds.engine.impl.Process;

import lombok.Data;
import lombok.NonNull;

@Data
public abstract class SynchronizationRequest {

	@NonNull
	private Process process;
	@NonNull
	private WorkspaceFolder location;
}
