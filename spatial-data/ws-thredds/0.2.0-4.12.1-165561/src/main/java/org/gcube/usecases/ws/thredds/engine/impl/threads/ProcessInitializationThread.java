package org.gcube.usecases.ws.thredds.engine.impl.threads;

import java.util.concurrent.ExecutorService;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.usecases.ws.thredds.engine.impl.Process;
import org.gcube.usecases.ws.thredds.engine.impl.ProcessDescriptor;
import org.gcube.usecases.ws.thredds.engine.impl.WorkspaceFolderManager;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceInteractionException;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@RequiredArgsConstructor
@Slf4j
public class ProcessInitializationThread implements Runnable {

	@NonNull
	private Process theProcess;
	@NonNull
	private ExecutorService service;
	
	@Override
	public void run() {
		ProcessDescriptor descriptor=theProcess.getDescriptor();
		ProcessIdProvider.instance.set(descriptor.getProcessId());
		log.info("Initialization of process {} ",descriptor);
		theProcess.getStatus().setCurrentMessage("Gathering synchronization information...");
		try {

			WorkspaceFolderManager manager=new WorkspaceFolderManager(descriptor.getFolderId());
			
			log.debug("Updateing synchronization status..");
			manager.check(true);
			log.debug("Launching requests...");
			theProcess.launch(service);
			
		}catch(WorkspaceInteractionException e) {
			log.error("Unable to proceed..",e);
			theProcess.cancel();
		} catch (InternalException e) {
			log.error("Unable to proceed..",e);
			theProcess.cancel();
		} catch (InternalErrorException e) {
			log.error("Unable to proceed..",e);
			theProcess.cancel();
		}catch(Throwable t) {
			log.error("Unexpected Error : ",t);
			theProcess.cancel();
		}finally {
			ProcessIdProvider.instance.reset();
		}
		
	}
}
