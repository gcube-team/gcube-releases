package org.gcube.portlets.widgets.wsthreddssync;

import java.io.IOException;
import java.net.MalformedURLException;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.usecases.ws.thredds.SyncEngine;
import org.gcube.usecases.ws.thredds.engine.impl.ProcessDescriptor;
import org.gcube.usecases.ws.thredds.engine.impl.ProcessStatus;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.ProcessNotFoundException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceInteractionException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceLockedException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceNotSynchedException;
import org.gcube.usecases.ws.thredds.model.SyncOperationCallBack;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;


// TODO: Auto-generated Javadoc
/**
 * The Class TestWsThreddsEngine.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 7, 2018
 */
public class TestWsThreddsEngine {

	public static String DEFAULT_SCOPE = "/gcube/devsec"; //DEV
//	public static String DEFAULT_SCOPE = "/d4science.research-infrastructures.eu/gCubeApps";
	public static String TEST_USER = "francesco.mangiacrapa";

	public static String TEST_FOLDER_ID = "";

	public static void main(String[] args) throws ProcessNotFoundException, InternalErrorException, WorkspaceInteractionException, InternalException, WorkspaceFolderNotFoundException, ItemNotFoundException, InsufficientPrivilegesException, ItemAlreadyExistException, HomeNotFoundException, UserNotFoundException, MalformedURLException, IOException {

		// GET ENGINE : SINGLETON INSTANCE
		SyncEngine engine=SyncEngine.get();



		Workspace ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(TEST_USER)
				.getWorkspace();


		//TEST INFO...
		//TestCommons.setScope();
		ScopeBean scope = new ScopeBean(DEFAULT_SCOPE);
		ScopeProvider.instance.set(scope.toString());
		WorkspaceFolder folder=(WorkspaceFolder) ws.getItem(TEST_FOLDER_ID);

		// FOLDER CONFIGURATION BEAN
		//SynchFolderConfiguration config=TestCommons.getSynchConfig();

		String filter = "";
		String remotePersistence  = "";
		String remotePath  = "";
		String targetToken  = "";
		SynchFolderConfiguration config = new SynchFolderConfiguration(remotePath, filter, targetToken, remotePersistence, folder.getId());

		try {
			//try to clean it up, first..
			System.out.println("Cleaning it up..");
			engine.unsetSynchronizedFolder(folder.getId(), false);
		}catch(WorkspaceNotSynchedException e) {
			// it was already cleared
		}catch(WorkspaceLockedException e) {
			engine.forceUnlock(folder.getId());
			engine.unsetSynchronizedFolder(folder.getId(), false);
		}


		try {
			// WHEN OPENING A FOLDER, INVOKE CHECK TO UPDATE SYNCH STATUS
			engine.check(folder.getId(), false);
		}catch(WorkspaceNotSynchedException e) {
			System.out.println("Folder not synched, configurin it..");
			engine.setSynchronizedFolder(config, folder.getId());
		}catch(WorkspaceLockedException e) {
			System.out.println("Workspace locked, going to force unlock.."); // MAINLY FOR TEST PURPOSES, OR WHEN SOMETHIGN GOES WRONG.. USE CAUTIOUSLY
			engine.forceUnlock(folder.getId());
		}

		// INVOKE SYNCHRONIZATION ON FOLDER
		ProcessDescriptor descriptor=engine.doSync(folder.getId());

		System.out.println("Obtained descriptor : "+descriptor);


		SyncOperationCallBack syncCall = new SyncOperationCallBack() {

			@Override
			public void onStep(ProcessStatus status, ProcessDescriptor descriptor) {
				System.out.println("ON STEP : "+status+" "+descriptor);
				System.out.println("LOG : \n"+ status.getLogBuilder().toString());
				if(status.getStatus().equals(ProcessStatus.Status.COMPLETED)) {

					//COMPLETED FARE REMOVE DALLA MAPPA
				}

			}
		};

		// REGISTER CALLBACK TO MONITOR PROGRESS
		engine.registerCallBack(folder.getId(), syncCall);


		engine.check(folder.getId(), true);


		// INVOKE WHEN PORTAL SHUTS DOWN TO FREE RESOURCES AND STOP SYNC PROCESSES
		engine.shutDown();

	}


}
