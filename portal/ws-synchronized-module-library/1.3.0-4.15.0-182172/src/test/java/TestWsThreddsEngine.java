

import org.gcube.usecases.ws.thredds.SyncEngine;
import org.gcube.usecases.ws.thredds.engine.impl.ProcessDescriptor;
import org.gcube.usecases.ws.thredds.engine.impl.ProcessStatus;
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

	public static void main(String[] args) throws Exception{

		// GET ENGINE : SINGLETON INSTANCE
		SyncEngine engine=SyncEngine.get();

		// FOLDER CONFIGURATION BEAN
		//SynchFolderConfiguration config=TestCommons.getSynchConfig();

		String filter = "";
		String remotePersistence  = "";
		String remotePath  = "";
		String targetToken  = "";
		SynchFolderConfiguration config = new SynchFolderConfiguration(remotePath, filter, targetToken, remotePersistence, TEST_FOLDER_ID);

		try {
			//try to clean it up, first..
			System.out.println("Cleaning it up..");
			engine.unsetSynchronizedFolder(TEST_FOLDER_ID, false);
		}catch(WorkspaceNotSynchedException e) {
			// it was already cleared
		}catch(WorkspaceLockedException e) {
			engine.forceUnlock(TEST_FOLDER_ID);
			engine.unsetSynchronizedFolder(TEST_FOLDER_ID, false);
		}


		try {
			// WHEN OPENING A FOLDER, INVOKE CHECK TO UPDATE SYNCH STATUS
			engine.check(TEST_FOLDER_ID, false);
		}catch(WorkspaceNotSynchedException e) {
			System.out.println("Folder not synched, configurin it..");
			engine.setSynchronizedFolder(config, TEST_FOLDER_ID);
		}catch(WorkspaceLockedException e) {
			System.out.println("Workspace locked, going to force unlock.."); // MAINLY FOR TEST PURPOSES, OR WHEN SOMETHIGN GOES WRONG.. USE CAUTIOUSLY
			engine.forceUnlock(TEST_FOLDER_ID);
		}

		// INVOKE SYNCHRONIZATION ON FOLDER
		ProcessDescriptor descriptor=engine.doSync(TEST_FOLDER_ID);

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
		engine.registerCallBack(TEST_FOLDER_ID, syncCall);


		engine.check(TEST_FOLDER_ID, true);


		// INVOKE WHEN PORTAL SHUTS DOWN TO FREE RESOURCES AND STOP SYNC PROCESSES
		engine.shutDown();

	}


}
