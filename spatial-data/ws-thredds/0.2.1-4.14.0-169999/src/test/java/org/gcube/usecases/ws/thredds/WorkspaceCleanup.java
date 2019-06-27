package org.gcube.usecases.ws.thredds;

import java.io.IOException;
import java.net.MalformedURLException;

import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceInteractionException;

public class WorkspaceCleanup {

	public static void main(String[] args) throws WorkspaceInteractionException, InternalException, InternalErrorException, WorkspaceFolderNotFoundException, ItemNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException, MalformedURLException, HomeNotFoundException, UserNotFoundException, IOException {
		TestCommons.setScope();
		WorkspaceFolder folder=TestCommons.getTestFolder();
		
		SyncEngine engine=SyncEngine.get();
		engine.forceUnlock(folder.getId());
		engine.unsetSynchronizedFolder(folder.getId(), false);
	}

}
