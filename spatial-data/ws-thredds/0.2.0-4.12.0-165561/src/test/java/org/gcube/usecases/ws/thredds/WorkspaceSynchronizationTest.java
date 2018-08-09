package org.gcube.usecases.ws.thredds;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.Semaphore;

import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.usecases.ws.thredds.engine.impl.ProcessDescriptor;
import org.gcube.usecases.ws.thredds.engine.impl.ProcessStatus;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.ProcessNotFoundException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceInteractionException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceLockedException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceNotSynchedException;
import org.gcube.usecases.ws.thredds.model.SyncOperationCallBack;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;

public class WorkspaceSynchronizationTest {

	public static void main(String[] args) throws ProcessNotFoundException, InternalErrorException, WorkspaceInteractionException, InternalException, WorkspaceFolderNotFoundException, ItemNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException, HomeNotFoundException, UserNotFoundException, MalformedURLException, IOException {
		
		// GET ENGINE : SINGLETON INSTANCE
		SyncEngine engine=SyncEngine.get();
		engine.setRequestLogger("requests.txt");
		
		//TEST INFO...
		TestCommons.setScope();
		WorkspaceFolder folder=TestCommons.getTestFolder();
		
		// FOLDER CONFIGURATION BEAN
		SynchFolderConfiguration config=TestCommons.getSynchConfig();
		
//		try {
//			//try to clean it up, first..
//			System.out.println("Cleaning it up.."); 
//			engine.unsetSynchronizedFolder(folder.getId(), false);
//		}catch(WorkspaceNotSynchedException e) {
//			// it was already cleared
//		}catch(WorkspaceLockedException e) {
//			engine.forceUnlock(folder.getId());
//			engine.unsetSynchronizedFolder(folder.getId(), false);			
//		}

		
		
		
		try {
			// WHEN OPENING A FOLDER, INVOKE CHECK TO UPDATE SYNCH STATUS 
			engine.check(folder.getId(), false);
		}catch(WorkspaceNotSynchedException e) {
			System.out.println("Folder not synched, configurin it..");
			engine.setSynchronizedFolder(config, folder.getId());
			engine.check(folder.getId(), false);
		}catch(WorkspaceLockedException e) {
			System.out.println("Workspace locked, going to force unlock.."); // MAINLY FOR TEST PURPOSES, OR WHEN SOMETHIGN GOES WRONG.. USE CAUTIOUSLY
			engine.forceUnlock(folder.getId());
			engine.check(folder.getId(), false);
		}
		
		
		
		
		// INVOKE SYNCHRONIZATION ON FOLDER
		ProcessDescriptor descriptor=engine.doSync(folder.getId());
		
		System.out.println("Obtained descriptor : "+descriptor);
		
		Semaphore sem=new Semaphore(0);
		
		// REGISTER CALLBACK TO MONITOR PROGRESS
		engine.registerCallBack(folder.getId(), new SyncOperationCallBack() {
			
			@Override
			public void onStep(ProcessStatus status, ProcessDescriptor descriptor) {
				System.out.println("ON STEP : "+status+" "+descriptor);
				System.out.println("LOG : \n"+ status.getLogBuilder().toString());
				if(status.getStatus().equals(ProcessStatus.Status.COMPLETED)) sem.release();
			}
		});
		
		System.out.println("Waiting for process.. ");
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			
		}
		
		engine.check(folder.getId(), true);
		
		
		// INVOKE WHEN PORTAL SHUTS DOWN TO FREE RESOURCES AND STOP SYNC PROCESSES
		engine.shutDown();
		
	}

}
