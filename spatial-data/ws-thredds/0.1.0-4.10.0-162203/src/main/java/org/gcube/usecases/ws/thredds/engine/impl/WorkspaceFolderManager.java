package org.gcube.usecases.ws.thredds.engine.impl;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Properties;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceException;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsCatalog;
import org.gcube.usecases.ws.thredds.Constants;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.LockNotOwnedException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceInteractionException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceLockedException;
import org.gcube.usecases.ws.thredds.faults.WorkspaceNotSynchedException;
import org.gcube.usecases.ws.thredds.model.SyncFolderDescriptor;
import org.gcube.usecases.ws.thredds.model.SynchFolderConfiguration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkspaceFolderManager {

	private static final Map<String,String> cleanedPropertiesMap=new HashMap<String,String>();

	static {
		cleanedPropertiesMap.put(Constants.WorkspaceProperties.TBS, null);
		cleanedPropertiesMap.put(Constants.WorkspaceProperties.SYNCH_FILTER, null);
		cleanedPropertiesMap.put(Constants.WorkspaceProperties.REMOTE_PATH, null);
		cleanedPropertiesMap.put(Constants.WorkspaceProperties.REMOTE_PERSISTENCE, null);

	}

	private WorkspaceFolder theFolder;


	private String folderId;

	// Cahced objects
	private SynchFolderConfiguration config=null;
	private ThreddsController threddsController=null;

	public WorkspaceFolderManager(String folderId) throws WorkspaceInteractionException {
		try{
			Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
			theFolder=(WorkspaceFolder) ws.getItem(folderId);
			this.folderId=folderId;
		}catch(WorkspaceException | InternalErrorException | HomeNotFoundException | UserNotFoundException e) {
			throw new WorkspaceInteractionException("Unable to access folder id "+folderId,e);
		}
	}


	public ThreddsController getThreddsController() throws WorkspaceNotSynchedException, WorkspaceInteractionException, InternalException {
		if(threddsController==null) {
			SynchFolderConfiguration config=getSynchConfiguration();
			threddsController=new ThreddsController(config.getRemotePath(),config.getTargetToken());
		}
		return threddsController;
	}

	public SynchFolderConfiguration getSynchConfiguration() throws WorkspaceInteractionException, WorkspaceNotSynchedException {
		if(config==null) {
			try {
				if(!isSynched()) throw new WorkspaceNotSynchedException("Folder "+folderId+" is not synched.");
				log.debug("Loading properties for ");
				Properties props=theFolder.getProperties();
				SynchFolderConfiguration config=new SynchFolderConfiguration();
				config.setFilter(props.getPropertyValue(Constants.WorkspaceProperties.SYNCH_FILTER));
				config.setRemotePath(props.getPropertyValue(Constants.WorkspaceProperties.REMOTE_PATH));
				config.setRemotePersistence(props.getPropertyValue(Constants.WorkspaceProperties.REMOTE_PERSISTENCE));
				config.setTargetToken(props.getPropertyValue(Constants.WorkspaceProperties.TARGET_TOKEN));
				this.config=config;
			}catch(InternalErrorException e) {
				throw new WorkspaceInteractionException("Unable to load synch configuration in "+folderId,e);
			}
		}
		return config;
	}	

	public boolean isSynched() throws WorkspaceInteractionException {
		try{			
			Map<String,String> props=theFolder.getProperties().getProperties();
			return props.containsKey(Constants.WorkspaceProperties.TBS)&&(props.get(Constants.WorkspaceProperties.TBS)!=null);
		}catch(InternalErrorException e) {
			throw new WorkspaceInteractionException("Unable to check Synch flag on "+folderId,e);
		}
	}


	public SyncFolderDescriptor check(boolean recursively) throws WorkspaceInteractionException, InternalException {
		if(!isSynched()) throw new WorkspaceNotSynchedException("Folder "+folderId+" is not synched.");
		if(isLocked()) throw new WorkspaceLockedException("Workspace "+folderId+" is locked.");
		
//		for() // elements in filter
			// check element (pass recursive flag)
			
		return null;
		
	}





	public String getLockId() throws WorkspaceNotSynchedException, WorkspaceInteractionException, InternalException {
		return getThreddsController().readThreddsFile(Constants.LOCK_FILE);
	}

	public boolean isLocked() throws WorkspaceNotSynchedException, WorkspaceInteractionException, InternalException {
		return getThreddsController().existsThreddsFile(Constants.LOCK_FILE);
	}

	public void configure(SynchFolderConfiguration toSet) throws WorkspaceInteractionException, InternalException {
		if(isSynched()) throw new WorkspaceInteractionException("Folder "+folderId+" is already configured for synchronization.");
		log.info("Configuring folder {} as {} ",folderId,toSet);


		Map<String,String> toSetValues=new HashMap<String,String>();
		toSetValues.put(Constants.WorkspaceProperties.TBS, "true");
		toSetValues.put(Constants.WorkspaceProperties.SYNCH_FILTER, toSet.getFilter());
		toSetValues.put(Constants.WorkspaceProperties.REMOTE_PATH, toSet.getRemotePath());
		toSetValues.put(Constants.WorkspaceProperties.REMOTE_PERSISTENCE, Constants.THREDDS_PERSISTENCE);
		toSetValues.put(Constants.WorkspaceProperties.TARGET_TOKEN, toSet.getTargetToken());
		try{
			Properties props=theFolder.getProperties();
			props.addProperties(toSetValues);
		}catch(InternalErrorException e) {
			throw new WorkspaceInteractionException("Unable to set Properties to "+folderId,e);
		}

		// Checking AND initializing remote folder
		log.debug("Checking remote folder existence .. ");
		boolean createCatalog=false;
		try {
			ThreddsController controller=getThreddsController();
			if(!controller.existsThreddsFile(null)) {
				log.info("Folder not found, creating it..");
				controller.cleanupFolder(null);
				createCatalog=true;
			}else {
				ThreddsCatalog catalog=controller.getCatalog();
				if (catalog==null) createCatalog=true;
			}

			if(createCatalog) {
				log.info("Creating catalog...");
				controller.getCatalog();
			}
		}catch(InternalException e) {
			throw new InternalException ("Unable to check/initialize remote folder",e);
		}
	}

	public void dismiss(boolean deleteRemote) throws WorkspaceInteractionException, InternalException {
		if(!isSynched()) throw new WorkspaceNotSynchedException("Folder "+folderId+" is not synched.");
		if(isLocked()) throw new WorkspaceLockedException("Unable to dismiss locked folder "+folderId);
		
		try {
			cleanCache();
			cleanItem(theFolder);
			if(deleteRemote) 
				getThreddsController().cleanupFolder(null);
		}catch(InternalErrorException e) {
			throw new WorkspaceInteractionException("Unable to cleanup "+folderId,e);
		}
	}


	public void lock(String processId) throws WorkspaceNotSynchedException, WorkspaceInteractionException, InternalException {		
		getThreddsController().lockFolder(processId);
	}

	public void unlock(String processId) throws WorkspaceNotSynchedException, WorkspaceInteractionException, InternalException {
		String currentLock=getLockId();
		if(processId.equals(currentLock)) getThreddsController().deleteThreddsFile(Constants.LOCK_FILE);
		else throw new LockNotOwnedException("Process "+processId+" can't remove lock owned by "+currentLock);
	}

	//*************************** PRIVATE 

	private void cleanCache() {
		this.config=null;
		this.threddsController=null;
	}


	private static void cleanItem(WorkspaceItem item) throws InternalErrorException {
		Properties props=item.getProperties();
		if(props.hasProperty(Constants.WorkspaceProperties.TBS)) {
			props.addProperties(cleanedPropertiesMap);
			if(item.isFolder()) {
				for(WorkspaceItem child : ((WorkspaceFolder)item).getChildren())
					cleanItem(child);
			}
		}
	}
	
	
	private static void checkItem(WorkspaceItem item,boolean recursive) {
		// get Props
		// if not tbs set tbs init
		// else check remote :
				// if remote older || remote younger || doesn't exists, set status outdated
				// else set up to date
		
	}
}
