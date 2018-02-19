package org.gcube.usecases.ws.thredds;

import java.util.Collections;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.usecases.ws.thredds.engine.impl.WorkspaceFolderManager;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceInteractionException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceNotSynchedException;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;

public class WorkspaceLock {

	public static void main(String[] args) throws WorkspaceFolderNotFoundException, ItemNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException, InternalErrorException, HomeNotFoundException, UserNotFoundException, WorkspaceInteractionException, InternalException {
		TestCommons.setScope();
		WorkspaceFolder folder=TestCommons.getTestFolder();
		WorkspaceFolderManager manager=new WorkspaceFolderManager(folder.getId());
		
		String processID="mytest";
		try {
			System.out.println("Trying to cleanup, first.. ");
			folder.getProperties().addProperties(Collections.singletonMap(org.gcube.usecases.ws.thredds.Constants.WorkspaceProperties.TBS, null));
			System.out.println("FOLDER PROPERTIES : "+folder.getProperties().getProperties());
		}catch(Throwable t) {
			System.err.println("Dismiss error : ");
			t.printStackTrace();
		}
		
		
		
		manager.configure(new SynchFolderConfiguration("mySynchedCatalog","*.nc,*.ncml,*.asc",SecurityTokenProvider.instance.get()));
		
		System.out.println("Is locked : "+manager.isLocked());
		System.out.println("locking ... ");
		manager.lock(processID);
		
		try {
			manager.dismiss(false);
			System.err.println("It should have raised locked exception");
		}catch(Exception e){
			System.out.println("Ok check lock on dismiss ");
		}
			
		manager.unlock(processID);
		
		manager.dismiss(false);

		
		System.out.println("This should be false : "+manager.isSynched());
	}

}
