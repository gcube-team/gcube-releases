package org.gcube.usecases.ws.thredds;

import java.util.concurrent.Semaphore;

import org.gcube.usecases.ws.thredds.engine.impl.ProcessDescriptor;
import org.gcube.usecases.ws.thredds.engine.impl.ProcessStatus;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.ProcessNotFoundException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceInteractionException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceNotSynchedException;
import org.gcube.usecases.ws.thredds.model.SyncOperationCallBack;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;

public class DTSynchUseCase {

	public static void main(String[] args) throws WorkspaceInteractionException, InternalException, ProcessNotFoundException {
		TokenSetter.set("/d4science.research-infrastructures.eu");
		SyncEngine engine=SyncEngine.get();

		String folderId="a8cd78d3-69e8-4d02-ac90-681b2d16d84d";
		
		
//		String folderId="8a6f9749-68d7-4a9a-a475-bd645050c3fd"; // sub folder for faster tests 
		System.out.println("Clearing configuration.. ");
		
		try {
			engine.unsetSynchronizedFolder(folderId, false);
		}catch(WorkspaceNotSynchedException e) {

		}
		System.out.println("Setting configuration");


		SynchFolderConfiguration config=new SynchFolderConfiguration("public/netcdf/GPTest", "", 
				"f851ba11-bd3e-417a-b2c2-753b02bac506-98187548", // devVRE
				"Agro",folderId);

		engine.setSynchronizedFolder(config, folderId);
		
		System.out.println("Invoke check... ");
		engine.check(folderId, false);

		
		
		System.out.println("Invoke synch");

		engine.doSync(folderId);

		Semaphore sem=new Semaphore(0);

		engine.registerCallBack(folderId, new SyncOperationCallBack() {

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

		System.out.println("Done");
		
		engine.check(folderId, false);


		// INVOKE WHEN PORTAL SHUTS DOWN TO FREE RESOURCES AND STOP SYNC PROCESSES
		engine.shutDown();

	}

}
